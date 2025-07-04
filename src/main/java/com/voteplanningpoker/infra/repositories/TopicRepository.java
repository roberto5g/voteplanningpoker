package com.voteplanningpoker.infra.repositories;


import com.voteplanningpoker.infra.entities.TopicEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TopicRepository extends CrudRepository<TopicEntity, UUID> {
}

