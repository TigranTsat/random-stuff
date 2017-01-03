package assignment_12;

import java.util.Arrays;

// That does not belong to assigment, but I used assigment project to test some ideas
public class DPtest {

    public static void main(String[] args) {
        int maxN = 60;
        boolean[] arr = new boolean[maxN];

        for (int i = 0; i < maxN; i++) {
            int index6 = i - 6;
            int index9 = i - 9;
            int index20 = i - 20;

            boolean reacheable = false;

            if (index6 == 0) {
                reacheable = true;
            } else if (index6 > 0) {
                reacheable = reacheable || arr[index6];
            }
            if (index9 == 0) {
                reacheable = true;
            } else if (index9 > 0) {
                reacheable = reacheable || arr[index9];
            }
            if (index20 == 0) {
                reacheable = true;
            } else if (index20 > 0) {
                reacheable = reacheable || arr[index20];
            }
            arr[i] = reacheable;
        }
        System.out.println(Arrays.toString(arr));
    }

}
