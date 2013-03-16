package todo.test;

import java.util.Date;

import todo.model.Todo;

public class TestUtil {
    
    public static Todo newTodo(String userId,String body,boolean finished){
        Todo todo=new Todo();
        todo.setUserId(userId);
        todo.setBody(body);
        todo.setCreatedAt(new Date());
        todo.setfinished(finished);
        todo.setFinishedAt(finished ? new Date():null);
        return todo;
    }

}
