package assignment_11;

import ignore.TestingUtils;

public class App {


	/**

	Given a string, return a string where for every char in the original, append another. 
	<br>
	<br>

	 * <b>EXPECTATIONS:</b><br>
		repeatChar("The")  <b>---></b>"TThhee"<br>
		repeatChar("AAbb")    <b>---></b> "AAAAbbbb" <br>
		repeatChar("Hi-There") <b>---></b> "HHii--TThheerree" <br>
	 */

    public static String repeatChar(String str) {
        if (str == null) {
            return null;
        }
        StringBuilder strBld = new StringBuilder(str.length() * 2);
        for (int i = 0; i < str.length(); i++) {
            strBld.append(str.charAt(i)).append(str.charAt(i));
        }
        return strBld.toString();

    }

    // ----------------------STARTING POINT OF PROGRAM. IGNORE BELOW
    // --------------------//
    public static void main(String args[]) {
        TestingUtils.runTests();

    }
}
