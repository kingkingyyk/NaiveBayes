package naivebayes;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class NaiveBayesData {
	private HashMap<String, Integer> classesCount=new HashMap<>();
	private HashMap<String, Integer> wordCount=new HashMap<>();
	private HashMap<String, Integer> wordAndClassesCount=new HashMap<>();
	private HashMap<String, HashSet<String>> classWords=new HashMap<>();
	
	private HashMap<String, Double> classesProbability=new HashMap<>();
	private int totalWords;
	
	private static String constructKey(String cls, String word) {
		return cls+"@@@@"+word;
	}
	
	private void mergeWordCount(HashMap<String,Integer> map) {
		for (String key : map.keySet()) wordCount.put(key, wordCount.getOrDefault(key,0)+map.get(key));
	}
	
	public void train1(List<DataFile> dfs) {
		for (DataFile df : dfs) {
			mergeWordCount(df.getDictionary()); //all distinct words in training text, total number of words in Texti.(count duplicates multiple times)
			classesCount.put(df.cls, classesCount.getOrDefault(df.cls,0)+1); //P(Ci) for every class Ci. P(Ci) =( Number of documents of class Ci ) /( total number of documents ).
			if (!classWords.containsKey(df.cls)) classWords.put(df.cls, new HashSet<>());
			for (String word : df.getDictionary().keySet()) {
				String key=constructKey(df.cls, word);
				wordAndClassesCount.put(key, wordAndClassesCount.getOrDefault(key,0)+df.getDictionary().get(word));
				classWords.get(df.cls).add(word);
			}
		}
		
		int totalClassCount=0;
		for (int v : classesCount.values()) totalClassCount+=v;
		for (String cls : classesCount.keySet()) classesProbability.put(cls, classesCount.get(cls)/((double)totalClassCount));
		
		for (String word : wordCount.keySet()) totalWords+=wordCount.get(word);
	}
	
	public Map<String, Double> predict1(DataFile df) {
		HashMap<String, Double> classProbability=new HashMap<>();
		for (String cls : classesCount.keySet()) {
			double Ci=Math.log(classesProbability.get(cls));
			for (String word : df.getDictionary().keySet()) {
				double wordProbabilityGlobal = df.getDictionary().get(word) * ((wordAndClassesCount.getOrDefault(constructKey(cls, word),0)+1.0) / classWords.get(cls).size());
				Ci+=Math.log(wordProbabilityGlobal);
			}
			classProbability.put(cls, Ci);
		}
		return classProbability;
	}
	
	public void printTable() {
		System.out.println("< ------ BASIC ------ >");
		System.out.println("NO. OF WORDS - "+wordCount.size());
		System.out.println("NO. OF CLASSES - "+classesCount.size());
		System.out.println();
		System.out.println("< ------ CLASS ------ >");
		System.out.println("| Class |    Probability    | Words |");
		System.out.println("-------------------------------------");
		for (String cls : classesProbability.keySet()) {
			System.out.println("| "+cls+" | "+classesProbability.get(cls)+" | "+classWords.get(cls).size()+" | ");
		}
		System.out.println();
		System.out.println("< ------ WORD OCCURANCE ------ >");
		System.out.print("| Word |");
		for (String cls : classesProbability.keySet()) System.out.print(" "+cls+" |");
		System.out.println();
		System.out.println("-----------------------");
		for (String word : wordCount.keySet()) {
			System.out.print("| "+word+" |");
			for (String cls : classesProbability.keySet()) {
				String key = constructKey(cls, word);
				System.out.print(" "+wordAndClassesCount.getOrDefault(key,0)+" |");
			}
			System.out.println();
		}

		
	}
	
	public Map<String, Double> testData(DataFile df) {
		HashMap<String, Double> classProbability=new HashMap<>();
		
		for (String cls : this.classesCount.keySet()) {
			System.out.println(this.classesProbability.get(cls));
			double ans=Math.log(this.classesProbability.get(cls));
			for (String word : df.getDictionary().keySet()) {
				int wordCountInDoc=df.getDictionary().get(word)+1;
				double total=classWords.get(cls).size()+wordCount.size();
				System.out.println(wordCountInDoc);
				ans+=Math.log(wordCountInDoc/total);
			}
			classProbability.put(cls, ans);
		}
		return classProbability;
	}
}
