package com.myproject.bikereviewapp.service.impl;

import com.myproject.bikereviewapp.entity.User;
import com.myproject.bikereviewapp.exceptionhandler.exception.EntityNotFoundException;
import com.myproject.bikereviewapp.exceptionhandler.exception.UserDuplicationException;
import com.myproject.bikereviewapp.repository.UserRepository;
import com.myproject.bikereviewapp.service.abstraction.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public boolean exists(String username) {
        return userRepository.existsUserByUsername(username);
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public List<User> getAllSortedByIdAsc() {
        return userRepository.findAllByOrderByIdAsc();
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

        String encodedPassword = new BCryptPasswordEncoder().encode(user.getPassword());
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
    public void delete(Long id) {
        userRepository.delete(getById(id));
    }
}
