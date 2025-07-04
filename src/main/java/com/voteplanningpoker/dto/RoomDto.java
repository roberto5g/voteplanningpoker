package com.voteplanningpoker.dto;

import com.voteplanningpoker.domain.Room;
import com.voteplanningpoker.domain.Topic;
import com.voteplanningpoker.domain.User;
import com.voteplanningpoker.infra.entities.RoomEntity;
import lombok.Builder;

import java.util.List;
import java.util.Set;

@Builder
public record RoomDto(
        String id,
        User creator,
        String roomName,
        Set<User> participants,
        Topic topic,
        List<Integer> allowedVotes,
        Set<User> revealAuthorizedUsers
) {
    public static RoomDto from(Room room) {
        return new RoomDto(
                room.getId(),
                room.getCreator(),
                room.getRoomName(),
                Set.copyOf(room.getParticipants()),
                room.getTopic(),
                room.getAllowedVotes(),
                room.getRevealAuthorizedUsers()
        );
    }

}
