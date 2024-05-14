package assignmentfop;
//Import all Libraries needed
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class DateTime {
    //Declare variables
    static long diffInTime,totalDiffInTimeInMilisec = 0;
    static int count = 0;
    //Create Method to return difference in time
    public static long findDiff (String startDate, String endDate) {
        //Set Date Format
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        try {
            Date d1 = sdf.parse(startDate);
            Date d2 = sdf.parse(endDate);
            //Calculate difference in time
            diffInTime = (long)d2.getTime() - (long)d1.getTime(); //.getTime() returns result in miliseconds
            totalDiffInTimeInMilisec += diffInTime;
            count++;
            return diffInTime;
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    //Create Method to return total jobs
    public int jobsNum () {
        return count;
    }

    //Create Method to return average execution time
    public static long calcAverage () {
        return (totalDiffInTimeInMilisec/count);
    }

    //Create Method to Convert time into Days, Hours, Minutes, Seconds and Miliseconds
    public static void convertTime (long time) {
        long diffInMiliseconds = time%1000;
        long diffInSeconds = (time/1000)%60;
        long diffInMinutes = (time/(1000*60))%60;
        long diffInHours = (time/(1000*60*60))%24;
        long diffInDays = (time/(1000*60*60*24))%365;

        //Output Average execution time
        System.out.print("Average Execution Time of the Job Submitted to UMHPC: ");
        System.out.println(diffInDays + " days, " + diffInHours + " hours, " + diffInMinutes + " minutes, " + diffInSeconds + " seconds, " + diffInMiliseconds + " miliseconds.");
    }
    
}