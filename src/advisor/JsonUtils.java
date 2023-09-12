package advisor;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

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
                String url = itemObj.get("href").getAsString();

                JsonObject firstArtistObj = itemObj.get("artists").getAsJsonArray().get(0).getAsJsonObject();
                String artistName = firstArtistObj.get("name").getAsString();

                albumList.add(new Album(name, artistName, url));
            }
        }
        return albumList;
    }
}
