package com.codehows.wqproject.entity;

import com.codehows.wqproject.auditing.BaseEntity;
import com.codehows.wqproject.constant.LikeId;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "likes")
@Getter @Setter @ToString
@NoArgsConstructor
@IdClass(LikeId.class)
public class Likes extends BaseEntity {

    @Id
    @Column(name = "q_code")
    private String qCode;

    @MapsId
    @ManyToOne
    @JoinColumn(name = "q_code")
    private Question question;

    @Id
    @Column(name = "user_id")
    private String userId;

    @Builder
    Likes(String qCode, Question question, String userId) {
        this.qCode = qCode;
        this.question = question;
        this.userId = userId;
    }
}
