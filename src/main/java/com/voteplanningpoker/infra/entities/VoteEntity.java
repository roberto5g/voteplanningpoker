package com.voteplanningpoker.infra.entities;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "votes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "topic_id")
    private TopicEntity topic;

    @Column(nullable = false)
    private String userName;

    @Column(name = "vote_value", nullable = false)
    private int vote;

}

