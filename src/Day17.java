import util.Point;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Day17 {
    
    public static void main(final String[] args) throws IOException {
        final var lines = Files.readAllLines(Paths.get("resources/day17.txt"));
        
        System.out.println(part1(lines));
        System.out.println(part2(lines));
    }
    
    static long part1(final List<String> lines) {
        return getHeatLoss(lines, 1, 3);
    }
    
    static long part2(final List<String> lines) {
        return getHeatLoss(lines, 4, 10);
    }
    
    
    private static long getHeatLoss(final List<String> lines, final int minStep, final int maxStep) {
        final var map = parseMap(lines);
        
        final var queue  = new PriorityQueue<Node>();
        final var seen   = new HashSet<Step>();
        final var target = new Point(lines.getFirst().length() - 1, lines.size() - 1);
        queue.add(new Node(new Step(new Point(0, 0), null), 0));
        
        while (!queue.isEmpty()) {
            final var node = queue.poll();
            final var step = node.step();
            
            if (step.point().equals(target)) {
                return node.cost();
            }
            if (!seen.add(step)) {
                continue;
            }
            
            for (final Point.Direction direction : Point.Direction.values()) {
                if (step.direction() == direction || step.direction() == direction.opposite()) {
                    continue;
                }
                
                var newPoint = step.point();
                var newCost  = node.cost();
                for (int i = 1; i <= maxStep; i++) {
                    newPoint = newPoint.move(direction);
                    if (!map.containsKey(newPoint)) {
                        break;
                    }
                    newCost += map.get(newPoint);
                    
                    if (i >= minStep) {
                        queue.add(new Node(new Step(newPoint, direction), newCost));
                    }
                }
            }
        }
        
        throw new IllegalStateException("No path found");
    }
    
    private static Map<Point, Integer> parseMap(final List<String> lines) {
        final var map = new HashMap<Point, Integer>();
        
        for (var y = 0; y < lines.size(); y++) {
            final var line = lines.get(y);
            for (var x = 0; x < line.length(); x++) {
                map.put(new Point(x, y), Integer.parseInt(line.substring(x, x + 1)));
            }
        }
        
        return map;
    }
    
    private record Step(Point point, Point.Direction direction) {
    }
    
    private record Node(Step step, int cost) implements Comparable<Node> {
        @Override
        public int compareTo(final Node other) {
            return Integer.compare(this.cost(), other.cost());
        }
    }
}
