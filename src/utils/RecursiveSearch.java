package utils;

import java.util.List;

import domain.Libro;


public final class RecursiveSearch {

    private RecursiveSearch() {}

    
    public static Libro binarySearchByTitle(List<Libro> sortedByTitle, String exactTitle) {
        if (sortedByTitle == null || sortedByTitle.isEmpty() || exactTitle == null) return null;
        int idx = binarySearchByTitle(sortedByTitle, exactTitle, 0, sortedByTitle.size() - 1);
        return idx >= 0 ? sortedByTitle.get(idx) : null;
    }

    // Búsqueda binaria RECURSIVA
    private static int binarySearchByTitle(List<Libro> list, String title, int low, int high) {
        if (low > high) return -1;

        int mid = low + (high - low) / 2;
        String midTitle = list.get(mid).getTitulo();

        int cmp = midTitle.compareToIgnoreCase(title);
        if (cmp == 0) return mid;
        if (cmp > 0) return binarySearchByTitle(list, title, low, mid - 1);
        return binarySearchByTitle(list, title, mid + 1, high);
    }
}
