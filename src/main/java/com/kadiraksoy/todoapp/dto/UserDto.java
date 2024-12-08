package com.kadiraksoy.todoapp.dto;

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


    private String username;
    private String email;
    private String password;
    private List<Todo> todos;

    public static User toUser(UserDto userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        user.setTodos(userDto.getTodos());
        return user;
    }
}
