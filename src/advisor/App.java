package advisor;

import advisor.entities.AbstractEntity;
import advisor.entities.Category;

import java.util.List;
import java.util.Scanner;
import java.util.function.Function;

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
            executeUsingPagination(NEW_RELEASES_RESOURCE, "albums", JsonUtils::getAlbumsFromBodyResponse);
        }

    }

    private static class FeaturedCommand extends Command {

        private static final String FEATURED_RESOURCE = "/v1/browse/featured-playlists";

        @Override
        void execute() {
            executeUsingPagination(FEATURED_RESOURCE, "playlists", JsonUtils::getFeaturedPlaylistsFromBodyResponse);
        }
    }

    private static class CategoriesCommand extends Command {

        private static final String CATEGORIES_RESOURCE = "/v1/browse/categories";

        @Override
        void execute() {
            executeUsingPagination(CATEGORIES_RESOURCE, "categories", JsonUtils::getCategoriesFromBodyResponse);
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
                List<Category> categories = JsonUtils.getCategoriesFromBodyResponse(bodyStr);

                String categoryId = getCategoryIdByName(categories, categoryName);
                if (categoryId != null) {
                    String playlistsIdResource = "/v1/browse/categories/" + categoryId + "/playlists";

                    bodyStr = HttpCustomHandler.getBodyResponseAsString(playlistsIdResource);
                    if (JsonUtils.isErrorResponse(bodyStr)) {
                        System.out.println(JsonUtils.getErrorMessageBodyResponse(bodyStr));
                    } else {
                        executeUsingPagination(playlistsIdResource, "playlists", JsonUtils::getFeaturedPlaylistsFromBodyResponse);
                    }
                } else {
                    System.out.println("Unknown category name.");
                }
            } else {
                System.out.println("Unknown category name.");
            }
        }

        private String getCategoryIdByName(List<Category> categories, String name) {
            return categories.stream()
                    .filter(c -> c.getName().equals(name))
                    .map(Category::getId)
                    .findAny()
                    .orElse(null);
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


    private static void executeUsingPagination(String resource, String rootStr, Function<String, List<? extends AbstractEntity>> function) {
        String bodyStr = HttpCustomHandler.getBodyResponseAsString(resource);
        int totalRecords = JsonUtils.getTotalRecords(bodyStr, rootStr);
        int numOfPages = (int) Math.ceil((double) totalRecords / Config.RECORDS_FOR_PAGE);
        int offset = 0;
        int limit = Config.RECORDS_FOR_PAGE;

        int currentPage = 1;
        while (true) {
            String resourceWithPagination = resource + String.format("?offset=%s&limit=%s", offset, limit);
            bodyStr = HttpCustomHandler.getBodyResponseAsString(resourceWithPagination);
            String prevResource = JsonUtils.getPrevious(bodyStr, rootStr);
            String nextResource = JsonUtils.getNext(bodyStr, rootStr);

            List<AbstractEntity> entityList = (List<AbstractEntity>) function.apply(bodyStr);
            entityList.forEach(System.out::println);

            System.out.printf("---PAGE %s OF %s---\n", currentPage, numOfPages);
            while (true) {
                String userInput = scanner.nextLine();
                if ("next".equals(userInput)) {
                    if (nextResource != null) {
                        offset += Config.RECORDS_FOR_PAGE;
                        currentPage++;
                        break;
                    } else {
                        System.out.println("No more pages.");
                    }
                } else if ("prev".equals(userInput)) {
                    if (prevResource != null) {
                        offset -= Config.RECORDS_FOR_PAGE;
                        currentPage--;
                        break;
                    } else {
                        System.out.println("No more pages.");
                    }
                } else {
                    handleCommand(userInput);
                    return;
                }
            }
        }
    }
}
