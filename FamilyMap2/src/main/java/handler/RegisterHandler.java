package handler;

import java.io.*;
import java.net.*;
import com.google.gson.Gson;
import com.sun.net.httpserver.*;
import request.RegisterRequest;
import response.RegisterResponse;
import service.RegisterService;

public class RegisterHandler implements HttpHandler {

    /**
     * Handles HTTP requests containing the "/user/register" URL path
     * @param exchange an HttpExchange object, defined by Java
     * @throws IOException
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        RegisterRequest request;
        RegisterResponse response;
        RegisterService service = new RegisterService();
        Gson gson = new Gson();
        boolean success = false;

        try {
            // Determine the HTTP request type
            if (exchange.getRequestMethod().toLowerCase().equals("post")) {

                // Get the HTTP request headers
                Headers reqHeaders = exchange.getRequestHeaders();

                // Extract the JSON string from the HTTP request body

                // Get the request body input stream
                InputStream reqBody = exchange.getRequestBody();
                // Read JSON string from the input stream
                String reqJson = readString(reqBody);

                // Display/log the request JSON data
                System.out.println(reqJson);

                // Call the login service and get the response
                request = gson.fromJson(reqJson, RegisterRequest.class);
                response = service.register(request);

                // Convert the response to a byte array
                String respJson = gson.toJson(response);
                byte[] array = respJson.getBytes();

                // Set the header and write the response
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                OutputStream respBody = exchange.getResponseBody();
                respBody.write(array);
                respBody.close();

                success = true;
            }

            if (!success) {
                // The HTTP request was invalid somehow, so we return a "bad request"
                // status code to the client.
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                // Since the client request was invalid, they will not receive the
                // list of games, so we close the response body output stream,
                // indicating that the response is complete.
                exchange.getResponseBody().close();
            }
        }
        catch (IOException e) {
            // Some kind of internal error has occurred inside the server (not the
            // client's fault), so we return an "internal server error" status code to the client.
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);
            // Since the server is unable to complete the request, the client will
            // not receive the list of games, so we close the response body output stream,
            // indicating that the response is complete.
            exchange.getResponseBody().close();

            // Display/log the stack trace
            e.printStackTrace();
        }
    }

    /**
     * The readString method shows how to read a String from an InputStream
     * @param is InputStream
     * @return string
     * @throws IOException
     */
    private String readString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader sr = new InputStreamReader(is);
        char[] buf = new char[1024];
        int len;
        while ((len = sr.read(buf)) > 0) {
            sb.append(buf, 0, len);
        }
        return sb.toString();
    }
}