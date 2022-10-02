module vlad.lailo.lab11 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires eu.hansolo.tilesfx;

    opens vlad.lailo.lab to javafx.fxml;
    exports vlad.lailo.lab;
    exports vlad.lailo.lab.controller;
    exports vlad.lailo.lab.utils;
}