package com.voteplanningpoker.service.helper;

import com.voteplanningpoker.infra.entities.RoomEntity;
import com.voteplanningpoker.infra.repositories.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoomHelperService {
    private final RoomRepository roomRepository;

    public RoomEntity getRoomOrThrow(String roomId) {
        return roomRepository.findWithAllRelationsById(UUID.fromString(roomId))
                .orElseThrow(() -> new IllegalStateException("Room not found or already closed"));
    }
}

