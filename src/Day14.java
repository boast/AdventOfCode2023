import util.Point;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day14 {
    
    public static void main(final String[] args) throws IOException {
        final var lines = Files.readAllLines(Paths.get("resources/day14.txt"));
        
        System.out.println(part1(lines));
        System.out.println(part2(lines));
    }
    
    static long part1(final List<String> lines) {
        final var map  = parseMap(lines);
        final var xMax = lines.getFirst().length();
        final var yMax = lines.size();
        
        // Tilt up
        for (var y = 1; y < yMax; y++) {
            for (var x = 0; x < xMax; x++) {
                final var tile = map.get(new Point(x, y));
                if (tile == Tile.ROUND_ROCK) {
                    var currentY = y;
                    while (currentY > 0 && map.get(new Point(x, currentY - 1)) == Tile.EMPTY) {
                        map.put(new Point(x, currentY), Tile.EMPTY);
                        map.put(new Point(x, currentY - 1), Tile.ROUND_ROCK);
                        currentY--;
                    }
                }
            }
        }
        
        return calculateLoad(yMax, map);
    }
    
    static long part2(final List<String> lines) {
        final var map  = parseMap(lines);
        final var xMax = lines.getFirst().length();
        final var yMax = lines.size();
        final var seen = new ArrayList<Map<Point, Tile>>();
        
        int index;
        for (var i = 0L; i < 1000000000; i++) {
            cycle(map, xMax, yMax);
            if ((index = seen.indexOf(map)) != -1) {
                // Add the remaining cycles to the index
                final var remaining       = 1000000000 - i;
                final var cycleLength     = i - index;
                final var remainingCycles = remaining / cycleLength;
                i += remainingCycles * cycleLength;
                continue;
            }
            seen.add(new HashMap<>(map));
        }
        
        return calculateLoad(yMax, map);
    }
    
    private static long calculateLoad(final int yMax, final Map<Point, Tile> map) {
        return map.entrySet()
                  .stream()
                  .filter(entry -> entry.getValue() == Tile.ROUND_ROCK)
                  .mapToLong(entry -> yMax - entry.getKey().y())
                  .sum();
    }
    
    private static void cycle(final Map<Point, Tile> map, final int xMax, final int yMax) {
        // Tilt up
        for (var y = 1; y < yMax; y++) {
            for (var x = 0; x < xMax; x++) {
                if (map.get(new Point(x, y)) == Tile.ROUND_ROCK) {
                    var currentY = y;
                    while (currentY > 0 && map.get(new Point(x, currentY - 1)) == Tile.EMPTY) {
                        map.put(new Point(x, currentY), Tile.EMPTY);
                        map.put(new Point(x, currentY - 1), Tile.ROUND_ROCK);
                        currentY--;
                    }
                }
            }
        }
        
        // Tilt left
        for (var x = 1; x < xMax; x++) {
            for (var y = 0; y < yMax; y++) {
                if (map.get(new Point(x, y)) == Tile.ROUND_ROCK) {
                    var currentX = x;
                    while (currentX > 0 && map.get(new Point(currentX - 1, y)) == Tile.EMPTY) {
                        map.put(new Point(currentX, y), Tile.EMPTY);
                        map.put(new Point(currentX - 1, y), Tile.ROUND_ROCK);
                        currentX--;
                    }
                }
            }
        }
        
        // Tilt down
        for (var y = yMax - 2; y >= 0; y--) {
            for (var x = 0; x < xMax; x++) {
                if (map.get(new Point(x, y)) == Tile.ROUND_ROCK) {
                    var currentY = y;
                    while (currentY < yMax - 1 && map.get(new Point(x, currentY + 1)) == Tile.EMPTY) {
                        map.put(new Point(x, currentY), Tile.EMPTY);
                        map.put(new Point(x, currentY + 1), Tile.ROUND_ROCK);
                        currentY++;
                    }
                }
            }
        }
        
        // Tilt right
        for (var x = xMax - 2; x >= 0; x--) {
            for (var y = 0; y < yMax; y++) {
                if (map.get(new Point(x, y)) == Tile.ROUND_ROCK) {
                    var currentX = x;
                    while (currentX < xMax - 1 && map.get(new Point(currentX + 1, y)) == Tile.EMPTY) {
                        map.put(new Point(currentX, y), Tile.EMPTY);
                        map.put(new Point(currentX + 1, y), Tile.ROUND_ROCK);
                        currentX++;
                    }
                }
            }
        }
    }
    
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
        ROUND_ROCK,
        ROCK,
        EMPTY,
        ;
        
        public static Tile from(final char c) {
            return switch (c) {
                case '.' -> EMPTY;
                case '#' -> ROCK;
                case 'O' -> ROUND_ROCK;
                default -> throw new IllegalArgumentException("Invalid tile type: " + c);
            };
        }
    }
}
