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

import tigrantsat.hackercup.Config;

public class Manic {
    public static class Move {
        public final Integer nodeA;
        public final Integer nodeB;

        public Move(Integer nodeA, Integer nodeB) {
            this.nodeA = nodeA;
            this.nodeB = nodeB;
        }
    }

    public static class Path {
        public final Integer nodeA;
        public final Integer nodeB;
        public final long distance;

        public Path(Integer nodeA, Integer nodeB, long distance) {
            this.nodeA = nodeA;
            this.nodeB = nodeB;
            this.distance = distance;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + (int) (distance ^ (distance >>> 32));
            result = prime * result + ((nodeA == null) ? 0 : nodeA.hashCode());
            result = prime * result + ((nodeB == null) ? 0 : nodeB.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Path other = (Path) obj;
            if (distance != other.distance)
                return false;
            if (nodeA == null) {
                if (other.nodeA != null)
                    return false;
            } else if (!nodeA.equals(other.nodeA))
                return false;
            if (nodeB == null) {
                if (other.nodeB != null)
                    return false;
            } else if (!nodeB.equals(other.nodeB))
                return false;
            return true;
        }
    }

    public static class PathComparator implements Comparator<Path> {

        @Override
        public int compare(Path o1, Path o2) {
            if (o1.distance < o2.distance)
                return -1;
            return 1;
        }

    }

    public static class RoadMap {
        public final long[][] roadMap;
        public final int nCount;

        public RoadMap(final int nCount, Path paths[]) {
            this.nCount = nCount;
            roadMap = new long[nCount + 1][nCount + 1];
            for (int i = 0; i <= nCount; i++) {
                Arrays.fill(roadMap[i], -1);
                roadMap[i][i] = 0;
            }

            Arrays.sort(paths, new PathComparator());
            for (int i = 1; i <= nCount; i++) {
                fillTableForNode(i, paths);
            }

        }

        // Dikstra starting since node
        private void fillTableForNode(int node, Path paths[]) {
            final long[] processedNodesPaths = new long[nCount + 1];
            Arrays.fill(processedNodesPaths, -1);
            processedNodesPaths[node] = 0;
            for (int i = 0; i <= nCount; i++) {
                for (Path p : paths) {
                    if (processedNodesPaths[p.nodeA] >= 0 && processedNodesPaths[p.nodeB] >= 0) {
                        continue; // This path lies in already processed nodes
                    }
                    if (processedNodesPaths[p.nodeA] >= 0) {
                        processedNodesPaths[p.nodeB] = processedNodesPaths[p.nodeA] + p.distance;
                        break;
                    }
                    if (processedNodesPaths[p.nodeB] >= 0) {
                        processedNodesPaths[p.nodeA] = processedNodesPaths[p.nodeB] + p.distance;
                        break;
                    }
                }
            }
            for (int i = 0; i <= nCount; i++) {
                roadMap[node][i] = processedNodesPaths[i];
                roadMap[i][node] = processedNodesPaths[i];
            }
        }

        public String getRoadMapAsString() {
            StringBuilder strBuilder = new StringBuilder();
            for (int i = 0; i <= nCount; i++) {
                strBuilder.append(Arrays.toString(roadMap[i])).append("\n");
            }
            return strBuilder.toString();

        }
    }

    public static class Problem {
        public final int N; // Towns
        public final int M; // Paths
        public final int K; // Transports
        public final RoadMap roadMap;
        public final List<Move> moves;

        public Problem(int N, int M, int K, Path paths[], List<Move> moves) {
            this.N = N;
            this.M = M;
            this.K = K;
            roadMap = new RoadMap(N, paths);
            this.moves = moves;
            assertTrue(M == paths.length);
            assertTrue(K == moves.size());
            assertTrue(moves.size() > 0);
            assertTrue(N >= 2);
        }

        public long solve(final boolean doDebug) {

            long totalCost = 0;
            int i = 0;
            Move currentMove = this.moves.get(i);
            final long costToReachFirst = roadMap.roadMap[1][currentMove.nodeA];
            if (costToReachFirst < 0) {
                if (doDebug) {
                    System.out.println("move 1 unreacheable");
                }
                return -1;
            } else {
                totalCost += costToReachFirst;
            }
            i++;

            int currentLocation = currentMove.nodeA;
            int load1Dst = currentMove.nodeB;
            int load2Dst = 0;
            int currentLoad = 0;
            if (doDebug) {
                System.out.println("\t----Starting at first location with total = " + totalCost);
            }

            while (true) {
                assertTrue(currentLoad <= 2);
                assertTrue(currentLoad >= 0);
                if (doDebug) {
                    System.out.println("Making decision at point " + currentLocation + ", current total cost "
                            + totalCost + ", current load " + currentLoad + ", i = " + i);
                }

                if (i >= this.moves.size()) {
                    // no more moves, except current
                    // if (load1Dst == 0) {
                    // if (doDebug) {
                    // System.out.println("\tNo more destinations. Everything was unloaded at "
                    // + currentLocation);
                    // }
                    // break;
                    // }
                    if (doDebug) {
                        System.out.println("\tLast one destination remained. From " + currentMove.nodeA + " to "
                                + currentMove.nodeB + ". Driving to remaning point " + currentMove.nodeA + " from "
                                + currentLocation);
                    }
                    totalCost += roadMap.roadMap[currentLocation][currentMove.nodeA];
                    totalCost += roadMap.roadMap[currentMove.nodeA][currentMove.nodeB];
                    if (doDebug) {
                        System.out.println("Completed with total cost = " + totalCost);
                    }
                    break;

                }
                if (currentLocation != currentMove.nodeA) {
                    long driveToPickup = (roadMap.roadMap[currentLocation][currentMove.nodeA]);

                    if (doDebug) {
                        System.out.println("\tDriving from " + currentLocation + " to " + currentMove.nodeA
                                + " with length " + driveToPickup);
                    }
                    totalCost += driveToPickup;
                }
                Move nextMove = moves.get(i);

                // for 1:
                long from1pickup_2pickup = roadMap.roadMap[currentMove.nodeA][nextMove.nodeA];
                long from2pickup_1dump = roadMap.roadMap[nextMove.nodeA][currentMove.nodeB];
                long from1dump_2dump = roadMap.roadMap[currentMove.nodeB][nextMove.nodeB];
                // for 2:
                long from1pickup_1dump = roadMap.roadMap[currentMove.nodeA][currentMove.nodeB];
                long from1dump_2pickup = roadMap.roadMap[currentMove.nodeB][nextMove.nodeA];
                long from2pickup_2dump = roadMap.roadMap[nextMove.nodeA][nextMove.nodeB];
                if (from1pickup_2pickup < 0 || from2pickup_1dump < 0 || from2pickup_1dump < 0 || from1pickup_1dump < 0
                        || from1dump_2pickup < 0 || from2pickup_2dump < 0) {
                    // unreacheable
                    return -1;
                }

                long costToPerformThrough = from1pickup_2pickup + from2pickup_1dump + from1dump_2dump;
                long costToPerformSeparately = from1pickup_1dump + from1dump_2pickup + from2pickup_2dump;
                boolean goThrow = false;
                if (currentLoad == 0) {
                    if (costToPerformThrough < costToPerformSeparately) {
                        goThrow = true;
                    } else if (costToPerformThrough == costToPerformSeparately && this.moves.size() > i + 1) {
                        Move moveAfterIt = moves.get(i + 1);
                    }
                }
                if (goThrow) {
                    // picking up second cargo and go to first unload
                    long addingCost = (from1pickup_2pickup + from2pickup_1dump);
                    if (doDebug) {
                        String vs = String.format("(%s vs %s = (through vs separate)", costToPerformThrough,
                                costToPerformSeparately);
                        System.out.println("\tGoing though " + vs + ": from " + currentMove.nodeA + " though "
                                + nextMove.nodeA + " to " + currentMove.nodeB + "; Total Cost now = " + totalCost
                                + " adding " + addingCost);
                    }
                    totalCost += addingCost;
                    currentLoad += 2;
                    load1Dst = currentMove.nodeB;
                    load2Dst = nextMove.nodeB;

                } else {
                    // going directly to first unload
                    if (doDebug) {
                        String vs = String.format("(%s vs %s = (through vs separate)", costToPerformThrough,
                                costToPerformSeparately);
                        System.out.println("\tGoing directly " + vs + ": from " + currentMove.nodeA + " to "
                                + currentMove.nodeB + "; TotalCost now = " + totalCost + " adding cost "
                                + from1pickup_1dump);
                    }
                    totalCost += from1pickup_1dump;
                    currentLoad++;
                    load1Dst = currentMove.nodeB;
                }
                // Drive
                currentLocation = currentMove.nodeB;
                currentMove = nextMove;

                // Unloading
                int unloadedLoad = 0;
                if (load1Dst == currentLocation) {
                    currentLoad--;
                    load1Dst = load2Dst;
                    load2Dst = 0;
                    unloadedLoad++;
                    i++;
                }
                if (load1Dst == currentLocation) {
                    // Unloading second. That also means that move was completed
                    currentLoad--;
                    load1Dst = 0;
                    unloadedLoad++;
                    if (i >= this.moves.size()) {
                        if (doDebug) {
                            System.out.println("All done");
                        }
                        break;
                    } else {
                        currentMove = this.moves.get(i);
                    }
                    i++;

                }
                if (doDebug) {
                    System.out.println("\tUnloaded at location " + currentLocation + ", unloaded load " + unloadedLoad
                            + ", current load " + currentLoad + ", i = " + i);
                }
            }
            return totalCost;
        }
    }

    public static void runTests() {
        Path[] paths1 = { new Path(1, 2, 3), new Path(3, 4, 2), new Path(1, 1, 1) };
        Arrays.sort(paths1, new PathComparator());
        assertEqual(paths1[0].distance, 1);

        Path[] paths2 = { new Path(1, 2, 1), new Path(2, 3, 4), new Path(3, 1, 1) };
        RoadMap rMap = new RoadMap(4, paths2);
        // System.out.println(rMap.getRoadMapAsString());
        assertEqual(rMap.roadMap[2][3], 2);

        // ========

        Path[] paths_p1 = { new Path(1, 2, 4), new Path(2, 3, 7) };
        List<Move> moves_p1 = new ArrayList<Move>();
        moves_p1.add(new Move(2, 1));
        moves_p1.add(new Move(3, 2));
        moves_p1.add(new Move(3, 2));
        Problem p = new Problem(3, 2, 3, paths_p1, moves_p1);
        long minSize_p1 = p.solve(false);
        assertEqual(minSize_p1, 26);

        Path[] paths_p2 = { new Path(1, 2, 1), new Path(2, 3, 1), new Path(3, 4, 1) };
        List<Move> moves_p2 = new ArrayList<Move>();
        moves_p2.add(new Move(1, 2));
        moves_p2.add(new Move(1, 2));
        moves_p2.add(new Move(2, 4));
        moves_p2.add(new Move(2, 4));
        Problem p2 = new Problem(4, 3, 4, paths_p2, moves_p2);
        long minSize_p2 = p2.solve(false);
        assertEqual(minSize_p2, 3);

        Path[] paths_p3 = { new Path(1, 2, 7), new Path(1, 2, 4), new Path(2, 3, 7), new Path(2, 3, 2) };
        List<Move> moves_p3 = new ArrayList<Move>();
        moves_p3.add(new Move(1, 2));
        moves_p3.add(new Move(3, 2));
        moves_p3.add(new Move(3, 2));
        Problem p3 = new Problem(3, 4, 3, paths_p3, moves_p3);
        long minSize_p3 = p3.solve(false);
        assertEqual(minSize_p3, 4 + 2 + 2);

    }

    public static void main(String[] args) throws IOException {
        System.out.println("Started");
        boolean testsOnly = false;
        runTests();
        if (testsOnly) {
            System.out.println("Completed");
            return;
        }

        File inputFile = new File(Config.LOCATION_TO_HACKER_CUP_DIR, "input/2/manic/manic_moving.txt");
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
                if (parts.length != 3) {
                    throw new RuntimeException("Wrong length: " + parts.length);
                }

                int N = Integer.parseInt(parts[0]);
                int M = Integer.parseInt(parts[1]);
                int K = Integer.parseInt(parts[2]);

                Path paths[] = new Path[M];
                for (int j = 0; j < M; j++) {
                    String line2 = br.readLine();
                    String[] chunks = line2.split(" ");
                    assertEqual(chunks.length, 3);
                    final Integer A = Integer.parseInt(chunks[0]);
                    final Integer B = Integer.parseInt(chunks[1]);
                    final long G = Long.parseLong(chunks[2]);
                    paths[j] = new Path(A, B, G);
                }

                List<Move> moves = new ArrayList<Move>();
                for (int j = 0; j < K; j++) {
                    String line2 = br.readLine();
                    String[] chunks = line2.split(" ");
                    assertEqual(chunks.length, 2);
                    final int S = Integer.parseInt(chunks[0]);
                    final int D = Integer.parseInt(chunks[1]);
                    moves.add(new Move(S, D));
                }

                Problem problem = new Problem(N, M, K, paths, moves);
                problemDB.add(problem);
            }

        } finally {
            br.close();
        }

        List<Long> results = new ArrayList<Long>();
        int problemCount = 1;
        for (Problem problem : problemDB) {
            boolean doDebug = problemCount == -1 ? true : false;
            long res = problem.solve(doDebug);
            results.add(res);
            problemCount++;
        }
        if (results.size() != problemDB.size()) {
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
                "input/2/manic/manic_moving_example_output_me_official.txt");
        PrintWriter out = new PrintWriter(output);
        out.write(strBld.toString());
        out.flush();
        out.close();

        checkFilesEqual(output, new File(Config.LOCATION_TO_HACKER_CUP_DIR,
                "input/2/manic/manic_moving_example_output.txt"));

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
