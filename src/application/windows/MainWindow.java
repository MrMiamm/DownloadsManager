package application.windows;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.python.util.PythonInterpreter;

import application.Main;
import application.resources.back.Rule;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class MainWindow extends Stage{
	
	//Window Size
	private static final int WIDTH = 600;
	private static final int HEIGHT = 450;
	
	private TableView<Rule> l_rules;
	private Button b_add;
	private Button b_modify;
	private Button b_delete;
	private Button b_delete_all;
	private Button b_clean;
	private CheckBox checkb_delete_everything;
	private CheckBox checkb_delete_folders;
	
	public MainWindow () throws Exception{
		this.setTitle("Downloads Manager"); 
		
		this.setMinWidth(WIDTH);
		this.setMinHeight(HEIGHT);
		this.setWidth(WIDTH);
		this.setHeight(HEIGHT);
		
		//Loads FXML file
		this.setScene(new Scene(FXMLLoader.load(getClass().getResource("/application/resources/ui/MainWindow.fxml"))));
		
		//Get content from FXML file
		this.linkContent();
		
		this.setContent();
	}
	
	public TableView getLRules() {
		return l_rules;
	}

	private void linkContent() throws IllegalArgumentException, IllegalAccessException {
		// Get all IDs from the FXML file and set it in Stage variables
		
		for (Node n : Main.getAllNodes(this.getScene().getRoot())) {
			
			for (Field f : MainWindow.class.getDeclaredFields()) {

				if ((n.getId() != null) && (n.getId().equals(f.getName()))) {
					f.set(this, n);
				} 
			}
		}
	}

	private void setContent(){
		//Defines actions / style / ... of each element
		
		BooleanBinding is_selection_empty = Bindings.equal(this.l_rules.getSelectionModel().selectedIndexProperty(), -1);
		
		TableColumn<Rule, String> extension_column = new TableColumn<>("Extension");
		extension_column.setCellValueFactory(new PropertyValueFactory<>("extension"));

		TableColumn<Rule, String> operation_column = new TableColumn<>("Operation");
		operation_column.setCellValueFactory(new PropertyValueFactory<>("operation"));
		
		TableColumn<Rule, String> path_column = new TableColumn<>("Path");
		path_column.setCellValueFactory(new PropertyValueFactory<>("path"));

		this.l_rules.getColumns().add(extension_column);
		this.l_rules.getColumns().add(operation_column);
		this.l_rules.getColumns().add(path_column);
		this.l_rules.setItems(FXCollections.observableArrayList(Rule.loadAll()));
		
		this.b_add.setOnAction(e -> {
			try {
				new AddRuleWindow(this).show();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		
		this.b_modify.disableProperty().bind(is_selection_empty);
		this.b_modify.setOnAction(e -> {
			try {
				new ModifyRuleWindow(this).show();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		
		this.b_delete.disableProperty().bind(is_selection_empty);
		this.b_delete.setOnAction(e -> clickButtonDelete(e));
		
		this.testSetDisableButtonDeleteAll();
		this.b_delete_all.setOnAction(e -> clickButtonDeleteAll(e));
		
		this.b_clean.setOnAction(e -> clickButtonClean());
	}
	
	private void clickButtonClean() {
		
		String path = System.getenv("LOCALAPPDATA") + "\\Downloads Manager\\clear_downloads.py";
		Thread t = new Thread(() -> {
	        
			if (new File(path).exists() == false) {
				this.createPythonProgram(path);
			} 
			Boolean error = this.executePythonProgram(path);

	        Platform.runLater(() -> {
	            Alert a = null;
	            if (error) {
	            	a = new Alert(Alert.AlertType.ERROR);
	        		a.setTitle("Error");
	        		a.setContentText("No rules in the list !"); 	
	            } else {
	            	a = new Alert(Alert.AlertType.INFORMATION);
	        		a.setTitle("Success");
	        		a.setContentText("The Downloads folder has been cleaned up !"); 	
	            }
	            
	            a.showAndWait();
	        });
	    });
		t.start();
	}
	
	private boolean executePythonProgram(String path) {
		//Return true if it cannot get rules.csv
		
	    PythonInterpreter interpreter = new PythonInterpreter();
	    try {
	    	interpreter.execfile(path);
	    } catch (Exception e) {
	    	return true;
	    }
	    return false;
	}
	
	private void createPythonProgram(String path) {
		String program = "##########################################################################################\r\n"
				+ "#Libraries#\r\n"
				+ "##########################################################################################\r\n"
				+ "\r\n"
				+ "import os\r\n"
				+ "from shutil import move\r\n"
				+ "import csv\r\n"
				+ "\r\n"
				+ "##########################################################################################\r\n"
				+ "#Constants#\r\n"
				+ "##########################################################################################\r\n"
				+ "\r\n"
				+ "DOWNLOADS_FOLDER = os.path.join(os.path.expanduser(\"~\"), \"Downloads\")\r\n"
				+ "DOWNLADS_CONTENT = os.listdir(DOWNLOADS_FOLDER)\r\n"
				+ "\r\n"
				+ "##########################################################################################\r\n"
				+ "#Functions#\r\n"
				+ "##########################################################################################\r\n"
				+ "def loadRulesFromCSV():\r\n"
				+ "    rules = []\r\n"
				+ "    with open(os.environ['LOCALAPPDATA'] + \"\\\\Downloads Manager\\\\\" + \"rules.csv\", \"r\") as csvfile:\r\n"
				+ "        reader = csv.reader(csvfile)\r\n"
				+ "        for row in reader:\r\n"
				+ "            extension = row[0]\r\n"
				+ "            operation = row[1]\r\n"
				+ "            path = row[2]\r\n"
				+ "            rule = (extension, operation, path)\r\n"
				+ "            rules.append(rule)\r\n"
				+ "    return rules\r\n"
				+ "\r\n"
				+ "def deleteFilesWithExtensions(extensions):\r\n"
				+ "\r\n"
				+ "    for file in DOWNLADS_CONTENT:\r\n"
				+ "        if (os.path.splitext(file)[1] in extensions):\r\n"
				+ "            os.remove(DOWNLOADS_FOLDER + \"\\\\\" + file)\r\n"
				+ "        \r\n"
				+ "def moveFilesWithExtensions(extensions):\r\n"
				+ "    \r\n"
				+ "    for file in DOWNLADS_CONTENT:\r\n"
				+ "        ext = os.path.splitext(file)[1]\r\n"
				+ "        if (ext in extensions[0]):\r\n"
				+ "            move(DOWNLOADS_FOLDER + \"\\\\\" + file, extensions[1][extensions[0].index(ext)])\r\n"
				+ "        \r\n"
				+ "##########################################################################################\r\n"
				+ "#Main#\r\n"
				+ "##########################################################################################\r\n"
				+ "\r\n"
				+ "list_extensions_to_delete = []\r\n"
				+ "list_extensions_to_move = [[],[]] #Right list: extension, left list: path\r\n"
				+ "\r\n"
				+ "# Get rules from database\r\n"
				+ "rules = loadRulesFromCSV()\r\n"
				+ "\r\n"
				+ "# Add in lists rules\r\n"
				+ "for rule in rules:\r\n"
				+ "\r\n"
				+ "    if (rule[1].lower() == \"delete\"):\r\n"
				+ "        list_extensions_to_delete.append(rule[0])\r\n"
				+ "    else:\r\n"
				+ "        list_extensions_to_move[0].append(rule[0])\r\n"
				+ "        list_extensions_to_move[1].append(rule[2])\r\n"
				+ "        \r\n"
				+ "deleteFilesWithExtensions(list_extensions_to_delete)\r\n"
				+ "moveFilesWithExtensions(list_extensions_to_move)";
		
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(path));
            writer.write(program);
            writer.close();
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}

	private void clickButtonDeleteAll(ActionEvent e) {
		
		Alert a = new Alert(Alert.AlertType.CONFIRMATION);
		a.setTitle("Delete All");
		a.setContentText("Are you sure you want to delete all rules ?"); 
		
		a.showAndWait();
		if (a.getResult() == ButtonType.OK) {
			new File(Rule.FILE_LOCATION).delete();
			new File(Rule.CSV_LOCATION).delete();
			
			this.l_rules.getItems().clear();
			
			this.testSetDisableButtonDeleteAll();
		}
	}

	public void testSetDisableButtonDeleteAll() {
		if (this.l_rules.getItems().isEmpty()) {
			this.b_delete_all.setDisable(true);
		} else {
			this.b_delete_all.setDisable(false);
		}
	}

	public void clickButtonDelete(ActionEvent e) {
		
		new File(Rule.FILE_LOCATION).delete();
		new File(Rule.CSV_LOCATION).delete();
		
		for (Rule r : this.l_rules.getItems()) {
			if (!r.equals(this.l_rules.getSelectionModel().getSelectedItem())) {
				r.saveCSV();
				r.save();
			}
		}
		
		this.l_rules.setItems(FXCollections.observableArrayList(Rule.loadAll()));
		this.testSetDisableButtonDeleteAll();
	}
}
