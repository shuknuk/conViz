module com.traceview.gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.traceview.shared;
    requires org.slf4j;
    requires ch.qos.logback.classic;
    
    exports com.traceview.gui;
    
    opens com.traceview.gui to javafx.fxml;
}
