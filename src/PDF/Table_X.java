package PDF;


import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;


public class Table_X {
	
  
	private static final String NULL = null;
	// FIELDS
	
	String name;
	ArrayList<String> nameworker 	= new ArrayList<String>();
	ArrayList<String> number 		= new ArrayList<String>();
	ArrayList<String> regime 		= new ArrayList<String>(); 
	ArrayList<String> nests 		= new ArrayList<String>(); 
	ArrayList<String> idcardno		= new ArrayList<String>();
	
	SimpleDateFormat HHMMformatter= new SimpleDateFormat("HH:mm");
	SimpleDateFormat DDformatter = new SimpleDateFormat("DD");
	SimpleDateFormat YYYYMMDDformatter = new SimpleDateFormat("YYYY-MM-dd");
	
	
	 public Table_X(){
		TimeZone zone = TimeZone.getTimeZone("GMT+1");
		HHMMformatter.setTimeZone(zone);
		
	}
	
	
	void add(String name,String workernumber,String regime,  String nest){
		this.nameworker.add(name);
		this.number.add(workernumber);
		this.regime.add(regime);
		this.nests.add(nest);
		this.idcardno.add(NULL);
	}

	
	
	void clear(){
		this.nameworker.clear();
		this.number.clear();
		this.regime.clear();
		this.nests.clear();
	}	
	
	void display(PrintStream device){
		for (int i = 0 ; i < this.nameworker.size() ; i++){
						
			// Pass date object
			System.setOut(device);
			
			System.out.print(this.nests.get(i));
			System.out.print(" ");
			System.out.print(this.nameworker.get(i));
			System.out.print(" ");
			System.out.print(this.number.get(i));
			System.out.print(" ");
			System.out.println(this.regime.get(i));
			//System.out.print(" ");
			//System.out.println(this.idcardno.get(i));
		}//end for
	}
	
	public int size(){
		int result = nameworker.size();
		return result;
	}
	
	public String getNameworker(int index){
		String result = this.nameworker.get(index);
		return result;
	}
	

}