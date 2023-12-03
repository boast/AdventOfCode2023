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
    
    public Point left() {
        return new Point(x - 1, y);
    }
    
    public Point right() {
        return new Point(x + 1, y);
    }
}
