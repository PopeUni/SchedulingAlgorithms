/*Name: Jacob Metcalfe, Student Number: c3305509
 * COMP2240A1
 * Multi-Level-Feedback algorithm class, used in order to run the simulation and calculate 
 * the average waiting time and turnaround time
 */
package src;

import java.util.*;

public class Fbv {

    public float[] findavgTime(Process proc[], int n, int disp) {

        int wt[] = new int[n], tat[] = new int[n];
        int total_wt = 0, total_tat = 0;

        System.out.print("\n");
        System.out.println("FBV: ");

        wt = findWaitingTime(proc, n, disp);
        tat = findTurnAroundTime(proc, n, wt);

        System.out.print("\n");
        System.out.println("Processes  Turn around time  Waiting time ");

        // Calculate total waiting time and
        // total turnaround time
        for (int i = 0; i < n; i++) {
            total_wt = total_wt + wt[i];
            total_tat = total_tat + tat[i];
            System.out.println(" " + proc[i].pid + "\t\t" + tat[i] + "\t\t" + wt[i]);
        }

        float avg_tat = (float) total_tat / (float) n;
        float avg_wt = (float) total_wt / (float) n;
        float avg[] = { avg_tat, avg_wt };

        return avg;
    }

    // function used to calculate the waiting time of each process
    static int[] findWaitingTime(Process proc[], int n, int disp) {

        int waiting_time[] = new int[n];

        LinkedList<Integer> wt = new LinkedList<Integer>();
        LinkedList<String> pc = new LinkedList<String>();
        LinkedList<Integer> exec_size = new LinkedList<Integer>();
        LinkedList<Integer> arrival = new LinkedList<Integer>();
        LinkedList<String> process = new LinkedList<String>();
        LinkedList<Integer> exec_copy = new LinkedList<Integer>();
        // Will keeps track of which queue a process is in
        LinkedList<Integer> queuenum = new LinkedList<Integer>();

        // Will count the processes' timer after it has reached the last queue
        LinkedList<Integer> timer = new LinkedList<Integer>();

        String tempPID;
        int tempES, tempA, tempQ, tempT, tempS, tempESC;
        int quantum[] = { 1, 2, 4, 4 };
        int selected = 0, t = disp;

        // Gets the maximum queue allowed based on the length of the quantum
        int lastqueue = quantum.length - 1;

        // Stores a copy of the number of processes
        int num = n;

        // Puts the data into a linked list
        for (int i = 0; i < n; i++) {
            process.add(proc[i].pid);
            arrival.add(proc[i].at);
            exec_size.add(proc[i].es);
            exec_copy.add(proc[i].es);
            // Sets all the default of the processes' queues and timer to 0
            timer.add(0);
            queuenum.add(0);
        }

        while (n != 0) {
            boolean found = false;

            // Checks if the system is idling because no processes has arrived yet
            boolean idleCheck = true;
            for (int i = 0; i < n; i++) {
                if (t > arrival.get(i)) {
                    idleCheck = false;
                }
            }
            if (idleCheck) {
                t++;
                continue;
            }

            // Checks if the process has been running/waiting in the last queue for more
            // than 32 ms
            // Reset its timer and puts its queue back to the highest priority
            for (int i = 0; i < n; i++) {
                if (timer.get(i) > 32) {
                    timer.set(i, 0);
                    queuenum.set(i, 0);
                }
            }

            // Used to get the current smalleslt queue
            LinkedList<Integer> smallest = new LinkedList<Integer>();
            for (int i = 0; i < n; i++) {
                smallest.add(queuenum.get(i));
            }
            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {
                    if (smallest.get(i) > smallest.get(j)) {
                        tempS = smallest.get(i);
                        smallest.set(i, smallest.get(j));
                        smallest.set(j, tempS);
                    }
                }
            }

            for (int i = 0; i < n; i++) {
                if (t > arrival.get(i) && !found) {
                    // Checks if the processes' current queue is the same as the smallest queue
                    if (smallest.get(0) == queuenum.get(i)) {
                        selected = i;
                        found = true;
                    }
                }
            }

            // If there are no processes in the current queue with an arrival time greater
            // than the current time then
            // Go to a processes which arrival time has already passed the current time
            // And go to it's next queue
            if (!found) {
                // Sorts the arrays according to the queue the process is on
                for (int i = 0; i < n; i++) {
                    for (int j = i + 1; j < n; j++) {
                        if (queuenum.get(i) > queuenum.get(j)) {

                            tempQ = queuenum.get(i);
                            tempPID = process.get(i);
                            tempES = exec_size.get(i);
                            tempA = arrival.get(i);
                            tempESC = exec_copy.get(i);
                            tempT = timer.get(i);

                            queuenum.set(i, queuenum.get(j));
                            process.set(i, process.get(j));
                            exec_size.set(i, exec_size.get(j));
                            arrival.set(i, arrival.get(j));
                            exec_copy.set(i, exec_copy.get(j));
                            timer.set(i, timer.get(j));

                            queuenum.set(j, tempQ);
                            process.set(j, tempPID);
                            exec_size.set(j, tempES);
                            arrival.set(j, tempA);
                            exec_copy.set(j, tempESC);
                            timer.set(j, tempT);

                        }
                    }
                }
                // The sorted data is required to ensure that we get the process based on
                // The lowest queue and highest priority
                for (int i = 0; i < n; i++) {
                    // Checks if the processes' arrival time has passed the current time
                    if (t > arrival.get(i) && !found) {
                        selected = i;
                        found = true;
                    }
                }

            }

            if (found) {
                // Displays the time and the executed process
                System.out.println("T" + t + ": " + process.get(selected));

                // Checks if the execsize of the process is less than or equals to the quantum
                // of its queue
                if (exec_size.get(selected) <= quantum[queuenum.get(selected)]) {
                    // Checks if a process is in the last queue and add to its timer
                    for (int i = 0; i < n; i++) {
                        if (queuenum.get(i) == lastqueue) {
                            timer.set(i, (timer.get(i) + exec_size.get(selected) + disp));
                        }
                    }

                    int finish_time = t + exec_size.get(selected);
                    // Calculate waiting time
                    int calculateWT = finish_time - exec_copy.get(selected) - arrival.get(selected);

                    // Passes the waiting time and the specific process into a linked list
                    pc.add(process.get(selected));
                    wt.add(calculateWT);
                    // Adds the dispatcher and the execsize to the time
                    t += exec_size.get(selected) + disp;
                    // Remove process because it is completed
                    process.remove(selected);
                    exec_size.remove(selected);
                    exec_copy.remove(selected);
                    arrival.remove(selected);
                    queuenum.remove(selected);
                    timer.remove(selected);
                    n--;
                } else {
                    // Checks if a process is in the last queue and add to its timer
                    for (int i = 0; i < n; i++) {
                        if (queuenum.get(i) == lastqueue) {
                            timer.set(i, (timer.get(i) + (quantum[queuenum.get(selected)] + disp)));
                        }
                    }

                    // Adds the dispatcher and the quantum of its queue
                    t += quantum[queuenum.get(selected)] + disp;

                    // Calculates the remaining execsize of the process after
                    // Subtracting the quantum of the processes' current queue
                    int remaining = exec_size.get(selected) - quantum[queuenum.get(selected)];

                    // Updates the execsize of the process to its remaining execsize
                    exec_size.set(selected, remaining);

                    // Ensures that the queue does not go over the maximum size of the quanta that
                    // has been set
                    if (queuenum.get(selected) < lastqueue) {
                        // Puts the specific process in the next queue
                        queuenum.set(selected, (queuenum.get(selected)) + 1);
                    }

                    tempPID = process.get(selected);
                    tempES = exec_size.get(selected);
                    tempESC = exec_copy.get(selected);
                    tempA = arrival.get(selected);
                    tempQ = queuenum.get(selected);
                    tempT = timer.get(selected);

                    process.remove(selected);
                    exec_size.remove(selected);
                    exec_copy.remove(selected);
                    arrival.remove(selected);
                    queuenum.remove(selected);
                    timer.remove(selected);

                    process.add(tempPID);
                    exec_size.add(tempES);
                    exec_copy.add(tempESC);
                    arrival.add(tempA);
                    queuenum.add(tempQ);
                    timer.add(tempT);
                }
            }
        }

        String tempPC;
        int tempWT;
        // The waiting time array is currently using the sorted processes
        // So it must be sorted back
        // Sorts the waiting time back
        for (int i = 0; i < num; i++) {
            for (int j = i + 1; j < num; j++) {
                // Checks if the previous process number(ID) is greater than the next process
                // number
                if (Integer.valueOf((pc.get(i)).replace("p", "")) > Integer.valueOf(pc.get(j).replace("p", ""))) {
                    tempPC = pc.get(i);
                    tempWT = wt.get(i);

                    pc.set(i, pc.get(j));
                    wt.set(i, wt.get(j));

                    pc.set(j, tempPC);
                    wt.set(j, tempWT);
                }
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
