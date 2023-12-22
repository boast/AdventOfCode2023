import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Day12 {
    private static final String               UNKNOWN = "?";
    private static final String               GOOD    = ".";
    private static final String               BAD     = "#";
    private static final Map<Candidate, Long> CACHE   = new HashMap<>();
    
    public static void main(final String[] args) throws IOException {
        final var lines = Files.readAllLines(Path.of("resources/day12.txt"));
        
        System.out.println(part1(lines));
        System.out.println(part2(lines));
    }
    
    static long part1(final List<String> lines) {
        return lines.stream().map(Candidate::fromString).mapToLong(Day12::solutionsFor).sum();
    }
    
    static long part2(final List<String> lines) {
        return lines.stream().map(Candidate::fromStringExpanded).mapToLong(Day12::solutionsFor).sum();
    }
    
    private static long solutionsFor(final Candidate candidate) {
        // Memoization pattern, records ara perfect keys for cache maps (they implement equals and hashCode)
        if (CACHE.containsKey(candidate)) {
            return CACHE.get(candidate);
        }
        
        final var result = solutionsForUncached(candidate);
        CACHE.put(candidate, result);
        return result;
    }
    
    private static long solutionsForUncached(final Candidate candidate) {
        // If condition is empty, we have found a solution if there are no groups left
        if (candidate.conditions.isEmpty()) {
            return candidate.groups.isEmpty() ? 1 : 0;
        }
        // If there are no groups left, we have found a solution if the remaining conditions are all not BAD
        if (candidate.groups.isEmpty()) {
            return candidate.conditions.contains(BAD) ? 0 : 1;
        }
        
        // Check if the conditions is long enough to contain all groups + a space between each group
        final var sumOfGroups = candidate.groups.stream().reduce(0, Integer::sum);
        if (candidate.conditions.length() < sumOfGroups + candidate.groups.size() - 1) {
            return 0;
        }
        
        if (candidate.conditions.startsWith(GOOD)) {
            return solutionsFor(new Candidate(candidate.conditions.substring(1), candidate.groups));
        }
        if (candidate.conditions.startsWith(BAD)) {
            // The next group-length parts contains good, this is not a solution
            if (candidate.conditions.substring(1, candidate.groups.getFirst()).contains(GOOD)) {
                return 0;
            }
            // If after the group-length parts is a bad part, this is not a solution (String::startsWith works out of bounds)
            if (candidate.conditions.startsWith(BAD, candidate.groups.getFirst())) {
                return 0;
            }
            
            final var isAtEnd = candidate.groups.getFirst() == candidate.conditions.length();
            
            return solutionsFor(new Candidate(
                    isAtEnd ? "" : candidate.conditions.substring(candidate.groups.getFirst() + 1),
                    candidate.groups.subList(1, candidate.groups.size())
            ));
        }
        
        return solutionsFor(new Candidate(BAD + candidate.conditions.substring(1), candidate.groups)) +
               solutionsFor(new Candidate(GOOD + candidate.conditions.substring(1), candidate.groups));
    }
    
    private record Candidate(String conditions, List<Integer> groups) {
        public static Candidate fromString(final String string) {
            final var parts = string.split(" ");
            return new Candidate(parts[0], parseGroups(parts[1]));
        }
        
        public static Candidate fromStringExpanded(final String string) {
            final var parts          = string.split(" ");
            final var conditionsCopy = Collections.nCopies(5, parts[0]);
            final var groupsCopy     = Collections.nCopies(5, parts[1]);
            return new Candidate(String.join(UNKNOWN, conditionsCopy), parseGroups(String.join(",", groupsCopy)));
        }
        
        private static List<Integer> parseGroups(final String part) {
            return Arrays.stream(part.split(",")).map(Integer::parseInt).toList();
        }
    }
}
