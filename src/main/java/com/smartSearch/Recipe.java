package com.smartSearch;

import java.util.ArrayList;
import java.util.List;

public class Recipe {

    private String name;
    private List<String> ingredients = new ArrayList<>();
    private int minutes;
    private String id;

    public Recipe () {}

    public Recipe (String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
