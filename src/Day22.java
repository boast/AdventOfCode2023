import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day22 {
    
    public static void main(final String[] args) throws IOException {
        final var lines = Files.readAllLines(Path.of("resources/day22.txt"));
        
        System.out.println(part1(lines));
        System.out.println(part2(lines));
    }
    
    static long part1(final List<String> lines) {
        final var bricks = getBricks(lines);
        
        final var compactBricks = fall(bricks);
        final var allCollisions = getAllCollisions(compactBricks);
        
        return allCollisions.filter(c -> c == 0).count();
    }
    
    static long part2(final List<String> lines) {
        final var bricks = getBricks(lines);
        
        final var compactBricks = fall(bricks);
        final var allCollisions = getAllCollisions(compactBricks);
        
        return allCollisions.reduce(0, Integer::sum);
    }
    
    private static ArrayList<Set<Point3D>> getBricks(final List<String> lines) {
        final var bricks = new ArrayList<>(lines.stream().map(Brick::fromString).map(Brick::toSet).toList());
        bricks.sort(Comparator.comparingInt(set -> set.stream().mapToInt(Point3D::z).min().orElseThrow()));
        return bricks;
    }
    
    private static Stream<Integer> getAllCollisions(final Step compactBricks) {
        return compactBricks.bricks().stream().map(set -> {
            final var newBricks = compactBricks.bricks().stream().filter(s -> s != set).collect(Collectors.toList());
            return fall(newBricks).collisions();
        });
    }
    
    private static Step fall(final List<Set<Point3D>> bricks) {
        final var newBricks    = new ArrayList<Set<Point3D>>();
        final var fallenBricks = new HashSet<Point3D>();
        var       collisions   = 0;
        
        for (final var brick : bricks) {
            var currentBrick = brick;
            
            while (true) {
                final var fallenBrick = currentBrick.stream().map(Point3D::moveDown).collect(Collectors.toSet());
                
                // Stop falling this brick if it collides with another brick (now in fallen bricks) or the ground
                if (fallenBrick.stream().anyMatch(b -> fallenBricks.contains(b) || b.z() < 1)) {
                    newBricks.add(currentBrick);
                    fallenBricks.addAll(currentBrick);
                    if (currentBrick != brick) {
                        collisions++;
                    }
                    break;
                }
                currentBrick = fallenBrick;
            }
        }
        
        return new Step(newBricks, collisions);
    }
    
    private record Step(List<Set<Point3D>> bricks, int collisions) {
    }
    
    private record Brick(Point3D a, Point3D b) {
        public static Brick fromString(final String line) {
            final var parts = line.split("~");
            final var a     = Arrays.stream(parts[0].split(",")).mapToInt(Integer::parseInt).toArray();
            final var b     = Arrays.stream(parts[1].split(",")).mapToInt(Integer::parseInt).toArray();
            
            if (a[2] > b[2]) {
                throw new IllegalArgumentException("Brick is not valid");
            }
            
            return new Brick(Point3D.of(a), Point3D.of(b));
        }
        
        public Set<Point3D> toSet() {
            final var set = new HashSet<Point3D>();
            
            for (var x = a().x(); x <= b().x(); x++) {
                for (var y = a().y(); y <= b().y(); y++) {
                    for (var z = a().z(); z <= b().z(); z++) {
                        set.add(new Point3D(x, y, z));
                    }
                }
            }
            
            return set;
        }
    }
    
    private record Point3D(int x, int y, int z) {
        public static Point3D of(final int[] coords) {
            return new Point3D(coords[0], coords[1], coords[2]);
        }
        
        public Point3D moveDown() {
            return new Point3D(x, y, z - 1);
        }
    }
}