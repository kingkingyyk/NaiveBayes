package naivebayes;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

public class NaiveBayes {
	
	private static String rootDir = "D:\\Documents\\King\\Desktop\\aaaa";
	private static String [] trainingDataDirs = {"part1"};//, "part2","part3","part4","part5","part6","part7","part8","part9"};
	private static String [] testDataDirs = {"part10"};
	
	private static ArrayList<DataFile> scanFiles(File dir, String [] dirNames) throws Exception {
		ArrayList<DataFile> dfs=new ArrayList<>();
		for (File subDir : dir.listFiles()) {
			boolean isTrainingDataset=false;
			for (String partDir : dirNames) isTrainingDataset |= subDir.getName().equals(partDir);
			if (isTrainingDataset) for (File trainingFile : subDir.listFiles())  {
				DataFile df=new DataFile(trainingFile, trainingFile.getName().startsWith("spmsgb") ? "spam" : "legit");
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
		//nbd.printTable();
		for (DataFile testD : testData) {
			Map<String, Double> probability=nbd.predict1(testD);
			String bestFit="";
			for (String key : probability.keySet()) if (bestFit.length()==0 || probability.get(key)>probability.get(bestFit)) {
				bestFit=key;
			}
			System.out.println(testD.path + " - "+probability);
			System.out.println(testD.path + " - "+bestFit);
		}
		
		
	}
	
}
