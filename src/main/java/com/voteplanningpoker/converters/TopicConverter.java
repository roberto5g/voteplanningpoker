package com.voteplanningpoker.converters;

import com.voteplanningpoker.domain.Topic;
import com.voteplanningpoker.domain.TopicStatus;
import com.voteplanningpoker.infra.entities.TopicEntity;

public class TopicConverter {
    public static Topic toDomain(TopicEntity topicEntity) {
        if (topicEntity == null) {
            return null;
        }
        return Topic.builder()
                .id(topicEntity.getId())
                .title(topicEntity.getTitle())
                .status(TopicStatus.fromString(topicEntity.getStatus().name()))
                .votes(VoteConverter.toDomainList(topicEntity.getVotes()))
                .votesRevealed(topicEntity.isVotesRevealed())
                .average(topicEntity.getAverage())
                .suggested(topicEntity.getSuggested())
                .build();
    }
}
