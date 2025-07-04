package com.voteplanningpoker.service;

import com.voteplanningpoker.dto.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock
    RoomFactory roomFactory;
    @Mock
    RoomParticipantService roomParticipantService;
    @Mock
    RoomTopicService roomTopicService;
    @Mock
    RoomVoteService roomVoteService;

    @InjectMocks
    RoomService roomService;

    @Test
    void createRoomReturnsRoomDtoFromFactory() {
        CreateRoomRequest request = mock(CreateRoomRequest.class);
        RoomDto expected = mock(RoomDto.class);
        when(roomFactory.createRoom(request)).thenReturn(expected);

        RoomDto result = roomService.createRoom(request);

        assertSame(expected, result);
        verify(roomFactory).createRoom(request);
    }

    @Test
    void joinRoomReturnsRoomDtoFromParticipantService() {
        JoinRequest request = mock(JoinRequest.class);
        RoomDto expected = mock(RoomDto.class);
        when(roomParticipantService.joinRoom(request)).thenReturn(expected);

        RoomDto result = roomService.joinRoom(request);

        assertSame(expected, result);
        verify(roomParticipantService).joinRoom(request);
    }

    @Test
    void createTopicReturnsRoomDtoFromTopicService() {
        CreateTopicRequest request = mock(CreateTopicRequest.class);
        RoomDto expected = mock(RoomDto.class);
        when(roomTopicService.createTopic(request)).thenReturn(expected);

        RoomDto result = roomService.createTopic(request);

        assertSame(expected, result);
        verify(roomTopicService).createTopic(request);
    }

    @Test
    void updateTopicReturnsRoomDtoFromTopicService() {
        UpdateTopicRequest request = mock(UpdateTopicRequest.class);
        RoomDto expected = mock(RoomDto.class);
        when(roomTopicService.updateTopic(request)).thenReturn(expected);

        RoomDto result = roomService.updateTopic(request);

        assertSame(expected, result);
        verify(roomTopicService).updateTopic(request);
    }

    @Test
    void voteReturnsVoteRegisteredResponseFromVoteService() {
        VoteRequest request = mock(VoteRequest.class);
        VoteRegisteredResponse expected = mock(VoteRegisteredResponse.class);
        when(roomVoteService.vote(request)).thenReturn(expected);

        VoteRegisteredResponse result = roomService.vote(request);

        assertSame(expected, result);
        verify(roomVoteService).vote(request);
    }

    @Test
    void revealVotesReturnsRevealVotesResponseFromVoteService() {
        RevealVotesRequest request = mock(RevealVotesRequest.class);
        RevealVotesResponse expected = mock(RevealVotesResponse.class);
        when(roomVoteService.revealVotes(request)).thenReturn(expected);

        RevealVotesResponse result = roomService.revealVotes(request);

        assertSame(expected, result);
        verify(roomVoteService).revealVotes(request);
    }

    @Test
    void resetVotesReturnsRoomDtoFromVoteService() {
        ResetVotesRequest request = mock(ResetVotesRequest.class);
        RoomDto expected = mock(RoomDto.class);
        when(roomVoteService.resetVotes(request)).thenReturn(expected);

        RoomDto result = roomService.resetVotes(request);

        assertSame(expected, result);
        verify(roomVoteService).resetVotes(request);
    }

    @Test
    void removeUserReturnsRoomDtoFromParticipantService() {
        RemoveUserRoomRequest request = mock(RemoveUserRoomRequest.class);
        RoomDto expected = mock(RoomDto.class);
        when(roomParticipantService.removeUser(request)).thenReturn(expected);

        RoomDto result = roomService.removeUser(request);

        assertSame(expected, result);
        verify(roomParticipantService).removeUser(request);
    }
}