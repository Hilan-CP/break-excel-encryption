package gui;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import model.BreakEncryption;

public class BackgroundThread implements Runnable{
	
	private ExecutorService executor;
	private BreakEncryption[] breakEncryption;
	
	public BackgroundThread(BreakEncryption[] breakEncryption) {
		this.breakEncryption = breakEncryption;
	}

	@Override
	public void run() {
    	executor = Executors.newFixedThreadPool(BreakEncryption.getProcessors());
    	
		try {
    		LocalDateTime inicio = LocalDateTime.now();
			String password = executor.invokeAny(List.of(breakEncryption));
			LocalDateTime fim = LocalDateTime.now();
			executor.shutdown();
			
			if(password != null) {
				System.out.println("Senha encontrada: " + password);
				System.out.println(inicio);
				System.out.println(fim);
			}
		} catch (InterruptedException | ExecutionException e) {
			System.out.println("Exceção de threads: " + e.getMessage());
		} finally {
			terminate();
		}
	}
	
	public void terminate() {
		BreakEncryption.setEncrypted(false);
		
		if(!executor.isShutdown()) {
			executor.shutdownNow();
		}
	}
}
