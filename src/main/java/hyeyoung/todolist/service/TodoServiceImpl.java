package hyeyoung.todolist.service;

import hyeyoung.todolist.dao.TodoRepository;
import hyeyoung.todolist.dto.Priority;
import hyeyoung.todolist.dto.PrioritySort;
import hyeyoung.todolist.dto.Todo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;

    @Override
    public Todo addNewTodo(Todo todo) {
        todo.setPriority(new Priority('B', 0));

        Todo saved = todoRepository.save(todo);

        todoRepository.findByDate(todo.getDate())
                .ifPresent(todos -> {
                    // 최하순위 바로 아래 순위 부여
                    checkDuplicatedPriority(todos, saved);
                });
        return todo;
    }

    @Override
    public Optional<List<Todo>> getTodoList(LocalDate date) {
        List<Todo> sort;

        if (todoRepository.findByDate(date).isPresent()) {
            sort = sortPriority(todoRepository.findByDate(date).get());
        } else {
            throw new IllegalStateException();
        }

        return Optional.ofNullable(sort);
    }

    @Override
    public Todo updateTodo(Todo todo) {
        Todo target = todoRepository.findById(todo.getId()).orElseThrow(() -> {
            throw new IllegalStateException();
        });

        todoRepository.update(todo);

        // 우선순위를 수정한 경우
        if ((target.getPriority().getImportance() != todo.getPriority().getImportance())
                || (target.getPriority().getOrder() != todo.getPriority().getOrder())) {

            todoRepository.findByDate(todo.getDate()).ifPresent(todos -> {
                adjustTodoList(todos, todo);
            });
        }
        return todo;
    }

    @Override
    public Todo deleteTodo(long id) {
        Todo todo = todoRepository.findById(id).orElseThrow(() -> {
            throw new IllegalStateException();
        });

        todoRepository.findByDate(todo.getDate()).ifPresent(todos -> {
            Collections.sort(todos, new PrioritySort());
            pullForward(todos, id);
        });

        todoRepository.delete(id);
        return todo;
    }

    private List<Todo> sortPriority(List<Todo> todos) {
        List<Todo> sorted = new ArrayList<>();
        Collections.sort(todos, new PrioritySort());
        for (int i = todos.size() - 1; i >= 0; i--) {
            if (todos.get(i).getPriority().getImportance() == 'S') {
                sorted.add(todos.get(i));
                todos.remove(i);
            } else {
                break;
            }
        }
        Collections.sort(sorted, new PrioritySort());

        for (Todo t : todos) {
            sorted.add(t);
        }
        return sorted;
    }

    private void checkDuplicatedPriority(List<Todo> todos, Todo now) {
        if (todos.size() < 2) {
            return;
        }
        List<Todo> sorted = sortPriority(todos);

        int lastIndex = sorted.size() - 1;
        Todo last = sorted.get(lastIndex);
        now.setPriority(new Priority(last.getPriority().getImportance()
                , last.getPriority().getOrder() + 1));
        todoRepository.update(now);

    }

    private void adjustTodoList(List<Todo> todos, Todo change) {
        // 1. 같은 우선순위인 경우 : 끼워넣고 나머지 뒤로 민다
        // 2. 알파벳 맨 끝에 끼는 경우 (새로운 알파벳인데 0인것 포함)
        // 3. 같은 알파벳인데 순서 누락인 경우
        // 4. 새로운 알파벳인데 순서 누락인 경우

        if (todos.size() < 2) {
            Todo first = todos.get(0);
            if (first.getPriority().getOrder() != 0) {
                first.setPriority(new Priority(first.getPriority().getImportance(), 0));
                todoRepository.update(first);
            }
            return;
        }
        List<Todo> sorted = sortPriority(todos);

        char targetImportance = change.getPriority().getImportance();

        for (int i = 0; i < sorted.size() - 1; i++) {
            Todo now = sorted.get(i); // 이게 changed
            Todo next = sorted.get(i + 1);

            if (now.getPriority().getImportance() == next.getPriority().getImportance()) {
                // 1. 같은 우선순위인 경우 : 끼워넣고 나머지 뒤로 민다
                if (now.getPriority().getOrder() == next.getPriority().getOrder()) {
                    int targetIndex = now.getId() == change.getId() ? i : i + 1;
                    pushBack(sorted, targetIndex + 1);
                }
                // 3. 같은 알파벳인데 순서 누락인 경우
                if (now.getPriority().getOrder() + 1 != next.getPriority().getOrder()) {
                    next.setPriority(new Priority(now.getPriority().getImportance()
                            , now.getPriority().getOrder() + 1));
                    todoRepository.update(next);
                }
            } else {
                // 4. 다른 중요도인데 순서 누락인 경우
                if (now.getPriority().getImportance() == targetImportance) {
                    if (i == 0 && now.getPriority().getOrder() != 0) {
                        now.setPriority(new Priority(targetImportance, 0));
                        todoRepository.update(now);
                    } else if (i > 0 && now.getPriority().getOrder() - 1 != sorted.get(i - 1).getPriority().getOrder()) {
                        now.setPriority(new Priority(targetImportance, sorted.get(i - 1).getPriority().getOrder() + 1));
                        todoRepository.update(now);
                    }

                } else if (next.getPriority().getImportance() == targetImportance) {
                    if (next.getPriority().getOrder() != 0) {
                        next.setPriority(new Priority(targetImportance, 0));
                        todoRepository.update(next);
                    }
                }

            }
        }

    }

    private void pushBack(List<Todo> sorted, int startIndex) {
        char targetImportance = sorted.get(startIndex).getPriority().getImportance();

        for (int i = startIndex; i < sorted.size(); i++) {
            Todo now = sorted.get(i);
            if (now.getPriority().getImportance() != targetImportance) {
                break;
            }
            now.setPriority(new Priority(targetImportance, now.getPriority().getOrder() + 1));
            todoRepository.update(now);
        }
    }
    private void pullForward(List<Todo> sorted, long targetId) {
        int targetIndex = -1;
        char targetImportance = 0;
        for (int i = 0; i < sorted.size(); i++) {
            if (sorted.get(i).getId() == targetId) {
                targetIndex = i;
                targetImportance = sorted.get(i).getPriority().getImportance();
                break;
            }
        }
        for (int i = targetIndex+1; i < sorted.size(); i++) {
            Todo now = sorted.get(i);
            if (now.getPriority().getImportance() != targetImportance) {
                break;
            }
            now.setPriority(new Priority(targetImportance, now.getPriority().getOrder() - 1));
            todoRepository.update(now);
        }

    }
}
