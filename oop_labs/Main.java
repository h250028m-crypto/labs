import java.time.Year;

// ============================================================
// Main class: runs a demo of every exercise
// ============================================================
public class Main {
    public static void main(String[] args) {

        // ---------- Exercise 1: create three cars and print them ----------
        System.out.println("=== Exercise 1: Cars ===");
        Car c1 = new Car("Toyota", 2018, 85000);
        Car c2 = new Car("Mercedes", 1995, 210000);
        Car c3 = new Car("Honda", 1988, 175000);
        c1.display();
        c2.display();
        c3.display();

        // ---------- Exercise 2: test isAntique() ----------
        System.out.println("\n=== Exercise 2: isAntique() ===");
        System.out.println(c1.brand + " antique? " + c1.isAntique()); // false
        System.out.println(c2.brand + " antique? " + c2.isAntique()); // true (2026 - 1995 = 31)
        System.out.println(c3.brand + " antique? " + c3.isAntique()); // true

        // ---------- Exercise 3: array of 5 rectangles, find the largest area ----------
        System.out.println("\n=== Exercise 3: Largest rectangle ===");
        Rectangle[] rects = {
            new Rectangle(2, 3),
            new Rectangle(5, 4),
            new Rectangle(10, 2),
            new Rectangle(6, 6),
            new Rectangle(1, 9)
        };

        Rectangle largest = rects[0]; // assume the first one is the largest
        for (Rectangle r : rects) {   // check every rectangle
            if (r.area() > largest.area()) {
                largest = r;          // found a bigger one, remember it
            }
        }
        System.out.println("Largest: width=" + largest.width + ", height=" + largest.height
                + ", area=" + largest.area() + ", perimeter=" + largest.perimeter());

        // ---------- Exercise 4: reference assignment ----------
        System.out.println("\n=== Exercise 4: Aliasing ===");
        Rectangle r1 = new Rectangle(4, 5);
        Rectangle r2 = r1;   // copies the REFERENCE, not the object: r1 and r2 point to the same object
        r2.width = 10;       // changes the one shared object
        System.out.println("r1.width = " + r1.width); // prints 10.0, NOT 4.0
        // Explanation: "Rectangle r2 = r1;" does not create a new Rectangle.
        // Both variables hold the same memory address, so a change made through
        // r2 is visible through r1. To get an independent copy you would write
        // Rectangle r2 = new Rectangle(r1.width, r1.height);

        // ---------- Exercise 5: chained dot expression and null ----------
        System.out.println("\n=== Exercise 5: Person and Address ===");
        Person withAddress = new Person("Tendai", new Address("Harare"));
        System.out.println("City: " + withAddress.address.city); // chained dot: person -> address -> city

        Person noAddress = new Person("Rudo", null); // this person has no address
        try {
            // address is null, so ".city" on null throws a NullPointerException
            System.out.println("City: " + noAddress.address.city);
        } catch (NullPointerException e) {
            System.out.println("NullPointerException caught: address is null, so it has no city.");
        }

        // Safe way: check for null before using the chain
        if (noAddress.address != null) {
            System.out.println("City: " + noAddress.address.city);
        } else {
            System.out.println("Safe check: " + noAddress.name + " has no address.");
        }
    }
}

// ============================================================
// Exercise 1 & 2: Car class
// ============================================================
class Car {
    // Fields (instance variables) that describe a car
    String brand;
    int year;
    int mileage;

    // Constructor: runs when we write "new Car(...)" and sets the fields
    Car(String brand, int year, int mileage) {
        this.brand = brand;     // "this" refers to the object being created
        this.year = year;
        this.mileage = mileage;
    }

    // Exercise 1: prints the car's details
    void display() {
        System.out.println(brand + " | year: " + year + " | mileage: " + mileage + " km");
    }

    // Exercise 2: returns true if the car is more than 25 years old
    boolean isAntique() {
        int currentYear = Year.now().getValue(); // gets the current year automatically
        int age = currentYear - year;           // age of the car in years
        return age > 25;                        // true only when strictly older than 25
    }
}

// ============================================================
// Exercise 3 & 4: Rectangle class
// ============================================================
class Rectangle {
    // Fields are not private so Exercise 4 can do r2.width directly
    double width;
    double height;

    // Constructor sets width and height
    Rectangle(double width, double height) {
        this.width = width;
        this.height = height;
    }

    // Area = width * height
    double area() {
        return width * height;
    }

    // Perimeter = 2 * (width + height)
    double perimeter() {
        return 2 * (width + height);
    }
}

// ============================================================
// Exercise 5: Address and Person classes
// ============================================================
class Address {
    String city;

    Address(String city) {
        this.city = city;
    }
}

class Person {
    String name;
    Address address; // A Person HAS an Address (composition). Defaults to null.

    Person(String name, Address address) {
        this.name = name;
        this.address = address;
    }
}

