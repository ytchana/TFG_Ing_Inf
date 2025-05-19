package com.example.walkindoorrestapi;
import com.example.walkindoorrestapi.entities.User;
import com.example.walkindoorrestapi.repositories.UserRepository;
import com.example.walkindoorrestapi.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


import java.util.Date;

/**
 * Este test unitario cubre:
 *      Buscar un usuario por ID (testGetUserById()).
 *      Crear un nuevo usuario (testCreateUser()).
 *      Eliminar un usuario (testDeleteUser()).
 */

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUserById() {
        User user = new User("Yanick", "yanick@example.com", "securePassword", new Date());
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User foundUser = userService.getUserById(1L)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));


        assertNotNull(foundUser);
        assertEquals(1L, foundUser.getId());
        assertEquals("Yanick", foundUser.getUsername());
        assertEquals("yanick@example.com", foundUser.getEmail());
    }

    @Test
    void testCreateUser() {
        User user = new User("NewUser", "newuser@example.com", "strongPassword", new Date());

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(2L);
            return savedUser;
        });

        User createdUser = userService.createUser(user);

        assertNotNull(createdUser);
        assertEquals(2L, createdUser.getId());
        assertEquals("NewUser", createdUser.getUsername());
        assertEquals("newuser@example.com", createdUser.getEmail());
    }

    @Test
    void testDeleteUser() {
        Long userId = 3L;
        doNothing().when(userRepository).deleteById(userId);

        userService.deleteUser(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }
}
