package hyeyoung.todolist.controller;

import hyeyoung.todolist.dto.Priority;
import hyeyoung.todolist.dto.Todo;
import hyeyoung.todolist.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class TodoRestController {

    private final TodoService todoService;

    //params: date, task, description(optional)
    @PostMapping("/add-todo")
    public Todo makeTodo(@RequestBody Map<String, String> map) {
        if (map.get("date") == null || map.get("task") == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "date 또는 task 값이 없습니다.");
        }

        Todo todo = new Todo();
        todo.setDate(LocalDate.parse(map.get("date"), DateTimeFormatter.ISO_DATE));
        todo.setTask(map.get("task"));
        String description = map.get("description");
        if (description != null) {
            todo.setDescription(description);
        }
        todoService.addNewTodo(todo);
        return todo;
    }

    // 특정 날짜의 모든 todo들을 우선순위로 정렬
    // params: date
    @GetMapping("/get")
    public ResponseEntity<Iterable<Todo>> getTodo(@RequestBody Map<String, String> map) {
        String date = map.get("date");
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);

        List<Todo> todos = todoService.getTodoList(localDate).orElseThrow(() -> {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "date 또는 task 값이 없습니다.");
        });

        return new ResponseEntity<>(todos, HttpStatus.OK);
    }


    // 특정 일정 변경
    // params: id, date, importance, order, task, description, status
    @PostMapping("/change-todo")
    public Todo updateTodo(@RequestBody Map<String, String> map) {
        Todo todo = new Todo();
        if (map.get("id") == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "id 값이 없습니다.");
        }
        int id = Integer.parseInt(map.get("id"));
        LocalDate date = LocalDate.parse(map.get("date"), DateTimeFormatter.ISO_DATE);
        Priority priority = new Priority(map.get("importance").charAt(0), Integer.parseInt(map.get("order")));

        todo.setId(id);
        todo.setDate(date);
        todo.setPriority(priority);
        todo.setTask(map.get("task"));
        todo.setDescription(map.get("description"));
        todo.setStatus(map.get("status"));

        Todo updatedTodo = todoService.updateTodo(todo);
        return updatedTodo;
    }


    // 삭제
    // params: id
    @PostMapping("/delete-todo")
    public Todo deleteTodo(@RequestBody Map<String, String> map){
        if (map.get("id") == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "id 값이 없습니다.");
        }
        long id = Long.parseLong(map.get("id"));

        Todo deletedTodo = todoService.deleteTodo(id);
        return deletedTodo;
    }
}
