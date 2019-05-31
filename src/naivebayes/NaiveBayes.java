package naivebayes;

import java.util.List;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class NaiveBayes {
	
	private static String rootDir = "lemm_stop";
	
	private static class WrongResult {
		File f;
		String correct, wrong;
		public WrongResult(File f, String correct, String wrong) {
			this.f=f;
			this.correct=correct;
			this.wrong=wrong;
		}
	}
	
	private static class PredictionRun {
		HashMap<String, Integer> classDocCount, correctClassifiedCount;
		ArrayList<WrongResult> wronglyClassifiedList;
		ArrayList<DataFile> dataset;
		
		public PredictionRun(ArrayList<DataFile> dataset) {
			this.dataset=dataset;
		}
		
		public void run (double trainingPercent) {
			trainingPercent/=100.0;
			this.run(0, (int)(this.dataset.size()*trainingPercent));
		}
		
		public void run (int trainingDataStartIndex, int trainingDataEndIndex) {
			this.classDocCount=new HashMap<>();
			this.correctClassifiedCount=new HashMap<>();
			this.wronglyClassifiedList=new ArrayList<>();
			
			List<DataFile> trainingData = dataset.subList(trainingDataStartIndex, trainingDataEndIndex);
			List<DataFile> testData = new ArrayList<>(this.dataset);
			testData.removeAll(trainingData);
			
			NaiveBayesData nbd=new NaiveBayesData();
			nbd.train1(trainingData);
			
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
		}
		
		public Set<String> getClasses() {
			return classDocCount.keySet();
		}
		
		public double getAccuracy(String cls) {
			return (correctClassifiedCount.getOrDefault(cls, 0)*100.0) / classDocCount.get(cls);
		}
		
		public double getAccuracy() {
			double correct=0, total=0;
			for (String cls : this.getClasses()) {
				correct+=correctClassifiedCount.getOrDefault(cls, 0);
				total+=classDocCount.get(cls);
			}
			return (correct*100)/total;
		}
	}
	
	private static ArrayList<DataFile> scanFiles(File dir) throws Exception {
		ArrayList<DataFile> dfs=new ArrayList<>();
		for (File subDir : dir.listFiles()) {
			for (File trainingFile : subDir.listFiles())  {
				DataFile df=new DataFile(trainingFile, trainingFile.getName().startsWith("spms") ? "spam" : "legit");
				df.generateDictionary();
				dfs.add(df);
			}
		}
		Collections.sort(dfs);
		return dfs;
	}
	
	public static void main (String [] args) throws Exception {
		ArrayList<DataFile> dataset = scanFiles(new File(rootDir));

		System.out.println(" Training Percentage | Accuracy");
		PredictionRun run=new PredictionRun(dataset);
		for (double p=5.0;p<100.0;p+=10) {
			run.run(p);
			System.out.printf("\t\t %.0f%% | %.3f%%\n", p, run.getAccuracy());
		}
	}
	
}
