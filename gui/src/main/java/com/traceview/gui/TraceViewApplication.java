package com.traceview.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * TraceView JavaFX Application
 * 
 * Main entry point for the TraceView GUI application.
 * Follows the "Clarity in Motion" design philosophy with minimal, clean interface.
 */
public class TraceViewApplication extends Application {
    
    private static final Logger logger = LoggerFactory.getLogger(TraceViewApplication.class);
    private static final String APP_TITLE = "TraceView";
    private static final int MIN_WIDTH = 1000;
    private static final int MIN_HEIGHT = 600;
    
    private Stage primaryStage;
    private Scene scene;
    private boolean isDarkTheme = false;
    
    @Override
    public void start(Stage stage) throws IOException {
        logger.info("Starting TraceView application");
        
        this.primaryStage = stage;
        
        // Configure the primary stage
        setupPrimaryStage();
        
        // Show welcome view initially
        showWelcomeView();
        
        // Show the stage
        primaryStage.show();
        
        logger.info("TraceView application started successfully");
    }
    
    /**
     * Configure the primary stage properties
     */
    private void setupPrimaryStage() {
        primaryStage.setTitle(APP_TITLE);
        primaryStage.setMinWidth(MIN_WIDTH);
        primaryStage.setMinHeight(MIN_HEIGHT);
        
        // Set initial size
        primaryStage.setWidth(1200);
        primaryStage.setHeight(800);
        
        // Center on screen
        primaryStage.centerOnScreen();
        
        // Detect system theme preference
        isDarkTheme = isSystemDarkTheme();
    }
    
    /**
     * Show the welcome view (initial screen)
     */
    private void showWelcomeView() {
        try {
            WelcomeViewController welcomeController = new WelcomeViewController(this);
            VBox welcomeRoot = welcomeController.createWelcomeView();
            
            scene = new Scene(welcomeRoot);
            applyTheme();
            
            primaryStage.setScene(scene);
            
        } catch (Exception e) {
            logger.error("Failed to load welcome view", e);
            throw new RuntimeException("Failed to load welcome view", e);
        }
    }
    
    /**
     * Show the main analysis view (after loading a trace file)
     */
    public void showMainView(String traceFilePath) {
        try {
            logger.info("Loading main view for trace file: {}", traceFilePath);
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/traceview.fxml"));
            TraceViewController controller = new TraceViewController(this, traceFilePath);
            loader.setController(controller);
            
            VBox mainRoot = loader.load();
            
            scene = new Scene(mainRoot);
            applyTheme();
            
            primaryStage.setScene(scene);
            
            // Update window title to include file name
            String fileName = traceFilePath.substring(traceFilePath.lastIndexOf('/') + 1);
            primaryStage.setTitle(APP_TITLE + " - " + fileName);
            
        } catch (IOException e) {
            logger.error("Failed to load main view", e);
            throw new RuntimeException("Failed to load main view", e);
        }
    }
    
    /**
     * Toggle between light and dark themes
     */
    public void toggleTheme() {
        isDarkTheme = !isDarkTheme;
        applyTheme();
        logger.debug("Theme toggled to: {}", isDarkTheme ? "dark" : "light");
    }
    
    /**
     * Apply the current theme to the scene
     */
    private void applyTheme() {
        if (scene != null) {
            scene.getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            
            // Apply theme class to root
            scene.getRoot().getStyleClass().removeAll("light-theme", "dark-theme");
            scene.getRoot().getStyleClass().add(isDarkTheme ? "dark-theme" : "light-theme");
        }
    }
    
    /**
     * Get the current theme state
     */
    public boolean isDarkTheme() {
        return isDarkTheme;
    }
    
    /**
     * Get the primary stage
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }
    
    /**
     * Detect if the system is using dark theme
     * This is a simplified version - in a real implementation,
     * you might want to use platform-specific APIs
     */
    private boolean isSystemDarkTheme() {
        // For now, default to light theme
        // TODO: Implement proper system theme detection
        return false;
    }
    
    /**
     * Main method - entry point for the application
     */
    public static void main(String[] args) {
        // Set system properties for better macOS integration
        System.setProperty("prism.lcdtext", "false");
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", APP_TITLE);
        
        logger.info("Launching TraceView application");
        launch(args);
    }
}
