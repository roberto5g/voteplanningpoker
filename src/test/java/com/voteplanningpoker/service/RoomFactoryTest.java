package com.voteplanningpoker.service;

import com.voteplanningpoker.dto.CreateRoomRequest;
import com.voteplanningpoker.dto.RoomDto;
import com.voteplanningpoker.infra.entities.RoomEntity;
import com.voteplanningpoker.infra.entities.UserEntity;
import com.voteplanningpoker.infra.repositories.RoomRepository;
import com.voteplanningpoker.infra.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomFactoryTest {

    @Mock
    RoomRepository roomRepository;
    @Mock
    UserRepository userRepository;

    @InjectMocks
    RoomFactory roomFactory;

    @Mock
    CreateRoomRequest request;

    @BeforeEach
    void setup() {
        when(request.userName()).thenReturn("creator");
        when(request.roomName()).thenReturn("room1");
    }

    @Test
    void createRoomPersistsCreatorAndRoomAndReturnsRoomDto() {
        when(request.allowedVotes()).thenReturn(List.of(1, 2, 3));
        UserEntity creator = UserEntity.builder()
                .id(UUID.randomUUID())
                .name("creator")
                .canRevealVote(true)
                .isAdmin(true)
                .build();
        RoomEntity roomEntity = RoomEntity.builder()
                .id(UUID.randomUUID())
                .creator(creator)
                .roomName("room1")
                .allowedVotes(Set.of(1, 2, 3))
                .participants(Set.of(creator))
                .revealAuthorizedUsers(Set.of(creator))
                .build();

        when(userRepository.save(any(UserEntity.class))).thenReturn(creator);
        when(roomRepository.save(any(RoomEntity.class))).thenReturn(roomEntity);

        RoomDto dto = roomFactory.createRoom(request);

        assertNotNull(dto);
        assertEquals("room1", dto.roomName());
        assertEquals("creator", dto.creator().getName());
        assertTrue(dto.allowedVotes().containsAll(List.of(1, 2, 3)));
        verify(userRepository).save(any(UserEntity.class));
        verify(roomRepository).save(any(RoomEntity.class));
    }

    @Test
    void createRoomUsesDefaultAllowedVotesWhenNull() {
        when(request.allowedVotes()).thenReturn(null);
        UserEntity creator = UserEntity.builder()
                .id(UUID.randomUUID())
                .name("creator")
                .canRevealVote(true)
                .isAdmin(true)
                .build();
        RoomEntity roomEntity = RoomEntity.builder()
                .id(UUID.randomUUID())
                .creator(creator)
                .roomName("room1")
                .allowedVotes(Set.of(1, 2, 3, 5, 8, 13))
                .participants(Set.of(creator))
                .revealAuthorizedUsers(Set.of(creator))
                .build();

        when(userRepository.save(any(UserEntity.class))).thenReturn(creator);
        when(roomRepository.save(any(RoomEntity.class))).thenReturn(roomEntity);

        RoomDto dto = roomFactory.createRoom(request);

        assertNotNull(dto);
        assertTrue(dto.allowedVotes().containsAll(List.of(1, 2, 3, 5, 8, 13)));
    }

    @Test
    void createRoomUsesDefaultAllowedVotesWhenEmpty() {
        when(request.allowedVotes()).thenReturn(List.of());
        UserEntity creator = UserEntity.builder()
                .id(UUID.randomUUID())
                .name("creator")
                .canRevealVote(true)
                .isAdmin(true)
                .build();
        RoomEntity roomEntity = RoomEntity.builder()
                .id(UUID.randomUUID())
                .creator(creator)
                .roomName("room1")
                .allowedVotes(Set.of(1, 2, 3, 5, 8, 13))
                .participants(Set.of(creator))
                .revealAuthorizedUsers(Set.of(creator))
                .build();

        when(userRepository.save(any(UserEntity.class))).thenReturn(creator);
        when(roomRepository.save(any(RoomEntity.class))).thenReturn(roomEntity);

        RoomDto dto = roomFactory.createRoom(request);

        assertNotNull(dto);
        assertTrue(dto.allowedVotes().containsAll(List.of(1, 2, 3, 5, 8, 13)));
    }
}