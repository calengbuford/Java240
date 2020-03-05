package handler;

import java.io.*;
import java.net.*;

import com.google.gson.Gson;
import com.sun.net.httpserver.*;
import request.PersonIDRequest;
import request.PersonRequest;
import response.PersonIDResponse;
import response.PersonResponse;
import service.PersonIDService;
import service.PersonService;

public class PersonHandler implements HttpHandler {

    /**
     * Handles HTTP requests containing the "/person" URL path
     * @param exchange an HttpExchange object, defined by Java
     * @throws IOException
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        PersonService service = new PersonService();
        PersonIDService serviceID = new PersonIDService();
        Gson gson = new Gson();
        boolean success = false;

        try {
            // Determine the HTTP request type
            if (exchange.getRequestMethod().toLowerCase().equals("get")) {

                // Get the HTTP request headers
                Headers reqHeaders = exchange.getRequestHeaders();

                // Check to see if an "Authorization" header is present
                if (reqHeaders.containsKey("Authorization")) {

                    // Extract the auth token from the "Authorization" header
                    String headerAuthToken = reqHeaders.getFirst("Authorization");

                    // Determine url parameters
                    String url = exchange.getRequestURI().getPath();
                    String[] urlElements = url.split("/");

                    // This will hold the JSON data we will return in the HTTP response body
                    String respJson = "";

                    if (urlElements.length == 2) {
                        // Call event service
                        PersonResponse response;
                        PersonRequest request;

                        request = new PersonRequest();
                        response = service.person(request, headerAuthToken);

                        // Convert response to JSON
                        respJson = gson.toJson(response);

                        // Send the HTTP response to the client
                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                        OutputStream respBody = exchange.getResponseBody();
                        writeString(respJson, respBody);
                        respBody.close();

                        success = response.getSuccess();
                    }
                    else if (urlElements.length == 3) {
                        PersonIDResponse response;
                        PersonIDRequest request;

                        request = new PersonIDRequest();
                        response = serviceID.person(request, headerAuthToken, urlElements[2]);

                        // Convert response to JSON
                        respJson = gson.toJson(response);

                        // Send the HTTP response to the client
                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                        OutputStream respBody = exchange.getResponseBody();
                        writeString(respJson, respBody);
                        respBody.close();

                        success = response.getSuccess();
                    }
                    else {
                        PersonResponse response = new PersonResponse();
                        response.setMessage("Error: Invalid request");
                        response.setSuccess(false);

                        // Convert response to JSON
                        respJson = gson.toJson(response);

                        // Send the HTTP response to the client
                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                        OutputStream respBody = exchange.getResponseBody();
                        writeString(respJson, respBody);
                        respBody.close();

                        success = response.getSuccess();
                    }
                }
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

    /*
        The writeString method shows how to write a String to an OutputStream.
    */
    private void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        sw.write(str);
        sw.flush();
    }
}