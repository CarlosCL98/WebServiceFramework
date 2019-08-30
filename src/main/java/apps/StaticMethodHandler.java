package apps;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class StaticMethodHandler implements Handler {

    private Method m;

    public StaticMethodHandler(Method method) {
        m = method;
    }

    public String process() {
        String answer = null;
        try {
            answer = (String) m.invoke(null, null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return answer;
    }
}
