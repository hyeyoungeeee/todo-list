package hyeyoung.todolist.dao;
import hyeyoung.todolist.dto.Todo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TodoRepository {
    Todo save(Todo todo);
    Optional<List<Todo>> findByDate(LocalDate date);
    Optional<Todo> findById(long id);
    Todo update(Todo todo);
    void delete(long id);
    void deleteAll();

}
