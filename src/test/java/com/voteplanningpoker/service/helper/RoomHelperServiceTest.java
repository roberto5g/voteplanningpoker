package com.voteplanningpoker.service.helper;

import com.voteplanningpoker.infra.entities.RoomEntity;
import com.voteplanningpoker.infra.repositories.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoomHelperServiceTest {
    private RoomRepository roomRepository;
    private RoomHelperService roomHelperService;

    @BeforeEach
    void setUp() {
        roomRepository = mock(RoomRepository.class);
        roomHelperService = new RoomHelperService(roomRepository);
    }

    @Test
    void getRoomOrThrow_returnsRoomEntity_whenFound() {
        String roomId = UUID.randomUUID().toString();
        RoomEntity roomEntity = new RoomEntity();
        when(roomRepository.findWithAllRelationsById(UUID.fromString(roomId)))
                .thenReturn(Optional.of(roomEntity));

        RoomEntity result = roomHelperService.getRoomOrThrow(roomId);
        assertNotNull(result);
        assertEquals(roomEntity, result);
    }

    @Test
    void getRoomOrThrow_throwsException_whenNotFound() {
        String roomId = UUID.randomUUID().toString();
        when(roomRepository.findWithAllRelationsById(UUID.fromString(roomId)))
                .thenReturn(Optional.empty());

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                roomHelperService.getRoomOrThrow(roomId));
        assertEquals("Room not found or already closed", exception.getMessage());
    }
}

