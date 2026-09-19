package com.finalproject2.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "Feedbacks")
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @Size(max = 200)
    @Nationalized
    @Column(name = "title", length = 200)
    private String title;

    @Size(max = 1000)
    @NotNull
    @Nationalized
    @Column(name = "content", nullable = false, length = 1000)
    private String content;

    @Size(max = 1000)
    @Nationalized
    @Column(name = "admin_reply", length = 1000)
    private String adminReply;

    @ColumnDefault("getdate()")
    @Column(name = "created_at")
    private Instant createdAt;

}