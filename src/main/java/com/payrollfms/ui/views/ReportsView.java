package com.payrollfms.ui.views;

import com.payrollfms.data.DataStore;
import com.payrollfms.model.Employee;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Locale;

public class ReportsView extends VBox {

    private final DataStore store = DataStore.getInstance();
    private final NumberFormat inr = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

    public ReportsView() {
        setSpacing(0);
        getStyleClass().add("view-root");
        getChildren().addAll(buildTopBar(), buildContent());
    }

    private HBox buildTopBar() {
        Label title = new Label("Reports");
        title.getStyleClass().add("view-title");
        Label sub = new Label("Payroll analytics and summaries");
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

        content.getChildren().addAll(
            buildDeptSummary(),
            buildTopEarners(),
            buildPayrollOverview()
        );

        ScrollPane sp = new ScrollPane(content);
        sp.setFitToWidth(true);
        sp.getStyleClass().add("edge-to-edge");
        VBox.setVgrow(sp, Priority.ALWAYS);
        return sp;
    }

    private VBox buildDeptSummary() {
        Label title = new Label("Department-wise payroll summary");
        title.getStyleClass().add("section-title");

        Map<String, DoubleSummaryStatistics> byDept = store.getEmployees().stream()
            .collect(Collectors.groupingBy(Employee::getDepartment,
                Collectors.summarizingDouble(Employee::getNetPay)));

        TableView<Map.Entry<String, DoubleSummaryStatistics>> table = new TableView<>();
        table.getStyleClass().add("payroll-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(200);

        TableColumn<Map.Entry<String, DoubleSummaryStatistics>, String> deptCol = new TableColumn<>("Department");
        deptCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getKey()));

        TableColumn<Map.Entry<String, DoubleSummaryStatistics>, String> countCol = new TableColumn<>("Employees");
        countCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
            String.valueOf((long) d.getValue().getValue().getCount())));

        TableColumn<Map.Entry<String, DoubleSummaryStatistics>, String> avgCol = new TableColumn<>("Avg Net Pay");
        avgCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
            inr.format(d.getValue().getValue().getAverage())));

        TableColumn<Map.Entry<String, DoubleSummaryStatistics>, String> totalCol = new TableColumn<>("Total Net Pay");
        totalCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
            inr.format(d.getValue().getValue().getSum())));

        table.getColumns().addAll(deptCol, countCol, avgCol, totalCol);
        table.getItems().addAll(byDept.entrySet());

        return new VBox(10, title, table);
    }

    private VBox buildTopEarners() {
        Label title = new Label("Top 5 earners this month");
        title.getStyleClass().add("section-title");

        List<Employee> top5 = store.getEmployees().stream()
            .sorted(Comparator.comparingDouble(Employee::getNetPay).reversed())
            .limit(5).collect(Collectors.toList());

        VBox rows = new VBox(0);
        rows.setStyle("-fx-border-color: derive(-color-border-tertiary,0%); -fx-border-radius:8; -fx-border-width:1;");

        for (int i = 0; i < top5.size(); i++) {
            Employee e = top5.get(i);
            Label rank = new Label("#" + (i + 1));
            rank.setStyle("-fx-font-size:13px; -fx-font-weight:500; -fx-text-fill:#185FA5; -fx-min-width:28px;");
            Label name = new Label(e.getName());
            name.setStyle("-fx-font-size:13px;");
            Label role = new Label(e.getRole());
            role.setStyle("-fx-font-size:12px; -fx-text-fill: derive(-color-text-secondary,0%);");
            VBox nameBox = new VBox(2, name, role);
            Label pay = new Label(inr.format(e.getNetPay()));
            pay.setStyle("-fx-font-size:13px; -fx-font-weight:500;");

            HBox row = new HBox(12, rank, nameBox);
            HBox.setHgrow(nameBox, Priority.ALWAYS);
            row.getChildren().add(pay);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(10, 14, 10, 14));
            if (i < top5.size() - 1)
                row.setStyle("-fx-border-color: derive(-color-border-tertiary,0%); -fx-border-width: 0 0 0.5 0;");
            rows.getChildren().add(row);
        }

        return new VBox(10, title, rows);
    }

    private VBox buildPayrollOverview() {
        Label title = new Label("Payroll cost overview");
        title.getStyleClass().add("section-title");

        double totalGross = store.getEmployees().stream()
            .mapToDouble(e -> e.getBaseSalary() + e.getAllowances()).sum();
        double totalNet = store.getEmployees().stream().mapToDouble(Employee::getNetPay).sum();
        double totalDed = store.getEmployees().stream().mapToDouble(Employee::getDeductions).sum();
        double avgSalary = store.getEmployees().stream().mapToDouble(Employee::getBaseSalary).average().orElse(0);

        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(12);
        for (int i = 0; i < 4; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(25);
            grid.getColumnConstraints().add(cc);
        }

        grid.add(overviewCard("Total Gross Payroll", inr.format(totalGross), "All earnings"), 0, 0);
        grid.add(overviewCard("Total Deductions", inr.format(totalDed), "PF + TDS + others"), 1, 0);
        grid.add(overviewCard("Total Net Payroll", inr.format(totalNet), "After all deductions"), 2, 0);
        grid.add(overviewCard("Avg. Base Salary", inr.format(avgSalary), "Across all employees"), 3, 0);

        return new VBox(10, title, grid);
    }

    private VBox overviewCard(String label, String value, String sub) {
        Label l = new Label(label); l.getStyleClass().add("metric-label");
        Label v = new Label(value); v.getStyleClass().add("metric-value");
        v.setStyle("-fx-font-size:16px; -fx-font-weight:500;");
        Label s = new Label(sub); s.getStyleClass().add("metric-sub");
        VBox card = new VBox(5, l, v, s);
        card.getStyleClass().add("metric-card");
        return card;
    }
}
