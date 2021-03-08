package application;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.util.List;

import config.DBController;
import model.Item;
import model.ItemSearchAttribute;

/**
 * This class is responsible for handling Student View events.
 * 
 * @author Nikita Mezhenskyi
 * @version 2.0.0, 2021-03-07
 */
public class StudentViewController {
	@FXML
	private TextField searchField;
	@FXML
	private Label feedback;
	@FXML
	private ComboBox<String> parameterSelector;
	@FXML
	private TableView<Item> dataTable;
	@FXML
	private TableColumn<Item, String> itemId;
	@FXML
	private TableColumn<Item, String> itemTitle;
	@FXML
	private TableColumn<Item, String> itemAuthor;
	@FXML
	private TableColumn<Item, String> itemCategory;
	@FXML
	private TableColumn<Item, String> itemStatus;
	@FXML
	private TableColumn<Item, String> itemType;
	
	private Stage auxiliaryStage = null;
	private Scene currentScene = null;
	private ObservableList<Item> itemList = FXCollections.observableArrayList();
	private DBController db = new DBController();
	
	/**
	 * Creates an instance of StudentView class.
	 */
	public StudentViewController() { }
	
	/**
	 * Handles search <i>Item</i> event. Displays <i>Items</i> from the database based on search parameters.
	 * @param event
	 */
	public void handleSearch(ActionEvent event) {
		String selectedParameter = parameterSelector.getSelectionModel().getSelectedItem();
		
		if (selectedParameter != null) {
			if (!searchField.getText().isBlank()) {
				switch(selectedParameter) {
					case "by Title":
						displayItems(db.getItemsByAttribute(ItemSearchAttribute.TITLE, searchField.getText()));
						break;
					case "by Author":
						displayItems(db.getItemsByAttribute(ItemSearchAttribute.AUTHOR, searchField.getText()));
						break;
					case "by ID":
						displayItems(db.getItemsByAttribute(ItemSearchAttribute.ID, searchField.getText()));
						break;
				}
			}
			else {
				feedback.setTextFill(Color.web("#FF0000"));
				feedback.setText("Please enter a search string");
			}
		}
		else {
			feedback.setTextFill(Color.web("#FF0000"));
			feedback.setText("Please select a parameter");
		}
	}
	
	/**
	 * Handles enter key press while focusing on the text field.
	 * Calls handleSearch event.
	 */
	public void handleEnterPress(ActionEvent event) { handleSearch(event); }
	
	/**
	 * Clears the input field and removes all <i>Items</i> from the table.
	 * @param event
	 */
	public void handleClear(ActionEvent event) {
		clearTable();
		searchField.setText("");
	}
	
	/**
	 * Handles Log In button click event.
	 * Switches the scene to the Log In View.
	 * 
	 * @param event
	 */
	public void handleLogIn(ActionEvent event) {
		GridPane login = Login.loginMenu();
		
		if (currentScene != null) {
			// Switch the scene to the Log In View
			currentScene.setRoot(login);
			// Get the underlying stage and change its title
			((Stage) currentScene.getWindow()).setTitle("Library Management System");
			//((Stage) currentScene.getWindow()).setWidth(600);
			//((Stage) currentScene.getWindow()).setHeight(600);
		}
	}
	
	public void setCurrentScene(Scene currentScene) {
		this.currentScene = currentScene;
	}
	
	/**
	 * Handles view borrowed items event. Displays <i>Items</i> from the database that were borrowed.
	 * @param event
	 */
	public void viewBorrowedBtnClick(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("fxmlViews/BorrowedItemsView.fxml"));
			Parent root = loader.load();
			BorrowedItemsViewController viewCtrl = loader.getController();
			
			// Pass a reference to the BorrowedItemsViewController
			viewCtrl.setStudentView(this);
			
			auxiliaryStage = new Stage();
			auxiliaryStage.setResizable(false);
			auxiliaryStage.setTitle("View Borrowed Items");
			auxiliaryStage.setScene(new Scene(root));
			auxiliaryStage.show();
		}
		catch (IOException ex) { ex.printStackTrace(); }
	}
	
	/**
	 * Handles request an <i>Item</i> event.
	 * @param event
	 */
	public void handleRequestItem(ActionEvent event) {
		Item selectedItem = dataTable.getSelectionModel().getSelectedItem();
		if (selectedItem != null) {
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("fxmlViews/RequestItemView.fxml"));
				Parent root = loader.load();
				RequestItemViewController viewCtrl = loader.getController();
				
				// Pass selected item and feeback label to the RequestItemViewController
				viewCtrl.setStudentView(this);
				viewCtrl.setRequestData(selectedItem, feedback);
				
				auxiliaryStage = new Stage();
				auxiliaryStage.setResizable(false);
				auxiliaryStage.setTitle("Request an Item");
				auxiliaryStage.setScene(new Scene(root));
				auxiliaryStage.show();
			}
			catch (IOException ex) { ex.printStackTrace(); }
		}
		else {
			feedback.setTextFill(Color.web("#FF0000"));
			feedback.setText("Cannot make a request: item is not selected");
		}
	}
	
	/**
	 * Displays <i>Items</i> from the database in the table.
	 * @param value
	 */
	private void displayItems(List<Item> items) {
		clearTable();
		
		if (items != null && !items.isEmpty()) {
			for (Item item : items) itemList.add(item);
			
			itemId.setCellValueFactory(new PropertyValueFactory<>("Id"));
			itemTitle.setCellValueFactory(new PropertyValueFactory<>("Title"));
			itemAuthor.setCellValueFactory(new PropertyValueFactory<>("Author"));
			itemCategory.setCellValueFactory(new PropertyValueFactory<>("Category"));
			itemStatus.setCellValueFactory(new PropertyValueFactory<>("Status"));
			itemType.setCellValueFactory(new PropertyValueFactory<>("Type"));
			dataTable.setItems(itemList);
			
			feedback.setTextFill(Color.web("#006400"));
			feedback.setText("Found " + itemList.size() + (itemList.size() > 1 ? " entries" : " entry"));
		}
		else {
			feedback.setTextFill(Color.web("#FF0000"));
			feedback.setText("Nothing found");
		}
	}
	
	/**
	 * 
	 * @param studentId
	 */
	public void displayBorrowedItems(String studentId) {
		closeAuxiliaryStage();
		displayItems(db.getBorrowedItems(studentId, true));
		if (feedback.getText().contains("Found")) feedback.setText(feedback.getText() + ", report file generated");
	}
	
	/**
	 * Removes all <i>Items</i> from the table.
	 */
	private void clearTable() {
		feedback.setText("");
		dataTable.getItems().clear(); 
	}
	
	/**
	 * Closes auxiliary stage used by Student View Controller.
	 */
	public void closeAuxiliaryStage() {
		if (auxiliaryStage != null) auxiliaryStage.close();
	}
}
