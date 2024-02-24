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
import model.ExcelDecryptor;

public class ApplicationController implements Initializable {
	
	private BackgroundThread backgroundThread;
	private File chosenFile;

    @FXML
    private TextField fileTextField;

    @FXML
    private CheckBox numberCheckBox;
    
    @FXML
    private CheckBox characterCheckBox;

    @FXML
    private CheckBox symbolsCheckbox;
    
    @FXML
    private Spinner<Integer> passwordLengthSpinner;

    @FXML
    public void browseButtonAction(ActionEvent event) {
    	FileChooser fileChooser = new FileChooser();
    	fileChooser.setTitle("Selecione o arquivo");
    	chosenFile = fileChooser.showOpenDialog(Utils.getElementStage(event));
    	if(chosenFile != null) {
    		fileTextField.setText(chosenFile.getAbsolutePath());
    	}
    }

    @FXML
    public void executeButtonAction(ActionEvent event) {
    	if(chosenFile != null && areThereAnyCheckedBoxes()) {
    		int processors = defineMaximunProcessors();
        	ExcelDecryptor.setEncryptedFile(chosenFile);
        	ExcelDecryptor.setProcessors(processors);
        	ExcelDecryptor[] decryptors = createDecryptors(processors);
        	runDecryptorsOnBackground(decryptors);
    	}
    	else {
    		Utils.showAlert("Atenção!", "Escolha um arquivo e marque uma opção", AlertType.WARNING);
    	}
    }
    
    private boolean areThereAnyCheckedBoxes() {
    	return numberCheckBox.isSelected() || characterCheckBox.isSelected() || symbolsCheckbox.isSelected();
    }
    
    private int defineMaximunProcessors() {
    	// utiliza 3/4 dos processadores para não sobrecarregar o computador
    	// limite máximo de 10 processadores para evitar desperdício de CPU em senhas somente numérica
    	int processors = (int) (Runtime.getRuntime().availableProcessors() * 0.75);
    	if(processors > 10) {
    		processors = 10;
    	}
    	return processors;
    }
    
    private ExcelDecryptor[] createDecryptors(int processors) {
    	ExcelDecryptor[] decryptors = new ExcelDecryptor[processors];
    	for(int i = 0; i < processors; ++i) {
    		decryptors[i] = new ExcelDecryptor(passwordLengthSpinner.getValue(), i);
    		decryptors[i].setNumbers(numberCheckBox.isSelected());
    		decryptors[i].setCharacters(characterCheckBox.isSelected());
    		decryptors[i].setSymbols(symbolsCheckbox.isSelected());
    	}
    	return decryptors;
    }
    
    private void runDecryptorsOnBackground(ExcelDecryptor[] decryptors) {
    	backgroundThread = new BackgroundThread(decryptors);
    	Thread background = new Thread(backgroundThread);
    	background.setName("Background");
    	background.setDaemon(true);
    	background.start();
    }

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeSpinner();
		initializeTextField();
		addCloseOperation();
	}
	
	private void initializeSpinner() {
		SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 10);
		passwordLengthSpinner.setValueFactory(valueFactory);
	}
	
	private void initializeTextField() {
		fileTextField.setEditable(false);
	}
	
	private void addCloseOperation() {
		/*
		 * aguarda a GUI carregar e adiciona ao evento de fechamento da GUI
		 * uma operação que permite o encerramento das threads em segundo plano
		 */
		Thread finalizer = new Thread(() -> {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				System.out.println("Erro ao adicionar finalizador");
			}
			Stage stage = (Stage) fileTextField.getScene().getWindow();
			stage.setOnCloseRequest(e -> backgroundThread.terminate());
		});
		finalizer.start();
	}
}
