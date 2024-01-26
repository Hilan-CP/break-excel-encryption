package gui;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import model.BreakEncryption;

public class ApplicationController implements Initializable {
	
	private BreakEncryption breakEncryption;
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
    private Spinner<Integer> digitsCountSpinner;

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
    	/*
    	 * to do
    	 * verificar se algum checkbox esta selecionado
    	 * isCheckBoxSelected
    	 * lançar exceção
    	 */
    	
    	breakEncryption = new BreakEncryption(digitsCountSpinner.getValue());
    	breakEncryption.setNumbers(numberCheckBox.isSelected());
    	breakEncryption.setCharacters(characterCheckBox.isSelected());
    	breakEncryption.setSymbols(symbolsCheckbox.isSelected());
    	BreakEncryption.setFile(file);
    	
    	Thread thread = new Thread(breakEncryption);
    	thread.start();
    	
    	/*
    	 * to do
    	 * finalizar thread quando a janela for fechada
    	 * utilizar variavel encrypted para finalizar thread
    	 */
    }
    
    private boolean isCheckBoxSelected() {
    	return numberCheckBox.isSelected() && characterCheckBox.isSelected() && symbolsCheckbox.isSelected();
    }

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeSpinner();
	}
	
	private void initializeSpinner() {
		SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 10);
		digitsCountSpinner.setValueFactory(valueFactory);
	}
}
