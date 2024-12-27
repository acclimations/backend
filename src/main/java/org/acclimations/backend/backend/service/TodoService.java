package org.acclimations.backend.backend.service;

import org.acclimations.backend.backend.dto.CreateTodoRequest;
import org.acclimations.backend.backend.dto.UpdateTodoRequest;
import org.acclimations.backend.backend.exception.TodoNotFoundException;
import org.acclimations.backend.backend.model.Todo;
import org.acclimations.backend.backend.repository.TodoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TodoService {
    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public List<Todo> getAllTodos() {
        return todoRepository.findAll();
    }

    public Todo getTodoById(String id) {
        return todoRepository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(id));
    }

    public Todo createTodo(CreateTodoRequest request) {
        Todo todo = new Todo();
        todo.setTitle(request.getTitle());
        todo.setDescription(request.getDescription());
        todo.setCompleted(request.getCompleted() != null ? request.getCompleted() : false);
        
        return todoRepository.save(todo);
    }

    public Todo updateTodo(String id, UpdateTodoRequest request) {
        Todo todo = getTodoById(id);
        
        todo.setTitle(request.getTitle());
        todo.setDescription(request.getDescription());
        todo.setCompleted(request.getCompleted());
        
        return todoRepository.save(todo);
    }

    public void deleteTodo(String id) {
        if (!todoRepository.existsById(id)) {
            throw new TodoNotFoundException(id);
        }
        todoRepository.deleteById(id);
    }
}
