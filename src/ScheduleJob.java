import java.util.*;
import java.util.logging.Logger;

/* Author Divya Meharwade
The scheduleProcesses function of ScheduleJobList class schedules the processes from the jobQueue for execution.
It checks the freepages for availability, if there are atleast 4 free pages it will
allocate the processes for execution and add them to an ArrayList scheduledProcessList
to track them for completion. Once the process has completed it will execute the join method,
extract the hitRate details for the process and then freeMemory for the respective process.
If the freepages are not available the scheduleProcesses fucntion will wait till one of the process finishes
its execution and picks up the next process from the job queue for processing.
scheduleProcesses runs till there are processes in the queue and till a time frame of 1 min has elapsed as
per the details mentioned in the assignment.
Even if one process has finished execution it will move on to schedule the next process.
Finally when all the processes are done it will free the memory for all.
 */
class ScheduleJobList {

    static JobQueue jobQueue;
    static FreePageList freePages;
    static int MIN_FREE_PAGES = 4;

    private static final Logger logger = MyLogger.getLogger();
    public static double scheduleProcesses(String algo) {
        int count;
        long time;
        double hitRate = 0.0;
        int processCount = 0;
        ArrayList<Process> scheduledProcessList = new ArrayList<>();
        time = new Date().getTime();

        // schedules till processes exist and till the elapsed time < 1 min
        while((jobQueue.head != null) && ((new Date().getTime() - time) < 60000)){
            Process nextJob = jobQueue.head.process;

            // checks for 4 free frames before allocating the process for execution
            if(freePages.count >= MIN_FREE_PAGES) {

                // allocates memory and prints a entry record for the process
                count = allocateMemory(nextJob);
                logger.info("Enter " + nextJob.name + " allocated " + nextJob.pages + " pages "
                        + " Service duration " + nextJob.duration + ", freePages= " + count);

                // Set the pagereplacement algo to run
                nextJob.algo = algo;
                nextJob.start();                 // Start process

                scheduledProcessList.add(nextJob);  // tracks the process for completion

                // Moves on to schedule the next process from the jobQueue
                if (jobQueue.head != null) {
                    jobQueue.head = jobQueue.head.next;
                }

            } else {
                // Wait for free pages
                logger.info(" LENGTH OF PROCESSLIST " + scheduledProcessList.size());
                logger.info(" Process " + nextJob.name + " Waiting for " + nextJob.pages + " pages ");

                // checks for any processes that have completed execution
                while (scheduledProcessList.size() > 0) {
                    try {
                        Process p = scheduledProcessList.removeFirst();
                        logger.info(" process p " +p.getName());
                        p.join();  // Process completes

                        // Collect results from threads
                        hitRate += p.hitRatio;
                        processCount++;

                        // Deallocates the memory
                        count = freeMemory(p);
                        logger.info(" Exit "  + p.name + " " + p.pages + " " +
                                p.duration  + " freepages " + count + " hitRatio " + p.hitRatio);
                        break;  // even if one process is free it moves on to schedule the next process
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        // free memory for all remaining processes
        for (Process p: scheduledProcessList) {

            try {
                logger.info(" process p " +p.getName());
                p.join();  // Process completes

                hitRate += p.hitRatio;
                processCount++;

                count = freeMemory(p);
                logger.info(" Exit " + p.name + " " + p.pages + " " +
                        p.duration  + " " + count + "sec" +" HitRate " +p.hitRatio);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        logger.info("FINAL FREE PAGES " + freePages.count);
        logger.info("Total hitRatio " + hitRate + " processesCount " +processCount);
        return hitRate/processCount;   // return the hitRatio for the run

    }

    public static int allocateMemory(Process p) {
        // Allocate pages from free list
        p.head = freePages.getNextFreePage();
        Page last = p.head;

        // allocate a maximum of 4 pages
        int pages = 4;
        if (p.pages < pages) {
            pages = p.pages;
        }
        for(int i=1; i<pages; i++) {
            last.next = freePages.getNextFreePage();
            last = last.next;
        }
        last.next = null;
        return freePages.count;
    }

    public static int freeMemory(Process p) {
        // Free process pages
        Page current = p.head;
        while (current != null) {

                Page next = current.next;
                freePages.addToFreePageList(current);
                current = next;
            }
        return freePages.count;
    }
}

//public class ScheduleJob {
//    private static final Logger logger = MyLogger.getLogger();
//
//    public static void main(String[] args) {
//        ScheduleJobList.jobQueue  = new JobQueue();
//        Random rand = new Random();
//        String[] algorithms = {"FIFO", "LRU", "LFU", "MFU", "Random"};
//        Double[] stats = {0.0, 0.0, 0.0, 0.0, 0.0};
//        int[] sizes = {5, 11, 17, 31};          // Process sizes
//        // Running simulation for each Page Replacement algorithm
//        for (int i = 0; i < algorithms.length; i++) {
//            // Runnning simulation for each algorithm 5 times
//            double hitRatio = 0.0;
//            for (int j = 0; j < 1; j++) {
//
//                // Create processes and add to jobQueue
//                for(int k=0; k<5; k++) {
//
//                    int size = sizes[rand.nextInt(4)];
//                    int arrival = rand.nextInt(10)+1;
//                    int duration = (rand.nextInt(9)+1)*1000;
//                    int pages =  sizes[rand.nextInt(4)];
//
//                    Process p = new Process("P"+k, size, pages, arrival, duration);
//                    ScheduleJobList.jobQueue.addProcess(p);
//                }
//                ScheduleJobList.jobQueue.printQueue();
//
//                ScheduleJobList.freePages = new FreePageList(4);
//                ScheduleJobList.freePages.printList();
//
//                logger.info("Algorithm " + algorithms[i]);
//                System.out.println("Algorithm " + algorithms[i]);
//
//                hitRatio = hitRatio + ScheduleJobList.scheduleProcesses(algorithms[i]);
//                logger.info("--------------- " + algorithms[i] + " " + (j+1) + " times ----------------");
//                System.out.println("--------------- " + algorithms[i] + " " + (j+1) + " times ----------------");
//
//            }
//            stats[i] =  hitRatio/5;
//            System.out.println("Algorithm stasts" + algorithms[i] + " " + hitRatio/5);
//            logger.info("Algorithm stasts" + algorithms[i] + " " + hitRatio/5);
//        }
//
//        logger.info("SIMULATION RESULTS:");
//        System.out.println("SIMULATION RESULTS:");
//        logger.info("The average Hit Ratios for each algorithm are: ");
//        System.out.println("The average Hit Ratios for each algorithm are: ");
//        for (int i = 0; i < algorithms.length; i++) {
//            logger.info("Algorithm " + algorithms[i] + "HitRatio " + stats[i] );
//            System.out.println("Algorithm " + algorithms[i] + "HitRatio " + stats[i] );
//        }
//
//    }
//}


