package advisor;

import java.util.Map;
import java.util.Scanner;

public class App {
    private boolean running;
    private boolean authenticated;
    private final Map<String, Command> commands;


    public App() {
        this.commands = initCommands();
        running = true;
        authenticated = false;
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

    public void advise() {
        Scanner scanner = new Scanner(System.in);

        while (running) {
            String userInput = scanner.nextLine();
            if ("auth".equals(userInput) || "exit".equals(userInput) || authenticated) {
                commands.get(userInput).execute();
            } else {
                System.out.println("Please, provide access for application.");
            }
        }
    }


    abstract static class Command {
        abstract void execute();
    }

    private class ExitCommand extends Command {

        @Override
        void execute() {
            running = false;

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

            Authorization authorization = new Authorization();
            authorization.createHttpServer();
            if (!Config.AUTH_CODE.isBlank()) {
                authorization.authRequest();
            }
        }
    }
}
