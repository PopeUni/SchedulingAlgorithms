
/*Name: Jacob Metcalfe, Student Number: c3305509
 * COMP2240A1
 * Main class used in order to collect all required data from the textfile and run all cpu scheduling algorithms
 */

import java.util.*;

import src.Fcfs;
import src.Ltr;
import src.Fbv;
import src.Srt;
import src.Process;
import java.io.*;

public class A1 {

    // Function to get the dispatcher time from the input file
    private static int getDisp(String file) {
        int disp = 0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("DISP: ")) {
                    disp = Integer.valueOf(line.replace("DISP: ", ""));
                }
            }
            br.close();
        } catch (IOException e) {
            System.err.println("Caught IOException: " + e.getMessage());
        }
        return disp;
    }

    // Function to get the processes from the input file
    private static String[] getProcesses(String file) {
        ArrayList<String> processArray = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = "";
            while ((line = br.readLine()) != null) {
                if (line.contains("ID: ")) {
                    String getids = line.replace("ID: ", "");
                    processArray.add(getids);
                }
            }
            br.close();
        } catch (IOException e) {
            System.err.println("Caught IOException: " + e.getMessage());
        }
        String[] processid = processArray.toArray(new String[processArray.size()]);
        return processid;
    }

    // Function to get the different types of data from the input file
    private static Integer[] getData(String file, String type) {
        ArrayList<Integer> dataArray = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = "";
            while ((line = br.readLine()) != null) {
                if (line.contains("Arrive: ") && type.equals("Arrive")) {
                    String getarrive = line.replace("Arrive: ", "");
                    dataArray.add(Integer.valueOf(getarrive));
                }
                if (line.contains("ExecSize: ") && type.equals("ExecSize")) {
                    String getarrive = line.replace("ExecSize: ", "");
                    dataArray.add(Integer.valueOf(getarrive));
                }
                if (line.contains("Tickets: ") && type.equals("Tickets")) {
                    String getarrive = line.replace("Tickets: ", "");
                    dataArray.add(Integer.valueOf(getarrive));
                }
            }
            br.close();
        } catch (IOException e) {
            System.err.println("Caught IOException: " + e.getMessage());
        }
        Integer[] data = dataArray.toArray(new Integer[dataArray.size()]);
        return data;
    }

    // Function to get the random values from the inputfile
    private static Integer[] getRandom(String file) {
        ArrayList<Integer> randomArray = new ArrayList<>();
        String line;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            // Skip the lines in the file until after BEGINRANDOM
            while ((line = br.readLine()) != null && !line.equals("BEGINRANDOM")) {
            }
            // Retrieve the lines in the file before ENDRANDOM
            while ((line = br.readLine()) != null && !line.equals("ENDRANDOM")) {
                randomArray.add(Integer.valueOf(line));
            }
            br.close();
        } catch (IOException e) {
            System.err.println("Caught IOException: " + e.getMessage());
        }
        Integer[] random = randomArray.toArray(new Integer[randomArray.size()]);
        return random;
    }

    public static void main(String[] args) {

        try {
            String dataFile = args[0]; // user will enter a datafile they wish to test

            // Checks if the file exists
            File tempFile = new File(dataFile);
            boolean exists = tempFile.exists();

            if (exists) {
                // Assign data retrieved from the functions to different variables
                int disp = getDisp(dataFile);
                String[] processes = getProcesses(dataFile);
                Integer[] arrival_time = getData(dataFile, "Arrive");
                Integer[] execsize = getData(dataFile, "ExecSize");
                Integer[] tickets = getData(dataFile, "Tickets");
                Integer[] random = getRandom(dataFile);

                int n = processes.length;

                // Creates a class to store the Process ID, ExecSize, Arrival Time, Tickets
                Process proc[] = new Process[n];

                for (int i = 0; i < n; i++)
                    proc[i] = new Process(processes[i], execsize[i], arrival_time[i], tickets[i]);

                // Executes the different algorithms
                Fcfs fcfs = new Fcfs();
                float fcfs_avg[] = fcfs.findavgTime(proc, proc.length, disp);

                Srt srt = new Srt();
                float srt_avg[] = srt.findavgTime(proc, proc.length, disp);

                Fbv fbv = new Fbv();
                float fbv_avg[] = fbv.findavgTime(proc, proc.length, disp);

                Ltr ltr = new Ltr();
                float ltr_avg[] = ltr.findavgTime(proc, proc.length, disp, random);

                // Displays the Summary of the different algorithms
                System.out.println("\nSummary");
                System.out.println("Algorithm  Average Turnaround Time  Waiting Time");
                System.out.format("%-10s %-24s %s \n", "FCFS", String.format("%.2f", fcfs_avg[0]),
                        String.format("%.2f", fcfs_avg[1]));
                System.out.format("%-10s %-24s %s \n", "SRT", String.format("%.2f", srt_avg[0]),
                        String.format("%.2f", srt_avg[1]));
                System.out.format("%-10s %-24s %s \n", "FBV", String.format("%.2f", fbv_avg[0]),
                        String.format("%.2f", fbv_avg[1]));
                System.out.format("%-10s %-24s %s \n", "LTR", String.format("%.2f", ltr_avg[0]),
                        String.format("%.2f", ltr_avg[1]));
            } else {
                System.out.println("DATA FILE NOT FOUND");
            }
        } catch (Exception e) {
            // Datafile not found
            System.out.println("Error textfile not found");
        }

    }
}