package advisor.entities;

public abstract class AbstractEntity {
    private final String name;

    AbstractEntity(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
