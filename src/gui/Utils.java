package gui;

import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.scene.Node;

public class Utils {
	public static Stage getElementStage(ActionEvent event) {
		return (Stage) ((Node) event.getSource()).getScene().getWindow();
	}
}
