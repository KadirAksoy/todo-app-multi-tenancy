package com.kadiraksoy.todoapp.repository;

import com.kadiraksoy.todoapp.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITodoRepository extends JpaRepository<Todo, Long> {
}
