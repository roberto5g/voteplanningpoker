package com.voteplanningpoker.converters;

import com.voteplanningpoker.domain.Vote;
import com.voteplanningpoker.infra.entities.VoteEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VoteConverter {
    public static Vote convertToDomain(VoteEntity voteEntity) {
        if (voteEntity == null) {
            return null;
        }
        return Vote.builder()
                .value(voteEntity.getVote())
                .userName(voteEntity.getUserName())
                .build();
    }

    public static List<Vote> toDomainList(List<VoteEntity> voteEntities) {
        if (voteEntities == null) {
            return List.of();
        }
        return voteEntities.stream()
                .map(VoteConverter::convertToDomain)
                .toList();
    }
}
