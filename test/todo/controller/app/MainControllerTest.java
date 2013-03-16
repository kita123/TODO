package todo.controller.app;

import org.slim3.tester.ControllerTestCase;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class MainControllerTest extends ControllerTestCase {

    @Test
    public void run() throws Exception {
        tester.start("/app/main");
        MainController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.isRedirect(), is(false));
        assertThat(tester.getDestinationPath(), is("/app/main.jsp"));
    }
}
