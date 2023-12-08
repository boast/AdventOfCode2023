package util;

public class MathUtil {
    private MathUtil() {
        // Utility
    }
    
    public static long gcd(final long a, final long b) {
        return b == 0 ? a : gcd(b, a % b);
    }
    
    public static long lcm(final long a, final long b) {
        return (a * b) / gcd(a, b);
    }
}
