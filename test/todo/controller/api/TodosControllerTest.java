package todo.controller.api;

import org.slim3.datastore.Datastore;
import org.slim3.tester.ControllerTestCase;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import todo.meta.TodoMeta;
import todo.model.Todo;
import todo.test.TestUtil;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class TodosControllerTest extends ControllerTestCase {

    private TodoMeta m = TodoMeta.get();
    private Key unfinishedKey;
    private Key finishedKey;
    private Key othersKey;

    @Before
    public void setUp() throws Exception {

        super.setUp();

        unfinishedKey =
            Datastore.put(TestUtil.newTodo("user01", "todo1", false));
        Datastore.put(TestUtil.newTodo("user01", "todo2", false));
        finishedKey = Datastore.put(TestUtil.newTodo("user01", "todo3", true));
        Datastore.put(TestUtil.newTodo("user01", "todo4", true));

        othersKey = Datastore.put(TestUtil.newTodo("user02", "todo5", false));
        Datastore.put(TestUtil.newTodo("user02", "todo6", true));

    }

    @Test
    public void post() throws Exception {

        int before = tester.count(Todo.class);

        TestUtil.login("user01", "test@example.com");
        tester.request.setMethod("POST");
        tester.param("body", "何かする");
        tester.start("/api/todos");

        assertThat(tester.response.getStatus(), is(200));

        Todo todo = m.jsonToModel(tester.response.getOutputAsString());
        assertThat(todo.getKey(), is(notNullValue()));
        assertThat(todo.getUserId(), is("user01"));
        assertThat(todo.getBody(), is("何かする"));
        assertThat(todo.isFinished(), is(false));
        assertThat(todo.getCreatedAt(), is(notNullValue()));
        assertThat(todo.getFinishedAt(), is(nullValue()));

        assertThat(tester.count(Todo.class), is(before + 1));

        todo = Datastore.getOrNull(Todo.class, todo.getKey());
        assertThat(todo, is(notNullValue()));
        assertThat(todo.getUserId(), is("user01"));
        assertThat(todo.getBody(), is("何かする"));
        assertThat(todo.isFinished(), is(false));
        assertThat(todo.getCreatedAt(), is(notNullValue()));
        assertThat(todo.getFinishedAt(), is(nullValue()));

    }

    @Test
    public void post_bodyパラメータがない() throws Exception {
        TestUtil.login("user01", "test@example.com");
        tester.request.setMethod("POST");
        tester.start("/api/todos");
        assertThat(tester.response.getStatus(), is(400));
    }

    @Test
    public void post_ログインしていない() throws Exception {
        tester.request.setMethod("POST");
        tester.param("body", "何かする");
        tester.start("/api/todos");
        assertThat(tester.response.getStatus(), is(401));
    }

    @Test
    public void get_未完了TODO一覧() throws Exception {
        TestUtil.login("user01", "test@example.com");
        tester.request.setMethod("GET");
        tester.param("finished", "false");
        tester.start("/api/todos");

        assertThat(tester.response.getStatus(), is(200));

        Todo[] todos = m.jsonToModels(tester.response.getOutputAsString());
        assertThat(todos.length, is(2));
        assertThat(todos[0].getUserId(), is("user01"));
        assertThat(todos[0].getBody(), is("todo2"));
        assertThat(todos[0].isFinished(), is(false));
        assertThat(todos[1].getUserId(), is("user01"));
        assertThat(todos[1].getBody(), is("todo1"));
        assertThat(todos[1].isFinished(), is(false));
    }

    @Test
    public void get_完了済TODO一覧() throws Exception {
        TestUtil.login("user01", "test@example.com");
        tester.request.setMethod("GET");
        tester.param("finished", "true");
        tester.start("/api/todos");

        assertThat(tester.response.getStatus(), is(200));

        Todo[] todos = m.jsonToModels(tester.response.getOutputAsString());
        assertThat(todos.length, is(2));
        assertThat(todos[0].getUserId(), is("user01"));
        assertThat(todos[0].getBody(), is("todo4"));
        assertThat(todos[0].isFinished(), is(true));
        assertThat(todos[1].getUserId(), is("user01"));
        assertThat(todos[1].getBody(), is("todo3"));
        assertThat(todos[1].isFinished(), is(true));
    }

    @Test
    public void get_finishedパラメータがない() throws Exception {

        TestUtil.login("user01", "test@example.com");
        tester.request.setMethod("GET");
        tester.start("/api/todos");

        assertThat(tester.response.getStatus(), is(400));
    }

    @Test
    public void get_ログインしていない() throws Exception {

        tester.request.setMethod("GET");
        tester.param("finished", "false");
        tester.start("/api/todos");

        assertThat(tester.response.getStatus(), is(401));
    }

    @Test
    public void put_未完了から完了済みへ() throws Exception {

        TestUtil.login("user01", "test@example.com");
        tester.request.setMethod("PUT");
        tester.param("finished", "true");
        tester.start("/api/todos/" + KeyFactory.keyToString(unfinishedKey));

        assertThat(tester.response.getStatus(), is(200));

        Todo todo = m.jsonToModel(tester.response.getOutputAsString());
        assertThat(todo.getKey(), is(unfinishedKey));
        assertThat(todo.isFinished(), is(true));
        assertThat(todo.getFinishedAt(), is(notNullValue()));

        todo = Datastore.getOrNull(Todo.class, unfinishedKey);
        assertThat(todo, is(notNullValue()));
        assertThat(todo.isFinished(), is(true));
        assertThat(todo.getFinishedAt(), is(notNullValue()));

    }

    @Test
    public void put_完了済から未完了へ() throws Exception {

        TestUtil.login("user01", "test@example.com");
        tester.request.setMethod("PUT");
        tester.param("finished", "false");
        tester.start("/api/todos/" + KeyFactory.keyToString(finishedKey));

        assertThat(tester.response.getStatus(), is(200));

        Todo todo = m.jsonToModel(tester.response.getOutputAsString());
        assertThat(todo.getKey(), is(finishedKey));
        assertThat(todo.isFinished(), is(false));
        assertThat(todo.getFinishedAt(), is(nullValue()));

        todo = Datastore.getOrNull(Todo.class, finishedKey);
        assertThat(todo, is(notNullValue()));
        assertThat(todo.isFinished(), is(false));
        assertThat(todo.getFinishedAt(), is(nullValue()));
    }

    @Test
    public void put_finishedパラメータがない() throws Exception {
        TestUtil.login("user01", "test@example.com");
        tester.request.setMethod("PUT");
        tester.start("/api/todos/" + KeyFactory.keyToString(unfinishedKey));

        assertThat(tester.response.getStatus(), is(400));
    }

    @Test
    public void put_ログインしていない() throws Exception {
        tester.request.setMethod("PUT");
        tester.param("finished", "true");
        tester.start("/api/todos/" + unfinishedKey);
        assertThat(tester.response.getStatus(), is(401));
    }

    @Test
    public void put_指定したキーのTODOが存在しない() throws Exception {
        Key key = Datastore.createKey(Todo.class, Long.MAX_VALUE);
        tester.request.setMethod("PUT");
        tester.param("finished", "true");
        tester.start("/api/todos/" + KeyFactory.keyToString(key));
        assertThat(tester.response.getStatus(), is(404));
    }

    @Test
    public void put_別ユーザーが登録したTODOのキーを指定() throws Exception {
        TestUtil.login("user01", "test@example.com");
        tester.request.setMethod("PUT");
        tester.param("finished", "true");
        tester.start("/api/todos/" + KeyFactory.keyToString(othersKey));
        assertThat(tester.response.getStatus(), is(404));
    }
    
    @Test
    public void delete() throws Exception{
        
        int before = tester.count(Todo.class);
        
        TestUtil.login("user01","test@example.com");
        tester.request.setMethod("DELETE");
        tester.start("/api/todos/"+KeyFactory.keyToString(unfinishedKey));
        
        assertThat(tester.response.getStatus(), is(200));
        
        assertThat(tester.count(Todo.class),is(before - 1));
        
        for(Todo todo:Datastore.query(Todo.class).asList()){
            assertThat(todo.getKey(),is(not(unfinishedKey)));            
        }        
    }
    
    @Test
    public void delete_ログインしていない() throws Exception{
        tester.request.setMethod("DELETE");
        tester.start("/api/todos/"+KeyFactory.keyToString(unfinishedKey));        
        assertThat(tester.response.getStatus(), is(401));
    }
    
    @Test
    public void delete_指定したキーのTODOが存在しない() throws Exception{
        Key key = Datastore.createKey(Todo.class, Long.MAX_VALUE);
        TestUtil.login("user01","test@example.com");
        tester.request.setMethod("DELETE");
        tester.start("/api/todos/"+KeyFactory.keyToString(key));
        assertThat(tester.response.getStatus(), is(404));        
    }
    
    @Test
    public void delete_別ユーザーが登録したTODOのキーを指定() throws Exception{
        TestUtil.login("user01","test@example.com");
        tester.request.setMethod("DELETE");
        tester.start("/api/todos/"+KeyFactory.keyToString(othersKey));
        assertThat(tester.response.getStatus(), is(404)); 
    }
}
