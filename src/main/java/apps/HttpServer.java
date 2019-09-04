package apps;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Date;

/**
 * Big Http Server implements a web server that can return html, images and other files.
 *
 * @author Carlos Medina
 */
public class HttpServer {

    private static final File ROOT = new File(System.getProperty("user.dir") + "/src/main/resources/public");
    private static final String DEFAULT_FILE = "/index.html";
    private static final String NOT_FOUND = "/404.html";
    private static final String METHOD_NOT_ALLOWED = "/405.html";
    private static final String UNSUPPORTED_MEDIA_TYPE = "/415.html";

    /**
     *
     * @param header
     * @param out
     * @param dataOut
     * @throws IOException
     */
    public static void httpHandler(String[] header, PrintWriter out, BufferedOutputStream dataOut) throws IOException {
        String[] responseMethod = checkMethod(out, dataOut, header[0]);
        String contentType = "text/html";
        if (Boolean.parseBoolean(responseMethod[0])) {
            File file = null;
            if (header[1].equals("/")) {
                file = new File(ROOT, DEFAULT_FILE);
                sendResponse(out, dataOut, file, contentType, "200 OK");
            } else {
                file = new File(ROOT, header[1]);
                if (!file.exists()) {
                    File newFile = new File(ROOT, NOT_FOUND);
                    sendResponse(out, dataOut, newFile, "text/html", "404 NOT_FOUND");
                } else {
                    String[] responseContentType = checkContentType(out, dataOut, header[1]);
                    contentType = responseContentType[0];
                    if (responseContentType[1].equals("UNSUPPORTED_MEDIA_TYPE")) {
                        file = new File(ROOT, UNSUPPORTED_MEDIA_TYPE);
                        sendResponse(out, dataOut, file, contentType, "415 UNSUPPORTED_MEDIA_TYPE");
                    } else if (responseContentType[1].equals("OK")) {
                        sendResponse(out, dataOut, file, contentType, "200 OK");
                    }
                }
            }
        } else {
            if (responseMethod[1].equals("METHOD_NOT_ALLOWED")) {
                File file = new File(ROOT, METHOD_NOT_ALLOWED);
                sendResponse(out, dataOut, file, "text/html", "405 METHOD_NOT_ALLOWED");
            }
        }
    }

    /**
     * Checks the HTTP method sent by the client to determine if the
     * server can or cannot response to it.
     *
     * @param out
     * @param dataOut
     * @param method
     * @return String[] : the response.
     * @throws IOException
     */
    private static String[] checkMethod(PrintWriter out, BufferedOutputStream dataOut, String method) throws IOException {
        String[] response = new String[2];
        response[0] = "true";
        response[1] = "OK";
        if (!method.equals("GET")) {
            response[0] = "false";
            response[1] = "METHOD_NOT_ALLOWED";
        }
        return response;
    }

    /**
     * Checks the content type sent by the client to determine if the
     * server can or cannot response to it.
     *
     * @param out
     * @param dataOut
     * @param requestedFile
     * @return String[] : the response.
     * @throws IOException
     */
    private static String[] checkContentType(PrintWriter out, BufferedOutputStream dataOut, String requestedFile) throws IOException {
        String[] response = new String[2];
        response[0] = "text/html";
        response[1] = "OK";
        if (requestedFile.endsWith(".htm") || requestedFile.endsWith(".html")) {
            response[0] = "text/html";
        } else if (requestedFile.endsWith(".png")) { // Images
            response[0] = "image/png";
        } else if (requestedFile.endsWith(".jpg") || requestedFile.endsWith(".jpeg")) {
            response[0] = "image/jpeg";
        } else if (requestedFile.endsWith(".ico")) {
            response[0] = "image/x-icon";
        } else if (requestedFile.endsWith(".svg")) {
            response[0] = "image/svg+xml";
        } else if (requestedFile.endsWith(".css") || requestedFile.endsWith(".min.css") || requestedFile.endsWith(".scss")) { // CSS
            response[0] = "text/css";
        } else if (requestedFile.endsWith(".js") || requestedFile.endsWith(".min.js")) { // Javascript
            response[0] = "application/javascript";
        } else if (requestedFile.endsWith(".less")) {
            response[0] = "text/plain";
        } else if (requestedFile.endsWith(".ttf")) { // Fonts
            response[0] = "application/x-font-ttf";
        } else if (requestedFile.endsWith(".woff")) {
            response[0] = "application/font-woff";
        } else if (requestedFile.endsWith(".woff2")) {
            response[0] = "application/font-woff2";
        } else if (requestedFile.endsWith(".eot")) {
            response[0] = "application/vnd.ms-fontobject";
        } else if (requestedFile.endsWith(".otf")) {
            response[0] = "application/x-font-opentype";
        } else {
            response[1] = "UNSUPPORTED_MEDIA_TYPE";
        }
        return response;
    }

    /**
     * Send the response when the server filter the request.
     *
     * @param out
     * @param dataOut
     * @param file
     * @param contentType
     * @param response
     * @throws IOException
     */
    private static void sendResponse(PrintWriter out, BufferedOutputStream dataOut, File file, String contentType, String response) throws IOException {
        // Header
        headerResponse(out, file, contentType, response);
        // Content
        String[] contentTypeDivided = contentType.split("/");
        if (contentTypeDivided[0].equals("image")) {
            if (contentTypeDivided[1].contains("svg")) {
                contentResponse(file, dataOut);
            } else {
                BufferedImage image = ImageIO.read(file);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(image, contentTypeDivided[1], baos);
                dataOut.write(baos.toByteArray());
                dataOut.flush();
            }
        } else {
            contentResponse(file, dataOut);
        }
    }

    /**
     *
     * @param out
     * @param file
     * @param contentType
     * @param response
     */
    public static void headerResponse(PrintWriter out, File file, String contentType, String response) {
        out.println("HTTP/1.1 " + response + "\r");
        out.println("Server: Java HTTP Server from CarlosCL : 1.0\r");
        out.println("Date: " + new Date());
        out.println("Content-type: " + contentType + ";charset=UTF-8\r");
        if (file != null) {
            out.println("Content-length: " + file.length() + "\r");
        }
        out.println("\r");
        out.flush();
    }

    /**
     *
     * @param file
     * @param dataOut
     * @throws IOException
     */
    public static void contentResponse (File file, BufferedOutputStream dataOut) throws IOException {
        byte[] fileByte = fileToByte(file);
        dataOut.write(fileByte, 0, (int) file.length());
        dataOut.flush();
    }

    /**
     * Convert the file to bytes to send it to the client.
     *
     * @param file
     * @return byte[] : file in form of bytes.
     * @throws IOException
     */
    private static byte[] fileToByte(File file) throws IOException {
        byte[] dataByte = new byte[(int) file.length()];
        FileInputStream fileIn = null;
        try {
            fileIn = new FileInputStream(file);
            fileIn.read(dataByte);
        } finally {
            if (fileIn != null) {
                fileIn.close();
            }
        }
        return dataByte;
    }
}
