package org.example.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ClientInterface implements Runnable {

    private final HttpClient client;
    private final String data;
    private final String endpoint;
    private HttpResponse<String> response; // Store the response

    public ClientInterface(String endpoint, String data) {
        this.client = HttpClient.newHttpClient();
        this.endpoint = endpoint;
        this.data = data;
    }

    public ClientInterface(HttpClient client, String data, String endpoint) {
        this.client = client;
        this.data = data;
        this.endpoint = endpoint;
    }

    @Override
    public void run() {
        try {
            // Configure the request to upload a day of lift rides
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(endpoint))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(data))
                    .build();

            // Send the request to the server
            this.response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    // Method to wait for the response to be received
    public void awaitResponse() throws InterruptedException {
        // Wait for the response to be set
        while (response == null) {
            Thread.sleep(100); // Sleep for a short time before checking again
        }
    }

    // Method to get the HTTP response code
    public int getResponseCode() {
        if (response != null) {
            return response.statusCode();
        }
        return -1; // Return -1 if response is not available
    }
}
