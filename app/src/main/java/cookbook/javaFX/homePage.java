package cookbook.javaFX;

import cookbook.objectControllers.recipeControler;
import cookbook.objectControllers.userController;
import cookbook.objects.QuanitityIngredients;
import cookbook.objects.ingredientObject;
import cookbook.objects.recipeObject;
import cookbook.objects.userObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import cookbook.objects.tagObject;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class homePage implements Initializable {
  
  @FXML
  public TextField search;
  @FXML
  public Text IngField;
  @FXML
  public Label portionsLabel;
  @FXML
  public TableView<recipeObject> recipeLists;
  @FXML
  public CheckBox favoritecheck;
  @FXML
  private Text tagField;
  @FXML
  public Button back;
  @FXML
  public Label recipeName;
  @FXML
  public Button addToFavorite;
  
  public List<recipeObject> recipes;
  
  int portions = 1;
  
  
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    try {
      recipes = recipeControler.getRecpies();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    System.out.println(recipes.size() + "Size of elements");
    ObservableList<recipeObject> recipeList = FXCollections.observableArrayList(recipes);
    
    recipeLists.getColumns().clear();
    
    TableColumn<recipeObject, String> recipeNameColumn = new TableColumn<>("Name");
    recipeNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
    
    recipeLists.getColumns().add(recipeNameColumn);
    recipeLists.getItems().clear();
    recipeLists.setItems(recipeList);
    
    recipeLists.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      // code to update text of addToFavorite button based on favorite status of newly selected recipe
      recipeObject selectedRecipeObject = recipeLists.getSelectionModel().getSelectedItem();
      System.out.println("We out here ");
      System.out.println(selectedRecipeObject.getName());
      recipeName.setText(selectedRecipeObject.getName());
      if (selectedRecipeObject != null) {
        if (selectedRecipeObject.getStar() == true) {
          addToFavorite.setText("Remove From Favorite");
        } else {
          addToFavorite.setText("Add to Favorite");
        }
        System.out.println("We inside ");
        List<QuanitityIngredients> ingredientObjects = selectedRecipeObject.getIngredientsList();
        List<tagObject> tagObjects = selectedRecipeObject.getTagList();
        System.out.println(ingredientObjects.size() + "inggg");
        System.out.println("We inside 3");
        StringBuilder sb = new StringBuilder(); // ingridents 
        StringBuilder sb2 = new StringBuilder(); // tags
        for (QuanitityIngredients ingredient : ingredientObjects) {
          sb.append(ingredient.getAmount() + ingredient.getUnit() + " " + ingredient.getName()).append(", \n");
          System.out.println(sb);
        }
        if (sb.length() > 2) {
          sb.setLength(sb.length() - 2);
        }
        for (tagObject tag : tagObjects) {
          sb2.append(tag.getTag_name()).append(", ");
        }
        tagField.setText(sb2.toString());
        
        IngField.setText(sb.toString());
        
      }
    });


/* recipeLists.setOnMouseClicked(new EventHandler<MouseEvent>() {
  @Override
  public void handle(MouseEvent event) {
    if (event.getClickCount() > 0) {
      recipeObject selectedRecipeObject = recipeLists.getSelectionModel().getSelectedItem();
      System.out.println("We out here ");
      System.out.println(selectedRecipeObject.getName());
      recipeName.setText(selectedRecipeObject.getName());
      if (selectedRecipeObject != null) {
        if (selectedRecipeObject.getStar() == true) {
          addToFavorite.setText("Remove From Favorite");
        } else {
          addToFavorite.setText("Add to Favorite");
        }
        System.out.println("We inside ");
        List<QuanitityIngredients> ingredientObjects = selectedRecipeObject.getIngredientsList();
        List<tagObject> tagObjects = selectedRecipeObject.getTagList();
        System.out.println(ingredientObjects.size() + "inggg");
        System.out.println("We inside 3");
        StringBuilder sb = new StringBuilder(); // ingridents 
        StringBuilder sb2 = new StringBuilder(); // tags
        for (QuanitityIngredients ingredient : ingredientObjects) {
          sb.append(ingredient.getAmount() + ingredient.getUnit() + " " + ingredient.getName()).append(", \n");
          System.out.println(sb);
        }
        if (sb.length() > 2) {
          sb.setLength(sb.length() - 2);
        }
        for (tagObject tag : tagObjects) {
          sb2.append(tag.getTag_name()).append(", ");
        }
        tagField.setText(sb2.toString());
        
        IngField.setText(sb.toString());
        
      }
    }
  }
}); */

}

public void searchMethod() throws SQLException, IOException {
  String searchTxt = search.getText();
  recipeControler controller = new recipeControler(); // create an instance of recipeControler
  // Saving Arrays
  List<recipeObject> recipes = controller.getRecpies(); // Store recipes
  List<recipeObject> filteredRecipes = new ArrayList<>(); // filtered recipes
  
  
  String[] searchWord =searchTxt.split(",");
  
  for (recipeObject recipe : recipes) {
    List<QuanitityIngredients> ingredients = recipe.getIngredientsList();
    List<tagObject> tags = recipe.getTagList();
    boolean tagMatch = false;
    boolean ingMatch = false;
    
    for (String word : searchWord) {
      word = word.trim().toLowerCase();
      
      for (QuanitityIngredients ing : ingredients) {
        // check if the ingredient name contains the search string
        if (ing.getName().toLowerCase().contains(word)){
          ingMatch = true;
          break;
        }
      }
      
      for (tagObject tag : tags) {
        // check if the recipe has the tag.
        if (tag.getTag_name().toLowerCase().contains(word)) {
          tagMatch = true;
          break;
        }
      }
      
    }
    if (tagMatch || ingMatch) {
      filteredRecipes.add(recipe);
    }
  }
  // set the items of the TableView to the filtered list of recipes
  ObservableList<recipeObject> observableFilteredRecipes = FXCollections.observableArrayList(filteredRecipes);
  recipeLists.setItems(observableFilteredRecipes);
}


public void updateFavorite() throws SQLException {
  try {
    recipeControler recipeController = new recipeControler();
    recipeObject selectedRecipe = recipeLists.getSelectionModel().getSelectedItem();
    recipeController.updateFavoriteStatus(selectedRecipe);
    System.out.println(selectedRecipe.getStar());
    
    if (selectedRecipe.getStar() == true) {
      Alert addedToFav = new Alert(AlertType.CONFIRMATION);
      addedToFav.setTitle("Added to Fav!!");
      addedToFav.setContentText("You have added this recipe to Favorite!");
      addedToFav.show();
    } else {
      Alert addedToFav = new Alert(AlertType.CONFIRMATION);
      addedToFav.setTitle("removed from Fav!!");
      addedToFav.setContentText("You have removed this recipe from Favorite!");
      addedToFav.show();
    }
    
  } catch (Exception e) {
    System.out.println(e + "fav method problem");
  }
  
  
}


/*public void favoriteRecipeList() throws SQLException {
  List<recipeObject> faveList = recipeControler.favoriteObjects();
  ObservableList<recipeObject> observableFavList = FXCollections.observableArrayList(faveList);
  recipeLists.setItems(observableFavList);
  
}*/

public void getFilteredRecipes(ActionEvent event) throws SQLException {
  CheckBox favoritecheck = (CheckBox) event.getSource();
  if (favoritecheck.isSelected()) {
    List<recipeObject> faveList = recipeControler.favoriteObjects();
    ObservableList<recipeObject> observableFavList = FXCollections.observableArrayList(faveList);
    recipeLists.setItems(observableFavList);
  } else {
    List<recipeObject> normalList = recipeControler.getRecpies();
    ObservableList<recipeObject> observablenormList = FXCollections.observableArrayList(normalList);
    recipeLists.setItems(observablenormList);
  }
}

// adjust the number of persons a recipe is
private void updateIngredientsText() {
  StringBuilder ingredientsString = new StringBuilder();
  recipeObject recp = recipeLists.getSelectionModel().getSelectedItem();
  for (QuanitityIngredients quantifiedIngredient : recp.getIngredientsList()) {
    float ingredientAmount = quantifiedIngredient.getAmount() * portions;
    String ingredientUnit = quantifiedIngredient.getUnit() != null ? " " + quantifiedIngredient.getUnit() : "";
    String ingredientName = quantifiedIngredient.getIngredient().getName();
    ingredientsString.append(String.format("%s%s %s\n", ingredientAmount, ingredientUnit, ingredientName));
  }
  IngField.setText(ingredientsString.toString());
  portionsLabel.setText(String.valueOf(portions));
}

@FXML
void onDecreasePortions(ActionEvent event) {
  if (portions > 1) {
    portions--;
    updateIngredientsText();
  }
}

@FXML
void onIncreasePortions(ActionEvent event) {
  portions++;
  updateIngredientsText();
}

public void backButton(ActionEvent event) throws SQLException, IOException {
  URL url = new File("src/main/java/cookbook/resources/mainmenu.fxml").toURI().toURL();
  FXMLLoader loader = new FXMLLoader(url);
  Parent root = loader.load();
  Scene loginScene = new Scene(root);
  
  Stage mainStage = (Stage) back.getScene().getWindow();
  mainStage.setScene(loginScene);
  mainStage.show();
  mainStage.setHeight(740);
  mainStage.setWidth(1000);
  mainStage.setResizable(true);
  mainStage.centerOnScreen();
  userController user = new userController();
  String name = user.loggedInUser.getName();
  mainStage.setTitle("Welcome back to the main menu dear " + name );
  
}


}

