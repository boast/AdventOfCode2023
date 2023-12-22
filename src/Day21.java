import util.Point;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class Day21 {
    
    public static void main(final String[] args) throws IOException {
        final var lines = Files.readAllLines(Path.of("resources/day21.txt"));
        
        System.out.println(part1(lines));
        System.out.println(part2(lines));
    }
    
    static long part1(final List<String> lines) {
        final var xMax  = lines.getFirst().length();
        final var yMax  = lines.size();
        final var map   = parseMap(lines);
        final var rocks = getRocks(map);
        
        var steps = getStartStepAsSet(map);
        
        for (int i = 1; i <= 64; i++) {
            steps = getNextSteps(steps, rocks, xMax, yMax);
        }
        
        return steps.size();
    }
    
    static long part2(final List<String> lines) {
        // Note: in our input, xMax and yMax are equal! So it is a square.
        // We also notice, that from our start point onwards in all directions,
        // the map has no rock in it. This means, we can measure the growth
        // which must be quadratic.
        // To solve the quadratic growth, we measure the growth 3 times, each
        // when the step counter is at target % xMax (plus xMax).
        final var xMax  = lines.getFirst().length();
        final var yMax  = lines.size();
        final var map   = parseMap(lines);
        final var rocks = getRocks(map);
        
        final var target = 26501365;
        final var offset = target % xMax;
        final var sizes  = new ArrayList<Integer>();
        var       steps  = getStartStepAsSet(map);
        var       i      = 1;
        
        while (sizes.size() < 3) {
            steps = getNextSteps(steps, rocks, xMax, yMax);
            
            if (i % xMax == offset) {
                sizes.add(steps.size());
            }
            i++;
        }
        
        return solveQuadratic(target / xMax, sizes.get(0), sizes.get(1), sizes.get(2));
    }
    
    private static Set<Point> getRocks(final Map<Point, Tile> map) {
        return map.entrySet()
                  .stream()
                  .filter(entry -> entry.getValue() == Tile.ROCK)
                  .map(Map.Entry::getKey)
                  .collect(Collectors.toSet());
    }
    
    private static long solveQuadratic(final long n, final int a, final int b, final int c) {
        return a + (b - a) * n + ((c - b) - (b - a)) * (n * (n - 1) / 2);
    }
    
    private static Set<Point> getNextSteps(
            final Set<Point> steps, final Set<Point> rocks, final int xMax, final int yMax
    ) {
        final var nextSteps = new HashSet<Point>();
        
        for (final var step : steps) {
            for (final var neighbor : step.adjacent()) {
                final var xMod = neighbor.x() % xMax;
                final var yMod = neighbor.y() % yMax;
                final var x    = xMod < 0 ? xMod + xMax : xMod;
                final var y    = yMod < 0 ? yMod + yMax : yMod;
                if (!rocks.contains(new Point(x, y))) {
                    nextSteps.add(neighbor);
                }
            }
        }
        
        return nextSteps;
    }
    
    private static Set<Point> getStartStepAsSet(final Map<Point, Tile> map) {
        return map.entrySet()
                  .stream()
                  .filter(entry -> entry.getValue() == Tile.STEP)
                  .map(Map.Entry::getKey)
                  .collect(Collectors.toSet());
    }
    
    @SuppressWarnings("DuplicatedCode")
    private static Map<Point, Tile> parseMap(final List<String> lines) {
        final var map = new HashMap<Point, Tile>();
        
        for (var y = 0; y < lines.size(); y++) {
            final var line = lines.get(y);
            for (var x = 0; x < line.length(); x++) {
                map.put(new Point(x, y), Tile.from(line.charAt(x)));
            }
        }
        
        return map;
    }
    
    private enum Tile {
        STEP,
        GARDEN,
        ROCK,
        ;
        
        public static Tile from(final char c) {
            return switch (c) {
                case '.' -> GARDEN;
                case '#' -> ROCK;
                case 'S' -> STEP;
                default -> throw new IllegalArgumentException("Unknown tile: " + c);
            };
        }
    }
}