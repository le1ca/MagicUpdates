package magic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import us.monoid.web.Resty;

public class MagicClient {
	private static MagicClient instance = null;
	private String cfgUrl;
	private String rootUrl;
	private BufferedWriter logger;
	
	static final String dataDirectory = "c:\\magicupdates\\data";
	static final String logDirectory  = "c:\\magicupdates\\logs";
	static final String stateFile     = "c:\\magicupdates\\magic.dat";
	static final String batchFile     = "c:\\magicupdates\\magic.bat";
	
	// Constructor: takes configuration URL as parameter
	private MagicClient(String cfgUrl) throws IOException{
		this.cfgUrl = cfgUrl;
		
		// Create directories if needed
		new File(dataDirectory).mkdirs();
		new File(logDirectory).mkdirs();
		
		// initialize logger
		logger = new BufferedWriter(new FileWriter(logDirectory + "\\magic.log"));
	}
	
	public void log(String s) throws IOException{
		System.out.println(s);
		logger.write(s);
		logger.newLine();
	}

	// Run method: does the magic
	public void run() throws Exception {
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy hh:mm:ss a");
		ArrayList<MagicItem> items = null;
		
		log("MagicUpdates on " + System.getenv("COMPUTERNAME") + " at " + sdf.format(d));
		
		// Attempt to load state
		try {
			MagicItemVersionMap.getInstance().load(stateFile);
			log("Loaded saved update state");
		}
		catch(Exception e){
			log("Saved update state not found");
		}

		log("");
		
		// Attempt to process config file, fatal if failed
		try {
			items = getItems();
		} catch (Exception e) {
			log("Fatal error processing configuration: "+e.getMessage());
			e.printStackTrace();
		}
		
		// Attempt to process update items
		for(MagicItem i : items){
			try{
				i.doMagic();
			} catch(Exception e){
				log("Exception while processing "+i.getName());
				log(" ** " + e.getMessage().replaceAll("\n", "\n ** "));
				log("");
			}
		}
		
		// Attempt to save batch file and schedule task to run updates on reboot
		try{
			Process p;
			int r = -1;
			MagicBatchGenerator.getInstance().addLine("schtasks /delete /f /tn \"MagicUpdatesJob\""); // must delete task after it runs
			MagicBatchGenerator.getInstance().addLine("start cmd /C del " + batchFile);
			MagicBatchGenerator.getInstance().save(batchFile);
			log("Generated batch file "+batchFile);
			
			// remove existing task if it exists
			p = Runtime.getRuntime().exec("cmd /C schtasks /delete /f /tn \"MagicUpdatesJob\"");
			p.waitFor();
			
			// create new task
			p = Runtime.getRuntime().exec(
					"cmd /C schtasks /create /tn \"MagicUpdatesJob\" /tr " + batchFile +
					" /sc ONSTART /ru system"
				);
			r = p.waitFor();
			
			if(r != 0)
				log("Unable to schedule update task (exit code "+r+")");
			else
				log("Scheduled update task to run at next reboot");
		}catch(Exception e){
			log("Exception while saving batch file or scheduling update job");
			log(" ** " + e.getMessage());
		}
		
		// Attempt to save state
		try{
			MagicItemVersionMap.getInstance().save(stateFile);
		} catch(Exception e){
			log("Could not save updated state: " + e.getMessage());
		}
		
		log("Done");
		logger.flush();
		logger.close();
	}
	
	private ArrayList<MagicItem> getItems() throws Exception{
		String line;
		MagicItem current = null;
		ArrayList<MagicItem> items = new ArrayList<MagicItem>();
		String configString = null;
		BufferedReader configReader;
		
		try{
			configString = new Resty().text(cfgUrl).toString();
		}
		catch(Exception e){
			log("Fatal error: could not connect to update server");
			e.printStackTrace();
		}
		
		configReader = new BufferedReader(new StringReader(configString));
		
		// parse configuration
		while((line = configReader.readLine()) != null){
			int d1, d2;
			String command, data;
			
			// skip empty lines and comments
			if(line.equals("") || line.startsWith("//"))
				continue;
			
			// split into 'command' and 'data' parts
			for(d1 = 0;  d1 < line.length() && (line.charAt(d1) != ' ' && line.charAt(d1) != '\t'); d1++);
			for(d2 = d1; d2 < line.length() && (line.charAt(d2) == ' ' || line.charAt(d2) == '\t'); d2++);
			command = line.substring(0, d1);
			data = line.substring(d2);
			
			// process config entries
			if(command.equals("root"))
				rootUrl = data;
			else if(command.equals("end"))
				items.add(current);
			else if(command.equals("entry"))
				current = new MagicItem(data);			
			else if(command.equals("version"))
				current.setVersion(data);
			else if(command.equals("file"))
				current.addFile(data);
			else if(command.equals("command"))
				current.addCommand(data);
			else if(command.equals("cond"))
				current.addCondition(data);
		}
		
		return items;
	}

	public static MagicClient getInstance(String string) throws Exception {
		if(instance != null)
			throw new Exception("An instance already exists!");
		
		instance  = new MagicClient(string);
		return instance;
	}
	
	public static MagicClient getInstance() throws Exception{
		if(instance == null)
			throw new Exception("No instance exists!");
		else return instance;
	}

	public String getRoot() {
		return rootUrl;
	}
	
}
