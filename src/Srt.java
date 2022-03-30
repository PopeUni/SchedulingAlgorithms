/*Name: Jacob Metcalfe, Student Number: c3305509
 * COMP2240A1
 * Shortest Remaining Time algorithm class, used in order to run the simulation and calculate 
 * the average waiting time and turnaround time
 */

package src;

import java.util.*;

public class Srt {

    public float[] findavgTime(Process proc[], int n, int disp) {

        int wt[] = new int[n], tat[] = new int[n];
        int total_wt = 0, total_tat = 0;

        System.out.println("SRT:");

        wt = findWaitingTime(proc, n, disp);
        tat = findTurnAroundTime(proc, n, wt);
        // Function to find turn around time for
        // all processes

        // Display processes along with all
        // details

        System.out.print("\n");
        System.out.println("Processes  Turn around time  Waiting time ");

        // Calculate total waiting time and
        // total turnaround time
        for (int i = 0; i < n; i++) {
            total_wt = total_wt + wt[i];
            total_tat = total_tat + tat[i];
            System.out.println(" " + proc[i].pid + "\t\t " + tat[i] + "\t\t" + wt[i]);
        }

        float avg_tat = (float) total_tat / (float) n;
        float avg_wt = (float) total_wt / (float) n;
        float fcfs_avg[] = { avg_tat, avg_wt };

        return fcfs_avg;
    }

    // function used to calculate the waiting time of each process
    static int[] findWaitingTime(Process proc[], int n, int disp)

    {
        // Array to store the output
        int wt[] = new int[n];

        boolean sameArrival = true, check = false;
        int complete = 0, t = 0, minm = Integer.MAX_VALUE, shortest = 0, finish_time, count = 0, tempWT = 0, f = disp,
                remainingTime, processTime = 0;
        String duplicate = "";
        ArrayList<String> process_count = new ArrayList<>();

        // Linklists used to hold the process IDs, arrival time and exec size
        LinkedList<Integer> exec_size = new LinkedList<Integer>();
        LinkedList<Integer> arrival = new LinkedList<Integer>();
        LinkedList<String> process = new LinkedList<String>();

        LinkedList<Integer> exec_copy = new LinkedList<Integer>();

        // Puts the data into a linked list
        for (int i = 0; i < n; i++) {
            process.add(proc[i].pid);
            arrival.add(proc[i].at);
            exec_size.add(proc[i].es);
            exec_copy.add(proc[i].es);
        }

        String tempPID;
        int tempES, tempA, tempESCopyS;
        // sorts processes depending on their exec size
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (exec_size.get(i) > exec_size.get(j)) {
                    tempPID = process.get(i);
                    tempES = exec_size.get(i);
                    tempA = arrival.get(i);
                    tempESCopyS = exec_copy.get(i);

                    process.set(i, process.get(j));
                    exec_size.set(i, exec_size.get(j));
                    arrival.set(i, arrival.get(j));
                    exec_copy.set(i, exec_copy.get(j));

                    process.set(j, tempPID);
                    exec_size.set(j, tempES);
                    arrival.set(j, tempA);
                    exec_copy.set(j, tempESCopyS);
                }
            }
        }

        // Declare temporary variables to store when sorting the data

        // Sorts the processes in order depending on its Exec Time

        int first = arrival.get(0);
        // int first = proc[0].at;
        // Checks if all the arrival times of the processes are the same
        // By comparing the arrival times of each process to the first process
        for (int i = 0; i < n && sameArrival; i++) {
            if (arrival.get(i) != first) {
                sameArrival = false;
            }
        }

        // Executes until all processes are completed
        while (complete != n) {
            // Finds the process with the smallest remaining ExecSize and with an arrival
            // time that
            // has passed the current time and has not yet been completed (Remaining
            // ExecSize = 0)
            for (int j = 0; j < n; j++) {
                if (arrival.get(j) <= t && exec_size.get(j) < minm && exec_size.get(j) > 0) {

                    minm = exec_size.get(j);
                    shortest = j;
                    check = true;

                    // Adds another dispatcher to the time
                    // If the arrival times of the processes are not the same
                    if (!sameArrival) {
                        t = t + disp;
                        // Checks if the algorithm is missing a dispatcher in between processes
                        // Skip this if the previous process time is 0
                        if (processTime != 0 && (t - 1) == processTime) {
                            t = t + disp;
                            count++;
                        }
                        processTime = t;
                        System.out.println("T" + processTime + ": " + process.get(shortest));
                        // Stores the processes that is being executed
                        process_count.add(process.get(shortest));
                    }

                }
            }

            // Checks if the process has been executed and there is no process with an
            // arrival time
            // that has passed the current time
            // Skips the time + 1 (Idle Time)
            if (!check) {
                t++;
                continue;
            }

            // Reduce remaining execsize by 1
            remainingTime = exec_size.get(shortest) - 1;

            exec_size.set(shortest, remainingTime);

            // sortedES[shortest]--;

            // Update minimum
            minm = exec_size.get(shortest);
            if (minm == 0) {
                minm = Integer.MAX_VALUE;
            }

            // If a process is completed, calculate the waiting time of that process
            if (exec_size.get(shortest) == 0) {

                // Increment complete
                complete++;
                check = false;

                // Get finish time of current process
                finish_time = t + 1;

                // Gets the processes that are executed in parts
                String[] processid = process_count.toArray(new String[process_count.size()]);
                List<String> list = Arrays.asList(processid);
                Set<String> set = new HashSet<String>();
                for (String str : list) {
                    boolean flagForDuplicate = set.add(str);
                    if (!flagForDuplicate) {
                        duplicate = str.replace("p", "");
                    }
                }

                // Calculate waiting time
                wt[shortest] = finish_time - exec_copy.get(shortest) - arrival.get(shortest);
                // wt[shortest] = processTime - sortedAT[shortest];

                if (duplicate.equals(String.valueOf(shortest + 1))) {
                    wt[shortest] = wt[shortest] - count;
                }

                if (sameArrival) {
                    wt[shortest] = wt[shortest] + f;
                    f = f + 1;
                    processTime = wt[shortest] + arrival.get(shortest);
                    System.out.println("T" + processTime + " " + process.get(shortest));
                }

                if (wt[shortest] < 0) {
                    wt[shortest] = 0;

                }

                // for debugging purposes
                // System.out.println(sortedPID[shortest] + ": " + "waiting times: " +
                // wt[shortest]);
            }
            // Increments the time
            t++;
        }

        // The waiting time array is currently using the sorted processes
        // So it must sorted back
        // Sorts the waiting time back
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                // Checks if the previous process number(ID) is greater than the next process
                // number
                if (Integer.valueOf(process.get(i).replace("p", "")) > Integer
                        .valueOf(process.get(j).replace("p", ""))) {

                    // sorts the sortedPID array to be sorted by id
                    tempPID = process.get(i);
                    process.set(i, process.get(j));
                    process.set(j, tempPID);

                    // sorts the waiting time array so that the waiting time for each process in the
                    // sortedPID array has the correct waiting time
                    tempWT = wt[i];
                    wt[i] = wt[j];
                    wt[j] = tempWT;
                }
                // System.out.print("sorted pid is " + sortedPID[i] + " its waiting time is " +
                // wt[i] + "\n");

            }
        }

        for (int i = 0; i < n; i++) {

            // System.out.print("---> " + sortedPID[i]);

        }

        return wt;
    }

    // Method to calculate turn around time
    static int[] findTurnAroundTime(Process proc[], int n, int wt[]) {
        int tat[] = new int[n];
        for (int i = 0; i < n; i++) {
            tat[i] = proc[i].es + wt[i];
        }
        return tat;
    }

}