import java.util.*;
import java.util.logging.Logger;
/*
Author Divya Meharwade
This file contains the Process class & JobQueue Class.

The Process class extends thread class and implements the run function.
The process class has multiple fields like name, size, hitRatio, algo and page head.
It maintains a list of pages allocated to it by the schedule job process.
The constructor creates a new Process thread and when started executes the run method.
It invokes the referenceGenerator every 100 ms and references it till its service duration.
Based on the algo passed it creates the class for the algo and executes the page replacement
algorithm.
It finally gets the stats from the pagereplacement algorithm and stores hitRatio in its field.

The JobQueue class creates a node for each process added to it by the scheduleJob process.
It maintains a linkedlist and when time for execution each node is picked from the head of the
linkedlist. it arranges the processes based on their arrival times.

 */
class Process extends Thread{
    String name;
    int size;
    int pages;
    int arrival;
    int duration;

    double hitRatio;

    String algo;

    Page head;

    ReplacementStrategy strategy = new FIFOCache(6); // Default
    private static final Logger logger = MyLogger.getLogger();
    public Process(String name, int size, int pages, int arrival, int duration) {
        this.name = name;
        this.size = size;
        this.pages = pages;
        this.arrival = arrival;
        this.duration = duration;
        this.hitRatio = 0.0;
    }

    @Override
    public void run() {

        int TIME_QUANTUM = 100; // 100 ms
        int currentTime = 0;

        // Creates an instance of RandomRefGenerator
        RandomRefGenerator generator = new RandomRefGenerator();
        logger.info(" Strategy " + algo);
        // Creates an instance of the specific page replacement algo to execute
        switch (algo) {
            case "FIFO": strategy = new FIFOCache(3); break;
            case "LRU": strategy = new LRUCache(3); break;
            case "LFU": strategy = new LFUCache(3); break;
            case "MFU": strategy = new MFUCache(3); break;
            case "Random": strategy = new RandomPick(3); break;
        }
        // Creates a set of pages based on the reference page generated
        Page[] pages = new Page[11];
        for (int i = 0; i < 11; i++) {
            pages[i] = new Page(i);
        }

        try {
            logger.info(" Working " + this.name + this.pages + " " +
                    this.duration);

            // Simulate process execution
            int remainingDuration = this.duration;
            while (remainingDuration > 0) {
                int ref = generator.referenceGenerator(this);
                logger.info("Time " + currentTime + ": Process " + this.name + " referenced page " + ref +" "+ pages[ref] +" remaining time " + remainingDuration);

                strategy.referencePage(pages[ref]); // exceutes the page replacement algorithm

                try {
                    Thread.sleep(TIME_QUANTUM); // waits for 100ms
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                remainingDuration -= TIME_QUANTUM;
                currentTime += TIME_QUANTUM;
            }


            // Generates record for completing job
            logger.info("Time " + currentTime + ": Record - Complete: Process " + this.name +
                    ", Size: " + this.pages + " pages, Duration: " + this.duration +
                    " seconds. " + " Strategy " + algo + " Hit/Miss Ratio " + strategy.stats());
            this.hitRatio = strategy.stats(); // retrieves the statistics

            logger.info(" Work ended " + this.name + this.pages + " " + this.duration);
        } catch (Exception e) {}

    }

}

class JobQueue {
    static class Node {
        Process process;
        Node next;

        Node(Process p) {
            process = p;
        }
    }

    Node head;

    private static final Logger logger = MyLogger.getLogger();

    public void addProcess(Process p) {
        Node newNode = new Node(p); // generates a new node

        if(head == null || head.process.arrival >= p.arrival) {
            newNode.next = head;
            head = newNode;
            return;
        }

        // organises the nodes based on arrival time
        Node curr = head;
        while(curr.next != null && curr.next.process.arrival < p.arrival) {
            curr = curr.next;
        }

        newNode.next = curr.next;
        curr.next = newNode;
    }

    public void printQueue() {
        Node curr = head;
        while(curr != null) {
            logger.info(curr.process.name + " ");
            curr = curr.next;
        }
    }
}

public class ProcessJobQueue {

    private static final Logger logger = MyLogger.getLogger();

    public static void main(String[] args) {

        JobQueue queue = new JobQueue();
        Random rand = new Random();

        // Process sizes
        int[] sizes = {5, 11, 17, 31};

        for(int i=0; i<150; i++) {

            int size = sizes[rand.nextInt(4)];
            int arrival = rand.nextInt(10)+1;
            int duration = rand.nextInt(5)+1;
            int pages =  rand.nextInt(5)+1;

            Process p = new Process("P"+i, size, pages, arrival, duration);
            queue.addProcess(p);
        }

        queue.printQueue();
    }
}

