package PDF;

import java.io.PrintStream;
import java.util.Calendar;

public class Timeline {
	
	private boolean line[] = new boolean[1440];
	public String name ;
	public int minutesleft ;
	public String strMinutesLeft ;

	
	public Timeline(String name, int day, int shift) {
		// shift=0 dayshift
		// shift=1 shift 1
		// shift=2 shift 2
		// shift=3 night
		// shift=4 empty
		//
		this.name = name;
		this.minutesleft = 0;
		switch ( day)  {
			case 1: //Sunday do nothing
				break;
			case 7: //Saturday
					switch (shift){
						case 0:	//day shift
						case 1:
						case 2:
						case 3:
							setTimeLine(06,00,9,30);
							setTimeLine(9,52,14,00);
						break;	
						case 4:
						break;
					}
				break; //break case 7
			case 2: //Monday
			case 3: //Tuesday
			case 4: //Wednesday
			case 5: //Thursday
				switch (shift){
					case 0:	//day shift
						setTimeLine(06,30,9,30);
						setTimeLine(9,52,15,00);
						break;
					case 1:	// first shift
						setTimeLine(06,00,9,30);
						setTimeLine(9,52,14,06);
						break;
					case 2: //second shift
						setTimeLine(13,54,18,00);
						setTimeLine(18,21,22,00);
						break;
					case 3: //night shift
						break;
					case 4: //empty timeline
						break;
				} //END switch
				break; //break case 5
			case 6: //Friday
				switch (shift){
					case 0:	//day shift
						setTimeLine(06,30,9,30);
						setTimeLine(9,52,13,00);
						break;
					case 1:	// first shift
						setTimeLine(06,00,9,30);
						setTimeLine(9,52,14,06);
						break;
					case 2: //second shift
						setTimeLine(13,54,18,00);
						setTimeLine(18,21,22,00);
						break;
					case 3: //night shift
						break;
					case 4: //empty timeline
						break;
				} //END switch
				break; //break case 6
			
	} //END SWITCH
	}
	

	
	/**
	 * set all bits true during start and stop time
	 * @author CUB4U
	 *
	 * @param starthour 	give start hour as integer
	 * @param startminute 	give start minute as integer
	 * @param stophour		give stop hour as integer
	 * @param stopminute 	give stop minute as integer
	 * @return 
	 *
	 * @return boolean array with set bits
	 */
	void setTimeLine ( int starthour, int startminute, int stophour, int stopminute )  { 
		int start = starthour * 60 + startminute  ;
		int stop = stophour * 60 + stopminute ;
		if (stop <0 || stop > 1439){ // for debugging reasons
			System.out.println(" error hereeeeeee in stopvalue: " + stop +"  " + stophour + "  "+ stopminute);
			stop = 1439;
		}
		if (start < 0 || start > 1439){
			System.out.println(" error hereeeeeee in startvalue: " + start +"  " + starthour + "  "+ startminute);
			start = 0;} 
		for (int i = start; i < stop  ; i++){
			line[i] = true ;
		}
		countMinutesLeft();
	} //END VOID
	
	void deductTimelines ( boolean[] timelineToDeduct)  { 
		int counter = 0 ;
		for (int i = 0; i < line.length; i++){
			line[i] = (timelineToDeduct[i] == true) ? false : line[i] ;
			if(line[i]){ counter++;} //ENDIF
		} //end for
		this.minutesleft = counter;
	    this.strMinutesLeft = String.format("%02d:%02d",(counter-counter%60)/60,(counter%60));
	}  //END VOID
	
	void IntersectTimelines ( boolean[] timelineToDeduct)  { 
		int counter = 0 ;
		for (int i = 0; i < line.length; i++){
			line[i] = (timelineToDeduct[i] == true && line[i] == true) ? true : false ;
			if(line[i]){ counter++;} //ENDIF
		} //end for
		this.minutesleft = counter;
	    this.strMinutesLeft = String.format("%02d:%02d",(counter-counter%60)/60,(counter%60));
	}  //END VOID
	
	void  countMinutesLeft(){
		int counter = 0 ;
		for (int i = 0 ; i < line.length ; i++){
			if(line[i]){ counter++;} //ENDIF
		} //END FOR
	    //return result;
	    this.minutesleft = counter;
	    this.strMinutesLeft = String.format("%02d:%02d",(counter-counter%60)/60,(counter%60));
	}
	
	
	public   boolean[] returntimeline ()  { 
		boolean result[] = new boolean[1440];
		System.arraycopy(this.line, 0, result, 0, 1440);		
		return result;
	}
	
	public String missingdata(String nest ){
		String result = ""; 
		int counter = 0;
		int start = -1;
		
		boolean found = false; 
		
		//String printout = "";
		
			
		
		for(int i=1; i<line.length;i++){
			//boolean previous = line[i-1];
			//boolean thisOne = line[i];

			//printout = line[i] ? "1" : "0" ;
			
			
			found = (counter > 9 && !line[i]) ? true : false;
			
//			if(line[i]){
//				System.out.println(" line[i]: " + line[i] + " line[i-1]: " + line[i-1]);
//			}
			
			if (found){
				found=false;
				found=true;
			}
			
//			
			//if (one && two){	counter++; }else{ counter=0; }
		    if (line[i-1] && line[i]) {
		    	counter++;}
		    else{
		    	counter=0;}
		    
			
			start = (counter==1)? i : start;
				
			if (found) {
				result += nest + " <TABLE_RED_MISSING_WORKNOTE> missing worknote data: " +String.format("%02d:%02d<==>%02d:%02d",
						((start-start%60)/60),(start%60),((i-i%60)/60),(i%60)) +  "\r\n";
				} //END IF
			found=false;
			
		}//END FOR
		
		//System.out.println(printout);
		
		return result;
	} //END missingdata
	
	

}
