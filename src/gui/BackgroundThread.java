package gui;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import model.ExcelDecryptor;

//permite executar tarefas sem bloquear a interface gráfica
public class BackgroundThread implements Runnable{
	
	private ExecutorService executor;
	private ExcelDecryptor[] decryptors;
	
	public BackgroundThread(ExcelDecryptor[] decryptors) {
		this.decryptors = decryptors;
	}

	@Override
	public void run() {
    	executor = Executors.newFixedThreadPool(ExcelDecryptor.getProcessors());
		try {
    		LocalDateTime beginingTime = LocalDateTime.now();
			String password = executor.invokeAny(List.of(decryptors));
			LocalDateTime endingTime = LocalDateTime.now();
			executor.shutdown();
			
			if(password != null) {
				System.out.println("Senha encontrada: " + password);
				System.out.println("Início: " + beginingTime);
				System.out.println("Fim: " + endingTime);
			}
		} catch (InterruptedException | ExecutionException e) {
			System.out.println(e.getMessage());
		} finally {
			terminate();
		}
	}
	
	public void terminate() {
		ExcelDecryptor.setEncrypted(false);
		if(!executor.isShutdown()) {
			executor.shutdownNow();
		}
	}
}
