package com.voteplanningpoker.converters;

import com.voteplanningpoker.domain.Topic;
import com.voteplanningpoker.domain.TopicStatus;
import com.voteplanningpoker.infra.entities.TopicEntity;
import com.voteplanningpoker.infra.entities.VoteEntity;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TopicConverterTest {
    @Test
    void toDomain_shouldReturnNull_whenTopicEntityIsNull() {
        assertNull(TopicConverter.toDomain(null));
    }

    @Test
    void toDomain_shouldMapFieldsCorrectly() {
        TopicEntity entity = new TopicEntity();
        entity.setId(UUID.randomUUID());
        entity.setTitle("Test Topic");
        entity.setStatus(TopicStatus.OPEN);
        entity.setVotesRevealed(true);
        entity.setAverage(3.0);
        entity.setSuggested(3);
        VoteEntity voteEntity = new VoteEntity();
        voteEntity.setVote(3);
        voteEntity.setUserName("alice");
        entity.setVotes(Collections.singletonList(voteEntity));

        Topic topic = TopicConverter.toDomain(entity);
        assertNotNull(topic);
        assertEquals("Test Topic", topic.getTitle());
        assertEquals(TopicStatus.OPEN, topic.getStatus());
        assertTrue(topic.isVotesRevealed());
        assertEquals(3.0, topic.getAverage());
        assertNotNull(topic.getSuggested());
        assertNotNull(topic.getVotes());
        assertEquals(1, topic.getVotes().size());
        assertEquals("alice", topic.getVotes().get(0).getUserName());
        assertEquals(3, topic.getVotes().get(0).getVote());
    }

    @Test
    void toDomain_shouldHandleEmptyVotesList() {
        TopicEntity entity = new TopicEntity();
        entity.setId(UUID.randomUUID());
        entity.setTitle("No Votes");
        entity.setStatus(TopicStatus.CLOSED);
        entity.setVotes(Collections.emptyList());
        entity.setVotesRevealed(false);
        entity.setAverage(null);
        entity.setSuggested(null);

        Topic topic = TopicConverter.toDomain(entity);
        assertNotNull(topic);
        assertEquals(0, topic.getVotes().size());
        assertEquals(TopicStatus.CLOSED, topic.getStatus());
        assertNull(topic.getSuggested());
    }
}

