import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Day02 {
    public static void main(String[] args) throws IOException {
        final var lines = Files.readAllLines(Paths.get("resources/day02.txt"));
        
        System.out.println(part1(lines));
        System.out.println(part2(lines));
    }
    
    static int part1(List<String> lines) {
        final var maxRed   = 12;
        final var maxGreen = 13;
        final var maxBlue  = 14;
        
        return lines.stream().mapToInt((line) -> {
            final var parts = line.split(": ");
            final var game  = Integer.parseInt(parts[0].split(" ")[1]);
            
            final var grabs = parts[1].split("; ");
            for (final var grab : grabs) {
                final var cubes = grab.split(", ");
                for (final var cube : cubes) {
                    final var cubeParts = cube.split(" ");
                    final var amount        = Integer.parseInt(cubeParts[0]);
                    final var color         = cubeParts[1];
                    
                    if (color.equals("red") && amount > maxRed ||
                        color.equals("green") && amount > maxGreen ||
                        color.equals("blue") && amount > maxBlue) {
                        return 0;
                    }
                }
            }
            
            return game;
        }).sum();
    }
    
    static int part2(List<String> lines) {
        return lines.stream().mapToInt((line) -> {
            final var parts    = line.split(": ");
            var       minRed   = 0;
            var       minGreen = 0;
            var       minBlue  = 0;
            
            final var grabs = parts[1].split("; ");
            for (final var grab : grabs) {
                final var cubes = grab.split(", ");
                for (final var cube : cubes) {
                    final var cubeParts = cube.split(" ");
                    final var amount        = Integer.parseInt(cubeParts[0]);
                    final var color         = cubeParts[1];
                    
                    switch (color) {
                        case "red" -> minRed = Math.max(minRed, amount);
                        case "green" -> minGreen = Math.max(minGreen, amount);
                        case "blue" -> minBlue = Math.max(minBlue, amount);
                    }
                }
            }
            
            return minRed * minBlue * minGreen;
        }).sum();
    }
}
