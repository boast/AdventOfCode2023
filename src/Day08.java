import util.MathUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Day08 {
    public static void main(final String[] args) throws IOException {
        final var lines = Files.readAllLines(Paths.get("resources/day08.txt"));
        
        System.out.println(part1(lines));
        System.out.println(part2(lines));
    }
    
    static long part1(final List<String> lines) {
        final var instructions = parseInstructions(lines);
        final var nodes        = parseNodes(lines);
        
        return navigate("AAA", "ZZZ", nodes, instructions);
    }
    
    static long part2(final List<String> lines) {
        final var instructions = parseInstructions(lines);
        final var nodes        = parseNodes(lines);
        
        final var startNodes = nodes.keySet().stream().filter(key -> key.endsWith("A")).toList();
        final var cycles     = startNodes.stream().map(start -> navigate(start, "Z", nodes, instructions)).toList();
        
        return cycles.stream().reduce(1L, MathUtil::lcm);
    }
    
    private static long navigate(
            final String start, final String end, final Map<String, Node> nodes, final List<Direction> instructions
    ) {
        var current = start;
        var i       = 0L;
        while (!current.endsWith(end) || i == 0L) {
            final var node        = nodes.get(current);
            final var instruction = instructions.get((int) ((i++) % instructions.size()));
            current = switch (instruction) {
                case L -> node.left;
                case R -> node.right;
            };
        }
        return i;
    }
    
    private static List<Direction> parseInstructions(final List<String> lines) {
        return Arrays.stream(lines.getFirst().split("")).map(Direction::valueOf).toList();
    }
    
    private static Map<String, Node> parseNodes(final List<String> lines) {
        return lines.stream()
                    .skip(2)
                    .map(line -> line.split(" = "))
                    .collect(Collectors.toMap(parts -> parts[0], parts -> {
                        final var lookup = parts[1].split(", ");
                        final var left   = lookup[0].substring(1);
                        final var right  = lookup[1].substring(0, lookup[1].length() - 1);
                        return new Node(left, right);
                    }));
    }
    
    enum Direction {
        L,
        R,
    }
    
    record Node(String left, String right) {
    }
}
