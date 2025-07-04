package com.voteplanningpoker.service;

import com.voteplanningpoker.converters.TopicConverter;
import com.voteplanningpoker.converters.UserConverter;
import com.voteplanningpoker.domain.Room;
import com.voteplanningpoker.dto.*;
import com.voteplanningpoker.infra.entities.UserEntity;
import com.voteplanningpoker.infra.entities.VoteEntity;
import com.voteplanningpoker.infra.repositories.RoomRepository;
import com.voteplanningpoker.service.helper.RoomHelperService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomVoteService {

    private final RoomRepository roomRepository;
    private final RoomHelperService roomHelperService;

    @Transactional
    public VoteRegisteredResponse vote(VoteRequest request) {
        var room = roomHelperService.getRoomOrThrow(request.roomId());
        var topic = room.getTopic();

        boolean isParticipant = room.getParticipants().stream()
                .anyMatch(user -> user.getName().equalsIgnoreCase(request.userName()));

        if (!isParticipant) {
            throw new IllegalArgumentException("User not in room: " + request.userName());
        }

        topic.getVotes().removeIf(v -> v.getUserName().equalsIgnoreCase(request.userName()));

        topic.getVotes().add(
                VoteEntity.builder()
                        .userName(request.userName())
                        .vote(request.vote())
                        .topic(topic)
                        .build()
        );

        roomRepository.save(room);

        var voters = topic.getVotes().stream()
                .map(VoteEntity::getUserName)
                .collect(Collectors.toSet());

        return new VoteRegisteredResponse(room.getId().toString(), voters);
    }


    @Transactional
    public RevealVotesResponse revealVotes(RevealVotesRequest request) {
        var room = roomHelperService.getRoomOrThrow(request.roomId());
        var topic = room.getTopic();

        boolean isAuthorized = room.getRevealAuthorizedUsers().stream()
                .anyMatch(user -> user.getName().equals(request.userName()))
                || room.getRevealAuthorizedUsers().stream().anyMatch(UserEntity::isAdmin);

        if (!isAuthorized) {
            throw new IllegalArgumentException("User not authorized in room: " + request.userName());
        }

        topic.setVotesRevealed(true);

        var votes = topic.getVotes();
        if (votes == null || votes.isEmpty()) {
            throw new IllegalArgumentException("No votes to reveal");
        }

        Map<String, Integer> userVotes = votes.stream()
                .collect(Collectors.toMap(
                        VoteEntity::getUserName,
                        VoteEntity::getVote,
                        (existing, replacement) -> existing
                ));

        double average = votes.stream()
                .mapToInt(VoteEntity::getVote)
                .average()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "No votes found"));

        average = BigDecimal.valueOf(average)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();

        topic.setAverage(average);

        int suggested;
        List<Integer> allowedVotes = List.copyOf(room.getAllowedVotes());
        if (average % 1 == 0 && allowedVotes.contains((int) average)) {
            suggested = (int) average;
        } else {
            double finalAverage = average;
            suggested = allowedVotes.stream()
                    .filter(v -> v > finalAverage)
                    .min(Integer::compareTo)
                    .orElse(allowedVotes.get(allowedVotes.size() - 1));
        }

        topic.setSuggested(suggested);

        roomRepository.save(room);

        return new RevealVotesResponse(room.getId(), userVotes, average, suggested);
    }

    @Transactional
    public RoomDto resetVotes(ResetVotesRequest request) {
        var room = roomHelperService.getRoomOrThrow(request.roomId());
        var topic = room.getTopic();

        boolean isAuthorized = room.getRevealAuthorizedUsers().stream()
                .anyMatch(user -> user.getName().equals(request.userName()));

        if (!isAuthorized) {
            throw new IllegalArgumentException("User not authorized in room: " + request.userName());
        }

        topic.setVotesRevealed(false);
        topic.setAverage(null);
        topic.setSuggested(null);

        topic.getVotes().clear();

        roomRepository.save(room);

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


