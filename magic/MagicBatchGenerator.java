package magic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class MagicBatchGenerator {
	private ArrayList<String> lines;
	private static MagicBatchGenerator instance = null;
	
	private MagicBatchGenerator(){
		lines = new ArrayList<String>();
	}
	
	public static MagicBatchGenerator getInstance(){
		if(instance == null)
			instance = new MagicBatchGenerator();
		return instance;
	}
	
	public void addLine(String s){
		lines.add(s);
	}
	
	public void save(String filename) throws IOException{
		BufferedWriter f = new BufferedWriter(new FileWriter(new File(filename)));
		for(String s : lines){
			f.append(s);
			f.newLine();
		}
		f.flush();
		f.close();
	}
}
