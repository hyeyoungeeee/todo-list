package hyeyoung.todolist.service;

import hyeyoung.todolist.dto.Todo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TodoService {
    Todo addNewTodo(Todo todo);
    Optional<List<Todo>> getTodoList(LocalDate date);
    Todo updateTodo(Todo todo);
    Todo deleteTodo(long id);
}
