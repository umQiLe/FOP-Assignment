package assignmentfop;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Range{
    //Create Method to return List of lines with Job Created and Ended within a Time Range
    static List<String> readdata(String startDate, String endDate) throws IOException, ParseException {
        List<String> logList = new ArrayList<String>();
        List<String> newList = new ArrayList<String>();
        FileInputStream doc = new FileInputStream("extracted_log");
        BufferedReader br = new BufferedReader (new InputStreamReader(doc));
        String line = br.readLine();
        while (line != null) {
            logList.add(line);
            // read next line
            line = br.readLine();
        }
        br.close();
        //RegEx Pattern of Date Matched
        Pattern dateForm = Pattern.compile("([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))");
    
        Matcher matcher = null;
        String logString = null;
        Date date = null;
        //Set Date Format
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0; i < logList.size(); i++) {
            logString = logList.get(i);
            matcher = dateForm.matcher(logString);

            //Selection of Lines
            if (matcher.find()) {
                date = format.parse(matcher.group());
                
                //Parse the parameters from main class
                Date st = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(startDate);
                Date ed = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(endDate);
                
                //Select lines in the time range
                int res = st.compareTo(date);
    
                if (res <= 0) {
    
                    if (logString.contains(startDate)) {
                        {
                            newList.add(logString);
                        }
                        //continue;
                    }
                    if (date.after(ed)) {
                        break;
                    }
                    //Add the lines into the list
                    newList.add(logString);
                }
    
            }
        }
        //Return to main class
        return newList;
    }
}
