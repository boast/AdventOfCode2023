import util.Point;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Day23 {
    
    public static void main(final String[] args) throws IOException {
        final var lines = Files.readAllLines(Path.of("resources/day23.txt"));
        
        System.out.println(part1(lines));
        System.out.println(part2(lines));
    }
    
    static long part1(final List<String> lines) {
        final var map    = parseMap(lines);
        final var start  = new Point(1, 0);
        final var target = new Point(lines.size() - 2, lines.size() - 1);
        
        final var edgeMap = getEdgeMap(map, true);
        
        return getLongestPath(edgeMap, start, target);
    }
    
    static long part2(final List<String> lines) {
        final var map    = parseMap(lines);
        final var start  = new Point(1, 0);
        final var target = new Point(lines.size() - 2, lines.size() - 1);
        
        final var edgeMap = getEdgeMap(map, false);
        collapseEdgeMap(edgeMap);
        
        return getLongestPath(edgeMap, start, target);
    }
    
    private static int getLongestPath(final Map<Point, Set<Edge>> edgeMap, final Point start, final Point target) {
        final var queue = new ArrayDeque<Edge>();
        queue.add(new Edge(start, 0));
        final var visited = new HashSet<Point>();
        var       longest = 0;
        
        while (!queue.isEmpty()) {
            final var edge = queue.pop();
            if (edge.distance() == -1) {
                visited.remove(edge.point());
                continue;
            }
            
            if (edge.point().equals(target)) {
                longest = Math.max(longest, edge.distance());
                continue;
            }
            
            if (!visited.add(edge.point())) {
                continue;
            }
            
            // Backtrack
            queue.push(new Edge(edge.point(), -1));
            for (final var nextEdge : edgeMap.get(edge.point())) {
                queue.push(new Edge(nextEdge.point(), edge.distance() + nextEdge.distance()));
            }
        }
        
        return longest;
    }
    
    private static void collapseEdgeMap(final Map<Point, Set<Edge>> edgeMap) {
        while (true) {
            var didCollapse = false;
            for (final var edge : edgeMap.entrySet()) {
                final var point = edge.getKey();
                final var edges = edge.getValue();
                
                if (edges.size() == 2) {
                    final var iterator = edges.iterator();
                    final var edgeA    = iterator.next();
                    final var edgeB    = iterator.next();
                    final var distance = edgeA.distance() + edgeB.distance();
                    
                    edgeMap.get(edgeA.point()).remove(new Edge(point, edgeA.distance()));
                    edgeMap.get(edgeA.point()).add(new Edge(edgeB.point(), distance));
                    
                    edgeMap.get(edgeB.point()).add(new Edge(edgeA.point(), distance));
                    edgeMap.get(edgeB.point()).remove(new Edge(point, edgeB.distance()));
                    
                    edgeMap.remove(point);
                    didCollapse = true;
                    break;
                }
            }
            if (!didCollapse) {
                break;
            }
        }
    }
    
    private static Map<Point, Set<Edge>> getEdgeMap(final Map<Point, Tile> map, final boolean handleSlopes) {
        final var edgeMap = new HashMap<Point, Set<Edge>>();
        
        for (final var entry : map.entrySet()) {
            final var point = entry.getKey();
            final var tile  = entry.getValue();
            
            if (tile == Tile.FOREST || handleSlopes && tile.isSlope()) {
                continue;
            }
            
            for (final var direction : new Point.Direction[]{Point.Direction.E, Point.Direction.S, Point.Direction.W, Point.Direction.N}) {
                final var nextPoint = point.move(direction);
                final var nextTile  = map.getOrDefault(nextPoint, Tile.FOREST);
                
                if (nextTile == Tile.FOREST) {
                    continue;
                }
                
                if (nextTile.isSlope() && handleSlopes) {
                    edgeMap.computeIfAbsent(point, p -> new HashSet<>())
                           .add(new Edge(nextPoint.move(nextTile.moveSlope()), 2));
                    continue;
                }
                edgeMap.computeIfAbsent(point, p -> new HashSet<>()).add(new Edge(nextPoint, 1));
                edgeMap.computeIfAbsent(nextPoint, p -> new HashSet<>()).add(new Edge(point, 1));
            }
        }
        
        return edgeMap;
    }
    
    private static Map<Point, Tile> parseMap(final List<String> lines) {
        final var map = new HashMap<Point, Tile>();
        
        for (int y = 0; y < lines.size(); y++) {
            final var line = lines.get(y);
            for (int x = 0; x < line.length(); x++) {
                map.put(new Point(x, y), Tile.from(line.charAt(x)));
            }
        }
        
        return map;
    }
    
    private enum Tile {
        PATH,
        FOREST,
        SLOPE_DOWN,
        SLOPE_RIGHT,
        ;
        
        public static Tile from(final char c) {
            return switch (c) {
                case '.' -> PATH;
                case '#' -> FOREST;
                case 'v' -> SLOPE_DOWN;
                case '>' -> SLOPE_RIGHT;
                default -> throw new IllegalArgumentException("Unknown tile: %c".formatted(c));
            };
        }
        
        public boolean isSlope() {
            return this == SLOPE_DOWN || this == SLOPE_RIGHT;
        }
        
        public Point.Direction moveSlope() {
            return switch (this) {
                case SLOPE_DOWN -> Point.Direction.S;
                case SLOPE_RIGHT -> Point.Direction.E;
                default -> throw new IllegalArgumentException("Unknown slope: %s".formatted(this));
            };
        }
    }
    
    private record Edge(Point point, int distance) {
    }
}