package com.myproject.bikereviewapp.service.impl;

import com.myproject.bikereviewapp.entity.Motorcycle;
import com.myproject.bikereviewapp.entity.User;
import com.myproject.bikereviewapp.exceptionhandler.exception.EntityNotFoundException;
import com.myproject.bikereviewapp.exceptionhandler.exception.UniquenessConstraintViolationException;
import com.myproject.bikereviewapp.repository.UserRepository;
import com.myproject.bikereviewapp.service.abstraction.CloudService;
import com.myproject.bikereviewapp.service.abstraction.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final String USER_IMAGES_FOLDER = "user";


    private final UserRepository userRepository;

    private final CloudService cloudService;

    private final PasswordEncoder passwordEncoder;



    @Override
    public boolean exists(String username) {
        return userRepository.existsUserByUsername(username);
    }

    @Override
    public boolean isCorrectCredentials(String username, String password) {

        log.debug("Checking credentials of user with username: {}", username);

        User user = getByUsername(username);

        return passwordEncoder.matches(password, user.getPassword());
    }

    @Override
    public Page<User> getAll(Pageable pageable) {

        log.debug("Page request received: {}", pageable);

        return userRepository.findAll(pageable);
    }

    @Override
    public User getById(Long id) {

        log.debug("Looking for User with ID: {}", id);

        return userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("User with id " + id + " not found")
        );
    }

    @Override
    public User getByUsername(String username) {

        log.debug("Looking for User with username: {}", username);

        return userRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException("User with username " + username + " not found")
        );
    }

    @Override
    public User create(User user) {

        if (userRepository.existsUserByUsername(user.getUsername())) {
            throw new UniquenessConstraintViolationException("User with username " + user.getUsername() + " already exists");
        }

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        log.debug("Saving new User: {}", user);

        return userRepository.save(user);
    }

    @Override
    public void toggleStatus(Long id) {

        User currentUser = getById(id);

        currentUser.setEnabled(!currentUser.isEnabled());

        userRepository.save(currentUser);

        log.debug("Toggled status of User with ID: {}", id);
    }

    @Override
    public User updatePassword(Long id, String password) {

        User user = getById(id);

        user.setPassword(passwordEncoder.encode(password));

        log.debug("Saving User with updated password with ID: {}", id);

        return userRepository.save(user);
    }

    @Override
    public User updatePublicName(Long id, String publicName) {

        User user = getById(id);

        user.setPublicName(publicName);

        log.debug("Saving User with updated public name with ID: {}", id);

        return userRepository.save(user);
    }

    @Override
    public void uploadImg(Long id, MultipartFile file) {

        User user = userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("user with id " + id + "not found")
        );

        if(user.getImgUrl() != null && !user.getImgUrl().isBlank()) {
            cloudService.delete(USER_IMAGES_FOLDER, user.getId().toString());
        }

        user.setImgUrl(
                cloudService.upload(file, USER_IMAGES_FOLDER, user.getId().toString())
        );
        userRepository.save(user);
    }


    @Override
    public void delete(Long id) {

        log.debug("Removing User with ID: {}", id);

        userRepository.delete(getById(id));
    }
}
