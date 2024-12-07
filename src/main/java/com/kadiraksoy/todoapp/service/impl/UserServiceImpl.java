package com.kadiraksoy.todoapp.service.impl;

import com.kadiraksoy.todoapp.dto.UserDto;
import com.kadiraksoy.todoapp.entity.User;
import com.kadiraksoy.todoapp.exception.UserNotFoundException;
import com.kadiraksoy.todoapp.repository.IUserRepository;
import com.kadiraksoy.todoapp.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final IUserRepository userRepository;

    @Override
    public User create(UserDto userDto) {
        User user = UserDto.toUser(userDto);
        return userRepository.save(user);
    }

    @Override
    public User update(Long id, UserDto userDto) {
        User user = findById(id);
        if(user != null) {
            user.setName(userDto.getName());
            user.setLastName(userDto.getLastName());
            user.setEmail(userDto.getEmail());
            user.setPassword(userDto.getPassword());
            return userRepository.save(user);
        }
        return null;
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow();
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }
}
