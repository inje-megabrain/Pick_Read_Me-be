package com.example.Pick_Read_Me.Service;

import com.example.Pick_Read_Me.Domain.Dto.CustomSlice.CustomSliceResponseDto;
import com.example.Pick_Read_Me.Domain.Dto.PostDto.GetInfinityDto;
import com.example.Pick_Read_Me.Domain.Dto.PostDto.GetPostDto;
import com.example.Pick_Read_Me.Domain.Dto.PostDto.PostsDTO;
import com.example.Pick_Read_Me.Domain.Dto.PostDto.SelectAllPost;

import com.example.Pick_Read_Me.Domain.Entity.Member;
import com.example.Pick_Read_Me.Domain.Entity.Post;
import com.example.Pick_Read_Me.Exception.MemberNotFoundException;
import com.example.Pick_Read_Me.Jwt.JwtProvider;
import com.example.Pick_Read_Me.Repository.MemberRepository;
import com.example.Pick_Read_Me.Repository.PostLikeRepository;
import com.example.Pick_Read_Me.Repository.PostRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.batik.transcoder.TranscoderException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.Pick_Read_Me.Domain.Entity.QPost.post;

@Service
@Slf4j
public class PostService {

    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PostLikeRepository postLikeRepository;



    @Autowired
    private S3Client s3Client;

    @Value("${cloud.aws.credentials.bucket-name}")
    private String bucket;

    @Value("${baseurl}")
    private String baseUrl;

    @Autowired
    private SvgService svgService;
    private final EntityManager em;
    private final JPAQueryFactory query;

    public PostService(EntityManager em, JPAQueryFactory query) {
        this.em = em;
        this.query = new JPAQueryFactory(em);
    }

    //사용자가 입력한 repo_name의 readme파일을 가져와 마크다운으로 반환합니다.
    public String getReadMe(Authentication authentication, String repo_name) {
        Long github_id = Long.valueOf(authentication.getName());
        Member member = memberRepository.findById(Long.valueOf(github_id))
                .orElseThrow(() -> new MemberNotFoundException("Member not found with id: " + github_id));
        RestTemplate restTemplate = new RestTemplate();

        String apiUrl = String.format("https://api.github.com/repos/" + member.getName() + "/" +
                repo_name + "/readme");
        log.info(apiUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "Your-User-Agent"); // GitHub API 요청 시 User-Agent 헤더 필요

        HttpEntity<String> entity = new HttpEntity<>(headers);

        /*

         */
        ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {

            Map<String, Object> responseData = response.getBody();
            String content = (String) responseData.get("content");
            content = content.replace("\n", "");
            // Base64로 인코딩된 내용을 디코딩
            byte[] decodedBytes = Base64.getDecoder().decode(content);
            String decodedContent = new String(decodedBytes, StandardCharsets.UTF_8);
            log.info(decodedContent);

            return decodedContent;
        } else {
            // API 요청이 실패한 경우 에러 처리
            throw new RuntimeException("Failed to fetch README from GitHub API");
        }
    }

    public ResponseEntity<Post> createPost(Authentication authentication, PostsDTO postsDTO, MultipartFile file) throws IOException, TranscoderException {
        log.info(String.valueOf(authentication));
        Long github_id = Long.valueOf(authentication.getName());
        Member member = memberRepository.findById(Long.valueOf(github_id))
                .orElseThrow(() -> new MemberNotFoundException("Member not found with id: " + github_id));


        if (postsDTO.getTitle() == null || postsDTO.getTitle().isEmpty())
            return ResponseEntity.badRequest().body(null);

        Post post = new Post();
        post.setContent(postsDTO.getContent());
        post.setTitle(postsDTO.getTitle());
        post.setPostCreatedAt(new Date());
        post.setPostUpdatedAt(new Date());
        post.setRepo(postsDTO.getRepo());
        post.setPost_like(0L);
        post.setMember(member);

        member.getPosts().add(post);

        postRepository.save(post);
        memberRepository.save(member);
        svgService.convertToSvg(file, postsDTO.getTitle());

        return ResponseEntity.ok().body(post);
    }


    public List<SelectAllPost> selectAllPost() {
        List<Post> posts = postRepository.findAll();
        List<SelectAllPost> selectAllPosts = new ArrayList<>();
        for (int i = 0; i < posts.size(); i++) {
            Post p = posts.get(i);
            SelectAllPost selectAllPost = new SelectAllPost(p.getId(), p.getTitle(), p.getContent(), p.getPostUpdatedAt(), p.getRepo(),
                    p.getPost_like(), p.getMember().getName());
            selectAllPosts.add(selectAllPost);
        }
        return selectAllPosts;
    }


    private Date parseDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResponseEntity<GetPostDto> selectPost(Long post_id) {

        Post post = postRepository.findById(post_id)
                .orElseThrow(() -> new MemberNotFoundException("post not found with id: " + post_id));
        GetPostDto getPostDto = new GetPostDto(post.getId(), post.getTitle(),
                post.getContent(), post.getRepo(), post.getPost_like(), post.getMember().getName(), post.getPostCreatedAt(), post.getPostUpdatedAt());
        return new ResponseEntity<GetPostDto>(getPostDto, HttpStatus.valueOf(200));
    }

    public boolean deletePost(Authentication authentication, Long post_id) {
        Long github_id = Long.valueOf(authentication.getName());
        Member member = memberRepository.findById(Long.valueOf(github_id))
                .orElseThrow(() -> new MemberNotFoundException("Member not found with id: " + github_id));

        Post post = postRepository.findById(post_id)
                .orElseThrow(() -> new MemberNotFoundException("Post not found with id: " + post_id));
        try {
            postRepository.deleteById(post_id);
            member.getPosts().remove(post);

            memberRepository.save(member);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public Post postLikes(Authentication authentication, Long post_id) {    //글 좋아요
        Long github_id = Long.valueOf(authentication.getName());
        Member member = memberRepository.findById(Long.valueOf(github_id))
                .orElseThrow(() -> new MemberNotFoundException("Member not found with id: " + github_id));

        Optional<Post> optionalPost = postRepository.findById(post_id);
        if (!optionalPost.isPresent()) {  //아이디 없을시 예외처리
            throw new NoSuchElementException("post_id의 값이 DB에 존재하지 않습니다:" + post_id);
        }
        Post post = optionalPost.get();

        Optional<Member> optionalMember = memberRepository.findById(github_id);
        if (!optionalMember.isPresent()) {  //아이디 없을시 예외처리
            throw new NoSuchElementException("DB에 존재하지 않는 ID : " + github_id);
        }

        if (post.getLikedMembers().contains(member)) {
            post.removeLike(member);
        } else {
            post.addLike(member);
        }

        Post savePost = postRepository.save(post);

        return savePost;
    }

    @Transactional
    public ResponseEntity<PostsDTO> updatePost(Long post_id, PostsDTO postsDTO) {
        Post post = postRepository.findById(post_id).orElseGet(Post::new);
        post.setPostUpdatedAt(new Date());
        post.setContent(postsDTO.getContent());
        post.setRepo(postsDTO.getRepo());
        post.setTitle(postsDTO.getTitle());

        PostsDTO postsDTO1 = new PostsDTO(
                postsDTO.getTitle(), postsDTO.getContent(), postsDTO.getRepo()
        );
        postRepository.save(post);

        return new ResponseEntity<PostsDTO>(postsDTO1, HttpStatus.valueOf(200));
    }


    public CustomSliceResponseDto searchByPost(Long page_number, Pageable pageable, boolean check, HttpServletRequest request) {
        Long github_id = null;
        boolean doubleCheck = false;

        if (request.getHeader("accessToken")!= null) {
            log.info("널아님");
            github_id = Long.valueOf(jwtProvider.getGithubIdFromToken(request.getHeader("accessToken")));
            log.info("아이디는"+github_id);
            doubleCheck = true;
        }

        Long last_post_id = Long.valueOf(postRepository.findAll().size());
        Long totalPage = last_post_id/10;
        log.info(String.valueOf(last_post_id));
        last_post_id -= page_number*10;
        log.info(String.valueOf(last_post_id));
        List<GetInfinityDto> results = null;




        if (check==false && doubleCheck == true) {
            Member member = memberRepository.findById(Long.valueOf(github_id))
                    .orElseThrow(() -> new MemberNotFoundException("Member not found with id: " ));
            List<Post> k = member.getLikedPosts();

            log.info(String.valueOf(k));
            results = query.selectFrom(post)
                    .where(
                            ltPostId(last_post_id+1)
                    )
                    .orderBy(post.id.desc()) // post_id를 내림차순으로 정렬
                    .limit(pageable.getPageSize() + 1)
                    .fetch()
                    .stream()
                    .map(post -> mapToGetPostDto(post, k))
                    .collect(Collectors.toList());
        } else if (check == true && doubleCheck==true) {
            Member member = memberRepository.findById(Long.valueOf(github_id))
                    .orElseThrow(() -> new MemberNotFoundException("Member not found with id: " ));
            List<Post> k = member.getLikedPosts();
           results = query.selectFrom(post)
                    .where(
                            ltPostId(last_post_id+1)
                    )
                    .orderBy(post.post_like.desc()) // post_id를 내림차순으로 정렬
                    .limit(pageable.getPageSize() + 1)
                    .fetch()
                    .stream()
                   .map(post -> mapToGetPostDto(post, k))
                    .collect(Collectors.toList());
        } else if (check == true && doubleCheck==false) {
            results = query.selectFrom(post)
                    .where(
                            ltPostId(last_post_id+1)
                    )
                    .orderBy(post.post_like.desc()) // post_id를 내림차순으로 정렬
                    .limit(pageable.getPageSize() + 1)
                    .fetch()
                    .stream()
                    .map(post -> mapToGetPostDto(post, null))
                    .collect(Collectors.toList());
        } else if (check == false && doubleCheck==false) {
            results = query.selectFrom(post)
                    .where(
                            ltPostId(last_post_id+1)
                    )
                    .orderBy(post.id.desc()) // post_id를 내림차순으로 정렬
                    .limit(pageable.getPageSize() + 1)
                    .fetch()
                    .stream()
                    .map(post -> mapToGetPostDto(post, null))
                    .collect(Collectors.toList());
        }
      
        List<GetInfinityDto> content = checkLastPage(pageable, results).getContent();
        CustomSliceResponseDto custom = new CustomSliceResponseDto(
                content,
                page_number,
                totalPage,
                content.size()
        );
        return custom;

    }

    // no-offset 방식 처리하는 메서드
    private BooleanExpression ltPostId(Long lastPostId) {
        if (lastPostId == null) {
            return null;
        }

        return post.id.lt(lastPostId);
    }

    // 무한 스크롤 방식 처리하는 메서드
    private Slice<GetInfinityDto> checkLastPage(Pageable pageable, List<GetInfinityDto> results) {

        boolean hasNext = false;

        // 조회한 결과 개수가 요청한 페이지 사이즈보다 크면 뒤에 더 있음, next = true
        if (results.size() > pageable.getPageSize()) {
            hasNext = true;
            results.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(results, pageable, hasNext);
    }

    //본인글 조회
    public List<GetPostDto> searchByMyPost(Authentication authentication) {
        Long github_id = Long.valueOf(authentication.getName());
        Member member = memberRepository.findById(Long.valueOf(github_id))
                .orElseThrow(() -> new MemberNotFoundException("Member not found with id: " + github_id));
        Post post = new Post();
        List<GetPostDto> getPostDtos = new ArrayList<>();
        List<Post> posts = member.getPosts();
        for(int i=0; i<posts.size(); i++) {

            post = posts.get(i);
            GetPostDto getPostDto = new GetPostDto(post.getId(), post.getTitle(), post.getContent(),
                    post.getRepo(), post.getPost_like(), post.getRepo(), post.getPostUpdatedAt(), post.getPostUpdatedAt());
            getPostDtos.add(getPostDto);
        }
        return getPostDtos;

    }
    private GetInfinityDto mapToGetPostDto(Post post ,List<Post> likes) {

        boolean check =false;
        if(likes!=null) {

            for(int i=0; i<likes.size(); i++) {
                if(likes.get(i).getId().equals(post.getId())) {
                    check =true;
                }
            }
        } else {
            check = false;
        }

        GetInfinityDto getPostDto = new GetInfinityDto(post.getId(), post.getTitle(),
                post.getContent(), post.getRepo(), post.getPost_like(), post.getMember().getName(), post.getPostCreatedAt(), post.getPostUpdatedAt(),
                "https://pick-read-me-actions-s3-bucket.s3.ap-northeast-2.amazonaws.com/"+post.getTitle()+".svg", check);
        return getPostDto;
        // 추가적으로 필요한 데이터를 매핑합니다
    }



    public GetInfinityDto getDetailPost(Authentication authentication, Long post_id) {
        Long github_id = Long.valueOf(authentication.getName());
        Member member = memberRepository.findById(Long.valueOf(github_id))
                .orElseThrow(() -> new MemberNotFoundException("Member not found with id: " + github_id));

        List<Post> k = member.getLikedPosts();
        Post post =  postRepository.findById(post_id).orElseGet(Post::new);

        boolean check =false;
        if(k!=null) {

            for(int i=0; i<k.size(); i++) {
                if(k.get(i).getId().equals(post.getId())) {
                    check =true;
                }
            }
        } else {
            check = false;
        }


        GetInfinityDto getPostDto = new GetInfinityDto(post.getId(), post.getTitle(),
                post.getContent(), post.getRepo(), post.getPost_like(), post.getMember().getName(), post.getPostCreatedAt(), post.getPostUpdatedAt(),
                "https://pick-read-me-actions-s3-bucket.s3.ap-northeast-2.amazonaws.com/"+post.getTitle()+".svg", check);
        return getPostDto;
    }


    public String extractImageUrlsFromHtml(String html, String repoName) {
        List<String> imageUrls = new ArrayList<>();
        log.info(html);
        Document document = Jsoup.parse(html);


        int k=0;
        int end=1;
        int check=0;
        int copyLength=html.length();
        String copy = html;

        for(k=0; k<copyLength; k+=end) {

            int cnt = copy.indexOf("<img");
            log.info(String.valueOf(cnt));
            if(cnt!=-1) {
                for(int j=cnt+15; j<copy.length(); j++) {
                    if(copy.charAt(j)=='>') {
                        log.info("!!");
                        end = j;
                        break;
                    }

                }
                String plus = copy.substring(cnt+10, end);
                imageUrls.add(plus);
                copy = copy.substring(end, copy.length());
            }

        }




        Elements imageElements = document.select("img");
        log.info(String.valueOf(imageElements));
        for(int i=0; i<imageElements.size(); i++) {

            Element imageElement = imageElements.get(i);
            String imageUrl = imageElement.attr("src");


            String imagePath = imageUrl;
            String fileName = repoName + i;

            try {
                URL url = new URL(imagePath);
                InputStream inputStream = url.openStream();

                Path tempFilePath = Files.createTempFile("temp", ".svg"); //임시 파일 생성
                Files.copy(inputStream, tempFilePath, StandardCopyOption.REPLACE_EXISTING); //input내용을 tempFilePath에 복사

                PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(fileName)
                        .build();

                String newImageUrl =  baseUrl+fileName+'"';

                html = html.replace(imageUrls.get(i), newImageUrl);

                PutObjectResponse response = s3Client.putObject(putObjectRequest, software.amazon.awssdk.core.sync.RequestBody.fromFile(tempFilePath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return html;
    }


    public ResponseEntity<Post> createPostTest(Authentication authentication, PostsDTO postsDTO) {
        log.info(String.valueOf(authentication));
        Long github_id = Long.valueOf(authentication.getName());
        Member member = memberRepository.findById(Long.valueOf(github_id))
                .orElseThrow(() -> new MemberNotFoundException("Member not found with id: " + github_id));


        if (postsDTO.getTitle() == null || postsDTO.getTitle().isEmpty())
            return ResponseEntity.badRequest().body(null);

        Post post = new Post();
        post.setContent(postsDTO.getContent());
        post.setTitle(postsDTO.getTitle());
        post.setPostCreatedAt(new Date());
        post.setPostUpdatedAt(new Date());
        post.setRepo(postsDTO.getRepo());
        post.setPost_like(0L);
        post.setMember(member);

        member.getPosts().add(post);

        postRepository.save(post);
        memberRepository.save(member);

        return ResponseEntity.ok().body(post);
    }



    /*
    public void makeThumbnail(String name) {
        String k  = svgService.makeThumbnail(name);
    }

     */
}
