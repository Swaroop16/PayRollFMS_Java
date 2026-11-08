[README.md](https://github.com/user-attachments/files/34541779/README.md)
# PayRoll Fitness Management System

A JavaFX desktop application for managing employee payroll in a fitness/gym business.

---

## Features

- **Dashboard** — Monthly payroll overview, key metrics, payroll breakdown chart, and summary
- **Employees** — Add, edit, delete staff. Filter/search by name, role, or department
- **Payroll** — View monthly payroll records with gross pay, PF, TDS, and net pay. Process payments
- **Salary Slips** — Generate professional salary slips per employee per month
- **Reports** — Department-wise summaries, top earners, and payroll cost overview

---

## Project Structure

```
PayRollFMS/
├── pom.xml
└── src/
    └── main/
        ├── java/com/payrollfms/
        │   ├── MainApp.java                  ← Application entry point
        │   ├── data/
        │   │   └── DataStore.java            ← In-memory data & sample records
        │   ├── model/
        │   │   ├── Employee.java             ← Employee model (JavaFX properties)
        │   │   └── PayrollRecord.java        ← Payroll record model
        │   └── ui/
        │       ├── MainLayout.java           ← Sidebar + content area
        │       └── views/
        │           ├── DashboardView.java
        │           ├── EmployeeView.java
        │           ├── PayrollView.java
        │           ├── SalarySlipView.java
        │           └── ReportsView.java
        └── resources/
            └── styles/
                └── main.css                  ← Application stylesheet
```

---

## Requirements

- **Java 17+** (Java 21 recommended)
- **Maven 3.8+**
- JavaFX 21 (bundled via Maven dependency)

---

## How to Run

### Option 1: Maven (recommended)

```bash
cd PayRollFMS
mvn javafx:run
```

### Option 2: Build a runnable JAR

```bash
mvn package
java -jar target/payroll-fitness-management-1.0.0.jar
```

> **Note:** If you get a "JavaFX runtime components are missing" error when running the JAR directly, use the JavaFX Maven plugin method (`mvn javafx:run`) instead, or add `--module-path` and `--add-modules` flags pointing to your local JavaFX SDK.

---

## Extending the App

| Feature | Where to add |
|---|---|
| Database persistence | Replace `DataStore` with JDBC / SQLite |
| PDF salary slip export | Add `iTextPDF` or `Apache PDFBox` to `pom.xml` |
| Attendance tracking | Add new `AttendanceView` and `Attendance` model |
| Role-based login | Add a `LoginScreen` before `MainLayout` |
| Charts (bar/pie) | Add `JavaFX Charts` nodes to `DashboardView` |

---

## Sample Data

The app loads 10 sample employees from `DataStore.java` across departments:
Training, Wellness, Operations, Management, and Finance.
Three months of payroll records (Feb–Apr 2026) are pre-loaded.
