import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

// NOTE: all classes are nested inside Lab8 so they cannot clash with same-named
// classes (like Stack) from other files in the same folder.
public class Lab8 {

    public static void main(String[] args) {

        // ---------- Exercise 1: ArrayList of 10 integers ----------
        System.out.println("=== Exercise 1: ArrayList of integers ===");
        List<Integer> numbers = new ArrayList<>(Arrays.asList(4, 17, 9, 23, 1, 15, 8, 42, 6, 30));

        int sum = 0;
        int max = numbers.get(0);          // start with the first value as the maximum
        for (int n : numbers) {            // auto-unboxing: Integer -> int
            sum += n;
            if (n > max) {
                max = n;
            }
        }
        System.out.println("Sum: " + sum);
        System.out.println("Maximum: " + max);

        System.out.print("Reverse order: ");
        for (int i = numbers.size() - 1; i >= 0; i--) { // walk backwards, list is unchanged
            System.out.print(numbers.get(i) + " ");
        }
        System.out.println();

        // ---------- Exercise 2: remove duplicates with a Set ----------
        System.out.println("\n=== Exercise 2: Remove duplicates ===");
        List<String> names = Arrays.asList("Tendai", "Rudo", "Farai", "Rudo", "Tendai", "Chipo");
        // A Set cannot hold duplicates. LinkedHashSet also keeps the original order
        // (a plain HashSet would remove duplicates but in an unpredictable order).
        Set<String> unique = new LinkedHashSet<>(names);
        System.out.println("Original: " + names);
        System.out.println("Unique:   " + unique);

        // ---------- Exercise 3: generic Stack ----------
        System.out.println("\n=== Exercise 3: Generic Stack ===");
        Stack<String> stack = new Stack<>();
        stack.push("A");
        stack.push("B");
        stack.push("C");
        System.out.println("peek: " + stack.peek());   // C (looks, does not remove)
        System.out.println("pop:  " + stack.pop());    // C
        System.out.println("pop:  " + stack.pop());    // B
        System.out.println("isEmpty? " + stack.isEmpty()); // false (A is left)
        stack.pop();
        System.out.println("isEmpty? " + stack.isEmpty()); // true
        try {
            stack.pop(); // popping an empty stack is an error
        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
        }

        Stack<Integer> intStack = new Stack<>(); // the SAME class works for Integer too
        intStack.push(10);
        System.out.println("Integer stack peek: " + intStack.peek());

        // ---------- Exercise 4: word frequency, sorted descending ----------
        System.out.println("\n=== Exercise 4: Word frequency ===");
        String paragraph;
        // try-with-resources closes the Scanner automatically (no resource-leak warning)
        try (Scanner sc = new Scanner(System.in)) {
            System.out.print("Enter a paragraph (or just press Enter to use a sample): ");
            paragraph = sc.hasNextLine() ? sc.nextLine() : "";
        }
        if (paragraph.isBlank()) {
            paragraph = "The cat saw the dog and the dog saw the cat. A cat is a cat, but a dog is a dog.";
            System.out.println("(using sample paragraph)");
        }
        printWordFrequencies(paragraph);

        // ---------- Exercise 5: generic bubble sort ----------
        System.out.println("\n=== Exercise 5: Generic bubbleSort ===");
        Integer[] nums = {5, 2, 9, 1, 7};
        String[] words = {"pear", "apple", "mango", "banana"};
        bubbleSort(nums);   // T is inferred as Integer
        bubbleSort(words);  // T is inferred as String
        System.out.println("Sorted Integer[]: " + Arrays.toString(nums));
        System.out.println("Sorted String[]:  " + Arrays.toString(words));

        // int[] primitives = {3, 1, 2};
        // bubbleSort(primitives);   // WILL NOT COMPILE (explanation below)
        int[] primitives = {3, 1, 2};
        bubbleSort(primitives);      // works only because we wrote a separate int[] overload
        System.out.println("Sorted int[] (using the overload): " + Arrays.toString(primitives));
    }

    // ============================================================
    // Exercise 3: generic Stack backed by an ArrayList<T>
    // ============================================================
    // <T> is a type parameter: it is replaced by String, Integer, etc. when you use the class.
    static class Stack<T> {
        private final ArrayList<T> items = new ArrayList<>(); // the end of the list is the top

        void push(T item) {
            items.add(item);                       // add on top
        }

        T pop() {
            if (isEmpty()) {
                throw new RuntimeException("Cannot pop: the stack is empty");
            }
            return items.remove(items.size() - 1); // remove and return the top
        }

        T peek() {
            if (isEmpty()) {
                throw new RuntimeException("Cannot peek: the stack is empty");
            }
            return items.get(items.size() - 1);    // look at the top without removing
        }

        boolean isEmpty() {
            return items.isEmpty();
        }
    }

    // ============================================================
    // Exercise 4: count words with a Map, then sort by frequency (highest first)
    // ============================================================
    static void printWordFrequencies(String paragraph) {
        Map<String, Integer> counts = new HashMap<>();

        // Lower-case everything, then split on anything that is not a letter or apostrophe
        for (String word : paragraph.toLowerCase().split("[^a-z']+")) {
            if (word.isEmpty()) {
                continue; // splitting can leave an empty string at the start
            }
            // merge: if the word is new, store 1; otherwise add 1 to the existing count
            counts.merge(word, 1, Integer::sum);
        }

        // A HashMap has no order, so copy the entries into a list and sort that list
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(counts.entrySet());
        entries.sort((a, b) -> {
            int byCount = Integer.compare(b.getValue(), a.getValue()); // higher count first
            return byCount != 0 ? byCount : a.getKey().compareTo(b.getKey()); // tie: A to Z
        });

        for (Map.Entry<String, Integer> e : entries) {
            System.out.println(e.getKey() + ": " + e.getValue());
        }
    }

    // ============================================================
    // Exercise 5: generic bubble sort
    // ============================================================
    // "T extends Comparable<T>" means: T can be ANY type that knows how to compare
    // itself with another T (via compareTo). Integer and String both qualify.
    static <T extends Comparable<T>> void bubbleSort(T[] arr) {
        for (int pass = 0; pass < arr.length - 1; pass++) {
            for (int i = 0; i < arr.length - 1 - pass; i++) {
                if (arr[i].compareTo(arr[i + 1]) > 0) { // left is bigger than right?
                    T temp = arr[i];                    // then swap them
                    arr[i] = arr[i + 1];
                    arr[i + 1] = temp;
                }
            }
        }
    }

    static void bubbleSort(int[] arr) {
        for (int pass = 0; pass < arr.length - 1; pass++) {
            for (int i = 0; i < arr.length - 1 - pass; i++) {
                if (arr[i] > arr[i + 1]) { // primitives are compared with > directly
                    int temp = arr[i];
                    arr[i] = arr[i + 1];
                    arr[i + 1] = temp;
                }
            }
        }
    }
}
