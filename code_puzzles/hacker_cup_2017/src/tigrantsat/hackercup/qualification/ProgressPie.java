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

public class ProgressPie {
    public enum Position {
        WHITE, BLACK;
    }

    public static Position checkPoint(int progress, int x, int y) {
        if (progress < 0) {
            throw new RuntimeException("progress < 0");
        }
        if (progress > 100) {
            throw new RuntimeException("progress < 0");
        }
        final int center = 50;
        final int radius = 50;
        double distanceSquared = (x - center) * (x - center) + (y - center) * (y - center);
        if (distanceSquared > radius * radius) {
            // System.out.println("Point " + x + " ;" + y + " is outside");
            return Position.WHITE;
        }
        double hip = Math.sqrt(distanceSquared);
        double adj = y - center;
        double cosAlpha = (adj) / (double) (hip);
        // System.out.println("cosAlpha = " + cosAlpha);
        double degrees = Math.toDegrees(Math.acos(cosAlpha));
        double degressEffective = degrees;
        if (x < 50) {
            degressEffective = 360 - degrees;
        }
        // System.out.println("degressEffective = " + degressEffective + " for "
        // + x + " ;" + y);

        double progressNormalized = progress / (double) 100;
        double pointPositionNormalized = degressEffective / (double) 360;
        // System.out.println("progressNormalized = " + progressNormalized +
        // " pointPositionNormalized = "
        // + pointPositionNormalized);
        if (pointPositionNormalized > progressNormalized) {
            return Position.WHITE;
        } else {
            return Position.BLACK;
        }
    }

    public static void runTests() {
        assertPos(checkPoint(100, 100, 100), Position.WHITE);
        assertPos(checkPoint(100, 0, 100), Position.WHITE);
        assertPos(checkPoint(100, 100, 0), Position.WHITE);
        assertPos(checkPoint(100, 0, 0), Position.WHITE);

        assertPos(checkPoint(30, 50, 100), Position.BLACK);
        assertPos(checkPoint(30, 100, 50), Position.BLACK);
        assertPos(checkPoint(30, 50, 0), Position.WHITE);
        assertPos(checkPoint(30, 0, 50), Position.WHITE);
        assertPos(checkPoint(30, 45, 80), Position.WHITE);
        assertPos(checkPoint(30, 49, 80), Position.WHITE);
        assertPos(checkPoint(42, 67, 14), Position.WHITE);

        assertPos(checkPoint(100, 49, 70), Position.BLACK);
    }

    private static void assertPos(Position a, Position b) {
        if (!a.equals(b)) {
            throw new RuntimeException("Validation failed");
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Started");
        boolean testsOnly = false;
        runTests();
        if (testsOnly) {
            return;
        }
        File inputFile = new File(Config.LOCATION_TO_HACKER_CUP_DIR, "input/progress_pie/progress_pie.txt");
        if (!inputFile.exists()) {
            throw new RuntimeException("");
        }

        int problemSize = -1;
        List<int[]> pieDB = new ArrayList<int[]>();
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
                int[] pieInfo = new int[3];
                for (int j = 0; j < 3; j++) {
                    pieInfo[j] = Integer.parseInt(parts[j]);
                }
                pieDB.add(pieInfo);
            }

        } finally {
            br.close();
        }
        List<Position> results = new ArrayList<Position>();
        for (int[] pieInfo : pieDB) {
            Position pos = checkPoint(pieInfo[0], pieInfo[1], pieInfo[2]);
            results.add(pos);
        }
        if (results.size() != pieDB.size()) {
            throw new RuntimeException();
        }

        StringBuilder strBld = new StringBuilder();
        int i = 1;
        for (Position result : results) {
            strBld.append("Case #").append(i++).append(":").append(" ");
            if (result == Position.BLACK) {
                strBld.append("black");
            } else {
                strBld.append("white");
            }
            strBld.append("\n");
        }

        File output = new File(Config.LOCATION_TO_HACKER_CUP_DIR,
                "input/progress_pie/progress_pie_example_output_me_official.txt");
        PrintWriter out = new PrintWriter(output);
        out.write(strBld.toString());
        out.flush();
        out.close();
        
        checkFilesEqual(output, new File(Config.LOCATION_TO_HACKER_CUP_DIR,
                "input/progress_pie/progress_pie_example_output.txt"));

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
