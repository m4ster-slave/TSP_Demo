package com.tsp;

import com.tsp.gui.MainView;
import javafx.application.Application;

public class Main {
    public static void main(String[] args) {
        System.setProperty("javafx.preloader", "none");
        Application.launch(MainView.class, args);
    }
}
