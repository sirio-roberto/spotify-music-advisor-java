package advisor;

import java.util.Arrays;

public class Album {
    private String name;
    private String[] artists;
    private String url;

    public Album(String name, String[] artists, String link) {
        this.name = name;
        this.artists = artists;
        this.url = link;
    }

    @Override
    public String toString() {
        return name + "\n" + Arrays.toString(artists) + "\n" + url;
    }
}
