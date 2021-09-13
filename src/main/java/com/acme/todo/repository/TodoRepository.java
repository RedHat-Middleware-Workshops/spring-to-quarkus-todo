package com.acme.todo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.acme.todo.domain.TodoEntity;

@Repository  
public interface TodoRepository extends JpaRepository<TodoEntity, Long> {}
