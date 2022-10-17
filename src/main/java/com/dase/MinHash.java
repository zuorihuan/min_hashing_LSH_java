package com.dase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class MinHash {
	File folder;
	int numPermutations;
	int numTerms;
	int mod; //p: ax + b % p
	List<Pair> AB; //a, b: ax + b % p


	public MinHash(String folder, int numPermutations) throws IOException {
		this.folder = new File(folder);
		this.numPermutations = numPermutations;
		
		numTerms = ProcessingFunctions.numUnique(this.folder);
		mod = ProcessingFunctions.nextPrime(numTerms);
		AB = generateCoefficients(mod);
	}
	

	public String[] allDocs() {
		return(folder.list());
	}
	

	// 计算Jaccard相似度
	public double exactJaccard(String file1, String file2) throws IOException {
		Set<String> words1 = ProcessingFunctions.UniqueWordList(folder + File.separator + file1);
		Set<String> words2 = ProcessingFunctions.UniqueWordList(folder + File.separator + file2);
		
		int a = words1.size();
		int b = words2.size();
		
		words1.retainAll(words2);
		int intersect = words1.size();
		
		return((double) intersect / (a + b - intersect));
	}
	
//	最小哈希签名
	public int[] minHashSig(String fileName) throws IOException {
		FileReader fr = new FileReader(folder + File.separator + fileName);
		BufferedReader b = new BufferedReader(fr);
		
		String line;
		String[] words;
		int hashVal;
		int[] minHashVals = new int[numPermutations];
		Arrays.fill(minHashVals, Integer.MAX_VALUE);
		while((line = b.readLine()) != null) { //iterate through lines
			words = line.replaceAll("[.,:;']", "").toLowerCase().split("\\s+"); //remove punctuation
	
			for(int j = 0; j < words.length; j++) { //iterate through words
				if(!ProcessingFunctions.isStopWord(words[j])) {
					for(int i = 0; i < numPermutations; i++) { //hash through k-functions
						hashVal = word2int(words[j], AB.get(i).a, AB.get(i).b, mod);
						if(hashVal < minHashVals[i]) minHashVals[i] = hashVal;
					}
				}
			}
		}
		b.close();
		
		return(minHashVals);
	}
	
// 	两个最小哈希签名的向量之间的Jaccard相似度
	public double approximateJaccard(String file1, String file2) throws IOException {
		int[] hash1 = minHashSig(file1);
		int[] hash2 = minHashSig(file2);
			
		return(approximateJaccard(hash1, hash2));
	}

	public double approximateJaccard(int[] d1, int[] d2) {
		double numMatch = 0.0;
		for(int i = 0; i < numPermutations; i++) {
			if(d1[i] == d2[i]) numMatch++;
		}
		
		return(numMatch / numPermutations);
	}
	

	public int[][] minHashMatrix() throws IOException {
		File[] contents = folder.listFiles();
		int[][] minHashMatrix = new int[contents.length][numPermutations]; //documents are rows
		
		int[] doc;
		for(int i = 0; i < contents.length; i++) {
			if(contents[i].isFile()) {
				doc = minHashSig(contents[i].getName()); 
				
				for(int j = 0; j < numPermutations; j++) {
					minHashMatrix[i][j] = doc[j]; //documents are rows
				}
			} 
		}
		
		return(minHashMatrix);
	}
	

	public int numTerms() {
		return(numTerms);
	}
	

	public int numPermutations() {
		return(numPermutations);
	}
	

	private int word2int(String s, int a, int b, int mod) {
		int hashed = 0;
		
		for(int i = 0; i < s.length(); i++) {
			hashed ^= s.charAt(i);
			hashed = a + b * hashed;
			hashed = hashed % mod;
		}
		
		return(hashed);
	}
	

	public class Pair {
		int a, b;
		

		public Pair(int a, int b) {
			this.a = a;
			this.b = b;
		}
		

		@Override
		public boolean equals(Object other) {
			if(other == null) return(false);
			if(other == this) return(true);
			if(!(other instanceof Pair)) return(false);
			
			Pair p = (Pair) other;
			return(a == p.a && b == p.b);
		}
	}
	

	private List<Pair> generateCoefficients(int mod) {
		Random r = new Random();
		List<Pair> coef = new ArrayList<Pair>();
		
		Pair p = new Pair(r.nextInt(mod), r.nextInt(mod));
		for(int i = 0; i < numPermutations; i++) {
			while(coef.contains(p)) {
				p = new Pair(r.nextInt(mod), r.nextInt(mod));
			}
			coef.add(p);
		}
		
		return(coef);
	}
	
}
