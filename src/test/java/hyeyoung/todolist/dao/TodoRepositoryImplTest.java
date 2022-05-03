package hyeyoung.todolist.dao;

import hyeyoung.todolist.dto.Todo;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class TodoRepositoryImplTest {
    TodoRepository todoRepository = new TodoRepositoryImpl();

    @Test
    public void save(){
        Todo todo = new Todo();
        todo.setDate(LocalDate.now());
        todo.setTask("task1");
        todoRepository.save(todo);
        Todo result = todoRepository.findById(todo.getId()).get();
//        assertThat(todo).isEqualTo(result);
        assertThat(todo.getPriority()).isEqualTo(null);
    }

    @Test
    public void findByDate(){
        Todo todo1 = new Todo();
        todo1.setDate(LocalDate.now());
        todo1.setTask("task1");
        todoRepository.save(todo1);

        Todo todo2 = new Todo();
        todo2.setDate(LocalDate.now());
        todo2.setTask("task2");
        todoRepository.save(todo2);

        List<Todo> now = new ArrayList<>();
        now.add(todo1);
        now.add(todo2);

        Todo todo3 = new Todo();
        todo3.setDate(LocalDate.of(2021, 3, 31));
        todo3.setTask("task3");
        todoRepository.save(todo3);

        Todo todo4 = new Todo();
        todo4.setDate(LocalDate.of(2021, 3, 31));
        todo4.setTask("task4");
        todoRepository.save(todo4);


        List<Todo> result = todoRepository.findByDate(LocalDate.of(2021, 3, 31)).get();
        assertThat(result.size()).isEqualTo(2);

    }

    @Test
    void update() {
        Todo todo = new Todo();
        todo.setDate(LocalDate.now());
        todo.setTask("task1");
        todoRepository.save(todo);

        Todo todo2 = new Todo();
        todo2.setDate(LocalDate.now());
        todo2.setTask("task2");
        todoRepository.save(todo2);

        Todo todo3 = new Todo();
        todo3.setId(todo.getId());
        todo3.setDate(LocalDate.now());
        todo3.setTask("task3");

        todoRepository.update(todo3);

        assertThat(todo3).isEqualTo(todoRepository.findById(todo3.getId()).get());
    }
}
