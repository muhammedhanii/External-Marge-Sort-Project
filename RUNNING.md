# Running Guide

## Prerequisites
- Java JDK 17 or newer
- Maven 3.9+
- OS: Windows, Linux, or macOS

## Verify Installations
```bash
java -version
mvn -version
```

## Project Location
Repository root should be your current working directory:
```bash
cd /home/runner/work/External-Marge-Sort-Project/External-Marge-Sort-Project
```

## Build Commands
### Clean and compile
```bash
mvn clean compile
```

### Run tests (if present)
```bash
mvn test
```

### Package project
```bash
mvn package
```

## Run GUI Application
```bash
mvn javafx:run
```

## Output Directories
- `data/` generated input chunk files
- `sorted/` sorted chunk outputs
- `output/final_sorted.txt` merged final output

## Typical Workflow
1. Launch GUI (`mvn javafx:run`)
2. Click **Generate Files**
3. Click **Sort Files**
4. Click **Merge Files**
5. Or use **Run Full Pipeline** to run all steps at once

## Troubleshooting
### 1) JavaFX runtime error
- Ensure Maven can download JavaFX dependencies.
- Re-run:
```bash
mvn -U clean compile javafx:run
```

### 2) No files found in `data/` or `sorted/`
- Run generation first.
- Ensure application has write permissions in project directory.

### 3) Malformed record warnings
- The app skips malformed lines and logs warnings.
- Regenerate fresh files if source data was manually edited.

### 4) Permission denied errors
- Check file/folder permissions for `data`, `sorted`, and `output`.

### 5) Slow execution in GUI
- Increase JVM memory if needed:
```bash
MAVEN_OPTS="-Xms256m -Xmx1024m" mvn javafx:run
```

## Headless/CI Build Note
For environments without desktop UI support, use compile/package commands only:
```bash
mvn clean package
```
