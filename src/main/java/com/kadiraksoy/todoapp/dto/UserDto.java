package com.kadiraksoy.todoapp.dto;

import com.kadiraksoy.todoapp.constant.Role;
import com.kadiraksoy.todoapp.entity.Todo;
import com.kadiraksoy.todoapp.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {


    private String name;
    private String lastName;
    private String email;
    private String password;
    private Role role;
    private List<Todo> todos;

    {
        setRole(Role.USER);
    }

    public static User toUser(UserDto userDto) {
        User user = new User();
        user.setName(userDto.getName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        user.setRole(userDto.getRole());
        user.setTodos(userDto.getTodos());
        return user;
    }
}
