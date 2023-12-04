import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Day04 {
    public static void main(final String[] args) throws IOException {
        final var lines = Files.readAllLines(Paths.get("resources/day04.txt"));
        
        System.out.println(part1(lines));
        System.out.println(part2(lines));
    }
    
    static int part1(final List<String> lines) {
        return lines.stream().mapToInt((line) -> {
            final Result result = parseWinningNumbersAndNumbers(line);
            result.numbers.retainAll(result.winningNumbers);
            
            if (result.numbers.isEmpty()) {
                return 0;
            }
            
            return (int) Math.pow(2, result.numbers.size() - 1);
        }).sum();
    }
    
    static int part2(final List<String> lines) {
        final var cards = new int[lines.size()];
        final var i     = new AtomicInteger();
        
        Arrays.fill(cards, 1);
        
        lines.forEach((line) -> {
            final Result result = parseWinningNumbersAndNumbers(line);
            result.numbers().retainAll(result.winningNumbers());
            
            for (int j = 1; j <= result.numbers.size(); j++) {
                cards[i.get() + j] += cards[i.get()];
            }
            
            i.getAndIncrement();
        });
        
        
        return Arrays.stream(cards).sum();
    }
    
    private static Result parseWinningNumbersAndNumbers(final String line) {
        final var parts = line.split(": ");
        final var game  = parts[1].split(" \\| ");
        final var winningNumbers = Arrays.stream(game[0].trim().split("\\s+"))
                                         .map(String::trim)
                                         .map(Integer::parseInt)
                                         .toList();
        final var numbers = new ArrayList<>(Arrays.stream(game[1].trim().split("\\s+"))
                                                  .map(String::trim)
                                                  .map(Integer::parseInt)
                                                  .toList());
        return new Result(winningNumbers, numbers);
    }
    
    private record Result(List<Integer> winningNumbers, ArrayList<Integer> numbers) {
    }
}
