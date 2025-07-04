package com.voteplanningpoker.service;

import com.voteplanningpoker.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomFactory roomFactory;
    private final RoomParticipantService roomParticipantService;
    private final RoomTopicService roomTopicService;
    private final RoomVoteService roomVoteService;

    public RoomDto createRoom(CreateRoomRequest request) {
        return roomFactory.createRoom(request);
    }

    public RoomDto joinRoom(JoinRequest request) {
        return roomParticipantService.joinRoom(request);
    }

    public RoomDto createTopic(CreateTopicRequest request) {
        return roomTopicService.createTopic(request);
    }

    public RoomDto updateTopic(UpdateTopicRequest request) {
        return roomTopicService.updateTopic(request);
    }

    public VoteRegisteredResponse vote(VoteRequest request) {
        return roomVoteService.vote(request);
    }

    public RevealVotesResponse revealVotes(RevealVotesRequest request) {
        return roomVoteService.revealVotes(request);
    }

    public RoomDto resetVotes(ResetVotesRequest request) {
        return roomVoteService.resetVotes(request);
    }

    public RoomDto removeUser(RemoveUserRoomRequest request) {
        return roomParticipantService.removeUser(request);
    }
}


