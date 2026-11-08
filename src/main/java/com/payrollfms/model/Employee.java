package com.payrollfms.model;

import javafx.beans.property.*;

public class Employee {

    private final IntegerProperty id;
    private final StringProperty name;
    private final StringProperty role;
    private final StringProperty department;
    private final StringProperty email;
    private final StringProperty phone;
    private final DoubleProperty baseSalary;
    private final DoubleProperty allowances;
    private final DoubleProperty deductions;
    private final StringProperty status;
    private final StringProperty joinDate;

    public Employee(int id, String name, String role, String department,
                    String email, String phone, double baseSalary,
                    double allowances, double deductions, String status, String joinDate) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.role = new SimpleStringProperty(role);
        this.department = new SimpleStringProperty(department);
        this.email = new SimpleStringProperty(email);
        this.phone = new SimpleStringProperty(phone);
        this.baseSalary = new SimpleDoubleProperty(baseSalary);
        this.allowances = new SimpleDoubleProperty(allowances);
        this.deductions = new SimpleDoubleProperty(deductions);
        this.status = new SimpleStringProperty(status);
        this.joinDate = new SimpleStringProperty(joinDate);
    }

    public double getNetPay() {
        return baseSalary.get() + allowances.get() - deductions.get();
    }

    // --- Getters ---
    public int getId() { return id.get(); }
    public String getName() { return name.get(); }
    public String getRole() { return role.get(); }
    public String getDepartment() { return department.get(); }
    public String getEmail() { return email.get(); }
    public String getPhone() { return phone.get(); }
    public double getBaseSalary() { return baseSalary.get(); }
    public double getAllowances() { return allowances.get(); }
    public double getDeductions() { return deductions.get(); }
    public String getStatus() { return status.get(); }
    public String getJoinDate() { return joinDate.get(); }

    // --- Property accessors ---
    public IntegerProperty idProperty() { return id; }
    public StringProperty nameProperty() { return name; }
    public StringProperty roleProperty() { return role; }
    public StringProperty departmentProperty() { return department; }
    public StringProperty emailProperty() { return email; }
    public StringProperty phoneProperty() { return phone; }
    public DoubleProperty baseSalaryProperty() { return baseSalary; }
    public DoubleProperty allowancesProperty() { return allowances; }
    public DoubleProperty deductionsProperty() { return deductions; }
    public StringProperty statusProperty() { return status; }
    public StringProperty joinDateProperty() { return joinDate; }

    // --- Setters ---
    public void setName(String v) { name.set(v); }
    public void setRole(String v) { role.set(v); }
    public void setDepartment(String v) { department.set(v); }
    public void setEmail(String v) { email.set(v); }
    public void setPhone(String v) { phone.set(v); }
    public void setBaseSalary(double v) { baseSalary.set(v); }
    public void setAllowances(double v) { allowances.set(v); }
    public void setDeductions(double v) { deductions.set(v); }
    public void setStatus(String v) { status.set(v); }

    @Override
    public String toString() {
        return name.get() + " (" + role.get() + ")";
    }
}
