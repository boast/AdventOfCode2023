package util;

import java.util.List;

public record Point(int x, int y) {
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
    
    public Direction directionOf(final Point other) {
        return switch (other.x - x) {
            case 0 -> switch (other.y - y) {
                case -1 -> Direction.N;
                case 1 -> Direction.S;
                default -> null;
            };
            case -1 -> switch (other.y - y) {
                case 0 -> Direction.W;
                case -1 -> Direction.N;
                case 1 -> Direction.S;
                default -> null;
            };
            case 1 -> switch (other.y - y) {
                case 0 -> Direction.E;
                case -1 -> Direction.N;
                case 1 -> Direction.S;
                default -> null;
            };
            default -> null;
        };
    }
    
    public enum Direction {
        N,
        E,
        S,
        W,
        ;
        
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
