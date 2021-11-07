package cmpt276.as2.parentapp.model;

/**
 * Encapsulates a single child and its functionality into a class.
 */
public class Child {
    private String name;

    public Child(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String newName) {
        name = newName;
    }

    @Override
    public String toString() {
        return "Child name: " + name;
    }
}
