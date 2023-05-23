package com.example.Pick_Read_Me.Domain.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor()
@Entity
public class Member implements UserDetails {

    @Id
    @Column(name = "github_id", nullable = false)
    private Long id;

    @Column(name = "name", length = 200)
    private String name;

    @OneToOne
    @JoinColumn(name = "id")
    @JsonIgnore
    private Refresh refresh;

    @Column(name ="email")
    private String email;

    @Column(name = "password")
    private String password;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created", nullable = false, updatable = false)
    private Date created;

    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> roles = new ArrayList<>();

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated", nullable = false)
    private Date updated;

    @Builder.Default
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "post_like",
            joinColumns = @JoinColumn(name = "github_id"),
            inverseJoinColumns = @JoinColumn(name = "post_id")
    )
    private List<Post> likedPosts = new ArrayList<>();


    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL,  orphanRemoval=true)
    @JsonIgnore
    private List<Post> posts = new ArrayList<>();


    public void UpdatePosts(Post post) {
        this.posts.add(post);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }



}
