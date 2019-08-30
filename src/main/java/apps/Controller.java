package apps;

public class Controller {

    private static Service service = new Service();

    public static void main(String[] args) {
        Service serviceWebServer = getService();
        serviceWebServer.init();
    }

    public static Service getService() {
        return service;
    }
}
