package advisor;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static advisor.Config.*;

public class Authorization {

    public void createHttpServer() {

        String url = String.format("%s/authorize?client_id=%s&redirect_uri=%s&response_type=code",
                AUTH_SERVER_PATH, CLIENT_ID, REDIRECT_URI);

        System.out.println("use this link to request the access code:");
        System.out.println(url);

        try {
            HttpServer server = HttpServer.create();
            server.bind(new InetSocketAddress(8080), 0);
            server.start();

            server.createContext("/",
                    exchange -> {
                        String query = exchange.getRequestURI().getQuery();

                        String browserContent;
                        if (query != null && query.startsWith("code")) {
                            AUTH_CODE = query.substring(5);
                            System.out.println("code received");
                            browserContent = "Got the code. Return back to your program.";
                        } else {
                            browserContent = "Authorization code not found. Try again.";
                        }
                        exchange.sendResponseHeaders(200, browserContent.length());
                        exchange.getResponseBody().write(browserContent.getBytes());
                        exchange.getResponseBody().close();
                    });

            System.out.println("waiting for code...");

            int timeout = 30;
            while (AUTH_CODE.isBlank() && timeout > 0) {
                makeSimpleGetRequest();
                Thread.sleep(1000L);
                timeout--;
            }
            server.stop(1);

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void makeSimpleGetRequest() throws IOException, InterruptedException {
        HttpClient.newBuilder().build().send(
                HttpRequest.newBuilder()
                        .uri(URI.create(REDIRECT_URI))
                        .GET()
                        .build()
                , HttpResponse.BodyHandlers.ofString());
    }

    public void authRequest() {
        System.out.println("making http request for access_token...");
        System.out.println("response:");

        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .uri(URI.create(AUTH_SERVER_PATH + "/api/token"))
                .POST(HttpRequest.BodyPublishers.ofString(
                        "grant_type=authorization_code" +
                                "&code=" + AUTH_CODE +
                                "&client_id=" + CLIENT_ID +
                                "&client_secret=" + CLIENT_SECRET +
                                "&redirect_uri=" + REDIRECT_URI))
                .build();

        try {
            HttpClient client = HttpClient.newBuilder().build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response != null) {
                parseAccessToken(response.body());
                System.out.println(response.body());
                System.out.println("---SUCCESS---");
            }

        } catch (IOException | NullPointerException | InterruptedException e) {
            System.out.println("Server error");
        }
    }

    private void parseAccessToken(String body) throws NullPointerException {
        JsonObject object = JsonParser.parseString(body).getAsJsonObject();
        ACCESS_TOKEN = object.get("access_token").getAsString();
    }
}
