package ru.netology.ci_cd.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class Task {
    private Long id;

    @NotBlank(message = "Название задачи обязательно")
    @Size(min = 1, max = 100, message = "Название должно быть от 1 до 100 символов")
    private String title;

    private boolean completed;
    private LocalDateTime createdAt;

    // Конструктор без параметров (нужен для Spring)
    public Task() {
        this.createdAt = LocalDateTime.now();
        this.completed = false;
    }

    // Конструктор с названием
    public Task(String title) {
        this.title = title;
        this.createdAt = LocalDateTime.now();
        this.completed = false;
    }

    // Геттеры и сеттеры (правый клик → Generate → Getter and Setter)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}