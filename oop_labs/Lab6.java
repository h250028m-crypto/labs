import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Lab6 {

    public static void main(String[] args) {

        // ---------- Exercise 1: abstract Shape ----------
        System.out.println("=== Exercise 1: Shapes ===");
        // Shape s = new Shape();   // would NOT compile: abstract classes cannot be instantiated
        Shape[] shapes = { new Circle(2), new Rectangle(3, 4), new Square(5) };
        for (Shape s : shapes) {
            System.out.printf("%s -> area: %.2f, perimeter: %.2f%n",
                    s.getClass().getSimpleName(), s.area(), s.perimeter());
        }

        // ---------- Exercise 2: interface Flyable ----------
        System.out.println("\n=== Exercise 2: Flyable ===");
        // Bird and Aeroplane have no common superclass, but both are Flyable
        Flyable[] flyers = { new Bird(), new Aeroplane() };
        for (Flyable f : flyers) {
            f.fly();
        }

        // ---------- Exercise 3: abstract Account ----------
        System.out.println("\n=== Exercise 3: Accounts ===");
        Account[] accounts = { new SavingsAccount(1000), new CurrentAccount(1000) };
        for (Account a : accounts) {
            a.deposit(200); // concrete method, shared by all accounts
            System.out.println(a.getClass().getSimpleName() + ": balance " + a.getBalance()
                    + ", monthly fee " + a.monthlyFee());
        }

        // ---------- Exercise 4: Comparable and Comparator ----------
        System.out.println("\n=== Exercise 4: Sorting students ===");
        List<Student> students = new ArrayList<>();
        students.add(new Student("Rudo", 72));
        students.add(new Student("Tendai", 55));
        students.add(new Student("Farai", 91));

        Collections.sort(students); // uses compareTo(): natural order = by name
        System.out.println("By name:  " + students);

        students.sort(new MarksComparator()); // uses the Comparator: by marks (ascending)
        System.out.println("By marks: " + students);

        // The same Comparator written as a one-line lambda (descending marks):
        students.sort((a, b) -> Integer.compare(b.marks, a.marks));
        System.out.println("By marks (highest first): " + students);

        // ---------- Exercise 5: conflicting default methods ----------
        System.out.println("\n=== Exercise 5: Default method conflict ===");
        new Greeter().hello();
    }

    // ============================================================
    // Exercise 1: abstract class with abstract methods
    // ============================================================
    static abstract class Shape {
        // No body here: every concrete subclass MUST implement these
        abstract double area();
        abstract double perimeter();
    }

    static class Circle extends Shape {
        private double radius;

        Circle(double radius) { this.radius = radius; }

        @Override double area()      { return Math.PI * radius * radius; }
        @Override double perimeter() { return 2 * Math.PI * radius; }
    }

    static class Rectangle extends Shape {
        private double width, height;

        Rectangle(double width, double height) {
            this.width = width;
            this.height = height;
        }

        @Override double area()      { return width * height; }
        @Override double perimeter() { return 2 * (width + height); }
    }

    static class Square extends Shape {
        private double side;

        Square(double side) { this.side = side; }

        @Override double area()      { return side * side; }
        @Override double perimeter() { return 4 * side; }
    }

    // ============================================================
    // Exercise 2: interface for unrelated classes
    // ============================================================
    // An interface is a CONTRACT ("can do"), not a family ("is a").
    // Unrelated classes can promise the same ability.
    interface Flyable {
        void fly(); // implicitly public and abstract
    }

    static class Bird implements Flyable {
        @Override public void fly() { // interface methods are public, so the override must be too
            System.out.println("The bird flaps its wings and flies.");
        }
    }

    static class Aeroplane implements Flyable {
        @Override public void fly() {
            System.out.println("The aeroplane takes off using its engines.");
        }
    }

    // ============================================================
    // Exercise 3: abstract class mixing concrete and abstract methods
    // ============================================================
    static abstract class Account {
        protected double balance;

        Account(double openingBalance) { this.balance = openingBalance; }

        // CONCRETE: the same for every account type, written once here
        void deposit(double amount) {
            if (amount > 0) {
                balance += amount;
            }
        }

        double getBalance() { return balance; }

        // ABSTRACT: each account type charges a different fee
        abstract double monthlyFee();
    }

    static class SavingsAccount extends Account {
        SavingsAccount(double openingBalance) { super(openingBalance); }

        @Override double monthlyFee() { return 0.0; } // savings: no fee
    }

    static class CurrentAccount extends Account {
        CurrentAccount(double openingBalance) { super(openingBalance); }

        @Override double monthlyFee() { return 10.0; } // current: flat fee
    }

    // ============================================================
    // Exercise 4: Comparable (natural order) and Comparator (extra order)
    // ============================================================
    static class Student implements Comparable<Student> {
        String name;
        int marks;

        Student(String name, int marks) {
            this.name = name;
            this.marks = marks;
        }

        // Natural ordering: by name. Returns negative, zero or positive.
        @Override public int compareTo(Student other) {
            return this.name.compareTo(other.name);
        }

        @Override public String toString() {
            return name + "(" + marks + ")";
        }
    }

    // A separate class gives us another way to sort without changing Student.
    // (Comparable = ONE built-in order inside the class; Comparator = any number of
    // orders defined outside it.)
    static class MarksComparator implements Comparator<Student> {
        @Override public int compare(Student a, Student b) {
            return Integer.compare(a.marks, b.marks); // lowest marks first
        }
    }

    // ============================================================
    // Exercise 5: two interfaces with the same default method
    // ============================================================
    interface InterfaceA {
        default void hello() {
            System.out.println("Hello from InterfaceA");
        }
    }

    interface InterfaceB {
        default void hello() {
            System.out.println("Hello from InterfaceB");
        }
    }

    // Without the override below, this class would NOT compile: Java cannot decide
    // which hello() to inherit, so it refuses to guess.
    static class Greeter implements InterfaceA, InterfaceB {
        @Override public void hello() {
            InterfaceA.super.hello(); // explicitly choose A's version
            InterfaceB.super.hello(); // and B's version (we may call one, both, or neither)
        }
    }

}
