package com.kadiraksoy.todoapp.dto;

import com.kadiraksoy.todoapp.entity.Todo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TodoResponse {

    private Long id;
    private String title;
    private String description;
    private boolean completed;
    private Long userId;

    public static TodoResponse toTodoResponse(Todo todo) {
        TodoResponse todoResponse = new TodoResponse();
        todoResponse.setId(todo.getId());
        todoResponse.setTitle(todo.getTitle());
        todoResponse.setDescription(todo.getDescription());
        todoResponse.setCompleted(todo.isCompleted());
        todoResponse.setUserId(todo.getUser().getId());
        return todoResponse;
    }
}
