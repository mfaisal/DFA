import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.FileWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class MainContainer {
	public static void main(String[] args)  throws IOException, ParseException{
		
		
		
		
		int pattern_len = 16;
		
		
		int learning_window = 100;
		int validation_window = 300;
		
		String readDir = "/home/mustafa/Spring2016/Research/AvishaiPCAP_analysis/Results/Channels/Significantone/";
		String writeDir = "/home/mustafa/Spring2016/Research/AvishaiPCAP_analysis/Results/Channels/StatsOfSignificant/";
		
		/*File rDirFiles = new File(readDir);
		
		for(File fl: rDirFiles.listFiles()){
			for(int pl=2;pl<=100;pl+=2){
				doValidation(fl.getAbsolutePath(), writeDir+fl.getName(), pl, learning_window, validation_window);
			}
		}*/
		
		String fn = "/home/mustafa/Spring2016/Research/AvishaiPCAP_analysis/Results/Channels/Significantone/132.66.174.77._132.66.94.249._1.txt";
		
		String fnw = "rest.txt";
		String model = "1386012, 03024,  13126045, 03090, 1399923, 03046, 119981, 010123, 13106116, 030232, 1327011, 03022, 1345052, 030104, 13606114, 030228";
		
		
		Options ons1 = new Options();
		
		ons1.addOption("l", false, "model learning");
		ons1.addOption("inputdir", true, "input directory");
		ons1.addOption("outputdir", true, "output directory");
		ons1.addOption("lwindow", true, "learning window");
		ons1.addOption("vwindow", true, "validation window");
		
		
		ons1.addOption("e", false, "model evaluation");
		ons1.addOption("inputfile", true, "input data file");
		ons1.addOption("outputfile", true, "output data file");
		ons1.addOption("sklen", true, "skip length because learning and validation");
		ons1.addOption("model", true, "output file");
		
		
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = parser.parse( ons1, args);
		
		
		
		if(cmd.hasOption("l")){
			learning_window = Integer.parseInt(cmd.getOptionValue("lwindow"));
			validation_window = Integer.parseInt(cmd.getOptionValue("vwindow"));
			readDir = cmd.getOptionValue("inputdir")+"/";
			writeDir = cmd.getOptionValue("outputdir")+"/";
			//System.out.println(readDir);
			File rDirFiles = new File(readDir);
			
			for(File fl: rDirFiles.listFiles()){
				for(int pl=2;pl<=100;pl+=2){
					doValidation(fl.getAbsolutePath(), writeDir+fl.getName(), pl, learning_window, validation_window);
				}
			}
		}
		
		int skpsize = 400;
		if(cmd.hasOption("e")){
			skpsize = Integer.parseInt(cmd.getOptionValue("sklen"));
			model = cmd.getOptionValue("model");
			fn = cmd.getOptionValue("inputfile");
			fnw = cmd.getOptionValue("outputfile");
			
			ArrayList<String> sr = new ArrayList<String>();
			String[] symbs = model.split(",");
			for(String s:symbs){
				sr.add(s.trim());
			}
			
			evaluateWithModel(sr,skpsize,fn,fnw);
		}
		
		
				///"132566, 03012";
		//132.66.174.77._132.66.60.83._0.txt --->"1310025, 03050, 1360014, 03028, 115591, 0101";
		//132.66.174.77._132.66.94.249._1.txt --->"1386012, 03024, 1399923, 03046, 13126045, 03090, 119981, 010123, 13106116, 030232, 1327011, 03022, 1345052, 030104, 13606114, 030228";
		//132.66.174.77._132.66.160.232._0.txt --->  "136005,03010,119551,01069,1310040,03080" 
		
		/*ArrayList<String> sr = new ArrayList<String>();
		String[] symbs = model.split(",");
		for(String s:symbs){
			sr.add(s.trim());
		}
		//doValidation(fn, fnw, pattern_len, learning_window, validation_window);
		//System.out.println(model+"\n"+sr);
		evaluateWithModel(sr,skpsize,fn,fnw);*/
		
		
	}
	
	private static void evaluateWithModel(ArrayList<String> model,int skipLength,String fn, String fnw)throws IOException,
	FileNotFoundException, NumberFormatException{
		int count =0;
		
		Evaluation ev = new Evaluation();
		ev.initialize();
		String symbol;
		String[] flds;
		//System.out.println("Hi");
		Scanner sc = new Scanner(new File(fn));
		while(sc.hasNextLine()){
			sc.nextLine();
			count++;
		}
		sc.close();
		
		String[] window = new String[count];
		int[] tids = new int[count];
		double[] times = new double[count];
		PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(fnw,true)));
		sc = new Scanner(new File(fn));
		
		//System.out.println(count);
		
		int i = 0;
		count =0;

		while(sc.hasNextLine()){
			if(count>=skipLength){
				
				flds = sc.nextLine().split(",");
				symbol = flds[2]+flds[3]+flds[4]+flds[5]; 
				window[i] = symbol;
				//if(i<=100) System.out.println(symbol);
				tids[i] = Integer.parseInt(flds[1]);
				times[i] = Double.parseDouble(flds[0]);
				i++;
			}
			count++;
		}
		
		//ev.getValidationStats(model,window , tids);
		String[] flns = fn.split("/");
		ev.writeAnnomalyStats(model, window, tids, times, flns[flns.length-1]);
		pw.println(model.size()+","+ev.getNormal()+","+ev.getMiss()+","+ev.getRetransmission()+","+ev.getUnknown()+","+ev.getTIDMissMatch()+","+ev.getTotal()+","+(double)ev.getNormal()/ev.getTotal()+","+(double)model.size()/(model.size()+1));
		sc.close();
		pw.close();
	}

	/**
	 * @param fn
	 * @param fnw
	 * @param pattern_len
	 * @param learning_window
	 * @param validation_window
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws NumberFormatException
	 */
	private static void doValidation(String fn, String fnw, int pattern_len,
			int learning_window, int validation_window) throws IOException,
			FileNotFoundException, NumberFormatException {
		int count =0;
		
		Evaluation ev = new Evaluation();
		DFAGenerator ks = new DFAGenerator();
		
		ev.initialize();
		
		String symbol;
		String[] flds;
		
		PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(fnw,true)));
		Scanner sc = new Scanner(new File(fn));
		
		
		String[] l_window = new String[learning_window];
		String[] v_window = new String[validation_window];
		int[] l_tid = new int[learning_window];
		int[] v_tid = new int[validation_window];
		
		while(sc.hasNextLine()){
			flds = sc.nextLine().split(",");
			symbol = flds[2]+flds[3]+flds[4]+flds[5]; 

			if(count<learning_window){
				l_window[count] = symbol;
				l_tid[count] = Integer.parseInt(flds[1]);
			}else if(learning_window<=count&&count<(learning_window+validation_window)){
				v_window[count-learning_window] = symbol;
				v_tid[count-learning_window] = Integer.parseInt(flds[1]);
			}else{
				break;
			}
			
			count++;
		}
		
		
		ArrayList<String> ksym = ks.getDFA(pattern_len, l_window, l_tid);
		//System.out.println(ksym);
		ev.getValidationStats(ksym,v_window , v_tid);
		pw.println(ksym);
		pw.println(pattern_len+","+ev.getNormal()+","+ev.getMiss()+","+ev.getRetransmission()+","+ev.getUnknown()+","+ev.getTIDMissMatch()+","+ev.getTotal()+","+(double)ev.getNormal()/ev.getTotal()+","+(double)pattern_len/(pattern_len+1));
		//System.out.println(ev.getNormal()+","+ev.getMiss()+","+ev.getRetransmission()+","+ev.getUnknown()+","+ev.getTIDMissMatch()+","+ev.getTotal()+","+(double)ev.getNormal()/ev.getTotal()+","+(double)pattern_len/(pattern_len+1));
		sc.close();
		pw.close();
	}
}
