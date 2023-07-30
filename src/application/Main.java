package application;
	
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import application.resources.back.Rule;
import application.windows.MainWindow;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TextField;


public class Main extends Application {
	
	public static final String PYTHON_LOCATION = System.getenv("LOCALAPPDATA") + "\\Downloads Manager\\clear_downloads.py";
	
	/*****************************************************************/
	/*Main methods*/
	@Override
	public void start(Stage stage) throws Exception {
		stage = new MainWindow();
		stage.show();
	}
	
	public static void main(String[] args) {
		
		new File(Rule.CSV_LOCATION).getParentFile().mkdirs();
		launch(args);
	}
	
	/*****************************************************************/
	/*Other Methods*/
	
	public static ArrayList<Node> getAllNodes(Parent root) {
	    ArrayList<Node> nodes = new ArrayList<Node>();
	    addAllDescendents(root, nodes);
	    return nodes;
	}

	private static void addAllDescendents(Parent parent, ArrayList<Node> nodes) {
	    for (Node node : parent.getChildrenUnmodifiable()) {
	        nodes.add(node);
	        if (node instanceof Parent)
	            addAllDescendents((Parent)node, nodes);
	    }
	}
}
