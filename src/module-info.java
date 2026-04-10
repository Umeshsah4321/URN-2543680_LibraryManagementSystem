module Library_management_System2 {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires transitive java.sql;
    requires transitive javafx.graphics;
    requires javafx.base;

    opens application to javafx.graphics, javafx.fxml;
    opens application.controllers to javafx.graphics, javafx.fxml;
    opens application.models to javafx.base, javafx.graphics;
    opens application.views to javafx.graphics, javafx.fxml;

    exports application;
    exports application.controllers;
    exports application.models;
    exports application.views;
}
