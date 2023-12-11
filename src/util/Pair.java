package util;

import java.util.ArrayList;
import java.util.List;

public record Pair<T>(T first, T second){
    public static <T> Pair<T> pair(final T first, final T second) {
        return new Pair<>(first, second);
    }
    
    public static <T> List<Pair<T>> pairsFromList(final List<T> list) {
        final var pairs = new ArrayList<Pair<T>>();
        for (var i = 0; i < list.size(); i++) {
            for (var j = i + 1; j < list.size(); j++) {
                pairs.add(pair(list.get(i), list.get(j)));
            }
        }
        return pairs;
    }
}
