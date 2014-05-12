package magic;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

public class MagicCondition {
	private String type;
	private ArrayList<String> params;
	
	public MagicCondition(String t, ArrayList<String> p){
		type = t;
		params = p;
	}
	
	public MagicCondition(String line){
		params = new ArrayList<String>(Arrays.asList(line.split(";")));
		type = params.remove(0);
	}
	
	public String toString(){
		String s = type + "(";
		for(String p : params){
			s = s + p + ",";
		}
		return s.substring(0,s.length()-1) + ")";
	}
	
	public boolean test() throws Exception{
		boolean r = false;
		switch(type){
		
			case "platform":{
				switch(params.get(0)){
					case "32":
						r = (System.getenv("ProgramFiles(X86)") == null);
						break;
					case "64":
						r = (System.getenv("ProgramFiles(X86)") != null);
						break;
					default:
						throw new Exception("Configuration error: platform condition has invalid value '"+params.get(0)+"'");
				}
				break;
			}
			
			case "cmdnotcontain":{
				String val = params.get(0);
				BufferedReader br1, br2;
				String s;
				Process p;
				
				p = Runtime.getRuntime().exec("cmd /C "+params.get(1));
				br1 = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				br2 = new BufferedReader(new InputStreamReader(p.getInputStream()));
				
				r = true;
				
				p.waitFor();
				
				// check stderr
				while(r && (s = br1.readLine()) != null)
					if(s.contains(val))
						r = false;
				
				// check stdout
				while(r && (s = br2.readLine()) != null)
					if(s.contains(val))
						r = false;
		
				break;
			}
			
			case "cmdcontain":{
				String val = params.get(0);
				BufferedReader br1,br2;
				String s;
				Process p;
				
				p = Runtime.getRuntime().exec("cmd /C "+params.get(1));
				br1 = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				br2 = new BufferedReader(new InputStreamReader(p.getInputStream()));
				
				r = false;
				
				p.waitFor();
				
				// check stderr
				while(!r && (s = br1.readLine()) != null)
					if(s.contains(val))
						r = true;
				
				// check stdout
				while(!r && (s = br2.readLine()) != null)
					if(s.contains(val))
						r = true;
		
				break;
			}
			
			default:{
				throw new Exception("Configuration error: invalid condition type '"+type+"'");
			}
			
		}
		return r;
	}
}
