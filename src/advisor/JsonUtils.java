package advisor;

import advisor.entities.Album;
import advisor.entities.Playlist;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonUtils {
    public static List<Album> getAlbumsFromBodyResponse(String bodyStr) {
        List<Album> albumList = new ArrayList<>();
        JsonObject jo = JsonParser.parseString(bodyStr).getAsJsonObject();

        JsonObject albumsObj = jo.get("albums").getAsJsonObject();
        JsonArray items = albumsObj.get("items").getAsJsonArray();
        for (JsonElement element : items) {
            if (element.isJsonObject()) {
                JsonObject itemObj = element.getAsJsonObject();
                String name = itemObj.get("name").getAsString();

                JsonObject urlObj = itemObj.get("external_urls").getAsJsonObject();
                String url = urlObj.get("spotify").getAsString();

                JsonArray artists = itemObj.get("artists").getAsJsonArray();
                String[] artistsNameArray = new String[artists.size()];
                for (int i = 0; i < artistsNameArray.length; i++) {
                    JsonObject artistObj = artists.get(i).getAsJsonObject();
                    artistsNameArray[i] = artistObj.get("name").getAsString();
                }

                albumList.add(new Album(name, artistsNameArray, url));
            }
        }
        return albumList;
    }

    public static Map<String, String> getCategoriesFromBodyResponse(String bodyStr) {
        Map<String, String> categoriesMap = new HashMap<>();
        JsonObject jo = JsonParser.parseString(bodyStr).getAsJsonObject();

        JsonObject categoriesObj = jo.get("categories").getAsJsonObject();
        JsonArray items = categoriesObj.get("items").getAsJsonArray();
        for(JsonElement element : items) {
            if (element.isJsonObject()) {
                JsonObject itemObj = element.getAsJsonObject();

                String id = itemObj.get("id").getAsString();
                String name = itemObj.get("name").getAsString();
                categoriesMap.put(name, id);
            }
        }
        return categoriesMap;
    }

    public static List<Playlist> getFeaturedPlaylistsFromBodyResponse(String bodyStr) {
        List<Playlist> playlists = new ArrayList<>();
        JsonObject jo = JsonParser.parseString(bodyStr).getAsJsonObject();

        JsonObject playlistsObj = jo.get("playlists").getAsJsonObject();
        JsonArray items = playlistsObj.get("items").getAsJsonArray();
        for(JsonElement element : items) {
            if (element.isJsonObject()) {
                JsonObject itemObj = element.getAsJsonObject();

                JsonObject urlObj = itemObj.get("external_urls").getAsJsonObject();
                String url = urlObj.get("spotify").getAsString();

                String name = itemObj.get("name").getAsString();

                playlists.add(new Playlist(name, url));
            }
        }
        return playlists;
    }

    public static boolean isErrorResponse(String bodyStr) {
        JsonObject jo = JsonParser.parseString(bodyStr).getAsJsonObject();
        JsonElement errorElement = jo.get("error");
        return errorElement != null;
    }

    public static String getErrorMessageBodyResponse(String bodyStr) {
        JsonObject jo = JsonParser.parseString(bodyStr).getAsJsonObject();

        JsonObject errorObj = jo.get("error").getAsJsonObject();
        return errorObj.get("message").getAsString();
    }
}
