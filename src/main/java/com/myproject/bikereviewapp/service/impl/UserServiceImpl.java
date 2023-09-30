package com.myproject.bikereviewapp.service.impl;

import com.myproject.bikereviewapp.entity.User;
import com.myproject.bikereviewapp.repository.UserRepository;
import com.myproject.bikereviewapp.service.abstraction.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public User create(User user) {

        return userRepository.save(user);
    }

    @Override
    public void delete(Long id) {

        userRepository.deleteById(id);
    }
}
