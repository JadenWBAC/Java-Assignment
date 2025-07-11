module com.javafxtutorial.olaseven {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens com.javafxtutorial.olaseven to javafx.fxml;
    exports com.javafxtutorial.olaseven;
}