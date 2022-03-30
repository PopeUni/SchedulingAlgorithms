/*Name: Jacob Metcalfe, Student Number: c3305509
 * COMP2240A1
 * Process class, will hold all important values for each process such as the ProcessID,
 *Exec Size, Arrival Time and Tickets
*/

package src;

public class Process {

    String pid; // Process ID
    int es; // Exec Size
    int at; // Arrival Time
    int t; // Tickets

    public Process(String pid, int es, int at, int t) {
        this.pid = pid;
        this.es = es;
        this.at = at;
        this.t = t;
    }
}
