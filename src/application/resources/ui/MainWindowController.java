package application.resources.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableView;

public class MainWindowController {

    @FXML
    private Button b_add;

    @FXML
    private Button b_clean;

    @FXML
    private Button b_delete;

    @FXML
    private Button b_delete_all;

    @FXML
    private Button b_modify;

    @FXML
    private CheckBox checkb_delete_everything;

    @FXML
    private CheckBox checkb_delete_folders;

    @FXML
    private TableView<?> l_rules;

}
