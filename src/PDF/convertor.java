package PDF;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class convertor {
	
	static TimeZone Poland = TimeZone.getTimeZone("GMT+00:00");
	static SimpleDateFormat HHMMformatter= new SimpleDateFormat("HH:mm");
	static SimpleDateFormat DDformatter = new SimpleDateFormat("DD");
	static SimpleDateFormat YYYYMMDDformatter = new SimpleDateFormat("yyyy-MM-dd");
	static SimpleDateFormat YYYYMMDDHHMMformatter = new SimpleDateFormat ("yyyy-MM-dd HH:mm");
	static SimpleDateFormat godz = new SimpleDateFormat("HH;mm");
	static SimpleDateFormat doNazwy = new SimpleDateFormat("yyyy.MM.dd");
	static SimpleDateFormat day = new SimpleDateFormat("EEEEE");
	
	public static String stringFromDate (Date date){
			YYYYMMDDformatter.setTimeZone(Poland);
			String result = YYYYMMDDformatter.format(date);
		return result;
	}
	
	public static int intDayFromDAte (Date date){
		int result = 1;
		day.setTimeZone(Poland);
		String dayOfWeek = day.format(date);
		
	return result;
}
	
	public static Long getMaxValue ( Long[] stopHoursWorknotes){
		Long result = -1L;
		for (int i = 0 ; i < stopHoursWorknotes.length ; i++){
			if (stopHoursWorknotes[i]> result){
				result = stopHoursWorknotes[i];
			} //End if
		}//End for
		
		
		return result;
			
	}
	
	public static String dayOfTheYear (String day) throws ParseException{
		String dayoftheyear = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setTimeZone(Poland);
	
		Date date = sdf.parse(day);

	    Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
	    
		dayoftheyear = String.valueOf(calendar.get(calendar.DAY_OF_YEAR));
	    	    
	return dayoftheyear;
}
	
	public static int DatumStringToHour(String datum) throws ParseException { 
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setTimeZone(Poland);
		Date date = null;
		date = sdf.parse(datum);
		
		int hours = (int) ((date.getTime()/(1000 * 60 * 60)) % 24);
	    int min   = (int) ((date.getTime()/(1000 * 60 )) % 60);
	    	   
		    
	    return hours;
	}

	public static int DatumStringToMin(String datum) throws ParseException { 
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setTimeZone(Poland);
		Date date = null;
		date = sdf.parse(datum);
		
		int hours = (int) ((date.getTime()/(1000 * 60 * 60)) % 24);
	    int min   = (int) ((date.getTime()/(1000 * 60 )) % 60);
	    	   
	     
	    return min;
	}
	
	
	public static Long LongFromStringDateTime(String datum) throws ParseException { 
		// working and tested 21/05/2018  format datum HH:MM
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setTimeZone(Poland);
		
		Date date = sdf.parse(datum);
		
		Long result = date.getTime();
    return result;
	}
	
	public static Long LongFromStringTime (String datum) throws ParseException { 
		// working and tested 21/05/2018  format datum HH:MM
		//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		sdf.setTimeZone(Poland);
		
		Date date = sdf.parse(datum);
		
		Long result = date.getTime();
    return result;
}
	public static String timeFromInt(int hour, int min){
		String result = String.format("%02d:%02",hour,min);
		return result;
		
	}
	
	
	
	
}
