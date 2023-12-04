import util.Point;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Day03 {
    public static void main(final String[] args) throws IOException {
        final var lines = Files.readAllLines(Paths.get("resources/day03.txt"));
        
        System.out.println(part1(lines));
        System.out.println(part2(lines));
    }
    
    static int part1(final List<String> lines) {
        final var           map     = buildMap(lines);
        final AtomicInteger partSum = new AtomicInteger();
        
        map.entrySet()
           .stream()
           .filter((entry) -> Element.of(entry.getValue()) == Element.SYMBOL)
           .forEach((symbolEntry) -> {
               final var numberParts     = findNumberPartsFromPoint(symbolEntry.getKey(), map);
               final var numberPartsSeen = new HashSet<Point>();
               
               for (final var numberPart : numberParts) {
                   final var sb = findNumberFromNumberPart(numberPart, numberPartsSeen, map);
                   if (sb == null) {
                       continue;
                   }
                   partSum.addAndGet(Integer.parseInt(sb.toString()));
               }
           });
        
        return partSum.get();
    }
    
    static int part2(final List<String> lines) {
        final var           map      = buildMap(lines);
        final AtomicInteger ratioSum = new AtomicInteger();
        
        map.entrySet().stream().filter((entry) -> entry.getValue() == Element.GEAR_ELEMENT).forEach((gearEntry) -> {
            final var numberParts     = findNumberPartsFromPoint(gearEntry.getKey(), map);
            final var numberPartsSeen = new HashSet<Point>();
            
            // We have to map out all the numbers, because the gear is maybe only connected to one number part.
            final var ratios = new ArrayList<Integer>();
            
            for (final var numberPart : numberParts) {
                final var sb = findNumberFromNumberPart(numberPart, numberPartsSeen, map);
                if (sb == null) {
                    continue;
                }
                ratios.add(Integer.parseInt(sb.toString()));
            }
            
            if (ratios.size() < 2) {
                return;
            }
            ratioSum.addAndGet(ratios.stream().reduce((a, b) -> a * b).orElseThrow());
        });
        
        return ratioSum.get();
    }
    
    private static ArrayList<Point> findNumberPartsFromPoint(final Point point, final Map<Point, Character> map) {
        final var numberParts = new ArrayList<Point>();
        
        for (final var neighbor : point.neighbors()) {
            final var neighborElement = Element.of(map.getOrDefault(neighbor, Element.EMPTY_ELEMENT));
            if (neighborElement == Element.NUMBER) {
                numberParts.add(neighbor);
            }
        }
        
        return numberParts;
    }
    
    private static StringBuilder findNumberFromNumberPart(
            final Point part, final HashSet<Point> partsSeen, final Map<Point, Character> map
    ) {
        if (partsSeen.contains(part)) {
            return null;
        }
        
        partsSeen.add(part);
        final var sb = new StringBuilder();
        sb.append(map.get(part));
        
        // Go left
        var  current = part;
        char currentChar;
        while (Element.of((currentChar = map.getOrDefault((current = current.left()), Element.EMPTY_ELEMENT))) ==
               Element.NUMBER) {
            partsSeen.add(current);
            sb.insert(0, currentChar);
        }
        
        // Go right
        current = part;
        while (Element.of((currentChar = map.getOrDefault((current = current.right()), Element.EMPTY_ELEMENT))) ==
               Element.NUMBER) {
            partsSeen.add(current);
            sb.append(currentChar);
        }
        
        return sb;
    }
    
    private static Map<Point, Character> buildMap(final List<String> lines) {
        final var map = new HashMap<Point, Character>();
        
        for (int y = 0; y < lines.size(); y++) {
            final var line = lines.get(y);
            for (int x = 0; x < line.length(); x++) {
                final var c = line.charAt(x);
                map.put(new Point(x, y), c);
            }
        }
        
        return map;
    }
    
    private enum Element {
        NUMBER, SYMBOL, EMPTY;
        
        public static final Character EMPTY_ELEMENT = '.';
        public static final Character GEAR_ELEMENT  = '*';
        
        static Element of(final char c) {
            if (c >= '0' && c <= '9') {
                return NUMBER;
            } else if (c == EMPTY_ELEMENT) {
                return EMPTY;
            } else {
                return SYMBOL;
            }
        }
    }
}
