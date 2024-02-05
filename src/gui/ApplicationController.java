package gui;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.BreakEncryption;

public class ApplicationController implements Initializable {
	
	private BreakEncryption[] breakEncryption;
	private BackgroundThread bgt;
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
    	Stage currentStage = Utils.getElementStage(event);
    	currentStage.setOnCloseRequest(e -> bgt.terminate());
    	
    	if(file != null && isChecked()) {
    		int processors = Runtime.getRuntime().availableProcessors();
        	if(processors > 10) {
        		processors = 10;
        	}
        	
        	breakEncryption = new BreakEncryption[processors];
        	BreakEncryption.setFile(file);
        	BreakEncryption.setProcessors(processors);
        	
        	for(int p = 0; p < processors; ++p) {
        		breakEncryption[p] = new BreakEncryption(digitsCountSpinner.getValue(), p);
        		breakEncryption[p].setNumbers(numberCheckBox.isSelected());
        		breakEncryption[p].setCharacters(characterCheckBox.isSelected());
        		breakEncryption[p].setSymbols(symbolsCheckbox.isSelected());
        	}
    		
        	bgt = new BackgroundThread(breakEncryption);
        	Thread background = new Thread(bgt);
        	background.setName("Background");
        	background.setDaemon(true);
        	background.start();
    	}
    	else {
    		Utils.showAlert("Atenção!", "Escolha um arquivo e marque uma opção", AlertType.WARNING);
    	}
    }
    
    private boolean isChecked() {
    	return numberCheckBox.isSelected() || characterCheckBox.isSelected() || symbolsCheckbox.isSelected();
    }

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeSpinner();
		fileTextField.setEditable(false);
	}
	
	private void initializeSpinner() {
		SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 10);
		digitsCountSpinner.setValueFactory(valueFactory);
	}
}
