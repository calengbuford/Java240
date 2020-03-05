package handler_;

import java.io.*;
import java.net.*;
import java.nio.file.Files;

import com.sun.net.httpserver.*;

public class DefaultHandler implements HttpHandler {
    public final static String FILE_ROOT_DIRECTORY = "web";

    /**
     * Handles HTTP requests containing the "/" URL path
     * @param exchange an HttpExchange object, defined by Java
     * @throws IOException
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        boolean success = false;

        try {
            // Determine the HTTP request type
            if (exchange.getRequestMethod().toLowerCase().equals("get")) {

                String url = exchange.getRequestURI().getPath();
                String[] urlElements = url.split("/");

                if (urlElements.length == 0 || "/".equals(url)) {
                    url = "/index.html";
                }

                // Build path to file and create file
                String filePath = FILE_ROOT_DIRECTORY + url;
                File file = new File(filePath);

                if (file.exists() && file.canRead()) {
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);

                    // Copy the file into the response body
                    OutputStream respBody = exchange.getResponseBody();
                    Files.copy(file.toPath(), respBody);

                    respBody.close();
                    success = true;
                }
            }

            if (!success) {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, 0);

                String filePath = FILE_ROOT_DIRECTORY + "/HTML/404.html";
                File file = new File(filePath);

                OutputStream respBody = exchange.getResponseBody();
                Files.copy(file.toPath(), respBody);

                respBody.close();
            }
        }
        catch (IOException e) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);
            exchange.getResponseBody().close();

            // Display/log the stack trace
            e.printStackTrace();
        }
    }

}