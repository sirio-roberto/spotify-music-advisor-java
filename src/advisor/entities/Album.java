package advisor.entities;

import java.util.Arrays;

public class Album extends AbstractEntity {
    private String[] artists;
    private String url;

    public Album(String name, String[] artists, String link) {
        super(name);
        this.artists = artists;
        this.url = link;
    }

    @Override
    public String toString() {
        return getName() + "\n" + Arrays.toString(artists) + "\n" + url + "\n";
    }
}
