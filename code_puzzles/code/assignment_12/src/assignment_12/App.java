package assignment_12;

import ignore.TestingUtils;

public class App {


	/**
	 * 
	Return a version of the given string, where for every star (*) 
	in the string the star and the chars immediately to its left and right are gone. 
	So "ab*cd" yields "ad" and "ab**cd" also yields "ad". 	<br>
	<br>

	 * <b>EXPECTATIONS:</b><br>
		starKill("cd*zq")  <b>---></b>"cq"<br>
		starKill("ab**cd")    <b>---></b> "ad" <br>
		starKill("wacy*xko") <b>---></b> "wacko" <br>
	 */
    public static String starKill(String str) {
        int len = str.length();
        if (len == 0) {
            // Better to return than create StringBuilder
            return "";
        }
        if (len == 1) {
            return str.charAt(0) == '*' ? "" : str;
        }

        StringBuilder strBld = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            if (i >= 1 && i < len - 1) {
                if (str.charAt(i + 1) != '*' && str.charAt(i) != '*' && str.charAt(i - 1) != '*') {
                    strBld.append(str.charAt(i));
                }
            } else if (i == 0) {
                if (str.charAt(0) != '*' && str.charAt(1) != '*') {
                    strBld.append(str.charAt(i));
                }
            } else {
                // i == len -1
                if (str.charAt(len - 1) != '*' && str.charAt(len - 2) != '*') {
                    strBld.append(str.charAt(i));
                }
            }
        }

        return strBld.toString();
    }

    // ----------------------STARTING POINT OF PROGRAM. IGNORE BELOW
    // --------------------//
    public static void main(String args[]) {
        TestingUtils.runTests();

    }
}
