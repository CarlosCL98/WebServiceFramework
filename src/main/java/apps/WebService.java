package apps;

/**
 * Hello world!
 */
public class WebService {

    @Web("cuadrado")
    public static String square() {
        return "<html>" +
                "<head>" +
                "<title></title>" +
                "</head>" +
                "<body>" +
                "<div>Inicio</div>" +
                "</body>" +
                "</html>";
    }
}
