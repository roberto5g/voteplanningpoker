package com.voteplanningpoker.infra.repositories;


import com.voteplanningpoker.infra.entities.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoomRepository extends JpaRepository<RoomEntity, UUID> {

    @Query("""
                select r from RoomEntity r
                left join fetch r.topic t
                left join fetch t.votes
                left join fetch r.participants
                left join fetch r.revealAuthorizedUsers
                where r.id = :id
            """)
    Optional<RoomEntity> findWithAllRelationsById(UUID id);


    boolean existsByCreatorId(UUID userId);
}

