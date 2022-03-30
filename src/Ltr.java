/*Name: Jacob Metcalfe, Student Number: c3305509
 * COMP2240A1
 * Lottery algorithm class, used in order to run the simulation and calculate 
 * the average waiting time and turnaround time
 */
package src;

import java.util.*;

public class Ltr {
    public float[] findavgTime(Process proc[], int n, int disp, Integer random[]) {

        int wt[] = new int[n], tat[] = new int[n];
        int total_wt = 0, total_tat = 0;

        System.out.print("\n");
        System.out.println("LTR: ");
        wt = findWaitingTime(proc, n, disp, random);

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
    static int[] findWaitingTime(Process proc[], int n, int disp, Integer random[]) {
        int waiting_time[] = new int[n];
        LinkedList<String> process = new LinkedList<String>();
        LinkedList<Integer> exec_size = new LinkedList<Integer>();
        LinkedList<Integer> arrival = new LinkedList<Integer>();
        LinkedList<Integer> tickets = new LinkedList<Integer>();
        LinkedList<Integer> queue = new LinkedList<Integer>();
        LinkedList<Integer> wt = new LinkedList<Integer>();
        LinkedList<String> pc = new LinkedList<String>();
        // This copy of the exec size will be used to calculate the waiting time
        LinkedList<Integer> exec_copy = new LinkedList<Integer>();
        int winner = 0, winnerIndex = 0;
        int num = n, quantum = 4;
        int t = 0;
        for (int i = 0; i < n; i++) {
            process.add(proc[i].pid);
            exec_size.add(proc[i].es);
            exec_copy.add(proc[i].es);
            arrival.add(proc[i].at);
            tickets.add(proc[i].t);
        }

        String tempPID;
        int tempES, tempA, tempT, tempQ, tempESC;
        // Sorts the processes in order depending on its Arrival Time
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (arrival.get(i) > arrival.get(j)) {
                    tempPID = process.get(i);
                    tempES = exec_size.get(i);
                    tempESC = exec_copy.get(i);
                    tempA = arrival.get(i);
                    tempT = tickets.get(i);

                    process.set(i, process.get(j));
                    exec_size.set(i, exec_size.get(j));
                    exec_copy.set(i, exec_copy.get(j));
                    arrival.set(i, arrival.get(j));
                    tickets.set(i, tickets.get(j));

                    process.set(j, tempPID);
                    exec_size.set(j, tempES);
                    exec_copy.set(j, tempESC);
                    arrival.set(j, tempA);
                    tickets.set(j, tempT);
                }
            }
        }
        while (n != 0) {
            winner = random[winnerIndex];
            boolean found = false;
            int counter = 0;
            String selectedP = "";
            int selected = 0;
            LinkedList<String> pid = new LinkedList<String>();
            LinkedList<Integer> es = new LinkedList<Integer>();
            LinkedList<Integer> at = new LinkedList<Integer>();
            LinkedList<Integer> tic = new LinkedList<Integer>();

            boolean idleCheck = true;

            // Gets only the processes with an arrival time that has passed the current time
            for (int i = 0; i < n; i++) {
                if (t > arrival.get(i)) {
                    pid.add(process.get(i));
                    es.add(exec_size.get(i));
                    at.add(arrival.get(i));
                    tic.add(tickets.get(i));
                    idleCheck = false;
                }
            }

            if (idleCheck) {
                t++;
                continue;
            }

            int size = pid.size();

            while (!found) {
                for (int i = 0; i < size; i++) {
                    counter += tic.get(i);

                    if (counter > winner && !found) {
                        selectedP = pid.get(i);
                        found = true;
                        winnerIndex++;
                    }
                }
            }

            for (int i = 0; i < n; i++) {
                if (selectedP == process.get(i)) {
                    selected = i;
                }
            }

            System.out.println("T" + t + ": " + process.get(selected));
            if (exec_size.get(selected) <= quantum) {
                // Final finish time of the process
                int finish_time = t + exec_size.get(selected);
                // Calculate waiting time
                int calculateWT = finish_time - exec_copy.get(selected) - arrival.get(selected);
                // Passes the waiting time and the specific process into a linked list
                pc.add(process.get(selected));
                wt.add(calculateWT);

                t += exec_size.get(selected) + disp;

                process.remove(selected);
                exec_size.remove(selected);
                exec_copy.remove(selected);
                arrival.remove(selected);
                tickets.remove(selected);

                // System.out.print(process + "\n");
                n--;
            } else {
                t += quantum + disp;

                int calculateES = exec_size.get(selected) - quantum;
                exec_size.set(selected, calculateES);

                tempPID = process.get(selected);
                tempES = exec_size.get(selected);
                tempA = arrival.get(selected);
                tempT = tickets.get(selected);
                tempESC = exec_copy.get(selected);

                process.remove(selected);
                exec_size.remove(selected);
                arrival.remove(selected);
                tickets.remove(selected);
                exec_copy.remove(selected);

                process.add(tempPID);
                exec_size.add(tempES);
                arrival.add(tempA);
                tickets.add(tempT);
                exec_copy.add(tempESC);

                // System.out.print(process + "\n");
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
