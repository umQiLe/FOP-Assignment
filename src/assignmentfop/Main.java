package assignmentfop;

/**
 * 
 */

//Import libraries needed
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;

import org.jfree.ui.RefineryUtilities;
//Main Class
public class Main {
    public static void main(String[] args) throws IOException, ParseException {
//-------------------------------------Declaration and Initialization----------------------------------------------//
        //Regular Expression Pattern to find the lines with certain information
        Pattern start = Pattern.compile("(\\d{4}-\\d{2}-\\w{5}:\\d{2}:\\w{2}.\\w{3}] sched: Allocate JobId=\\d{5})");
        Pattern end = Pattern.compile("(\\d{4}-\\d{2}-\\w{5}:\\d{2}:\\w{2}.\\w{3}] _job_complete: JobId=\\d{5} done)");
        Pattern nError = Pattern.compile("(error:\\D)");
        Pattern userName = Pattern.compile("(user='\\D\\w{2,}'|user='\\D\\w{2,}.\\w{1,}.\\w{1,}')");
        Pattern killJob = Pattern.compile("REQUEST_KILL_JOB JobId=\\w{5} uid \\d{5,}");
        Pattern prio = Pattern.compile("_slurm_rpc_submit_batch_job: JobId=\\w{5} InitPrio=\\d{3,}");
        //Variables
        String line;
        int lengthA, lengthB;
        int flag = 0; //detector : existence of content
        int errorCount = 0;
        int partition=0,v100s=0,opteron=0,k10=0,titan=0,epyc=0,k40c=0; //v100s_extra=0,v100stotal=0;
        int create=0, ended=0;
        int countKillJob = 0;
        int numberOfJobs = 0,min, max;
        double  total=0, mean;
        String startDate, endDate;
        //Array
        int [] range = new int[7];
        //Array List
        ArrayList <String> timeListStart = new ArrayList <String>();
        ArrayList <String> timeListEnd = new ArrayList <String>();
        ArrayList <String> timeListStartSorted = new ArrayList <String>();
        ArrayList <String> timeListEndSorted = new ArrayList <String>();
        ArrayList<String> log = new ArrayList<>();
        ArrayList<String> userList = new ArrayList<>();
        ArrayList<Integer> countList = new ArrayList<>();
        ArrayList<Integer> uid = new ArrayList<>();
        ArrayList<Integer> idList = new ArrayList<>();
        ArrayList<Integer> countIdList = new ArrayList<>();
        ArrayList<Integer> InitPrior = new ArrayList<>();
        List<String> JobinRange = new ArrayList<String>();
//-------------------------------------Information Process----------------------------------------------//
        try (Scanner in = new Scanner (System.in)) {
            try {
                //Read Log File
                FileInputStream doc = new FileInputStream("extracted_log");
                BufferedReader br = new BufferedReader (new InputStreamReader(doc));
                  
              while ((line = br.readLine()) != null) {
                //Match the line with the patterns
                    Matcher startPattern = start.matcher(line);
                    Matcher endPattern = end.matcher(line);
                    Matcher errorPattern = nError.matcher(line);
                    Matcher userPattern = userName.matcher(line);
                    Matcher killJobPattern = killJob.matcher(line);
                    Matcher prioPattern = prio.matcher(line);
                    //Collect lines with Job Created and Job Ended
                    while(startPattern.find()) {
                        timeListStart.add(startPattern.group());
                    }
                    while(endPattern.find()) {
                        timeListEnd.add(endPattern.group().substring(0,51));
                    }
                    //Collect lines with User Errors
                    while (errorPattern.find() && userPattern.find()) {
                        //from here will pass into your method:
                        String[] users = userPattern.group().split("'");
                        log.add(users[1]);
                        errorCount += 1;
                        flag = 1;
                    }
                    //Collect lines with Job Killed
                    while (killJobPattern.find()){
                        String[] Job = killJobPattern.group().split(" ");
                        // Extract the user id from the matched line
                        uid.add(Integer.valueOf(Job[3]));
                        countKillJob++;
                    }
                    //Collect lines with Init Priority
                    while (prioPattern.find()){
                        // Extract the init priority from the matched line
                        String[] prior = prioPattern.group().split(" ");
                        String [] values = prior[2].split("=");
                        InitPrior.add(Integer.valueOf(values[1]));
                        numberOfJobs++;
                    }
                    //Calculate Total Jobs with partitions
                    if(line.contains("Partition")&&!line.contains("error")&&line.contains("sched")){
                        partition=partition+1;
                    //Calculate Job Created in each partitions
                    if(line.contains("cpu-v100s"))
                        v100s+=1;
                    else if(line.contains("gpu-v100s"))
                        v100s+=1;
                    else if(line.contains("gpu-k10"))
                        k10+=1;
                    else if(line.contains("gpu-titan"))
                        titan+=1;
                    else if(line.contains("cpu-epyc"))
                        epyc+=1;
                    else if(line.contains("gpu-epyc"))
                        epyc+=1;
                    else if(line.contains("cpu-opteron"))
                        opteron+=1;
                    else if(line.contains("gpu-k40c"))
                        k40c+=1;
                }
              }
              //Store User into Array List
                for (String user : log) {
                    int index = userList.indexOf(user);
                    if (index == -1) {
                        userList.add(user);
                        countList.add(1);
                    } else {
                        //Accumulate number of Jobs
                        int count = countList.get(index);
                        countList.set(index, count + 1);
                    }
                }
                br.close();

                if (flag == 0) {
                    System.out.println("The file does not the information.");
                }
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            }
            //Match the time of Particular Job Starts and Ends
            for (int i = 0; i<timeListStart.size() ; i++){
                for (int j=0; j<timeListEnd.size() ; j++){
                    lengthA = timeListStart.get(i).toString().length();
                    lengthB = timeListEnd.get(j).toString().length();
                    if(timeListStart.get(i).substring(lengthA-5).equals(timeListEnd.get(j).substring(lengthB-5))){
                        timeListStartSorted.add(timeListStart.get(i).substring(0,22)); 
                        timeListEndSorted.add(timeListEnd.get(j).substring(0,22));     
                    }
                }
            }
            //Calculate the Average Execution Time
            for (int i = 0; i<timeListStartSorted.size() ; i++)
                DateTime.findDiff(timeListStartSorted.get(i), timeListEndSorted.get(i));
            long time = DateTime.calcAverage();

//--------------------------------------------------Output--------------------------------------------------------//
            //State the options
            System.out.println("Assignment [Fundamental of Programming]:\n");
            System.out.println("Please select an option to view:");
            System.out.println("1. Average Execution Time of Job\n2. Total Error\n3. Total Number of Job by Partition");
            System.out.println("4. Number of Job Created and Ended\n5. Total Number of Job Killed");
            System.out.println("6. Range of InitPrio Values\nINPUT -1 TO EXIT");
            int option = in.nextInt();
            //Output Average Time for Job Execution
            while (option!=-1) {
                if (option == 1){
                    DateTime.convertTime(time); //Call method from different class
                    //Ask user for next input
                    System.out.println("\nAny other option?");
                    System.out.println("2. Total Error\n3. Total Number of Job by Partition");
                    System.out.println("4. Number of Job Created and Ended\n5. Total Number of Job Killed");
                    System.out.println("6. Range of InitPrio Values\nINPUT -1 TO EXIT");
                    option = in.nextInt();
                }
                //Output Error done by User
                else if (option == 2){
                    System.out.print("Total number of error count: ");
                    System.out.print(errorCount+ "\n");
                    System.out.println("\nAny other option?");
                    System.out.println("1. Number of Error by Each User\nINPUT -1 TO EXIT");
                    option = in.nextInt();
                    if (option == 1){
                        //Convert ArrayList to Array
                        String [] user = userList.toArray(new String[userList.size()]);
                        int[] count = countList.stream().mapToInt(Integer::intValue).toArray();
                        //Create BarChart
                        BarChart("FOP Assignment", "Error Count",user , count);
                        System.out.println("Number of error count for each user: ");
                        for (int i = 0; i < userList.size(); i++) {
                        System.out.println(userList.get(i) + ": " + countList.get(i));
                        }
                        System.out.println("\nAny other option?");
                        System.out.println("1. Average Execution Time of Job\n3. Total Number of Job by Partition");
                        System.out.println("4. Number of Job Created and Ended\n5. Total Number of Job Killed");
                        System.out.println("6. Range of InitPrio Values\nINPUT -1 TO EXIT");
                        option = in.nextInt();
                    }
                //Output Job for Different Partitions
                }
                else if (option == 3){
                    System.out.println("Total Number of job by partition: "+ partition);
                    System.out.println("\nAny other option?");
                    System.out.println("1. Number of Job by Each Partition\nINPUT -1 TO EXIT");
                    option = in.nextInt();
                    if (option == 1){
                        String [] sPartition = {"v100s","k10","titan","epyc","opteron","k40c"};
                        int [] amount = {v100s,k10,titan,epyc,opteron,k40c};
                        PieChart("FOP Assignment", "Number of Job by Partition",sPartition , amount);
                        System.out.println("Number of Job by Each Partition: ");
                        System.out.println("| Partition | Number of Job |");
                        System.out.println("-----------------------------");
                        System.out.println("v100s: \t\t"+v100s);
                        System.out.println("k10: \t\t"+k10);
                        System.out.println("titan: \t\t"+titan);
                        System.out.println("epyc: \t\t"+epyc);
                        System.out.println("opteron:\t"+opteron);
                        System.out.println("k40c: \t\t"+k40c);
                        System.out.println("\nAny other option?");
                        System.out.println("1. Average Execution Time of Job\n2. Total Error");
                        System.out.println("4. Number of Job Created and Ended within a Time Range\n5. Total Number of Job Killed");
                        System.out.println("6. Range of InitPrio Values\nINPUT -1 TO EXIT");
                        option = in.nextInt();
                        }
                    }
                //Output the Job Created and Ended in a range of Time
                else if (option == 4){
                    create=0; ended=0;
                    System.out.println("Enter the Range:");
                    System.out.print("Start Date [yyyy-mm-dd]: ");
                    startDate = in.next();
                    System.out.print("End Date [yyyy-mm-dd]: ");
                    endDate = in.next();
                    //Call method from other class
                    JobinRange.addAll(Range.readdata(startDate, endDate));
                    for (int i = 0; i<JobinRange.size(); i++){
                        if (JobinRange.get(i).contains("sched: Allocate"))
                            create++;
                        else if (JobinRange.get(i).contains("done"))
                            ended++;
                    }
                    String [] sCreate = {"Created","Ended"};
                    int [] iCount = {create, ended};
                    PieChart("FOP Assignment", "Number of Job Created and Ended",sCreate , iCount);
                    System.out.println("Number of jobs created: "+create);
                    System.out.println("Number of jobs ended: "+ended);
                    System.out.println("\nAny other option?");
                    System.out.println("1. Average Execution Time of Job\n2. Total Error\n3. Total Number of Job by Partition");
                    System.out.println("5. Total Number of Job Killed");
                    System.out.println("6. Range of InitPrio Values\nINPUT -1 TO EXIT");
                    option = in.nextInt();
                }
                //Output Job Killed
                else if (option == 5){
                    System.out.println("Total number of Job Killed: ");
                    System.out.println(countKillJob);
                    for (int id : uid) {
                        int index = idList.indexOf(id);
                        // Check if the user id is already in the list
                        if (index == -1) {
                            // If not, add it to the list and set its count to 1
                            idList.add(id);
                            countIdList.add(1);
                        } 
                        else {
                            // If it is, increment the count
                            int count = countIdList.get(index);
                            countIdList.set(index, count + 1);
                        }
                    }
                    System.out.println("\nAny other option?");
                    System.out.println("1. Number of Job Killed for Each User\nINPUT -1 TO EXIT");
                    option = in.nextInt();
                    if (option == 1){
                        System.out.println("Number of Job Killed for each user: ");
                        for (int i = 0; i < idList.size(); i++) {
                            System.out.println(idList.get(i) + ": " + countIdList.get(i));
                        }
                        System.out.println("\nAny other option?");
                        System.out.println("1. Average Execution Time of Job\n2. Total Error\n3. Total Number of Job by Partition");
                        System.out.println("4. Number of Job Created and Ended");
                        System.out.println("6. Range of InitPrio Values\nINPUT -1 TO EXIT");
                        option = in.nextInt();
                    }
                }
                //Determine the Range of Init Priority using for-loop
                else if (option == 6){
                    min = InitPrior.get(1);
                    max = InitPrior.get(1);
                    for(int value : InitPrior){
                        // Find the min and max values
                        if (value < min){
                            min = value;
                        }
                        if (value > max){
                            max = value;
                        }

                        // Count the number of init priority values in each range
                        if(value>= 0 && value <=10000)
                            range[0] += 1;
                        else if(value > 10000 && value <=20000)
                            range[1] += 1;
                        else if (value > 20000 && value <= 30000)
                            range[2] += 1;
                        else if (value > 30000 && value <= 40000)
                            range[3] += 1;
                        else if (value > 40000 && value <= 50000)
                            range[4] += 1;
                        else if (value > 50000 && value <= 60000)
                            range[5] += 1;
                        else if (value > 60000 && value <= 70000)
                            range[6] += 1;

                        total += value;
                    }
                    // Output the mean init priority
                    mean = total / numberOfJobs;
                    System.out.println("The range of InitPrio values from " + min + " to " + max);
                    System.out.println("__________________________________________________________");
                    System.out.println("InitPrio values from      0 to 10000:" + range[0]);
                    System.out.println("InitPrio values from  10001 to 20000:" + range[1]);
                    System.out.println("InitPrio values from  20001 to 30000:" + range[2]);
                    System.out.println("InitPrio values from  30001 to 40000:" + range[3]);
                    System.out.println("InitPrio values from  40001 to 50000:" + range[4]);
                    System.out.println("InitPrio values from  50001 to 60000:" + range[5]);
                    System.out.println("InitPrio values from  60001 to 70000:" + range[6]);
                    System.out.println("The number of jobs : " + numberOfJobs);
                    System.out.printf("Mean InitPrio: %.2f", mean);
                    System.out.println("\n\nAny other option?");
                    System.out.println("1. Average Execution Time of Job\n2. Total Error\n3. Total Number of Job by Partition");
                    System.out.println("4. Number of Job Created and Ended\n5. Total Number of Job Killed");
                    System.out.println("INPUT -1 TO EXIT");
                    option = in.nextInt();
                    }
                    else {
                    //Ask for input again
                    System.err.println("Please enter valid number!");
                    System.out.println("\nAny other option?");
                    System.out.println("1. Average Execution Time of Job\n2. Total Error\n3. Total Number of Job by Partition");
                    System.out.println("4. Number of Job Created and Ended\n5. Total Number of Job Killed");
                    System.out.println("6. Range of InitPrio Values\nINPUT -1 TO EXIT");
                    option = in.nextInt();
                    }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        System.out.println("End of the Analysis!");
        System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        
    }

    //Method to form BarChart
    public static void BarChart(String appTitle, String chartTitle, String [] X_DATA, int [] Y_DATA) {
        BarChart_AWT chart = new BarChart_AWT(appTitle, chartTitle, X_DATA, Y_DATA);
        chart.pack();
        RefineryUtilities.centerFrameOnScreen( chart );
        chart.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        chart.setVisible( true );
    }
    //Method to form PieChart
    public static void PieChart(String appTitle, String chartTitle, String [] X_DATA, int [] Y_DATA) {
        PieChart CC = new PieChart(appTitle, chartTitle, X_DATA, Y_DATA);
        CC.pack();
        CC.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        CC.setVisible(true);
    }
}
