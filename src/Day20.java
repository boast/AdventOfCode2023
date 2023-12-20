import util.MathUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Day20 {
    
    public static void main(final String[] args) throws IOException {
        final var lines = Files.readAllLines(Paths.get("resources/day20.txt"));
        
        System.out.println(part1(lines));
        System.out.println(part2(lines));
    }
    
    static long part1(final List<String> lines) {
        final var modules = parseModules(lines);
        var       low     = 0L;
        var       high    = 0L;
        
        for (var i = 0; i < 1000; i++) {
            final var signalQueue = new ArrayDeque<Signal>();
            signalQueue.add(new Signal(null, "roadcaster", false)); // That's not a typo, we removed the first char
            
            while (!signalQueue.isEmpty()) {
                final var signal = signalQueue.poll();
                
                high += signal.value() ? 1 : 0;
                low += signal.value() ? 0 : 1;
                
                process(modules.get(signal.target()), signal, signalQueue);
            }
        }
        
        return low * high;
    }
    
    static long part2(final List<String> lines) {
        final var modules = parseModules(lines);
        
        // rx needs to be low, so let's inspect the input
        // &mg -> rx (all mg inputs must be high, so rx is low)
        // &jg -> mg (any jg input must be low, so mg is high)
        // &rh -> mg (any rh input must be low, so mg is high)
        // &jm -> mg (any jm input must be low, so mg is high)
        // &hf -> mg (any hf input must be low, so mg is high)
        // But: all of jg, rh, jm and hf have only one input!
        // -> Find the cycle of each conjunction (jg, rh, jm and hf) when it is the target of a low-signal
        // -> LCM of all cycles
        
        final var cycles = new HashMap<String, Long>();
        cycles.put("jg", 0L);
        cycles.put("rh", 0L);
        cycles.put("jm", 0L);
        cycles.put("hf", 0L);
        
        var currentCycle = 0L;
        
        // Break when all cycles are non 0
        while (!cycles.values().stream().allMatch(cycle -> cycle != 0L)) {
            currentCycle++;
            final var signalQueue = new ArrayDeque<Signal>();
            signalQueue.add(new Signal(null, "roadcaster", false));
            while (!signalQueue.isEmpty()) {
                final var signal = signalQueue.poll();
                
                // It *could* be, that one cycle fits multiple times in another,
                // so we need to set the cycle length only the first time we see it
                if (cycles.getOrDefault(signal.target(), -1L) == 0L && !signal.value()) {
                    cycles.put(signal.target(), currentCycle);
                }
                
                process(modules.get(signal.target()), signal, signalQueue);
            }
        }
        
        return cycles.values().stream().reduce(1L, MathUtil::lcm);
    }
    
    private static void process(final Module module, final Signal signal, final ArrayDeque<Signal> signalQueue) {
        if (module == null) {
            return;
        }
        
        final var nextSource = signal.target();
        
        switch (module.type()) {
            case FLIP_FLOP -> {
                if (!signal.value()) {
                    final var nextState = !module.state().get("");
                    module.state().put("", nextState);
                    
                    for (final var nextTarget : module.targets()) {
                        signalQueue.add(new Signal(nextSource, nextTarget, nextState));
                    }
                }
            }
            case CONJUNCTION -> {
                module.state().put(signal.source(), signal.value());
                final var nextState = !module.state().values().stream().allMatch(value -> value);
                for (final var nextTarget : module.targets()) {
                    signalQueue.add(new Signal(nextSource, nextTarget, nextState));
                }
            }
            case BROADCASTER -> {
                for (final var nextTarget : module.targets()) {
                    signalQueue.add(new Signal(nextSource, nextTarget, signal.value()));
                }
            }
        }
    }
    
    private static Map<String, Module> parseModules(final List<String> lines) {
        final var modules = lines.stream()
                                 .map(line -> line.split(" -> "))
                                 .collect(Collectors.toMap(parts -> parts[0].substring(1), Module::from));
        
        for (final var entry : modules.entrySet()) {
            final var target = entry.getKey();
            final var module = entry.getValue();
            if (module.type() == Type.CONJUNCTION) {
                for (final var otherEntry : modules.entrySet()) {
                    if (otherEntry.getValue().targets().contains(target)) {
                        module.state().put(otherEntry.getKey(), false);
                    }
                }
            }
        }
        
        return modules;
    }
    
    private enum Type {
        FLIP_FLOP,
        CONJUNCTION,
        BROADCASTER,
        ;
        
        public static Type from(final char c) {
            return switch (c) {
                case '%' -> FLIP_FLOP;
                case '&' -> CONJUNCTION;
                default -> BROADCASTER;
            };
        }
    }
    
    private record Module(Type type, List<String> targets, Map<String, Boolean> state) {
        public static Module from(final String[] parts) {
            final var type    = Type.from(parts[0].charAt(0));
            final var targets = List.of(parts[1].split(", "));
            final Map<String, Boolean> state = switch (type) {
                case FLIP_FLOP -> new HashMap<>(Map.of("", false));
                case CONJUNCTION -> new HashMap<>();
                default -> null;
            };
            
            return new Module(type, targets, state);
        }
    }
    
    private record Signal(String source, String target, boolean value) {
    }
}