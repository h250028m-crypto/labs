import java.util.EnumMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

// NOTE: all types are nested inside Lab9 so they cannot clash with same-named
// classes (like Student or Book) from other files in the same folder.
// Records and the arrow-style switch need Java 16 or newer.
public class Lab9 {

    public static void main(String[] args) {

        // ---------- Exercise 1: enum and switch ----------
        System.out.println("=== Exercise 1: Level enum ===");
        for (Level level : Level.values()) { // values() returns every constant in order
            advice(level);
        }

        // ---------- Exercise 2: record ----------
        System.out.println("\n=== Exercise 2: Student record ===");
        Student s1 = new Student("Tendai", 20);
        Student s2 = new Student("Tendai", 20);
        Student s3 = new Student("Rudo", 22);
        System.out.println(s1);                          // toString() is generated automatically
        System.out.println("s1.equals(s2)? " + s1.equals(s2)); // true: same component values
        System.out.println("s1 == s2? " + (s1 == s2));         // false: different objects
        System.out.println("s1.equals(s3)? " + s1.equals(s3)); // false
        System.out.println("s1.name() = " + s1.name());       // read a component via its accessor

        // ---------- Exercise 3: equals/hashCode and HashSet ----------
        System.out.println("\n=== Exercise 3: Book equals/hashCode ===");
        Set<Book> books = new HashSet<>();
        System.out.println("add Java Basics:           " + books.add(new Book("111", "Java Basics")));
        System.out.println("add Data Structures:       " + books.add(new Book("222", "Data Structures")));
        // Same ISBN as the first book, even though the title differs, so it is a "duplicate"
        System.out.println("add duplicate ISBN 111:    " + books.add(new Book("111", "Java Basics (2nd copy)")));
        System.out.println("Set size: " + books.size()); // 2, the duplicate was rejected
        System.out.println("Books in the set: " + books);

        // CONTRAST: the same test with a class that has NO equals/hashCode
        Set<PlainBook> plain = new HashSet<>();
        plain.add(new PlainBook("111"));
        plain.add(new PlainBook("111"));
        System.out.println("Set size without equals/hashCode: " + plain.size()); // 2, duplicates accepted
        System.out.println("PlainBooks in the set: " + plain);

        // ---------- Exercise 4: custom LinkedList with an Iterator ----------
        System.out.println("\n=== Exercise 4: LinkedList with for-each ===");
        MyLinkedList<String> list = new MyLinkedList<>();
        list.add("one");
        list.add("two");
        list.add("three");
        for (String item : list) { // possible only because MyLinkedList implements Iterable
            System.out.println("item: " + item);
        }
        System.out.println("size: " + list.size());

        // ---------- Exercise 5: Coin enum, fewest coins ----------
        System.out.println("\n=== Exercise 5: Fewest coins ===");
        for (int amount : new int[] {68, 41, 99, 5}) {
            Map<Coin, Integer> result = Coin.fewestCoins(amount);
            int totalCoins = 0;
            for (int count : result.values()) {
                totalCoins += count;
            }
            System.out.println(amount + " cents -> " + result + " (" + totalCoins + " coins)");
        }
    }

    // ============================================================
    // Exercise 1: enum + switch
    // ============================================================
    // An enum is a fixed set of named constants, safer than using 0/1/2 or strings.
    enum Level { LOW, MEDIUM, HIGH }

    static void advice(Level level) {
        // Arrow-style switch: no "break" needed, no fall-through
        switch (level) {
            case LOW    -> System.out.println("LOW: all is calm, keep going as you are.");
            case MEDIUM -> System.out.println("MEDIUM: stay alert and keep an eye on things.");
            case HIGH   -> System.out.println("HIGH: act now and take immediate action!");
        }
        // Because level is an enum, the compiler knows the possible values.
    }

    // ============================================================
    // Exercise 2: record
    // ============================================================
    // One line gives us: private final fields, a constructor, accessors (name(), age()),
    // toString(), equals() and hashCode(). Records are immutable data carriers.
    record Student(String name, int age) { }

    // ============================================================
    // Exercise 3: equals() and hashCode() by ISBN
    // ============================================================
    static class Book {
        private final String isbn;
        private final String title;

        Book(String isbn, String title) {
            this.isbn = isbn;
            this.title = title;
        }

        // Two books are equal when their ISBNs are equal (the title is ignored)
        @Override public boolean equals(Object o) {
            if (this == o) return true;                       // same object
            if (o == null || getClass() != o.getClass()) return false; // different type
            Book other = (Book) o;
            return isbn.equals(other.isbn);
        }

        // RULE: equal objects MUST have equal hash codes, so hash the same field
        // that equals() uses. A HashSet uses hashCode() to pick a bucket, then equals()
        // to check for a duplicate in that bucket. Without BOTH methods it fails.
        @Override public int hashCode() {
            return Objects.hash(isbn);
        }

        // Uses both fields, so they are really "used" (and the set prints nicely)
        @Override public String toString() {
            return title + " (ISBN " + isbn + ")";
        }
    }

    // A class WITHOUT equals/hashCode: it inherits Object's versions, which compare
    // object identity (memory address), so two "same" ISBNs count as different.
    static class PlainBook {
        private final String isbn;

        PlainBook(String isbn) {
            this.isbn = isbn;
        }

        @Override public String toString() {
            return "PlainBook(ISBN " + isbn + ")";
        }
    }

    // ============================================================
    // Exercise 4: LinkedList with private static Node and inner Iterator
    // ============================================================
    // "implements Iterable<T>" is what allows the for-each loop on this class
    static class MyLinkedList<T> implements Iterable<T> {

        // "static" nested class: a Node does not need access to any list instance,
        // so making it static avoids a hidden reference to the outer list.
        // "private": only the list itself should know about nodes.
        private static class Node<T> {
            T value;
            Node<T> next;

            Node(T value) {
                this.value = value;
            }
        }

        private Node<T> head; // first node
        private Node<T> tail; // last node (makes add() fast)
        private int size;

        void add(T value) {
            Node<T> node = new Node<>(value);
            if (head == null) {      // empty list: the new node is first and last
                head = node;
                tail = node;
            } else {
                tail.next = node;    // link after the current last node
                tail = node;
            }
            size++;
        }

        int size() {
            return size;
        }

        @Override public Iterator<T> iterator() {
            return new ListIterator();
        }

        // "inner" (non-static) class: each iterator belongs to ONE list and reads
        // that list's head directly through the hidden link to the outer object.
        private class ListIterator implements Iterator<T> {
            private Node<T> current = head; // start at the first node

            @Override public boolean hasNext() {
                return current != null;
            }

            @Override public T next() {
                if (current == null) {
                    throw new NoSuchElementException();
                }
                T value = current.value;
                current = current.next;   // move forward for the next call
                return value;
            }
        }
    }

    // ============================================================
    // Exercise 5: Coin enum with fields and a method
    // ============================================================
    // Enum constants can carry data. Constants are listed in order, smallest first.
    enum Coin {
        PENNY(1), NICKEL(5), DIME(10), QUARTER(25);

        private final int value; // each constant has its own value

        Coin(int value) {        // enum constructors are implicitly private
            this.value = value;
        }

        // Greedy method: always use as many of the LARGEST coin as possible, then the
        // next largest, and so on. For this coin system (1, 5, 10, 25) greedy is
        // guaranteed to give the fewest coins. (It is NOT guaranteed for every system.)
        static Map<Coin, Integer> fewestCoins(int amount) {
            if (amount < 0) {
                throw new IllegalArgumentException("Amount cannot be negative");
            }
            Map<Coin, Integer> result = new EnumMap<>(Coin.class); // keeps enum order
            Coin[] all = values();
            for (int i = all.length - 1; i >= 0; i--) { // largest to smallest
                int count = amount / all[i].value;      // how many of this coin fit
                if (count > 0) {
                    result.put(all[i], count);
                    amount -= count * all[i].value;     // what is left to pay
                }
            }
            return result;
        }
    }
}
