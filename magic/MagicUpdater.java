package magic;

public class MagicUpdater {
	
	public static boolean isAdmin(){
        Process p = null;
        
		try {
			p = Runtime.getRuntime().exec("reg query \"HKU\\S-1-5-19\"");
	        p.waitFor(); 
		} catch (Exception e) {
			System.out.println("Could not determine privilege level, exiting");
			e.printStackTrace();
		}
		
        return (p.exitValue() == 0);
	}
	
	public static void main(String[] args){
		
		if(!System.getProperty("os.name").startsWith("Windows")){
			System.out.println("Error: this application must be run on a Windows OS");
			return;
		}
		
		if(!isAdmin()){
			System.out.println("Error: this application must be run as administrator");
			return;
		}
		
		if(args.length != 1){
			System.out.println("Error: must specify config URL on command line");
			return;
		}
		
		try {
			MagicClient.getInstance(args[0]).run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
