package tigrantsat.hackercup.qualification;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import tigrantsat.hackercup.Config;

public class FightingtheZombie {

    public static Dice getBestDice(Dice[] dices) {
        Dice bestDice = dices[0];
        for (Dice d : dices) {
            if (d.getTotalAverageScore() > bestDice.getTotalAverageScore()) {
                if (d.X <= bestDice.X) {
                    bestDice = d;
                }
            }
        }
        return bestDice;
    }

    public static double chanceToKillZombie(int zHealth, Dice[] dices) {
        double best = 0;
//        Dice dice = getBestDice(dices);
        // System.out.println("Best dice = " + dice.toString());
        for (Dice d : dices) {
            double calcScore = d.calculateChanceOfHittingLargerOrEqual(zHealth);
            if (calcScore > best) {
                best = calcScore;
            }
        }

        return best;
    }

    private static BigInteger power(int a, int b) {
        BigInteger result = BigInteger.valueOf(1);
        for (int i = 0; i < b; i++) {
            result = result.multiply(BigInteger.valueOf(a));
            // assertNonNegative(result, "a is negative");
        }
        return result;
    }

    public static void runTests() {
        double chance = -1;
        // tests for dice
        
        power(20, 20);
        BigInteger res = power(3, 2);
        assertEqual(res.longValue(), 9);

        chance = new Dice("2d2").calculateChanceOfHittingLargerOrEqual(2);
        assertEqual(chance, 1.0);
        
        chance = new Dice("2d2").calculateChanceOfHittingLargerOrEqual(3);
        assertEqual(chance, 0.75);

        chance = new Dice("2d10").calculateChanceOfHittingLargerOrEqual(20);
        assertEqual(chance, 0.01);

        chance = new Dice("2d10").calculateChanceOfHittingLargerOrEqual(2);
        assertEqual(chance, 1.0);

        chance = new Dice("3d3").calculateChanceOfHittingLargerOrEqual(6);
        assertEqual(chance, (1 + 3 + 6 + 7) / (double) 27);

        Dice[] dices1 = { new Dice("2d4"), new Dice("2d5") };
        chance = chanceToKillZombie(10, dices1);
        assertEqual(chance, 0.2 * 0.2);

        Dice[] dices2 = { new Dice("2d4+4") };
        chance = chanceToKillZombie(4, dices2);
        assertEqual(chance, 1.0);

        Dice[] dices3 = { new Dice("2d4-8") };
        chance = chanceToKillZombie(1, dices3);
        assertEqual(chance, 0.0);

        Dice.flushDPcache();
        for (int i = 0; i < 1000; i++) {
            for (int j = 1; j <= 20; j++) {
                for (int k = 0; k <= 20; k++) {
                    if (k != 4 && k != 6 && k != 8 && k != 10 && k != 12 && k != 20) {
                        continue;
                    }
                    String diceStr = String.format("%sd%s", j, k);
                    chance = new Dice(diceStr).calculateChanceOfHittingLargerOrEqual(3);
                    // System.out.println("Calc J " + j);
                }
            }
            // System.out.println("Calc I" + i);
        }
        Dice.validateDPcache();

        // Check how long it will take to compute
        for (int i = 0; i < 1000 * 10; i++) {
            int randI = (int) (Math.random() * (double) 20 + 1);
            String diceStr = String.format("%sd%s", randI, 20);
            chance = new Dice(diceStr).calculateChanceOfHittingLargerOrEqual(5);
        }

        System.out.println("Tests completed");
    }

    public static boolean isEqual(double d1, double d2) {
        return d1 == d2 || isRelativelyEqual(d1, d2);
    }

    private static boolean isRelativelyEqual(double d1, double d2) {
        double delta = 0.0000000000001;
        return delta > Math.abs(d1 - d2) / Math.max(Math.abs(d1), Math.abs(d2));
    }

    public static void assertEqual(Double a, Double expected) {
        if (!isEqual(a, expected)) {
            throw new RuntimeException("Assertion failed: value = " + a + " expected " + expected);
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

    public static void assertTrue(int a, int b) {
        if (a != b) {
            throw new RuntimeException("Assertion failed");
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Started");

        boolean runTestsOnly = false;
        // runTests();
        if (runTestsOnly) {
            System.out.println("Completed");
            return;
        }

        File inputFile = new File(Config.LOCATION_TO_HACKER_CUP_DIR,
 "input/zombie/fighting_the_zombie.txt");
        if (!inputFile.exists()) {
            throw new RuntimeException("");
        }

        int problemSize = -1;
        List<Problem> problemsDB = new ArrayList<Problem>();
        BufferedReader br = new BufferedReader(new FileReader(inputFile));
        try {
            String totalDaysLine = br.readLine();
            problemSize = Integer.parseInt(totalDaysLine);
            for (int i = 0; i < problemSize; i++) {
                String header = br.readLine();
                String[] headerSplit = header.split(" ");
                assertEqual(headerSplit.length, 2);
                int zHealth = Integer.parseInt(headerSplit[0]);
                int diceCount = Integer.parseInt(headerSplit[1]);

                String dicesStr = br.readLine();
                String[] diceSplit = dicesStr.split(" ");
                assertEqual(diceSplit.length, diceCount);

                Problem problem = new Problem();
                problem.dices = new Dice[diceCount];
                problem.zHealth = zHealth;

                int j = 0;
                for (String diceDef : diceSplit) {
                    problem.dices[j++] = new Dice(diceDef);
                }

                problemsDB.add(problem);
            }

        } finally {
            br.close();
        }

        List<Double> results = new ArrayList<Double>();
        for (Problem problem : problemsDB) {
            double chance = chanceToKillZombie(problem.zHealth, problem.dices);
            results.add(chance);
        }
        if (results.size() != problemsDB.size()) {
            throw new RuntimeException();
        }

        StringBuilder strBld = new StringBuilder();
        int i = 1;
        for (Double result : results) {
            String formattedResult = String.format("%.6f", result);
            strBld.append("Case #").append(i++).append(":").append(" ").append(formattedResult);
            strBld.append("\n");
        }
        String result = strBld.toString();
        System.out.println(result);

        File output = new File(Config.LOCATION_TO_HACKER_CUP_DIR,
                "input/zombie/fighting_the_zombie_example_output_me_official_02.txt");
        PrintWriter out = new PrintWriter(output);
        out.write(result);
        out.flush();
        out.close();

        if (false) {
            checkFilesEqual(output, new File(Config.LOCATION_TO_HACKER_CUP_DIR,
                    "input/zombie/fighting_the_zombie_example_output.txt"));
        }

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

    private static void assertNonNegative(BigInteger n, String msg) {
        if (n.compareTo(BigInteger.ZERO) < 0) {
            throw new RuntimeException("Negatives non supported: " + msg);
        }
    }

    private static void assertNonNegative(long n, String msg) {
        if (n < 0) {
            throw new RuntimeException("Negatives non supported: " + msg);
        }
    }

    private static class Dice {
        private static HashMap<Integer, HashMap<Integer, BigInteger[]>> dpCache = new HashMap<Integer, HashMap<Integer, BigInteger[]>>();
        public static final int MAX_X = 20;
        public static final int MAX_Y = 20;
        public int X = 0;
        public int Y = 0;
        public int Z = 0;

        public double getTotalAverageScore() {
            double avgScore = (1 + Y) / (double) 2;
            return avgScore * X + Z;
        }

        public Dice(String diceStr) {
            String[] parts1 = diceStr.split("d");
            assertTrue(parts1.length, 2);
            X = Integer.parseInt(parts1[0]);
            if (parts1[1].contains("+")) {
                String[] parts2 = parts1[1].split("\\+");
                assertTrue(parts2.length, 2);

                Y = Integer.parseInt(parts2[0]);
                Z = Integer.parseInt(parts2[1]);
            } else if (parts1[1].contains("-")) {
                String[] parts2 = parts1[1].split("-");
                assertTrue(parts2.length, 2);

                Y = Integer.parseInt(parts2[0]);
                Z = -Integer.parseInt(parts2[1]);
            } else {
                Z = 0;
                Y = Integer.parseInt(parts1[1]);
            }
        }

        @Override
        public String toString() {
            if (Z != 0) {
                return String.format("%sd%s%s", X, Y, Z);
            } else {
                return String.format("%sd%s", X, Y);
            }
        }

        public static void validateDPcache() {
            if (dpCache.size() != 6) {
                throw new RuntimeException("Validation failed size != 6");
            }
            for (Integer diceSize : dpCache.keySet()) {
                if (dpCache.get(diceSize).size() != 19) {
                    throw new RuntimeException("Validation failed: " + dpCache.get(diceSize).size());
                }
            }
        }

        public static void flushDPcache() {
            System.out.println("dpCache flushed");
            dpCache.clear();
        }

        private static final void fillZero(BigInteger[] arr) {
            for (int i = 0; i < arr.length; i++) {
                arr[i] = BigInteger.valueOf(0);
            }
        }

        private static final void fillZero(long[] arr) {
            Arrays.fill(arr, 0);
        }

        public BigInteger[] calculateDPtable() {
            int maxXY = MAX_X * MAX_Y + 1;
            BigInteger[] arr1 = new BigInteger[maxXY];
            BigInteger[] arr2 = new BigInteger[maxXY];

            fillZero(arr1);
            fillZero(arr2);
            boolean useArray1 = true;
            
            for (int i = 1; i <= Y; i++) {
                // arr1[i] = 1;
                arr1[i] = BigInteger.valueOf(1);
            }
            int minBorder = 1;
            int maxBorder = Y;
            BigInteger[] finalArr = null;
            
            for (int i = 1; i <= X - 1; i++) {
                if (useArray1) {
                    fillZero(arr2);
                    finalArr = arr1;
                } else {
                    fillZero(arr1);
                    finalArr = arr2;
                }
                for (int j = 1; j <= Y; j++) {
                    for (int k = minBorder; k <= maxBorder; k++) {
                        if (useArray1) {
                            arr2[k + j] = arr2[k + j].add(arr1[k]);
                            assertNonNegative(arr2[k + j], String.format("i = %s,  j = %s, k = %s", i, j, k));
                        } else {
                            arr1[k + j] = arr1[k + j].add(arr2[k]);
                            assertNonNegative(arr1[k + j], String.format("i = %s,  j = %s, k = %s", i, j, k));
                        }
                    }
                }
                // System.out.println("Run #" + i);
                minBorder++;
                maxBorder += Y;
                useArray1 = !useArray1;
            }
            if (useArray1) {
                finalArr = arr1;
            } else {
                finalArr = arr2;
            }
            BigInteger allOutcomeSum = BigInteger.valueOf(0);
            for (int i = 0; i < maxXY; i++) {
                allOutcomeSum = allOutcomeSum.add(finalArr[i]);
            }
            BigInteger allOutcomeSumSimple = power(Y, X);
            if (!allOutcomeSumSimple.equals(allOutcomeSum)) {
                System.out.println("finalArr = " + Arrays.toString(finalArr));
                System.out.println("arr1 = " + Arrays.toString(arr1));
                System.out.println("arr2 = " + Arrays.toString(arr2));
                throw new RuntimeException(String.format(
                        "Validation failed: Calculated via DP (%s), via math (%s) for dice: %s", allOutcomeSum,
                        allOutcomeSumSimple, this));
            }
            return finalArr;
        }

        public double calculateChanceOfHittingNormalizedDP(final int threshold) {
            BigInteger[] dpTable = null;
            if (dpCache.get(Y) != null && dpCache.get(Y).get(X) != null) {
                dpTable = dpCache.get(Y).get(X);
            } else {
                dpTable = calculateDPtable();
                // System.out.println("Calculating db table for " + Y + " -> " +
                // X);
                if (dpCache.get(Y) == null) {
                    dpCache.put(Y, new HashMap<Integer, BigInteger[]>());
                }
                dpCache.get(Y).put(X, dpTable);
            }
            final int maxXY = MAX_X * MAX_Y + 1;
            assertEqual(dpTable.length, maxXY);
            
            if (threshold < 0) {
                // System.out.println("Case 1");
                return 1.0;
            } else if (threshold > maxXY) {
                // System.out.println("Case 2");
                return 0.0;
            } else {
                // System.out.println("Case 3");
                BigInteger matchedOutcomes = BigInteger.valueOf(0);
                for (int i = threshold; i < maxXY; i++) {
                    matchedOutcomes = matchedOutcomes.add(dpTable[i]);
                }
                // System.out.println("finalArr = " +
                // Arrays.toString(finalArr));
                // System.out.println("matchedOutcomes = " + matchedOutcomes);
                // System.out.println("threshold = " + threshold);
                // System.out.println("dice = " + this);
                // System.out.println("Z = " + Z);
                // TODO:
                BigInteger allOutcomeSumSimple = power(Y, X);
                double res = matchedOutcomes.doubleValue() / allOutcomeSumSimple.doubleValue();
                if (res < 0 || res > 1) {
                    throw new RuntimeException("Logic failure");
                }
                return res;
            }
        }

        public double calculateChanceOfHittingLargerOrEqual(int totalScore) {
            if (X == 1) {
                int totalOutcomes = 0;
                int fitOutcomes = 0;

                for (int i = 1; i <= Y; i++) {
                    totalOutcomes++;
                    int score = getScoreForY(i);
                    if (score >= totalScore) {
                        fitOutcomes++;
                    }
                }
                double res = fitOutcomes / (double) totalOutcomes;
                if (res < 0 || res > 1) {
                    throw new RuntimeException("Logic failure");
                }
                return res;
            } else {
                int normalizedScore = totalScore - Z;
                double calcScore = calculateChanceOfHittingNormalizedDP(normalizedScore);
                // System.out.println("calcScore = " + calcScore);
                return calcScore;
            }
        }

        private int getScoreForY(int y) {
            return X * y + Z;
        }
    }

    public static class Problem {
        public Dice[] dices;
        public int zHealth;
    }
}
