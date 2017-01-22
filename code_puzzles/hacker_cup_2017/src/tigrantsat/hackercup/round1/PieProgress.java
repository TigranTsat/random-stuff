package tigrantsat.hackercup.round1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tigrantsat.hackercup.Config;

public class PieProgress {
    
    public static class Line {
        private final long[] purchaseOptions;
        private int head = 0;
        private long totalPurchasedCost = 0;
        
        public Line(long[] purchaseOptions) {
            assertTrue(purchaseOptions.length >= 1);
            this.purchaseOptions  = purchaseOptions;
            Arrays.sort(this.purchaseOptions);
        }
        
        public long getCostOfNextPurchase() {
            long nextItem = purchaseOptions[head];
            return (nextItem + sqrt(head + 1) - sqrt(head));
        }
        
        public void purchaseNext() {
            totalPurchasedCost = (getTotalPurchasedCost() + getCostOfNextPurchase());
            head++;
        }
        
        public boolean isMore() {
            return head < purchaseOptions.length;
        }

        private long sqrt(int a) {
            return a*a;
        }

        public long getTotalPurchasedCost() {
            return totalPurchasedCost;
        }
    }
    
    public static class PieEatingSpree {
        private final int N;
        private final int M;
        private List<Line> lines = new ArrayList<Line>();

        public PieEatingSpree(int N, int M) {
            this.N = N;
            this.M = M;
            assertTrue(N > 0);
            assertTrue(M > 0);
        }

        public void addLine(long[] line) {
            Line l = new Line(line);
            lines.add(l);
            assertEqual(M, line.length);
        }

        public void addLine(String[] line) {
            long l[] = new long[line.length];
            for (int i = 0; i < line.length; i++) {
                l[i] = Integer.parseInt(line[i]);
            }
            addLine(l);
        }

        public void validate() {
            assertEqual(N, lines.size());
        }

        public long getTotalCost() {
            validate();
            // Day 1 purchase
            lines.get(0).purchaseNext();
            
            for (int i = 1; i < N; i++) {
                long minPurchaseCost = Long.MAX_VALUE;
                int minPurchaseIndex = -1;
                for (int j = 0; j <= i; j++) {
                    if (!lines.get(j).isMore()) {
                        continue;
                    }
                    long costOfPurchase = lines.get(j).getCostOfNextPurchase();
                    if (costOfPurchase < minPurchaseCost) {
                        minPurchaseCost = costOfPurchase;
                        minPurchaseIndex = j;
                    }
                }
                // We found min purcahse
                assertTrue(minPurchaseIndex >= 0);
                lines.get(minPurchaseIndex).purchaseNext();
            }

            // BigInteger result = BigInteger.valueOf(0);
            long result = 0;
            for (int i = 0; i < N; i++) {
                long totalPurchaseCostForLine = lines.get(i).getTotalPurchasedCost();
                result += totalPurchaseCostForLine;
                assertTrue(result > 0);
                // result.add(BigInteger.valueOf(totalPurchaseCostForLine));
            }
            return result;
        }
    }
    
    
    public static void runTests() {
        // Test line
        Line l = null;
        long[] arr1 = { 3, 2, 1 };
        l = new Line(arr1);
        assertTrue(l.getCostOfNextPurchase() == 1 + 1);
        l.purchaseNext();
        assertTrue(l.getTotalPurchasedCost() == 1 + 1 * 1);
        
        assertEqual(l.getCostOfNextPurchase(), 2 + 2 * 2 - 1 * 1);
        l.purchaseNext();
        assertEqual(l.getTotalPurchasedCost(), 1 + 2 + 2 * 2);
        assertTrue(l.isMore());

        assertEqual(l.getCostOfNextPurchase(), 3 + 3 * 3 - 2 * 2);
        l.purchaseNext();
        assertEqual(l.getTotalPurchasedCost(), 1 + 2 + 3 + 3 * 3);
        assertTrue(l.isMore() == false);

        // Pie Test

        PieEatingSpree spree = new PieEatingSpree(3, 2);
        long[] day1 = {1, 1};
        long[] day2 = { 100, 100 };
        long[] day3 = { 10000, 1000 };
        
        spree.addLine(day1);
        spree.addLine(day2);
        spree.addLine(day3);

        long totalCost = spree.getTotalCost();
        assertEqual(totalCost, 107);
    }


    public static void main(String[] args) throws IOException {
        System.out.println("Started");
        boolean testsOnly = false;
        runTests();
        if (testsOnly) {
            System.out.println("Completed");
            return;
        }

        File inputFile = new File(Config.LOCATION_TO_HACKER_CUP_DIR, "input/2/pie/pie_progress.txt");
        if (!inputFile.exists()) {
            throw new RuntimeException("");
        }

        int problemSize = -1;
        List<PieEatingSpree> pieDB = new ArrayList<PieEatingSpree>();
        BufferedReader br = new BufferedReader(new FileReader(inputFile));
        try {
            String line = br.readLine();
            problemSize = Integer.parseInt(line);
            for (int i = 0; i < problemSize; i++) {
                String pieInfoStr = br.readLine();
                String parts[] = pieInfoStr.split(" ");
                if (parts.length != 2) {
                    throw new RuntimeException("Wrong length: " + parts.length);
                }

                int N = Integer.parseInt(parts[0]);
                int M = Integer.parseInt(parts[1]);
                PieEatingSpree pSpree = new PieEatingSpree(N, M);

                for (int j = 0; j < N; j++) {
                    String line2 = br.readLine();
                    String[] chunks = line2.split(" ");
                    assertEqual(chunks.length, M);
                    pSpree.addLine(chunks);
                }
                pieDB.add(pSpree);
            }

        } finally {
            br.close();
        }

        List<Long> results = new ArrayList<Long>();
        for (PieEatingSpree pieInfo : pieDB) {
            Long pos = pieInfo.getTotalCost();
            results.add(pos);
        }
        if (results.size() != pieDB.size()) {
            throw new RuntimeException();
        }

        StringBuilder strBld = new StringBuilder();
        int i = 1;
        for (Long result : results) {
            strBld.append("Case #").append(i++).append(":").append(" ");
            strBld.append(result);
            strBld.append("\n");
        }

        File output = new File(Config.LOCATION_TO_HACKER_CUP_DIR,
                "input/2/pie/progress_pie_example_output_me_official.txt");
        PrintWriter out = new PrintWriter(output);
        out.write(strBld.toString());
        out.flush();
        out.close();

        checkFilesEqual(output, new File(Config.LOCATION_TO_HACKER_CUP_DIR,
                "input/2/pie/pie_progress_example_output.txt"));

        System.out.println("Completed");
    }
    
    private static long minValue(long[] arr) {
        long min = arr[0];
        for (int ktr = 0; ktr < arr.length; ktr++) {
            if (arr[ktr] < min) {
                min = arr[ktr];
            }
        }
        return min;
    }

    public static void assertTrue(boolean val) {
        if (!val) {
            throw new RuntimeException("Assertion fails");
        }
    }

    public static void assertEqual(int a, int expected) {
        if (Integer.compare(a, expected) != 0) {
            throw new RuntimeException("Assertion failed: value = " + a + " expected " + expected);
        }
    }

    public static void assertEqual(long a, long expected) {
        if (Long.compare(a, expected) != 0) {
            throw new RuntimeException("Assertion failed: value = " + a + " expected " + expected);
        }
    }

    private static void checkFilesEqual(File file1, File file2) throws IOException {
        byte[] f1 = Files.readAllBytes(file1.toPath());
        byte[] f2 = Files.readAllBytes(file2.toPath());
        if (!Arrays.equals(f1, f2)) {
            System.out.println("f1 = " + Arrays.toString(f1));
            System.out.println("f2 = " + Arrays.toString(f1));
            throw new RuntimeException(String.format("Validation failed. Files differ. Size: f1=%s, f2=%s", f1.length,
                    f2.length));
        }
    }

}
