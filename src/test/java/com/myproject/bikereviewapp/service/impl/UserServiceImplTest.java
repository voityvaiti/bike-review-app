package com.myproject.bikereviewapp.service.impl;

import com.myproject.bikereviewapp.entity.Role;
import com.myproject.bikereviewapp.entity.User;
import com.myproject.bikereviewapp.exceptionhandler.exception.EntityNotFoundException;
import com.myproject.bikereviewapp.exceptionhandler.exception.UniquenessConstraintViolationException;
import com.myproject.bikereviewapp.repository.UserRepository;
import com.myproject.bikereviewapp.service.abstraction.ImageCloudService;
import com.myproject.bikereviewapp.service.abstraction.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = UserServiceImpl.class)
class UserServiceImplTest {

    @MockBean
    UserRepository userRepository;

    @MockBean
    ImageCloudService imageCloudService;

    @MockBean
    PasswordEncoder passwordEncoder;


    @Autowired
    UserService userService;


    @Test
    void isCorrectCredentials_shouldReturnTrue_ifPasswordsMatches() {
        Long id = 10L;
        String username = "someUsername";
        String password = "somePassword";
        String encodedPassword = "encodedPassword";

        User user = new User(id, username, encodedPassword, true, Role.CLIENT, "somePublicName");

        UserService spyUserService = spy(userService);
        doReturn(user).when(spyUserService).getByUsername(username);

        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);

        assertTrue(spyUserService.isCorrectCredentials(username, password));

        verify(spyUserService).getByUsername(username);
        verify(passwordEncoder).matches(password, encodedPassword);
    }

    @Test
    void isCorrectCredentials_shouldReturnFalse_ifPasswordsMismatches() {

        Long id = 10L;
        String username = "someUsername";
        String password = "somePassword";
        String encodedPassword = "encodedPassword";

        User user = new User(id, username, encodedPassword, true, Role.CLIENT, "somePublicName");

        UserService spyUserService = spy(userService);
        doReturn(user).when(spyUserService).getByUsername(username);

        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(false);

        assertFalse(spyUserService.isCorrectCredentials(username, password));

        verify(spyUserService).getByUsername(username);
        verify(passwordEncoder).matches(password, encodedPassword);
    }

    @Test
    void getById_shouldReturnUser_whenUserWasFound() {
        Long id = 8L;
        User user = new User(id, "someUsername", "somePassword", true, Role.CLIENT, "somePublicName");

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        assertEquals(user, userService.getById(id));

        verify(userRepository).findById(id);
    }

    @Test
    void getById_shouldThrowException_whenUserWasNotFound() {
        Long id = 8L;

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getById(id));

        verify(userRepository).findById(id);
    }

    @Test
    void getByUsername_shouldReturnUser_whenUserWasFound() {
        Long id = 8L;
        String username = "someUsername";
        User user = new User(id, username, "somePassword", true, Role.CLIENT, "somePublicName");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        assertEquals(user, userService.getByUsername(username));

        verify(userRepository).findByUsername(username);
    }

    @Test
    void getByUsername_shouldThrowException_whenUserWasNotFound() {
        String username = "someUsername";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getByUsername(username));

        verify(userRepository).findByUsername(username);
    }

    @Test
    void create_shouldSaveUser_ifUserIsUnique() {
        String username = "someUsername";
        User user = new User(8L, username, "somePassword", true, Role.CLIENT, "somePublicName");

        when(userRepository.existsUserByUsername(username)).thenReturn(false);

        userService.create(user);

        verify(userRepository).existsUserByUsername(username);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void create_shouldSaveAndReturnUserWithEncodedPassword_ifUserIsUnique() {
        String username = "someUsername";

        String rawPassword = "rawPassword";
        String encodedPassword = "encodedPassword";

        User user = new User(8L, username, rawPassword, true, Role.CLIENT, "somePublicName");
        User expectedUser = new User(user.getId(), user.getUsername(), encodedPassword, user.isEnabled(), user.getRole(), user.getPublicName());

        when(userRepository.existsUserByUsername(username)).thenReturn(false);
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(userRepository.save(expectedUser)).thenReturn(expectedUser);

        assertEquals(expectedUser, userService.create(user));

        verify(userRepository).existsUserByUsername(username);
        verify(passwordEncoder).encode(rawPassword);
        verify(userRepository).save(expectedUser);
    }

    @Test
    void create_shouldThrowException_ifUserIsDuplicated() {
        String username = "someUsername";
        User user = new User(8L, username, "somePassword", true, Role.CLIENT, "somePublicName");

        when(userRepository.existsUserByUsername(username)).thenReturn(true);

        assertThrows(UniquenessConstraintViolationException.class, () -> userService.create(user));

        verify(userRepository).existsUserByUsername(username);
        verify(userRepository, never()).save(any());
    }


    @Test
    void toggleStatus_shouldGetAndSaveUser() {
        Long id = 12L;

        User user = new User(id, "someUsername", "somePassword", true, Role.CLIENT, "somePublicName");

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        userService.toggleStatus(id);

        verify(userRepository).findById(id);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void toggleStatus_shouldGetAndSaveUserWithToggledStatus() {
        Long id = 12L;

        User user = new User(id, "someUsername", "somePassword", true, Role.CLIENT, "somePublicName");
        User expectedUser = new User(user.getId(), user.getUsername(), user.getPassword(), !user.isEnabled(), user.getRole(), user.getPublicName());

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        userService.toggleStatus(id);

        verify(userRepository).findById(id);
        verify(userRepository).save(expectedUser);
    }

    @Test
    void updatePassword_shouldGetAndSaveUser() {
        Long id = 12L;
        String updatedPassword = "updatedPassword";

        User user = new User(id, "someUsername", "somePassword", true, Role.CLIENT, "somePublicName");

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        userService.updatePassword(id, updatedPassword);

        verify(userRepository).findById(id);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updatePassword_shouldGetAndSaveUserWithUpdatedAndEncodedPassword() {
        Long id = 12L;

        String updatedPassword = "updatedPassword";
        String encodedUpdatedPassword = "encodedUpdatedPassword";

        User user = new User(id, "someUsername", "somePassword", true, Role.CLIENT, "somePublicName");
        User expectedUser = new User(id, "someUsername", encodedUpdatedPassword, true, Role.CLIENT, "somePublicName");

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(updatedPassword)).thenReturn(encodedUpdatedPassword);

        userService.updatePassword(id, updatedPassword);

        verify(userRepository).findById(id);
        verify(userRepository).save(expectedUser);
    }


    @Test
    void updatePublicName_shouldGetAndSaveUser() {
        Long id = 12L;
        String updatedPublicName = "updatedPublicName";

        User user = new User(id, "someUsername", "somePassword", true, Role.CLIENT, "somePublicName");

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        userService.updatePublicName(id, updatedPublicName);

        verify(userRepository).findById(id);
        verify(userRepository).save(any(User.class));
    }


    @Test
    void updatePublicName_shouldGetAndSaveUserWithUpdatedPublicName() {
        Long id = 12L;

        String updatedPublicName = "updatedPublicName";

        User user = new User(id, "someUsername", "somePassword", true, Role.CLIENT, "somePublicName");
        User expectedUser = new User(user.getId(), user.getUsername(), user.getPassword(), user.isEnabled(), user.getRole(), updatedPublicName);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        userService.updatePublicName(id, updatedPublicName);

        verify(userRepository).findById(id);
        verify(userRepository).save(expectedUser);
    }

}