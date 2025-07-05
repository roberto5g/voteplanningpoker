package com.voteplanningpoker.converters;

import com.voteplanningpoker.domain.Vote;
import com.voteplanningpoker.infra.entities.VoteEntity;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VoteConverterTest {
    @Test
    void convertToDomain_shouldReturnNull_whenVoteEntityIsNull() {
        assertNull(VoteConverter.convertToDomain(null));
    }

    @Test
    void convertToDomain_shouldMapFieldsCorrectly() {
        VoteEntity entity = new VoteEntity();
        entity.setVote(5);
        entity.setUserName("bob");
        Vote vote = VoteConverter.convertToDomain(entity);
        assertNotNull(vote);
        assertEquals(5, vote.getVote());
        assertEquals("bob", vote.getUserName());
    }

    @Test
    void toDomainList_shouldReturnNull_whenInputIsNull() {
        assertNull(VoteConverter.toDomainList(null));
    }

    @Test
    void toDomainList_shouldReturnEmptyList_whenInputIsEmpty() {
        List<Vote> votes = VoteConverter.toDomainList(Collections.emptyList());
        assertNotNull(votes);
        assertTrue(votes.isEmpty());
    }

    @Test
    void toDomainList_shouldMapListCorrectly() {
        VoteEntity entity1 = new VoteEntity();
        entity1.setVote(3);
        entity1.setUserName("alice");
        VoteEntity entity2 = new VoteEntity();
        entity2.setVote(8);
        entity2.setUserName("bob");
        List<Vote> votes = VoteConverter.toDomainList(Arrays.asList(entity1, entity2));
        assertNotNull(votes);
        assertEquals(2, votes.size());
        assertEquals("alice", votes.get(0).getUserName());
        assertEquals(3, votes.get(0).getVote());
        assertEquals("bob", votes.get(1).getUserName());
        assertEquals(8, votes.get(1).getVote());
    }
}

