# External Merge Sort for Large Employee Records

## Project Overview
This project is a professional Java 17 + JavaFX implementation of **External Merge Sort** for handling large employee datasets split across multiple files. It was designed for the CSE323 Advanced Database Systems project requirements and demonstrates chunk sorting, K-way heap merge, efficient file I/O, and execution performance tracking.

The system generates 16 input files (1000 records each), sorts every chunk in memory, then merges sorted chunks into one globally sorted output file without loading all records into memory.

## Key Features
- Random employee dataset generation (16,000 total records by default)
- Buffered file read/write using `BufferedReader` and `BufferedWriter`
- In-memory chunk sorting by:
  - Employee ID ascending
  - Last Name ascending
- True K-way external merge using `PriorityQueue`
- Performance timing:
  - Chunk sorting time
  - Merge time
  - Total pipeline time
- JavaFX GUI with:
  - Generate, Sort, Merge, and Full Pipeline controls
  - Progress bar and status indicator
  - Console output panel
  - File statistics panel
  - Light/Dark theme toggle
- Error handling for malformed records and missing input states

## External Merge Sort Explanation
External Merge Sort is used when datasets are too large to fit into RAM in one pass.

### Phase 1: Chunk Sorting
1. Read one file/chunk into memory.
2. Sort chunk in memory with O(n log n).
3. Write sorted chunk back to disk.
4. Repeat for all chunks.

### Phase 2: Multi-Way Merge
1. Open all sorted chunk files simultaneously.
2. Insert first record from each file into a min-heap.
3. Repeatedly extract the smallest record and write it to final output.
4. Read next record from the file that provided the extracted record, then push it into the heap.

This approach guarantees global ordering while keeping memory consumption efficient.

## Architecture
Layered modular design with clear separation of concerns:

- `model` – domain entities and sort criteria
- `generator` – random data generation
- `io` – buffered file management
- `sorting` – chunk-level sorting logic
- `merge` – multi-way merge with heap nodes
- `performance` – timing and metrics records
- `service` – application orchestration pipeline
- `gui` – JavaFX presentation layer
- `utils` – shared utilities (directory management)

## Project Structure
```text
ExternalMergeSort/
│
├── data/
├── sorted/
├── output/
├── docs/
│
├── src/main/java/com/externalmergesort/
│   ├── model/
│   ├── generator/
│   ├── io/
│   ├── sorting/
│   ├── merge/
│   ├── gui/
│   ├── utils/
│   ├── performance/
│   └── service/
│
├── README.md
├── PRESENTATION.md
├── RUNNING.md
└── pom.xml
```

## Complexity Analysis
Let:
- `N` = total number of records
- `K` = number of sorted chunk files
- `n` = records per chunk

### Chunk Sorting
- Per chunk: **O(n log n)**
- Across all chunks: **O(N log n)**

### K-Way Merge
- Heap operations cost: **O(log K)**
- Per record processed once: **O(N log K)**

### Space Behavior
- Chunk sorting uses memory proportional to one chunk
- Merge keeps only one current record per file in heap + readers

## Technologies Used
- Java 17
- JavaFX 21
- Maven
- Standard Java I/O (`java.nio`, buffered streams)

## GUI Walkthrough
1. Select number of files and records per file.
2. Select sort criteria.
3. Click:
   - **Generate Files** to create input dataset
   - **Sort Files** to sort chunks
   - **Merge Files** to produce final output
   - **Run Full Pipeline** to execute all steps with timing
4. Observe:
   - Progress bar
   - Status label
   - Console logs
   - Performance metrics
   - File statistics

## Sample Console Output
```text
[LOG] Starting full pipeline...
[LOG] Generated file: employees_1.txt (records=1000)
...
[LOG] Sorted employees_1.txt -> employees_1_sorted.txt (records=1000)
...
[LOG] Multi-way merge completed. Output: final_sorted.txt (records=16000)
[LOG] Execution summary -> chunk: 84.133 ms | merge: 97.882 ms | total: 256.901 ms
```

## Screenshot Placeholders
- `docs/screenshots/main-dashboard.png`
- `docs/screenshots/pipeline-complete.png`
- `docs/screenshots/dark-theme.png`

## Setup and Run
See [RUNNING.md](RUNNING.md) for complete setup and troubleshooting.
