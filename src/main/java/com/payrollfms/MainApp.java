package com.payrollfms;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.payrollfms.ui.MainLayout;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        MainLayout mainLayout = new MainLayout();
        Scene scene = new Scene(mainLayout, 1100, 680);
        scene.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());

        primaryStage.setTitle("PayRoll Fitness Management System");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
