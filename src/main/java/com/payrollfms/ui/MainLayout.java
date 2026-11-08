package com.payrollfms.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import com.payrollfms.ui.views.*;

public class MainLayout extends BorderPane {

    private VBox sidebar;
    private StackPane contentArea;

    private DashboardView dashboardView;
    private EmployeeView employeeView;
    private PayrollView payrollView;
    private SalarySlipView salarySlipView;
    private ReportsView reportsView;

    public MainLayout() {
        buildSidebar();
        buildContent();
        setLeft(sidebar);
        setCenter(contentArea);
        showDashboard();
    }

    private void buildSidebar() {
        sidebar = new VBox();
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(210);

        Label logoTitle = new Label("PayRoll FMS");
        logoTitle.getStyleClass().add("logo-title");
        Label logoSub = new Label("Fitness Management");
        logoSub.getStyleClass().add("logo-sub");

        VBox logoBox = new VBox(4, logoTitle, logoSub);
        logoBox.getStyleClass().add("logo-box");

        String[] navItems = {"Dashboard", "Employees", "Payroll", "Salary Slips", "Reports"};
        VBox navBox = new VBox(2);
        navBox.setPadding(new Insets(10, 0, 0, 0));

        for (String item : navItems) {
            HBox navItem = createNavItem(item);
            navBox.getChildren().add(navItem);
        }

        sidebar.getChildren().addAll(logoBox, navBox);
    }

    private HBox createNavItem(String label) {
        Region dot = new Region();
        dot.getStyleClass().add("nav-dot");
        dot.setPrefSize(7, 7);

        Label lbl = new Label(label);
        lbl.getStyleClass().add("nav-label");

        HBox item = new HBox(10, dot, lbl);
        item.getStyleClass().add("nav-item");
        item.setPadding(new Insets(10, 16, 10, 16));

        item.setOnMouseClicked(e -> {
            sidebar.lookupAll(".nav-item").forEach(n -> n.getStyleClass().remove("nav-item-active"));
            item.getStyleClass().add("nav-item-active");
            switch (label) {
                case "Dashboard"   -> showDashboard();
                case "Employees"   -> showEmployees();
                case "Payroll"     -> showPayroll();
                case "Salary Slips"-> showSalarySlips();
                case "Reports"     -> showReports();
            }
        });
        return item;
    }

    private void buildContent() {
        contentArea = new StackPane();
        contentArea.getStyleClass().add("content-area");

        dashboardView  = new DashboardView();
        employeeView   = new EmployeeView();
        payrollView    = new PayrollView();
        salarySlipView = new SalarySlipView();
        reportsView    = new ReportsView();
    }

    private void setView(Region view) {
        contentArea.getChildren().setAll(view);
    }

    private void showDashboard()   { setView(dashboardView); }
    private void showEmployees()   { setView(employeeView); }
    private void showPayroll()     { setView(payrollView); }
    private void showSalarySlips() { setView(salarySlipView); }
    private void showReports()     { setView(reportsView); }
}
