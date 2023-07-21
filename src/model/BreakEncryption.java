package model;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BreakEncryption {
	private int digitsCount;
	private char[] password;
	private boolean number;
	private boolean character;
	private boolean symbols;
	private boolean allCharacters;
	private Character[] charRange; //caracteres permitidos para gerar uma senha
	
	public BreakEncryption() {
		this.digitsCount = 1;
		this.number = false;
		this.character = false;
		this.symbols = false;
		this.allCharacters = false;
	}
	
	public boolean containsNumber() {
		return number;
	}
	
	public void setNumber(boolean number) {
		this.number = number;
	}
	
	public boolean containsCharacter() {
		return character;
	}
	
	public void setCharacter(boolean character) {
		this.character = character;
	}
	
	public boolean containsSymbols() {
		return symbols;
	}
	
	public void setSymbols(boolean symbols) {
		this.symbols = symbols;
	}
	
	public boolean containsAllCharacters() {
		return allCharacters;
	}
	
	public void setAllCharacters(boolean allCharacters) {
		this.allCharacters = allCharacters;
	}
	
	private String concatPassword() {
		return new String(password);
	}
	
	private void initializeCharRange() {
		List<Character> list = new ArrayList<>();
		
		if(allCharacters) {
			charRange = new Character[94];
			for(int i = 0; i < charRange.length; ++i) {
				charRange[i] = (char) (i + 32);
			}
		}
		else {
			if(number) {
				for(int i = 48; i <= 57; ++i) {
					list.add((char) i);
				}
			}
			
			if(character) {
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
	
	public void decrypt(File file) {
		initializeCharRange();
		
		//temp
		digitsCount = 4;
		
		int[] count;
		boolean encrypted = true;
		while(encrypted) {
			password = new char[digitsCount];
			count = new int[digitsCount];
			initializePasswordArray();
			initializeCountArray(count);
			int i = 0;
			while(i < digitsCount) {
				while(count[i] < charRange.length) {
					password[i] = charRange[count[i]];
					++count[i];
					if(i > 0) {
						for(int j = 0; j < i; ++j) {
							count[j] = 0;
							password[j] = charRange[count[j]];
							++count[j];
						}
						i = 0;
					}
					System.out.println(Arrays.toString(password));
				}
				++i;
			}
			//++digitsCount;
			
			//temp
			encrypted = false;
		}
	}

	@Override
	public String toString() {
		return "BreakEncryption [digitsCount=" + digitsCount + ", number=" + number + ", character=" + character
				+ ", symbols=" + symbols + ", allCharacters=" + allCharacters + ", charRange="
				+ Arrays.toString(charRange) + "]";
	}
}
