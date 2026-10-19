import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// ============================================================
// Main class: runs a demo of every exercise
// (kept first in the file so "java Lab3.java" also works)
// ============================================================
public class Lab3 {
    public static void main(String[] args) {

        // ---------- Exercises 1 & 2: Student ----------
        System.out.println("=== Exercises 1 & 2: Student ===");
        Student s = new Student("Tendai", 85);
        System.out.println(s.getName() + " has " + s.getMarks() + " marks -> grade " + s.getGrade());

        s.setMarks(150); // invalid: rejected, marks stay at 85
        System.out.println("After setMarks(150): marks = " + s.getMarks());

        s.setMarks(45);  // valid
        System.out.println("After setMarks(45): marks = " + s.getMarks() + " -> grade " + s.getGrade());

        // ---------- Exercise 3: BankAccount with history ----------
        System.out.println("\n=== Exercise 3: BankAccount history ===");
        BankAccount acc = new BankAccount(100.0);
        acc.deposit(50.0);
        acc.withdraw(30.0);
        acc.withdraw(500.0); // fails: insufficient funds (still recorded in history)

        List<String> history = acc.getTransactionHistory();
        for (String entry : history) {
            System.out.println("  " + entry);
        }
        try {
            history.add("HACK: deposit 1000000"); // the copy is unmodifiable
        } catch (UnsupportedOperationException e) {
            System.out.println("Cannot modify the history from outside (UnsupportedOperationException).");
        }

        // ---------- Exercise 4: ImmutablePerson ----------
        System.out.println("\n=== Exercise 4: ImmutablePerson ===");
        List<String> hobbies = new ArrayList<>(List.of("football", "chess"));
        ImmutablePerson p = new ImmutablePerson("Rudo", 20, hobbies);
        hobbies.add("gaming"); // changing the ORIGINAL list afterwards...
        System.out.println(p.getName() + ", " + p.getAge() + ", hobbies: " + p.getHobbies());
        // ...does not affect the person, because the constructor made its own copy
        // p.getName() cannot be changed: there is no setter and the field is final

        // ---------- Exercise 5: KelvinTemperature stored in kelvin ----------
        System.out.println("\n=== Exercise 5: KelvinTemperature ===");
        TemperatureReading kelvinBased = KelvinTemperature.fromCelsius(25);
        TemperatureReading celsiusBased = new TemperatureCelsiusInside(25);

        // The SAME caller code (describe) works with both internal units and gives
        // identical results, so changing the internal unit would not break callers.
        System.out.println("Kelvin inside:  " + describe(kelvinBased));
        System.out.println("Celsius inside: " + describe(celsiusBased));

        // Values below absolute zero (-273.15 C) are rejected
        try {
            KelvinTemperature.fromCelsius(-300);
        } catch (IllegalArgumentException e) {
            System.out.println("Rejected: " + e.getMessage());
        }
    }

    // A "caller": it only uses getCelsius() and getFahrenheit(), never the internal unit
    static String describe(TemperatureReading t) {
        return t.getCelsius() + " C, " + t.getFahrenheit() + " F";
    }
}

// ============================================================
// Exercises 1 & 2: Student
// ============================================================
class Student {
    // private = outside code cannot touch these directly (encapsulation)
    private String name;
    private int marks;

    Student(String name, int marks) {
        this.name = name;
        setMarks(marks); // reuse the setter so the constructor is validated too
    }

    String getName() {
        return name;
    }

    int getMarks() {
        return marks;
    }

    // Setter with validation: marks must be between 0 and 100
    void setMarks(int marks) {
        if (marks < 0 || marks > 100) {
            System.out.println("Rejected invalid marks: " + marks + " (must be 0-100)");
            return; // keep the old value
        }
        this.marks = marks;
    }

    // Exercise 2: the grade is CALCULATED from marks each time, not stored in a field.
    // This means the grade can never get out of sync with the marks.
    // (The exercise does not give thresholds, so I assumed: A>=80, B>=60, C>=40, else F.)
    char getGrade() {
        if (marks >= 80) return 'A';
        if (marks >= 60) return 'B';
        if (marks >= 40) return 'C';
        return 'F';
    }
}

// ============================================================
// Exercise 3: BankAccount with a transaction history
// ============================================================
class BankAccount {
    private double balance;
    // The history is private so nobody outside can edit it directly
    private final List<String> history = new ArrayList<>();

    BankAccount(double openingBalance) {
        this.balance = openingBalance;
        history.add("Opened with " + openingBalance);
    }

    void deposit(double amount) {
        balance += amount;
        history.add("Deposit " + amount + " -> balance " + balance);
    }

    void withdraw(double amount) {
        if (amount > balance) {
            history.add("FAILED withdrawal " + amount + " (insufficient funds)");
            return;
        }
        balance -= amount;
        history.add("Withdraw " + amount + " -> balance " + balance);
    }

    // Returns a COPY wrapped as unmodifiable:
    //  - new ArrayList<>(history) -> a separate list, so later changes to the real
    //    history do not show up in a list the caller is already holding
    //  - Collections.unmodifiableList -> the caller cannot add or remove entries
    List<String> getTransactionHistory() {
        return Collections.unmodifiableList(new ArrayList<>(history));
    }
}

// ============================================================
// Exercise 4: ImmutablePerson
// ============================================================
// DESIGN CHOICES:
// 1. "final class"        -> nobody can subclass it and add mutable behaviour.
// 2. "private" fields     -> outside code cannot read or write them directly.
// 3. "final" fields       -> each can be assigned only once, in the constructor.
// 4. No setters           -> there is no method that changes the state.
// 5. Defensive copy of the list (List.copyOf) in the constructor, so the caller's
//    original list cannot be used to change our data later.
// 6. getHobbies() returns an unmodifiable list, so callers cannot change it either.
//    (String and int are already immutable, so they need no copying.)
final class ImmutablePerson {
    private final String name;
    private final int age;
    private final List<String> hobbies;

    ImmutablePerson(String name, int age, List<String> hobbies) {
        this.name = name;
        this.age = age;
        this.hobbies = List.copyOf(hobbies); // private, unmodifiable copy
    }

    String getName() {
        return name;
    }

    int getAge() {
        return age;
    }

    List<String> getHobbies() {
        return hobbies; // already unmodifiable, so safe to return directly
    }
}

// ============================================================
// Exercise 5: KelvinTemperature (stored in kelvin)
// ============================================================

// The PUBLIC contract that callers depend on. They never see the internal unit.
interface TemperatureReading {
    double getCelsius();
    double getFahrenheit();
}

class KelvinTemperature implements TemperatureReading {
    private final double kelvin; // internal unit: kelvin

    private KelvinTemperature(double kelvin) {
        this.kelvin = kelvin;
    }

    // Factory: converts Celsius to kelvin, and rejects values below absolute zero (0 K)
    static KelvinTemperature fromCelsius(double c) {
        double k = c + 273.15;
        if (k < 0) {
            throw new IllegalArgumentException("Below absolute zero: " + c + " C");
        }
        return new KelvinTemperature(k);
    }

    @Override
    public double getCelsius() {
        return kelvin - 273.15;
    }

    @Override
    public double getFahrenheit() {
        return getCelsius() * 9.0 / 5.0 + 32;
    }
}

// SAME public behaviour, but the internal unit is now CELSIUS instead of kelvin.
// Any caller written against getCelsius()/getFahrenheit() keeps working unchanged,
// because the internal field is private. That is the benefit of encapsulation.
class TemperatureCelsiusInside implements TemperatureReading {
    private final double celsius; // different internal unit

    TemperatureCelsiusInside(double celsius) {
        if (celsius + 273.15 < 0) {
            throw new IllegalArgumentException("Below absolute zero: " + celsius + " C");
        }
        this.celsius = celsius;
    }

    @Override
    public double getCelsius() {
        return celsius;
    }

    @Override
    public double getFahrenheit() {
        return celsius * 9.0 / 5.0 + 32;
    }
}
