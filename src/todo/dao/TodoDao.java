package todo.dao;

import java.util.Date;
import java.util.List;

import org.slim3.datastore.DaoBase;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.users.User;

import todo.NoSuchTodoException;
import todo.meta.TodoMeta;
import todo.model.Todo;

public class TodoDao extends DaoBase<Todo>{
    
    private User user;
    
    public TodoDao(User user){
        this.user=user;
    }

    
    public Todo create(String body){
        if(body ==null){
            throw new NullPointerException();
        }
        Todo todo=new Todo();
        todo.setUserId(user.getUserId());
        todo.setBody(body);
        put(todo);
        return todo;
    }
    
    public List<Todo>find(boolean finished){
        TodoMeta m=TodoMeta.get();
        return query()
                .filter(
                    m.userId.equal(user.getUserId()),
                    m.finished.equal(finished))
                .sort(finished ? m.finishedAt.desc : m.createdAt.desc)
                .asList();
    }
    public Todo update(Key key, boolean finished){
        Todo todo = getOrNull(key);
        if (todo==null || !todo.getUserId().equals(user.getUserId())){
            throw new NoSuchTodoException(key.getId());
        }
        todo.setFinished(finished);
        todo.setFinishedAt(finished ? new Date() : null);
        put(todo);
        return todo;       
    }
    
    public void delete(Key key){
        Todo todo = getOrNull(key);
        if (todo == null || !todo.getUserId().equals(user.getUserId())){
            throw new NoSuchTodoException(key.getId());
        }
        super.delete(key);
    }
}