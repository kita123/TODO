package todo.model;

import org.slim3.tester.AppEngineTestCase;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class TodoTest extends AppEngineTestCase {

    private Todo model = new Todo();

    @Test
    public void test() throws Exception {
        assertThat(model, is(notNullValue()));
    }
}
