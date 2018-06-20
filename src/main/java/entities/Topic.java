package entities;

public class Topic {
    private String name, id, description;

    public Topic() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getDescription() { return this.description;}

    public void setId(String id) {
        this.id = id;
    }

    public void setDescription(String description) { this.description = description;}
}
