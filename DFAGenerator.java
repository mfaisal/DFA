import java.util.ArrayList;
import java.util.LinkedHashSet;

public class DFAGenerator {
	public ArrayList<String> getDFA(int pattern_length, String[] window,int[] tid){
		ArrayList<String> symbols = new ArrayList<String>(); // contain symbols
		ArrayList<Integer> TIDs = new ArrayList<Integer>();
		
		
		for(int i=0;i<window.length;i++){ 
			if(window[i].substring(0,1).equals("1")){ // store transaction id for request which did not appeared earlier 
				TIDs.add(tid[i]);
				symbols.add(window[i]);
			}else if(window[i].substring(0,1).equals("0")&&TIDs.contains(tid[i])){ // remove transaction id for response, if tid already exists
				TIDs.remove(new Integer(tid[i]));
				symbols.add(window[i]);
			}
			
			if(symbols.size()==pattern_length){ 
				break;
			}
			
		}
		
		LinkedHashSet<String> lhs = new LinkedHashSet<String>(symbols); // remove repeated symbols
		symbols.clear();
		symbols.addAll(lhs);
		return symbols;

	}
}
