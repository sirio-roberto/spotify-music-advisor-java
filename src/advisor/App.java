package advisor;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Scanner;

public class App {
    private final String CLIENT_ID = "e68bacb349d1465fb09530527e363699";
    private final String URI_STR = "http://localhost:8080";
    private final Scanner scanner = new Scanner(System.in);
    private boolean running;
    private boolean authenticated;
    private String accessServerPoint;
    private HttpServer server;
    private final Map<String, Command> commands;
    private String code;


    public App() {
        accessServerPoint = "https://accounts.spotify.com";
        this.commands = initCommands();
        running = true;
        authenticated = false;
    }

    public App(String accessServerPoint) {
        this();
        this.accessServerPoint = accessServerPoint;
    }

    private Map<String, Command> initCommands() {
        return Map.of(
                "exit", new ExitCommand(),
                "new", new NewCommand(),
                "featured", new FeaturedCommand(),
                "categories", new CategoriesCommand(),
                "playlists Mood", new PlaylistsCommand(),
                "auth", new AuthCommand());
    }

    public void run() {
        while (running) {
            String userInput = scanner.nextLine();
            if ("auth".equals(userInput) || "exit".equals(userInput) || authenticated) {
                commands.get(userInput).execute();
            } else {
                System.out.println("Please, provide access for application.");
            }
        }
    }

    private HttpServer getServer() {
        if (server == null) {
            try {
                server = HttpServer.create();
                server.bind(new InetSocketAddress(8080), 0);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return server;
    }

    private void startServer() {
        getServer().createContext("/",
                exchange -> {
                    code = exchange.getRequestURI().getQuery();

                    String browserContent;
                    if (code != null && code.startsWith("code=")) {
                        browserContent = "Got the code. Return back to your program.";
                    } else {
                        browserContent = "Authorization code not found. Try again.";
                    }
                    exchange.sendResponseHeaders(200, browserContent.length());
                    exchange.getResponseBody().write(browserContent.getBytes());
                    exchange.getResponseBody().close();
                });
        getServer().start();
    }

    private void getCode() {
        HttpClient client = HttpClient.newBuilder().build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URI_STR))
                .GET()
                .build();

//        System.out.println("waiting for code...");

        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void getAccessToken() {
        HttpClient client = HttpClient.newBuilder().build();

        HttpRequest request = HttpRequest.newBuilder()
                .headers("Content-Type", "application/x-www-form-urlencoded",
                        "Authorization", "Basic ZTY4YmFjYjM0OWQxNDY1ZmIwOTUzMDUyN2UzNjM2OTk6MjMyODA5NjNiYjE2NDc4ZWJmM2EyYmFjYTYwOTk4NWU=")
                .uri(URI.create(accessServerPoint + "/api/token"))
                .POST(HttpRequest.BodyPublishers.ofString(code + "&grant_type=authorization_code" + "&redirect_uri=" + URI_STR))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("response:\n" + response.body());

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    abstract static class Command {
        abstract void execute();
    }

    private class ExitCommand extends Command {

        @Override
        void execute() {
            running = false;
            scanner.close();

            System.out.println("---GOODBYE!---");
        }
    }

    private class NewCommand extends Command {

        @Override
        void execute() {
            System.out.println("""
                                ---NEW RELEASES---
                                Mountains [Sia, Diplo, Labrinth]
                                Runaway [Lil Peep]
                                The Greatest Show [Panic! At The Disco]
                                All Out Life [Slipknot]""");
        }
    }

    private class FeaturedCommand extends Command {

        @Override
        void execute() {
            System.out.println("""
                                ---FEATURED---
                                Mellow Morning
                                Wake Up and Smell the Coffee
                                Monday Motivation
                                Songs to Sing in the Shower""");
        }
    }

    private class CategoriesCommand extends Command {

        @Override
        void execute() {
            System.out.println("""
                                ---CATEGORIES---
                                Top Lists
                                Pop
                                Mood
                                Latin""");
        }
    }

    private class PlaylistsCommand extends Command {

        @Override
        void execute() {
            System.out.println("""
                                ---MOOD PLAYLISTS---
                                Walk Like A Badass \s
                                Rage Beats \s
                                Arab Mood Booster \s
                                Sunday Stroll""");
        }
    }

    private class AuthCommand extends Command {

        @Override
        void execute() {
            authenticated = true;

            int timeout = 20;
            startServer();

            String url = String.format("%s/authorize?client_id=%s&redirect_uri=%s&response_type=code",
                    accessServerPoint, CLIENT_ID, URI_STR);

            System.out.println("use this link to request the access code:");
            System.out.println(url);


            while (code == null && timeout > 0) {
                getCode();
                timeout--;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            getServer().stop(1);
            if (code == null || code.startsWith("error")) {
                System.out.println("Authorization code not found. Try again.");
            } else {
                System.out.println("Got the code. Return back to your program.");
                getAccessToken();
                System.out.println("---SUCCESS---");
            }
        }
    }
}
