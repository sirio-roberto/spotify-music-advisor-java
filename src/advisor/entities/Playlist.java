package advisor.entities;

public class Playlist extends AbstractEntity {
    private final String url;

    public Playlist(String name, String url) {
        super(name);
        this.url = url;
    }

    @Override
    public String toString() {
        return getName() + "\n" + url + "\n";
    }
}
