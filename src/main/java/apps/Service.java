package apps;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Service {

    private Map<String, Handler> urlHandler = new HashMap();

    public static void listen() {

    }

    public void addWebMethods(String className) {
        try {
            Class<?> c = Class.forName(className);
            Method[] methods = c.getMethods();
            for (Method m : methods) {
                if (m.isAnnotationPresent(Web.class)) {
                    urlHandler.put("apps/"+m.getAnnotation(Web.class), new StaticMethodHandler(m));
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void init() {

    }
}
