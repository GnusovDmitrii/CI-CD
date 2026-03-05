package ru.netology.ci_cd.controller;

import jakarta.validation.Valid;
import ru.netology.ci_cd.model.Task;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@RestController  // Говорит Spring, что это REST контроллер
@RequestMapping("/tasks")  // Все URL в этом контроллере начинаются с /tasks
public class TaskController {

    // Хранилище задач в памяти (потокобезопасное)
    private final ConcurrentHashMap<Long, Task> tasks = new ConcurrentHashMap<>();

    // Генератор ID (потокобезопасный)
    private final AtomicLong idGenerator = new AtomicLong(1);

    // POST /tasks - создать новую задачу
    @PostMapping
    public ResponseEntity<Task> createTask(@Valid @RequestBody Task task) {
        // Генерируем новый ID
        long id = idGenerator.getAndIncrement();
        task.setId(id);

        // Сохраняем задачу
        tasks.put(id, task);

        // Возвращаем созданную задачу с кодом 201 (Created)
        return new ResponseEntity<>(task, HttpStatus.CREATED);
    }

    // GET /tasks - получить все задачи
    @GetMapping
    public List<Task> getAllTasks() {
        // Возвращаем список всех задач
        return new ArrayList<>(tasks.values());
    }

    // GET /tasks/{id} - получить задачу по ID
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        Task task = tasks.get(id);
        if (task == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(task);
    }

    // PUT /tasks/{id} - обновить задачу
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody Task task) {

        // Проверяем, существует ли задача
        if (!tasks.containsKey(id)) {
            return ResponseEntity.notFound().build();
        }

        // Обновляем задачу
        task.setId(id);
        tasks.put(id, task);

        return ResponseEntity.ok(task);
    }

    // DELETE /tasks/{id} - удалить задачу
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        // Пытаемся удалить задачу
        Task removedTask = tasks.remove(id);

        // Если задачи не было, возвращаем 404
        if (removedTask == null) {
            return ResponseEntity.notFound().build();
        }

        // Возвращаем 204 (No Content) при успешном удалении
        return ResponseEntity.noContent().build();
    }
}