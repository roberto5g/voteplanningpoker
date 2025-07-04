package com.voteplanningpoker.converters;

import com.voteplanningpoker.domain.Vote;
import com.voteplanningpoker.infra.entities.VoteEntity;

import java.util.List;

public class VoteConverter {
    public static Vote convertToDomain(VoteEntity voteEntity) {
        if (voteEntity == null) {
            return null;
        }
        return Vote.builder()
                .vote(voteEntity.getVote())
                .userName(voteEntity.getUserName())
                .build();
    }

    public static List<Vote> toDomainList(List<VoteEntity> voteEntities) {
        if (voteEntities == null) {
            return null;
        }
        return voteEntities.stream()
                .map(VoteConverter::convertToDomain)
                .toList();
    }
}
