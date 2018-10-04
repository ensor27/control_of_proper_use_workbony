package PDF;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class Main_class {
	private static final String FONT = null;

	static Connection connection= WB.Connection2DB.dbConnector();
	
	private static Font catFont ;
	private static Font smallFont;
	private static Font smallFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 6);
	private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12,Font.BOLD);
	private static Font smallFont2RED = new Font(Font.FontFamily.TIMES_ROMAN, 6);
	
	
	//Font importantNoticeFont = FontFactory.getFont("Courier", 9, BaseColor.RED);
	
	static SimpleDateFormat HHMMformatter= new SimpleDateFormat("HH:mm");
	static SimpleDateFormat DDformatter = new SimpleDateFormat("DD");
	static SimpleDateFormat YYYYMMDDformatter = new SimpleDateFormat("yyyy-MM-dd");
	static SimpleDateFormat YYYYMMDDHHMMformatter = new SimpleDateFormat ("yyyy-MM-dd HH:mm");
	static SimpleDateFormat godz = new SimpleDateFormat("HH;mm");
	static SimpleDateFormat doNazwy = new SimpleDateFormat("yyyy.MM.dd");

	private static Object console;

	

	public static void createRaport() throws DocumentException, SQLException, ParseException, IOException{
		
		//FontFactory.register(Main_class.class.getClassLoader().getResource("times.ttf").toString(), "times");
		catFont = FontFactory.getFont("times", BaseFont.CP1250, BaseFont.EMBEDDED, 18);
		smallFont = FontFactory.getFont("times", BaseFont.CP1250, BaseFont.EMBEDDED, 12);
				
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getTimeZone("GMT-02:00"));
		
		String path = Parameters.getPathToSaveHours()+"/"+doNazwy.format(calendar.getTime())+"/";
		//int actualactualyear       	= calendar.get(Calendar.YEAR);
		//int actualmonth      		= calendar.get(Calendar.DAY_OF_MONTH);
		//int actualday		   		= calendar.get(Calendar.DAY_OF_MONTH);
		//int day_of_week		   		= calendar.get(Calendar.DAY_OF_WEEK);

		//define start and end date
			int numberofdays = 30;
			String[] checkdates = new String[numberofdays];
			int[] daynumbers = new int[numberofdays];
			String[] daydescription = new String[numberofdays];
			String[] strDays = new String[] { "Sunday", "Monday", "Tuesday", "Wednesday", "Thusday",  "Friday", "Saturday"};

			
		// create an array for all dates to be checked
		for(int i = checkdates.length; i>0 ; i--){
				calendar.add(Calendar.DAY_OF_MONTH, -1);
				checkdates[i-1]= convertor.stringFromDate(calendar.getTime());		// all dates to be checked in String version
				daynumbers[i-1]= calendar.get(Calendar.DAY_OF_WEEK) ;  				// sunday number one
				//int test = convertor.intDayFromDAte(calendar.getTime());
				daydescription[i-1]=strDays[calendar.get(Calendar.DAY_OF_WEEK)-1];
		} //END FOR
			

		
    	//create the directory for dump file
			File theDir = new File(path);
			if (!theDir.exists()) {
			    try{
			        theDir.mkdir();
			    } 
			    catch(SecurityException se){
			        //handle it
			    }
			}
		//end creation directory
		
		//create the dump file 
			String name2 	= "controller.txt"; //txt file
			File fdump = new File(path+name2);
					
			if(fdump.exists() && !fdump.isDirectory()){
				//name = godz.format(calendar.getTime())+" "+name;
				//name2 = godz.format(calendar.getTime())+" "+name2;
				fdump.delete();
			}

			FileOutputStream fos = new FileOutputStream(path+name2);
			PrintStream ps = new PrintStream(fos);
			PrintStream console = new PrintStream(new FileOutputStream(FileDescriptor.out));
			System.setOut(ps); 
			System.setOut(console);
		//end creation file
      
        //get all divisions in FAT
	        ArrayList<String> nests 		= new ArrayList<String>();
	        String sql10 = "SELECT DISTINCT pracownicy.NEST FROM fatdb.pracownicy WHERE nest IS NOT NULL AND nest <> 'BURO'";
			Statement st10 = connection.createStatement();
			ResultSet rs10 = st10.executeQuery(sql10);
			
			while(rs10.next()){
				nests.add(rs10.getString(1));
			}
			st10.close();
			rs10.close();
			//System.setOut(ps);System.out.println(nests.toString());
		//end all divisions in FAT
		
		
		//create data table for workername , workernumber, shift, nest
			Table_X table = new Table_X();
		// end creation data tabel
		
		//fill table
			for(int i=0; i<nests.size();i++){
				String sql20 = "SELECT pracownicy.NAAM , Werknemer, WERKREGIME FROM fatdb.pracownicy WHERE nest ='"+ nests.get(i) +"' AND DATUMUITDIENST IS NULL AND NAAM NOT LIKE  'AWARIA%'" ;
				
				   Statement st20 = connection.createStatement();
				   ResultSet rs20 = st20.executeQuery(sql20);
				   while(rs20.next()){
						 table.add(rs20.getString("NAAM"), rs20.getString("WERKNEMER"), rs20.getString("WERKREGIME"), nests.get(i));;
						 System.setOut(ps);
						 System.out.println(nests.get(i) + " <NOTHING> " + rs20.getString("NAAM") + " " + rs20.getString("WERKNEMER") + " " + rs20.getString("WERKREGIME")  );
				   }
				   st20.close();	 
				   rs20.close();
			}// END FOR
	
			   
		
		for(int i=0; i<table.size();i++){
			 	String sql25 = "select id_karty from fat.cards_name_surname_nrhacosoft where HacoSoftnumber = " + table.number.get(i);
				Statement st25 = connection.createStatement();
				ResultSet rs25 = st25.executeQuery(sql25);
				while(rs25.next()){
					
					table.idcardno.set(i, rs25.getString(1));
				}
				st25.close();
				rs25.close();
		}//END FOR
			
			
			
			
			
			
		//END FILL TABLE
	
		//table.display(ps);
		
		String pdfString 		= "";
		
		
		for(int i=0; i<table.size();i++){
			Worknotetable worknotes = new Worknotetable(table.nameworker.get(i),table.number.get(i),table.regime.get(i),table.nests.get(i),table.idcardno.get(i));
			Worknotetable present = new Worknotetable(table.nameworker.get(i),table.number.get(i),table.regime.get(i),table.nests.get(i),table.idcardno.get(i));
			
			String nest 			= table.nests.get(i);
			
			String pdfEntrance 		= "";
			String pfdPresentsleft  = "";
			String pfdWorknote		= "";
			String pdfMissingdata  	= "";
			String StrRealMissingWorknoteTime = "";
			String Strmissingpresents = "";
			int timeNoWorknotes		= 0 ;
			
			pdfString += nest + " <NAME> " + "name: " + worknotes.nameworker + "\r\n";	
			
			
				
			for(int j=0; j<checkdates.length;j++){
					pdfEntrance 	= "";
					pfdWorknote		= "";
					worknotes.day 	= checkdates[j];
					int shift 		= 0;
					int day 		= daynumbers[j];
					
					
					pdfString +=nest + " <NEW_LINE> " +"-----------------------------------------------\r\n";
					
					Timeline dayshift = new Timeline("dayshift",day,shift);
					Timeline shiftI = new Timeline("shiftI",day,1);
					Timeline shiftII = new Timeline("shiftII",day,2);
					Timeline result = new Timeline("result",day,shift);
					Timeline worknote = new Timeline("worknote",day,4);
					Timeline presents = new Timeline("presents", day, 4);
					//System.out.println(nest + " <NAME> " + "name: " + worknotes.nameworker + " on day: " + worknotes.day );
					
				//collecting all worknotes
					String sql30 = "SELECT w.datum ,w.WERKBONNUMMER, BEGINTIJDH , BEGINTIJDM60 , EINDTIJDH , EINDTIJDM60 , w.STATUS "
									+ "	, w.WERKPOST, r.OMSCHRIJVING , r.ARTIKELCODE  FROM werkuren w  left Join werkbon r  "
									+ "on w.WERKBONNUMMER=r.WERKBONNUMMER  "
									+ "where (w.werknemer= '"+ table.number.get(i)  +"' AND w.datum ='" + worknotes.day +"') "
									+ "ORDER BY BEGINTIJDH ASC, BEGINTIJDM60";
					Statement st30 = connection.createStatement();
					ResultSet rs30 = st30.executeQuery(sql30);
					worknotes.emptytable();
				    while(rs30.next()){
				    	int stophour =0 , stopmin = 0;
				    	String StrStopHour ="" , StrStopMin = "";
				    	if(rs30.getInt("EINDTIJDH")==0 && rs30.getInt("EINDTIJDM60")==0){ //procedure to replace midnight 00:00 to 23:59
				    		stophour = 23;
				    		stopmin  = 59;
				    		StrStopHour = "23";
				    		StrStopMin  = "59";
				    	}else{
				    		stophour = rs30.getInt("EINDTIJDH");
				    		stopmin  = rs30.getInt("EINDTIJDM60");
				    		StrStopHour = rs30.getString("EINDTIJDH");
				    		StrStopMin  = rs30.getString("EINDTIJDM60");
				    	}
				    	
				    	
				    	worknotes.add(rs30.getString("WERKBONNUMMER"), rs30.getString("BEGINTIJDH"), rs30.getString("BEGINTIJDM60"), StrStopHour, StrStopMin);
				    	worknote.setTimeLine(rs30.getInt("BEGINTIJDH"), rs30.getInt("BEGINTIJDM60"), stophour, stopmin);
				    	 
				    	String Status = rs30.getString("STATUS");
				    	if (rs30.getInt("STATUS") == 20){
				    	 	String sql35 = "SELECT STATUS, HOEVEELHEID , SEQ FROM werkbon WHERE AFDELINGSEQ in (select AFDELINGSEQ from werkbon where WERKBONNUMMER = "+ 
				    	 						rs30.getString("WERKBONNUMMER") +") and SEQ > (select seq from werkbon where WERKBONNUMMER = "+ rs30.getString("WERKBONNUMMER") +") order by SEQ";
							Statement st35 = connection.createStatement();
							ResultSet rs35 = st35.executeQuery(sql35);
							
							while(rs35.next()){
								if ( Status.equals("20") && rs35.getInt("HOEVEELHEID")==1){ Status = "RED:" + rs30.getString("STATUS");  }
								if ( Status.equals("90") )                                { Status = "RED:" + rs30.getString("STATUS");   }
								
							}
							st35.close();
							rs35.close();
				    	}
				    	 
				    	 
				    	 pfdWorknote += nest + " <WORKNOTES> " + rs30.getString("WERKBONNUMMER")+ " "+ rs30.getString("WERKPOST") + " "+ Status +" "+ 
				    			 		String.format("%02d:%02d<==>%02d:%02d",rs30.getInt("BEGINTIJDH"), rs30.getInt("BEGINTIJDM60"), stophour, stopmin) 
				    	 				+" "+ rs30.getString("ARTIKELCODE")+ " "+ rs30.getString("OMSCHRIJVING") +" \r\n";
				    	 
				    } //END WHILE
				    st30.close();
				    rs30.close();
				    //worknotes.display(ps);

				//collecting presents time
				    String sql40 = "select akcja , `data` from fat.access where id_karty = " + present.idcardno + " and    `data`  between '" 
							+ worknotes.day + " 00:00' and '" + worknotes.day + " 23:59' order by `data` ";
				    Statement st40 = connection.createStatement();
				    ResultSet rs40 = st40.executeQuery(sql40);
				    ArrayList<String> in_out 		= new ArrayList<String>();
				    ArrayList<String> datum 		= new ArrayList<String>();
				    String in = null;
				    String out = null;
				    pdfEntrance += nest + " <EMPTY>\r\n";
				    while(rs40.next()){
				    		in_out.add(rs40.getString(1));
				    		datum.add(rs40.getString(2));
				    		
				    } //END WHILE
				    st40.close();
				    rs40.close();	
				   				    		
				    int starthour = 0;
		    		int startmin = 0;
		    		int stophour = 0;
		    		int stopmin = 0;	
		    		pdfEntrance += nest + " <EMPTY>\r\n";
				    for(int k=0; k<in_out.size();k++)	{
				    	if (in_out.get(k).equals("wejscie")){
			    			try {
								starthour = convertor.DatumStringToHour(datum.get(k));
								startmin = convertor.DatumStringToMin(datum.get(k));
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}	
			    		}// END IF
				    	if (in_out.get(k).equals("wyjscie")){
			    			try {
								stophour = convertor.DatumStringToHour(datum.get(k));
								stopmin = convertor.DatumStringToMin(datum.get(k));
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
			    		}//ENF IF
				    	
				    	if ( (starthour + startmin) > 0 && (stophour + stopmin) > 0){
				    		presents.setTimeLine(starthour, startmin, stophour, stopmin);
				    		//System.out.println("data from bramka: ");
				    		//System.out.println(starthour+":"+ startmin +"<==>"+ stophour+":"+ stopmin);
				    		pdfEntrance += nest + " <PRESENTS> "  +String.format("%02d:%02d<==>%02d:%02d",starthour,startmin,stophour,stopmin) +  "\r\n";
				    		startmin = 0;
				    		stophour = 0;
				    		stopmin = 0;		
				    	}//ENF IF
				    }//END FOR
				    pdfEntrance += nest + " <EMPTY>\r\n";		
				    
				    //calculations:
				    
				    //selecting best shift:
				    	int DW, IW, IIW, bestfit = 0;
				    	result = dayshift;
				    	result.deductTimelines(worknote.returntimeline());
				    	DW = result.minutesleft;
				    	
				    	result = shiftI;
				    	result.deductTimelines(worknote.returntimeline());
				    	IW = result.minutesleft;
				    	
				    	result = shiftII;
				    	result.deductTimelines(worknote.returntimeline());
				    	IIW = result.minutesleft;
				    	
				    	bestfit =   (DW <= (IW <= IIW?IW:IIW))?1:-1;
				    	bestfit =  bestfit<1? (IW <= (IIW <= DW?IIW:DW)?2:3):bestfit; 
				    	// DEBUG LINE System.out.println(" best fit shedule is : " + bestfit + " DW: " + DW  + " IW: " + IW  + " IIW: " + IIW);
				    //END SELECTION BEST FIT
				    
				    	
				    	if(worknote.minutesleft ==0 && presents.minutesleft==0){
				    	   		pdfString +=  nest + " <NO_DATA> " + worknotes.day + " " + daydescription[j]  + " NO DATA\r\n";
				    	}//END IF
				    	
				    	if(worknote.minutesleft ==0 && !(presents.minutesleft==0)){
			    	   		pdfString +=  nest + " <NO_DATA_RED> " + worknotes.day + " " + daydescription[j]  + " NO DATA WORKNOTES\r\n";
				    	}//END IF
				    	if(!(worknote.minutesleft ==0) && presents.minutesleft==0){
			    	   		pdfString +=  nest + " <NO_DATA_RED> " + worknotes.day + " " + daydescription[j]  + " NO DATA FROM BRAMKA\r\n";
				    	}//END IF
				    	
				    	
				    	if(worknote.minutesleft >0 || presents.minutesleft >0){ //some data available
				    		switch (bestfit) {
				    			case 1:  // dayshift
				    				result = dayshift;
				    				result.name = "Dayshift";
				    				break;
				    			case 2:  // I shift
				    				result = shiftI;
				    				result.name = "Shift I";
				    				break;
				    			case 3:
				    				result = shiftII;
				    				result.name = "Shift II";
				    				break;
				    		
				    		} //end switch
				    		
				    		// the present time of the worker that day.
				    		
				    		pfdPresentsleft = presents.strMinutesLeft; 
				    		
				    		// result contains the best fit shift.
				    		// now deduct all worknotes and see for how many minutes we miss worknote data
				    			    		
				    		result.deductTimelines(worknote.returntimeline());
				    		String pdfMissingWorknote = result.strMinutesLeft;
				    		
				    		// result contains best fit shift - all worknote data
				    		// now deduct the present time in FAT and to check if worker didn`t leave the FAT in the meantime
				    		
				    		result.IntersectTimelines(presents.returntimeline());
				    		StrRealMissingWorknoteTime = result.strMinutesLeft;
				    		
				    		// deduct the present time from the worknote time to check if all worknotes were properly closed

				    		worknote.deductTimelines(presents.returntimeline());
				    		Strmissingpresents = worknote.strMinutesLeft;
				    		
				    		// DEBUGGING CODE
				    		//System.out.println( worknotes.nameworker + " " + worknotes.day + " " + daydescription[j]  + " shift-workn " + dayshift.minutesleft + " + with pres: "+ StrRealMissingWorknoteTime + " pres: " 
				    		//		+ pfdPresentsleft + " work-pres: " + Strmissingpresents);
				    
				    		//System.out.println("missing data: " + dayshift.missingdata(nest));
				    		// END DEBUGGING CODE
				    	
				    		
				    	// compose pdfString:
				    	// ------------------
				    		pdfString += nest + " <DAY> " + worknotes.day + " " + daydescription[j] + " was present: " + pfdPresentsleft + "  " +  worknotes.nameworker + " " + worknotes.nummerworker +  "\r\n";
				    		pdfString += pdfEntrance  + "\r\n" ;
				    		
				    		System.setOut(console);
				    		if(!result.missingdata(nest).isEmpty() ){
			    				pdfString  += nest + " <AktivRed>\r\n";
			    				}
				    		if(!result.missingdata(nest).isEmpty() ){
				    			pdfString +=  result.missingdata(nest) + "\r\n";
			    				}
				    		
				    		pdfString += nest + " <INFO> " + result.name + " time - Worknotes time (missing worknote hours)    	: " + pdfMissingWorknote + "\r\n";
				    		pdfString += nest + " <INFO> " + result.name + " time - Worknotes time - presents time 				: " + StrRealMissingWorknoteTime + "\r\n";	
				    		
				    		if (Strmissingpresents.equals("00:00")){
				    			pdfString += nest + " <INFO> " + "Worknotes - presents        : " + Strmissingpresents + "\r\n";
				    		}else{
				    			pdfString += nest + " <INFO_RED> " + "Worknotes - presents        : " + Strmissingpresents + "\r\n";
				    		}
				    		pdfString += pfdWorknote  ;
				    		
				    		System.out.println( nest + " <INFO> " + result.name + " time - Worknotes time (missing worknote hours)    	: " + pdfMissingWorknote );
				    		System.out.println( nest + " <INFO> " + result.name + "  time - Worknotes time - presents time 				: " + StrRealMissingWorknoteTime  );
				    		System.out.println( nest + " <INFO> " + "Worknotes - presents        : " + Strmissingpresents  );
				    		
				    		
				    	}//END IF
				    
				    	//worknotes.display(console);
				    	// prepare data for pdf
				    	
				    
				    
				  
			}// END FOR j  end checking every day
			System.out.println("===============FINISH===============================");
			
		}//END FOR i
		
		System.setOut(ps);
		System.out.println(pdfString);
		ps.flush();
		ps.close();
		//******************************************************************************
		//**************************Creating PDFs***************************************
		//******************************************************************************
		
		
		for(int i=0 ; i <nests.size();i++){  
			
				String name = "control"+  nests.get(i).replaceAll("/", "") +".pdf"; 
				
				File f = new File(path+name);
				
				//System.out.println(path+name);		
				if(f.exists() && !f.isDirectory()){
					name = godz.format(calendar.getTime())+" "+name;
					//System.out.println(path+name);	
				}
				
				Document document = new Document();
				PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(path+name));
				document.open();
				writer.setPageEvent(new PDF_MyFooter());
				
				//header for pdf
					Paragraph preface = new Paragraph();
			        preface.add("\n");
			        preface.add(new Paragraph("worknotes during period: " + checkdates[0] + " <==> " + checkdates[checkdates.length-1], catFont));
			        preface.add("\n");
			        document.add(preface);
		        //end pdf header
		
			    //fill pdf data
			        PdfPTable tabPDF = new PdfPTable(7);
			        float widths[] = new float[] { 8, 6, 4, 16, 20, 39, 1};
			        tabPDF.setWidths(widths);
			        tabPDF.setWidthPercentage(100);
			       

			         
			        try {
			        	String line;
			        	String txtfile = "controller.txt";
						BufferedReader bufferreader = new BufferedReader(new FileReader(path+txtfile));

			            while ((line = bufferreader.readLine()) != null) {   //open everytime the cumulated txt file
			            	
			            	String[] words = line.split(" ");     			// split every sentence into stringarray
			            	
			            	
			            	if (words[0].equals(nests.get(i))){  			// filter out all data for every nest
			            		//System.setOut(console);
			            		//for (int l=1; l<words.length; l++){			// select for every line which syntax is needed
			            		//System.setOut(console);
			            		//System.out.println( "   " + words[1]);
			            		PdfPCell cellspace = new PdfPCell(new Phrase(""));
			            	
			            		
			            		Font font = new Font(FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.BLACK);
			            		Font red = new Font(FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.RED);
			            		Font blue = new Font(FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.BLUE);
			            		Font fontworknote = new Font(FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.DARK_GRAY);
			            		Font fontWorknoteRed = new Font(FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.RED);
			            		
			            			 switch (words[1]) {
					            	         case "<NAME>":
					            	        	 	//ADD ONE SPACE LINES
					            	    				
							            	    			cellspace.setMinimumHeight(10);
							            	    			cellspace.setHorizontalAlignment(Element.ALIGN_CENTER);
							            	    			cellspace.setVerticalAlignment(Element.ALIGN_MIDDLE);
							            	    			cellspace.setColspan(7);
							            	    			cellspace.setRowspan(1);
							            	    			cellspace.setBorder(Rectangle.NO_BORDER);
							            	    			cellspace.setPhrase(new Phrase("  "));
							            	       	        tabPDF.addCell(cellspace);
							            	       	        
							            	       	        cellspace.setMinimumHeight(30);
							            	       	        cellspace.setHorizontalAlignment(Element.ALIGN_CENTER);
							            	       	        cellspace.setVerticalAlignment(Element.ALIGN_MIDDLE);
							            	       	        cellspace.setBackgroundColor(BaseColor.ORANGE);
							            	       	        cellspace.setBorder(Rectangle.BOX);
							            	       	        cellspace.setColspan(7);
							            	       	        cellspace.setPhrase(new Phrase(words[3]+ " "+ words[4] ));
							            	       	        tabPDF.addCell(cellspace);
						            	       	        
						            	       	   //ADD ONE SPACE LINES
						            	    			//PdfPCell cellspace = new PdfPCell(new Phrase(""));
							            	    			cellspace.setMinimumHeight(10);
							            	    			cellspace.setHorizontalAlignment(Element.ALIGN_CENTER);
							            	    			cellspace.setVerticalAlignment(Element.ALIGN_MIDDLE);
							            	    			cellspace.setBackgroundColor(BaseColor.WHITE);
							            	    			cellspace.setColspan(7);
							            	    			cellspace.setRowspan(1);
							            	    			cellspace.setBorder(Rectangle.NO_BORDER);
							            	    			cellspace.setPhrase(new Phrase("  "));
							            	       	        tabPDF.addCell(cellspace);
					            	             break;
					            	         
					            	             
					            	         case "<NEW_LINE>":
					            	        	 		
					            	        	 		cellspace.setMinimumHeight(10);
					            	        	 		cellspace.setHorizontalAlignment(Element.ALIGN_LEFT);
					            	        	 		cellspace.setVerticalAlignment(Element.ALIGN_MIDDLE);
					            	        	 		cellspace.setColspan(7);
					            	        	 		cellspace.setRowspan(1);
					            	        	 		cellspace.setBorder(Rectangle.NO_BORDER);
					            	        	 		cellspace.setPhrase(new Phrase("______________________________________________________________________"));
						            	       	        tabPDF.addCell(cellspace);
					            	        	 break;
					            	        	 
					            	        	 
					            	         case "<WORKNOTES>":
					            	        	 	//pfdWorknote += nest + " <WORKNOTES> " + rs30.getString(1)+ " "+ rs30.getString(6) +" "+  String.format("%02d:%02d<==>%02d:%02d",rs30.getInt(2), rs30.getInt(3), rs30.getInt(4), rs30.getInt(5)) 
								    	 			//					+" "+ rs30.getString(9)+ " "+ rs30.getString(8) +" \r\n";
					            	        	 
					            	        	 	//System.out.println(words);
					            	    	      	PdfPCell workNoteCell = new PdfPCell();
					            	    	      	//Font color = words[6].equals("POPRAWKI")?fontWorknoteRed:fontworknote;
					            	    	      	Font color = fontworknote;
					            	    	      	
					            	    	      	//cell 1
						            	    	      	workNoteCell.setColspan(1);
						            	    	      	workNoteCell.setPhrase(new Phrase(words[2],fontworknote));
						            	    	      	workNoteCell.setBorder(Rectangle.BOX);
						            					tabPDF.addCell(workNoteCell);
					            					
					            					//cell 2
						            					workNoteCell.setColspan(1);
						            					workNoteCell.setPhrase(new Phrase(words[3] ,fontworknote)); //workstation
						            					workNoteCell.setBorder(Rectangle.BOX);
						            					tabPDF.addCell(workNoteCell);
					            					
					            					//cell 3 
						            					
						            					if (words[4].startsWith("RED")){
						            						System.setOut(console);
							            					System.out.println(" red detected: "+ words[4] +" "+ words[4].length() + " "+ words[2]);
							            					words[4]= words[4].substring(4) ;
							            					color = fontWorknoteRed;
						            					}
						            										
						            					
						            					workNoteCell.setColspan(1);
						            					workNoteCell.setPhrase(new Phrase( words[4] , color));
						            					workNoteCell.setBorder(Rectangle.BOX);
						            					tabPDF.addCell(workNoteCell); 
					            	        	 
					            					//cell 4
						            					workNoteCell.setColspan(1);
						            					workNoteCell.setPhrase(new Phrase(words[5],fontworknote ));
						            					workNoteCell.setBorder(Rectangle.BOX);
						            					tabPDF.addCell(workNoteCell); 
					            	        	 
					            					//cell 5
						            					workNoteCell.setColspan(1);
						            					workNoteCell.setPhrase(new Phrase(words[6],fontworknote ));
						            					workNoteCell.setBorder(Rectangle.BOX);
						            					tabPDF.addCell(workNoteCell); 
						            					
						            				//cell 6
						            					String workNoteCellData = "";
						            					color =words[6].equals("POPRAWKI")?fontWorknoteRed:fontworknote;
						            					for(int h=7; h<words.length;h++){ workNoteCellData += words[h] + " "; }
						            					workNoteCell.setColspan(1);
						            					workNoteCell.setPhrase(new Phrase(workNoteCellData,color ));
						            					workNoteCell.setBorder(Rectangle.BOX);
						            					tabPDF.addCell(workNoteCell);
						            					
						            				//cell 7
						            					workNoteCell.setColspan(1);
						            					workNoteCell.setPhrase(new Phrase("",color ));
						            					workNoteCell.setBorder(Rectangle.BOX);
						            					tabPDF.addCell(workNoteCell);
					            	        	 
					            					
					            					
					            	        	 break;
					            	        	 
					            	        	 
					            	         case "<PRESENTS>":
					            	        	 	
					            	    	      	PdfPCell presentCell = new PdfPCell();
					            	    	      	
					            	    	      	//cell 1 - 2 
						            	    	      	presentCell.setColspan(2);
						            					presentCell.setPhrase(new Phrase(" "));
						            					presentCell.setBorder(Rectangle.RIGHT);
						            					tabPDF.addCell(presentCell);
					            					
					            					//cell 3 - 4
						            					presentCell.setHorizontalAlignment(Element.ALIGN_CENTER);
						            					presentCell.setColspan(2);
						            					presentCell.setPhrase(new Phrase(words[2],fontworknote));
						            					presentCell.setBorder(Rectangle.BOX);
						            					tabPDF.addCell(presentCell);
					            					
					            					//cell  5 - 6 - 7
						            					presentCell.setColspan(3);
						            					presentCell.setPhrase(new Phrase(" "));
						            					presentCell.setBorder(Rectangle.LEFT);
						            					tabPDF.addCell(presentCell); 
					            					
					            	             break;
					            	             
					            	             
					            	         case "<NO_DATA>":  //ex W1/1B <NO_DATA>  2018-08-22 Wednesday 		  NO DATA  
					            	        	 	String noData ="";
					            	        	 	for(int h=2; h<words.length;h++){ noData += words[h] + " "; }
					            	        	 	PdfPCell noDataCell = new PdfPCell();
							            	        	
							            	        	 noDataCell.setMinimumHeight(10);
							            	        	 noDataCell.setHorizontalAlignment(Element.ALIGN_LEFT);
							            	        	 noDataCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							            	        	 noDataCell.setColspan(7);
							            	        	 noDataCell.setRowspan(1);
							            	        	 noDataCell.setBorder(Rectangle.NO_BORDER);
							            	        	 noDataCell.setPhrase(new Phrase(noData, blue));
							            	       	     tabPDF.addCell(noDataCell);
					            	             break;
					            	             
					            	             
					            	         case "<NO_DATA_RED>":  //ex W1/1B <NO_DATA>  2018-08-22 Wednesday 		  NO DATA  
					            	        	 	String noData1 ="";
					            	        	 	for(int h=2; h<words.length;h++){ noData1 += words[h] + " "; }
					            	        	 	PdfPCell noDataCell1 = new PdfPCell();
							            	        	
							            	        	 noDataCell1.setMinimumHeight(10);
							            	        	 noDataCell1.setHorizontalAlignment(Element.ALIGN_LEFT);
							            	        	 noDataCell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
							            	        	 noDataCell1.setColspan(7);
							            	        	 noDataCell1.setRowspan(1);
							            	        	 noDataCell1.setBorder(Rectangle.NO_BORDER);
							            	        	 noDataCell1.setPhrase(new Phrase(noData1, red));
							            	       	     tabPDF.addCell(noDataCell1);
					            	             break;
					            	             
					            	             
					            	         case "<INFO>":
					            	        	 String info ="";
					            	        	 	for(int h=2; h<words.length;h++){ info += words[h] + " "; }
					            	        	 	PdfPCell infoCell = new PdfPCell();
							            	        	
						            	        	 	infoCell.setMinimumHeight(10);
						            	        	 	infoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
						            	        	 	infoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						            	        	 	infoCell.setColspan(7);
						            	        	 	infoCell.setRowspan(1);
						            	        	 	infoCell.setBorder(Rectangle.NO_BORDER);
						            	        	 	infoCell.setPhrase(new Phrase(info,font));
								            	       	tabPDF.addCell(infoCell);
					            	             break;
					            	             
					            	             
					            	         case "<INFO_RED>":
					            	        	 	info ="";
					            	        	 	for(int h=2; h<words.length;h++){ info += words[h] + " "; }
					            	        	 	PdfPCell infoCellred = new PdfPCell();
							            	        	
					            	        	 		infoCellred.setMinimumHeight(10);
					            	        	 		infoCellred.setHorizontalAlignment(Element.ALIGN_LEFT);
					            	        	 		infoCellred.setVerticalAlignment(Element.ALIGN_MIDDLE);
					            	        	 		infoCellred.setColspan(7);
					            	        	 		infoCellred.setRowspan(1);
					            	        	 		infoCellred.setBorder(Rectangle.NO_BORDER);
					            	        	 		infoCellred.setPhrase(new Phrase(info,red));
								            	       	tabPDF.addCell(infoCellred);
					            	             break;  
					            	             
					            	             
					            	         case "<DAY>":
					            	        	 	info ="";
					            	        	 	for(int h=2; h<words.length;h++){ info += words[h] + " "; }
					            	        	 	PdfPCell dayCell = new PdfPCell();
							            	        	
						            	        	 	dayCell.setMinimumHeight(10);
						            	        	 	dayCell.setHorizontalAlignment(Element.ALIGN_LEFT);
						            	        	 	dayCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						            	        	 	dayCell.setColspan(7);
						            	        	 	dayCell.setRowspan(1);
						            	        	 	dayCell.setBorder(Rectangle.NO_BORDER);
						            	        	 	dayCell.setPhrase(new Phrase(info,blue));
								            	       	tabPDF.addCell(dayCell);
					            	             break;
					            	             
					            	             
					            	         case "<EMPTY>":
						            	        	 PdfPCell emptyCell = new PdfPCell();
							            	        	 emptyCell.setMinimumHeight(10);
							            	        	 emptyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
							            	        	 emptyCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							            	        	 emptyCell.setColspan(7);
							            	        	 emptyCell.setRowspan(1);
							            	        	 emptyCell.setBorder(Rectangle.NO_BORDER);
							            	        	 emptyCell.setPhrase(new Phrase(""));
						            	       	         tabPDF.addCell(emptyCell);
						            	         break;
						            	         
						            	         
					            	         case "<AktivRed>":   
					            	        	 
					            	        	 PdfPCell alertCell = new PdfPCell();
							            	        	 alertCell.setMinimumHeight(10);
							            	        	 alertCell.setHorizontalAlignment(Element.ALIGN_LEFT);
							            	        	 alertCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							            	        	 alertCell.setColspan(7);
							            	        	 alertCell.setRowspan(1);
							            	        	 alertCell.setBorder(Rectangle.NO_BORDER);
							            	        	 alertCell.setPhrase(new Phrase("                  Missing data to be completed for next time sections ....", red));
						            	       	         tabPDF.addCell(alertCell);
					            	       	    break;
					            	       	    
					            	       	    
					            	         case "<NOTHING>":   
					            	        	 
					            	        	
					            	       	    break;
						            	         
					            	         case "<TABLE_RED_MISSING_WORKNOTE>":  // nest [0] + "  <TABLE_RED_MISSING_WORKNOTE>[1] missing[2] worknote[3] data:[4] " +String.format [5]("%02d:%02d<==>%02d:%02d",
	
					            	        	 
					            	        	 PdfPCell missingCell = new PdfPCell();
					            	        	 	
					            	        	 	//cell 1 - 2
							            	        	missingCell.setMinimumHeight(10);
							            	        	missingCell.setHorizontalAlignment(Element.ALIGN_CENTER);
							            	        	missingCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							            	        	missingCell.setRowspan(1);
						            	    	      	missingCell.setColspan(2);
						            	    	      	missingCell.setBorder(Rectangle.RIGHT);
						            	    	      	missingCell.setPhrase(new Phrase(" ",red));
						            					tabPDF.addCell(missingCell);
						            				
						            				//cell 3 - 4 
						            					missingCell.setHorizontalAlignment(Element.ALIGN_CENTER);
						            					missingCell.setColspan(2);
						            					missingCell.setBorder(Rectangle.BOX);
						            					missingCell.setPhrase(new Phrase(words[5],red));
						            					tabPDF.addCell(missingCell);
						            					
						            				//cell 5 - 6 - 7 	 
						            					missingCell.setColspan(3);
						            					missingCell.setBorder(Rectangle.LEFT);
						            					missingCell.setPhrase(new Phrase(" ",red));
						            					tabPDF.addCell(missingCell); 
						            	       	    
						            	      break;
						            	         
						            	         
					            	        
					            	         default:
					            	        	 	System.setOut(console);
							            			//System.out.println("no special tags:" + words[1]+ ": "); 
			            			 }//END SWITCH
			            			
			            			
				            	
			            	}
			            	
			            	
			            	
			            	
			            	
			            	
			                //line = bufferreader.readLine();
			            } //END WHILE
			            bufferreader.close();

			        } catch (FileNotFoundException ex) {
			            ex.printStackTrace();
			        } catch (IOException ex) {
			            ex.printStackTrace();
			        }   
			        
			
      
			        
			        
			        
			        
			        
			    document.add(tabPDF);
		        document.close();
				
		}// END FOR
	
		System.setOut(console);
		System.out.println("  analyze done ");
		
	}
}
