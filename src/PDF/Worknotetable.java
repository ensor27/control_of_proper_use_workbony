package PDF;

import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class Worknotetable {

	// FIELDS
	
		String name;
		String nameworker;
		String nummerworker;
		String regime;
		String nests;
		String idcardno;
		String day;
		
		ArrayList<String> worknotenumber 	= new ArrayList<String>();
		ArrayList<String> starthour 		= new ArrayList<String>();
		ArrayList<String> startmin 			= new ArrayList<String>(); 
		ArrayList<String> endhour 			= new ArrayList<String>(); 
		ArrayList<String> endmin 			= new ArrayList<String>(); 
		
		
		SimpleDateFormat HHMMformatter= new SimpleDateFormat("HH:mm");
		SimpleDateFormat DDformatter = new SimpleDateFormat("DD");
		SimpleDateFormat YYYYMMDDformatter = new SimpleDateFormat("YYYY-MM-dd");
		
		
		 public Worknotetable(String nameworker,String nummerworker,String regime,String nests,String idcardno){
			TimeZone zone = TimeZone.getTimeZone("GMT+1");
			HHMMformatter.setTimeZone(zone)	;
			this.nameworker=nameworker;
			this.nummerworker=nummerworker;
			this.regime=regime;
			this.nests=nests;
			this.idcardno=idcardno;
			
		}
		
		
		void add(String worknotenumber,String starthour,String startmin, String endhour,String endmin){
			this.worknotenumber.add(worknotenumber);
			this.starthour.add(starthour);
			this.startmin.add(startmin);
			this.endhour.add(endhour);
			this.endmin.add(endmin);
		}

		
		void clear(){
			this.name=null;
			this.nameworker=null;
			this.nummerworker=null;
			this.regime=null;
			this.nests=null;
			this.idcardno=null;
			this.worknotenumber.clear();
			this.starthour.clear();
			this.startmin.clear();
			this.endhour.clear();
			this.endmin.clear();
			}	
		
		void emptytable(){
			this.worknotenumber.clear();
			this.starthour.clear();
			this.startmin.clear();
			this.endhour.clear();
			this.endmin.clear();
			}	
		
		private String totaltime() throws ParseException{
			long time = 0L;
			for (int i=0; i<this.worknotenumber.size();i++){
				//String.valueOf((start-start%60)/60)+":"+String.valueOf(start%60)
				time += convertor.LongFromStringTime(endhour.get(i)+":"+ endmin.get(i)) - convertor.LongFromStringTime(starthour.get(i)+":"+ startmin.get(i));
				} //END FOR
			String result = String.format("%02d:%02d", 
					TimeUnit.MILLISECONDS.toHours(time),	
					TimeUnit.MILLISECONDS.toMinutes(time) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time)));
			return result;
		}
		
		void display(PrintStream device) throws ParseException{
			System.setOut(device);
			System.out.println("WorknoteLine" + nameworker + " " + nummerworker + " today on: " + day + " + working for: " + nests + " in " + regime);
			System.out.println("this day he scanned for total amount: " + totaltime());
			for (int i = 0 ; i < this.worknotenumber.size() ; i++){
				System.out.print(this.worknotenumber.get(i));
				System.out.print(": ");
				System.out.print(this.starthour.get(i));
				System.out.print(":");
				System.out.print(this.startmin.get(i));
				System.out.print(" <=> ");
				System.out.print(this.endhour.get(i));
				System.out.print(":");
				System.out.println(this.endmin.get(i));
				System.out.println("===========================================================");
			}//end for
		}
		

}
