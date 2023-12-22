import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

public class Day15 {
    
    public static void main(final String[] args) throws IOException {
        final var lines = Files.readAllLines(Path.of("resources/day15.txt"));
        
        System.out.println(part1(lines.getFirst()));
        System.out.println(part2(lines.getFirst()));
    }
    
    static long part1(final String line) {
        return Arrays.stream(line.split(",")).map(Day15::hash).reduce(0, Integer::sum);
    }
    
    static long part2(final String line) {
        final var boxes = new ArrayList<LinkedHashMap<String, Integer>>(256);
        for (int i = 0; i < 256; i++) {
            boxes.add(new LinkedHashMap<>());
        }
        
        for (final var task : line.split(",")) {
            final var parts = task.split("[-=]");
            final var label = parts[0];
            final var box   = boxes.get(hash(label));
            
            if (parts.length == 1) {
                box.remove(label);
                continue;
            }
            
            box.put(label, Integer.parseInt(parts[1]));
        }
        
        var sum = 0;
        for (var box = 0; box < boxes.size(); box++) {
            var slot = 1;
            for (final var focalLength : boxes.get(box).values()) {
                sum += (box + 1) * slot * focalLength;
                slot++;
            }
        }
        return sum;
    }
    
    private static int hash(final String value) {
        return value.chars().reduce(0, (a, b) -> ((a + b) * 17) % 256);
    }
}
