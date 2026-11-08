package com.payrollfms.ui.views;

import com.payrollfms.data.DataStore;
import com.payrollfms.model.Employee;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.text.NumberFormat;
import java.util.Locale;

public class EmployeeView extends VBox {

    private final DataStore store = DataStore.getInstance();
    private final NumberFormat inr = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
    private TableView<Employee> table;

    public EmployeeView() {
        setSpacing(0);
        getStyleClass().add("view-root");
        getChildren().addAll(buildTopBar(), buildContent());
    }

    private HBox buildTopBar() {
        Label title = new Label("Employees");
        title.getStyleClass().add("view-title");
        Label sub = new Label("Manage your gym staff and payroll details");
        sub.getStyleClass().add("view-sub");
        VBox titleBox = new VBox(3, title, sub);

        Button addBtn = new Button("+ Add Employee");
        addBtn.getStyleClass().add("btn-primary");
        addBtn.setOnAction(e -> showEmployeeDialog(null));

        HBox bar = new HBox();
        bar.getStyleClass().add("topbar");
        HBox.setHgrow(titleBox, Priority.ALWAYS);
        bar.getChildren().addAll(titleBox, addBtn);
        return bar;
    }

    private ScrollPane buildContent() {
        VBox content = new VBox(16);
        content.getStyleClass().add("view-content");

        TextField search = new TextField();
        search.setPromptText("Search by name or role...");
        search.getStyleClass().add("search-field");
        search.setPrefWidth(260);

        table = new TableView<>(store.getEmployees());
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);
        table.getStyleClass().add("payroll-table");

        TableColumn<Employee, Number> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(d -> d.getValue().idProperty());
        idCol.setPrefWidth(50);

        TableColumn<Employee, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(d -> d.getValue().nameProperty());

        TableColumn<Employee, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(d -> d.getValue().roleProperty());

        TableColumn<Employee, String> deptCol = new TableColumn<>("Department");
        deptCol.setCellValueFactory(d -> d.getValue().departmentProperty());

        TableColumn<Employee, Number> salaryCol = new TableColumn<>("Base Salary");
        salaryCol.setCellValueFactory(d -> d.getValue().baseSalaryProperty());
        salaryCol.setCellFactory(tc -> new TableCell<>() {
            @Override protected void updateItem(Number v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty || v == null ? null : inr.format(v.doubleValue()));
            }
        });

        TableColumn<Employee, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(d -> d.getValue().statusProperty());
        statusCol.setCellFactory(tc -> new TableCell<>() {
            @Override protected void updateItem(String v, boolean empty) {
                super.updateItem(v, empty);
                if (empty || v == null) { setText(null); setGraphic(null); return; }
                Label b = new Label(v);
                b.getStyleClass().addAll("badge", v.equals("Active") ? "badge-active" : "badge-warning");
                setGraphic(b); setText(null);
            }
        });

        TableColumn<Employee, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(120);
        actionsCol.setCellFactory(tc -> new TableCell<>() {
            private final Button edit = new Button("Edit");
            private final Button del  = new Button("Delete");
            {
                edit.getStyleClass().add("btn-secondary");
                del.getStyleClass().addAll("btn-secondary", "btn-danger");
                edit.setOnAction(e -> showEmployeeDialog(getTableView().getItems().get(getIndex())));
                del.setOnAction(e -> deleteEmployee(getTableView().getItems().get(getIndex())));
            }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : new HBox(6, edit, del));
            }
        });

        table.getColumns().addAll(idCol, nameCol, roleCol, deptCol, salaryCol, statusCol, actionsCol);

        search.textProperty().addListener((obs, old, val) -> filterTable(val));

        content.getChildren().addAll(search, table);

        ScrollPane sp = new ScrollPane(content);
        sp.setFitToWidth(true);
        sp.setFitToHeight(true);
        sp.getStyleClass().add("edge-to-edge");
        VBox.setVgrow(sp, Priority.ALWAYS);
        return sp;
    }

    private void filterTable(String query) {
        if (query == null || query.isBlank()) {
            table.setItems(store.getEmployees());
            return;
        }
        String q = query.toLowerCase();
        table.setItems(store.getEmployees().filtered(e ->
            e.getName().toLowerCase().contains(q) ||
            e.getRole().toLowerCase().contains(q) ||
            e.getDepartment().toLowerCase().contains(q)
        ));
    }

    private void showEmployeeDialog(Employee existing) {
        Dialog<Employee> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Add Employee" : "Edit Employee");
        dialog.setHeaderText(existing == null ? "New employee details" : "Edit — " + existing.getName());

        ButtonType saveType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);

        GridPane form = new GridPane();
        form.setHgap(12); form.setVgap(10);
        form.setPadding(new Insets(16));

        TextField tfName   = field(existing == null ? "" : existing.getName());
        TextField tfRole   = field(existing == null ? "" : existing.getRole());
        TextField tfDept   = field(existing == null ? "" : existing.getDepartment());
        TextField tfEmail  = field(existing == null ? "" : existing.getEmail());
        TextField tfPhone  = field(existing == null ? "" : existing.getPhone());
        TextField tfSalary = field(existing == null ? "" : String.valueOf((int)existing.getBaseSalary()));
        TextField tfAllow  = field(existing == null ? "" : String.valueOf((int)existing.getAllowances()));
        TextField tfDed    = field(existing == null ? "" : String.valueOf((int)existing.getDeductions()));
        ComboBox<String> cbStatus = new ComboBox<>();
        cbStatus.getItems().addAll("Active", "On Leave", "Terminated");
        cbStatus.setValue(existing == null ? "Active" : existing.getStatus());

        form.addRow(0, lbl("Name"),       tfName,   lbl("Role"), tfRole);
        form.addRow(1, lbl("Department"), tfDept,   lbl("Email"), tfEmail);
        form.addRow(2, lbl("Phone"),      tfPhone,  lbl("Status"), cbStatus);
        form.addRow(3, lbl("Base Salary"), tfSalary, lbl("Allowances"), tfAllow);
        form.addRow(4, lbl("Deductions"), tfDed);

        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().setPrefWidth(520);

        dialog.setResultConverter(btn -> {
            if (btn == saveType) {
                try {
                    String name = tfName.getText().trim();
                    if (name.isEmpty()) throw new IllegalArgumentException("Name required");
                    double sal = Double.parseDouble(tfSalary.getText().trim());
                    double allow = Double.parseDouble(tfAllow.getText().trim());
                    double ded = Double.parseDouble(tfDed.getText().trim());

                    if (existing == null) {
                        Employee emp = new Employee(
                            store.nextEmployeeId(), name, tfRole.getText().trim(),
                            tfDept.getText().trim(), tfEmail.getText().trim(),
                            tfPhone.getText().trim(), sal, allow, ded,
                            cbStatus.getValue(), java.time.LocalDate.now().toString()
                        );
                        store.getEmployees().add(emp);
                    } else {
                        existing.setName(name);
                        existing.setRole(tfRole.getText().trim());
                        existing.setDepartment(tfDept.getText().trim());
                        existing.setEmail(tfEmail.getText().trim());
                        existing.setPhone(tfPhone.getText().trim());
                        existing.setBaseSalary(sal);
                        existing.setAllowances(allow);
                        existing.setDeductions(ded);
                        existing.setStatus(cbStatus.getValue());
                        table.refresh();
                    }
                } catch (NumberFormatException ex) {
                    new Alert(Alert.AlertType.ERROR, "Salary, allowances, and deductions must be numbers.").showAndWait();
                } catch (IllegalArgumentException ex) {
                    new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void deleteEmployee(Employee e) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
            "Delete " + e.getName() + "? This cannot be undone.", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText("Confirm delete");
        confirm.showAndWait().filter(r -> r == ButtonType.YES)
               .ifPresent(r -> store.getEmployees().remove(e));
    }

    private TextField field(String val) {
        TextField tf = new TextField(val);
        tf.setPrefWidth(160);
        return tf;
    }

    private Label lbl(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size: 13px;");
        return l;
    }
}
