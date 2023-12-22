import util.Pair;
import util.Point;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static util.Pair.pair;
import static util.Pair.pairsFromList;
import static util.Point.manhattanDistance;

public class Day11 {
    public static void main(final String[] args) throws IOException {
        final var lines = Files.readAllLines(Path.of("resources/day11.txt"));
        
        System.out.println(part1(lines));
        System.out.println(part2(lines));
    }
    
    static long part1(final List<String> lines) {
        final var expansion = getExpansions(lines);
        final var pairs     = pairsFromList(parseGalaxies(lines));
        
        return pairs.stream().mapToLong(pair -> getDistanceWithExpansionFactor(pair, expansion, 2)).sum();
    }
    
    static long part2(final List<String> lines) {
        final var expansions = getExpansions(lines);
        final var pairs      = pairsFromList(parseGalaxies(lines));
        
        return pairs.stream().mapToLong(pair -> getDistanceWithExpansionFactor(pair, expansions, 1000000)).sum();
    }
    
    private static Pair<List<Integer>> getExpansions(final List<String> lines) {
        final var yExpansion = new ArrayList<Integer>();
        for (var y = 0; y < lines.size(); y++) {
            if (!lines.get(y).contains("#")) {
                yExpansion.add(y);
            }
        }
        
        final var xExpansion = new ArrayList<Integer>();
        for (var x = 0; x < lines.getFirst().length(); x++) {
            final var finalX        = x;
            final var currentColumn = lines.stream().map(line -> line.charAt(finalX));
            if (currentColumn.noneMatch(c -> c == '#')) {
                xExpansion.add(x);
            }
        }
        
        return pair(xExpansion, yExpansion);
    }
    
    private static List<Point> parseGalaxies(final List<String> lines) {
        final var galaxies = new ArrayList<Point>();
        
        for (var y = 0; y < lines.size(); y++) {
            for (var x = 0; x < lines.get(y).length(); x++) {
                if (lines.get(y).charAt(x) == '#') {
                    galaxies.add(new Point(x, y));
                }
            }
        }
        
        return galaxies;
    }
    
    private static long getDistanceWithExpansionFactor(
            final Pair<Point> pair, final Pair<List<Integer>> expansions, final int factor
    ) {
        final var fromX = Math.min(pair.first().x(), pair.second().x());
        final var toX   = Math.max(pair.first().x(), pair.second().x());
        final var fromY = Math.min(pair.first().y(), pair.second().y());
        final var toY   = Math.max(pair.first().y(), pair.second().y());
        
        final var xExpansion = expansions.first().stream().filter(x -> x >= fromX && x <= toX).count() * (factor - 1);
        final var yExpansion = expansions.second().stream().filter(y -> y >= fromY && y <= toY).count() * (factor - 1);
        
        return manhattanDistance(pair) + xExpansion + yExpansion;
    }
}
