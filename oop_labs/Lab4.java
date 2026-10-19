import java.util.ArrayList;
import java.util.List;

// NOTE: all classes below are nested inside Lab4 (as "static" nested classes).
// This keeps them from clashing with classes of the same name (like Person or Car)
// that you may have compiled in earlier labs in the same folder.
public class Lab4 {

    public static void main(String[] args) {

        // ---------- Exercise 1: Person and Student ----------
        System.out.println("=== Exercise 1: Person and Student ===");
        Person p = new Person("Tendai", 40);
        Student s = new Student("Rudo", 20, "Computer Science");
        p.display(); // display() is defined in Person
        s.display(); // Student did NOT write display(); it INHERITS it from Person
        System.out.println("  (Student's own field) course: " + s.getCourse());

        // ---------- Exercise 2: Teacher shares the Person code ----------
        System.out.println("\n=== Exercise 2: Teacher ===");
        Teacher t = new Teacher("Farai", 45, "Mathematics");
        t.display(); // the same inherited display() method again
        // Student and Teacher are both Persons, so they share the same Person code:
        System.out.println("Student is a Person? " + (s instanceof Person));
        System.out.println("Teacher is a Person? " + (t instanceof Person));
        Person[] people = { p, s, t }; // one array can hold all three kinds
        for (Person each : people) {
            each.display();
        }

        // ---------- Exercise 3: multilevel inheritance and constructor order ----------
        System.out.println("\n=== Exercise 3: Constructor call order ===");
        new ElectricCar("Tesla", 4, 75);
        // TRACE: creating an ElectricCar first CALLS the Car constructor, which first
        // CALLS the Vehicle constructor. The bodies then finish from the top down:
        //   1. Vehicle constructor body
        //   2. Car constructor body
        //   3. ElectricCar constructor body
        // (Constructors are entered child -> parent, but their bodies run parent -> child.)

        // ---------- Exercise 4: why "Dog extends Animal" fails ----------
        System.out.println("\n=== Exercise 4: Missing super constructor ===");
        FixedDog d = new FixedDog("Rex");
        System.out.println("Created a dog named " + d.getName());

        // ---------- Exercise 5: Stack by inheritance vs by composition ----------
        System.out.println("\n=== Exercise 5: Stack ===");
        BadStack<String> bad = new BadStack<>();
        bad.push("A");
        bad.push("B");
        bad.push("C");
        bad.add(0, "HACK"); // allowed! inserts at the BOTTOM of the stack, breaking LIFO
        bad.remove(1);      // allowed! removes from the middle of the stack
        System.out.println("BadStack after illegal operations: " + bad);

        GoodStack<String> good = new GoodStack<>();
        good.push("A");
        good.push("B");
        good.push("C");
        // good.add(0, "HACK");  // would NOT compile: GoodStack has no add() method
        System.out.println("GoodStack pop: " + good.pop() + ", then pop: " + good.pop());
        System.out.println("GoodStack size: " + good.size());
    }

    // ============================================================
    // Exercises 1 & 2: Person, Student, Teacher
    // ============================================================
    static class Person {
        private String name;
        private int age;

        Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        // Written ONCE here, reused by every subclass
        void display() {
            System.out.println("Name: " + name + ", Age: " + age);
        }
    }

    // "extends Person" means Student gets name, age and display() for free
    static class Student extends Person {
        private String course; // the only thing Student adds

        Student(String name, int age, String course) {
            super(name, age); // calls the Person constructor to set name and age
            this.course = course;
        }

        String getCourse() {
            return course;
        }
    }

    static class Teacher extends Person {
        private String subject;

        Teacher(String name, int age, String subject) {
            super(name, age);
            this.subject = subject;
        }

        String getSubject() {
            return subject;
        }
    }

    // ============================================================
    // Exercise 3: Vehicle -> Car -> ElectricCar (multilevel)
    // ============================================================
    static class Vehicle {
        Vehicle(String brand) {
            System.out.println("1. Vehicle constructor (brand: " + brand + ")");
        }
    }

    static class Car extends Vehicle {
        Car(String brand, int doors) {
            super(brand); // must be the first line; runs the Vehicle constructor
            System.out.println("2. Car constructor (doors: " + doors + ")");
        }
    }

    static class ElectricCar extends Car {
        ElectricCar(String brand, int doors, int batteryKwh) {
            super(brand, doors); // runs the Car constructor (which runs Vehicle's)
            System.out.println("3. ElectricCar constructor (battery: " + batteryKwh + " kWh)");
        }
    }

    // ============================================================
    // Exercise 4: Animal and Dog
    // ============================================================
    static class Animal {
        private String name;

        // Animal has ONLY this constructor. Because we wrote one, Java does NOT
        // create a hidden no-argument constructor Animal() for us.
        Animal(String name) {
            this.name = name;
        }

        String getName() {
            return name;
        }
    }

    // THE BROKEN VERSION (does not compile, so it is commented out):
    //
    //     static class Dog extends Animal { }
    //
    // WHY IT FAILS:
    //  - Every constructor must call a parent constructor as its first step.
    //  - Dog has no constructor, so Java adds a hidden default one:  Dog() { super(); }
    //  - super() means "call Animal()", but Animal has no no-argument constructor.
    //  - Error: constructor Animal in class Animal cannot be applied to given types.
    //
    // THE FIX: write a Dog constructor that calls super(name) explicitly.
    static class FixedDog extends Animal {
        FixedDog(String name) {
            super(name); // tells Java which Animal constructor to use
        }
    }

    // ============================================================
    // Exercise 5: Stack by inheritance (bad) vs composition (good)
    // ============================================================

    // BAD DESIGN: a Stack "is an" ArrayList.
    // WHAT INHERITANCE WRONGLY EXPOSES: every ArrayList method becomes public on the
    // stack too: add(index, x), remove(index), get(index), set(index, x), clear(),
    // sort(...), and more. Anyone can insert or remove in the MIDDLE or at the BOTTOM,
    // which breaks the rule that a stack is last-in-first-out (LIFO). A Stack is not
    // truly an ArrayList; it only USES one.
    static class BadStack<T> extends ArrayList<T> {
        void push(T item) {
            add(item); // add to the end (the top)
        }

        T pop() {
            return remove(size() - 1); // remove from the end (the top)
        }
    }

    // GOOD DESIGN: a Stack "has an" ArrayList (composition).
    // The list is private, so only the methods we choose to write are available.
    static class GoodStack<T> {
        private final List<T> items = new ArrayList<>(); // hidden implementation detail

        void push(T item) {
            items.add(item);
        }

        T pop() {
            if (items.isEmpty()) {
                throw new RuntimeException("Stack is empty");
            }
            return items.remove(items.size() - 1);
        }

        int size() {
            return items.size();
        }
    }
}
