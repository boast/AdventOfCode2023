import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

public class Day07 {
    
    final Comparator<Hand> handComparator = (hand1, hand2) -> {
        final var compareTypes = hand1.getTypeJoker().compareTo(hand2.getTypeJoker());
        
        if (compareTypes != 0) {
            return compareTypes;
        }
        
        // Compare left to right until one is greater than the other
        for (int i = 0; i < hand1.cards.length; i++) {
            if (hand1.cards[i] != hand2.cards[i]) {
                return hand1.cards[i].compareTo(hand2.cards[i]);
            }
        }
        throw new IllegalStateException("Hands are equal");
    };
    
    public static void main(final String[] args) throws IOException {
        final var lines = Files.readAllLines(Path.of("resources/day07.txt"));
        
        System.out.println(part1(lines));
        System.out.println(part2(lines));
    }
    
    static long part1(final List<String> lines) {
        final var hands = lines.stream()
                               .map(line -> Hand.fromString(line, false))
                               .sorted(new Day07().handComparator)
                               .toArray(Hand[]::new);
        return calculateWinnings(hands);
    }
    
    static long part2(final List<String> lines) {
        final var hands = lines.stream()
                               .map(line -> Hand.fromString(line, true))
                               .sorted(new Day07().handComparator)
                               .toArray(Hand[]::new);
        return calculateWinnings(hands);
    }
    
    static long calculateWinnings(final Hand[] hands) {
        var sum = 0L;
        
        for (int i = 0; i < hands.length; i++) {
            sum += hands[i].bet() * (hands.length - i);
        }
        
        return sum;
    }
    
    enum Type {
        FIVE_OF_A_KIND,
        FOUR_OF_A_KIND,
        FULL_HOUSE,
        THREE_OF_A_KIND,
        TWO_PAIR,
        ONE_PAIR,
        HIGH_CARD,
    }
    
    enum Card {
        ACE,
        KING,
        QUEEN,
        JACK,
        TEN,
        NINE,
        EIGHT,
        SEVEN,
        SIX,
        FIVE,
        FOUR,
        THREE,
        TWO,
        JOKER;
        
        static Card fromChar(final char value, final boolean joker) {
            if (joker && value == 'J') {
                return JOKER;
            }
            return switch (value) {
                case 'A' -> ACE;
                case 'K' -> KING;
                case 'Q' -> QUEEN;
                case 'J' -> JACK;
                case 'T' -> TEN;
                case '9' -> NINE;
                case '8' -> EIGHT;
                case '7' -> SEVEN;
                case '6' -> SIX;
                case '5' -> FIVE;
                case '4' -> FOUR;
                case '3' -> THREE;
                case '2' -> TWO;
                default -> throw new IllegalArgumentException("Invalid card value: " + value);
            };
        }
    }
    
    record Hand(Card[] cards, long bet) {
        static Hand fromString(final String string, final boolean joker) {
            final var parts     = string.split(" ");
            final var cardsChar = parts[0].toCharArray();
            final var cards = new Card[]{
                    Card.fromChar(cardsChar[0], joker),
                    Card.fromChar(cardsChar[1], joker),
                    Card.fromChar(cardsChar[2], joker),
                    Card.fromChar(cardsChar[3], joker),
                    Card.fromChar(cardsChar[4], joker)
            };
            final var bet = Long.parseLong(parts[1]);
            
            return new Hand(cards, bet);
        }
        
        Type getType() {
            if (areSame(0, 1, 2, 3, 4)) {
                return Type.FIVE_OF_A_KIND;
            }
            if (areSame(1, 2, 3, 4) ||
                areSame(0, 2, 3, 4) ||
                areSame(0, 1, 3, 4) ||
                areSame(0, 1, 2, 4) ||
                areSame(0, 1, 2, 3)) {
                return Type.FOUR_OF_A_KIND;
            }
            if (areSame(0, 1, 2) && areSame(3, 4) ||
                areSame(0, 1, 3) && areSame(2, 4) ||
                areSame(0, 1, 4) && areSame(2, 3) ||
                areSame(0, 2, 3) && areSame(1, 4) ||
                areSame(0, 2, 4) && areSame(1, 3) ||
                areSame(0, 3, 4) && areSame(1, 2) ||
                areSame(1, 2, 3) && areSame(0, 4) ||
                areSame(1, 2, 4) && areSame(0, 3) ||
                areSame(1, 3, 4) && areSame(0, 2) ||
                areSame(2, 3, 4) && areSame(0, 1)) {
                return Type.FULL_HOUSE;
            }
            if (areSame(0, 1, 2) ||
                areSame(0, 1, 3) ||
                areSame(0, 1, 4) ||
                areSame(0, 2, 3) ||
                areSame(0, 2, 4) ||
                areSame(0, 3, 4) ||
                areSame(1, 2, 3) ||
                areSame(1, 2, 4) ||
                areSame(1, 3, 4) ||
                areSame(2, 3, 4)) {
                return Type.THREE_OF_A_KIND;
            }
            if (areSame(0, 1) && areSame(2, 3) ||
                areSame(0, 1) && areSame(2, 4) ||
                areSame(0, 1) && areSame(3, 4) ||
                areSame(0, 2) && areSame(1, 3) ||
                areSame(0, 2) && areSame(1, 4) ||
                areSame(0, 2) && areSame(3, 4) ||
                areSame(0, 3) && areSame(1, 2) ||
                areSame(0, 3) && areSame(1, 4) ||
                areSame(0, 3) && areSame(2, 4) ||
                areSame(0, 4) && areSame(1, 2) ||
                areSame(0, 4) && areSame(1, 3) ||
                areSame(0, 4) && areSame(2, 3) ||
                areSame(1, 2) && areSame(3, 4) ||
                areSame(1, 3) && areSame(2, 4) ||
                areSame(1, 4) && areSame(2, 3)) {
                return Type.TWO_PAIR;
            }
            if (areSame(0, 1) ||
                areSame(0, 2) ||
                areSame(0, 3) ||
                areSame(0, 4) ||
                areSame(1, 2) ||
                areSame(1, 3) ||
                areSame(1, 4) ||
                areSame(2, 3) ||
                areSame(2, 4) ||
                areSame(3, 4)) {
                return Type.ONE_PAIR;
            }
            return Type.HIGH_CARD;
        }
        
        int jokerCount() {
            var count = 0;
            for (final var card : cards) {
                if (card == Card.JOKER) {
                    count++;
                }
            }
            return count;
        }
        
        Type getTypeJoker() {
            final var type = getType();
            
            // No jokers or already best type
            if (jokerCount() == 0 || type == Type.FIVE_OF_A_KIND) {
                return type;
            }
            if (jokerCount() == 1) {
                if (type == Type.FOUR_OF_A_KIND) {
                    return Type.FIVE_OF_A_KIND;
                }
                // Full house is impossible with only one joker (not enough cards)
                if (type == Type.THREE_OF_A_KIND) {
                    return Type.FOUR_OF_A_KIND;
                }
                if (type == Type.TWO_PAIR) {
                    return Type.FULL_HOUSE;
                }
                if (type == Type.ONE_PAIR) {
                    return Type.THREE_OF_A_KIND;
                }
                if (type == Type.HIGH_CARD) {
                    return Type.ONE_PAIR;
                }
                throw new IllegalStateException("Invalid type: %s".formatted(type));
            }
            if (jokerCount() == 2) {
                // Four of a kind is impossible with two jokers (not enough cards)
                if (type == Type.FULL_HOUSE) {
                    return Type.FIVE_OF_A_KIND;
                }
                // Three of a kind is impossible with two jokers (would be full house)
                if (type == Type.TWO_PAIR) {
                    return Type.FOUR_OF_A_KIND;
                }
                if (type == Type.ONE_PAIR) {
                    return Type.THREE_OF_A_KIND;
                }
                // High card is impossible with two jokers (would be one pair)
            }
            if (jokerCount() == 3) {
                // Four of a kind is impossible with three jokers (not enough cards)
                if (type == Type.FULL_HOUSE) {
                    return Type.FIVE_OF_A_KIND;
                }
                if (type == Type.THREE_OF_A_KIND) {
                    return Type.FULL_HOUSE;
                }
                // Two pair is impossible with three jokers (would be full house)
                // One pair is impossible with three jokers (would be three of a kind)
                // High card is impossible with three jokers (would be three of a kind)
            }
            if (jokerCount() == 4) {
                return Type.FIVE_OF_A_KIND;
            }
            throw new IllegalStateException("Invalid joker count: %d".formatted(jokerCount()));
        }
        
        private boolean areSame(final int... index) {
            for (int i = 0; i < index.length - 1; i++) {
                if (cards[index[i]] != cards[index[i + 1]]) {
                    return false;
                }
            }
            
            return true;
        }
        
    }
}
