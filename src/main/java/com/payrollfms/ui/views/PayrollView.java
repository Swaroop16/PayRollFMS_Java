package com.payrollfms.ui.views;

import com.payrollfms.data.DataStore;
import com.payrollfms.model.PayrollRecord;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.text.NumberFormat;
import java.util.Locale;

public class PayrollView extends VBox {

    private final DataStore store = DataStore.getInstance();
    private final NumberFormat inr = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

    public PayrollView() {
        setSpacing(0);
        getStyleClass().add("view-root");
        getChildren().addAll(buildTopBar(), buildContent());
    }

    private HBox buildTopBar() {
        Label title = new Label("Payroll");
        title.getStyleClass().add("view-title");
        Label sub = new Label("Monthly payroll records and payment status");
        sub.getStyleClass().add("view-sub");
        VBox titleBox = new VBox(3, title, sub);

        ComboBox<String> monthFilter = new ComboBox<>();
        monthFilter.getItems().addAll("All Months", "April 2026", "March 2026", "February 2026");
        monthFilter.setValue("April 2026");

        Button runBtn = new Button("Process Payments");
        runBtn.getStyleClass().add("btn-primary");
        runBtn.setOnAction(e -> processPayments());

        HBox bar = new HBox(10);
        bar.getStyleClass().add("topbar");
        HBox.setHgrow(titleBox, Priority.ALWAYS);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.getChildren().addAll(titleBox, monthFilter, runBtn);
        return bar;
    }

    private ScrollPane buildContent() {
        VBox content = new VBox(16);
        content.getStyleClass().add("view-content");

        TableView<PayrollRecord> table = new TableView<>(store.getPayrollRecords()
            .filtered(r -> r.getMonth().equals("April 2026")));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getStyleClass().add("payroll-table");
        VBox.setVgrow(table, Priority.ALWAYS);
        table.setPrefHeight(500);

        TableColumn<PayrollRecord, String> nameCol = new TableColumn<>("Employee");
        nameCol.setCellValueFactory(d -> d.getValue().employeeNameProperty());

        TableColumn<PayrollRecord, String> monthCol = new TableColumn<>("Month");
        monthCol.setCellValueFactory(d -> d.getValue().monthProperty());

        TableColumn<PayrollRecord, Number> grossCol = new TableColumn<>("Gross Pay");
        grossCol.setCellValueFactory(d -> d.getValue().grossPayProperty());
        grossCol.setCellFactory(currencyCell());

        TableColumn<PayrollRecord, Number> pfCol = new TableColumn<>("PF");
        pfCol.setCellValueFactory(d -> d.getValue().pfProperty());
        pfCol.setCellFactory(currencyCell());

        TableColumn<PayrollRecord, Number> tdsCol = new TableColumn<>("TDS");
        tdsCol.setCellValueFactory(d -> d.getValue().tdsProperty());
        tdsCol.setCellFactory(currencyCell());

        TableColumn<PayrollRecord, Number> netCol = new TableColumn<>("Net Pay");
        netCol.setCellValueFactory(d -> d.getValue().netPayProperty());
        netCol.setCellFactory(currencyCell());

        TableColumn<PayrollRecord, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(d -> d.getValue().paymentStatusProperty());
        statusCol.setCellFactory(tc -> new TableCell<>() {
            @Override protected void updateItem(String v, boolean empty) {
                super.updateItem(v, empty);
                if (empty || v == null) { setGraphic(null); return; }
                Label b = new Label(v);
                b.getStyleClass().addAll("badge",
                    v.equals("Paid") ? "badge-active" : "badge-pending");
                setGraphic(b); setText(null);
            }
        });

        table.getColumns().addAll(nameCol, monthCol, grossCol, pfCol, tdsCol, netCol, statusCol);

        content.getChildren().add(table);

        ScrollPane sp = new ScrollPane(content);
        sp.setFitToWidth(true);
        sp.setFitToHeight(true);
        sp.getStyleClass().add("edge-to-edge");
        VBox.setVgrow(sp, Priority.ALWAYS);
        return sp;
    }

    private <T> javafx.util.Callback<TableColumn<T, Number>, TableCell<T, Number>> currencyCell() {
        return tc -> new TableCell<>() {
            @Override protected void updateItem(Number v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty || v == null ? null : inr.format(v.doubleValue()));
            }
        };
    }

    private void processPayments() {
        store.getPayrollRecords().stream()
            .filter(r -> r.getMonth().equals("April 2026") && r.getPaymentStatus().equals("Pending"))
            .forEach(r -> r.setPaymentStatus("Paid"));
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Payments Processed");
        a.setHeaderText(null);
        a.setContentText("All pending April 2026 payments have been marked as Paid.");
        a.showAndWait();
    }
}
