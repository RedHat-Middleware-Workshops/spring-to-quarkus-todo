package com.acme.todo.rest;

import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.acme.todo.domain.TodoEntity;
import com.acme.todo.repository.TodoRepository;

@RestController
@RequestMapping("/todo")
public class TodoController {
    private final TodoRepository todoRepository;

    public TodoController(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @GetMapping
    public List<TodoEntity> findAll() {
        return this.todoRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }
 
    @GetMapping("/{id}")
    public TodoEntity findById(@PathVariable("id") Long id) {
        return this.todoRepository.findById(id).get();
    }

    @PutMapping
    @Transactional
    public void update(@RequestBody TodoEntity resource) {
        this.todoRepository.save(resource);
    }
 
    @PostMapping
    @Transactional
    public TodoEntity create(@RequestBody TodoEntity resource) {
        return this.todoRepository.save(resource);
    }
 
    @DeleteMapping("/{id}")
    @Transactional
    public void delete(@PathVariable("id") Long id) {
        this.todoRepository.deleteById(id);
    }
}
