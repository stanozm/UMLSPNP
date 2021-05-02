package cz.muni.fi.umlspnp.views.common.layouts;

import cz.muni.fi.umlspnp.views.TransformatorOptionConstant;
import cz.muni.fi.umlspnp.views.TransformatorOptionDouble;
import cz.muni.fi.umlspnp.views.TransformatorOptionInteger;
import java.util.Collection;
import java.util.function.Consumer;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * A modal window which sets transform properties and performs 
 * the model transformation itself.
 *
 */
public class TransformModalWindow extends ModalWindow {
    private final Button transformButton;
    private final Button closeButton;
    
    private final CheckBox debugInfoCheckBox;
    private final CheckBox debugPrintCheckBox;
    
    private final RadioButton simulationRadio;
    private final RadioButton numericRadio;
    
    private final GridPane numericGroup;
    private int numericGroupIndex = 0;

    private final GridPane simulationGroup;
    private int simulationGroupIndex = 0;
    
    public TransformModalWindow(Stage parentStage, String windowName, Consumer onTransform) {
        super(parentStage, windowName);
        
        simulationGroup = new GridPane();
        simulationGroup.setHgap(5);
        simulationGroup.setVgap(5);
        
        numericGroup = new GridPane();
        numericGroup.setHgap(5);
        numericGroup.setVgap(5);

        debugInfoCheckBox = new CheckBox("Print debug info to stderr");
        debugInfoCheckBox.setSelected(true);

        debugPrintCheckBox = new CheckBox("Generate debug print segment");
        
        Label solutionLabel = new Label("Solution method:");
        ToggleGroup radioGroup = new ToggleGroup();
        simulationRadio = new RadioButton("Simulation");
        numericRadio = new RadioButton("Numeric analysis");
        simulationRadio.setToggleGroup(radioGroup);
        simulationRadio.setSelected(true);
        numericRadio.setToggleGroup(radioGroup);
        
        simulationRadio.setOnAction(eh -> {
            if(simulationRadio.isSelected()) {
                numericGroup.setVisible(false);
                simulationGroup.setVisible(true);
            }
        });

        numericRadio.setOnAction(eh -> {
            if(numericRadio.isSelected()) {
                numericGroup.setVisible(true);
                simulationGroup.setVisible(false);
            }
        });

        closeButton = new Button("Close");
        closeButton.setOnAction((e) -> {
            close();
        });
        transformButton = new Button("Transform");
        transformButton.setOnAction((ActionEvent e) -> {
            onTransform.accept(this);
        });

        var generalLabel = new Label("General:");

        rootGrid.setHgap(10);
        rootGrid.setVgap(10);
        
        rootGrid.add(generalLabel, 0, 0);
        rootGrid.add(debugInfoCheckBox, 0, 1, 2, 1);
        rootGrid.add(debugPrintCheckBox, 0, 2, 2, 1);
        rootGrid.add(solutionLabel, 0, 3);
        rootGrid.add(simulationRadio, 0, 4);
        rootGrid.add(numericRadio, 1, 4);
        
        rootGrid.add(simulationGroup, 0, 5, 2, 1);
        rootGrid.add(numericGroup, 0, 5, 2, 1);
        numericGroup.setVisible(false);
        
        rootGrid.add(transformButton, 0, 6);
        rootGrid.add(closeButton, 1, 6);
    }
    
    public GridPane getSimulationGroup() {
        return simulationGroup;
    }
    
    public GridPane getNumericGroup() {
        return numericGroup;
    }
    
    public boolean getDebugInfoSelected() {
        return debugInfoCheckBox.isSelected();
    }

    public boolean getGenerateDebugPrintSegmentSelected() {
        return debugPrintCheckBox.isSelected();
    }
    
    public void addConstantInputItem(String labelText, Collection<String> stringItems, int selectedIndex, boolean isSimulation) {
        var constantOption = createConstantOption(labelText, stringItems, selectedIndex);
        var label = new Label(labelText);
        addInputItem(label, constantOption, isSimulation);
    }

    public void addIntegerInputItem(String labelText, Integer initValue, boolean isSimulation) {
        var input = new TransformatorOptionInteger(labelText, initValue.toString());
        var label = new Label(labelText);
        addInputItem(label, input, isSimulation);
    }

    public void addDoubleInputItem(String labelText, Double initValue, boolean isSimulation) {
        var input = new TransformatorOptionDouble(labelText, initValue.toString());
        var label = new Label(labelText);
        addInputItem(label, input, isSimulation);
    }
    
    public void addInputItem(Label label, Node input, boolean isSimulation) {
        if(isSimulation) {
            simulationGroup.add(label, 0, simulationGroupIndex);
            simulationGroup.add(input, 1, simulationGroupIndex++);
        }
        else {
            numericGroup.add(label, 0, numericGroupIndex);
            numericGroup.add(input, 1, numericGroupIndex++);
        }
    }

    public TransformatorOptionConstant createConstantOption(String labelText, Collection<String> stringItems, int selectedIndex) {
        var items = FXCollections.observableArrayList(stringItems);
        var comboBox = new TransformatorOptionConstant(labelText);

        comboBox.setItems(items);
        comboBox.getSelectionModel().selectFirst();
        comboBox.getSelectionModel().select(selectedIndex);
        return comboBox;
    }
    
    public boolean simulationSelected() {
        return simulationRadio.isSelected();
    }

}
