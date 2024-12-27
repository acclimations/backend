package org.acclimations.backend.backend.repository;

import org.acclimations.backend.backend.model.Todo;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;

@Repository
public class TodoRepository {
    private final Map<String, Todo> todos = new ConcurrentHashMap<>();

    public List<Todo> findAll() {
        return new ArrayList<>(todos.values());
    }

    public Optional<Todo> findById(String id) {
        return Optional.ofNullable(todos.get(id));
    }

    public Todo save(Todo todo) {
        if (todo.getId() == null) {
            todo.setId(UUID.randomUUID().toString());
        }
        todos.put(todo.getId(), todo);
        return todo;
    }

    public void deleteById(String id) {
        todos.remove(id);
    }

    public boolean existsById(String id) {
        return todos.containsKey(id);
    }
}
