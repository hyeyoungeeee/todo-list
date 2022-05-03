package hyeyoung.todolist.service;

import hyeyoung.todolist.dao.TodoRepository;
import hyeyoung.todolist.dao.TodoRepositoryImpl;
import hyeyoung.todolist.dto.Todo;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TodoServiceImplTest {

    private TodoServiceImpl todoService;
    private TodoRepositoryImpl todoRepository;

    @BeforeEach
    void beforEach(){
        todoRepository = new TodoRepositoryImpl();
        todoService = new TodoServiceImpl(todoRepository);
    }

    @AfterEach
    void afterEach() {
        todoRepository.deleteAll();
    }

    @Test
    void addNewTodo() {
        // given
        Todo todo = new Todo();
        todo.setTask("task1");
        todo.setDate(LocalDate.now());

        Todo todo2 = new Todo();
        todo2.setTask("task2");
        todo2.setDate(LocalDate.now());

        // when
        Todo result = todoService.addNewTodo(todo);
        Todo result2 = todoService.addNewTodo(todo2);

        List<Todo> resultList = new ArrayList<>();
        resultList.add(result);
        resultList.add(result2);

        // then
        List<Todo> todos = todoRepository.findByDate(LocalDate.now()).get();
        Assertions.assertThat(resultList).isEqualTo(todos);

    }
}
