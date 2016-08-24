import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
//import java.util.HashSet;
import java.util.HashMap;
import java.util.TreeMap;



public class Evaluation {
	private static long total;
	private static long normal;
	private static long miss;
	private static long retransmission;
	private static long unknown;
	private static long TIDMissMatch;
	
	public long getTotal(long prev_total){
		return total+prev_total;
	}
	
	public long getTotal(){
		return total;
	}
	
	public long getNormal(long prev_normal){
		return normal+prev_normal;
	}
	
	public long getNormal(){
		return normal;
	}
	
	public long getMiss(){
		return miss;
	}
	
	public long getMiss(long prev_miss){
		return miss;
	}
	
	public long getRetransmission(long prev_retransmission){
		return retransmission + prev_retransmission;
	}
	
	public long getRetransmission(){
		return retransmission;
	}
	
	public long getUnknown(long prev_unknown){
		return unknown;
	}
	
	public long getUnknown(){
		return unknown;
	}
	
	public long getTIDMissMatch(long prev_TIDMissMatch){
		return prev_TIDMissMatch+TIDMissMatch;
	}
	
	public long getTIDMissMatch(){
		return TIDMissMatch;
	}
	
	public void initialize(){
		total=0;
		normal=0;
		miss=0;
		retransmission=0;
		unknown=0;
		TIDMissMatch=0;
	}
	
	public void getValidationStats(ArrayList<String> symbols,String[] window,int[] tid) throws FileNotFoundException{
		String prev_symbol = "";
		//int prev_index  = 0;
		total = window.length;
		ArrayList<Integer> TIDs = new ArrayList<Integer>();
		
		
		int i;
		int j;
		int nextIndex = 0;
		
		
		
		for( i=0;i<window.length;i++){
			if(window[i]==null) break;
			
			if(!symbols.contains(window[i])){ //symbol is not in pattern
				unknown++; // unknown symbol
				nextIndex = 0;
				
				continue; // start from the beginning
			}else if(window[i].equals(prev_symbol)){ // current symbol is same as previous symbol
				retransmission++; // retransmission. loop to the same state
			}
			else{
				if(nextIndex>=symbols.size()) nextIndex = 0;
				j = getNextSymbolIndex(window[i],symbols,nextIndex);
				if(j==-1){
					miss++;
					
					//nextIndex = i%symbols.size();
					//nextIndex = 0;
				}//else if(j==prev_index){
				//	retransmission++; // retransmission. loop to the same state
				//}
				else{
					nextIndex = j;
					normal++; //normal
					
					if(window[i].substring(0,1).equals("1")){ // store transaction id for request 
						TIDs.add(tid[i]);
					}else if(window[i].substring(0,1).equals("0")&&TIDs.contains(tid[i])){ // remove transaction id for response
						TIDs.remove(new Integer(tid[i]));
					}
				}
				//prev_index = j;
			}
			prev_symbol = window[i];
		}
		total = i;
		TIDMissMatch = TIDs.size();
	}

public void writeAnnomalyStats(ArrayList<String> symbols,String[] window,int[] tid,double[] timestamps,String fn) throws FileNotFoundException{
		
		TreeMap<Double,Integer> misses = new TreeMap<Double,Integer>();
		TreeMap<Double,Integer> unknowns = new TreeMap<Double,Integer>();
		TreeMap<Double,Integer> retransms = new TreeMap<Double,Integer>();
 		
		
		String prev_symbol = "";
		//int prev_index  = 0;
		total = window.length;
		ArrayList<Integer> TIDs = new ArrayList<Integer>();
		
		
		int i;
		int j;
		int nextIndex = 0;
		
		
		
		for( i=0;i<window.length;i++){
			if(window[i]==null) break;
			
			if(!symbols.contains(window[i])){ //symbol is not in pattern
				unknown++; // unknown symbol
				nextIndex = 0;
				
				unknowns.put(timestamps[i], 1);
				retransms.put(timestamps[i], 0);
				misses.put(timestamps[i], 0);
				
				continue; // start from the beginning
			}else if(window[i].equals(prev_symbol)){ // current symbol is same as previous symbol
				retransmission++; // retransmission. loop to the same state
				retransms.put(timestamps[i], 1);
				unknowns.put(timestamps[i], 0);
				misses.put(timestamps[i], 0);
			}
			else{
				if(nextIndex>=symbols.size()) nextIndex = 0;
				j = getNextSymbolIndex(window[i],symbols,nextIndex);
				if(j==-1){
					miss++;
					
					misses.put(timestamps[i], 1);
					unknowns.put(timestamps[i], 0);
					retransms.put(timestamps[i], 0);
					//nextIndex = i%symbols.size();
					//nextIndex = 0;
				}//else if(j==prev_index){
				//	retransmission++; // retransmission. loop to the same state
				//}
				else{
					nextIndex = j;
					normal++; //normal
					
					unknowns.put(timestamps[i], 0);
					retransms.put(timestamps[i], 0);
					misses.put(timestamps[i], 0);
					
					if(window[i].substring(0,1).equals("1")){ // store transaction id for request 
						TIDs.add(tid[i]);
					}else if(window[i].substring(0,1).equals("0")&&TIDs.contains(tid[i])){ // remove transaction id for response
						TIDs.remove(new Integer(tid[i]));
					}
				}
				//prev_index = j;
			}
			prev_symbol = window[i];
		}
		total = i;
		TIDMissMatch = TIDs.size();
		
		
		
		writeTimeTick(unknowns, "unknowns_"+fn);
		
		writeTimeTick(retransms, "retrans_"+fn);
		
		writeTimeTick(misses, "misses_"+fn);
		
		
		
	}

	
	private void writeTimeTick(TreeMap<Double, Integer> unknowns,String fn) throws FileNotFoundException {
		PrintWriter pw = new PrintWriter(fn);
		for(double k:unknowns.keySet()){
			pw.println(k+","+unknowns.get(k));
		}
		pw.println();
		pw.close();
	}
	
	public int getNextSymbolIndex(String cSymbol,ArrayList<String> symbols,int cIndex){
		
		if(symbols.get(cIndex).equals(cSymbol)) 
			return cIndex+1;
		
		return -1;
	}
	
	
}
