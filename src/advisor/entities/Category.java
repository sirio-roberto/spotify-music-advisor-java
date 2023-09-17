package advisor.entities;

public class Category extends AbstractEntity {
    private final String id;

    public Category(String name, String id) {
        super(name);
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return getName();
    }
}
