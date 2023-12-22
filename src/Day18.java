import util.Point;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

public class Day18 {
    
    public static void main(final String[] args) throws IOException {
        final var lines = Files.readAllLines(Path.of("resources/day18.txt"));
        
        System.out.println(part1(lines));
        System.out.println(part2(lines));
    }
    
    static long part1(final List<String> lines) {
        return getArea(lines, Instruction::fromPart1);
    }
    
    static long part2(final List<String> lines) {
        return getArea(lines, Instruction::fromPart2);
    }
    
    private static long getArea(final List<String> lines, final Function<String, Instruction> parser) {
        var shoelaces       = 0L;
        var boundary        = 0L;
        var currentPosition = new Point();
        
        for (final var line : lines) {
            final var instruction  = parser.apply(line);
            final var nextPosition = currentPosition.move(instruction.direction(), instruction.distance());
            
            // See https://en.wikipedia.org/wiki/Shoelace_formula
            shoelaces += (currentPosition.y() + nextPosition.y()) * (currentPosition.x() - nextPosition.x());
            boundary += instruction.distance();
            currentPosition = nextPosition;
        }
        
        // See https://en.wikipedia.org/wiki/Pick%27s_theorem
        // A = shoelaces / 2 (Shoelace formula)
        // A = I + B/2 - 1 (Pick's theorem)
        // -> I = A + 1 - B/2
        // Total = I + B
        // Total = (A + 1 - B/2) + B
        // Total = A + 1 + B/2
        // Total = shoelaces / 2 + 1 + B/2
        return shoelaces / 2 + 1 + boundary / 2;
    }
    
    private record Instruction(Point.Direction direction, int distance) {
        public static Instruction fromPart1(final String line) {
            final var parts = line.split(" ");
            return new Instruction(Point.Direction.from(parts[0]), Integer.parseInt(parts[1]));
        }
        
        public static Instruction fromPart2(final String line) {
            final var parts = line.split(" ");
            final var dir   = parts[2].charAt(parts[2].length() - 2);
            
            final var direction = switch (dir) {
                case '0' -> Point.Direction.E;
                case '1' -> Point.Direction.S;
                case '2' -> Point.Direction.W;
                case '3' -> Point.Direction.N;
                default -> throw new IllegalStateException("Unexpected value: " + dir);
            };
            final var distance = Integer.parseInt(parts[2].substring(2, parts[2].length() - 2), 16);
            
            return new Instruction(direction, distance);
        }
    }
}
