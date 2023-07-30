package application.windows;

import java.io.IOException;
import java.lang.reflect.Field;

import application.Main;
import application.resources.back.Rule;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ModifyRuleWindow extends AddRuleWindow{
	
	private MainWindow parent;
	
	public ModifyRuleWindow(MainWindow parent) throws IllegalArgumentException, IllegalAccessException, IOException {
		super(parent);
		this.parent = parent;
		
		this.setTitle("Modify Rule");
		
		this.getSelectedContent();
	
		this.modifyContent();
	}

	private void getSelectedContent() {
		Rule r = (Rule) this.parent.getLRules().getSelectionModel().getSelectedItem();
		
		this.getFieldExtension().setText(r.getExtension());
		this.getComboBoxOperation().getSelectionModel().select(r.getOperation());
		this.getFieldPath().setText(r.getPath());
	}

	private void modifyContent() {
		
		this.getLabelTitle().setText("Modify Rule");
		
		this.getButtonSave().setText("Modify");
		
		this.disableFieldExtension();
	}
	
	@Override
	public void clickButtonSave(ActionEvent e) {
		
		parent.clickButtonDelete(e);
		
		super.clickButtonSave(e);
	}
}
