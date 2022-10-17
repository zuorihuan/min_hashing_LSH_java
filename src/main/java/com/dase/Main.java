package com.dase;

import javafx.util.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Main {

	/**

	 * @param args Folder, number of permutations
	 * @throws IOException 
	 * @throws NumberFormatException 
	 */
	public static void main(String[] args) throws NumberFormatException, IOException {
		if(args.length < 2) throw new IllegalArgumentException(
				"参数错误");
		MinHash mh = new MinHash(args[0], Integer.parseInt(args[1]));
		int[][] hashMtx = mh.minHashMatrix();
		String[] docNames = mh.allDocs();
		String[] fileNames = mh.folder.list();	//所有文章对应的文件
		ArrayList<Pair<Pair<String, String>, Double>> res = new ArrayList<>();
		LSH lsh = new LSH(hashMtx, docNames, Integer.parseInt(args[2]));
		for (String fileName : fileNames) {
			//  候选对
			List<String> nearDuplicates = lsh.nearDuplicatesOf(fileName);
			for(String s : nearDuplicates) {
				double sim = mh.exactJaccard(fileName, s);
				res.add(new Pair<>(new Pair<>(fileName, s),sim));
			}
		}
		// 降序排序
		res.sort((a,b)->{
			if(b.getValue() > a.getValue()){
				return 1;
			}else{
				return -1;
			}
		});
		//输出最相似的五篇文章
		for (int i = 0; i < 5; i++) {
			System.out.println(String.format("第%d: 文件%s和文件%s对应的文章对",i, res.get(i).getKey().getKey(), res.get(i).getKey().getValue()));
		}
	}

}