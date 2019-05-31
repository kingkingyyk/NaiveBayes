package naivebayes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.StringTokenizer;

public class DataFile {
	public File path;
	public String cls;
	private HashMap<String,Integer> dictionary;
	private String fileContent;
	private int wordCount;
	
	public DataFile (File p, String cls) {
		this.path = p;
		this.cls = cls;
	}
	
	public void generateDictionary() throws Exception {
		BufferedReader br=new BufferedReader(new FileReader(this.path));
		StringBuilder sb=new StringBuilder();
		String s;
		while ((s=br.readLine())!=null) {
			sb.append(s);
			sb.append(' ');
		}
		br.close();
		fileContent=sb.toString();
		StringTokenizer st=new StringTokenizer(fileContent);
		HashMap<String,Integer> ret=new HashMap<>();
		while (st.hasMoreTokens()) {
			String currWord = Utils.cleanWord(st.nextToken().toLowerCase());
			if (currWord.length()>0) {
				ret.put(currWord, ret.getOrDefault(currWord,0)+1);
				this.wordCount++;
			}
		}
		this.dictionary=ret;
	}

	public HashMap<String, Integer> getDictionary() {
		return dictionary;
	}

	public String getFileContent() {
		return fileContent;
	}

	public int getWordCount() {
		return wordCount;
	}
	
	
}
