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
public class TodoDto {

    private String title;
    private String description;
    private boolean completed;
    private Long userId;

    {
        setCompleted(false);
    }

    public static Todo toTodo(TodoDto todoDto) {
        Todo todo = new Todo();
        todo.setTitle(todoDto.getTitle());
        todo.setDescription(todoDto.getDescription());
        todo.setCompleted(todoDto.isCompleted());
        return todo;
    }
}
