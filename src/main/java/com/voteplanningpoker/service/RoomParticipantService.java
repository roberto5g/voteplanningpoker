package com.voteplanningpoker.service;

import com.voteplanningpoker.converters.TopicConverter;
import com.voteplanningpoker.converters.UserConverter;
import com.voteplanningpoker.domain.Room;
import com.voteplanningpoker.dto.JoinRequest;
import com.voteplanningpoker.dto.RemoveUserRoomRequest;
import com.voteplanningpoker.dto.RoomDto;
import com.voteplanningpoker.infra.entities.RoomEntity;
import com.voteplanningpoker.infra.entities.UserEntity;
import com.voteplanningpoker.infra.repositories.RoomRepository;
import com.voteplanningpoker.infra.repositories.UserRepository;
import com.voteplanningpoker.service.helper.RoomHelperService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoomParticipantService {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final RoomHelperService roomHelperService;

    @Transactional
    public RoomDto joinRoom(JoinRequest request) {
        var room = roomHelperService.getRoomOrThrow(request.roomId());

        boolean alreadyInRoom = room.getParticipants().stream()
                .anyMatch(user -> user.getName().equalsIgnoreCase(request.userName()));

        if (!alreadyInRoom) {
            UserEntity user = UserEntity.builder()
                    .id(UUID.randomUUID())
                    .name(request.userName())
                    .canRevealVote(false)
                    .isAdmin(false)
                    .build();
            user = userRepository.save(user);

            room.getParticipants().add(user);
            room.getRevealAuthorizedUsers().add(user);
            roomRepository.save(room);
        }

        var roomDomain = Room.builder()
                .id(room.getId().toString())
                .roomName(room.getRoomName())
                .creator(UserConverter.toDomain(room.getCreator()))
                .allowedVotes(List.copyOf(room.getAllowedVotes()))
                .topic(TopicConverter.toDomain(room.getTopic()))
                .participants(UserConverter.toDomainList(List.copyOf(room.getParticipants())))
                .build();

        return RoomDto.from(roomDomain);
    }


    @Transactional
    public RoomDto removeUser(RemoveUserRoomRequest request) {
        var room = roomHelperService.getRoomOrThrow(request.roomId());

        boolean isAuthorized = room.getRevealAuthorizedUsers().stream()
                .anyMatch(user -> user.getName().equals(request.requestedBy()));

        if (!isAuthorized) {
            throw new IllegalArgumentException("Only authorized users can remove users");
        }

        UUID userId = UUID.fromString(request.userIdToRemove());
        boolean isCreator = roomRepository.existsByCreatorId(userId);
        if (isCreator) {
            throw new IllegalArgumentException("Cannot delete user: is creator of a room");
        }
        room.getParticipants().removeIf(user -> user.getId().equals(userId));
        room.getRevealAuthorizedUsers().removeIf(user -> user.getId().equals(userId));

        roomRepository.save(room);

        List<RoomEntity> otherRooms = roomRepository.findAll();
        for (RoomEntity r : otherRooms) {
            if (r.getParticipants().removeIf(u -> u.getId().equals(userId))
                    | r.getRevealAuthorizedUsers().removeIf(u -> u.getId().equals(userId))) {
                roomRepository.save(r);
            }
        }

        userRepository.deleteById(userId);

        var roomDomain = Room.builder()
                .id(room.getId().toString())
                .roomName(room.getRoomName())
                .creator(UserConverter.toDomain(room.getCreator()))
                .allowedVotes(List.copyOf(room.getAllowedVotes()))
                .topic(TopicConverter.toDomain(room.getTopic()))
                .participants(UserConverter.toDomainList(List.copyOf(room.getParticipants())))
                .build();

        return RoomDto.from(roomDomain);
    }
}


