package hyeyoung.todolist.dto;

import java.util.Comparator;

// priority를 기준으로 정렬하기 위한 Comparator 구현 클래스
public class PrioritySort implements Comparator<Todo> {


    @Override
    public int compare(Todo o1, Todo o2) {
        if (o1.getPriority().getImportance() > o2.getPriority().getImportance()) {
            return 1;
        } else if (o1.getPriority().getImportance() < o2.getPriority().getImportance()) {
            return -1;
        } else {
            if (o1.getPriority().getOrder() > o2.getPriority().getOrder()) {
                return 1;
            } else {
                return -1;
            }
        }
    }
}
