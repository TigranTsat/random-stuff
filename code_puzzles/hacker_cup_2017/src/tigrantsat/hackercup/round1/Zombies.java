package tigrantsat.hackercup.round1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import tigrantsat.hackercup.Config;

public class Zombies {

    public static class Point2D {
        public final long x;
        public final long y;
        public Point2D(long x, long y) {
            assertTrue(x >= 0);
            assertTrue(y >= 0);
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "(" + x + ", " + y + ")";
        }
    }
    
    public static class Point2DyComparator implements Comparator<Point2D> {

        @Override
        public int compare(Point2D o1, Point2D o2) {
            if (o1.y < o2.y)
                return -1;
            if (o1.y == o2.y && o1.x == o2.x) {
                return 0;
            }
            return 1;
        }

    }

    public static class Region {
        public final long x1;
        public final long y1;
        public final long x2;
        public final long y2;
        public final List<Point2D> pointsInRegion;

        public Region(long x1, long y1, long x2, long y2, List<Point2D> pointsInRegion) {
            // assertTrue(x1 >= 0);
            // assertTrue(y1 >= 0);
            // assertTrue(x2 >= 0);
            // assertTrue(y2 >= 0);
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.pointsInRegion = pointsInRegion;
        }

        @Override
        public String toString() {
            return "Region [x1=" + x1 + ", y1=" + y1 + ", x2=" + x2 + ", y2=" + y2 + ", pointsInRegion="
                    + pointsInRegion + "]";
        }

    }

    // public static void filterPointsFitIntoRegtion(long x, long y, long l,
    // List<Point2D> input, List<Point2D> output) {
    //
    // }

    public static Region findMostDenseRegion(long l, final List<Point2D> points) {
        List<Point2D> maxDensePoints = new ArrayList<Point2D>();
        long x = Long.MIN_VALUE;
        long y = Long.MIN_VALUE;
        
        SortedSet<Point2D> pointsThatFitOnWidth = new TreeSet<Point2D>(new Point2DyComparator());
        for (Point2D currentPoint : points) {
            // Assuming that currentPoint is best and placing it to bottom left
            // corner
            pointsThatFitOnWidth.clear();
            for (Point2D pt : points) {
                if (pt.x >= currentPoint.x && pt.x <= currentPoint.x + l && (Math.abs(pt.y - currentPoint.y) <= l)) {
                    pointsThatFitOnWidth.add(pt);
                    // System.out.println("Adding " + pt);
                } else {
                    // System.out.println("Point " + pt + " does not fit");
                }
            }
            // System.out.println("For point " + currentPoint +
            // " found next poits width: " + pointsThatFitOnWidth);
            // if (true) {
            // continue;
            // }
            // Finding the best option (for each point):
            List<Point2D> currentPoints = new ArrayList<Point2D>();
            for (Point2D bottomPoint : pointsThatFitOnWidth) {
                currentPoints.clear();
                for (Point2D p : pointsThatFitOnWidth) {
                    if (Math.abs(p.y - bottomPoint.y) <= l) {
                        currentPoints.add(p);
                    }
                }

                // System.out.println("currentPoints = " + currentPoints +
                // " for point bottomPoint = " + bottomPoint);
                if (currentPoints.size() > maxDensePoints.size()) {
                    maxDensePoints = currentPoints;
                    x = bottomPoint.x;
                    y = bottomPoint.y;
                }
            }
        }
        // System.out.println("Found points: " + maxDensePoints.toString());
        return new Region(x, y, x + l, y + l, maxDensePoints);
    }
    
    public static class Problem {
        public final long l;
        public final List<Point2D> points = new ArrayList<Point2D>();

        public Problem(long l) {
            this.l = l;
        }
    }

    public static int solve(long l, final List<Point2D> points) {
        Region topRegion = findMostDenseRegion(l, points);
        List<Point2D> pointsWithoutTopRegion = new ArrayList<Point2D>();

        for (Point2D p : points) {
            if (topRegion.pointsInRegion.contains(p)) {
                continue;
            }
            pointsWithoutTopRegion.add(p);
        }
        Region secondTopRegion = findMostDenseRegion(l, pointsWithoutTopRegion);

        int totalKill = topRegion.pointsInRegion.size() + secondTopRegion.pointsInRegion.size();
        return totalKill;
    }
    
    public static void runTests() {
        List<Point2D> lst1 = new ArrayList<Point2D>();
        lst1.add(new Point2D(0, 0));
        lst1.add(new Point2D(5, 0));
        lst1.add(new Point2D(0, 5));
        lst1.add(new Point2D(5, 5));
        lst1.add(new Point2D(1, 1));
        Region r1 = findMostDenseRegion(1, lst1);
        System.out.println("Got region r1: " + r1);
        int r1Solution = solve(1, lst1);
        assertEqual(r1Solution, 3);

        List<Point2D> lst2 = new ArrayList<Point2D>();
        lst2.add(new Point2D(0, 0));
        lst2.add(new Point2D(5, 0));
        lst2.add(new Point2D(0, 5));
        lst2.add(new Point2D(5, 5));
        lst2.add(new Point2D(1, 1));
        lst2.add(new Point2D(6, 6));
        lst2.add(new Point2D(5, 6));
        Region r2 = findMostDenseRegion(3, lst2);
        System.out.println("Got region r2: " + r2);
        int r2Solution = solve(3, lst2);
        assertEqual(r2Solution, 3 + 2);

        List<Point2D> lst3 = new ArrayList<Point2D>();
        for (int i =0; i < 70; i++) {
            long x = Math.round(Math.random() * 20);
            long y = Math.round(Math.random() * 20);
            lst3.add(new Point2D(x, y));
        }
        int r3Solution = solve(100, lst3);
    }


    public static void main(String[] args) throws IOException {
        System.out.println("Started");
        boolean testsOnly = false;
        runTests();
        if (testsOnly) {
            System.out.println("Completed");
            return;
        }

        File inputFile = new File(Config.LOCATION_TO_HACKER_CUP_DIR,
 "input/2/zombie/fighting_the_zombies.txt");
        if (!inputFile.exists()) {
            throw new RuntimeException("");
        }

        int problemSize = -1;
        List<Problem> problemDB = new ArrayList<Problem>();
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
                long R = Long.parseLong(parts[1]);
                Problem problem = new Problem(R);

                for (int j = 0; j < N; j++) {
                    String line2 = br.readLine();
                    String[] chunks = line2.split(" ");
                    assertEqual(chunks.length, 2);
                    final long x = Long.parseLong(chunks[0]);
                    final long y = Long.parseLong(chunks[1]);
                    problem.points.add(new Point2D(x, y));
                }
                problemDB.add(problem);
            }

        } finally {
            br.close();
        }

        List<Integer> results = new ArrayList<Integer>();
        for (Problem problem : problemDB) {
            Integer pos = solve(problem.l, problem.points);
            results.add(pos);
        }
        if (results.size() != problemDB.size()) {
            throw new RuntimeException();
        }

        StringBuilder strBld = new StringBuilder();
        int i = 1;
        for (Integer result : results) {
            strBld.append("Case #").append(i++).append(":").append(" ");
            strBld.append(result);
            strBld.append("\n");
        }

        File output = new File(Config.LOCATION_TO_HACKER_CUP_DIR,
                "input/2/zombie/fighting_the_zombies_example_output_me_official.txt");
        PrintWriter out = new PrintWriter(output);
        out.write(strBld.toString());
        out.flush();
        out.close();

        checkFilesEqual(output, new File(Config.LOCATION_TO_HACKER_CUP_DIR,
                "input/2/zombie/fighting_the_zombies_example_output.txt"));

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
