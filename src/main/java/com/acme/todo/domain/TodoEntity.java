package com.acme.todo.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
  
@Entity
@Table(name = "todo")
public class TodoEntity {  
    @Id
    @GeneratedValue
    private Long id;  
    private String title;  
    private boolean completed = false;

    public TodoEntity() {
    }

    public TodoEntity(Long id, String title, boolean completed) {
        this.id = id;
        this.title = title;
        this.completed = completed;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean getCompleted() { 
        return this.completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
