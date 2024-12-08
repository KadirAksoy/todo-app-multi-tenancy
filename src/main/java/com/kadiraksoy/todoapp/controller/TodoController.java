package com.kadiraksoy.todoapp.controller;

import com.kadiraksoy.todoapp.dto.TodoDto;
import com.kadiraksoy.todoapp.dto.response.TodoResponse;
import com.kadiraksoy.todoapp.service.ITodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
public class TodoController {

    private final ITodoService todoService;

    @PostMapping
    public ResponseEntity<TodoResponse> createTodo(@RequestBody TodoDto todoDto) {
        TodoResponse createdTodo = todoService.create(todoDto);
        return new ResponseEntity<>(createdTodo, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TodoResponse> updateTodo(@PathVariable Long id, @RequestBody TodoDto todoDto) {
        TodoResponse updatedTodo = todoService.update(id, todoDto);
        if (updatedTodo != null) {
            return ResponseEntity.ok(updatedTodo);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
        todoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TodoResponse> getTodoById(@PathVariable Long id) {
        TodoResponse todo = todoService.findById(id);
        if (todo != null) {
            return ResponseEntity.ok(todo);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<TodoResponse>> getAllTodos() {
        List<TodoResponse> todos = todoService.findAll();
        return ResponseEntity.ok(todos);
    }
}

