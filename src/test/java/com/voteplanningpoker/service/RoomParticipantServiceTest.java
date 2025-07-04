package com.voteplanningpoker.service;

import com.voteplanningpoker.dto.JoinRequest;
import com.voteplanningpoker.dto.RemoveUserRoomRequest;
import com.voteplanningpoker.dto.RoomDto;
import com.voteplanningpoker.infra.entities.RoomEntity;
import com.voteplanningpoker.infra.entities.UserEntity;
import com.voteplanningpoker.infra.repositories.RoomRepository;
import com.voteplanningpoker.infra.repositories.UserRepository;
import com.voteplanningpoker.service.helper.RoomHelperService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoomParticipantServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoomHelperService roomHelperService;

    @InjectMocks
    private RoomParticipantService roomParticipantService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    void mockRoomWithParticipants(RoomEntity room, UserEntity user) {
        when(roomHelperService.getRoomOrThrow(room.getId().toString())).thenReturn(room);
        when(userRepository.save(any(UserEntity.class))).thenReturn(user);
        when(roomRepository.save(any(RoomEntity.class))).thenReturn(room);
    }

    @Test
    void joinRoomAddsUserToRoomWhenNotAlreadyInRoom() {
        RoomEntity room = new RoomEntity();
        room.setId(UUID.randomUUID());
        room.setParticipants(new HashSet<>(Set.of(new UserEntity(UUID.randomUUID(), "existingUser", true, true))));

        when(roomHelperService.getRoomOrThrow(room.getId().toString())).thenReturn(room);
        UserEntity user = new UserEntity();
        user.setName("newUser");

        mockRoomWithParticipants(room, user);

        JoinRequest request = new JoinRequest(room.getId().toString(), "newUser", List.of(1, 2, 3, 5, 8));
        RoomDto result = roomParticipantService.joinRoom(request);

        assertEquals(2, result.participants().size());
        assertTrue(result.participants().stream().anyMatch(p -> p.getName().equals("newUser")));
        verify(userRepository, times(1)).save(any(UserEntity.class));
        verify(roomRepository, times(1)).save(room);
    }

    @Test
    void joinRoomDoesNotAddUserWhenAlreadyInRoom() {
        UserEntity existingUser = new UserEntity();
        existingUser.setName("existingUser");
        RoomEntity room = new RoomEntity();
        room.setId(UUID.randomUUID());
        room.setParticipants(Set.of(existingUser));

        when(roomHelperService.getRoomOrThrow(room.getId().toString())).thenReturn(room);

        JoinRequest request = new JoinRequest(room.getId().toString(), "existingUser", List.of(1, 2, 3, 5, 8));
        RoomDto result = roomParticipantService.joinRoom(request);

        assertEquals(1, result.participants().size());
        verify(userRepository, never()).save(any(UserEntity.class));
        verify(roomRepository, never()).save(room);
    }

    @Test
    void removeUserRemovesUserFromRoomWhenAuthorized() {
        UserEntity userToRemove = new UserEntity();
        userToRemove.setId(UUID.randomUUID());
        userToRemove.setName("userToRemove");
        UserEntity authorizedUser = new UserEntity();
        authorizedUser.setId(UUID.randomUUID());
        authorizedUser.setName("authorizedUser");
        authorizedUser.setAdmin(true);

        RoomEntity room = new RoomEntity();
        room.setId(UUID.randomUUID());
        room.setParticipants(new HashSet<>(Set.of(userToRemove, authorizedUser)));
        room.setRevealAuthorizedUsers(new HashSet<>(Set.of(authorizedUser)));

        when(roomHelperService.getRoomOrThrow(room.getId().toString())).thenReturn(room);
        when(roomRepository.findAll()).thenReturn(List.of(room));
        when(roomRepository.existsByCreatorId(userToRemove.getId())).thenReturn(false);

        RemoveUserRoomRequest request = new RemoveUserRoomRequest(room.getId().toString(), "authorizedUser", userToRemove.getId().toString());
        RoomDto result = roomParticipantService.removeUser(request);

        assertEquals(1, result.participants().size());
        assertFalse(result.participants().stream().anyMatch(p -> p.getId().equals(userToRemove.getId().toString())));
        verify(userRepository, times(1)).deleteById(userToRemove.getId());
        verify(roomRepository, times(1)).save(room);
    }

    @Test
    void removeUserThrowsExceptionWhenUserIsCreator() {
        UserEntity userToRemove = new UserEntity();
        userToRemove.setId(UUID.randomUUID());
        userToRemove.setName("userToRemove");
        UserEntity authorizedUser = new UserEntity();
        authorizedUser.setId(UUID.randomUUID());
        authorizedUser.setName("authorizedUser");
        authorizedUser.setAdmin(true);

        RoomEntity room = new RoomEntity();
        room.setId(UUID.randomUUID());
        room.setParticipants(new HashSet<>(Set.of(userToRemove, authorizedUser)));
        room.setCreator(authorizedUser);
        room.setRevealAuthorizedUsers(new HashSet<>(Set.of(authorizedUser)));

        when(roomHelperService.getRoomOrThrow(room.getId().toString())).thenReturn(room);
        when(roomRepository.existsByCreatorId(userToRemove.getId())).thenReturn(true);

        RemoveUserRoomRequest request = new RemoveUserRoomRequest(room.getId().toString(), "authorizedUser", userToRemove.getId().toString());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> roomParticipantService.removeUser(request));
        assertEquals("Cannot delete user: is creator of a room", exception.getMessage());
        verify(userRepository, never()).deleteById(userToRemove.getId());
    }

    @Test
    void removeUserThrowsExceptionWhenUserIsNotAuthorized() {
        UserEntity userToRemove = new UserEntity();
        userToRemove.setId(UUID.randomUUID());
        userToRemove.setName("userToRemove");
        UserEntity unauthorized = new UserEntity();
        unauthorized.setId(UUID.randomUUID());
        unauthorized.setName("unauthorizedUser");
        unauthorized.setAdmin(false);
        UserEntity authorizedUser = new UserEntity();
        authorizedUser.setId(UUID.randomUUID());
        authorizedUser.setName("authorizedUser");
        authorizedUser.setAdmin(true);


        RoomEntity room = new RoomEntity();
        room.setId(UUID.randomUUID());
        room.setParticipants(new HashSet<>(Set.of(userToRemove, unauthorized, authorizedUser)));
        room.setCreator(authorizedUser);
        room.setRevealAuthorizedUsers(new HashSet<>(Set.of(authorizedUser)));

        when(roomHelperService.getRoomOrThrow(room.getId().toString())).thenReturn(room);
        when(roomRepository.existsByCreatorId(userToRemove.getId())).thenReturn(true);

        RemoveUserRoomRequest request = new RemoveUserRoomRequest(room.getId().toString(), "unauthorizedUser", userToRemove.getId().toString());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> roomParticipantService.removeUser(request));
        assertEquals("Only authorized users can remove users", exception.getMessage());
        verify(userRepository, never()).deleteById(userToRemove.getId());
    }

    @Test
    void removeUserRemovesUserFromOtherRoomsAndSavesThem() {
        UUID userId = UUID.randomUUID();
        UserEntity userToRemove = new UserEntity();
        userToRemove.setId(userId);
        userToRemove.setName("userToRemove");

        UserEntity authorizedUser = new UserEntity();
        authorizedUser.setId(UUID.randomUUID());
        authorizedUser.setName("authorizedUser");

        RoomEntity mainRoom = new RoomEntity();
        mainRoom.setId(UUID.randomUUID());
        mainRoom.setParticipants(new HashSet<>(Set.of(userToRemove, authorizedUser)));
        mainRoom.setRevealAuthorizedUsers(new HashSet<>(Set.of(authorizedUser)));

        RoomEntity otherRoom = new RoomEntity();
        otherRoom.setId(UUID.randomUUID());
        otherRoom.setParticipants(new HashSet<>(Set.of(userToRemove)));
        otherRoom.setRevealAuthorizedUsers(new HashSet<>(Set.of(userToRemove)));

        when(roomHelperService.getRoomOrThrow(mainRoom.getId().toString())).thenReturn(mainRoom);
        when(roomRepository.existsByCreatorId(userId)).thenReturn(false);
        when(roomRepository.findAll()).thenReturn(List.of(mainRoom, otherRoom));

        RemoveUserRoomRequest request = new RemoveUserRoomRequest(
                mainRoom.getId().toString(), "authorizedUser", userId.toString());

        roomParticipantService.removeUser(request);

        assertFalse(otherRoom.getParticipants().contains(userToRemove));
        assertFalse(otherRoom.getRevealAuthorizedUsers().contains(userToRemove));
        verify(roomRepository, atLeastOnce()).save(otherRoom);
        verify(userRepository).deleteById(userId);
    }
}