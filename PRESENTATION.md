# Slide 1 — Title
## External Merge Sort for Large Employee Records
- Course: CSE323 Advanced Database Systems
- Technology: Java 17, Maven, JavaFX
- Dataset: 16 files × 1000 records = 16,000 employees

---

# Slide 2 — Introduction
- Sorting very large datasets efficiently is a core database challenge.
- In-memory sorting alone is not scalable for data bigger than RAM.
- External Merge Sort solves this using disk-based processing and controlled memory usage.

---

# Slide 3 — Problem Statement
- Given multiple employee data files:
  - Parse records safely
  - Sort each file independently
  - Merge all sorted chunks into one globally sorted output
- Must provide performance measurements and a professional GUI.

---

# Slide 4 — External Merge Sort Concept
- **Phase 1: Run Generation / Chunk Sorting**
  - Sort manageable chunks in memory.
- **Phase 2: K-Way Merge**
  - Merge all sorted chunks using a min-heap.
- Reads are incremental, avoiding full dataset loading.

---

# Slide 5 — Data Model
- Employee fields:
  - `employeeId`
  - `firstName`
  - `lastName`
  - `department`
  - `salary`
- CSV format:
  - `1001,Ahmed,Hassan,IT,12000.00`
- Supports parsing and comparator-based ordering.

---

# Slide 6 — System Architecture
- `model`: Employee + sort criteria
- `generator`: random record creation
- `io`: buffered file reading/writing
- `sorting`: per-chunk in-memory sort
- `merge`: priority queue merge engine
- `service`: orchestrates full pipeline
- `gui`: controls, logs, metrics, progress

---

# Slide 7 — Data Flow
1. Generate random files in `data/`
2. Read each file, sort records, write to `sorted/`
3. Merge sorted files to `output/final_sorted.txt`
4. Display metrics and status in GUI

---

# Slide 8 — Algorithms
## Chunk Sorting
- For each file: read list → sort using comparator → write sorted file
- Time: `O(n log n)` per chunk

## Multi-Way Merge
- Initialize heap with first row from each sorted file
- Repeatedly pop min row and append to output
- Push next row from the same file
- Time: `O(N log K)`

---

# Slide 9 — Performance Analysis
- Measured using `System.nanoTime()`
- Captured metrics:
  - chunk sorting duration
  - merge duration
  - total pipeline duration
- Displayed in GUI and logs for direct comparison.

---

# Slide 10 — GUI Showcase
- Buttons:
  - Generate Files
  - Sort Files
  - Merge Files
  - Run Full Pipeline
- Features:
  - Progress bar
  - Status indicator
  - Console output panel
  - Light/Dark theme toggle
  - Dataset and output statistics

---

# Slide 11 — Results Summary
- Successfully handles 16,000 employee records.
- Produces deterministic sorted output by selected criteria.
- Keeps memory usage efficient during merge phase.
- Demonstrates practical database-style external sorting pipeline.

---

# Slide 12 — Conclusion
- External Merge Sort is essential for large-scale data processing.
- PriorityQueue-based K-way merge provides efficient scalable merging.
- Modular Java architecture and GUI make the project production-like and maintainable.

---

# Slide 13 — Future Improvements
- Add charts for real-time throughput visualization
- Add configurable worker threads for generation/sorting
- Add support for larger datasets and compressed input files
- Export performance reports as CSV/JSON
