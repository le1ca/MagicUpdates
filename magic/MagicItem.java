package magic;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;

import us.monoid.web.Resty;

public class MagicItem{
	
	private String name;
	private MagicItemVersion version;
	private ArrayList<String> files;
	private ArrayList<String> commands;
	private ArrayList<MagicCondition> conditions;
	
	public MagicItem(String name){
		this.name = name;
		this.version = null;
		this.files = new ArrayList<String>();
		this.commands = new ArrayList<String>();
		this.conditions = new ArrayList<MagicCondition>();
	}
	
	private void log(String s) throws Exception{
		MagicClient.getInstance().log(s);
	}
	
	public void addFile(String file){
		files.add(file);
	}
	
	public void addCommand(String cmd){
		commands.add(cmd);
	}
	
	public void addCondition(String cond){
		conditions.add(new MagicCondition(cond));
	}
	
	public void setVersion(String line) {
		version = new MagicItemVersion(line);		
	}
	
	public void doMagic() throws Exception {
		boolean conds = true;
		Resty r = new Resty();
		ArrayList<File> tmps = new ArrayList<File>();
		MagicItemVersion prev = MagicItemVersionMap.getInstance().get(name);
		
		log("Processing item '"+name+"'");
		
		// check conditions
		for(MagicCondition c : conditions){
			if(!c.test()){
				log(" -> Not needed by condition "+c.toString());
				conds = false;
				break;
			}
		}
		
		// check if up to date
		if(conds && prev != null && version.compareTo(prev) == 0){
			log(" -> Already up to date (version "+prev+")");
			conds = false;
		}
		
		// if conditions met and up to date, do the thing
		if(conds){
			// populate array of temp file handles
			for(int i = 0; i < files.size(); i++){
				tmps.add(new File(MagicClient.dataDirectory + "\\" + files.get(i)));
			}	
			
			if(prev != null)
				log(" -> Updating from version "+prev+" to "+version);
			else
				log(" -> No current installation detected, installing version "+version);
		
			// download files
			for(int i = 0; i < files.size(); i++){
				@SuppressWarnings("deprecation")
				String url = MagicClient.getInstance().getRoot() + "/" + URLEncoder.encode(files.get(i)).replace("+", "%20");
				log(" -> Downloading "+url);
				r.bytes(url).save(tmps.get(i));
			}
			
			// add commands
			for(int i = 0; i < commands.size(); i++){
				String command = commands.get(i);
				
				// perform replacements
				
				// @@ is the data directory
				command = command.replace("@@", MagicClient.dataDirectory);
				
				// %%n is the full path to the file number 'n'
				// !!n generates a log filename based on a particular file
				for(int j = 1; j <= files.size(); j++){
					command = command.replace("%%"+j, tmps.get(j-1).getAbsolutePath());
					command = command.replace("!!"+j, MagicClient.logDirectory+"\\"+files.get(j-1)+".log");
				}
				
				log(" -> Adding command "+command);
				MagicBatchGenerator.getInstance().addLine(command);
			}
			
			// add commands to remove downloaded files
			for(int i = 0; i < files.size(); i++){
				MagicBatchGenerator.getInstance().addLine("del \"" + tmps.get(i).getAbsolutePath()+"\"");
			}
			
			MagicItemVersionMap.getInstance().set(name, version);
		}
		log("");
	}

	public String getName() {
		return name;
	}
}
