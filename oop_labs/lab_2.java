// ============================================================
// Main class: runs a demo of every exercise
// (kept first in the file so "java Lab2.java" also works)
// ============================================================
public class lab_2 {
    public static void main(String[] args) {

        // ---------- Exercises 1 & 2: Circle and the static counter ----------
        System.out.println("=== Exercises 1 & 2: Circle ===");
        System.out.println("Circles before creating any: " + Circle.getCount()); // 0
        Circle c1 = new Circle(5.0);   // constructor with radius
        Circle c2 = new Circle();      // no-arg constructor, radius defaults to 1.0
        System.out.println("c1 radius = " + c1.getRadius());
        System.out.println("c2 radius = " + c2.getRadius());
        // The static getter is called on the CLASS, not on an object
        System.out.println("Circles created: " + Circle.getCount()); // 2

        // ---------- Exercise 3: Employee ----------
        System.out.println("\n=== Exercise 3: Employee ===");
        Employee e1 = new Employee("Tendai");                          // 1 argument
        Employee e2 = new Employee("Rudo", "IT");                      // 2 arguments
        Employee e3 = new Employee("Farai", "Finance", 1500.0);        // 3 arguments
        Employee e4 = new Employee(e3);                                // copy constructor
        System.out.println(e1);
        System.out.println(e2);
        System.out.println(e3);
        System.out.println(e4); // same data as e3 but a NEW id

        // ---------- Exercise 4: static and instance blocks ----------
        System.out.println("\n=== Exercise 4: Initialization blocks ===");
        // PREDICTION (before running):
        //   "main: before first object"
        //   Static block 1      <- static blocks run ONCE, when the class is first used,
        //   Static block 2         in the order they are written
        //   Instance block 1    <- for EACH new object: instance blocks run first (in order),
        //   Instance block 2
        //   Constructor         <- then the constructor body
        //   (second object:)  Instance block 1, Instance block 2, Constructor
        //   (static blocks do NOT run again)
        System.out.println("main: before first object");
        new InitDemo();
        System.out.println("main: before second object");
        new InitDemo();

        // ---------- Exercise 5: Temperature with static factories ----------
        System.out.println("\n=== Exercise 5: Temperature ===");
        // Temperature t = new Temperature(25);   // would NOT compile: constructor is private
        Temperature t1 = Temperature.fromCelsius(100);
        Temperature t2 = Temperature.fromFahrenheit(32);
        System.out.println("100 C = " + t1.toFahrenheit() + " F");
        System.out.println("32 F  = " + t2.toCelsius() + " C");
    }
}

// ============================================================
// Exercises 1 & 2: Circle
// ============================================================
class Circle {
    // static = ONE shared copy for the whole class (not one per object)
    private static int count = 0;

    private double radius; // each circle has its own radius

    // Constructor taking a radius
    Circle(double radius) {
        this.radius = radius;
        count++; // a new circle exists, so increase the shared counter
    }

    // No-arg constructor: defaults radius to 1.0 by calling the other constructor
    Circle() {
        this(1.0); // this(...) reuses the constructor above (which also does count++)
    }

    double getRadius() {
        return radius;
    }

    // Static getter: lets us read the counter without needing an object
    static int getCount() {
        return count;
    }
}

// ============================================================
// Exercise 3: Employee
// ============================================================
class Employee {
    // Shared counter: holds the id that the NEXT employee will receive
    private static int nextId = 1;

    private final int employeeId; // set once, never changed
    private String name;
    private String department;
    private double salary;

    // Constructor 1: name only (other fields get default values)
    Employee(String name) {
        this(name, "Unassigned", 0.0); // chain to the full constructor
    }

    // Constructor 2: name and department
    Employee(String name, String department) {
        this(name, department, 0.0);
    }

    // Constructor 3: the "full" constructor that does the real work
    Employee(String name, String department, double salary) {
        this.employeeId = nextId++; // use the current id, then increase the counter
        this.name = name;
        this.department = department;
        this.salary = salary;
    }

    // Copy constructor: builds a new Employee from an existing one.
    // It copies the data but gets a NEW id so ids stay unique.
    Employee(Employee other) {
        this(other.name, other.department, other.salary);
    }

    @Override
    public String toString() {
        return "Employee #" + employeeId + " | " + name + " | " + department + " | salary: " + salary;
    }
}

// ============================================================
// Exercise 4: static blocks and instance blocks
// ============================================================
class InitDemo {
    // Static block 1: runs once, when the class is first loaded
    static {
        System.out.println("Static block 1");
    }

    // Instance block 1: runs every time an object is created, before the constructor
    {
        System.out.println("Instance block 1");
    }

    // Constructor: runs after all instance blocks
    InitDemo() {
        System.out.println("Constructor");
    }

    // Static block 2: runs once, right after static block 1 (textual order)
    static {
        System.out.println("Static block 2");
    }

    // Instance block 2: runs after instance block 1, before the constructor
    {
        System.out.println("Instance block 2");
    }
}

// ============================================================
// Exercise 5: Temperature with static factory methods
// ============================================================
class Temperature {
    private final double celsius; // we always store the value in Celsius

    // Private constructor: outsiders must use the factory methods below
    private Temperature(double celsius) {
        this.celsius = celsius;
    }

    // Factory method: the name says the input is in Celsius
    static Temperature fromCelsius(double c) {
        return new Temperature(c);
    }

    // Factory method: converts Fahrenheit to Celsius, then builds the object
    static Temperature fromFahrenheit(double f) {
        return new Temperature((f - 32) * 5.0 / 9.0);
    }

    double toCelsius() {
        return celsius;
    }

    double toFahrenheit() {
        return celsius * 9.0 / 5.0 + 32;
    }

    
    
}
