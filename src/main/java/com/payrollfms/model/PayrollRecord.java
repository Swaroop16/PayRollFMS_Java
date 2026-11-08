package com.payrollfms.model;

import javafx.beans.property.*;

public class PayrollRecord {

    private final IntegerProperty id;
    private final StringProperty employeeName;
    private final StringProperty month;
    private final DoubleProperty grossPay;
    private final DoubleProperty pf;
    private final DoubleProperty tds;
    private final DoubleProperty otherDeductions;
    private final DoubleProperty netPay;
    private final StringProperty paymentStatus;

    public PayrollRecord(int id, String employeeName, String month,
                         double grossPay, double pf, double tds,
                         double otherDeductions, String paymentStatus) {
        this.id = new SimpleIntegerProperty(id);
        this.employeeName = new SimpleStringProperty(employeeName);
        this.month = new SimpleStringProperty(month);
        this.grossPay = new SimpleDoubleProperty(grossPay);
        this.pf = new SimpleDoubleProperty(pf);
        this.tds = new SimpleDoubleProperty(tds);
        this.otherDeductions = new SimpleDoubleProperty(otherDeductions);
        this.netPay = new SimpleDoubleProperty(grossPay - pf - tds - otherDeductions);
        this.paymentStatus = new SimpleStringProperty(paymentStatus);
    }

    public int getId() { return id.get(); }
    public String getEmployeeName() { return employeeName.get(); }
    public String getMonth() { return month.get(); }
    public double getGrossPay() { return grossPay.get(); }
    public double getPf() { return pf.get(); }
    public double getTds() { return tds.get(); }
    public double getOtherDeductions() { return otherDeductions.get(); }
    public double getNetPay() { return netPay.get(); }
    public String getPaymentStatus() { return paymentStatus.get(); }

    public IntegerProperty idProperty() { return id; }
    public StringProperty employeeNameProperty() { return employeeName; }
    public StringProperty monthProperty() { return month; }
    public DoubleProperty grossPayProperty() { return grossPay; }
    public DoubleProperty pfProperty() { return pf; }
    public DoubleProperty tdsProperty() { return tds; }
    public DoubleProperty otherDeductionsProperty() { return otherDeductions; }
    public DoubleProperty netPayProperty() { return netPay; }
    public StringProperty paymentStatusProperty() { return paymentStatus; }

    public void setPaymentStatus(String v) { paymentStatus.set(v); }
}
