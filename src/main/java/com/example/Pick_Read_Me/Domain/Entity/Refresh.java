package com.example.Pick_Read_Me.Domain.Entity;

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
    @Column(name = "refresh_id")
    private Long id;

    @OneToOne(mappedBy = "refresh")
    private Member member;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name="ip")
    private String ip;
}
