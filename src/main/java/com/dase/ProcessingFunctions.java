package com.dase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ProcessingFunctions {
	static final List<String> stopWords = new ArrayList<String>(Arrays.asList("the"));
	

	public static int nextPrime(int n) {
		boolean isPrime = false;
		
		int m = n;
		while(!isPrime) {
			isPrime = isPrime(++m);
		}	
		return(m);
	}
	

	public static boolean isPrime(int n) {
		if(n == 1) return(false);
		else if(n == 2 || n == 3) return(true);
		else if(n % 2 == 0 || n % 3 == 0) return(false);
		else {
			for(int i = 5; i*i < n + 1; i += 6) {
				if(n % i == 0 || n % (i + 2) == 0) {
					return(false);
				}
			}
			return(true);
		}
	}
	

	public static boolean isStopWord(String s) {
		if(stopWords.contains(s) || s.length() < 3) return(true);
		return(false);
	}
	

	public static int numUnique(File folder) throws IOException {
		Set<String> s = new HashSet<String>();
		File[] contents = folder.listFiles();
		
		FileReader fr;
		BufferedReader b;
		for(int i = 0; i < contents.length; i++) { //iterate through the documents
			if(contents[i].isFile()) {
				fr = new FileReader(contents[i]);
				b = new BufferedReader(fr);
				
				String line;
				String[] words;
				while((line = b.readLine()) != null) { //iterate through lines
					words = line.replaceAll("[.,:;']", "").toLowerCase().split("\\s+"); //remove punctuation
					for(int j = 0; j < words.length; j++) { //iterate through words
						if(!isStopWord(words[j])) s.add(words[j]);
					}
				}
				b.close();
			}
		}
		return(s.size());
	}
	

	public static Set<String> UniqueWordList(String fileName) throws IOException {
		FileReader fr = new FileReader(fileName);
		BufferedReader b = new BufferedReader(fr);
		Set<String> s = new HashSet<String>();
		
		String line;
		String[] words;
		while((line = b.readLine()) != null) {
			words = line.replaceAll("[.,:;']", "").toLowerCase().split("\\s+");
			for(int i = 0; i < words.length; i++) {
				if(!isStopWord(words[i])) s.add(words[i]);
			}
		}
		b.close();
		
		return(s);
	}
}
