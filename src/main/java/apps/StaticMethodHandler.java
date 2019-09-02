package apps;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * StaticMethodHandler class is the implementation of
 * the Handler interface. This class allows to execute
 * the method that is handling or keeping.
 */
public class StaticMethodHandler implements Handler {

    private Method m;

    /**
     * StaticMethodHandler constructor.
     *
     * @param method
     */
    public StaticMethodHandler(Method method) {
        m = method;
    }

    /**
     * Process allows to execute the method that
     * this handler is keeping.
     *
     * @return String : the response of the method.
     */
    @Override
    public String process(String methodArg) {
        String answer = null;
        try {
            answer = (String) m.invoke(null, methodArg);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return answer;
    }
}
