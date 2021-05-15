package cz.muni.fi.umlspnp.views;

import com.google.gson.annotations.Expose;
import cz.muni.fi.umlspnp.views.sequencediagram.SequenceDiagramView;
import cz.muni.fi.umlspnp.views.deploymentdiagram.DeploymentDiagramView;
import cz.muni.fi.umlspnp.views.common.layouts.StringModalWindow;
import cz.muni.fi.umlspnp.views.common.layouts.PropertiesModalWindow;
import java.io.PrintStream;
import java.util.ArrayList;
import javafx.beans.property.StringProperty;
import javafx.geometry.Orientation;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *  Main view rendering the main application layout and main menu.
 * 
 */
public class MainView extends VBox{
    @Expose(serialize = true)
    private DeploymentDiagramView deploymentDiagramView;
    @Expose(serialize = true)
    private SequenceDiagramView sequenceDiagramView;

    private final Tab deploymentDiagramTab;
    private final Tab sequenceDiagramTab;
    private final Stage appStage;
    private final MenuBar mainMenu = new MenuBar();
    
    
    public MainView(Stage appStage){
        this.appStage = appStage;
        
        /* Main menu */
        this.getChildren().add(mainMenu);

        /* Diagram tabs */
        TabPane diagramTabPane = new TabPane();
        diagramTabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        deploymentDiagramTab = new Tab("Deployment Diagram");
        sequenceDiagramTab = new Tab("Sequence Diagram");
        diagramTabPane.getTabs().addAll(deploymentDiagramTab, sequenceDiagramTab);

        reinit();

        /* Console output tabs */
        TabPane debugTabPane = new TabPane();
        debugTabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        Tab stdoutTab = new Tab("Stdout");
        Tab stderrTab = new Tab("Stderr");
        
        /* Stdout */
        StreamViewer stdoutViewer = new StreamViewer();
        PrintStream stdoutPrintStream = new PrintStream(stdoutViewer, true);
        System.setOut(stdoutPrintStream);
        stdoutTab.setContent(stdoutViewer.getContainer());
        
        /* Stderr */
        StreamViewer stderrViewer = new StreamViewer();
        PrintStream stderrPrintStream = new PrintStream(stderrViewer, true);
        System.setErr(stderrPrintStream);
        stderrTab.setContent(stderrViewer.getContainer());
        stderrViewer.getTextArea().setStyle("-fx-text-fill: red;");

        debugTabPane.getTabs().addAll(stderrTab, stdoutTab);
        
        /* Vertical split */
        SplitPane splitPane = new SplitPane();
        splitPane.orientationProperty().setValue(Orientation.VERTICAL);
        splitPane.setDividerPosition(0, 0.75);

        splitPane.getItems().addAll(diagramTabPane, debugTabPane);
        
        HBox.setHgrow(splitPane, Priority.ALWAYS);
        VBox.setVgrow(splitPane, Priority.ALWAYS);
        
        this.getChildren().add(splitPane);
    }
    
    public final void reinit() {
        deploymentDiagramView = new DeploymentDiagramView();
        deploymentDiagramTab.setContent(deploymentDiagramView);
        
        sequenceDiagramView = new SequenceDiagramView();
        sequenceDiagramTab.setContent(sequenceDiagramView);
    }

    public Stage getAppStage(){
        return appStage;
    }
 
    public void addMenu(Menu newMenu) {
        mainMenu.getMenus().add(newMenu);
    }
    
    public DeploymentDiagramView getDeploymentDiagramView(){
        return deploymentDiagramView;
    }
    
    public SequenceDiagramView getSequenceDiagramView(){
        return sequenceDiagramView;
    }
    
    public void createStringModalWindow(String windowName, String labelText, StringProperty output, String restrictionRegex){
        var modal = new StringModalWindow(appStage, windowName, labelText, output);
        if(restrictionRegex != null)
            modal.setStringRestrictionRegex(restrictionRegex);
        modal.showAndWait();
    }
    
    public void createPropertiesModalWindow(String windowName, ArrayList sections){
        var modal = new PropertiesModalWindow(appStage, windowName, sections);
        modal.showAndWait();
    }
}