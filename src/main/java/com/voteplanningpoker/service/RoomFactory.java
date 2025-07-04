package com.voteplanningpoker.service;

import com.voteplanningpoker.converters.TopicConverter;
import com.voteplanningpoker.converters.UserConverter;
import com.voteplanningpoker.domain.Room;
import com.voteplanningpoker.dto.CreateRoomRequest;
import com.voteplanningpoker.dto.RoomDto;
import com.voteplanningpoker.infra.entities.RoomEntity;
import com.voteplanningpoker.infra.entities.UserEntity;
import com.voteplanningpoker.infra.repositories.RoomRepository;
import com.voteplanningpoker.infra.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoomFactory {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    @Transactional
    public RoomDto createRoom(CreateRoomRequest request) {
        UserEntity creator = UserEntity.builder()
                .id(UUID.randomUUID())
                .name(request.userName())
                .canRevealVote(true)
                .isAdmin(true)
                .build();

        creator = userRepository.save(creator);

        RoomEntity room = RoomEntity.builder()
                .id(UUID.randomUUID())
                .creator(creator)
                .roomName(request.roomName())
                .allowedVotes(new HashSet<>(getAllowedVotesPermitted(request.allowedVotes())))
                .participants(new HashSet<>(Set.of(creator)))
                .revealAuthorizedUsers(new HashSet<>(Set.of(creator)))
                .build();

        room = roomRepository.save(room);

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

    private List<Integer> getAllowedVotesPermitted(List<Integer> allowedVotes) {
        if (allowedVotes == null || allowedVotes.isEmpty()) {
            allowedVotes = List.of(1, 2, 3, 5, 8, 13);
        }
        return allowedVotes;
    }
}


