package advisor;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class App {
    private static final Scanner scanner = new Scanner(System.in);
    private static boolean running;
    private static boolean authenticated;

    public App() {
        running = true;
        authenticated = false;
    }

    public void advise() {
        while (running) {
            String userInput = scanner.nextLine();
            if ("auth".equals(userInput) || "exit".equals(userInput) || authenticated) {
                handleCommand(userInput);
            } else {
                System.out.println("Please, provide access for application.");
            }
        }
    }

    private static void handleCommand(String userInput) {
        if (userInput.startsWith("playlists")) {
            new PlaylistsCommand(userInput).execute();
        } else {
            switch (userInput) {
                case "exit" -> new ExitCommand().execute();
                case "new" -> new NewCommand().execute();
                case "featured" -> new FeaturedCommand().execute();
                case "categories" -> new CategoriesCommand().execute();
                case "auth" -> new AuthCommand().execute();
                default -> System.out.println("Unknown command");
            }
        }
    }


    abstract static class Command {
        abstract void execute();
    }

    private static class ExitCommand extends Command {

        @Override
        void execute() {
            running = false;

            System.out.println("---GOODBYE!---");
        }
    }

    private static class NewCommand extends Command {
        private static final String NEW_RELEASES_RESOURCE = "/v1/browse/new-releases";

        @Override
        void execute() {
            String bodyStr = HttpCustomHandler.getBodyResponseAsString(NEW_RELEASES_RESOURCE);
            List<Album> albums = JsonUtils.getAlbumsFromBodyResponse(bodyStr);

            int numOfPages = (int) Math.ceil((double) albums.size() / Config.RECORDS_FOR_PAGE);

            int i = 0;
            int currentPage = 1;

            for (int j = 0; true; j++) {
                if (j < 0) {
                    j = 0;
                }
                System.out.println(albums.get(j) + "\n");
                i++;

                if (i == Config.RECORDS_FOR_PAGE || j == albums.size() - 1) {
                    i = 0;

                    System.out.printf("---PAGE %s OF %s---\n", currentPage, numOfPages);

                    while (true) {
                        String userInput = scanner.nextLine();
                        if ("next".equals(userInput)) {
                            if (currentPage == numOfPages) {
                                System.out.println("No more pages.");
                            } else {
                                currentPage++;
                                break;
                            }
                        } else if ("prev".equals(userInput)) {
                            if (currentPage == 1) {
                                System.out.println("No more pages.");
                            } else {
                                j -= 2 * Config.RECORDS_FOR_PAGE;
                                currentPage--;
                                break;
                            }
                        } else {
                            handleCommand(userInput);
                            return;
                        }
                    }
                }

            }
        }
    }

    private static class FeaturedCommand extends Command {

        private static final String FEATURED_RESOURCE = "/v1/browse/featured-playlists";

        @Override
        void execute() {
            String bodyStr = HttpCustomHandler.getBodyResponseAsString(FEATURED_RESOURCE);
            Map<String, String> playlistsMap = JsonUtils.getFeaturedPlaylistsFromBodyResponse(bodyStr);

            playlistsMap.keySet()
                    .forEach(k -> System.out.println(playlistsMap.get(k).concat("\n").concat(k.concat("\n"))));
        }
    }

    private static class CategoriesCommand extends Command {

        private static final String CATEGORIES_RESOURCE = "/v1/browse/categories";

        @Override
        void execute() {
            String bodyStr = HttpCustomHandler.getBodyResponseAsString(CATEGORIES_RESOURCE);
            Map<String, String> categoriesMap = JsonUtils.getCategoriesFromBodyResponse(bodyStr);

            categoriesMap.keySet().forEach(System.out::println);
        }
    }

    private static class PlaylistsCommand extends Command {
        private static final String CATEGORIES_RESOURCE = "/v1/browse/categories";
        private final String NAME;

        public PlaylistsCommand(String playlistName) {
            NAME = playlistName;
        }

        @Override
        void execute() {
            if (NAME.split(" ").length > 1) {
                String categoryName = NAME.substring("playlists ".length());

                String bodyStr = HttpCustomHandler.getBodyResponseAsString(CATEGORIES_RESOURCE);
                Map<String, String> categoriesMap = JsonUtils.getCategoriesFromBodyResponse(bodyStr);

                String categoryId = categoriesMap.get(categoryName);
                if (categoryId != null) {
                    String playlistsIdResource = "/v1/browse/categories/" + categoryId + "/playlists";

                    bodyStr = HttpCustomHandler.getBodyResponseAsString(playlistsIdResource);
                    if (JsonUtils.isErrorResponse(bodyStr)) {
                        System.out.println(JsonUtils.getErrorMessageBodyResponse(bodyStr));
                    } else {
                        Map<String, String> playlistsMap = JsonUtils.getFeaturedPlaylistsFromBodyResponse(bodyStr);

                        playlistsMap.keySet()
                                .forEach(k -> System.out.println(playlistsMap.get(k).concat("\n").concat(k.concat("\n"))));
                    }
                } else {
                    System.out.println("Unknown category name.");
                }
            } else {
                System.out.println("Unknown category name.");
            }
        }
    }

    private static class AuthCommand extends Command {

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
