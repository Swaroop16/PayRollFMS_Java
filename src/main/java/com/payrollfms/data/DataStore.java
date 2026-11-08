package com.payrollfms.data;

import com.payrollfms.model.Employee;
import com.payrollfms.model.PayrollRecord;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DataStore {

    private static DataStore instance;

    private final ObservableList<Employee> employees = FXCollections.observableArrayList();
    private final ObservableList<PayrollRecord> payrollRecords = FXCollections.observableArrayList();

    private DataStore() {
        loadSampleData();
    }

    public static DataStore getInstance() {
        if (instance == null) instance = new DataStore();
        return instance;
    }

    private void loadSampleData() {
        employees.addAll(
            new Employee(1, "Arjun Kumar",     "Head Trainer",    "Training",   "arjun@fitlife.com",  "9876543210", 45000, 5000, 4500, "Active", "2021-03-15"),
            new Employee(2, "Priya Rao",        "Yoga Instructor", "Wellness",   "priya@fitlife.com",  "9876543211", 32000, 3000, 3200, "Active", "2022-06-01"),
            new Employee(3, "Suresh Mehta",     "Front Desk",      "Operations", "suresh@fitlife.com", "9876543212", 22000, 2000, 2200, "Active", "2023-01-10"),
            new Employee(4, "Neha Bhat",        "Nutritionist",    "Wellness",   "neha@fitlife.com",   "9876543213", 38000, 4000, 3800, "Active", "2021-11-20"),
            new Employee(5, "Ravi Shankar",     "Gym Trainer",     "Training",   "ravi@fitlife.com",   "9876543214", 28000, 2500, 2800, "Active", "2022-09-05"),
            new Employee(6, "Anita Desai",      "Manager",         "Management", "anita@fitlife.com",  "9876543215", 55000, 8000, 5500, "Active", "2020-07-01"),
            new Employee(7, "Kiran Patel",      "Receptionist",    "Operations", "kiran@fitlife.com",  "9876543216", 20000, 1500, 2000, "Active", "2023-04-15"),
            new Employee(8, "Deepa Nair",       "Zumba Trainer",   "Training",   "deepa@fitlife.com",  "9876543217", 27000, 2500, 2700, "On Leave", "2022-02-28"),
            new Employee(9, "Manoj Gupta",      "Accountant",      "Finance",    "manoj@fitlife.com",  "9876543218", 35000, 3500, 3500, "Active", "2021-08-12"),
            new Employee(10, "Savita Singh",    "Cleaning Staff",  "Operations", "savita@fitlife.com", "9876543219", 15000, 1000, 1500, "Active", "2023-06-01")
        );

        String[] months = {"April 2026", "March 2026", "February 2026"};
        int recId = 1;
        for (String month : months) {
            for (Employee e : employees) {
                double gross = e.getBaseSalary() + e.getAllowances();
                double pf = e.getBaseSalary() * 0.12;
                double tds = gross * 0.05;
                double other = e.getDeductions() - pf;
                if (other < 0) other = 0;
                String status = month.equals("April 2026") ? "Pending" : "Paid";
                payrollRecords.add(new PayrollRecord(recId++, e.getName(), month, gross, pf, tds, other, status));
            }
        }
    }

    public ObservableList<Employee> getEmployees() { return employees; }
    public ObservableList<PayrollRecord> getPayrollRecords() { return payrollRecords; }

    public int nextEmployeeId() {
        return employees.stream().mapToInt(Employee::getId).max().orElse(0) + 1;
    }

    public double getTotalMonthlyPayroll() {
        return employees.stream().mapToDouble(Employee::getNetPay).sum();
    }

    public long getActiveEmployeeCount() {
        return employees.stream().filter(e -> e.getStatus().equals("Active")).count();
    }
}
