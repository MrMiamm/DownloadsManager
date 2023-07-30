package application.resources.ui;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class RuleWindowController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button b_cancel;

    @FXML
    private Button b_path;

    @FXML
    private Button b_save;

    @FXML
    private ComboBox<?> c_operation;

    @FXML
    private TextField f_extension;

    @FXML
    private TextField f_path;

    @FXML
    private Label l_title;

    @FXML
    void initialize() {
        assert b_cancel != null : "fx:id=\"b_cancel\" was not injected: check your FXML file 'RuleWindow.fxml'.";
        assert b_path != null : "fx:id=\"b_path\" was not injected: check your FXML file 'RuleWindow.fxml'.";
        assert b_save != null : "fx:id=\"b_save\" was not injected: check your FXML file 'RuleWindow.fxml'.";
        assert c_operation != null : "fx:id=\"c_operation\" was not injected: check your FXML file 'RuleWindow.fxml'.";
        assert f_extension != null : "fx:id=\"f_extension\" was not injected: check your FXML file 'RuleWindow.fxml'.";
        assert f_path != null : "fx:id=\"f_path\" was not injected: check your FXML file 'RuleWindow.fxml'.";
        assert l_title != null : "fx:id=\"l_title\" was not injected: check your FXML file 'RuleWindow.fxml'.";

    }

}
