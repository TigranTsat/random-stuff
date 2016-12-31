public class StringUtils {

    static String reverse(String str) {
        if (str == null) {
            return null;
        }
        int len = str.length();
        char c[] = new char[len];
        for (int i = 0; i < len; i++) {
            c[len - i - 1] = str.charAt(i);
        }
        return new String(c);
    }

    static String reverse(String str, int startIndex, int endIndex) {
        if (str == null) {
            return null;
        }
        int startEffective = startIndex;
        int endEffective = endIndex;
        int len = str.length();
        if (startIndex < 0) {
            startEffective = 0;
        }
        if (startIndex >= len - 1) {
            return str;
        }
        if (endIndex <= 0) {
            return str;
        }
        if (endIndex >= len - 1) {
            endEffective = len - 1;
        }
        if (endIndex <= startIndex) {
            return str;
        }
        // Now we have proper startEffective, endEffective
        char[] c = new char[len];
        for (int i = 0; i < startEffective; i++) {
            c[i] = str.charAt(i);
        }
        for (int i = startEffective; i <= endEffective; i++) {
            c[endEffective - (i - startEffective)] = str.charAt(i);
        }
        for (int i = endEffective + 1; i < len; i++) {
            c[i] = str.charAt(i);
        }
        return new String(c);
    }

    static int copyChars(String original, char[] c, int start, int end, int cStartPosition) {
        if (c.length - 1 < end) {
            throw new IllegalArgumentException();
        }
        for (int i = start; i <= end; i++) {
            c[cStartPosition++] = original.charAt(i);
        }
        return cStartPosition;
    }

    static String reverseWords(String sentence) {
        if (sentence == null || sentence.length() <= 1) {
            return sentence;
        }
        final int len = sentence.length();
        int wordStartPos = len - 1;
        int wordEndPos = wordStartPos;
        boolean isWord = false;
        char[] c = new char[len];
        int currentTargetPos = 0;
        while (true) {
            if (wordStartPos >= 0 && sentence.charAt(wordStartPos) != ' ') {
                if (isWord == false) {
                    wordEndPos = wordStartPos;
                }
                isWord = true;
            } else {
                if (isWord) {
                    currentTargetPos = copyChars(sentence, c, wordStartPos + 1, wordEndPos, currentTargetPos);
                    isWord = false;
                }
                if (wordStartPos >= 0) {
                    c[currentTargetPos++] = ' ';
                }
            }
            wordStartPos--;
            if (wordStartPos < -1) {
                break;
            }
        }
        return new String(c);

    }

    static boolean isPalindrom(String str) {
        if (str == null || str.length() <= 1) {
            return true;
        }
        int len = str.length();
        int middle = len / 2;
        
        for (int i = 0; i <= middle; i++) {
            if (str.charAt(i) != str.charAt(len - 1 - i)) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        System.out.println("Running");
        testReverseString();
        testReverseSubString();
        testReverseWords();
        testPalindrom();
        System.out.println("Completed");
    }

    private static void testPalindrom() {
        assertTrue(isPalindrom(" "));
        assertTrue(isPalindrom("  "));
        assertTrue(isPalindrom("11"));
        assertTrue(isPalindrom(null));
        assertTrue(isPalindrom("dog god"));
        assertTrue(isPalindrom("dog  god"));
        assertTrue(isPalindrom("dog a god"));

        assertFalse(isPalindrom("12"));
        assertFalse(isPalindrom("21"));
        assertFalse(isPalindrom("hello"));
        assertFalse(isPalindrom(" l"));
        assertFalse(isPalindrom("dog god "));
    }

    private static void testReverseWords() {
        assertEquals(reverseWords("dog cat"), "cat dog");
        assertEquals(reverseWords("dog, cat"), "cat dog,");
        assertEquals(reverseWords("dog  cat"), "cat  dog");
        assertEquals(reverseWords(null), null);
        assertEquals(reverseWords(" "), " ");
        assertEquals(reverseWords(""), "");
        assertEquals(reverseWords("a b c"), "c b a");
        assertEquals(reverseWords("aaa769aaaa"), "aaa769aaaa");
        assertEquals(reverseWords(" b"), "b ");
    }

    private static void testReverseSubString() {
        assertEquals(reverse("dog cat", 4, 6), "dog tac");
        assertEquals(reverse(null, 4, 6), null);
        assertEquals(reverse("", 0, 0), "");
        assertEquals(reverse("abc", 0, 2), "cba");
        assertEquals(reverse("abc", 2, -2), "abc");
        assertEquals(reverse("abcde", -10, 30), "edcba");
    }

    private static void testReverseString() {
        assertEquals(reverse("hello"), "olleh");
        assertEquals(reverse(""), "");
        assertEquals(reverse(" "), " ");
        assertEquals(reverse(null), null);
        assertEquals(reverse("abc"), "cba");
    }

    private static void assertEquals(String a, String b) {
        if (a == null && b == null) {
            return;
        }
        if (a != null && a.equals(b) == false) {
            throw new RuntimeException("Assert failed: Non equal: a = " + a + "; b = " + b);
        }
        if (a != null && a.equals(b) == true) {
            return;
        }
        throw new RuntimeException("Assert failed: Non equal (a == null, b != null)");
    }

    private static void assertTrue(boolean val) {
        if (!val) {
            throw new RuntimeException("value is true");
        }
    }

    private static void assertFalse(boolean val) {
        if (val) {
            throw new RuntimeException("value is true");
        }
    }
}
