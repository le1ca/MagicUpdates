package magic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

class MagicItemVersion implements Comparable<MagicItemVersion> {
	private ArrayList<Integer> parts;
	
	public MagicItemVersion(String version){
		parts = new ArrayList<Integer>();
		StringTokenizer s = new StringTokenizer(version,".");
		while(s.hasMoreTokens())
			parts.add(Integer.decode(s.nextToken()));
	}

	public int compareTo(MagicItemVersion o) {
		Iterator<Integer> a = parts.iterator();
		Iterator<Integer> b = o.parts.iterator();
		while(a.hasNext() || b.hasNext()){
			Integer aa;
			Integer bb;
			if(!a.hasNext()) return -1;
			if(!b.hasNext()) return 1;
			 
			aa = a.next();
			bb = b.next();
			
			if(aa.compareTo(bb) != 0)
				return aa.compareTo(bb);
		}
		return 0;
	}
	
	public String toString(){
		String s = "";
		Iterator<Integer> a = parts.iterator();
		while(a.hasNext())
			s = s + (s.length()>0?".":"") + a.next().toString();
		return s;
	}
	
}