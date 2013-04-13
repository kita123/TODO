package todo.test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slim3.tester.TestEnvironment;

import com.google.apphosting.api.ApiProxy;

import todo.model.Todo;

public class TestUtil {
    
    
    
    public static void login(String userId,String email){
        
        Map<String,Object>attrs=new HashMap<String,Object>();
        attrs.put(
            "com.google.appengine.api.users.UserService.user_id_key",
            userId);
        
        TestEnvironment env=
                (TestEnvironment)ApiProxy.getCurrentEnvironment();
        env.setEmail(email);
        env.setAttributes(attrs);
    }
    
    public static Todo newTodo(String userId,String body,boolean finished){
        Todo todo=new Todo();
        todo.setUserId(userId);
        todo.setBody(body);
        todo.setCreatedAt(new Date());
        todo.setFinished(finished);
        todo.setFinishedAt(finished ? new Date():null);
        try {
            Thread.sleep(10);
        } catch (InterruptedException ignore) {
        }
        return todo;
    }

}
