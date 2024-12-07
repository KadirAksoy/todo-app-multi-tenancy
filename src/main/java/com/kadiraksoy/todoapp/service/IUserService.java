package com.kadiraksoy.todoapp.service;

import com.kadiraksoy.todoapp.dto.UserDto;
import com.kadiraksoy.todoapp.entity.User;

import java.util.List;

public interface IUserService {

    User create(UserDto userDto);
    User update(Long id, UserDto userDto);
    void delete(Long id);
    User findById(Long id);
    List<User> findAll();
}
