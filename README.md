# DAA Assignment 2

A Maven project with simple implementations of DynamicArray, MyLinkedList, and MinHeap. All structures store primitive `int` values. The implementations do not use built-in collections, generics, or the Stream API.

The assignment allows AI tools only for debugging and explaining concepts. This project was generated with AI as a learning example and must not be presented as independently written work that satisfies that restriction.

## Running the Project

You need JDK 17 or newer and Maven. Open the folder containing `pom.xml` in IntelliJ IDEA as a Maven project. Select your installed JDK as the Project SDK. You can run the tests from `StructuresTest`.

The easiest way to run the benchmark in IntelliJ is to wait for Maven dependencies to load, open `Benchmark.java`, and click the green arrow next to `main`. The working directory must be the project folder containing `pom.xml`. IntelliJ’s bundled Maven is sufficient for this approach; a separate `mvn` command in PATH is not required.

From a terminal in the project root:

```sh
mvn package
java -jar target/daa-assignment2-1.0.jar
```

The first command compiles the code, runs the JUnit 5 tests, and builds the JAR. The second runs all benchmarks and overwrites `results/results.csv`. To run only the tests, use `mvn test`. The first build requires internet access to download dependencies. The benchmark may take several minutes because it repeatedly traverses lists containing 100,000 nodes.

Verified on October 2, 2026, on Windows with OpenJDK 27: `mvn package` completed successfully, and all 7 JUnit tests passed, with no failures, errors, or skipped tests. Running the generated JAR produced 36 data rows in the CSV file. The code targets Java 17, but it was not separately tested on JDK 17.

Generated PNG charts are stored in `results/plots`. To update them after running the benchmark again, you need Python 3 and matplotlib:

```sh
python -m pip install matplotlib
python plots.py
```

## Project Files

| File | Purpose |
| --- | --- |
| `src/main/java/daa/DynamicArray.java` | Dynamic array, capacity doubling, and element shifting |
| `src/main/java/daa/MyLinkedList.java` | Singly linked list with head and tail references |
| `src/main/java/daa/MinHeap.java` | Bubble-up and bubble-down operations in a binary min-heap |
| `src/main/java/daa/IntList.java` | Common interface for the array and linked list |
| `src/main/java/daa/Metrics.java` | Three counters of type long |
| `src/main/java/daa/Benchmark.java` | W1–W4 workloads, warm-up runs, medians, and CSV output |
| `src/test/java/daa/StructuresTest.java` | Edge cases, randomized checks, and counter tests |
| `REPORT.md` | Complexity analysis, two proofs, charts, and discussion |

Start with DynamicArray, then study Node and MyLinkedList, followed by MinHeap. The `remove(index)` method returns the removed value. The additional `size()` and `metrics()` methods support testing and benchmarking. The `isValidHeap()` method is accessible to tests in the same package and does not modify the counters.

## Counters

`steps`: one array element read or one traversal through `next`, including a transition to null. Reading a node’s value is not counted as a separate step. Copying a reference to reconnect nodes without advancing the traversal position counts only as a link update.

`moves`: moving an existing array element, including copying it during resizing, or assigning a value to a `head`, `tail`, or `next` field. A heap swap counts as two moves. Writing a newly inserted value into an array is not a shift. Local variable assignments and automatic initialization of references to null are not counted.

`comparisons`: comparisons between element values, including comparisons against a search value. Checks involving indices, size, null, or loop boundaries are not counted. These are the three specific metrics required by the assignment, rather than a count of every processor instruction.

## Measurement Conditions

For each value of n, the input data is generated again using `new Random(42)`. Both structures receive identical values, indices, and search queries. A new structure is created before every run. Ten warm-up runs are discarded, and the median of five measured runs is recorded. The counters must match across all five measured runs.

W1–W3 exclude the initial filling of the structures. W4 includes inserting n values and extracting n values. Query generation, CSV output, and sorted-output verification are excluded from the measured time. W3 performs 1,000 insertions followed by 1,000 removals. The middle index always equals the original n / 2. The CSV contains 36 data rows.

## Remaining Submission Requirements

A GitHub repository and development history have not been created. The assignment requires actual feature branches named `feature/array`, `feature/list`, `feature/heap`, and `feature/metrics`, small meaningful commits, a working `main` branch, and a `v1.0` tag. Maintain this history as you develop your own work. Creating empty branches after finishing does not replace a development history.

Add your GitHub repository link, enter your first name, surname, and group, name the archive `DAA_Assignment2_name_surname_group.zip`, and upload it to Moodle yourself. The optional JOL and buildHeap tasks are not implemented. Markdown does not have a fixed page count, so check that the formatted report meets the required length of 3–5 pages in your chosen editor.