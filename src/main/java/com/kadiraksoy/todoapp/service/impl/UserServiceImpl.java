package com.kadiraksoy.todoapp.service.impl;

import com.kadiraksoy.todoapp.config.DataSourceConfig;
import com.kadiraksoy.todoapp.dto.UserDto;
import com.kadiraksoy.todoapp.entity.User;
import com.kadiraksoy.todoapp.exception.UserNotFoundException;
import com.kadiraksoy.todoapp.repository.IUserRepository;
import com.kadiraksoy.todoapp.service.IUserService;
import com.kadiraksoy.todoapp.utils.FlywayUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final IUserRepository userRepository;
    private final DataSourceConfig dataSourceConfig;


    @Override
    public User create(UserDto userDto) {
        User user = UserDto.toUser(userDto);

        // Flyway yapılandırması
        FlywayUtil.initTenantTables(dataSourceConfig.dataSource(), user.getUsername().toLowerCase() );
        return userRepository.save(user);
    }

    @Override
    public User update(Long id, UserDto userDto) {
        User user = findById(id);
        if(user != null) {
            user.setUsername(userDto.getUsername());
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
