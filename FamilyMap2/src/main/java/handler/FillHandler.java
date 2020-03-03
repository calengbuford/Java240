package handler;

import java.io.*;
import java.net.*;
import com.google.gson.Gson;
import com.sun.net.httpserver.*;
import request.FillRequest;
import response.FillResponse;
import service.FillService;

public class FillHandler implements HttpHandler {

    /**
     * Handles HTTP requests containing the "/fill/[userName]/{generations}" URL path
     * @param exchange an HttpExchange object, defined by Java
     * @throws IOException
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        FillRequest request;
        FillResponse response;
        Gson gson = new Gson();
        boolean success = false;
        int generations = 4;
        String userName = "";
        boolean validParams = true;

        try {
            // Determine the HTTP request type
            if (exchange.getRequestMethod().toLowerCase().equals("post")) {

                // Get the HTTP request headers
                Headers reqHeaders = exchange.getRequestHeaders();

                // Determine url parameters
                String url = exchange.getRequestURI().getPath();
                String[] urlElements = url.split("/");

                response = new FillResponse();

                // Check the URL parameters
                if (urlElements.length > 2) {
                    userName = urlElements[2];
                }
                else {
                    validParams = false;
                    response.setMessage("Invalid userName");
                    response.setSuccess(false);
                }
                if (urlElements.length > 3) {
                    try {
                        generations = Integer.parseInt(urlElements[3]);
                        if (generations < 0) {
                            validParams = false;
                            response.setMessage("Invalid generations parameter");
                            response.setSuccess(false);
                        }
                    }
                    catch (NumberFormatException e) {
                        validParams = false;
                        response.setMessage("Invalid generations parameter");
                        response.setSuccess(false);
                        System.out.println(e);
                    }
                }

                if (validParams) {
                    // Extract the JSON string from the HTTP request body

                    // Get the request body input stream
                    InputStream reqBody = exchange.getRequestBody();
                    // Read JSON string from the input stream
                    String reqJson = readString(reqBody);

                    // Display/log the request JSON data
                    System.out.println(reqJson);

                    // Call the login service and get the response
                    request = gson.fromJson(reqJson, FillRequest.class);
                    FillService service = new FillService();
                    response = service.fill(request, userName, generations);
                }

                // Convert the response to a byte array
                String respJson = gson.toJson(response);
                byte[] array = respJson.getBytes();

                // Set the header and write the response
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                OutputStream respBody = exchange.getResponseBody();
                respBody.write(array);
                respBody.close();

                success = response.getSuccess();
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
        catch (Exception e) {
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