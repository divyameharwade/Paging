
import java.util.*;
import java.util.logging.Logger;
/*
Author: Divya Meharwade
This file consists of all the PageReplacment Algorithms that the simulation requires.
namely FIFO, LRU, LFU, MFU and RandomPick.
Each of the pageReplacement Algorithms implement the ReplacementStrategy interace.
Each PageReplacement class captures the hit and miss ratio for each process and returns
the hitRatio stats.
Each class takes the capacity attribute for initilisation and use logger to log the details.
The classes are invoked using the strategy design pattern.
 */
class FIFOCache implements ReplacementStrategy {

    private Queue<Page> pageQueue;
    private int capacity;

    double hit = 0.0;
    double miss = 0.0;
    private static final Logger logger = MyLogger.getLogger();
    public FIFOCache(int capacity) {
        this.pageQueue = new LinkedList<>();
        this.capacity = capacity;
    }

    @Override
    public void referencePage(Page p) {

        if (pageQueue.contains(p))
        {
            hit++;
            logger.info("Hit for Page " + p);

        } else {
            miss++;
            logger.info("Miss for Page " + p);

            if (pageQueue.size() == capacity) {
                evictPage();
            }
            pageQueue.offer(p);
        }
    }

    @Override
    public Page evictPage() {
        return pageQueue.poll();
    }

    @Override
    public double stats() {
        logger.info("STATS FOR FIFO");
        logger.info("Hit " + hit + " Miss " + miss);
        return (hit/(miss+hit));
    }
}


class LRUCache implements ReplacementStrategy {

    private LinkedHashMap<Page, Long> pageHistory;
    private int capacity;


    double hit = 0.0;
    double miss = 0.0;
    private static final Logger logger = MyLogger.getLogger();

    public LRUCache(int capacity) {
        this.pageHistory = new LinkedHashMap<>(0);
        this.capacity = capacity;
    }

    @Override
    public void referencePage(Page p) {
        logger.info("Page history " + pageHistory.keySet());
        if (pageHistory.containsKey(p)) {

            hit++;
            logger.info("Hit for Page " + p);
            pageHistory.put(p, System.currentTimeMillis());

        }
        else {

            miss++;
            logger.info("Miss for Page " + p);

            if (pageHistory.size() > capacity) {
                evictPage();
            }

            pageHistory.put(p, System.currentTimeMillis());
            logger.info("PageHistory size " + pageHistory.size());


        }

    }

    @Override
    public Page evictPage() {
        Map.Entry<Page, Long> eldestEntry = pageHistory.entrySet().iterator().next();
        Page lruPage = eldestEntry.getKey();
        pageHistory.remove(lruPage);
        logger.info("Evicted page: " + lruPage);
        return lruPage;
    }

    @Override
    public double stats() {
        logger.info("STATS FOR LRU");
        logger.info("Hit " + hit + " Miss " + miss);
        return (hit/(miss+hit));
    }
}

class LFUCache implements ReplacementStrategy {

    private Map<Page, Integer> pageFrequency;
    private int capacity;

    double hit = 0.0;
    double miss = 0.0;
    private static final Logger logger = MyLogger.getLogger();

    public LFUCache(int capacity) {
        this.pageFrequency = new HashMap<>();
        this.capacity = capacity;
    }

    @Override
    public void referencePage(Page p) {


        if (pageFrequency.containsKey(p)) {
            hit++;
            logger.info("Hit for Page " + p);
        } else {
            miss++;
            logger.info("Miss for Page " + p);

            if (pageFrequency.size() > capacity) {
                evictPage();
            }
            pageFrequency.put(p, pageFrequency.getOrDefault(p, 0) + 1);

        }
    }


    @Override
    public Page evictPage() {
        Page lfuPage = Collections.min(pageFrequency.entrySet(), Map.Entry.comparingByValue()).getKey();
        pageFrequency.remove(lfuPage);
        return lfuPage;
    }

    @Override
    public double stats() {
        logger.info("STATS FOR LFU");
        logger.info("Hit " + hit + " Miss " + miss);
        return (hit/(miss+hit));
    }

}


class MFUCache implements ReplacementStrategy {

    private Map<Page, Integer> pageFrequency;
    private int capacity;

    double hit = 0.0;
    double miss = 0.0;
    private static final Logger logger = MyLogger.getLogger();

    public MFUCache(int capacity) {
        this.pageFrequency = new HashMap<>();
        this.capacity = capacity;
    }

    @Override
    public void referencePage(Page p) {
        if (pageFrequency.containsKey(p)) {
            hit++;
            logger.info("Hit for Page " + p);
        } else {
            miss++;
            logger.info("Miss for Page " + p);

            if (pageFrequency.size() > capacity) {
                evictPage();
            }
            pageFrequency.put(p, pageFrequency.getOrDefault(p, 0) + 1);

        }
    }

    @Override
    public Page evictPage() {
        Page mfuPage = Collections.max(pageFrequency.entrySet(), Map.Entry.comparingByValue()).getKey();
        pageFrequency.remove(mfuPage);
        return mfuPage;
    }

    @Override
    public double stats() {
        logger.info("STATS FOR MFU");
        logger.info("Hit " + hit + " Miss " + miss);
        return (hit/(miss+hit));
    }
}


class RandomPick implements ReplacementStrategy {

    private List<Page> pageList;
    private int capacity;
    private static final Logger logger = MyLogger.getLogger();

    public RandomPick(int capacity) {
        this.pageList = new ArrayList<>();
        this.capacity = capacity;
    }

    double hit = 0.0;
    double miss = 0.0;
    @Override
    public void referencePage(Page p) {
        if (pageList.contains(p)) {
            // Page is already in the cache (hit)
            hit++;
            logger.info("Hit for Page " + p);


        } else {
            // Page is not in the cache (miss)
            miss++;
            logger.info("Miss for Page " + p);


            if (pageList.size() >= capacity) {
                evictPage();
            }
            pageList.add(p);
        }
    }

    @Override
    public Page evictPage() {
        if (!pageList.isEmpty()) {
            // Randomly select a page for eviction
            Random random = new Random();
            int randomIndex = random.nextInt(pageList.size());
            Page evictedPage = pageList.remove(randomIndex);
            return evictedPage;
        } else {
            return null; // The cache is already empty
        }
    }

    @Override
    public double stats() {
        logger.info("STATS FOR RandomPick");
        logger.info("Hit " + hit + " Miss " + miss);
        return (hit/(miss+hit));
    }

}


public class PageReplacement {
    private static final Logger logger = MyLogger.getLogger();

    public static void main(String[] args) {
        // Example usage for LRU
        ReplacementStrategy lruCache = new LRUCache(3);
        invokePageReplacementAlgorithm(lruCache);

        // Example usage for MFU
        ReplacementStrategy mfuCache = new MFUCache(3);
        invokePageReplacementAlgorithm(mfuCache);

        // Example usage for LFU
        ReplacementStrategy lfuCache = new LFUCache(4);
        invokePageReplacementAlgorithm(lfuCache);

        // Example usage for FIFO
        ReplacementStrategy fifoCache = new FIFOCache(3);
        invokePageReplacementAlgorithm(fifoCache);
    }

    private static void invokePageReplacementAlgorithm(ReplacementStrategy cache) {
        Page p1 = new Page(1);
        Page p2 = new Page(1);
        Page p3 = new Page(1);
        Page p4 = new Page(1);
        Page p5 = new Page(1);
        Page p6 = new Page(1);
        Page p7 = new Page(1);
        Page p8 = new Page(1);
        Page p9 = new Page(1);

        cache.referencePage(p1); //m =1
        cache.referencePage(p3); //m =2
        cache.referencePage(p1); //h =1 [2,1]
        cache.referencePage(p2); //m = 3 [2,1,4]
        cache.referencePage(p2); //h = 2 [1,4,2]
        cache.referencePage(p7); //m = 4 [4,2,3]
        cache.referencePage(p1); //m = 5 [2,3,7]
        cache.referencePage(p3); //m = 6 [3,7,1]
        cache.referencePage(p2); //m = 7 [7,1,2]
        cache.referencePage(p5); //h = 3 [7,1,2]
        cache.referencePage(p3); //h = 4 [1,2,7]
        cache.referencePage(p2); //m = 8 [2,7,8]
        cache.referencePage(p4); //m = 9 [7,8,3]
        cache.referencePage(p9); //m = 10 [8,3,9]

        cache.stats();

        // Print the cache content
        logger.info("Cache content after referencing pages: " + cache);
        // Print the cache content after eviction
        logger.info("Cache content after eviction: " + cache);
    }
}
