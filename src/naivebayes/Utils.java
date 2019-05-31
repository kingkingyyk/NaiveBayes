package naivebayes;

public class Utils {
	
	public static String cleanWord(String s) {
		char [] ch=s.toCharArray();
		int startIndex=0;
		for (;startIndex<ch.length && !Character.isAlphabetic(ch[startIndex]);startIndex++) {}
		int endIndex=ch.length-1;
		for (;endIndex>=startIndex && !Character.isAlphabetic(ch[endIndex]);endIndex--) {}
		return s.substring(startIndex,endIndex+1).toLowerCase();
	}
}
