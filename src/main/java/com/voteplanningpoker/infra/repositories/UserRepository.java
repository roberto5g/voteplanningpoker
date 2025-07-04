package com.voteplanningpoker.infra.repositories;


import com.voteplanningpoker.infra.entities.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, UUID> {
    Optional<UserEntity> findByNameIgnoreCase(String name);
}

