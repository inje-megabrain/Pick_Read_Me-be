package com.example.Pick_Read_Me.Service;

import com.example.Pick_Read_Me.BasicResponse;
import com.example.Pick_Read_Me.Domain.Dto.PostDto.GetPostDto;
import com.example.Pick_Read_Me.Domain.Dto.PostDto.PostsDTO;
import com.example.Pick_Read_Me.Domain.Dto.PostDto.SelectAllPost;
import com.example.Pick_Read_Me.Domain.Entity.Member;
import com.example.Pick_Read_Me.Domain.Entity.Post;
import com.example.Pick_Read_Me.Domain.Entity.QPost;
import com.example.Pick_Read_Me.Exception.MemberNotFoundException;
import com.example.Pick_Read_Me.Jwt.JwtProvider;
import com.example.Pick_Read_Me.Repository.MemberRepository;
import com.example.Pick_Read_Me.Repository.PostRepository;
import com.example.Pick_Read_Me.Util.CommonUtil;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.Pick_Read_Me.Domain.Entity.QPost.post;

@Service
@Slf4j
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JwtProvider jwtProvider;

    private final EntityManager em;
    private final JPAQueryFactory query;

    public PostService(EntityManager em, JPAQueryFactory query) {
        this.em = em;
        this.query = new JPAQueryFactory(em);
    }

    public String getReadMe(Authentication authentication, String repo_name) {
        Long github_id = Long.valueOf(authentication.getName());
        Member member = memberRepository.findById(Long.valueOf(github_id))
                .orElseThrow(() -> new MemberNotFoundException("Member not found with id: " + github_id));
        RestTemplate restTemplate = new RestTemplate();

        String apiUrl = String.format("https://api.github.com/repos/"+member.getName()+"/"+
                repo_name+"/readme");
        log.info(apiUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "Your-User-Agent"); // GitHub API 요청 시 User-Agent 헤더 필요

        HttpEntity<String> entity = new HttpEntity<>(headers);

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

    public Post createPost(Authentication authentication, PostsDTO postsDTO) {
        Long github_id = Long.valueOf(authentication.getName());
        Member member = memberRepository.findById(Long.valueOf(github_id))
                .orElseThrow(() -> new MemberNotFoundException("Member not found with id: " + github_id));



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

        return post;
    }

    public List<Post> selectAllPost(Authentication authentication) {
        Long github_id = Long.valueOf(authentication.getName());
        Member member = memberRepository.findById(Long.valueOf(github_id))
                .orElseThrow(() -> new MemberNotFoundException("Member not found with id: " + github_id));
        return member.getPosts();
    }

    public ResponseEntity<GetPostDto> selectPost(Long post_id) {

        Post post = postRepository.findById(post_id)
                .orElseThrow(() -> new MemberNotFoundException("post not found with id: " + post_id));
        GetPostDto getPostDto = new GetPostDto(
          post.getId(), post.getTitle(), post.getContent(),post.getPostCreatedAt(), post.getPostUpdatedAt(),
          post.getRepo(), post.getPost_like(), post.getLikedMembers(), post.getMember()
        );
        return new ResponseEntity<GetPostDto>(getPostDto, HttpStatus.valueOf(200));
    }

    public boolean deletePost(Authentication authentication, Long post_id) {
        Long github_id = Long.valueOf(authentication.getName());
        Member member = memberRepository.findById(Long.valueOf(github_id))
                .orElseThrow(() -> new MemberNotFoundException("Member not found with id: " + github_id));

        Post post = postRepository.findById(post_id)
                .orElseThrow(() -> new MemberNotFoundException("Post not found with id: " + post_id));
        try{
            postRepository.deleteById(post_id);
            member.getPosts().remove(post);

            memberRepository.save(member);
            return true;
        }catch(Exception e) {
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
            postsDTO.getTitle(),postsDTO.getContent(), postsDTO.getRepo()
        );
        postRepository.save(post);

        return new ResponseEntity<PostsDTO>(postsDTO1, HttpStatus.valueOf(200));
    }



    public Slice<Post> searchByPost(Authentication authentication,  Pageable pageable) {

        Long github_id = Long.valueOf(authentication.getName());
        Member member = memberRepository.findById(Long.valueOf(github_id))
                .orElseThrow(() -> new MemberNotFoundException("Member not found with id: " + github_id));



        List<Post> results = query.selectFrom(post)
                .where(
                        post.member.notIn(member)
                )
                .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
                .limit(pageable.getPageSize()+1)
                .fetch();

        return checkLastPage(pageable, results);
    }

    /*

     */
    private Slice<Post> checkLastPage(Pageable pageable, List<Post> results) {

        boolean hasNext = false;

        // 조회한 결과 개수가 요청한 페이지 사이즈보다 크면 뒤에 더 있음, next = true
        if (results.size() > pageable.getPageSize()) {
            hasNext = true;
            results.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(results, pageable, hasNext);
    }
}
