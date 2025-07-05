package com.voteplanningpoker.controller;

import com.voteplanningpoker.dto.*;
import com.voteplanningpoker.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;
import java.util.Objects;

@Slf4j
@Controller
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;
    private final SimpMessagingTemplate messaging;
    private static final String ROOM_TOPIC_PREFIX = "/topic/room/";


    @MessageMapping("/create")
    public void createRoom(CreateRoomRequest request, SimpMessageHeaderAccessor headerAccessor) {
        log.info("CreateRoomRequest {}", request);
        var room = roomService.createRoom(request);
        log.info("Room created: {}", room);

        messaging.convertAndSendToUser(
                Objects.requireNonNull(headerAccessor.getSessionId()),
                "/queue/room-created",
                new RoomCreatedResponse(room.id(), room),
                headerAccessor.getMessageHeaders()
        );
    }

    @MessageMapping("/create-topic")
    public void createTopic(CreateTopicRequest request) {
        log.info("CreateTopicRequest {}", request);
        var room = roomService.createTopic(request);
        messaging.convertAndSend(ROOM_TOPIC_PREFIX + request.roomId(), room);
    }

    @MessageMapping("/update-topic")
    public void updateTopic(UpdateTopicRequest request) {
        log.info("UpdateTopicRequest {}", request);
        var room = roomService.updateTopic(request);
        messaging.convertAndSend(ROOM_TOPIC_PREFIX + request.roomId(), room);
    }

    @MessageMapping("/join")
    public void joinToRoom(JoinRequest request, SimpMessageHeaderAccessor headerAccessor) {
        log.info("JoinRequestRoom {}", request);
        var room = roomService.joinRoom(request);
        messaging.convertAndSendToUser(
                Objects.requireNonNull(headerAccessor.getSessionId()),
                "/queue/room-config",
                new RoomConfigResponse(room.roomName(), room.creator().getName(), room.allowedVotes()),
                headerAccessor.getMessageHeaders()
        );
        messaging.convertAndSend(
                ROOM_TOPIC_PREFIX + request.roomId() + "/participants",
                Map.of(
                        "type", "USER_LIST_UPDATED",
                        "participants", room.participants()
                )
        );

        messaging.convertAndSend(ROOM_TOPIC_PREFIX + request.roomId(), room);
    }

    @MessageMapping("/vote")
    public void vote(VoteRequest request) {
        log.info("VoteRequest {}", request);
        var room = roomService.vote(request);
        messaging.convertAndSend(ROOM_TOPIC_PREFIX + request.roomId() + "/votes", room);
    }

    @MessageMapping("/reveal")
    public void revealVotes(RevealVotesRequest request) {
        log.info("RevealVotesRequest {}", request);
        var room = roomService.revealVotes(request);
        messaging.convertAndSend(ROOM_TOPIC_PREFIX + request.roomId() + "/reveal", room);
    }

    @MessageMapping("/reset")
    public void resetVotes(ResetVotesRequest request) {
        log.info("ResetVotesRequest {}", request);
        var room = roomService.resetVotes(request);
        messaging.convertAndSend(ROOM_TOPIC_PREFIX + request.roomId() + "/reset", room);
    }

    @MessageMapping("/room/remove-user")
    public void removeUserFromRoom(RemoveUserRoomRequest request) {
        log.info("RemoveUserRoomRequest {}", request);
        var room = roomService.removeUser(request);
        messaging.convertAndSend(
                ROOM_TOPIC_PREFIX + request.roomId() + "/participants",
                Map.of(
                        "type", "USER_LIST_UPDATED",
                        "participants", room.participants()
                )
        );
    }

}
