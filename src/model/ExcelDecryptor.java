package model;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.poi.poifs.crypt.Decryptor;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class ExcelDecryptor implements Callable<String>{
	private static boolean encrypted = true;
	private static File encryptedFile = null;
	private static int processors = 1;
	
	private int processorNumber;
	private int passwordLength;
	private char[] password;
	private boolean numbers;
	private boolean characters;
	private boolean symbols;
	private Character[] characterRange; //caracteres permitidos para gerar uma senha
	
	public ExcelDecryptor(int passwordLength, int processorNumber) {
		this.passwordLength = passwordLength;
		this.processorNumber = processorNumber;
	}
	
	public void setNumbers(boolean number) {
		this.numbers = number;
	}

	public void setCharacters(boolean character) {
		this.characters = character;
	}

	public void setSymbols(boolean symbols) {
		this.symbols = symbols;
	}
	
	public static void setEncryptedFile(File encryptedFile) {
		ExcelDecryptor.encryptedFile = encryptedFile;
	}
	
	public static void setEncrypted(boolean encrypted) {
		ExcelDecryptor.encrypted = encrypted;
	}
	
	public static void setProcessors(int processors) {
		ExcelDecryptor.processors = processors;
	}

	public static int getProcessors() {
		return processors;
	}

	private String concatenatePassword() {
		return new String(password);
	}	
	
	private void initializeCharacterRange() {
		List<Character> allowedCharacters = new ArrayList<>();
		if(numbers) {
			for(int i = 48; i <= 57; ++i) {
				allowedCharacters.add((char) i);
			}
		}
		if(characters) {
			for(int i = 97; i <= 122; ++i) {
				allowedCharacters.add((char) i);
			}
		}
		if(symbols) {
			allowedCharacters.addAll(List.of('!', '@', '#', '$', '%', '&', '*', '-', '+', '?', '_', ' '));
		}
		characterRange = new Character[allowedCharacters.size()];
		allowedCharacters.toArray(characterRange);
	}
	
	private void initializePasswordArray() {
		password = new char[passwordLength];
		for(int i = 0; i < passwordLength; ++i) {
			password[i] = characterRange[0];
		}
	}
	
	private int[] initializeCountArray() {
		int[] count = new int[passwordLength];
		count[0] = processorNumber;
		for(int i = 1; i < passwordLength; ++i) {
			count[i] = 1;
		}
		return count;
	}
	
	//incremente o contador atual e limpe os anteriores
	private void adjustCountArray(int[] count, int currentPosition){
		++count[currentPosition];
		password[0] = characterRange[processorNumber];
		for(int j = 1; j < currentPosition; ++j) {
			count[j] = 0;
			password[j] = characterRange[count[j]];
			++count[j];
		}
	}

	@Override
	public String call() {
		initializeCharacterRange();
		try (POIFSFileSystem poiFileSystem = new POIFSFileSystem(encryptedFile)) {
			EncryptionInfo encryptionInfo = new EncryptionInfo(poiFileSystem);
			Decryptor decryptor = Decryptor.getInstance(encryptionInfo);

			while(encrypted) {
				initializePasswordArray();
				int[] count = initializeCountArray();
				for(int i = 0; i < passwordLength; ++i) {
					while(encrypted && count[i] < characterRange.length) {
						password[i] = characterRange[count[i]];
						count[0] = count[0] + processors;

						if(i > 0) {
							adjustCountArray(count, i);
							i = 0;
						}

						System.out.println("p" + processorNumber + ":" + concatenatePassword());
						if(decryptor.verifyPassword(concatenatePassword())) {
							return concatenatePassword();
						}
					}
					count[0] = processorNumber;
				}
				++passwordLength;
			}
		} catch (IOException e) {
			System.out.println("IOException: " + e.getMessage());
		} catch (GeneralSecurityException e) {
			System.out.println("GeneralSecurityException: " + e.getMessage());
		} catch(Exception e) {
			System.out.println("Exception: " + e.getMessage());
		}
		return null;
	}
}
