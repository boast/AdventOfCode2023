import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Day01 {
    static Map<String, String> digitsMap = Map.of(
            "one",
            "1",
            "two",
            "2",
            "three",
            "3",
            "four",
            "4",
            "five",
            "5",
            "six",
            "6",
            "seven",
            "7",
            "eight",
            "8",
            "nine",
            "9"
    );
    
    public static void main(final String[] args) throws IOException {
        final var lines = Files.readAllLines(Path.of("resources/day01.txt"));
        
        System.out.println(part1(lines));
        System.out.println(part2(lines));
    }
    
    static int part1(final List<String> lines) {
        final var regex        = "(\\d)";
        final var patternFirst = Pattern.compile(regex);
        final var patternLast  = Pattern.compile(".*" + regex);
        
        return lines.stream().map(line -> {
            final var matchesFirst = patternFirst.matcher(line).results().findFirst().orElseThrow().group(1);
            final var matchesLast  = patternLast.matcher(line).results().findFirst().orElseThrow().group(1);
            
            return matchesFirst + matchesLast;
        }).mapToInt(Integer::parseInt).sum();
    }
    
    static int part2(final List<String> lines) {
        final var regex        = "(\\d|%s)".formatted(digitsMap.keySet()
                                                               .stream()
                                                               .reduce((a, b) -> a + "|" + b)
                                                               .orElseThrow());
        final var patternFirst = Pattern.compile(regex);
        final var patternLast  = Pattern.compile(".*" + regex);
        
        return lines.stream().map(line -> {
            var matchesFirst = patternFirst.matcher(line).results().findFirst().orElseThrow().group(1);
            var matchesLast  = patternLast.matcher(line).results().findFirst().orElseThrow().group(1);
            
            if (digitsMap.containsKey(matchesFirst)) {
                matchesFirst = digitsMap.get(matchesFirst);
            }
            if (digitsMap.containsKey(matchesLast)) {
                matchesLast = digitsMap.get(matchesLast);
            }
            
            return matchesFirst + matchesLast;
        }).mapToInt(Integer::parseInt).sum();
    }
}