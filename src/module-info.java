module BreakExcelEncryption {
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.graphics;
	requires org.apache.poi.poi;
	
	opens application to javafx.graphics, javafx.fxml;
	opens gui to javafx.fxml;
}
