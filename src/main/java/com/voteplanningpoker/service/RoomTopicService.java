package com.voteplanningpoker.service;

import com.voteplanningpoker.converters.TopicConverter;
import com.voteplanningpoker.converters.UserConverter;
import com.voteplanningpoker.domain.Room;
import com.voteplanningpoker.domain.TopicStatus;
import com.voteplanningpoker.dto.CreateTopicRequest;
import com.voteplanningpoker.dto.RoomDto;
import com.voteplanningpoker.dto.UpdateTopicRequest;
import com.voteplanningpoker.infra.entities.TopicEntity;
import com.voteplanningpoker.infra.entities.UserEntity;
import com.voteplanningpoker.infra.repositories.TopicRepository;
import com.voteplanningpoker.service.helper.RoomHelperService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoomTopicService {
    private final TopicRepository topicRepository;
    private final RoomHelperService roomHelperService;

    @Transactional
    public RoomDto createTopic(CreateTopicRequest request) {
        var room = roomHelperService.getRoomOrThrow(request.roomId());

        boolean isAuthorized = room.getRevealAuthorizedUsers().stream()
                .anyMatch(user -> user.getName().equals(request.createdBy()))
                || room.getRevealAuthorizedUsers().stream().anyMatch(UserEntity::isAdmin);

        if (!isAuthorized) {
            throw new IllegalArgumentException("Only the room creator or an admin can create a topic");
        }
        var topic = TopicEntity.builder()
                .id(UUID.randomUUID())
                .room(room)
                .title(request.topicTitle())
                .votesRevealed(false)
                .status(TopicStatus.OPEN)
                .build();
        topic = topicRepository.save(topic);

        var roomDomain = Room.builder()
                .id(room.getId().toString())
                .roomName(room.getRoomName())
                .creator(UserConverter.toDomain(room.getCreator()))
                .allowedVotes(List.copyOf(room.getAllowedVotes()))
                .topic(TopicConverter.toDomain(topic))
                .participants(UserConverter.toDomainList(List.copyOf(room.getParticipants())))
                .build();

        return RoomDto.from(roomDomain);
    }

    @Transactional
    public RoomDto updateTopic(UpdateTopicRequest request) {
        var room = roomHelperService.getRoomOrThrow(request.roomId());

        boolean isAuthorized = room.getRevealAuthorizedUsers().stream()
                .anyMatch(user -> user.getName().equals(request.updatedBy()));

        if (!isAuthorized) {
            throw new IllegalArgumentException("Only the room creator or an admin can update a topic");
        }

        var topic = room.getTopic();
        topic.setTitle(request.topicTitle());

        topic = topicRepository.save(topic);

        var roomDomain = Room.builder()
                .id(room.getId().toString())
                .roomName(room.getRoomName())
                .creator(UserConverter.toDomain(room.getCreator()))
                .allowedVotes(List.copyOf(room.getAllowedVotes()))
                .topic(TopicConverter.toDomain(topic))
                .participants(UserConverter.toDomainList(List.copyOf(room.getParticipants())))
                .build();

        return RoomDto.from(roomDomain);
    }
}
