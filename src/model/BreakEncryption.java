package model;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.poifs.crypt.Decryptor;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import gui.Utils;
import javafx.scene.control.Alert.AlertType;

public class BreakEncryption implements Runnable{
	private static boolean encrypted = true;
	private static File file = null;
	
	private int digitsCount;
	private char[] password;
	private boolean numbers;
	private boolean characters;
	private boolean symbols;
	private Character[] charRange; //caracteres permitidos para gerar uma senha
	
	public BreakEncryption(int digitsCount) {
		this.digitsCount = digitsCount;
		this.numbers = false;
		this.characters = false;
		this.symbols = false;
	}
	
	public boolean containsNumbers() {
		return numbers;
	}
	
	public void setNumbers(boolean number) {
		this.numbers = number;
	}
	
	public boolean containsCharacters() {
		return characters;
	}
	
	public void setCharacters(boolean character) {
		this.characters = character;
	}
	
	public boolean containsSymbols() {
		return symbols;
	}
	
	public void setSymbols(boolean symbols) {
		this.symbols = symbols;
	}
	
	public static void setFile(File file) {
		BreakEncryption.file = file;
	}
	
	private String concatPassword() {
		return new String(password);
	}
	
	private void initializeCharRange() {
		List<Character> list = new ArrayList<>();

		if(numbers) {
			for(int i = 48; i <= 57; ++i) {
				list.add((char) i);
			}
		}
		if(characters) {
			for(int i = 97; i <= 122; ++i) {
				list.add((char) i);
			}
		}
		if(symbols) {
			list.add('!');
			list.add('@');
			list.add('#');
			list.add('$');
			list.add('%');
			list.add('&');
			list.add('*');
			list.add('-');
			list.add('+');
			list.add('?');
			list.add(' ');
		}
		
		charRange = new Character[list.size()];
		list.toArray(charRange);
	}
	
	private void initializePasswordArray() {
		for(int i = 0; i < password.length; ++i) {
			password[i] = charRange[0];
		}
	}
	
	private void initializeCountArray(int[] count) {
		count[0] = 0;
		for(int i = 1; i < password.length; ++i) {
			count[i] = 1;
		}
	}

	@Override
	public void run() {
		initializeCharRange();
		LocalDateTime inicio = LocalDateTime.now();

		try {
			POIFSFileSystem poifs = new POIFSFileSystem(file);
			EncryptionInfo info = new EncryptionInfo(poifs);
			Decryptor decryptor = Decryptor.getInstance(info);

			String passwordString;
			int[] count;

			while(encrypted) {
				password = new char[digitsCount];
				count = new int[digitsCount];
				initializePasswordArray();
				initializeCountArray(count);
				int i = 0;
				while(i < digitsCount) {
					while(encrypted && count[i] < charRange.length) {
						password[i] = charRange[count[i]];
						++count[i];

						//apos trocar a posicao do digito da senha, limpe os contadores de digitos anteriores
						if(i > 0) {
							for(int j = 0; j < i; ++j) {
								count[j] = 0;
								password[j] = charRange[count[j]];
								++count[j];
							}
							i = 0;
						}

						passwordString = concatPassword();
						System.out.println(passwordString);
						if(decryptor.verifyPassword(passwordString)) {
							Utils.showAlert("Senha encontrada", "A senha é: " + passwordString, AlertType.INFORMATION);
							System.out.println(inicio); //inicio
							System.out.println(LocalDateTime.now()); //fim
							encrypted = false;
						}
					}
					++i;
				}
				++digitsCount;
			}

			poifs.close();

		} catch (IOException e) {
			Utils.showAlert("IOException", e.getMessage(), AlertType.ERROR);
		} catch (GeneralSecurityException e) {
			Utils.showAlert("GeneralSecurityException", e.getMessage(), AlertType.ERROR);
		}
	}
}
