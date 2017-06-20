package entities;

import java.util.List;

public class City {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getTopics() {
        return topics;
    }

    public void setTopics(List<Integer> topics) {
        this.topics = topics;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private String id;
    private String name;
    private List<Integer> topics;
    private String description;
}
