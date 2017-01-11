package tigrantsat.hackercup.qualification;

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

public class LazyLoading {
    private static final int MIN_COUNTABLE_LOAD = 50;

    public static int maxMovements(int[] weights) {
        Arrays.sort(weights);
        if (weights.length == 0) {
            return 0;
        }
        int minPos = 0;
        int maxPos = weights.length - 1;

        int totalMovements = 0;
        int movementWeight = 0;
        int maxWeight = 0;

        while (maxPos >= minPos) {
            if (maxWeight == 0) {
                maxWeight = weights[maxPos];
                maxPos--;
            } else {
                minPos++;
            }
            movementWeight += maxWeight;
            if (movementWeight >= MIN_COUNTABLE_LOAD) {
                maxWeight = 0;
                movementWeight = 0;
                totalMovements++;
            }
        }

        return totalMovements;
    }

    public static void runTests() {
        int[] weights1 = { 1, 2, 3, 4 };
        assertTrue(maxMovements(weights1), 0);
        
        int[] weights2 = { 50, 50 };
        assertTrue(maxMovements(weights2), 2);

        int[] weights3 = { 49, 1, 50 };
        assertTrue(maxMovements(weights3), 2);

        int[] weights4 = { 3, 3, 3, 3, 10 };
        assertTrue(maxMovements(weights4), 1);

        int[] weights5 = { 30, 30, 35, 1, 20, 1 };
        assertTrue(maxMovements(weights5), 3);
    }

    public static void assertTrue(int a, int b) {
        if (a != b) {
            throw new RuntimeException("Assertion failed");
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Started");

        boolean runTestsOnly = false;
        runTests();
        if (runTestsOnly) {
            System.out.println("Completed");
            return;
        }

        File inputFile = new File(Config.LOCATION_TO_HACKER_CUP_DIR, "input/lazy_loading/lazy_loading.txt");
        if (!inputFile.exists()) {
            throw new RuntimeException("");
        }

        int problemSize = -1;
        List<int[]> daysDB = new ArrayList<int[]>();
        BufferedReader br = new BufferedReader(new FileReader(inputFile));
        try {
            String totalDaysLine = br.readLine();
            problemSize = Integer.parseInt(totalDaysLine);
            for (int i = 0; i < problemSize; i++) {
                String totalNumberWeightsLine = br.readLine();
                int totalNumberWeights = Integer.parseInt(totalNumberWeightsLine);
                int[] problem = new int[totalNumberWeights];
                for (int j = 0; j < totalNumberWeights; j++) {
                    String valStr = br.readLine();
                    problem[j] = Integer.parseInt(valStr);
                }
                daysDB.add(problem);
            }

        } finally {
            br.close();
        }

        List<Integer> results = new ArrayList<Integer>();
        for (int[] problem : daysDB) {
            Integer res = maxMovements(problem);
            results.add(res);
        }
        if (results.size() != daysDB.size()) {
            throw new RuntimeException();
        }

        StringBuilder strBld = new StringBuilder();
        int i = 1;
        for (Integer result : results) {
            strBld.append("Case #").append(i++).append(":").append(" ").append(result);
            strBld.append("\n");
        }

        File output = new File(Config.LOCATION_TO_HACKER_CUP_DIR,
                "input/lazy_loading/lazy_loading_example_output_me_official.txt");
        PrintWriter out = new PrintWriter(output);
        out.write(strBld.toString());
        out.flush();
        out.close();

        checkFilesEqual(output, new File(Config.LOCATION_TO_HACKER_CUP_DIR,
                "input/lazy_loading/lazy_loading_example_output.txt"));

        System.out.println("Completed");
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
