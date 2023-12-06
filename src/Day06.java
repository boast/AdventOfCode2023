import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class Day06 {
    public static void main(final String[] args) throws IOException {
        final var lines = Files.readAllLines(Paths.get("resources/day06.txt"));
        
        System.out.println(part1(lines));
        System.out.println(part2(lines));
    }
    
    static long part1(final List<String> lines) {
        final var times     = splitAndSkipFirst(lines.get(0)).mapToLong(Long::parseLong).toArray();
        final var distances = splitAndSkipFirst(lines.get(1)).mapToLong(Long::parseLong).toArray();
        
        var totalPossibilities = 1L;
        
        for (int i = 0; i < times.length; i++) {
            var possibilities = 0L;
            for (long t = 1; t < times[i]; t++) {
                final var distance = t * (times[i] - t);
                
                if (distance > distances[i]) {
                    possibilities++;
                }
            }
            totalPossibilities *= possibilities;
        }
        
        return totalPossibilities;
    }
    
    static long part2(final List<String> lines) {
        final var time     = Long.parseLong(splitAndSkipFirst(lines.get(0)).reduce((a, b) -> a + b).orElseThrow());
        final var distance = Long.parseLong(splitAndSkipFirst(lines.get(1)).reduce((a, b) -> a + b).orElseThrow());
        
        var totalPossibilities = 0L;
        
        for (long t = 1; t < time; t++) {
            final var currentDistance = t * (time - t);
            
            if (currentDistance > distance) {
                totalPossibilities++;
            }
        }
        
        return totalPossibilities;
    }
    
    private static Stream<String> splitAndSkipFirst(final String line) {
        return Arrays.stream(line.split("\\s+")).skip(1);
    }
}
