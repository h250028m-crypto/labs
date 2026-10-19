import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class Lab10 {

    // The file used by Exercises 1 and 3
    static final Path NOTES = Path.of("notes.txt");

    public static void main(String[] args) {

        // ---------- Exercise 1: save five typed lines, read them back ----------
        System.out.println("=== Exercise 1: Save and read back ===");
        List<String> typed = new ArrayList<>();
        // try-with-resources closes the Scanner automatically (this removes the
        // "resource leak" warning). It is only needed in this exercise.
        try (Scanner sc = new Scanner(System.in)) {
            for (int i = 1; i <= 5; i++) {
                System.out.print("Line " + i + ": ");
                if (!sc.hasNextLine()) {
                    break; // no more input available
                }
                typed.add(sc.nextLine());
            }
        }

        // try-with-resources: the writer is closed automatically, even if an error happens
        try (BufferedWriter out = Files.newBufferedWriter(NOTES)) {
            for (String line : typed) {
                out.write(line);
                out.newLine(); // platform-correct line break
            }
        } catch (IOException e) {
            System.out.println("Could not write the file: " + e.getMessage());
        }

        System.out.println("--- Reading " + NOTES + " back ---");
        try (BufferedReader in = Files.newBufferedReader(NOTES)) {
            String line;
            while ((line = in.readLine()) != null) { // readLine() returns null at the end
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println("Could not read the file: " + e.getMessage());
        }

        // ---------- Exercise 2: Predicate and removeIf ----------
        System.out.println("\n=== Exercise 2: Remove even numbers ===");
        // List.of() is immutable, so copy it into a modifiable ArrayList first
        List<Integer> numbers = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        Predicate<Integer> isEven = n -> n % 2 == 0; // a Predicate answers true/false for a value
        System.out.println("Before: " + numbers);
        numbers.removeIf(isEven); // removes every element for which the predicate is true
        System.out.println("After:  " + numbers);

        // ---------- Exercise 3: count lines, words, characters ----------
        System.out.println("\n=== Exercise 3: Count lines, words and characters ===");
        countFile(NOTES);

        // ---------- Exercise 4: streams on a list of records ----------
        System.out.println("\n=== Exercise 4: Above-average students ===");
        List<Student> students = List.of(
                new Student("Tendai", 72),
                new Student("Rudo", 55),
                new Student("Farai", 91),
                new Student("Chipo", 64),
                new Student("Blessing", 80));

        double average = students.stream()
                .mapToInt(Student::marks)   // turn each Student into its marks
                .average()                  // OptionalDouble
                .orElse(0);                 // value used if the list were empty
        System.out.println("Class average: " + average);

        List<String> aboveAverage = students.stream()
                .filter(s -> s.marks() > average) // keep only those above the average
                .map(Student::name)               // take just the names
                .sorted()                         // alphabetical order
                .collect(Collectors.toList());    // gather into a List
        System.out.println("Above average (A to Z): " + aboveAverage);

        // ---------- Exercise 5: learners.csv, groupingBy, one report per band ----------
        System.out.println("\n=== Exercise 5: Group learners by grade band ===");
        groupLearners(Path.of("learners.csv"));
    }

    // ============================================================
    // Exercise 3
    // ============================================================
    static void countFile(Path file) {
        try {
            List<String> lines = Files.readAllLines(file); // reads the whole file into memory
            int wordCount = 0;
            int charCount = 0;
            for (String line : lines) {
                charCount += line.length();            // characters (line breaks not counted)
                if (!line.isBlank()) {
                    wordCount += line.trim().split("\\s+").length; // split on runs of whitespace
                }
            }
            System.out.println("File: " + file);
            System.out.println("Lines: " + lines.size());
            System.out.println("Words: " + wordCount);
            System.out.println("Characters (excluding line breaks): " + charCount);
        } catch (IOException e) {
            System.out.println("Could not read " + file + ": " + e.getMessage());
        }
    }

    // ============================================================
    // Exercise 4: record used with streams
    // ============================================================
    record Student(String name, int marks) { }

    // ============================================================
    // Exercise 5
    // ============================================================
    // Expected file format (first line is a header):
    //   name,score
    //   Tendai,82
    record Learner(String name, int score) {
        // Grade band (the exercise gives no cut-offs, so these are assumed):
        // A = 70 and above, B = 50 to 69, C = below 50
        String band() {
            if (score >= 70) return "A";
            if (score >= 50) return "B";
            return "C";
        }
    }

    static void groupLearners(Path csv) {
        try {
            // If learners.csv does not exist yet, create a sample so the program can run
            if (Files.notExists(csv)) {
                Files.write(csv, List.of(
                        "name,score",
                        "Tendai,82", "Rudo,55", "Farai,91", "Chipo,44",
                        "Blessing,70", "Tapiwa,38", "Nyasha,63"));
                System.out.println("(learners.csv not found, created a sample file)");
            }

            // Load the learners; skip the header line and any badly formatted line
            List<Learner> learners = new ArrayList<>();
            List<String> lines = Files.readAllLines(csv);
            for (int i = 1; i < lines.size(); i++) { // start at 1 to skip the header
                String[] parts = lines.get(i).split(",");
                try {
                    learners.add(new Learner(parts[0].trim(), Integer.parseInt(parts[1].trim())));
                } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                    System.out.println("Skipping bad line " + (i + 1) + ": " + lines.get(i));
                }
            }

            // groupingBy: key = band, value = list of learners in that band.
            // TreeMap::new keeps the bands in order A, B, C.
            Map<String, List<Learner>> byBand = learners.stream()
                    .collect(Collectors.groupingBy(Learner::band, TreeMap::new, Collectors.toList()));

            // Write one report file per band
            for (Map.Entry<String, List<Learner>> entry : byBand.entrySet()) {
                String band = entry.getKey();
                List<String> report = new ArrayList<>();
                report.add("Band " + band + " report (" + entry.getValue().size() + " learners)");
                for (Learner l : entry.getValue()) {
                    report.add(l.name() + ": " + l.score());
                }
                Path reportFile = Path.of("report_" + band + ".txt");
                Files.write(reportFile, report);
                System.out.println("Wrote " + reportFile + " -> " + entry.getValue());
            }
        } catch (IOException e) {
            System.out.println("File problem: " + e.getMessage());
        }
    }
}
