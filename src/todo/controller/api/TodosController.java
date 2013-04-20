package todo.controller.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import org.apache.tools.ant.taskdefs.Delete;
import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;
import org.slim3.controller.validator.Validators;

import todo.dao.TodoDao;
import todo.meta.TodoMeta;
import todo.model.Todo;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class TodosController extends Controller {
    
    private static final Logger logger =
            Logger.getLogger(TodosController.class.getName());
    protected TodoDao dao;
    protected TodoMeta m;
    
    Navigation sendJson(String json) throws IOException{
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();
        out.print(json);
        out.flush();
        return null;
    }

//    @Override
//    protected Navigation handleError(Throwable error) throws Throwable{
//        if(error instanceof NoSuchTodoException){
//            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
//        }else{
//            logger.log(Level.SEVERE,error.getMessage(),error);
//            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//        }
//        return null;
//    }
    
    @Override
    public Navigation run() throws Exception {
        
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();
        if (user == null){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return null;
        }
        dao =new TodoDao(user);
        m = TodoMeta.get();
        
        if(isGet()){
            return get();
        }else if(isPost()) {
            return post();
        } else if (isPut()) {
            return put();
        } else if (isDelete()) {
            return delete();
        }else {
            response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        return null;
    }
   }     
        
    Navigation get() throws Exception{
        
        Validators v = new Validators(request);
        v.add("finished", v.required());
        if (!v.validate()){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        
        boolean finished = asBoolean("finished");
        List<Todo> todos = dao.find(finished);
        return sendJson(m.modelsToJson(todos));
    }
    
    Navigation post() throws Exception{
        Validators v = new Validators(request);
        v.add("body", v.required());
        if (!v.validate()){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        
        String body = asString("body");
        Todo todo = dao.create(body);
        return sendJson(m.modelToJson(todo));
    }
    
    Navigation put() throws Exception{
        
        Validators v = new Validators(request);
        v.add("key",v.required());
        v.add("finished",v.required());
        if(!v.validate()){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        
        Key key = KeyFactory.stringToKey(asString("key"));
        boolean finished = asBoolean("finished");
        Todo todo = dao.update(key,finished);
        return sendJson(m.modelToJson(todo));
        }
    Navigation delete() throws Exception{
        
        Validators v = new Validators(request);
        v.add("key",v.required());
        if(!v.validate()){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        
        Key key = KeyFactory.stringToKey(asString("key"));
        dao.delete(key);
        response.setStatus(HttpServletResponse.SC_OK);
        return null;
    }
    
}
