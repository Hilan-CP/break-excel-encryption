package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import model.BreakEncryption;

public class ApplicationController {
	
	private BreakEncryption breakEncryption = new BreakEncryption();

    @FXML
    private Button browseButton;

    @FXML
    private Button executeButton;

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

    }

    @FXML
    public void executeButtonAction(ActionEvent event) {
    	breakEncryption.setNumber(numberCheckBox.isSelected());
    	breakEncryption.setCharacter(characterCheckBox.isSelected());
    	breakEncryption.setSymbols(symbolsCheckbox.isSelected());
    	breakEncryption.setAllCharacters(allCharactersCheckBox.isSelected());
    	breakEncryption.decrypt(null);
    }
}
