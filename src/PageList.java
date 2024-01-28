import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

/*
This file contains the Page and FreePageList classes.
The Page is a single page node with size and a next pointer attributes.
The FreePageList creates the a linkedlist fo 100 pages for each round of execution.
It consists of the getNextFreePage and  addToFreePageList functions.
getNextFreePage = will return the next free page form the freePagesList to the schedule process for allocation
addToFreePageList = will add back the free pages back to the freePages linked list.
PrintList - that prints the list
the getNextFreePage and addToFreePageList use a lock for thread synchronisation to ensure the free pages count stays
idempotent.
 */
class Page {
    int size = 1024; // 1 MB
    Page next;

    public Page(int size) {
        this.size = size;
    }
}

class FreePageList {
    Page head;
    static int count;
    private static final Logger logger = MyLogger.getLogger();
    ReentrantLock lock = new ReentrantLock();
    public FreePageList(int totalPages) {
        count = totalPages;

        // Initialize linked list
        head = new Page(1024);
        Page current = head;
        for(int i=1; i<totalPages; i++) {
            current.next = new Page(1024);
            current = current.next;
        }
    }

    // will return the next free page form the freePagesList
    // to the schedule process for allocation
    public Page getNextFreePage() {

        if (head == null)
            return null;

        Page temp = head;

        lock.lock();

        try {
            head = head.next;
            count--;
        } catch (Exception ex) {

        } finally {
            lock.unlock();
        }
            return temp;

    }

    // add back the free pages back to the freePages linked list.
    public void addToFreePageList(Page p) {
        lock.lock();
        try {
            p.next = head;
            head = p;
            count++;
        }catch (Exception ex) {

        }finally {
            lock.unlock();
        }

    }
    public void printList() {
        Page temp = head;
        while(temp != null) {
            logger.info(temp.size + " --> ");
            temp = temp.next;
        }
        logger.info("null");
    }

    public void cleanUp() {
        Page curr = head;
        while (curr != null) {
            Page next = curr.next;
            // free current page
            curr = next;
        }
        head = null;
    }
}

public class PageList {
    private static final Logger logger = MyLogger.getLogger();

    public static void main(String[] args) {

        FreePageList list = new FreePageList(100);

        list.printList();

        Page p1 = list.getNextFreePage();
        Page p2 = list.getNextFreePage();

        logger.info("Assigned Pages:");

        list.cleanUp();
        logger.info("cleaned up Pages:");
        list.printList();

    }
}