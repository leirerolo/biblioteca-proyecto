package utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Utilidad de ordenación usando recursividad (MergeSort).
 */
public final class RecursiveSorter {

    private RecursiveSorter() {}

    public static <T> List<T> mergeSort(List<T> list, Comparator<? super T> comparator) {
        if (list == null) throw new IllegalArgumentException("list no puede ser null");
        if (comparator == null) throw new IllegalArgumentException("comparator no puede ser null");
        if (list.size() <= 1) return list;

        List<T> sorted = mergeSortInternal(new ArrayList<>(list), comparator);
        list.clear();
        list.addAll(sorted);
        return list;
    }

    private static <T> List<T> mergeSortInternal(List<T> list, Comparator<? super T> comparator) {
        if (list.size() <= 1) return list;

        int mid = list.size() / 2;

        List<T> left = new ArrayList<>(list.subList(0, mid));
        List<T> right = new ArrayList<>(list.subList(mid, list.size()));

        left = mergeSortInternal(left, comparator);   // recursividad
        right = mergeSortInternal(right, comparator); // recursividad

        return merge(left, right, comparator);
    }

    private static <T> List<T> merge(List<T> left, List<T> right, Comparator<? super T> comparator) {
        List<T> result = new ArrayList<>(left.size() + right.size());
        int i = 0, j = 0;

        while (i < left.size() && j < right.size()) {
            if (comparator.compare(left.get(i), right.get(j)) <= 0) {
                result.add(left.get(i++));
            } else {
                result.add(right.get(j++));
            }
        }
        while (i < left.size()) result.add(left.get(i++));
        while (j < right.size()) result.add(right.get(j++));

        return result;
    }
}
