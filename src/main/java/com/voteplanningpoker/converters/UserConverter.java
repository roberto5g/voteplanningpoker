package com.voteplanningpoker.converters;

import com.voteplanningpoker.domain.User;
import com.voteplanningpoker.infra.entities.UserEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
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
            return List.of();
        }
        return userEntities.stream()
                .map(UserConverter::toDomain)
                .toList();
    }
}
