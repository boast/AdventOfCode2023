import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Day05 {
    public static void main(final String[] args) throws IOException {
        final var lines = Files.readAllLines(Paths.get("resources/day05.txt"));
        
        System.out.println(part1(lines));
        System.out.println(part2(lines));
    }
    
    static long part1(final List<String> lines) {
        final var seeds   = Arrays.stream(lines.getFirst().split(": ")[1].split(" ")).map(Long::parseLong).toList();
        final var almanac = parseAlmanac(lines);
        
        return seeds.stream().map((seed) -> {
            var currentValue = seed;
            for (final var listMap : almanac) {
                for (final var map : listMap) {
                    if (currentValue >= map.src() && currentValue < map.end()) {
                        currentValue += map.offset();
                        break;
                    }
                }
            }
            
            return currentValue;
        }).min(Long::compare).orElseThrow();
    }
    
    static long part2(final List<String> lines) {
        final var seeds  = Arrays.stream(lines.getFirst().split(": ")[1].split(" ")).map(Long::parseLong).toList();
        final var ranges = new ArrayList<Range>();
        
        for (int i = 0; i < seeds.size(); i += 2) {
            ranges.add(new Range(seeds.get(i), seeds.get(i) + seeds.get(i + 1)));
        }
        
        final var almanac = parseAlmanac(lines);
        var       min     = Long.MAX_VALUE;
        
        for (final var initialRange : ranges) {
            var currentRanges = new ArrayList<Range>();
            currentRanges.add(initialRange);
            for (final var listMap : almanac) {
                // We must not split up an offset range, so we need to keep track of them separately
                final var offsetRanges = new ArrayList<Range>();
                
                for (final var map : listMap) {
                    final var newRanges = new ArrayList<Range>();
                    
                    for (final var range : currentRanges) {
                        // If the range is completely before or after the map, we can just add it to the next iteration
                        if (range.start() >= map.end() || range.end() <= map.src()) {
                            newRanges.add(range);
                            continue;
                        }
                        
                        // Range starts before the map, so we split away the part before the map
                        if (range.start() < map.src()) {
                            newRanges.add(new Range(range.start(), map.src()));
                        }
                        
                        // Range is inside the map -> offset it
                        final var innerStart = Math.max(range.start(), map.src());
                        final var innerEnd   = Math.min(range.end(), map.end());
                        if (innerStart < innerEnd) {
                            offsetRanges.add(new Range(innerStart + map.offset(), innerEnd + map.offset()));
                        }
                        
                        // Range ends after the map, so we split away the part after the map
                        if (map.end() < range.end()) {
                            newRanges.add(new Range(map.end(), range.end()));
                        }
                    }
                    
                    currentRanges = new ArrayList<>(newRanges);
                }
                currentRanges.addAll(offsetRanges);
            }
            
            final var currentMin = currentRanges.stream().mapToLong(Range::start).min().orElseThrow();
            min = Math.min(min, currentMin);
        }
        
        return min;
    }
    
    private static ArrayList<ArrayList<AlmanacMap>> parseAlmanac(final List<String> lines) {
        final var almanac        = new ArrayList<ArrayList<AlmanacMap>>();
        var       currentListMap = new ArrayList<AlmanacMap>();
        almanac.add(currentListMap);
        
        for (int i = 3; i < lines.size(); i++) {
            if (lines.get(i).isBlank()) {
                currentListMap = new ArrayList<>();
                almanac.add(currentListMap);
                i++; // Skip the next "abc-to-xyz:" line
                continue;
            }
            final var parts = Arrays.stream(lines.get(i).split(" ")).mapToLong(Long::parseLong).toArray();
            currentListMap.add(new AlmanacMap(parts[0], parts[1], parts[2]));
        }
        
        return almanac;
    }
    
    private record AlmanacMap(long dst, long src, long range) {
        long end() {
            return src + range;
        }
        
        long offset() {
            return dst - src;
        }
    }
    
    private record Range(long start, long end) {
    }
}
