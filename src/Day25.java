import util.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Day25 {
    
    public static void main(final String[] args) throws IOException {
        final var lines = Files.readAllLines(Path.of("resources/day25.txt"));
        
        System.out.println(part1(lines));
        System.out.println(part2(lines));
    }
    
    static long part1(final List<String> lines) {
        final var graph = parseGraph(lines);
        
        while (true) {
            final var currentGraph = graph.copy();
            
            while (currentGraph.vertices().size() > 2) {
                final var edge = currentGraph.edges().get(new Random().nextInt(0, currentGraph.edges().size()));
                currentGraph.contract(edge);
            }
            
            if (currentGraph.edges().size() == 3) {
                return currentGraph.vertices()
                                   .stream()
                                   .mapToLong(v -> v.split("\\|").length)
                                   .reduce(1L, (a, b) -> a * b);
            }
        }
    }
    
    static String part2(final List<String> ignoredLines) {
        return "Merry Christmas!";
    }
    
    private static Graph parseGraph(final List<String> lines) {
        final var vertices = new HashSet<String>();
        final var edges    = new ArrayList<Pair<String>>();
        
        for (final var line : lines) {
            final var parts  = line.split(": ");
            final var vertex = parts[0];
            vertices.add(parts[0]);
            
            for (final var otherVertex : parts[1].split(" ")) {
                edges.add(new Pair<>(vertex, otherVertex));
                vertices.add(otherVertex);
            }
        }
        
        return new Graph(new ArrayList<>(vertices), edges);
    }
    
    private record Graph(ArrayList<String> vertices, ArrayList<Pair<String>> edges) {
        public void contract(final Pair<String> edge) {
            final var newVertex   = "%s|%s".formatted(edge.first(), edge.second());
            vertices.remove(edge.first());
            vertices.remove(edge.second());
            vertices.add(newVertex);
            
            final var edgeReversed = Pair.reverse(edge);
            
            for (int i = 0; i < edges.size(); i++) {
                final var e = edges.get(i);
                if (e.equals(edge) || e.equals(edgeReversed)) {
                    edges.remove(i);
                    i--;
                } else if (e.first().equals(edge.first()) || e.first().equals(edge.second())) {
                    edges.set(i, new Pair<>(newVertex, e.second()));
                } else if (e.second().equals(edge.first()) || e.second().equals(edge.second())) {
                    edges.set(i, new Pair<>(e.first(), newVertex));
                }
            }
        }
        
        public Graph copy() {
            return new Graph(new ArrayList<>(vertices), new ArrayList<>(edges));
        }
    }
}