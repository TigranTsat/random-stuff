package assignment_15;

import ignore.TestingUtils;

public class App {


	/**
	 * 
	Given a string, return the length of the longest streak of the same chars in the string. 

	<br>
	<br>

	 * <b>EXPECTATIONS:</b><br>
		longestStreak("hayyeu") <b>---></b> 2<br>
		longestStreak("XPNzzzddOOOxx")  <b>---></b> 3 <br>
		longestStreak("")  <b>---></b> 0 <br>
	 */
    public static int longestStreak(String str) {
        char knownChar = 0;
        int maxKnownLen = 0;
        int currentLen = 0;
        for (int i = 0; i < str.length() + 1; i++) {
            if (i < str.length() && str.charAt(i) == knownChar) {
                currentLen++;
            } else {
                if (maxKnownLen < currentLen) {
                    maxKnownLen = currentLen;
                }
                currentLen = 1;
                if (i == str.length()) {
                    break;
                }
                knownChar = str.charAt(i);
            }
        }
        return maxKnownLen;
    }

    // ----------------------STARTING POINT OF PROGRAM. IGNORE BELOW
    // --------------------//
    public static void main(String args[]) {
        TestingUtils.runTests();

    }
}

