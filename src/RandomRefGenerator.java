import java.util.Random;

/* The RandomRefGenerator class generates the random reference string for
each process. The referenceGenerator function is invoked every 100 ms
by each process. It generates the random reference based on the below criteria:
1. generate a random number i which is <=10
2. If the i generated is <7 then the generate the reference page j such that the
delta is one of i-1, i, i+1
3. if i > 7 then generate the reference page j such that 0 <=  j <= i-2 or j <= i+2 <= 10
This is to consider the locality fo reference.
The function referenceGenerator returns random integer j
*/

public class  RandomRefGenerator {
    private static final double LOCALITY_PROBABILITY = 0.7;
    private static int currentPage = 0;
    private static int pages = 11;

    public static int referenceGenerator(Process process) {

        Random random = new Random();

        int i = random.nextInt(11); // Generate a random number from 0 to 10

        if (i < LOCALITY_PROBABILITY * 10) {
            // Temporal locality: Generate Δi to be -1, 0, or +1
            int delta = random.nextInt(3) - 1;
            currentPage = (currentPage + delta + pages) % pages; // Wrap around from 10 to 0
        } else {
            // Larger gap: Randomly generate the new page reference j, 2 ≤ |Δi| ≤ 9
            int j = random.nextInt(8) + 2;
            if (random.nextBoolean()) {
                currentPage = (currentPage - j + pages) % pages; // Wrap around from 10 to 0
            } else {
                currentPage = (currentPage + j) % pages; // Wrap around from 0 to 10
            }
        }

        return currentPage;
    }

//    public static void main(String[] args) {
//        for (int i = 0; i < 100; i++) { // Simulate 100 references
////            int nextPage = referenceGenerator(th);
////            System.out.println("Reference: " + nextPage);
//        }
//    }
}

//        if (i < 7) {
//            // 70% probability: Reference page i, i-1, or i+1
//            deltaI = random.nextInt(3) + i - 1;
//            j = (process.pages + deltaI + 10) % 10;
//
//        } else {
//            // 30% probability: Reference a new page j
//            deltaI = random.nextInt(8) + 2;
//            j = (process.pages + deltaI) % 10;
//
//        }
//        return j;
//    }