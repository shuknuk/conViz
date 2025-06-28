package com.traceview.gui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

/**
 * Welcome View Controller
 * 
 * Handles the initial welcome screen with file drop and open functionality.
 * Implements the minimalist design with centered layout and clean aesthetics.
 */
public class WelcomeViewController {
    
    private static final Logger logger = LoggerFactory.getLogger(WelcomeViewController.class);
    
    private final TraceViewApplication application;
    private VBox rootContainer;
    
    public WelcomeViewController(TraceViewApplication application) {
        this.application = application;
    }
    
    /**
     * Create the welcome view UI
     */
    public VBox createWelcomeView() {
        rootContainer = new VBox();
        rootContainer.getStyleClass().add("welcome-container");
        rootContainer.setAlignment(Pos.CENTER);
        
        // Create UI elements
        Label iconLabel = createIconLabel();
        Label titleLabel = createTitleLabel();
        Label subtitleLabel = createSubtitleLabel();
        Button openFileButton = createOpenFileButton();
        
        // Add elements to container
        rootContainer.getChildren().addAll(
            iconLabel,
            titleLabel,
            subtitleLabel,
            openFileButton
        );
        
        // Set up drag and drop
        setupDragAndDrop();
        
        return rootContainer;
    }
    
    /**
     * Create the application icon/logo
     */
    private Label createIconLabel() {
        Label iconLabel = new Label("📊"); // Timeline/chart emoji as placeholder
        iconLabel.getStyleClass().add("welcome-icon");
        return iconLabel;
    }
    
    /**
     * Create the main title
     */
    private Label createTitleLabel() {
        Label titleLabel = new Label("TraceView");
        titleLabel.getStyleClass().add("welcome-title");
        return titleLabel;
    }
    
    /**
     * Create the subtitle/instruction text
     */
    private Label createSubtitleLabel() {
        Label subtitleLabel = new Label("Drop a trace file to begin");
        subtitleLabel.getStyleClass().add("welcome-subtitle");
        return subtitleLabel;
    }
    
    /**
     * Create the open file button
     */
    private Button createOpenFileButton() {
        Button openFileButton = new Button("Open File...");
        openFileButton.getStyleClass().add("open-file-button");
        openFileButton.setOnAction(event -> openFileDialog());
        return openFileButton;
    }
    
    /**
     * Set up drag and drop functionality
     */
    private void setupDragAndDrop() {
        // Handle drag over
        rootContainer.setOnDragOver(this::handleDragOver);
        
        // Handle drag entered (visual feedback)
        rootContainer.setOnDragEntered(event -> {
            if (event.getGestureSource() != rootContainer && event.getDragboard().hasFiles()) {
                rootContainer.getStyleClass().add("drop-target");
            }
            event.consume();
        });
        
        // Handle drag exited (remove visual feedback)
        rootContainer.setOnDragExited(event -> {
            rootContainer.getStyleClass().remove("drop-target");
            event.consume();
        });
        
        // Handle drop
        rootContainer.setOnDragDropped(this::handleDragDropped);
    }
    
    /**
     * Handle drag over events
     */
    private void handleDragOver(DragEvent event) {
        if (event.getGestureSource() != rootContainer && event.getDragboard().hasFiles()) {
            // Check if any of the dragged files is a .tview file
            List<File> files = event.getDragboard().getFiles();
            boolean hasTraceFile = files.stream()
                .anyMatch(file -> file.getName().toLowerCase().endsWith(".tview"));
            
            if (hasTraceFile) {
                event.acceptTransferModes(TransferMode.COPY);
            }
        }
        event.consume();
    }
    
    /**
     * Handle file drop events
     */
    private void handleDragDropped(DragEvent event) {
        Dragboard dragboard = event.getDragboard();
        boolean success = false;
        
        if (dragboard.hasFiles()) {
            List<File> files = dragboard.getFiles();
            
            // Find the first .tview file
            File traceFile = files.stream()
                .filter(file -> file.getName().toLowerCase().endsWith(".tview"))
                .findFirst()
                .orElse(null);
            
            if (traceFile != null) {
                logger.info("Dropped trace file: {}", traceFile.getAbsolutePath());
                loadTraceFile(traceFile);
                success = true;
            }
        }
        
        // Remove visual feedback
        rootContainer.getStyleClass().remove("drop-target");
        
        event.setDropCompleted(success);
        event.consume();
    }
    
    /**
     * Open file dialog to select a trace file
     */
    private void openFileDialog() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Trace File");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("TraceView Files (*.tview)", "*.tview")
        );
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        
        File selectedFile = fileChooser.showOpenDialog(application.getPrimaryStage());
        if (selectedFile != null) {
            logger.info("Selected trace file: {}", selectedFile.getAbsolutePath());
            loadTraceFile(selectedFile);
        }
    }
    
    /**
     * Load a trace file and transition to main view
     */
    private void loadTraceFile(File traceFile) {
        try {
            // Validate file exists and is readable
            if (!traceFile.exists() || !traceFile.canRead()) {
                logger.error("Cannot read trace file: {}", traceFile.getAbsolutePath());
                showErrorDialog("Cannot read the selected file.");
                return;
            }
            
            // TODO: Add basic validation of trace file format
            
            // Transition to main view
            application.showMainView(traceFile.getAbsolutePath());
            
        } catch (Exception e) {
            logger.error("Error loading trace file: {}", traceFile.getAbsolutePath(), e);
            showErrorDialog("Error loading trace file: " + e.getMessage());
        }
    }
    
    /**
     * Show error dialog (simplified for MVP)
     */
    private void showErrorDialog(String message) {
        // For MVP, we'll just log the error
        // In a full implementation, you'd show a proper error dialog
        logger.error("Error: {}", message);
        
        // TODO: Implement proper error dialog
        System.err.println("Error: " + message);
    }
}
