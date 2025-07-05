package com.voteplanningpoker.service;

import com.voteplanningpoker.domain.TopicStatus;
import com.voteplanningpoker.dto.*;
import com.voteplanningpoker.infra.entities.RoomEntity;
import com.voteplanningpoker.infra.entities.TopicEntity;
import com.voteplanningpoker.infra.entities.UserEntity;
import com.voteplanningpoker.infra.entities.VoteEntity;
import com.voteplanningpoker.infra.repositories.RoomRepository;
import com.voteplanningpoker.service.helper.RoomHelperService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomVoteServiceTest {

    @Mock
    RoomRepository roomRepository;
    @Mock
    RoomHelperService roomHelperService;

    @InjectMocks
    RoomVoteService roomVoteService;

    RoomEntity room;
    TopicEntity topic;
    UserEntity participant;
    UserEntity authorizedUser;

    @BeforeEach
    void setup() {
        participant = UserEntity.builder().id(UUID.randomUUID()).name("user1").isAdmin(false).build();
        authorizedUser = UserEntity.builder().id(UUID.randomUUID()).name("admin").isAdmin(true).build();
        topic = TopicEntity.builder().id(UUID.randomUUID()).votes(new ArrayList<>()).build();
        room = RoomEntity.builder()
                .id(UUID.randomUUID())
                .roomName("room1")
                .allowedVotes(Set.of(1, 2, 3, 5, 8))
                .participants(new HashSet<>(Set.of(participant, authorizedUser)))
                .revealAuthorizedUsers(new HashSet<>(Set.of(authorizedUser)))
                .topic(topic)
                .build();
        topic.setRoom(room);
    }

    @Test
    void voteRegistersVoteForParticipant() {
        VoteRequest request = mock(VoteRequest.class);
        when(request.roomId()).thenReturn(room.getId().toString());
        when(request.userName()).thenReturn("user1");
        when(request.vote()).thenReturn(3);

        when(roomHelperService.getRoomOrThrow(room.getId().toString())).thenReturn(room);

        VoteRegisteredResponse response = roomVoteService.vote(request);

        assertTrue(response.usersWhoVoted().contains("user1"));
        verify(roomRepository).save(room);
    }

    @Test
    void voteRemovesPreviousVoteOfUser() {
        topic.getVotes().add(VoteEntity.builder().userName("user1").vote(2).topic(topic).build());
        VoteRequest request = mock(VoteRequest.class);
        when(request.roomId()).thenReturn(room.getId().toString());
        when(request.userName()).thenReturn("user1");
        when(request.vote()).thenReturn(5);

        when(roomHelperService.getRoomOrThrow(room.getId().toString())).thenReturn(room);

        roomVoteService.vote(request);

        assertEquals(1, topic.getVotes().size());
        assertEquals(5, topic.getVotes().get(0).getVote());
    }

    @Test
    void voteThrowsIfUserNotParticipant() {
        VoteRequest request = mock(VoteRequest.class);
        when(request.roomId()).thenReturn(room.getId().toString());
        when(request.userName()).thenReturn("notInRoom");

        when(roomHelperService.getRoomOrThrow(room.getId().toString())).thenReturn(room);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> roomVoteService.vote(request));
        assertTrue(ex.getMessage().contains("User not in room"));
    }

    @Test
    void revealVotesReturnsAverageAndSuggested() {
        topic.getVotes().add(VoteEntity.builder().userName("user1").vote(2).topic(topic).build());
        topic.getVotes().add(VoteEntity.builder().userName("admin").vote(3).topic(topic).build());
        RevealVotesRequest request = mock(RevealVotesRequest.class);
        when(request.roomId()).thenReturn(room.getId().toString());
        when(request.userName()).thenReturn("admin");

        when(roomHelperService.getRoomOrThrow(room.getId().toString())).thenReturn(room);

        RevealVotesResponse response = roomVoteService.revealVotes(request);

        assertEquals(2.5, response.average());
        assertEquals(3, response.suggested());
        assertTrue(topic.isVotesRevealed());
        verify(roomRepository).save(room);
    }

    @Test
    void revealVotesThrowsIfNotAuthorized() {
        RevealVotesRequest request = mock(RevealVotesRequest.class);
        when(request.roomId()).thenReturn(room.getId().toString());
        when(request.userName()).thenReturn("user1");

        when(roomHelperService.getRoomOrThrow(room.getId().toString())).thenReturn(room);

        room.setRevealAuthorizedUsers(Set.of()); // no authorized users

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> roomVoteService.revealVotes(request));
        assertTrue(ex.getMessage().contains("User not authorized"));
    }

    @Test
    void revealVotesThrowsIfNoVotes() {
        RevealVotesRequest request = mock(RevealVotesRequest.class);
        when(request.roomId()).thenReturn(room.getId().toString());
        when(request.userName()).thenReturn("admin");

        when(roomHelperService.getRoomOrThrow(room.getId().toString())).thenReturn(room);

        topic.setVotes(new ArrayList<>());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> roomVoteService.revealVotes(request));
        assertEquals("No votes to reveal", ex.getMessage());
    }

    @Test
    void resetVotesClearsVotesAndResetsFields() {
        topic.setVotesRevealed(true);
        topic.setAverage(2.0);
        topic.setSuggested(2);
        topic.getVotes().add(VoteEntity.builder().userName("user1").vote(2).topic(topic).build());
        topic.setStatus(TopicStatus.OPEN);

        ResetVotesRequest request = mock(ResetVotesRequest.class);
        when(request.roomId()).thenReturn(room.getId().toString());
        when(request.userName()).thenReturn("admin");

        when(roomHelperService.getRoomOrThrow(room.getId().toString())).thenReturn(room);

        RoomDto dto = roomVoteService.resetVotes(request);

        assertFalse(topic.isVotesRevealed());
        assertNull(topic.getAverage());
        assertNull(topic.getSuggested());
        assertTrue(topic.getVotes().isEmpty());
        verify(roomRepository).save(room);
        assertNotNull(dto);
    }

    @Test
    void resetVotesThrowsIfNotAuthorized() {
        ResetVotesRequest request = mock(ResetVotesRequest.class);
        when(request.roomId()).thenReturn(room.getId().toString());
        when(request.userName()).thenReturn("user1");

        when(roomHelperService.getRoomOrThrow(room.getId().toString())).thenReturn(room);

        room.setRevealAuthorizedUsers(Set.of());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> roomVoteService.resetVotes(request));
        assertTrue(ex.getMessage().contains("User not authorized"));
    }

    @Test
    void revealVotesThrowsIfVotesIsNull() {
        topic.setVotes(null);
        RevealVotesRequest request = mock(RevealVotesRequest.class);
        when(request.roomId()).thenReturn(room.getId().toString());
        when(request.userName()).thenReturn("admin");
        when(roomHelperService.getRoomOrThrow(room.getId().toString())).thenReturn(room);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> roomVoteService.revealVotes(request));
        assertEquals("No votes to reveal", ex.getMessage());
    }

    @Test
    void revealVotesSuggestsAverageWhenIntegerAndAllowed() {
        // average = 3.0, allowedVotes = [1,2,3,5,8]
        topic.getVotes().add(VoteEntity.builder().userName("user1").vote(3).topic(topic).build());
        topic.getVotes().add(VoteEntity.builder().userName("admin").vote(3).topic(topic).build());
        RevealVotesRequest request = mock(RevealVotesRequest.class);
        when(request.roomId()).thenReturn(room.getId().toString());
        when(request.userName()).thenReturn("admin");
        when(roomHelperService.getRoomOrThrow(room.getId().toString())).thenReturn(room);

        RevealVotesResponse response = roomVoteService.revealVotes(request);

        assertEquals(3.0, response.average());
        assertEquals(3, response.suggested());
    }

    @Test
    void revealVotesSuggestsNextAllowedWhenAverageNotIntegerOrNotAllowed() {
        // average = 4.0, allowedVotes = [1,2,3,5,8] (4 not in allowed, so should suggest 5)
        topic.getVotes().add(VoteEntity.builder().userName("user1").vote(3).topic(topic).build());
        topic.getVotes().add(VoteEntity.builder().userName("admin").vote(5).topic(topic).build());
        RevealVotesRequest request = mock(RevealVotesRequest.class);
        when(request.roomId()).thenReturn(room.getId().toString());
        when(request.userName()).thenReturn("admin");
        when(roomHelperService.getRoomOrThrow(room.getId().toString())).thenReturn(room);

        RevealVotesResponse response = roomVoteService.revealVotes(request);

        assertEquals(4.0, response.average());
        assertEquals(5, response.suggested());
    }

    @Test
    void revealVotesSuggestsLastAllowedWhenAverageAboveAllAllowed() {
        // average = 6.5, allowedVotes = [1,2,3,5,8] (should suggest 8)
        topic.getVotes().add(VoteEntity.builder().userName("user1").vote(8).topic(topic).build());
        topic.getVotes().add(VoteEntity.builder().userName("admin").vote(5).topic(topic).build());
        room.setAllowedVotes(Set.of(1,2,3,5,8));
        RevealVotesRequest request = mock(RevealVotesRequest.class);
        when(request.roomId()).thenReturn(room.getId().toString());
        when(request.userName()).thenReturn("admin");
        when(roomHelperService.getRoomOrThrow(room.getId().toString())).thenReturn(room);

        RevealVotesResponse response = roomVoteService.revealVotes(request);

        assertEquals(6.5, response.average());
        assertEquals(8, response.suggested());
    }

    @Test
    void revealVotesHandlesDuplicateUserNamesByKeepingFirstVote() {
        // Two votes with the same userName, different values
        topic.getVotes().add(VoteEntity.builder().userName("user1").vote(2).topic(topic).build());
        topic.getVotes().add(VoteEntity.builder().userName("user1").vote(5).topic(topic).build());
        topic.getVotes().add(VoteEntity.builder().userName("admin").vote(3).topic(topic).build());
        RevealVotesRequest request = mock(RevealVotesRequest.class);
        when(request.roomId()).thenReturn(room.getId().toString());
        when(request.userName()).thenReturn("admin");
        when(roomHelperService.getRoomOrThrow(room.getId().toString())).thenReturn(room);

        RevealVotesResponse response = roomVoteService.revealVotes(request);

        assertEquals(2, response.votes().get("user1"));
        assertEquals(3, response.votes().get("admin"));
        assertEquals(2, response.votes().size());
    }
}