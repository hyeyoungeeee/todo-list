package hyeyoung.todolist.dao;

import hyeyoung.todolist.dto.Todo;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Repository
public class TodoRepositoryImpl implements TodoRepository {
    private HashMap<Long, Todo> todoList = new HashMap<>();
    private static long id = 0L;

    @Override
    public Todo save(Todo todo) {
        todo.setId(++id);
        todo.setStatus("진행중");
        todoList.put(todo.getId(), todo);
        return todo;
    }

    @Override
    public Optional<List<Todo>> findByDate(LocalDate date) {
        List<Todo> list = new ArrayList<>();

        todoList.forEach((key, value) -> {
            if (value.getDate().equals(date)) list.add(value);
        });
        return Optional.ofNullable(list);
    }

    @Override
    public Optional<Todo> findById(long id) {
        return Optional.ofNullable(todoList.get(id));
    }


    @Override
    public Todo update(Todo todo) {
        todoList.put(todo.getId(), todo);
        return todo;
    }

    @Override
    public void delete(long id) {
        todoList.remove(id);
    }

    @Override
    public void deleteAll() {
        todoList.clear();
    }


}
