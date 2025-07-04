package com.voteplanningpoker.converters;

import com.voteplanningpoker.domain.User;
import com.voteplanningpoker.infra.entities.UserEntity;

import java.util.List;

public class UserConverter {
    public static User toDomain(UserEntity userEntity) {
        if (userEntity == null) {
            return null;
        }
        return User.builder()
                .id(userEntity.getId())
                .name(userEntity.getName())
                .canRevealVote(userEntity.isCanRevealVote())
                .isAdmin(userEntity.isAdmin())
                .build();
    }

    public static List<User> toDomainList(List<UserEntity> userEntities) {
        if (userEntities == null) {
            return null;
        }
        return userEntities.stream()
                .map(UserConverter::toDomain)
                .toList();
    }
}
