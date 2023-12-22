import util.Point;
import util.Point.Direction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Day10 {
    public static void main(final String[] args) throws IOException {
        final var lines = Files.readAllLines(Path.of("resources/day10.txt"));
        
        System.out.println(part1(lines));
        System.out.println(part2(lines));
    }
    
    static long part1(final List<String> lines) {
        final var map  = parseMap(lines);
        final var loop = getLoop(map);
        
        return loop.size() / 2;
    }
    
    static long part2(final List<String> lines) {
        final var map  = parseMap(lines);
        final var loop = getLoop(map);
        
        // Replace start with actual tile
        final var startOut = loop.getFirst().directionOf(loop.get(1)).orElseThrow();
        final var startIn  = loop.getFirst().directionOf(loop.getLast()).orElseThrow().opposite();
        final var startTile = Arrays.stream(Tile.values())
                                    .filter(tile -> tile.next(startIn) == startOut)
                                    .findFirst()
                                    .orElseThrow();
        map.put(loop.getFirst(), startTile);
        
        final var yMax        = lines.size();
        final var xMax        = lines.getFirst().length();
        var       insideCount = 0;
        
        for (var y = 0; y < yMax; y++) {
            var inside = false;
            for (var x = 0; x < xMax; x++) {
                final var point = new Point(x, y);
                
                if (!loop.contains(point)) {
                    insideCount += inside ? 1 : 0;
                    continue;
                }
                
                // As we go from top to bottom and left to right, we have to toggle "inside" when we either cross a
                // vertical line or a line hits an L or J tile (we could also use vertical, F and 7).
                switch (map.get(point)) {
                    case VERTICAL, N_TO_E, N_TO_W -> inside = !inside;
                }
            }
        }
        
        return insideCount;
    }
    
    private static ArrayList<Point> getLoop(final HashMap<Point, Tile> map) {
        final var loop  = new ArrayList<Point>();
        final var start = findStart(map);
        loop.add(start);
        var currentDirection = findFirstDirection(start, map);
        var currentPoint     = start.move(currentDirection);
        
        while (!loop.contains(currentPoint)) {
            loop.add(currentPoint);
            final var tile = map.get(currentPoint);
            currentDirection = tile.next(currentDirection);
            currentPoint = currentPoint.move(currentDirection);
        }
        
        return loop;
    }
    
    private static Direction findFirstDirection(final Point start, final HashMap<Point, Tile> map) {
        return Arrays.stream(Direction.values()).filter(direction -> {
            final var point = start.move(direction);
            if (!map.containsKey(point)) {
                return false;
            }
            final var nextDirection = map.get(point).next(direction);
            if (nextDirection == null) {
                return false;
            }
            final var nextDirectionReverse = map.get(point).next(nextDirection.opposite());
            return point.move(nextDirectionReverse).equals(start);
        }).findFirst().orElseThrow();
    }
    
    private static Point findStart(final HashMap<Point, Tile> map) {
        return map.entrySet()
                  .stream()
                  .filter(entry -> entry.getValue() == Tile.START)
                  .findFirst()
                  .orElseThrow()
                  .getKey();
    }
    
    private static HashMap<Point, Tile> parseMap(final List<String> lines) {
        final var map = new HashMap<Point, Tile>();
        for (var y = 0; y < lines.size(); y++) {
            final var line = lines.get(y);
            for (var x = 0; x < line.length(); x++) {
                map.put(new Point(x, y), Tile.fromSymbol(line.substring(x, x + 1)));
            }
        }
        return map;
    }
    
    enum Tile {
        EMPTY("."),
        VERTICAL("|"),
        HORIZONTAL("-"),
        N_TO_E("L"),
        N_TO_W("J"),
        S_TO_E("F"),
        S_TO_W("7"),
        START("S"),
        ;
        
        private final String symbol;
        
        Tile(final String symbol) {
            this.symbol = symbol;
        }
        
        public static Tile fromSymbol(final String symbol) {
            return Arrays.stream(values()).filter(tile -> tile.symbol.equals(symbol)).findFirst().orElseThrow();
        }
        
        public Direction next(final Direction direction) {
            return switch (this) {
                case EMPTY, START -> null;
                case VERTICAL -> direction == Direction.N || direction == Direction.S ? direction : null;
                case HORIZONTAL -> direction == Direction.W || direction == Direction.E ? direction : null;
                case N_TO_E -> direction == Direction.S ? Direction.E : direction == Direction.W ? Direction.N : null;
                case N_TO_W -> direction == Direction.S ? Direction.W : direction == Direction.E ? Direction.N : null;
                case S_TO_E -> direction == Direction.N ? Direction.E : direction == Direction.W ? Direction.S : null;
                case S_TO_W -> direction == Direction.N ? Direction.W : direction == Direction.E ? Direction.S : null;
            };
        }
    }
}
