package application.windows;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import application.Main;
import application.resources.back.Rule;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class AddRuleWindow extends Stage{
	
	//Window Size
	private static final int WIDTH = 370;
	private static final int HEIGHT = 360;
	
	private String error_style = "-fx-background-color: #fff; -fx-border-color: red; -fx-border-width: 1px; -fx-border-radius: 5px; -fx-padding: 5px;";
	private String field_style = "-fx-background-color: #fff; -fx-border-color: #a0a0a0; -fx-border-width: 1px; -fx-border-radius: 5px; -fx-padding: 5px;";
	private String locked_field_style = "-fx-background-color: #f2f2f2; -fx-border-color: #a0a0a0; -fx-border-width: 1px; -fx-border-radius: 5px; -fx-padding: 5px;";
	
	private Label l_title;
	private TextField f_extension;
	private ComboBox<String> c_operation;
	private Button b_path;
	private TextField f_path;
	private Button b_save;
	private Button b_cancel;
	
	private boolean error_extension = true;
	private boolean error_path = true;
	
	private MainWindow parent;

	public AddRuleWindow (MainWindow parent) throws IOException, IllegalArgumentException, IllegalAccessException {
		this.parent = parent;
		
		this.setTitle("Add New Rule");
		
		this.setMinWidth(WIDTH);
		this.setMinHeight(HEIGHT);
		this.setWidth(WIDTH);
		this.setHeight(HEIGHT);
		
		//Loads FXML file
		this.setScene(new Scene(FXMLLoader.load(getClass().getResource("/application/resources/ui/RuleWindow.fxml"))));
		
		//Get content from FXML file
		this.linkContent();
		
		this.setContent();
	}
	
	private void linkContent() throws IllegalArgumentException, IllegalAccessException {
		// Get all IDs from the FXML file and set it in Stage variables
		
		for (Node n : Main.getAllNodes(this.getScene().getRoot())) {
			
			for (Field f : AddRuleWindow.class.getDeclaredFields()) {

				if ((n.getId() != null) && (n.getId().equals(f.getName()))) {
					f.set(this, n);
				} 
			}
		}
	}

	public void setContent() {
		
		initializeBindings();
		
		/*******************************************************************/
		/*Field Extension*/
		
		this.f_extension.setStyle(field_style);
		this.f_extension.setText(".");
		this.f_extension.setOnKeyTyped(e -> this.fieldExtensionKeyTyped(e));
		
		/*******************************************************************/
		/*ComboBox Operation*/
		
		this.c_operation.setItems(FXCollections.observableArrayList("Move","Delete"));
		this.c_operation.getSelectionModel().select(0);;
		this.c_operation.setOnAction(e -> ActionComboBoxOperation(e));
		
		/*******************************************************************/
		/*Field Path*/
		
		this.b_path.setOnAction(e -> clickButtonPath(e));
		this.f_path.setStyle(locked_field_style);
		this.f_path.setEditable(false);
		
		/*******************************************************************/
		/*Button Save*/
		
		this.b_save.setOnAction(e -> clickButtonSave(e));
		
		/*******************************************************************/
		/*Button Cancel*/
		
		this.b_cancel.setOnAction(e -> this.close());
	}
	
	private void initializeBindings() {

	    BooleanBinding save_button_disabled = new BooleanBinding() {
	        {
	            super.bind(c_operation.getSelectionModel().selectedIndexProperty(), f_extension.textProperty(), f_path.textProperty());
	        }

	        @Override
	        protected boolean computeValue() {
	            int index = c_operation.getSelectionModel().getSelectedIndex();

	            if (index == 0) {
	            	// When operation is moving
	                return f_extension.getText().isEmpty() || f_extension.getText().equals(".") || f_path.getText().isEmpty();
	            } else if (index == 1) {
	                // When operation is deleting
	                return f_extension.getText().isEmpty() || f_extension.getText().equals(".");
	            }
	            else return true;
	        }
	    };

	    b_save.disableProperty().bind(save_button_disabled);
	}

	
	public Label getLabelTitle() {
		return l_title;
	}

	public Button getButtonSave() {
		return b_save;
	}

	public boolean isErrorExtension() {
		return error_extension;
	}

	public boolean isErrorPath() {
		return error_path;
	}
	
	public void disableFieldExtension() {
		this.f_extension.setStyle(locked_field_style);
		this.f_extension.setEditable(false);
		this.f_extension.setOpacity(0.5);
	}

	public TextField getFieldExtension() {
		return f_extension;
	}

	public ComboBox<String> getComboBoxOperation() {
		return c_operation;
	}

	public TextField getFieldPath() {
		return f_path;
	}

	public void clickButtonSave(ActionEvent e) {
		
		Rule r = new Rule(this.f_extension.getText(), this.c_operation.getSelectionModel().getSelectedItem(), this.f_path.getText());
		r.save();
		r.saveCSV();
		System.out.println(r);
		
		this.parent.getLRules().setItems(FXCollections.observableArrayList(Rule.loadAll()));
		this.parent.getLRules().refresh();
		this.parent.testSetDisableButtonDeleteAll();
		
		this.close();
	}

	private void ActionComboBoxOperation(ActionEvent e) {
		
		if (this.c_operation.getSelectionModel().getSelectedIndex() == 1) {
			this.b_path.setDisable(true);
			this.f_path.setText("");
		} else {
			this.b_path.setDisable(false);
			this.error_path = true;
		}
	}

	private void fieldExtensionKeyTyped(KeyEvent e) {
		
		String[] illegal_chars = {"/","\\","*",":","?","\"","<",">","|","."," "};
		
		if (this.f_extension.getText().isEmpty() || this.f_extension.getText().charAt(0) != '.') {
			this.f_extension.setText("." + this.f_extension.getText());
			this.f_extension.positionCaret(this.f_extension.getText().length());
		} else {
			for (int i = 0; i < illegal_chars.length; i++) {
				if (this.replaceStringTextField(f_extension, illegal_chars[i], "")) {
					break;
				}
			}
		}
		
		this.error_extension = true;
		if (!this.f_extension.getText().equals(".")) {
			this.error_extension = false;
		}
	}
	
	private static boolean replaceStringTextField(TextField t, String d, String a) {
		
		String text = t.getText().substring(1);
		if (text.contains(d)) {
			
			int index_float = text.indexOf(d);
			t.setText("." + text.replace(d, a));
			t.positionCaret(index_float + 1);
			return true;
		}
		return false;
	}

	private void clickButtonPath(ActionEvent e) {
		DirectoryChooser dc = new DirectoryChooser();
		File location = dc.showDialog(this);
		
		this.error_path = true;
		if (location != null) {
			this.f_path.setText(location.getPath());
			this.error_path = false;
		}
	}
}
