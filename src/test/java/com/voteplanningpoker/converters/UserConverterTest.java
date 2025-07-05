package com.voteplanningpoker.converters;

import com.voteplanningpoker.domain.User;
import com.voteplanningpoker.infra.entities.UserEntity;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserConverterTest {
    @Test
    void toDomain_shouldReturnNull_whenUserEntityIsNull() {
        assertNull(UserConverter.toDomain(null));
    }

    @Test
    void toDomain_shouldMapFieldsCorrectly() {
        var userID = UUID.randomUUID();
        UserEntity entity = new UserEntity();
        entity.setId(userID);
        entity.setName("Alice");
        entity.setCanRevealVote(true);
        entity.setAdmin(false);

        User user = UserConverter.toDomain(entity);
        assertNotNull(user);
        assertEquals(userID, user.getId());
        assertEquals("Alice", user.getName());
        assertTrue(user.isCanRevealVote());
        assertFalse(user.isAdmin());
    }

    @Test
    void toDomainList_shouldReturnNull_whenInputIsNull() {
        assertNull(UserConverter.toDomainList(null));
    }

    @Test
    void toDomainList_shouldReturnEmptyList_whenInputIsEmpty() {
        List<User> users = UserConverter.toDomainList(Collections.emptyList());
        assertNotNull(users);
        assertTrue(users.isEmpty());
    }

    @Test
    void toDomainList_shouldMapListCorrectly() {
        UserEntity entity1 = new UserEntity();
        entity1.setId(UUID.randomUUID());
        entity1.setName("Alice");
        entity1.setCanRevealVote(true);
        entity1.setAdmin(false);

        UserEntity entity2 = new UserEntity();
        entity2.setId(UUID.randomUUID());
        entity2.setName("Bob");
        entity2.setCanRevealVote(false);
        entity2.setAdmin(true);

        List<User> users = UserConverter.toDomainList(Arrays.asList(entity1, entity2));
        assertNotNull(users);
        assertEquals(2, users.size());
        assertEquals("Alice", users.get(0).getName());
        assertEquals("Bob", users.get(1).getName());
    }
}

