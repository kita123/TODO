package todo.controller.app;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;

public class FinishedListController extends Controller {

    @Override
    public Navigation run() throws Exception {
        return forward("finishedList.jsp");
    }
}
