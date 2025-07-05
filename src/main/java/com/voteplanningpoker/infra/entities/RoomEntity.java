package com.voteplanningpoker.infra.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomEntity {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "creator_id")
    private UserEntity creator;

    @Column(nullable = false)
    private String roomName;

    @ManyToMany
    @JoinTable(
            name = "room_participants",
            joinColumns = @JoinColumn(name = "room_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<UserEntity> participants = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "room_reveal_authorized_users",
            joinColumns = @JoinColumn(name = "room_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<UserEntity> revealAuthorizedUsers = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "room_allowed_votes", joinColumns = @JoinColumn(name = "room_id"))
    @Column(name = "vote_value")
    private Set<Integer> allowedVotes = new HashSet<>();

    @OneToOne(mappedBy = "room", cascade = CascadeType.ALL)
    private TopicEntity topic;
}

