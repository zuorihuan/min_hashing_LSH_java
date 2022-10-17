package com.dase;

import java.util.*;


public class LSH {
	private int n; //文章个数
	int rows; //band的行数
	private int[][] minHashMatrix; //最小哈希签名矩阵
	private String[] docNames; //docnames
	private Map<Pair, String> hashTable; //Key = <Band, string hash value>
	
	int p;
	int a; // ax + b % p
	int b; //ax + b % p
	String name;
	

	public LSH(int[][] minHashMatrix, String[] docNames, int bands) {
		Random r = new Random();
		
		n = minHashMatrix.length;
		rows = minHashMatrix[0].length / bands;
		this.minHashMatrix = minHashMatrix;
		this.docNames = docNames;
		
		p = ProcessingFunctions.nextPrime(5 * n);
		a = r.nextInt(p);
		b = r.nextInt(p);
		hashTable = new HashMap<Pair, String>();
		
		int currBand = 0;
		int currProd = 1;
		for(int i = 0; i < n; i++) {
			for(int j = 0; j < minHashMatrix[0].length; j++) {
				currBand = j / rows;
				currProd = currProd + (a * minHashMatrix[i][j] + b);
				currProd = currProd % p;

				if((j + 1) % rows == 0 || (j + 1) == n) {
					Pair pa = new Pair(currBand, currProd);
					String names = hashTable.get(pa);
					names = names == null ? docNames[i] : names + "~::~" + docNames[i];
					
					hashTable.put(pa, names);
					currProd = 1;
				}	
			}
		}
	}
	

	public ArrayList<String> nearDuplicatesOf(String docName) {
		Set<String> setDuplicates = new HashSet<String>();
		ArrayList<String> nearDuplicates = new ArrayList<String>();
		
		int docIndex = 0;
		for(int i = 0; i < n; i++) {
			if(docNames[i].equals(docName)) {
				docIndex = i;
			}
		}
		
		int currBand = 0;
		int currProd = 1;
		String[] currString;
		for(int i = 0; i < minHashMatrix[docIndex].length; i++) {
			currBand = i / rows;
			currProd = currProd + (a * minHashMatrix[docIndex][i] + b);
			currProd = currProd % p;
			
			if((i + 1) % rows == 0 || (i + 1) == minHashMatrix[docIndex].length) {
				Pair pa = new Pair(currBand, currProd);
				String names = hashTable.get(pa);
				names = names == null ? "" : names;
				
				if(!names.equals("")) {
					currString = names.split("~::~");
					setDuplicates.addAll(Arrays.asList(currString));
				}
				currProd = 1;
			}	
		}
			
		nearDuplicates.addAll(setDuplicates);
		return(nearDuplicates);
	}
	

	public class Pair {	// band经映射对应的hashVal
		int band;
		int hashVal;
		

		public Pair(int band, int hashVal) {
			this.band = band;
			this.hashVal = hashVal;
		}
		
		

		@Override 
		public int hashCode() {
			int hash = 5;
			hash = hash * band * hashVal;
			
			return(hash);
		}
		
		

		@Override
		public boolean equals(Object other) {
			if(other == null) return(false);
			if(other == this) return(true);
			if(!(other instanceof Pair)) return(false);
			
			Pair p = (Pair) other;
			return(band == p.band && hashVal == p.hashVal);
		}
		

		@Override
		public String toString() {
			String s = "(" + band + "," + hashVal + ")";
			
			return(s);
		}
	}
}
