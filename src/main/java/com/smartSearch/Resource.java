package com.smartSearch;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Path("/recipes")
@Produces(MediaType.APPLICATION_JSON)
public class Resource {

    private final MongoCollection<Document> collection;
    private List<Recipe> recipes = new ArrayList<>();

    public Resource(MongoDatabase database) {
        this.collection = database.getCollection("recipesCollection");
    }

    @GET
    public List<Recipe> getAllRecipes() {
        this.recipes = collection.find().map(doc -> docToRecipe(doc)).into(new ArrayList<>());
        return recipes;
    }

    @GET
    @Path("{id}")
    public List<Recipe> getRecipeByID(@PathParam("id") String id) {
        List<Recipe> selectedRecipes = new ArrayList<>();

        for (Recipe recipe : recipes) {
            if (recipe.getId().equals(id)) {
                selectedRecipes.add(recipe);
                return selectedRecipes;
            }
        }
        return null;
    }

    @GET
    @Path("time")
    public List<Recipe> getRecipeByTime(@QueryParam("time") int minutes) {
        List<Recipe> selectedRecipes = new ArrayList<>();

        for (Recipe recipe : recipes) {
            if (recipe.getMinutes() <= minutes) {
                selectedRecipes.add(recipe);
            }
        }
        return selectedRecipes;
    }

    @POST
    public void postRecipe(Recipe recipe) {
        saveToDB(recipe);
    }

    private void saveToDB(Recipe recipe) throws NullPointerException {
        collection.insertOne(recipeToDoc(recipe));
    }

    private final Document recipeToDoc(Recipe recipe) {

        return new Document()
                .append("name", recipe.getName())
                .append("ingredients", recipe.getIngredients())
                .append("minutes", recipe.getMinutes());
    }

    private Recipe docToRecipe(Document doc) {
        Recipe recipe = new Recipe();

        recipe.setId(doc.get("_id").toString());
        recipe.setName(doc.getString("name"));
        recipe.setIngredients((List<String>) doc.get("ingredients"));
        recipe.setMinutes(doc.getInteger("minutes"));
        return recipe;
    }
}