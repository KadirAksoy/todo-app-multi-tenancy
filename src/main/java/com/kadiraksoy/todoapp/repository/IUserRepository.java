package com.kadiraksoy.todoapp.repository;

import com.kadiraksoy.todoapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUserRepository extends JpaRepository<User, Long> {
}
