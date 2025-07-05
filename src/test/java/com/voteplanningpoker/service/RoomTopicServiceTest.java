package com.voteplanningpoker.service;

import com.voteplanningpoker.domain.TopicStatus;
import com.voteplanningpoker.dto.CreateTopicRequest;
import com.voteplanningpoker.dto.RoomDto;
import com.voteplanningpoker.dto.UpdateTopicRequest;
import com.voteplanningpoker.infra.entities.RoomEntity;
import com.voteplanningpoker.infra.entities.TopicEntity;
import com.voteplanningpoker.infra.entities.UserEntity;
import com.voteplanningpoker.infra.repositories.TopicRepository;
import com.voteplanningpoker.service.helper.RoomHelperService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomTopicServiceTest {

    @Mock
    TopicRepository topicRepository;
    @Mock
    RoomHelperService roomHelperService;

    @InjectMocks
    RoomTopicService roomTopicService;

    RoomEntity room;
    UserEntity authorizedUser;
    UserEntity adminUser;

    @BeforeEach
    void setup() {
        authorizedUser = UserEntity.builder()
                .id(UUID.randomUUID())
                .name("authorizedUser")
                .isAdmin(false)
                .build();
        adminUser = UserEntity.builder()
                .id(UUID.randomUUID())
                .name("adminUser")
                .isAdmin(true)
                .build();
        room = RoomEntity.builder()
                .id(UUID.randomUUID())
                .roomName("room1")
                .allowedVotes(Set.of(1, 2, 3))
                .participants(new HashSet<>(Set.of(authorizedUser, adminUser)))
                .revealAuthorizedUsers(new HashSet<>(Set.of(authorizedUser, adminUser)))
                .creator(adminUser)
                .build();
    }

    @Test
    void createTopicSucceedsForAuthorizedUser() {
        CreateTopicRequest request = mock(CreateTopicRequest.class);
        when(request.roomId()).thenReturn(room.getId().toString());
        when(request.createdBy()).thenReturn("authorizedUser");
        when(request.topicTitle()).thenReturn("topic1");

        TopicEntity topicEntity = TopicEntity.builder()
                .id(UUID.randomUUID())
                .room(room)
                .title("topic1")
                .votesRevealed(false)
                .status(TopicStatus.OPEN)
                .build();

        when(roomHelperService.getRoomOrThrow(room.getId().toString())).thenReturn(room);
        when(topicRepository.save(any(TopicEntity.class))).thenReturn(topicEntity);

        RoomDto dto = roomTopicService.createTopic(request);

        assertNotNull(dto);
        assertEquals("room1", dto.roomName());
        assertEquals("topic1", dto.topic().getTitle());
        verify(topicRepository).save(any(TopicEntity.class));
    }

    @Test
    void createTopicSucceedsForAdminUser() {
        CreateTopicRequest request = mock(CreateTopicRequest.class);
        when(request.roomId()).thenReturn(room.getId().toString());
        when(request.createdBy()).thenReturn("someoneElse");
        when(request.topicTitle()).thenReturn("topic2");

        TopicEntity topicEntity = TopicEntity.builder()
                .id(UUID.randomUUID())
                .room(room)
                .title("topic2")
                .votesRevealed(false)
                .status(TopicStatus.OPEN)
                .build();

        when(roomHelperService.getRoomOrThrow(room.getId().toString())).thenReturn(room);
        when(topicRepository.save(any(TopicEntity.class))).thenReturn(topicEntity);

        RoomDto dto = roomTopicService.createTopic(request);

        assertNotNull(dto);
        assertEquals("topic2", dto.topic().getTitle());
    }

    @Test
    void createTopicThrowsIfNotAuthorized() {
        CreateTopicRequest request = mock(CreateTopicRequest.class);
        when(request.roomId()).thenReturn(room.getId().toString());
        when(request.createdBy()).thenReturn("unauthorizedUser");
        when(roomHelperService.getRoomOrThrow(room.getId().toString())).thenReturn(room);

        room.setRevealAuthorizedUsers(Set.of(authorizedUser)); // no admin in set

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> roomTopicService.createTopic(request));
        assertEquals("Only the room creator or an admin can create a topic", ex.getMessage());
    }

    @Test
    void updateTopicSucceedsForAuthorizedUser() {
        UpdateTopicRequest request = mock(UpdateTopicRequest.class);
        when(request.roomId()).thenReturn(room.getId().toString());
        when(request.updatedBy()).thenReturn("authorizedUser");
        when(request.topicTitle()).thenReturn("newTitle");

        TopicEntity topic = TopicEntity.builder()
                .id(UUID.randomUUID())
                .room(room)
                .title("oldTitle")
                .votesRevealed(false)
                .status(TopicStatus.OPEN)
                .build();
        room.setTopic(topic);

        when(roomHelperService.getRoomOrThrow(room.getId().toString())).thenReturn(room);
        when(topicRepository.save(any(TopicEntity.class))).thenReturn(topic);

        RoomDto dto = roomTopicService.updateTopic(request);

        assertNotNull(dto);
        assertEquals("newTitle", dto.topic().getTitle());
        verify(topicRepository).save(topic);
    }

    @Test
    void updateTopicThrowsIfNotAuthorized() {
        UpdateTopicRequest request = mock(UpdateTopicRequest.class);
        when(request.roomId()).thenReturn(room.getId().toString());
        when(request.updatedBy()).thenReturn("unauthorizedUser");

        when(roomHelperService.getRoomOrThrow(room.getId().toString())).thenReturn(room);

        room.setRevealAuthorizedUsers(Set.of(authorizedUser));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> roomTopicService.updateTopic(request));
        assertEquals("Only the room creator or an admin can update a topic", ex.getMessage());
    }
}