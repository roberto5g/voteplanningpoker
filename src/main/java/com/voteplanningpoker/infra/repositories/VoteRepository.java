package com.voteplanningpoker.infra.repositories;


import com.voteplanningpoker.infra.entities.VoteEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VoteRepository extends CrudRepository<VoteEntity, Long> {
    List<VoteEntity> findByTopicId(UUID topicId);

}

