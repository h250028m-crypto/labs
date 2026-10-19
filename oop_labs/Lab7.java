import java.util.InputMismatchException;
import java.util.Scanner;

// NOTE: all classes are nested inside Lab7 so they cannot clash with same-named
// classes (like BankAccount) from earlier labs in the same folder.
public class Lab7 {

    public static void main(String[] args) {

        // ---------- Exercise 1: Scanner input, divide, handle bad input ----------
        System.out.println("=== Exercise 1: Divide two integers ===");
        // try-with-resources closes the Scanner automatically (no resource-leak warning)
        try (Scanner sc = new Scanner(System.in)) {
            System.out.print("Enter the first integer: ");
            int a = sc.nextInt();   // throws InputMismatchException if the input is not an int
            System.out.print("Enter the second integer: ");
            int b = sc.nextInt();
            System.out.println(a + " / " + b + " = " + (a / b)); // throws ArithmeticException if b is 0
        } catch (InputMismatchException e) {
            System.out.println("Error: that was not a whole number.");
        } catch (ArithmeticException e) {
            System.out.println("Error: cannot divide by zero.");
        }
        // Try these inputs: "10 2" (works), "10 0" (division error), "abc" (not numeric).
        // Note: integer division by zero throws an exception, but 10.0 / 0 with
        // doubles does NOT (it gives Infinity), so the catch only matters for ints.

        // ---------- Exercise 2: ArrayIndexOutOfBoundsException ----------
        System.out.println("\n=== Exercise 2: Array access ===");
        int[] numbers = {10, 20, 30};
        System.out.println("Element at 1: " + getElement(numbers, 1));
        System.out.println("Element at 5: " + getElement(numbers, 5)); // out of range

        // ---------- Exercise 3: checked custom exception ----------
        System.out.println("\n=== Exercise 3: validateAge ===");
        int[] ages = {25, -3, 150};
        for (int age : ages) {
            try {
                validateAge(age); // the compiler FORCES us to handle (or declare) the exception
                System.out.println("Age " + age + " is valid.");
            } catch (InvalidAgeException e) {
                System.out.println("Rejected: " + e.getMessage());
            }
        }

        // ---------- Exercise 4: withdraw throws an exception ----------
        System.out.println("\n=== Exercise 4: BankAccount withdraw ===");
        BankAccount account = new BankAccount(100);
        try {
            account.withdraw(30);   // fine
            account.withdraw(500);  // throws InsufficientFundsException
            System.out.println("This line is skipped because the exception jumped past it.");
        } catch (InsufficientFundsException e) {
            System.out.println("Withdrawal failed: " + e.getMessage());
        }
        System.out.println("Final balance: " + account.getBalance());

        // ---------- Exercise 5: return inside try, print inside finally ----------
        System.out.println("\n=== Exercise 5: try / finally ===");
        // PREDICTION for test1():
        //   "finally block runs"   <- printed FIRST, while test1() is still returning
        //   "test1 returned: from try"
        System.out.println("test1 returned: " + test1());
        // PREDICTION for test2(): returns 1, not 99 (see the explanation in test2)
        System.out.println("test2 returned: " + test2());
        // EXPLANATION: finally ALWAYS runs when the try block is left, even by return
        // or by an exception. Java first evaluates the return value and sets it aside,
        // then runs finally, then finishes the return. The only things that stop
        // finally are System.exit(), a JVM crash, or the thread being killed.
    }

    // ============================================================
    // Exercise 2: catch ArrayIndexOutOfBoundsException
    // ============================================================
    static int getElement(int[] arr, int index) {
        try {
            return arr[index]; // Java checks the index on every array access
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("  Error: index " + index + " is invalid (valid: 0 to "
                    + (arr.length - 1) + ")");
            return -1; // a "sentinel" value meaning "no element"
        }
    }

    // ============================================================
    // Exercise 3: checked exception and the method that throws it
    // ============================================================
    // Extending Exception (not RuntimeException) makes it CHECKED: callers must
    // catch it or declare "throws" in their own signature.
    static class InvalidAgeException extends Exception {
        InvalidAgeException(String message) {
            super(message);
        }
    }

    // "throws" in the signature warns callers that this method may throw it
    static void validateAge(int age) throws InvalidAgeException {
        if (age < 0 || age > 120) {
            throw new InvalidAgeException("Age " + age + " is outside the range 0 to 120");
        }
    }

    // ============================================================
    // Exercise 4: BankAccount that throws instead of returning false
    // ============================================================
    static class InsufficientFundsException extends Exception {
        private final double shortfall; // extra information a boolean cannot carry

        InsufficientFundsException(double shortfall) {
            super("Insufficient funds: you need " + shortfall + " more");
            this.shortfall = shortfall;
        }

        double getShortfall() {
            return shortfall;
        }
    }

    static class BankAccount {
        private double balance;

        BankAccount(double openingBalance) {
            this.balance = openingBalance;
        }

        // OLD DESIGN would be:  boolean withdraw(double amount) { ... return false; }
        // NEW DESIGN: throw an exception when the withdrawal cannot happen.
        void withdraw(double amount) throws InsufficientFundsException {
            if (amount > balance) {
                throw new InsufficientFundsException(amount - balance);
            }
            balance -= amount;
        }

        double getBalance() {
            return balance;
        }
    }

    // TRADE-OFFS (exception vs returning false):
    //  Exception:
    //   + The caller cannot silently ignore a failure (checked exceptions must be handled).
    //   + It can carry details (message, shortfall) instead of just true/false.
    //   + Normal code stays separate from error-handling code.
    //   - More verbose: every caller needs try/catch or "throws".
    //   - Heavier and slower than a boolean if failures are very common.
    //  Boolean:
    //   + Simple and cheap, fine when "failure" is an ordinary, expected outcome.
    //   - Easy to forget to check the result, so errors pass unnoticed.
    //   - Says only THAT it failed, not WHY.

    // ============================================================
    // Exercise 5: return inside try, print inside finally
    // ============================================================
    static String test1() {
        try {
            return "from try"; // return value is prepared here...
        } finally {
            System.out.println("finally block runs"); // ...but this still executes before returning
        }
    }

    static int test2() {
        int x = 1;
        try {
            return x;   // the value 1 is copied and set aside here
        } finally {
            x = 99;     // changes the variable, but NOT the value already set aside
        }
        // (If finally itself contained "return x;", it would override the try's return.
        //  That is legal but considered bad practice, because it also hides exceptions.)
    }
}
