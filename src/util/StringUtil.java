package util;

public class StringUtil {
    private StringUtil() {
        // Utility
    }
    
    /**
     * Returns the number of characters that differ between the two strings. If the strings are of different lengths,
     * the difference is the length-difference plus the number of characters that differ in the shorter string.
     *
     * @param a The first string
     * @param b The second string
     * @return The number of characters that differ between the two strings
     */
    public static int difference(final String a, final String b) {
        final var minLength = Math.min(a.length(), b.length());
        
        var difference = Math.abs(a.length() - b.length());
        for (var i = 0; i < minLength; i++) {
            difference += a.charAt(i) != b.charAt(i) ? 1 : 0;
        }
        
        return difference;
    }
}
