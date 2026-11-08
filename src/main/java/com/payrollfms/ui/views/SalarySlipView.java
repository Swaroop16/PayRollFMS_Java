package com.payrollfms.ui.views;

import com.payrollfms.data.DataStore;
import com.payrollfms.model.Employee;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.text.NumberFormat;
import java.util.Locale;

public class SalarySlipView extends VBox {

    private final DataStore store = DataStore.getInstance();
    private final NumberFormat inr = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

    private VBox slipPanel;
    private ComboBox<Employee> empPicker;

    public SalarySlipView() {
        setSpacing(0);
        getStyleClass().add("view-root");
        getChildren().addAll(buildTopBar(), buildContent());
    }

    private HBox buildTopBar() {
        Label title = new Label("Salary Slips");
        title.getStyleClass().add("view-title");
        Label sub = new Label("Generate and view individual salary slips");
        sub.getStyleClass().add("view-sub");
        VBox titleBox = new VBox(3, title, sub);

        HBox bar = new HBox();
        bar.getStyleClass().add("topbar");
        HBox.setHgrow(titleBox, Priority.ALWAYS);
        bar.getChildren().add(titleBox);
        return bar;
    }

    private ScrollPane buildContent() {
        VBox content = new VBox(20);
        content.getStyleClass().add("view-content");

        // Selector row
        Label selectLbl = new Label("Select employee:");
        selectLbl.setStyle("-fx-font-size:13px;");
        empPicker = new ComboBox<>(store.getEmployees());
        empPicker.setPrefWidth(240);
        empPicker.setPromptText("Choose employee...");

        ComboBox<String> monthPicker = new ComboBox<>();
        monthPicker.getItems().addAll("April 2026", "March 2026", "February 2026");
        monthPicker.setValue("April 2026");

        Button genBtn = new Button("Generate Slip");
        genBtn.getStyleClass().add("btn-primary");
        genBtn.setOnAction(e -> generateSlip(empPicker.getValue(), monthPicker.getValue()));

        HBox selectorRow = new HBox(12, selectLbl, empPicker, monthPicker, genBtn);
        selectorRow.setAlignment(Pos.CENTER_LEFT);

        slipPanel = new VBox();
        slipPanel.setVisible(false);

        content.getChildren().addAll(selectorRow, slipPanel);

        ScrollPane sp = new ScrollPane(content);
        sp.setFitToWidth(true);
        sp.getStyleClass().add("edge-to-edge");
        VBox.setVgrow(sp, Priority.ALWAYS);
        return sp;
    }

    private void generateSlip(Employee emp, String month) {
        if (emp == null) {
            new Alert(Alert.AlertType.WARNING, "Please select an employee.").showAndWait();
            return;
        }

        slipPanel.getChildren().clear();
        slipPanel.setVisible(true);
        slipPanel.getStyleClass().setAll("salary-slip");

        double pf = emp.getBaseSalary() * 0.12;
        double tds = (emp.getBaseSalary() + emp.getAllowances()) * 0.05;
        double gross = emp.getBaseSalary() + emp.getAllowances();
        double netPay = gross - pf - tds;

        // Header
        Label company = new Label("FitLife Gym & Wellness");
        company.setStyle("-fx-font-size:18px; -fx-font-weight:500;");
        Label slipTitle = new Label("Salary Slip — " + month);
        slipTitle.setStyle("-fx-font-size:13px; -fx-text-fill: -fx-text-secondary;");
        VBox header = new VBox(4, company, slipTitle);
        header.setStyle("-fx-border-color: derive(-color-border-tertiary, 0%); -fx-border-width: 0 0 1 0; -fx-padding: 0 0 14 0;");

        // Employee info
        GridPane info = new GridPane();
        info.setHgap(20); info.setVgap(8);
        info.addRow(0, infoLabel("Employee Name"), infoValue(emp.getName()),
                       infoLabel("Employee ID"),   infoValue("EMP-" + String.format("%03d", emp.getId())));
        info.addRow(1, infoLabel("Designation"),   infoValue(emp.getRole()),
                       infoLabel("Department"),    infoValue(emp.getDepartment()));
        info.addRow(2, infoLabel("Pay Period"),     infoValue(month),
                       infoLabel("Payment Mode"),  infoValue("Bank Transfer"));

        // Earnings & Deductions
        GridPane earnings = earningsGrid(emp, pf, tds, gross, netPay);

        // Net Pay box
        HBox netBox = new HBox();
        netBox.setStyle("-fx-background-color: #E6F1FB; -fx-background-radius: 8; -fx-padding: 12 16;");
        Label netLbl = new Label("Net Pay:");
        netLbl.setStyle("-fx-font-size:14px; -fx-font-weight:500; -fx-text-fill:#185FA5;");
        Label netVal = new Label(inr.format(netPay));
        netVal.setStyle("-fx-font-size:18px; -fx-font-weight:500; -fx-text-fill:#185FA5;");
        HBox.setHgrow(netLbl, Priority.ALWAYS);
        netBox.getChildren().addAll(netLbl, netVal);

        Label footer = new Label("This is a system-generated salary slip and does not require a signature.");
        footer.setStyle("-fx-font-size:11px; -fx-text-fill:-fx-text-secondary;");

        slipPanel.setSpacing(16);
        slipPanel.setPadding(new Insets(20));
        slipPanel.setMaxWidth(640);
        slipPanel.getChildren().addAll(header, info, earnings, netBox, footer);
    }

    private GridPane earningsGrid(Employee emp, double pf, double tds, double gross, double netPay) {
        GridPane grid = new GridPane();
        grid.setHgap(30); grid.setVgap(0);
        ColumnConstraints c1 = new ColumnConstraints(); c1.setPercentWidth(50);
        ColumnConstraints c2 = new ColumnConstraints(); c2.setPercentWidth(50);
        grid.getColumnConstraints().addAll(c1, c2);

        VBox earningsBox = new VBox(0);
        earningsBox.setStyle("-fx-border-color: derive(-color-border-tertiary,0%); -fx-border-radius: 6; -fx-border-width: 1;");
        earningsBox.getChildren().addAll(
            sectionHeader("Earnings"),
            slipRow("Basic Salary",    inr.format(emp.getBaseSalary())),
            slipRow("HRA / Allowances", inr.format(emp.getAllowances())),
            slipRowTotal("Gross Earnings", inr.format(gross))
        );

        VBox deductionsBox = new VBox(0);
        deductionsBox.setStyle("-fx-border-color: derive(-color-border-tertiary,0%); -fx-border-radius: 6; -fx-border-width: 1;");
        deductionsBox.getChildren().addAll(
            sectionHeader("Deductions"),
            slipRow("Provident Fund (12%)", inr.format(pf)),
            slipRow("TDS (Income Tax)",     inr.format(tds)),
            slipRow("Other Deductions",     inr.format(emp.getDeductions() - pf > 0 ? emp.getDeductions() - pf : 0)),
            slipRowTotal("Total Deductions", inr.format(pf + tds))
        );

        grid.add(earningsBox, 0, 0);
        grid.add(deductionsBox, 1, 0);
        return grid;
    }

    private HBox sectionHeader(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size:13px; -fx-font-weight:500; -fx-padding: 8 12;");
        HBox box = new HBox(l);
        box.setStyle("-fx-background-color: derive(-color-background-secondary,0%); -fx-border-color: derive(-color-border-tertiary,0%); -fx-border-width: 0 0 1 0;");
        return box;
    }

    private HBox slipRow(String label, String value) {
        Label l = new Label(label); l.setStyle("-fx-font-size:12px; -fx-padding: 7 12;");
        Label v = new Label(value); v.setStyle("-fx-font-size:12px; -fx-font-weight:500; -fx-padding: 7 12;");
        HBox row = new HBox();
        HBox.setHgrow(l, Priority.ALWAYS);
        row.setStyle("-fx-border-color: derive(-color-border-tertiary,0%); -fx-border-width: 0 0 0.5 0;");
        row.getChildren().addAll(l, v);
        return row;
    }

    private HBox slipRowTotal(String label, String value) {
        Label l = new Label(label); l.setStyle("-fx-font-size:12px; -fx-font-weight:500; -fx-padding: 8 12;");
        Label v = new Label(value); v.setStyle("-fx-font-size:12px; -fx-font-weight:500; -fx-padding: 8 12;");
        HBox row = new HBox();
        HBox.setHgrow(l, Priority.ALWAYS);
        row.setStyle("-fx-background-color: derive(-color-background-secondary,0%);");
        row.getChildren().addAll(l, v);
        return row;
    }

    private Label infoLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size:12px; -fx-text-fill: -fx-text-secondary;");
        return l;
    }

    private Label infoValue(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size:12px; -fx-font-weight:500;");
        return l;
    }
}
