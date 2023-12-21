package util;

import java.util.List;
import java.util.Optional;

public record Point(long x, long y) {
    public Point() {
        this(0, 0);
    }
    
    public static long manhattanDistance(final Point a, final Point b) {
        return a.manhattanDistance(b);
    }
    
    public static long manhattanDistance(final Pair<Point> pair) {
        return manhattanDistance(pair.first(), pair.second());
    }
    
    public List<Point> neighbors() {
        return List.of(
                new Point(x - 1, y - 1),
                new Point(x - 1, y),
                new Point(x - 1, y + 1),
                new Point(x, y - 1),
                new Point(x, y + 1),
                new Point(x + 1, y - 1),
                new Point(x + 1, y),
                new Point(x + 1, y + 1)
        );
    }
    
    public List<Point> adjacent() {
        return List.of(
                new Point(x - 1, y),
                new Point(x, y - 1),
                new Point(x, y + 1),
                new Point(x + 1, y)
        );
    }
    
    public long manhattanDistance(final Point other) {
        return Math.abs(x - other.x) + Math.abs(y - other.y);
    }
    
    public Point up() {
        return new Point(x, y - 1);
    }
    
    public Point down() {
        return new Point(x, y + 1);
    }
    
    public Point left() {
        return new Point(x - 1, y);
    }
    
    public Point right() {
        return new Point(x + 1, y);
    }
    
    public Point move(final Direction direction) {
        return switch (direction) {
            case N -> up();
            case E -> right();
            case S -> down();
            case W -> left();
        };
    }
    
    public Point move(final Direction direction, final long distance) {
        return switch (direction) {
            case N -> new Point(x, y - distance);
            case E -> new Point(x + distance, y);
            case S -> new Point(x, y + distance);
            case W -> new Point(x - distance, y);
        };
    }
    
    public Optional<Direction> directionOf(final Point other) {
        final long diffX = other.x - x;
        final long diffY = other.y - y;
        
        if (Math.abs(diffX) > 1 || Math.abs(diffY) > 1) {
            return Optional.empty();
        }
        
        final int diffXInt = (int) diffX;
        final int diffYInt = (int) diffY;
        
        return Optional.ofNullable(switch (diffXInt) {
            case 0 -> switch (diffYInt) {
                case -1 -> Direction.N;
                case 1 -> Direction.S;
                default -> null;
            };
            case -1 -> switch (diffYInt) {
                case 0 -> Direction.W;
                case -1 -> Direction.N;
                case 1 -> Direction.S;
                default -> null;
            };
            case 1 -> switch (diffYInt) {
                case 0 -> Direction.E;
                case -1 -> Direction.N;
                case 1 -> Direction.S;
                default -> null;
            };
            default -> null;
        });
    }
    
    public enum Direction {
        N,
        E,
        S,
        W,
        ;
        
        public static Direction from(final String s) {
            return switch (s) {
                case "U" -> N;
                case "R" -> E;
                case "D" -> S;
                case "L" -> W;
                default -> throw new IllegalArgumentException("Invalid direction: " + s);
            };
        }
        
        public Direction opposite() {
            return switch (this) {
                case N -> S;
                case E -> W;
                case S -> N;
                case W -> E;
            };
        }
    }
}
