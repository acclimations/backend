package org.acclimations.backend.backend.service;

import org.acclimations.backend.backend.dto.CreateTodoRequest;
import org.acclimations.backend.backend.dto.UpdateTodoRequest;
import org.acclimations.backend.backend.exception.TodoNotFoundException;
import org.acclimations.backend.backend.model.Todo;
import org.acclimations.backend.backend.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private TodoService todoService;

    private Todo createSampleTodo(String id, String title, String description, boolean completed) {
        Todo todo = new Todo();
        todo.setId(id);
        todo.setTitle(title);
        todo.setDescription(description);
        todo.setCompleted(completed);
        todo.setCreatedAt(Instant.now());
        todo.setUpdatedAt(Instant.now());
        return todo;
    }

    @Nested
    @DisplayName("getAllTodos のテスト")
    class GetAllTodosTest {

        @Test
        @DisplayName("全てのTodoを取得できること")
        void shouldReturnAllTodos() {
            // 準備
            List<Todo> expectedTodos = Arrays.asList(
                createSampleTodo("1", "タスク1", "説明1", false),
                createSampleTodo("2", "タスク2", "説明2", true)
            );
            when(todoRepository.findAll()).thenReturn(expectedTodos);

            // 実行
            List<Todo> actualTodos = todoService.getAllTodos();

            // 検証
            assertThat(actualTodos).hasSize(2);
            assertThat(actualTodos).isEqualTo(expectedTodos);
            verify(todoRepository).findAll();
        }
    }

    @Nested
    @DisplayName("getTodoById のテスト")
    class GetTodoByIdTest {

        @Test
        @DisplayName("存在するIDのTodoを取得できること")
        void shouldReturnTodoWhenExists() {
            // 準備
            String id = "1";
            Todo expectedTodo = createSampleTodo(id, "タスク1", "説明1", false);
            when(todoRepository.findById(id)).thenReturn(Optional.of(expectedTodo));

            // 実行
            Todo actualTodo = todoService.getTodoById(id);

            // 検証
            assertThat(actualTodo).isEqualTo(expectedTodo);
            verify(todoRepository).findById(id);
        }

        @Test
        @DisplayName("存在しないIDの場合は例外がスローされること")
        void shouldThrowExceptionWhenNotExists() {
            // 準備
            String id = "999";
            when(todoRepository.findById(id)).thenReturn(Optional.empty());

            // 実行と検証
            assertThatThrownBy(() -> todoService.getTodoById(id))
                .isInstanceOf(TodoNotFoundException.class)
                .hasMessageContaining(id);
            verify(todoRepository).findById(id);
        }
    }

    @Nested
    @DisplayName("createTodo のテスト")
    class CreateTodoTest {

        @Test
        @DisplayName("completedが指定されている場合のTodo作成")
        void shouldCreateTodoWithCompletedStatus() {
            // 準備
            CreateTodoRequest request = new CreateTodoRequest();
            request.setTitle("新しいタスク");
            request.setDescription("説明");
            request.setCompleted(true);

            Todo expectedTodo = createSampleTodo("1", request.getTitle(), request.getDescription(), true);
            when(todoRepository.save(any(Todo.class))).thenReturn(expectedTodo);

            // 実行
            Todo createdTodo = todoService.createTodo(request);

            // 検証
            assertThat(createdTodo.getTitle()).isEqualTo(request.getTitle());
            assertThat(createdTodo.getDescription()).isEqualTo(request.getDescription());
            assertThat(createdTodo.isCompleted()).isTrue();
            verify(todoRepository).save(any(Todo.class));
        }

        @Test
        @DisplayName("completedが指定されていない場合のTodo作成")
        void shouldCreateTodoWithDefaultCompletedStatus() {
            // 準備
            CreateTodoRequest request = new CreateTodoRequest();
            request.setTitle("新しいタスク");
            request.setDescription("説明");
            request.setCompleted(null);

            Todo expectedTodo = createSampleTodo("1", request.getTitle(), request.getDescription(), false);
            when(todoRepository.save(any(Todo.class))).thenReturn(expectedTodo);

            // 実行
            Todo createdTodo = todoService.createTodo(request);

            // 検証
            assertThat(createdTodo.getTitle()).isEqualTo(request.getTitle());
            assertThat(createdTodo.getDescription()).isEqualTo(request.getDescription());
            assertThat(createdTodo.isCompleted()).isFalse();
            verify(todoRepository).save(any(Todo.class));
        }
    }

    @Nested
    @DisplayName("updateTodo のテスト")
    class UpdateTodoTest {

        @Test
        @DisplayName("存在するTodoを更新できること")
        void shouldUpdateExistingTodo() {
            // 準備
            String id = "1";
            Todo existingTodo = createSampleTodo(id, "古いタイトル", "古い説明", false);
            when(todoRepository.findById(id)).thenReturn(Optional.of(existingTodo));

            UpdateTodoRequest request = new UpdateTodoRequest();
            request.setTitle("新しいタイトル");
            request.setDescription("新しい説明");
            request.setCompleted(true);

            Todo updatedTodo = createSampleTodo(id, request.getTitle(), request.getDescription(), true);
            when(todoRepository.save(any(Todo.class))).thenReturn(updatedTodo);

            // 実行
            Todo result = todoService.updateTodo(id, request);

            // 検証
            assertThat(result.getTitle()).isEqualTo(request.getTitle());
            assertThat(result.getDescription()).isEqualTo(request.getDescription());
            assertThat(result.isCompleted()).isTrue();
            verify(todoRepository).findById(id);
            verify(todoRepository).save(any(Todo.class));
        }

        @Test
        @DisplayName("存在しないTodoの更新時は例外がスローされること")
        void shouldThrowExceptionWhenUpdatingNonExistentTodo() {
            // 準備
            String id = "999";
            when(todoRepository.findById(id)).thenReturn(Optional.empty());

            UpdateTodoRequest request = new UpdateTodoRequest();
            request.setTitle("新しいタイトル");
            request.setDescription("新しい説明");
            request.setCompleted(true);

            // 実行と検証
            assertThatThrownBy(() -> todoService.updateTodo(id, request))
                .isInstanceOf(TodoNotFoundException.class)
                .hasMessageContaining(id);
            verify(todoRepository).findById(id);
            verify(todoRepository, never()).save(any(Todo.class));
        }
    }

    @Nested
    @DisplayName("deleteTodo のテスト")
    class DeleteTodoTest {

        @Test
        @DisplayName("存在するTodoを削除できること")
        void shouldDeleteExistingTodo() {
            // 準備
            String id = "1";
            when(todoRepository.existsById(id)).thenReturn(true);

            // 実行
            todoService.deleteTodo(id);

            // 検証
            verify(todoRepository).existsById(id);
            verify(todoRepository).deleteById(id);
        }

        @Test
        @DisplayName("存在しないTodoの削除時は例外がスローされること")
        void shouldThrowExceptionWhenDeletingNonExistentTodo() {
            // 準備
            String id = "999";
            when(todoRepository.existsById(id)).thenReturn(false);

            // 実行と検証
            assertThatThrownBy(() -> todoService.deleteTodo(id))
                .isInstanceOf(TodoNotFoundException.class)
                .hasMessageContaining(id);
            verify(todoRepository).existsById(id);
            verify(todoRepository, never()).deleteById(any());
        }
    }
}
