/*Name: Jacob Metcalfe, Student Number: c3305509
 * COMP2240A1
 * First Come, First Serve algorithm class, used in order to run the simulation and calculate 
 * the average waiting time and turnaround time
 */

package src;

import java.util.*;

public class Fcfs {

    public float[] findavgTime(Process proc[], int n, int disp) {
        int wt[] = new int[n], tat[] = new int[n];
        int total_wt = 0, total_tat = 0;

        System.out.println("FCFS: ");
        wt = findWaitingTime(proc, n, disp);

        tat = findTurnAroundTime(proc, n, wt);

        System.out.print("\n");
        // Display processes along with the turnaround time and waiting time
        System.out.print("Process  Turnaround Time  Waiting Time \n");
        for (int i = 0; i < n; i++) {
            total_wt = total_wt + wt[i];
            total_tat = total_tat + tat[i];
            System.out.println(proc[i].pid + "\t " + tat[i] + "\t\t  " + wt[i]);
        }
        System.out.print("\n");

        // Calculates the average turnaround time and average waiting time
        // And puts it into an array to be passed
        float avg_tat = (float) total_tat / (float) n;
        float avg_wt = (float) total_wt / (float) n;
        float avg[] = { avg_tat, avg_wt };

        return avg;
    }

    // function used to calculate the waiting time of each process
    static int[] findWaitingTime(Process proc[], int n, int disp) {
        int num = n, t = 0, executed = 0;
        int waiting_time[] = new int[n];

        LinkedList<String> process = new LinkedList<String>();
        LinkedList<Integer> exec_size = new LinkedList<Integer>();
        LinkedList<Integer> arrival = new LinkedList<Integer>();

        // Used to make a copy of the processID and its waiting time
        LinkedList<Integer> wt = new LinkedList<Integer>();

        for (int i = 0; i < n; i++) {
            process.add(proc[i].pid);
            exec_size.add(proc[i].es);
            arrival.add(proc[i].at);
        }

        while (n != 0) {
            boolean found = false;

            for (int i = 0; i < n; i++) {
                if (t > arrival.get(i) && !found) {
                    executed = i;
                    found = true;
                }
            }

            if (found) {
                // Print out the time and the processes
                System.out.println("T" + t + ": " + process.get(executed));

                // Stores the waiting time
                wt.add(t - arrival.get(executed));

                // Add to the time
                t += exec_size.get(executed) + disp;

                // Removes the completed process
                process.remove();
                exec_size.remove();
                arrival.remove();
                n--;
            }
            // If not a process with an arrival time that has passed the current time then
            // add to the time
            else if (!found) {
                t++;
                continue;
            }
        }

        // Returns the waiting time as an array
        for (int i = 0; i < num; i++) {
            waiting_time[i] = wt.get(i);
        }

        return waiting_time;
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
