package gui;

import java.io.File;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import model.BreakEncryption;

public class ApplicationController {
	
	private BreakEncryption breakEncryption = new BreakEncryption();
	private File file;

    @FXML
    private TextField fileTextField;

    @FXML
    private CheckBox numberCheckBox;
    
    @FXML
    private CheckBox characterCheckBox;

    @FXML
    private CheckBox symbolsCheckbox;
    
    @FXML
    private CheckBox allCharactersCheckBox;

    @FXML
    public void browseButtonAction(ActionEvent event) {
    	FileChooser fileChooser = new FileChooser();
    	fileChooser.setTitle("Selecione o arquivo");
    	
    	file = fileChooser.showOpenDialog(Utils.getElementStage(event));
    	if(file != null) {
    		fileTextField.setText(file.getAbsolutePath());
    	}
    }

    @FXML
    public void executeButtonAction(ActionEvent event) {
    	breakEncryption.setNumber(numberCheckBox.isSelected());
    	breakEncryption.setCharacter(characterCheckBox.isSelected());
    	breakEncryption.setSymbols(symbolsCheckbox.isSelected());
    	breakEncryption.setAllCharacters(allCharactersCheckBox.isSelected());
    	breakEncryption.decrypt(file);
    }
}
