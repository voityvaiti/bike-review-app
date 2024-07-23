package com.myproject.bikereviewapp.service.impl;

import com.myproject.bikereviewapp.entity.Role;
import com.myproject.bikereviewapp.entity.User;
import com.myproject.bikereviewapp.exceptionhandler.exception.EntityNotFoundException;
import com.myproject.bikereviewapp.exceptionhandler.exception.UniquenessConstraintViolationException;
import com.myproject.bikereviewapp.repository.UserRepository;
import com.myproject.bikereviewapp.service.abstraction.ImageService;
import com.myproject.bikereviewapp.service.abstraction.UserService;
import org.junit.jupiter.api.BeforeAll;
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
    ImageService imageService;

    @MockBean
    PasswordEncoder passwordEncoder;


    @Autowired
    UserService userService;


    private static final Long id = 7L;
    private static final User user = new User();

    @BeforeAll
    static void init() {

        user.setId(id);
        user.setUsername("someUsername");
        user.setPassword("somePassword");
        user.setEnabled(true);
        user.setRole(Role.CLIENT);
        user.setPublicName("somePublicName");
    }


    @Test
    void isCorrectCredentials_shouldReturnTrue_ifPasswordsMatches() {

        String username = "someUsername";
        String password = "somePassword";
        String encodedPassword = "encodedPassword";

        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setPassword(encodedPassword);
        user.setEnabled(true);
        user.setRole(Role.CLIENT);
        user.setPublicName("somePublicName");


        UserService spyUserService = spy(userService);
        doReturn(user).when(spyUserService).getByUsername(username);

        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);

        assertTrue(spyUserService.isCorrectCredentials(username, password));

        verify(spyUserService).getByUsername(username);
        verify(passwordEncoder).matches(password, encodedPassword);
    }

    @Test
    void isCorrectCredentials_shouldReturnFalse_ifPasswordsMismatches() {

        String username = "someUsername";
        String password = "somePassword";
        String encodedPassword = "encodedPassword";

        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setPassword(encodedPassword);
        user.setEnabled(true);
        user.setRole(Role.CLIENT);
        user.setPublicName("somePublicName");


        UserService spyUserService = spy(userService);
        doReturn(user).when(spyUserService).getByUsername(username);

        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(false);

        assertFalse(spyUserService.isCorrectCredentials(username, password));

        verify(spyUserService).getByUsername(username);
        verify(passwordEncoder).matches(password, encodedPassword);
    }

    @Test
    void getById_shouldReturnUser_whenUserWasFound() {

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        assertEquals(user, userService.getById(id));

        verify(userRepository).findById(id);
    }

    @Test
    void getById_shouldThrowException_whenUserWasNotFound() {

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getById(id));

        verify(userRepository).findById(id);
    }

    @Test
    void getByUsername_shouldReturnUser_whenUserWasFound() {

        String username = "someUsername";

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

        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setPassword(rawPassword);
        user.setEnabled(true);
        user.setRole(Role.CLIENT);
        user.setPublicName("somePublicName");

        User expectedUser = new User();
        expectedUser.setId(user.getId());
        expectedUser.setUsername(user.getUsername());
        expectedUser.setPassword(encodedPassword);
        expectedUser.setEnabled(user.isEnabled());
        expectedUser.setRole(user.getRole());
        expectedUser.setPublicName(user.getPublicName());


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

        when(userRepository.existsUserByUsername(username)).thenReturn(true);

        assertThrows(UniquenessConstraintViolationException.class, () -> userService.create(user));

        verify(userRepository).existsUserByUsername(username);
        verify(userRepository, never()).save(any());
    }


    @Test
    void toggleStatus_shouldGetAndSaveUser() {

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        userService.toggleStatus(id);

        verify(userRepository).findById(id);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void toggleStatus_shouldGetAndSaveUserWithToggledStatus() {

        User expectedUser = new User();
        expectedUser.setId(user.getId());
        expectedUser.setUsername(user.getUsername());
        expectedUser.setPassword(user.getPassword());
        expectedUser.setEnabled(!user.isEnabled());
        expectedUser.setRole(user.getRole());
        expectedUser.setPublicName(user.getPublicName());

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        userService.toggleStatus(id);

        verify(userRepository).findById(id);
        verify(userRepository).save(expectedUser);
    }

    @Test
    void updatePassword_shouldGetAndSaveUser() {

        String updatedPassword = "updatedPassword";

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        userService.updatePassword(id, updatedPassword);

        verify(userRepository).findById(id);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updatePassword_shouldGetAndSaveUserWithUpdatedAndEncodedPassword() {

        String updatedPassword = "updatedPassword";
        String encodedUpdatedPassword = "encodedUpdatedPassword";

        User expectedUser = new User();
        expectedUser.setId(id);
        expectedUser.setUsername(user.getUsername());
        expectedUser.setPassword(encodedUpdatedPassword);
        expectedUser.setEnabled(user.isEnabled());
        expectedUser.setRole(user.getRole());
        expectedUser.setPublicName(user.getPublicName());

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(updatedPassword)).thenReturn(encodedUpdatedPassword);

        userService.updatePassword(id, updatedPassword);

        verify(userRepository).findById(id);
        verify(userRepository).save(expectedUser);
    }


    @Test
    void updatePublicName_shouldGetAndSaveUser() {

        String updatedPublicName = "updatedPublicName";

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        userService.updatePublicName(id, updatedPublicName);

        verify(userRepository).findById(id);
        verify(userRepository).save(any(User.class));
    }


    @Test
    void updatePublicName_shouldGetAndSaveUserWithUpdatedPublicName() {

        String updatedPublicName = "updatedPublicName";

        User expectedUser = new User();
        expectedUser.setId(id);
        expectedUser.setUsername(user.getUsername());
        expectedUser.setPassword(user.getPassword());
        expectedUser.setEnabled(user.isEnabled());
        expectedUser.setRole(user.getRole());
        expectedUser.setPublicName(updatedPublicName);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        userService.updatePublicName(id, updatedPublicName);

        verify(userRepository).findById(id);
        verify(userRepository).save(expectedUser);
    }

}