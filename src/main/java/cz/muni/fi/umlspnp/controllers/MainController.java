package cz.muni.fi.umlspnp.controllers;

import cz.muni.fi.umlspnp.SystemInfo;
import cz.muni.fi.umlspnp.common.Serializer;
import cz.muni.fi.umlspnp.controllers.deploymentdiagram.DeploymentDiagramController;
import cz.muni.fi.umlspnp.controllers.sequencediagram.SequenceDiagramController;
import cz.muni.fi.umlspnp.models.MainModel;
import cz.muni.fi.umlspnp.models.sequencediagram.Activation;
import cz.muni.fi.umlspnp.models.sequencediagram.Lifeline;
import cz.muni.fi.umlspnp.transformations.DefaultTransformator;
import cz.muni.fi.umlspnp.views.MainView;
import cz.muni.fi.umlspnp.views.TransformatorOptionConstant;
import cz.muni.fi.umlspnp.views.TransformatorOptionDouble;
import cz.muni.fi.umlspnp.views.TransformatorOptionInteger;
import cz.muni.fi.umlspnp.views.common.layouts.AboutModalWindow;
import cz.muni.fi.umlspnp.views.common.layouts.TransformModalWindow;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Consumer;
import javafx.event.ActionEvent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;

/**
 *  Main controller which handles the main menu functionality.
 *
 */
public class MainController extends BaseController<MainModel, MainView> {
    private final Menu fileMenu;
    private final Menu aboutMenu;
    
    private final Serializer serializer;
    private File file = null;
    
    private DeploymentDiagramController deploymentDiagramController;
    private SequenceDiagramController sequenceDiagramController;

    public MainController(MainModel model, MainView view){
        super(model, view, model, view);
        
        serializer = new Serializer(this);

        fileMenu = new Menu("File");
        aboutMenu = new Menu("Help");
        view.addMenu(fileMenu);
        view.addMenu(aboutMenu);

        initTransformMenu();
        initAboutMenu();
        
        setDeploymentDiagramController(new DeploymentDiagramController(mainModel, mainView));
        setSequenceDiagramController(new SequenceDiagramController(mainModel, mainView));
    }
    
    public final void setDeploymentDiagramController(DeploymentDiagramController ddc) {
        deploymentDiagramController = ddc;
    }
    
    public final DeploymentDiagramController getDeploymentDiagramController() {
        return deploymentDiagramController;
    }
    
    public final void setSequenceDiagramController(SequenceDiagramController sdc) {
        sequenceDiagramController = sdc;
    }

    public final SequenceDiagramController getSequenceDiagramController() {
        return sequenceDiagramController;
    }
    
    private void open(File file) {
        if(!file.canRead()) {
            System.err.println(String.format("Error: unable to read file \"%s\"", file.getAbsolutePath()));
            return;
        }
        if(serializer.loadFromFile(file)) {
            System.err.println(String.format("Successfully loaded from \"%s\"", file.getAbsolutePath()));
        }
    }
    
    private void openFrom() {
        var fileChooser = new FileChooser();

        var extensionFilter = new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extensionFilter);
        var tmpFile = fileChooser.showOpenDialog(mainView.getAppStage());
        if(tmpFile != null) {
            open(tmpFile);
        }
    }
    
    private void save(File file) {
        if(serializer.saveToFile(file)) {
            System.err.println(String.format("Successfully saved to \"%s\"", file.getAbsolutePath()));
        }
    }
    
    private void saveAs() {
        var fileChooser = new FileChooser();
        fileChooser.setInitialFileName("uml2spnp_project");

        var extensionFilter = new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extensionFilter);
        var tmpFile = fileChooser.showSaveDialog(mainView.getAppStage());
        if(tmpFile != null) {
            file = tmpFile;
            save(file);
        }
    }
    
    private void initTransformMenu() {
        var openMenuItem = new MenuItem("Open...");
        openMenuItem.setOnAction((ActionEvent tt) -> {
            openFrom();
        });
        fileMenu.getItems().add(openMenuItem);

        var saveMenuItem = new MenuItem("Save");
        saveMenuItem.setOnAction((ActionEvent tt) -> {
            if(file == null)
                saveAs();
            else
                save(file);
        });
        fileMenu.getItems().add(saveMenuItem);
        
        var saveAsMenuItem = new MenuItem("Save as...");
        saveAsMenuItem.setOnAction((ActionEvent tt) -> {
            saveAs();
        });
        fileMenu.getItems().add(saveAsMenuItem);


        var transformMenuItem = new MenuItem("Transform...");
        Consumer<TransformModalWindow> onTransform = (transformWindow) -> {
            performPreTransformActions();

            var transformator = new DefaultTransformator(model);

            createOptions(transformator, transformWindow);
            transformator.transform(transformWindow.getGenerateComments());
            
            if(transformWindow.getDebugInfoSelected())
                transformator.printDebugInfo();
            
            if(transformWindow.getGenerateDebugPrintSegmentSelected())
                transformator.generatePrintingSegment();
            
            System.out.println(transformator.getOutput());
        };
        transformMenuItem.setOnAction((ActionEvent tt) -> {
            if(tt.getSource().equals(transformMenuItem)){
                var transformWindow = new TransformModalWindow(view.getAppStage(),
                                                               "Transform model",
                                                               onTransform);
                initSimulationOptions(transformWindow);
                initNumericOptions(transformWindow);
                transformWindow.showAndWait();
            }
        });
        fileMenu.getItems().add(transformMenuItem);
    }
    
    private void initAboutMenu() {
        var aboutMenuItem = new MenuItem("About");
        aboutMenuItem.setOnAction((ActionEvent tt) -> {
            if(tt.getSource().equals(aboutMenuItem)){
                var content = new StringBuilder();
                content.append(String.format("The UML2SPNP application allows you to create a general %n"
                                           + "system representation in UML diagrams and transform %n"
                                           + "it into CSPL code for further analysis in the SPNP tool.%n%n"));
                content.append(String.format("The program was developed as a part of my master's thesis. %n%n"));
                var version = getClass().getPackage().getImplementationVersion();
                if(version != null)
                    content.append(String.format("Version: %s%n", version));
                content.append(String.format("Java SE version: %s%n", SystemInfo.javaVersion()));
                content.append(String.format("JavaFX version: %s%n%n", SystemInfo.javafxVersion()));
                content.append(String.format("License: The 3-Clause BSD License%n"));
                content.append(String.format("©2021 Ondrej Marek%n"));
                var aboutWindow = new AboutModalWindow(view.getAppStage(),
                                                       "About",
                                                       content.toString());
                aboutWindow.showAndWait();
            }
        });
        aboutMenu.getItems().add(aboutMenuItem);
    }
    
    private void initSimulationOptions(TransformModalWindow transformWindow) {
        transformWindow.addIntegerInputItem("IOP_SIM_RUNS", 100000, true);
        
        var IOP_SIM_RUNMETHOD = new ArrayList<String>();
        IOP_SIM_RUNMETHOD.add("VAL_REPL");
        IOP_SIM_RUNMETHOD.add("VAL_BATCH");
        IOP_SIM_RUNMETHOD.add("VAL_RESTART");
        IOP_SIM_RUNMETHOD.add("VAL_SPLIT");
        IOP_SIM_RUNMETHOD.add("VAL_IS");
        IOP_SIM_RUNMETHOD.add("VAL_THIN");
        IOP_SIM_RUNMETHOD.add("VAL_ISTHIN");
        IOP_SIM_RUNMETHOD.add("VAL_REG");
        IOP_SIM_RUNMETHOD.add("VAL_ISREG");
        transformWindow.addConstantInputItem("IOP_SIM_RUNMETHOD", IOP_SIM_RUNMETHOD, 0, true);

        transformWindow.addIntegerInputItem("IOP_SIM_SEED", 52836, true);
        
        var IOP_SIM_CUMULATIVE = new ArrayList<String>();
        IOP_SIM_CUMULATIVE.add("VAL_YES");
        IOP_SIM_CUMULATIVE.add("VAL_NO");
        transformWindow.addConstantInputItem("IOP_SIM_CUMULATIVE", IOP_SIM_CUMULATIVE, 0, true);

        var IOP_SIM_STD_REPORT = new ArrayList<String>();
        IOP_SIM_STD_REPORT.add("VAL_YES");
        IOP_SIM_STD_REPORT.add("VAL_NO");
        transformWindow.addConstantInputItem("IOP_SIM_STD_REPORT", IOP_SIM_STD_REPORT, 0, true);
        
        transformWindow.addIntegerInputItem("IOP_SPLIT_LEVEL_DOWN", 60, true);
        
        var IOP_SPLIT_PRESIM = new ArrayList<String>();
        IOP_SPLIT_PRESIM.add("VAL_YES");
        IOP_SPLIT_PRESIM.add("VAL_NO");
        transformWindow.addConstantInputItem("IOP_SPLIT_PRESIM", IOP_SPLIT_PRESIM, 0, true);
        
        transformWindow.addIntegerInputItem("IOP_SPLIT_NUMBER", 6, true);
        
        var IOP_SPLIT_RESTART_FINISH = new ArrayList<String>();
        IOP_SPLIT_RESTART_FINISH.add("VAL_YES");
        IOP_SPLIT_RESTART_FINISH.add("VAL_NO");
        transformWindow.addConstantInputItem("IOP_SPLIT_RESTART_FINISH", IOP_SPLIT_RESTART_FINISH, 1, true);
        
        transformWindow.addIntegerInputItem("IOP_SPLIT_PRESIM_RUNS", 10, true);
        transformWindow.addDoubleInputItem("FOP_SIM_LENGTH", 50.0, true);
        transformWindow.addDoubleInputItem("FOP_SIM_CONFIDENCE", 0.95, true);
        transformWindow.addDoubleInputItem("FOP_SIM_ERROR", 0.1, true);
    }
    
    private void initNumericOptions(TransformModalWindow transformWindow) {
        var IOP_MC = new ArrayList<String>();
        IOP_MC.add("VAL_CTMC");
        IOP_MC.add("VAL_DTMC");
        transformWindow.addConstantInputItem("IOP_MC", IOP_MC, 0, false);

        var IOP_SSMETHOD = new ArrayList<String>();
        IOP_SSMETHOD.add("VAL_SSSOR");
        IOP_SSMETHOD.add("VAL_GASEI");
        IOP_SSMETHOD.add("VAL_POWER");
        transformWindow.addConstantInputItem("IOP_SSMETHOD", IOP_SSMETHOD, 0, false);

        var IOP_SSDETECT = new ArrayList<String>();
        IOP_SSDETECT.add("VAL_YES");
        IOP_SSDETECT.add("VAL_NO");
        transformWindow.addConstantInputItem("IOP_SSDETECT", IOP_SSDETECT, 0, false);
        
        transformWindow.addDoubleInputItem("FOP_SSPRES", 0.25, false);
        
        var IOP_TSMETHOD = new ArrayList<String>();
        IOP_TSMETHOD.add("VAL_TSUNIF");
        IOP_TSMETHOD.add("VAL_FOXUNIF");
        transformWindow.addConstantInputItem("IOP_TSMETHOD", IOP_TSMETHOD, 1, false);
        
        var IOP_CUMULATIVE = new ArrayList<String>();
        IOP_CUMULATIVE.add("VAL_YES");
        IOP_CUMULATIVE.add("VAL_NO");
        transformWindow.addConstantInputItem("IOP_CUMULATIVE", IOP_CUMULATIVE, 0, false);    

        var IOP_SENSITIVITY = new ArrayList<String>();
        IOP_SENSITIVITY.add("VAL_YES");
        IOP_SENSITIVITY.add("VAL_NO");
        transformWindow.addConstantInputItem("IOP_SENSITIVITY", IOP_SENSITIVITY, 1, false);
        
        transformWindow.addIntegerInputItem("IOP_ITERATIONS", 2000, false);
        transformWindow.addDoubleInputItem("FOP_PRECISION", 0.000001, false);
    }
    
    private void createOptions(DefaultTransformator transformator, TransformModalWindow transformWindow) {
        GridPane pane;
        if(transformWindow.simulationSelected()){
            transformator.createSPNPOptionConstant("IOP_SIMULATION", "VAL_YES");
            pane = transformWindow.getSimulationGroup();
        }
        else {
            transformator.createSPNPOptionConstant("IOP_SIMULATION", "VAL_NO");
            pane = transformWindow.getNumericGroup();
        }

        pane.getChildren().forEach(child -> {
            
            if(child instanceof TransformatorOptionConstant){
                var option = (TransformatorOptionConstant) child;
                transformator.createSPNPOptionConstant(option.getKey(), option.getOptionValue());
            }
            else if(child instanceof TransformatorOptionInteger){
                var option = (TransformatorOptionInteger) child;
                transformator.createSPNPOptionInteger(option.getKey(), option.getOptionValue());
            }
            else if(child instanceof TransformatorOptionDouble){
                var option = (TransformatorOptionDouble) child;
                transformator.createSPNPOptionDouble(option.getKey(), option.getOptionValue());
            }
        });
    }

    private void createActivationSortedMessages(Activation activation) {
        var messages = new ArrayList<>(activation.getMessages());
        
        Collections.sort(messages, (m1, m2) -> {
            return m1.getOrder().compareTo(m2.getOrder());
        });

        activation.setSortedMessages(messages);
    }
    
    private void createLifelineSortedActivations(Lifeline lifeline) {
        var sequenceDiagramView = view.getSequenceDiagramView();

        var activationIDs = new ArrayList<Integer>();
        lifeline.getActivations().forEach(activation -> {
            activationIDs.add(activation.getObjectInfo().getID());
        });

        var sortedActivationViews = sequenceDiagramView.sortActivations(activationIDs);
        var sortedActivations = new ArrayList<Activation>();
        for(var activationView : sortedActivationViews){
            var activation = lifeline.getActivation(activationView.getObjectInfo().getID());
            if(activation != null){
                sortedActivations.add(activation);
            }
        }
        lifeline.setSortedActivations(sortedActivations);
    }
    
    private void performPreTransformActions() {
        model.getSequenceDiagram().getLifelines().forEach(lifeline -> {
            createLifelineSortedActivations(lifeline);

            lifeline.getActivations().forEach(activation -> {
                createActivationSortedMessages(activation);
            });
        });
    }
}
