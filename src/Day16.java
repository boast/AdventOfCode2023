import util.Point;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Day16 {
    
    public static void main(final String[] args) throws IOException {
        final var lines = Files.readAllLines(Paths.get("resources/day16.txt"));
        
        System.out.println(part1(lines));
        System.out.println(part2(lines));
    }
    
    static long part1(final List<String> lines) {
        final var map  = parseMap(lines);
        final var maxX = lines.getFirst().length();
        final var maxY = lines.size();
        
        return countRays(new Beam(new Point(0, 0), Point.Direction.E), map, maxX, maxY);
    }
    
    private static long countRays(final Beam start, final HashMap<Point, Tile> map, final int maxX, final int maxY) {
        final var beamsSeen = new HashSet<Beam>();
        final var beams     = new ArrayDeque<Beam>();
        beams.add(start);
        
        Beam beam;
        while ((beam = beams.poll()) != null) {
            // Out of bounds or seen
            if (beam.point().x() < 0 ||
                beam.point().x() >= maxX ||
                beam.point().y() < 0 ||
                beam.point().y() >= maxY ||
                !beamsSeen.add(beam)) {
                continue;
            }
            
            switch (map.get(beam.point())) {
                case EMPTY -> beams.push(new Beam(beam.point().move(beam.direction()), beam.direction()));
                case MIRROR_LEFT -> {
                    switch (beam.direction()) {
                        case N -> beams.push(new Beam(beam.point().move(Point.Direction.E), Point.Direction.E));
                        case E -> beams.push(new Beam(beam.point().move(Point.Direction.N), Point.Direction.N));
                        case S -> beams.push(new Beam(beam.point().move(Point.Direction.W), Point.Direction.W));
                        case W -> beams.push(new Beam(beam.point().move(Point.Direction.S), Point.Direction.S));
                    }
                }
                case MIRROR_RIGHT -> {
                    switch (beam.direction()) {
                        case N -> beams.push(new Beam(beam.point().move(Point.Direction.W), Point.Direction.W));
                        case E -> beams.push(new Beam(beam.point().move(Point.Direction.S), Point.Direction.S));
                        case S -> beams.push(new Beam(beam.point().move(Point.Direction.E), Point.Direction.E));
                        case W -> beams.push(new Beam(beam.point().move(Point.Direction.N), Point.Direction.N));
                    }
                }
                case SPLIT_HORIZONTAL -> {
                    switch (beam.direction()) {
                        case E, W -> beams.push(new Beam(beam.point().move(beam.direction()), beam.direction()));
                        case N, S -> {
                            beams.push(new Beam(beam.point().move(Point.Direction.E), Point.Direction.E));
                            beams.push(new Beam(beam.point().move(Point.Direction.W), Point.Direction.W));
                        }
                    }
                }
                case SPLIT_VERTICAL -> {
                    switch (beam.direction()) {
                        case N, S -> beams.push(new Beam(beam.point().move(beam.direction()), beam.direction()));
                        case E, W -> {
                            beams.push(new Beam(beam.point().move(Point.Direction.N), Point.Direction.N));
                            beams.push(new Beam(beam.point().move(Point.Direction.S), Point.Direction.S));
                        }
                    }
                }
            }
        }
        
        return beamsSeen.stream().map(Beam::point).distinct().count();
    }
    
    static long part2(final List<String> lines) {
        final var map  = parseMap(lines);
        final var maxX = lines.getFirst().length();
        final var maxY = lines.size();
        
        var max = 0L;
        
        // Left and right
        for (var y = 0; y < maxY; y++) {
            max = Math.max(max, countRays(new Beam(new Point(0, y), Point.Direction.E), map, maxX, maxY));
            max = Math.max(max, countRays(new Beam(new Point(maxX - 1, y), Point.Direction.W), map, maxX, maxY));
        }
        // Top and bottom
        for (var x = 0; x < maxX; x++) {
            max = Math.max(max, countRays(new Beam(new Point(x, 0), Point.Direction.S), map, maxX, maxY));
            max = Math.max(max, countRays(new Beam(new Point(x, maxY - 1), Point.Direction.N), map, maxX, maxY));
        }
        
        return max;
    }
    
    private static HashMap<Point, Tile> parseMap(final List<String> lines) {
        final var map = new HashMap<Point, Tile>();
        for (var y = 0; y < lines.size(); y++) {
            final var line = lines.get(y);
            for (var x = 0; x < line.length(); x++) {
                map.put(new Point(x, y), Tile.parse(line.charAt(x)));
            }
        }
        return map;
    }
    
    private enum Tile {
        EMPTY,
        MIRROR_LEFT,
        MIRROR_RIGHT,
        SPLIT_HORIZONTAL,
        SPLIT_VERTICAL,
        ;
        
        public static Tile parse(final char c) {
            return switch (c) {
                case '.' -> EMPTY;
                case '/' -> MIRROR_LEFT;
                case '\\' -> MIRROR_RIGHT;
                case '-' -> SPLIT_HORIZONTAL;
                case '|' -> SPLIT_VERTICAL;
                default -> throw new IllegalArgumentException("Unknown tile: " + c);
            };
        }
    }
    
    // We could also call this "vector"
    private record Beam(Point point, Point.Direction direction) {
    }
}
