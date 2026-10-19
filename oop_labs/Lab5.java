
public class Lab5 {

    public static void main(String[] args) {

        // ---------- Exercise 1: method overloading ----------
        System.out.println("=== Exercise 1: Overloaded print ===");
        print(42);                    // int     -> print(int)
        print(3.14);                  // double  -> print(double)
        print("hello");               // String  -> print(String)
        print(new int[] {1, 2, 3});   // int[]   -> print(int[])
        // Java picks the version at COMPILE time from the argument types.

        // ---------- Exercise 2: overriding and polymorphism ----------
        System.out.println("\n=== Exercise 2: Animals ===");
        Animal[] animals = { new Cat(), new Cow(), new Duck() };
        for (Animal a : animals) {
            // The variable type is Animal, but Java runs the sound() of the REAL object
            System.out.println(a.getClass().getSimpleName() + " says: " + a.sound());
        }

        // ---------- Exercise 3: employee hierarchy and payroll ----------
        System.out.println("\n=== Exercise 3: Payroll ===");
        Employee[] staff = {
            new Manager("Tendai", 3000, 800),
            new Developer("Rudo", 2500),
            new Intern("Farai", 400)       // new subclass, added with NO change to the loop
        };
        System.out.println("Total payroll: " + totalPayroll(staff)); // 3800 + 2500 + 400 = 6700

        // ---------- Exercise 4: fields vs methods through a superclass reference ----------
        System.out.println("\n=== Exercise 4: Field vs method ===");
        Parent ref = new Child(); // superclass reference, subclass object
        System.out.println("ref.name   -> " + ref.name);    // FIELD:  Parent's version
        System.out.println("ref.who()  -> " + ref.who());    // METHOD: Child's version
        Child real = new Child();
        System.out.println("real.name  -> " + real.name);    // Child reference sees Child's field

        // ---------- Exercise 5: instanceof design vs polymorphic design ----------
        System.out.println("\n=== Exercise 5: Payments ===");
        Payment[] payments = {
            new CardPayment(100, "1234-5678"),
            new MobileMoneyPayment(50, "+263771234567"),
            new CashPayment(20)  // a NEW type
        };
        System.out.println("-- Design 1: instanceof --");
        for (Payment p : payments) {
            PaymentProcessor.pay(p);
        }
        System.out.println("-- Design 2: polymorphic process() --");
        for (Payment p : payments) {
            PaymentProcessor2.pay(p);
        }
    }

    // ============================================================
    // Exercise 1: overloaded methods (same name, different parameter types)
    // ============================================================
    static void print(int x)      { System.out.println("int: " + x); }
    static void print(double x)   { System.out.println("double: " + x); }
    static void print(String x)   { System.out.println("String: " + x); }
    static void print(int[] x) {
        System.out.print("int[]: ");
        for (int v : x) {
            System.out.print(v + " ");
        }
        System.out.println();
    }

    // ============================================================
    // Exercise 2: Animal and subclasses
    // ============================================================
    static class Animal {
        String sound() {
            return "...";
        }
    }

    // @Override asks the compiler to check we really are overriding a parent method
    static class Cat extends Animal {
        @Override String sound() { return "Meow"; }
    }

    static class Cow extends Animal {
        @Override String sound() { return "Moo"; }
    }

    static class Duck extends Animal {
        @Override String sound() { return "Quack"; }
    }

    // ============================================================
    // Exercise 3: Employee hierarchy (the exercise says "Lab 4", but Lab 4 had no
    // employees, so this builds a small Employee hierarchy from scratch)
    // ============================================================
    static class Employee {
        protected String name;
        protected double baseSalary;

        Employee(String name, double baseSalary) {
            this.name = name;
            this.baseSalary = baseSalary;
        }

        // Subclasses override this to change how pay is calculated
        double calculatePay() {
            return baseSalary;
        }
    }

    static class Manager extends Employee {
        private double bonus;

        Manager(String name, double baseSalary, double bonus) {
            super(name, baseSalary);
            this.bonus = bonus;
        }

        @Override double calculatePay() {
            return baseSalary + bonus; // manager = salary + bonus
        }
    }

    static class Developer extends Employee {
        Developer(String name, double baseSalary) {
            super(name, baseSalary);
        }
        // inherits calculatePay() unchanged
    }

    // The NEW class: an intern gets a fixed stipend
    static class Intern extends Employee {
        Intern(String name, double stipend) {
            super(name, stipend);
        }

        @Override double calculatePay() {
            return baseSalary; // stipend only, no extras
        }
    }

    // THE PAYROLL LOOP: it only knows about Employee. It never mentions Manager,
    // Developer or Intern, so adding Intern did not require changing it.
    static double totalPayroll(Employee[] staff) {
        double total = 0;
        for (Employee e : staff) {
            total += e.calculatePay(); // runs the right version for each real object
        }
        return total;
    }

    // ============================================================
    // Exercise 4: Parent and Child with the same field name and method name
    // ============================================================
    static class Parent {
        String name = "Parent field";
        String who() { return "Parent method"; }
    }

    static class Child extends Parent {
        String name = "Child field";                  // HIDES Parent.name (not overriding)
        @Override String who() { return "Child method"; } // OVERRIDES Parent.who()
    }

    // ============================================================
    // Exercise 5: Payments
    // ============================================================
    static class Payment {
        protected double amount;

        Payment(double amount) {
            this.amount = amount;
        }

        // Used only by Design 2. Each subclass says how IT is processed.
        void process() {
            throw new UnsupportedOperationException("process() not implemented");
        }
    }

    static class CardPayment extends Payment {
        String cardNumber;

        CardPayment(double amount, String cardNumber) {
            super(amount);
            this.cardNumber = cardNumber;
        }

        @Override void process() {
            System.out.println("Charging card " + cardNumber + " for " + amount);
        }
    }

    static class MobileMoneyPayment extends Payment {
        String phone;

        MobileMoneyPayment(double amount, String phone) {
            super(amount);
            this.phone = phone;
        }

        @Override void process() {
            System.out.println("Sending mobile money request to " + phone + " for " + amount);
        }
    }

    // A new payment type, added later, to compare how the two designs cope
    static class CashPayment extends Payment {
        CashPayment(double amount) {
            super(amount);
        }

        @Override void process() {
            System.out.println("Taking cash: " + amount);
        }
    }

    // DESIGN 1: the processor checks the type itself using instanceof pattern matching.
    // "p instanceof CardPayment c" tests the type AND creates a typed variable c.
    static class PaymentProcessor {
        static void pay(Payment p) {
            if (p instanceof CardPayment c) {
                System.out.println("Charging card " + c.cardNumber + " for " + c.amount);
            } else if (p instanceof MobileMoneyPayment m) {
                System.out.println("Sending mobile money request to " + m.phone + " for " + m.amount);
            } else {
                // CashPayment lands here: the processor was never updated for it
                System.out.println("Unsupported payment type: " + p.getClass().getSimpleName());
            }
        }
    }

    // DESIGN 2: the processor just calls process(); each payment knows how to handle itself.
    static class PaymentProcessor2 {
        static void pay(Payment p) {
            p.process(); // polymorphism picks the right version at run time
        }
    }

   
}
