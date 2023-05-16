package com.example.Pick_Read_Me.Domain;

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
    @GeneratedValue
    @Column(name = "refresh_id")
    private Long refreshId;

    @OneToOne(mappedBy = "refresh")
    private Member member;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name="ip")
    private String ip;
}
