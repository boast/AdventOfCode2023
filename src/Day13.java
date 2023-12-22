import util.StringUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Day13 {
    
    public static void main(final String[] args) throws IOException {
        final var lines = Files.readAllLines(Path.of("resources/day13.txt"));
        
        System.out.println(part1(lines));
        System.out.println(part2(lines));
    }
    
    static long part1(final List<String> lines) {
        return parsePatterns(lines).stream()
                                   .map(pattern -> findSimilar(pattern.rows, 0) * 100 + findSimilar(pattern.cols, 0))
                                   .reduce(0, Integer::sum);
    }
    
    static long part2(final List<String> lines) {
        return parsePatterns(lines).stream()
                                   .map(pattern -> findSimilar(pattern.rows, 1) * 100 + findSimilar(pattern.cols, 1))
                                   .reduce(0, Integer::sum);
    }
    
    private static int findSimilar(final List<String> lines, final int difference) {
        for (var i = 1; i < lines.size(); i++) {
            if (isSimilar(lines, i, difference)) {
                return i;
            }
        }
        return 0;
    }
    
    // Checks if each pair of lines an equal distance from the mirror point are similar, similar meaning they differ in
    // their characters by exactly the requiredDifference parameter.
    private static boolean isSimilar(final List<String> lines, final int mirrorPoint, final int requiredDifference) {
        final var limit      = Math.min(mirrorPoint, lines.size() - mirrorPoint); // Prevents out of bounds
        var       difference = 0;
        
        for (var i = 0; i < limit && difference <= requiredDifference; i++) {
            final var before = lines.get(mirrorPoint - i - 1);
            final var after  = lines.get(mirrorPoint + i);
            
            difference += StringUtil.difference(before, after);
        }
        
        return difference == requiredDifference;
    }
    
    
    static ArrayList<Pattern> parsePatterns(final List<String> lines) {
        final var patterns = new ArrayList<Pattern>();
        
        var rows = new ArrayList<String>();
        for (final var line : lines) {
            if (line.isBlank()) {
                patterns.add(Pattern.fromRows(rows));
                rows = new ArrayList<>();
                continue;
            }
            rows.add(line);
        }
        patterns.add(Pattern.fromRows(rows));
        
        return patterns;
    }
    
    record Pattern(List<String> rows, List<String> cols) {
        static Pattern fromRows(final List<String> rows) {
            final var cols = new ArrayList<String>();
            for (var i = 0; i < rows.getFirst().length(); i++) {
                final var col = new StringBuilder();
                for (final var from : rows) {
                    col.append(from.charAt(i));
                }
                cols.add(col.toString());
            }
            return new Pattern(rows, cols);
        }
        
    }
}
