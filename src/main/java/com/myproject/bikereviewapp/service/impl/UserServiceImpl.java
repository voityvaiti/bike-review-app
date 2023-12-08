package com.myproject.bikereviewapp.service.impl;

import com.myproject.bikereviewapp.entity.User;
import com.myproject.bikereviewapp.exceptionhandler.exception.EntityNotFoundException;
import com.myproject.bikereviewapp.exceptionhandler.exception.UserDuplicationException;
import com.myproject.bikereviewapp.repository.UserRepository;
import com.myproject.bikereviewapp.service.abstraction.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public boolean exists(String username) {
        return userRepository.existsUserByUsername(username);
    }

    @Override
    public boolean isCorrectCredentials(String username, String password) {

        User user = getByUsername(username);

        return passwordEncoder.matches(password, user.getPassword());
    }

    @Override
    public Page<User> getAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("User with id " + id + "not found")
        );
    }

    @Override
    public User getByUsername(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isEmpty()) {
            throw new EntityNotFoundException("User with username " + username + "not found");
        }

        return optionalUser.get();
    }

    @Override
    public User create(User user) {

        if (userRepository.existsUserByUsername(user.getUsername())) {
            throw new UserDuplicationException("User with username " + user.getUsername() + " already exists");
        }

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        return userRepository.save(user);
    }

    @Override
    public void toggleStatus(Long id) {

        User currentUser = getById(id);

        currentUser.setEnabled(!currentUser.isEnabled());

        userRepository.save(currentUser);
    }

    @Override
    public User updatePassword(Long id, String password) {

        User user = getById(id);

        user.setPassword(passwordEncoder.encode(password));

        return userRepository.save(user);
    }

    @Override
    public User updatePublicName(Long id, String publicName) {

        User user = getById(id);

        user.setPublicName(publicName);

        return userRepository.save(user);
    }


    @Override
    public void delete(Long id) {
        userRepository.delete(getById(id));
    }
}
