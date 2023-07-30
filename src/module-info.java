module DownloadsManager {
	requires javafx.controls;
	requires javafx.graphics;
	requires javafx.fxml;
	requires java.sql;
	requires javafx.base;
	requires jython.standalone;
	
	opens application to javafx.graphics, javafx.fxml;
	opens application.resources.ui to javafx.fxml;
	opens application.resources.back to javafx.base;
}
