package advisor;

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
        for(JsonElement element : items) {
            if (element.isJsonObject()) {
                JsonObject itemObj = element.getAsJsonObject();
                String name = itemObj.get("name").getAsString();

                JsonObject urlObj = itemObj.get("external_urls").getAsJsonObject();
                String url = urlObj.get("spotify").getAsString();

                JsonObject firstArtistObj = itemObj.get("artists").getAsJsonArray().get(0).getAsJsonObject();
                String artistName = firstArtistObj.get("name").getAsString();

                albumList.add(new Album(name, artistName, url));
            }
        }
        return albumList;
    }

    public static Map<String, String> getCategoriesFromBodyResponse(String bodyStr) {
        Map<String, String> categoriesMap = new HashMap<>();
        JsonObject jo = JsonParser.parseString(bodyStr).getAsJsonObject();

        JsonObject albumsObj = jo.get("categories").getAsJsonObject();
        JsonArray items = albumsObj.get("items").getAsJsonArray();
        for(JsonElement element : items) {
            if (element.isJsonObject()) {
                JsonObject itemObj = element.getAsJsonObject();

                String id = itemObj.get("id").getAsString();
                String name = itemObj.get("name").getAsString();
                categoriesMap.put(id, name);
            }
        }
        return categoriesMap;
    }
}
