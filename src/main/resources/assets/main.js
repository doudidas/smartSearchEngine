$(function() {
    getData();

    // activate 'save recipe' button
    $("#saveRecipe").click(function(event){
        event.preventDefault();
        getRecipeInfo();
    });
})

function getData() {
   $.get('api/recipes')
    .then(function(data) {
        createFilters(data);
        displayData(data);
    });
}

function createFilters(data) {
    //filter by name dropdown
    $.each(data, function( index, value ) {
        $('#filterByName').append('<option value="' +value.id+ '">' + value.name + '</option>');
    });

    $('#filterByName').change(function() {
        var id = $('#filterByName').val();
        getRecipeById(id);
    })

    //filter by time
    $('#search').click(function(event) {
        event.preventDefault();
        var minutes = $('#timeFilter').val();
        filterRecipesByTime(minutes);
    })
}

function getRecipeById(id) {
    $.get('api/recipes/' + id )
    .then(function(response) {  displayData(response); })
}

function filterRecipesByTime(minutes) {
    $.get('api/recipes/time', { time: minutes })
    .then(function(response) { displayData(response); })
}

function displayData(response) {
    var recipes = response;
    $('tr').remove();

    $.each(recipes, function( index, value ) {

        var ingredients = "";
        var secondLastIndex = value.ingredients.length - 1;

        for(var i = 0; i < value.ingredients.length; i++) {
            ingredients += value.ingredients[i];
            if (i < secondLastIndex) { ingredients += ", " }
        }

        $('tbody').append('<tr><td>' + value.name + '</td><td>' + ingredients +  '<td>' + value.minutes + '</td></tr>');
    })
}

function getRecipeInfo() {
    var name = $("#name").val();
    var ingredients = $("#ingredients").val().split(',');
    var minutes = $("#minutes").val();

    if ( name && ingredients && minutes ) {
        postRecipe(name, ingredients, minutes);
    }
}


function postRecipe(name, ingredients, minutes) {

    $.post({url: '/api/recipes',
            data: JSON.stringify({ name: name, ingredients: ingredients, minutes: minutes }),
            dataType: "text",
            contentType: "application/json"
    }).done(function(response) {
        location.reload();
        alert("Recipe posted!");
    }).fail(function(error) {
        console.log(error);
    });
}

