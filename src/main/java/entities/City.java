package entities;

import java.util.List;

public class City {

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTopics(List<Integer> topics) {
        this.topics = topics;
    }
    public void addTopic(Integer topics) {
        this.topics.add(topics);
    }
    public void setDescription(String description) {
        this.description = description;
    }

    private String id;
    private String name;
    private List<Integer> topics;
    private String description;

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public List<Integer> getTopics() {
        return topics;
    }

    public String getDescription() {
        return description;
    }
}
