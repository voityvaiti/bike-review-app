package com.myproject.bikereviewapp.service.abstraction;

import com.myproject.bikereviewapp.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    boolean exists(String username);

    boolean isCorrectCredentials(String username, String password);

    Page<User> getAll(Pageable pageable);

    User getById(Long id);

    User getByUsername(String name);

    User create(User user);

    void toggleStatus(Long id);

    User updatePassword(Long id, String password);

    User updatePublicName(Long id, String publicName);

    void delete(Long id);
    
}
