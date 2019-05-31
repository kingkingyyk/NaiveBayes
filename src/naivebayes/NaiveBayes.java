package naivebayes;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class NaiveBayes {
	
	private static String rootDir = "D:\\Documents\\King\\Desktop\\lingspam_public\\lemm_stop";
	private static String [] trainingDataDirs = {"part1",  "part2","part3","part4","part5","part6","part7","part8","part9"};
	private static String [] testDataDirs = {"part10"};
	
	private static class WrongResult {
		File f;
		String correct, wrong;
		public WrongResult(File f, String correct, String wrong) {
			this.f=f;
			this.correct=correct;
			this.wrong=wrong;
		}
	}
	
	private static ArrayList<DataFile> scanFiles(File dir, String [] dirNames) throws Exception {
		ArrayList<DataFile> dfs=new ArrayList<>();
		for (File subDir : dir.listFiles()) {
			boolean isTrainingDataset=false;
			for (String partDir : dirNames) isTrainingDataset |= subDir.getName().equals(partDir);
			if (isTrainingDataset) for (File trainingFile : subDir.listFiles())  {
				DataFile df=new DataFile(trainingFile, trainingFile.getName().startsWith("spms") ? "spam" : "legit");
				df.generateDictionary();
				dfs.add(df);
			}
		}
		return dfs;
	}
	
	public static void main (String [] args) throws Exception {
		ArrayList<DataFile> trainingData = scanFiles(new File(rootDir), trainingDataDirs);
		ArrayList<DataFile> testData = scanFiles(new File(rootDir), testDataDirs);
		NaiveBayesData nbd=new NaiveBayesData();
		nbd.train1(trainingData);
		
		HashMap<String, Integer> classDocCount=new HashMap<>();
		HashMap<String, Integer> correctClassifiedCount=new HashMap<>();
		ArrayList<WrongResult> wronglyClassifiedList=new ArrayList<>();
		
		for (DataFile testD : testData) {
			classDocCount.put(testD.cls, classDocCount.getOrDefault(testD.cls,0)+1);
			
			Map<String, Double> probability=nbd.predict1(testD);
			String bestFit="";
			for (String key : probability.keySet()) if (bestFit.length()==0 || probability.get(key)>probability.get(bestFit)) {
				bestFit=key;
			}
			
			if (bestFit.equals(testD.cls)) correctClassifiedCount.put(testD.cls, correctClassifiedCount.getOrDefault(testD.cls,0)+1);
			else wronglyClassifiedList.add(new WrongResult(testD.path, testD.cls, bestFit));
		}
		
		for (String cls : classDocCount.keySet()) System.out.println("Class "+cls+" accurancy - "+(correctClassifiedCount.getOrDefault(cls,0)*100.0/classDocCount.get(cls))+"%");
		
		System.out.println("================== WRONGLY CLASSIFIED ======================");
		System.out.println("|\t\t\tFile\t\t\t\t\t|\tExpected\t|\tClassified as\t|");
		for (WrongResult wr : wronglyClassifiedList) {
			System.out.println(wr.f.getAbsolutePath()+" | "+wr.correct+" | "+wr.wrong);
		}
		
		
	}
	
}
