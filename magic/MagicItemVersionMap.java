package magic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

class MagicItemVersionMap {
	private HashMap<String,MagicItemVersion> data;
	private static MagicItemVersionMap instance;
	
	private MagicItemVersionMap(){
		data = new HashMap<String,MagicItemVersion>();
	}
	
	public static MagicItemVersionMap getInstance(){
		if(instance == null)
			instance = new MagicItemVersionMap();
		return instance;
	}
	
	public MagicItemVersion get(String index){
		return data.get(index);
	}
	
	public void set(String index, MagicItemVersion version){
		data.put(index, version);
	}
	
	public void save(String filename) throws IOException {
		BufferedWriter f = new BufferedWriter(new FileWriter(new File(filename)));
		for(Map.Entry<String,MagicItemVersion> p : data.entrySet()){
			f.write(p.getKey());
			f.newLine();
			f.write(p.getValue().toString());
			f.newLine();
			f.newLine();
		}
		f.flush();
		f.close();
	}
	
	public void load(String filename) throws IOException {
		File ff = new File(filename);
		BufferedReader f = new BufferedReader(new FileReader(ff));
		String s;
		while((s = f.readLine()) != null){
			if(s.length() == 0) continue;
			String v = f.readLine();
			set(s, new MagicItemVersion(v));
		}
		f.close();
	}
	
}