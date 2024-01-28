import java.util.Random;
import java.util.logging.Logger;

// Author - Divya Meharwade
// This is the entry file that needs to be invoked for the simulation.
/* The Simulation class creates a logger object, initialises the Process jobQueue
Initialises the linked list for free pages, and a bunch of variables needed to begin.
It then loops through each of the simulation algorithms("FIFO", "LRU", "LFU", "MFU", "Random"),
in order  and execute the Scheduling process 5 times for each algorithm.
For every execution of the algorithm it generates 150 new processes(threads).
It calculates the average hitRatio for each  Page replacement algorithm and finally prints out the
results in order.
It also logs all the details to the logfile1.log.
*/

public class Simulation {
    // creating the logger instance
    private static final Logger logger = MyLogger.getLogger();

    public static void main(String[] args) {
        // initialising the process jobQueue
        ScheduleJobList.jobQueue  = new JobQueue();
        Random rand = new Random();
        String[] algorithms = {"FIFO", "LRU", "LFU", "MFU", "Random"};
        // Store the hitRatio stats for each algorithm
        Double[] stats = {0.0, 0.0, 0.0, 0.0, 0.0};
        int[] sizes = {5, 11, 17, 31};          // Process sizes
        // Running simulation for each Page Replacement algorithm
        for (int i = 0; i < algorithms.length; i++) {
            // Runnning simulation for each algorithm 5 times
            double hitRatio = 0.0;
            for (int j = 0; j < 5; j++) {

                // Create processes and add to jobQueue
                for(int k=0; k<15; k++) {

                    int size = sizes[rand.nextInt(4)];
                    int arrival = rand.nextInt(10)+1;
                    int duration = (rand.nextInt(9)+1)*1000;
                    int pages =  sizes[rand.nextInt(4)];

                    // Creating and adding the processes to the jobQueue
                    Process p = new Process("P"+k, size, pages, arrival, duration);
                    ScheduleJobList.jobQueue.addProcess(p);
                }
                ScheduleJobList.jobQueue.printQueue();

                // Creating the linked list of freePages
                ScheduleJobList.freePages = new FreePageList(10);
                ScheduleJobList.freePages.printList();

                logger.info("Algorithm " + algorithms[i]);
                System.out.println("Algorithm " + algorithms[i]);

                hitRatio = hitRatio + ScheduleJobList.scheduleProcesses(algorithms[i]);
                logger.info("--------------- " + algorithms[i] + " " + (j+1) + " times ----------------");
                System.out.println("--------------- " + algorithms[i] + " " + (j+1) + " times ----------------");

            }
            // Computing the hitRatio for each Page Replacement Algorithm
            stats[i] =  hitRatio/5;
            System.out.println("Algorithm stasts" + algorithms[i] + " " + hitRatio/5);
            logger.info("Algorithm stasts" + algorithms[i] + " " + hitRatio/5);
        }

        logger.info("SIMULATION RESULTS:");
        System.out.println("SIMULATION RESULTS:");
        logger.info("The average Hit Ratios for each algorithm are: ");
        System.out.println("The average Hit Ratios for each algorithm are: ");
        for (int i = 0; i < algorithms.length; i++) {
            logger.info("Algorithm " + algorithms[i] + "HitRatio " + stats[i] );
            System.out.println("Algorithm " + algorithms[i] + "HitRatio " + stats[i] );
        }

    }
}

