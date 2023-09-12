package advisor;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static advisor.Config.*;

public class HttpCustomHandler {

    public static String getBodyResponseAsString(String resource) {

        HttpRequest request = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + ACCESS_TOKEN)
                .uri(URI.create(Config.API_SERVER_PATH + resource))
                .GET()
                .build();

        try {
            HttpClient client = HttpClient.newBuilder().build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();

        } catch (IOException | NullPointerException | InterruptedException e) {
            System.out.println("Server error");
        }
        throw new RuntimeException("Error while getting response");
    }
}
