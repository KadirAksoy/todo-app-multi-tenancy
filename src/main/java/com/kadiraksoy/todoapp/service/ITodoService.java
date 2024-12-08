package com.kadiraksoy.todoapp.service;

import com.kadiraksoy.todoapp.dto.TodoDto;
import com.kadiraksoy.todoapp.dto.response.TodoResponse;


import java.util.List;

public interface ITodoService {

    TodoResponse create(TodoDto todoDto);
    TodoResponse update(Long id, TodoDto todoDto);
    void delete(Long id);
    TodoResponse findById(Long id);
    List<TodoResponse> findAll();
}
