package com.kadiraksoy.todoapp.service.impl;

import com.kadiraksoy.todoapp.dto.TodoDto;
import com.kadiraksoy.todoapp.dto.TodoResponse;
import com.kadiraksoy.todoapp.entity.Todo;
import com.kadiraksoy.todoapp.entity.User;
import com.kadiraksoy.todoapp.exception.TodoNotFoundException;
import com.kadiraksoy.todoapp.exception.UserNotFoundException;
import com.kadiraksoy.todoapp.repository.ITodoRepository;
import com.kadiraksoy.todoapp.service.ITodoService;
import com.kadiraksoy.todoapp.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TodoServiceImpl implements ITodoService {

    private final ITodoRepository todoRepository;
    private final IUserService userService;

    @Override
    public TodoResponse create(TodoDto todoDto) {
        Todo todo = TodoDto.toTodo(todoDto);
        todo.setUser(userService.findById(todoDto.getUserId()));
        todoRepository.save(todo);
        return TodoResponse.toTodoResponse(todo);
    }

    @Override
    public TodoResponse update(Long id, TodoDto todoDto) {
        Todo todo = todoRepository.findById(id).orElseThrow();
        todo.setTitle(todoDto.getTitle());
        todo.setDescription(todoDto.getDescription());
        if (todoDto.getUserId() != null) {
            User user = userService.findById(todoDto.getUserId());
            if (user == null) {
                throw new UserNotFoundException("User with ID " + todoDto.getUserId() + " not found");
            }
            todo.setUser(user);
        }
        todoRepository.save(todo);
        return TodoResponse.toTodoResponse(todo);
    }

    @Override
    public void delete(Long id) {
        todoRepository.deleteById(id);
    }

    @Override
    public TodoResponse findById(Long id) {
       Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException("Todo with ID " + id + " not found"));
       return TodoResponse.toTodoResponse(todo);
    }


    @Override
    public List<TodoResponse> findAll() {
        List<Todo> todos = todoRepository.findAll();
        return todos.stream().map(TodoResponse::toTodoResponse).toList();
    }
}
