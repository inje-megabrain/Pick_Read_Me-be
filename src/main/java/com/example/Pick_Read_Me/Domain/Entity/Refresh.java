package com.example.Pick_Read_Me.Domain.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@Getter
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Refresh {
    @Id
    @Column(name = "refreshId")
    private Long id;

    @OneToOne
    @JoinColumn(name = "githubId") //글을쓴 Member_id
    @JsonIgnore
    private Member member;

    @Column(name = "refreshToken")
    private String refreshToken;

    @Column(name="ip")
    private String ip;
}
