import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class Day09 {
    public static void main(final String[] args) throws IOException {
        final var lines = Files.readAllLines(Path.of("resources/day09.txt"));
        
        System.out.println(part1(lines));
        System.out.println(part2(lines));
    }
    
    static long part1(final List<String> lines) {
        final var measurements = parseMeasurements(lines);
        var       total        = 0L;
        
        for (final var measurement : measurements) {
            final var lastElements = differencesUntilZeroes(measurement).stream().map(List::getLast);
            total += lastElements.reduce(0L, Long::sum);
        }
        
        return total;
    }
    
    static long part2(final List<String> lines) {
        final var measurements = parseMeasurements(lines);
        var       total        = 0L;
        
        for (final var measurement : measurements) {
            final var firstElements = differencesUntilZeroes(measurement).stream().map(List::getFirst);
            total += firstElements.reduce(0L, (a, b) -> b - a);
        }
        
        return total;
    }
    
    private static List<List<Long>> parseMeasurements(final List<String> lines) {
        return lines.stream()
                    .map(line -> line.split(" "))
                    .map(parts -> Arrays.stream(parts).map(Long::parseLong).toList())
                    .toList();
    }
    
    private static ArrayList<List<Long>> differencesUntilZeroes(final List<Long> measurement) {
        final var differenceList = new ArrayList<List<Long>>();
        differenceList.add(measurement);
        
        while (differenceList.getFirst().stream().anyMatch(value -> value != 0L)) {
            differenceList.addFirst(getDifferences(differenceList.getFirst()));
        }
        
        return differenceList;
    }
    
    private static List<Long> getDifferences(final List<Long> measurement) {
        final var previous = new AtomicLong(measurement.getFirst());
        return measurement.stream().skip(1).map(value -> value - previous.getAndSet(value)).toList();
    }
}
