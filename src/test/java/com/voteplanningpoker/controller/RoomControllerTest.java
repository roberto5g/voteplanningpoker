package com.voteplanningpoker.controller;

import com.voteplanningpoker.domain.User;
import com.voteplanningpoker.dto.*;
import com.voteplanningpoker.service.RoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.*;

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
        MessageHeaders headers = new MessageHeaders(Collections.emptyMap());
        when(accessor.getMessageHeaders()).thenReturn(headers);

        controller.createRoom(request, accessor);

        verify(roomService).createRoom(request);
        verify(messaging).convertAndSendToUser(
                "sessionId",
                "/queue/room-created",
                new RoomCreatedResponse("roomId", room),
                headers
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
        verify(messaging).convertAndSend("/topic/room/roomId", room);
    }

    @Test
    void updateTopic_shouldSendRoomToTopic() {
        UpdateTopicRequest request = mock(UpdateTopicRequest.class);
        RoomDto room = mock(RoomDto.class);
        when(request.roomId()).thenReturn("roomId");
        when(roomService.updateTopic(request)).thenReturn(room);

        controller.updateTopic(request);

        verify(roomService).updateTopic(request);
        verify(messaging).convertAndSend("/topic/room/roomId", room);
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
        MessageHeaders headers = new MessageHeaders(Collections.emptyMap());
        when(accessor.getMessageHeaders()).thenReturn(headers);

        Set<User> participants = Set.of();
        when(room.participants()).thenReturn(participants);

        controller.joinToRoom(request, accessor);

        verify(roomService).joinRoom(request);

        RoomConfigResponse expectedConfig = new RoomConfigResponse(
                "roomName", "creatorName", List.of(1, 2)
        );

        Map<String, Object> expectedParticipants = Map.of(
                "type", "USER_LIST_UPDATED",
                "participants", participants
        );

        verify(messaging).convertAndSendToUser(
                "sessionId",
                "/queue/room-config",
                expectedConfig,
                headers
        );
        verify(messaging).convertAndSend("/topic/room/roomId/participants", expectedParticipants);
        verify(messaging).convertAndSend("/topic/room/roomId", room);
    }

    @Test
    void vote_shouldSendVotes() {
        VoteRequest request = mock(VoteRequest.class);
        VoteRegisteredResponse responseVote = mock(VoteRegisteredResponse.class);
        when(request.roomId()).thenReturn("roomId");
        when(roomService.vote(request)).thenReturn(responseVote);

        controller.vote(request);

        verify(roomService).vote(request);
        verify(messaging).convertAndSend("/topic/room/roomId/votes", responseVote);
    }

    @Test
    void revealVotes_shouldSendReveal() {
        RevealVotesRequest request = mock(RevealVotesRequest.class);
        RevealVotesResponse votesResponse = mock(RevealVotesResponse.class);
        when(request.roomId()).thenReturn("roomId");
        when(roomService.revealVotes(request)).thenReturn(votesResponse);

        controller.revealVotes(request);

        verify(roomService).revealVotes(request);
        verify(messaging).convertAndSend("/topic/room/roomId/reveal", votesResponse);
    }

    @Test
    void resetVotes_shouldSendReset() {
        ResetVotesRequest request = mock(ResetVotesRequest.class);
        RoomDto room = mock(RoomDto.class);
        when(request.roomId()).thenReturn("roomId");
        when(roomService.resetVotes(request)).thenReturn(room);

        controller.resetVotes(request);

        verify(roomService).resetVotes(request);
        verify(messaging).convertAndSend("/topic/room/roomId/reset", room);
    }

    @Test
    void removeUserFromRoom_shouldSendParticipants() {
        RemoveUserRoomRequest request = mock(RemoveUserRoomRequest.class);
        RoomDto room = mock(RoomDto.class);
        when(request.roomId()).thenReturn("roomId");
        when(roomService.removeUser(request)).thenReturn(room);
        User user = mock(User.class);
        Set<User> participants = Set.of(user);
        when(room.participants()).thenReturn(participants);

        controller.removeUserFromRoom(request);

        verify(roomService).removeUser(request);

        Map<String, Object> expectedPayload = Map.of(
                "type", "USER_LIST_UPDATED",
                "participants", participants
        );
        verify(messaging).convertAndSend("/topic/room/roomId/participants", expectedPayload);
    }
}
