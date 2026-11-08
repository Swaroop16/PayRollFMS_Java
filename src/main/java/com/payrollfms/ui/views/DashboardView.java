package com.payrollfms.ui.views;

import com.payrollfms.data.DataStore;
import com.payrollfms.model.Employee;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.text.NumberFormat;
import java.util.Locale;

public class DashboardView extends VBox {

    private final DataStore store = DataStore.getInstance();
    private final NumberFormat inr = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

    public DashboardView() {
        setSpacing(0);
        getStyleClass().add("view-root");

        getChildren().addAll(buildTopBar(), buildScrollContent());
    }

    private HBox buildTopBar() {
        Label title = new Label("Payroll Dashboard");
        title.getStyleClass().add("view-title");
        Label sub = new Label("April 2026  —  FitLife Gym & Wellness");
        sub.getStyleClass().add("view-sub");
        VBox titleBox = new VBox(3, title, sub);

        Button exportBtn = new Button("Export");
        exportBtn.getStyleClass().add("btn-secondary");
        Button runBtn = new Button("Run Payroll");
        runBtn.getStyleClass().add("btn-primary");
        runBtn.setOnAction(e -> showRunPayrollAlert());

        HBox right = new HBox(8, exportBtn, runBtn);
        HBox bar = new HBox();
        bar.getStyleClass().add("topbar");
        HBox.setHgrow(titleBox, Priority.ALWAYS);
        bar.getChildren().addAll(titleBox, right);
        return bar;
    }

    private ScrollPane buildScrollContent() {
        VBox content = new VBox(18);
        content.getStyleClass().add("view-content");

        content.getChildren().addAll(
            buildMetrics(),
            buildTableSection(),
            buildBottomRow()
        );

        ScrollPane sp = new ScrollPane(content);
        sp.setFitToWidth(true);
        sp.getStyleClass().add("edge-to-edge");
        VBox.setVgrow(sp, Priority.ALWAYS);
        return sp;
    }

    private GridPane buildMetrics() {
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        for (int i = 0; i < 4; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(25);
            grid.getColumnConstraints().add(col);
        }

        long active = store.getActiveEmployeeCount();
        double gross = store.getTotalMonthlyPayroll();
        long pending = store.getEmployees().stream().filter(e -> e.getStatus().equals("On Leave")).count();
        double net = gross * 0.80;

        grid.add(metricCard("Total Employees", String.valueOf(store.getEmployees().size()), active + " active"), 0, 0);
        grid.add(metricCard("Monthly Payroll", formatINR(gross), "+3.2% vs last month"), 1, 0);
        grid.add(metricCard("On Leave", String.valueOf(pending), "Needs attention"), 2, 0);
        grid.add(metricCard("Net Disbursed", formatINR(net), "After deductions"), 3, 0);
        return grid;
    }

    private VBox metricCard(String label, String value, String sub) {
        Label lbl = new Label(label);
        lbl.getStyleClass().add("metric-label");
        Label val = new Label(value);
        val.getStyleClass().add("metric-value");
        Label subLbl = new Label(sub);
        subLbl.getStyleClass().add("metric-sub");
        VBox card = new VBox(5, lbl, val, subLbl);
        card.getStyleClass().add("metric-card");
        return card;
    }

    private VBox buildTableSection() {
        Label title = new Label("Employee payroll — April 2026");
        title.getStyleClass().add("section-title");

        TableView<Employee> table = new TableView<>(store.getEmployees());
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(220);
        table.getStyleClass().add("payroll-table");

        TableColumn<Employee, String> nameCol = new TableColumn<>("Employee");
        nameCol.setCellValueFactory(d -> d.getValue().nameProperty());
        nameCol.setPrefWidth(160);

        TableColumn<Employee, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(d -> d.getValue().roleProperty());

        TableColumn<Employee, Number> baseCol = new TableColumn<>("Base Salary");
        baseCol.setCellValueFactory(d -> d.getValue().baseSalaryProperty());
        baseCol.setCellFactory(tc -> new TableCell<>() {
            @Override protected void updateItem(Number v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty || v == null ? null : formatINR(v.doubleValue()));
            }
        });

        TableColumn<Employee, Number> allowCol = new TableColumn<>("Allowances");
        allowCol.setCellValueFactory(d -> d.getValue().allowancesProperty());
        allowCol.setCellFactory(tc -> new TableCell<>() {
            @Override protected void updateItem(Number v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty || v == null ? null : formatINR(v.doubleValue()));
            }
        });

        TableColumn<Employee, Number> dedCol = new TableColumn<>("Deductions");
        dedCol.setCellValueFactory(d -> d.getValue().deductionsProperty());
        dedCol.setCellFactory(tc -> new TableCell<>() {
            @Override protected void updateItem(Number v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty || v == null ? null : formatINR(v.doubleValue()));
            }
        });

        TableColumn<Employee, String> netCol = new TableColumn<>("Net Pay");
        netCol.setCellValueFactory(d -> {
            double net = d.getValue().getNetPay();
            return new javafx.beans.property.SimpleStringProperty(formatINR(net));
        });

        TableColumn<Employee, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(d -> d.getValue().statusProperty());
        statusCol.setCellFactory(tc -> new TableCell<>() {
            @Override protected void updateItem(String v, boolean empty) {
                super.updateItem(v, empty);
                if (empty || v == null) { setText(null); setGraphic(null); return; }
                Label badge = new Label(v);
                badge.getStyleClass().add("badge");
                badge.getStyleClass().add(v.equals("Active") ? "badge-active" :
                                          v.equals("On Leave") ? "badge-warning" : "badge-pending");
                setGraphic(badge);
                setText(null);
            }
        });

        table.getColumns().addAll(nameCol, roleCol, baseCol, allowCol, dedCol, netCol, statusCol);

        return new VBox(10, title, table);
    }

    private HBox buildBottomRow() {
        VBox breakdown = buildBreakdownCard();
        VBox summary = buildSummaryCard();
        HBox.setHgrow(breakdown, Priority.ALWAYS);
        HBox.setHgrow(summary, Priority.ALWAYS);
        HBox row = new HBox(14, breakdown, summary);
        return row;
    }

    private VBox buildBreakdownCard() {
        Label title = new Label("Payroll breakdown");
        title.getStyleClass().add("section-title");

        double gross = store.getTotalMonthlyPayroll();
        VBox bars = new VBox(10,
            barRow("Basic salary", gross * 0.76, gross, "#185FA5"),
            barRow("Allowances",   gross * 0.12, gross, "#1D9E75"),
            barRow("PF deduction", gross * 0.07, gross, "#D85A30"),
            barRow("Tax (TDS)",    gross * 0.05, gross, "#BA7517")
        );

        VBox card = new VBox(12, title, bars);
        card.getStyleClass().add("detail-card");
        return card;
    }

    private HBox barRow(String label, double value, double max, String color) {
        Label lbl = new Label(label);
        lbl.getStyleClass().add("bar-label");
        lbl.setPrefWidth(100);

        double pct = max > 0 ? value / max : 0;
        Region fill = new Region();
        fill.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 4;");
        fill.setPrefHeight(7);
        fill.setMaxWidth(Double.MAX_VALUE);

        Region track = new Region();
        track.getStyleClass().add("bar-track");
        track.setPrefHeight(7);
        track.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(track, Priority.ALWAYS);

        fill.prefWidthProperty().bind(track.widthProperty().multiply(pct));
        StackPane bar = new StackPane(track, fill);
        StackPane.setAlignment(fill, javafx.geometry.Pos.CENTER_LEFT);
        HBox.setHgrow(bar, Priority.ALWAYS);

        Label amt = new Label(formatINR(value));
        amt.getStyleClass().add("bar-amount");
        amt.setPrefWidth(90);

        HBox row = new HBox(10, lbl, bar, amt);
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        return row;
    }

    private VBox buildSummaryCard() {
        Label title = new Label("This month summary");
        title.getStyleClass().add("section-title");

        double gross = store.getTotalMonthlyPayroll();
        double ded = gross * 0.20;
        double net = gross - ded;
        int total = store.getEmployees().size();
        long active = store.getActiveEmployeeCount();

        VBox rows = new VBox(0,
            summaryRow("Gross payroll", formatINR(gross)),
            summaryRow("Total deductions", formatINR(ded)),
            summaryRow("Net disbursement", formatINR(net)),
            summaryRow("Payroll processed", active + " / " + total),
            summaryRow("Next payroll date", "May 1, 2026")
        );

        VBox card = new VBox(12, title, rows);
        card.getStyleClass().add("detail-card");
        return card;
    }

    private HBox summaryRow(String label, String value) {
        Label lbl = new Label(label);
        lbl.getStyleClass().add("summary-label");
        Label val = new Label(value);
        val.getStyleClass().add("summary-value");
        HBox row = new HBox();
        HBox.setHgrow(lbl, Priority.ALWAYS);
        row.getStyleClass().add("summary-row");
        row.getChildren().addAll(lbl, val);
        return row;
    }

    private void showRunPayrollAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Payroll");
        alert.setHeaderText("Payroll Processing");
        alert.setContentText("Payroll for April 2026 has been queued for processing.");
        alert.showAndWait();
    }

    private String formatINR(double amount) {
        return inr.format(amount);
    }
}
