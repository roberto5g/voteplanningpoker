package com.voteplanningpoker.controller;

import com.voteplanningpoker.domain.User;
import com.voteplanningpoker.dto.*;
import com.voteplanningpoker.service.RoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

class RoomControllerTest {
    private RoomService roomService;
    private SimpMessagingTemplate messaging;
    private RoomController controller;

    @BeforeEach
    void setUp() {
        roomService = mock(RoomService.class);
        messaging = mock(SimpMessagingTemplate.class);
        controller = new RoomController(roomService, messaging);
    }

    @Test
    void createRoom_shouldSendRoomCreatedResponse() {
        CreateRoomRequest request = mock(CreateRoomRequest.class);
        SimpMessageHeaderAccessor accessor = mock(SimpMessageHeaderAccessor.class);
        RoomDto room = mock(RoomDto.class);

        when(room.id()).thenReturn("roomId");
        when(roomService.createRoom(request)).thenReturn(room);
        when(accessor.getSessionId()).thenReturn("sessionId");
        when(accessor.getMessageHeaders()).thenReturn(new MessageHeaders(Collections.emptyMap()));

        controller.createRoom(request, accessor);

        verify(roomService).createRoom(request);
        verify(messaging).convertAndSendToUser(
                eq("sessionId"),
                eq("/queue/room-created"),
                any(RoomCreatedResponse.class),
                any(MessageHeaders.class)
        );
    }

    @Test
    void createTopic_shouldSendRoomToTopic() {
        CreateTopicRequest request = mock(CreateTopicRequest.class);
        RoomDto room = mock(RoomDto.class);
        when(request.roomId()).thenReturn("roomId");
        when(roomService.createTopic(request)).thenReturn(room);

        controller.createTopic(request);

        verify(roomService).createTopic(request);
        verify(messaging).convertAndSend(eq("/topic/room/roomId"), eq(room));
    }

    @Test
    void updateTopic_shouldSendRoomToTopic() {
        UpdateTopicRequest request = mock(UpdateTopicRequest.class);
        RoomDto room = mock(RoomDto.class);
        when(request.roomId()).thenReturn("roomId");
        when(roomService.updateTopic(request)).thenReturn(room);

        controller.updateTopic(request);

        verify(roomService).updateTopic(request);
        verify(messaging).convertAndSend(eq("/topic/room/roomId"), eq(room));
    }

    @Test
    void joinToRoom_shouldSendRoomConfigAndParticipants() {
        JoinRequest request = mock(JoinRequest.class);
        SimpMessageHeaderAccessor accessor = mock(SimpMessageHeaderAccessor.class);
        RoomDto room = mock(RoomDto.class);
        when(request.roomId()).thenReturn("roomId");
        when(roomService.joinRoom(request)).thenReturn(room);
        when(room.roomName()).thenReturn("roomName");
        User creator = mock(User.class);
        when(room.creator()).thenReturn(creator);
        when(creator.getName()).thenReturn("creatorName");
        when(room.allowedVotes()).thenReturn(List.of(1, 2));
        when(accessor.getSessionId()).thenReturn("sessionId");
        when(accessor.getMessageHeaders()).thenReturn(new MessageHeaders(Collections.emptyMap()));

        controller.joinToRoom(request, accessor);

        verify(roomService).joinRoom(request);
        verify(messaging).convertAndSendToUser(
                eq("sessionId"),
                eq("/queue/room-config"),
                any(RoomConfigResponse.class),
                any(MessageHeaders.class)
        );
        verify(messaging).convertAndSend(eq("/topic/room/roomId/participants"), any(Map.class));
        verify(messaging).convertAndSend(eq("/topic/room/roomId"), eq(room));
    }

    @Test
    void vote_shouldSendVotes() {
        VoteRequest request = mock(VoteRequest.class);
        VoteRegisteredResponse responseVote = mock(VoteRegisteredResponse.class);
        when(request.roomId()).thenReturn("roomId");
        when(roomService.vote(request)).thenReturn(responseVote);

        controller.vote(request);

        verify(roomService).vote(request);
        verify(messaging).convertAndSend(eq("/topic/room/roomId/votes"), eq(responseVote));
    }

    @Test
    void revealVotes_shouldSendReveal() {
        RevealVotesRequest request = mock(RevealVotesRequest.class);
        RevealVotesResponse votesResponse = mock(RevealVotesResponse.class);
        when(request.roomId()).thenReturn("roomId");
        when(roomService.revealVotes(request)).thenReturn(votesResponse);

        controller.revealVotes(request);

        verify(roomService).revealVotes(request);
        verify(messaging).convertAndSend(eq("/topic/room/roomId/reveal"), eq(votesResponse));
    }

    @Test
    void resetVotes_shouldSendReset() {
        ResetVotesRequest request = mock(ResetVotesRequest.class);
        RoomDto room = mock(RoomDto.class);
        when(request.roomId()).thenReturn("roomId");
        when(roomService.resetVotes(request)).thenReturn(room);

        controller.resetVotes(request);

        verify(roomService).resetVotes(request);
        verify(messaging).convertAndSend(eq("/topic/room/roomId/reset"), eq(room));
    }

    @Test
    void removeUserFromRoom_shouldSendParticipants() {
        RemoveUserRoomRequest request = mock(RemoveUserRoomRequest.class);
        RoomDto room = mock(RoomDto.class);
        when(request.roomId()).thenReturn("roomId");
        when(roomService.removeUser(request)).thenReturn(room);

        controller.removeUserFromRoom(request);

        verify(roomService).removeUser(request);
        verify(messaging).convertAndSend(eq("/topic/room/roomId/participants"), any(Map.class));
    }
}
