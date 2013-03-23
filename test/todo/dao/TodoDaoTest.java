package todo.dao;

import java.util.Date;
import java.util.List;

import org.slim3.datastore.Datastore;
import org.slim3.tester.AppEngineTestCase;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.users.User;

import todo.model.Todo;
import todo.test.TestUtil;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class TodoDaoTest extends AppEngineTestCase {

    private TodoDao dao=new TodoDao(new User(
        "test@example.com",
        "test",
        "user01"));
    
   private Key key;
   private Key othersKey;
   
   @Before
   public void setUp() throws Exception{
       
       super.setUp() ;
       
       key=Datastore.put(TestUtil.newTodo("user01","todo1",false));
       Datastore.put(TestUtil.newTodo("user01","todo2",false));
       Datastore.put(TestUtil.newTodo("user01","todo3",true));
       Datastore.put(TestUtil.newTodo("user01","todo4",true));
       
       othersKey=Datastore.put(TestUtil.newTodo("user02","todo5",false));
       Datastore.put(TestUtil.newTodo("user02","todo6",true));
   }

   @Test
   public void create(){
       
       int before=tester.count(Todo.class);
       
       Todo todo=dao.create("ƒ[ƒ‹‘‚­");
       assertThat(todo,is(notNullValue()));
       assertThat(todo.getBody(),is("ƒ[ƒ‹‘‚­"));
       assertThat(todo.isFinished(),is(false));
       assertThat(todo.getCreatedAt(),is(notNullValue()));
       assertThat(todo.getFinishedAt(),is(nullValue()));
       
       assertThat(tester.count(Todo.class),is(before+1));
       
       todo=Datastore.get(Todo.class,todo.getKey());
       assertThat(todo.getBody(),is("ƒ[ƒ‹‘‚­"));
       assertThat(todo.isFinished(),is(false));
       assertThat(todo.getCreatedAt(),is(notNullValue()));
       assertThat(todo.getFinishedAt(),is(nullValue()));
   }
   
   @Test(expected=NullPointerException.class)
   public void create_body‚ªnull(){
       dao.create(null);
   }
   
   @Test
   public void find(){
   
       List<Todo>todos=dao.find(false);
       assertThat(todos.size(),is(2));
       assertThat(todos.get(0).isFinished(),is(false));
       assertThat(todos.get(0).getUserId(),is("user01"));
       assertThat(todos.get(1).isFinished(),is(false));
       assertThat(todos.get(1).getUserId(),is("user01"));
       Date createdAt0=todos.get(0).getCreatedAt();
       Date createdAt1=todos.get(1).getCreatedAt();
       assertThat(createdAt0.compareTo(createdAt1)>=0,is(true));
       
       todos=dao.find(true);
       assertThat(todos.size(),is(2));
       assertThat(todos.get(0).isFinished(),is(true));
       assertThat(todos.get(0).getUserId(),is("user01"));
       assertThat(todos.get(1).isFinished(),is(true));
       assertThat(todos.get(1).getUserId(),is("user01"));
       Date finishedAt0=todos.get(0).getFinishedAt();
       Date finishedAt1=todos.get(1).getFinishedAt();
       assertThat(createdAt0.compareTo(createdAt1)>=0,is(true));
    }

}
