import util.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class Day19 {
    
    public static void main(final String[] args) throws IOException {
        final var lines = Files.readAllLines(Paths.get("resources/day19.txt"));
        
        System.out.println(part1(lines));
        System.out.println(part2(lines));
    }
    
    static long part1(final List<String> lines) {
        final var instructions = parseInstructions(lines);
        final var parts        = lines.stream().skip(instructions.size() + 1).map(Part::from).toList();
        
        var sum = 0L;
        
        for (final var part : parts) {
            var result = "in";
            while (!result.equals("A") && !result.equals("R")) {
                for (final var rule : instructions.get(result)) {
                    if (rule.variable() == null) {
                        result = rule.target();
                        break;
                    }
                    final var variable = part.valueOf(rule.variable());
                    
                    if (rule.isSmaller() && variable < rule.value() || !rule.isSmaller() && variable > rule.value()) {
                        result = rule.target();
                        break;
                    }
                }
            }
            sum += result.equals("A") ? part.sum() : 0;
        }
        
        return sum;
    }
    
    static long part2(final List<String> lines) {
        final var instructions = parseInstructions(lines);
        final var state        = new State("in", new Intervals());
        final var candidates   = new ArrayDeque<State>();
        candidates.add(state);
        
        var sum = 0L;
        
        while (!candidates.isEmpty()) {
            final var candidate        = candidates.poll();
            var       currentIntervals = candidate.intervals();
            
            if (candidate.target().equals("A")) {
                sum += currentIntervals.sum();
                continue;
            }
            if (candidate.target().equals("R")) {
                continue;
            }
            
            for (final var instruction : instructions.get(candidate.target())) {
                if (instruction.variable() == null) {
                    candidates.add(new State(instruction.target(), currentIntervals));
                    continue;
                }
                
                candidates.add(new State(instruction.target(), currentIntervals.apply(instruction)));
                currentIntervals = currentIntervals.apply(instruction.invert());
            }
        }
        
        return sum;
    }
    
    private static Map<String, List<Rule>> parseInstructions(final List<String> lines) {
        return lines.stream()
                    .takeWhile(line -> !line.isBlank())
                    .map(line -> line.split("\\{"))
                    .collect(Collectors.toMap(parts -> parts[0], parts -> {
                        final var part = parts[1].substring(0, parts[1].length() - 1).split(",");
                        return Arrays.stream(part).map(Rule::from).toList();
                    }));
    }
    
    private record State(String target, Intervals intervals) {
    }
    
    private record Intervals(Pair<Long> x, Pair<Long> m, Pair<Long> a, Pair<Long> s) {
        private Intervals() {
            this(new Pair<>(1L, 4000L), new Pair<>(1L, 4000L), new Pair<>(1L, 4000L), new Pair<>(1L, 4000L));
        }
        
        public Long sum() {
            return (x.second() - x.first() + 1) *
                   (m.second() - m.first() + 1) *
                   (a.second() - a.first() + 1) *
                   (s.second() - s.first() + 1);
        }
        
        private Intervals apply(final Rule rule) {
            final var pair = switch (rule.variable()) {
                case "x" -> x();
                case "m" -> m();
                case "a" -> a();
                case "s" -> s();
                default -> throw new IllegalStateException("Unexpected value: " + rule.variable());
            };
            
            var lowerBound = pair.first();
            var upperBound = pair.second();
            
            if (rule.isSmaller()) {
                upperBound = rule.value() - 1;
            } else {
                lowerBound = rule.value() + 1;
            }
            
            final Pair<Long> p = new Pair<>(lowerBound, upperBound);
            
            return switch (rule.variable()) {
                case "x" -> new Intervals(p, m, a, s);
                case "m" -> new Intervals(x, p, a, s);
                case "a" -> new Intervals(x, m, p, s);
                case "s" -> new Intervals(x, m, a, p);
                default -> throw new IllegalStateException("Unexpected value: " + rule.variable());
            };
        }
        
    }
    
    private record Rule(String target, String variable, Long value, Boolean isSmaller) {
        public static Rule from(final String line) {
            final var parts = line.split(":");
            
            if (parts.length == 1) {
                return new Rule(parts[0], null, null, null);
            }
            
            final var operation = parts[0].split("[<>]");
            final var variable  = operation[0];
            final var value     = Long.parseLong(operation[1]);
            final var isSmaller = parts[0].contains("<");
            
            return new Rule(parts[1], variable, value, isSmaller);
        }
        
        public Rule invert() {
            if (isSmaller()) {
                return new Rule(target(), variable(), value() - 1, false);
            } else {
                return new Rule(target(), variable(), value() + 1, true);
            }
        }
    }
    
    private record Part(int x, int m, int a, int s) {
        public static Part from(final String line) {
            final var parts = Arrays.stream(line.substring(1, line.length() - 1).split(","))
                                    .map(part -> part.split("=")[1])
                                    .mapToInt(Integer::parseInt)
                                    .toArray();
            return new Part(parts[0], parts[1], parts[2], parts[3]);
        }
        
        public int sum() {
            return x + m + a + s;
        }
        
        public int valueOf(final String variable) {
            return switch (variable) {
                case "x" -> x;
                case "m" -> m;
                case "a" -> a;
                case "s" -> s;
                default -> throw new IllegalStateException("Unexpected value: " + variable);
            };
        }
    }
    
}