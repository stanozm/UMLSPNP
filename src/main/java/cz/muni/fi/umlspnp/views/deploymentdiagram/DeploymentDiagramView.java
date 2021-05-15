package cz.muni.fi.umlspnp.views.deploymentdiagram;

import com.google.gson.annotations.Expose;
import cz.muni.fi.umlspnp.common.ElementContainer;
import cz.muni.fi.umlspnp.views.DiagramView;
import cz.muni.fi.umlspnp.views.common.Annotation;
import cz.muni.fi.umlspnp.views.common.BasicRectangle;
import cz.muni.fi.umlspnp.views.common.NamedRectangle;
import javafx.scene.Group;
import javafx.scene.input.MouseButton;

/**
 * View rendering the deployment diagram pane and providing related functionality.
 *
 */
public class DeploymentDiagramView extends DiagramView {
    private final Group root;

    @Expose(serialize = true, deserialize = false)
    private final ElementContainer<NamedRectangle, CommunicationLinkView> allElements = new ElementContainer<>();

    public DeploymentDiagramView(){
        this.root = new Group();
        
        diagramPane.getChildren().add(root);

        setMouseDraggedCallback(MouseButton.SECONDARY, (e) -> {
            moveAll(e.getSceneX(), e.getSceneY());
        });
    }

    public void moveAll(double sceneX, double sceneY) {
        double diffX = sceneX - originalPositionX;
        double diffY = sceneY - originalPositionY;
        for(var node : allElements.getNodes().values()) {
            if(!node.getParent().getClass().equals(DeploymentTargetView.class)) {
                node.setTranslateX(node.getTranslateX() + diffX);
                node.setTranslateY(node.getTranslateY() + diffY);
            }
        }
        originalPositionX = sceneX;
        originalPositionY = sceneY;
    }

    public ElementContainer getElementContainer(){
        return allElements;
    }
    
    @Override
    public NamedRectangle getNode(int objectID){
        return (NamedRectangle) allElements.getNode(objectID);
    }
    
    public DeploymentTargetView getDeploymentTargetView(int objectID){
        var node = getNode(objectID);
        if(node instanceof DeploymentTargetView)
            return (DeploymentTargetView) node;
        return null;
    }
    
    public DeploymentTargetView createDeploymentTargetView(DeploymentTargetView parentNode, int modelObjectID){
        var dt = new DeploymentTargetView(0, 10, 0, 0, 10, root, modelObjectID);
        allElements.addNode(dt, modelObjectID);
        
        registerNodeToSelect(dt, (e) -> {
            var startElement = (BasicRectangle) connectionContainer.getFirstElement();
            if(startElement != null){
                if(startElement != dt && startElement.getClass().equals(dt.getClass())){
                    connectionContainer.setSecondElement(dt);
                }
                else{
                    System.err.println("Unable to create connection. Select suitable destination node.");
                    startElement.setSelected(false);
                    connectionContainer.clear();
                }
            }
        });
        
        if(parentNode == null){
            // NOTE: Uncomment if the deployment target should not be moved outside the pane bounds
//            dt.setRestrictionsInParent(root);
            root.getChildren().add(dt);
        }
        else{
            parentNode.addInnerNode(dt);
        }
        
        dt.changeDimensions(150, 150);
        return dt;
    }
        
    public ArtifactView CreateArtifact(DeploymentTargetView parentNode, int modelObjectID){
        var newArtifact = new ArtifactView(0, 0, 0, 0, modelObjectID);
        allElements.addNode(newArtifact, modelObjectID);
        parentNode.addInnerNode(newArtifact);
        newArtifact.changeDimensions(150, 150);
        return newArtifact;
    }
        
    private boolean removeInnerNode(NamedRectangle removedNode){
        var parent = removedNode.getParentDeploymentTargetview();
        if(parent != null)
            return parent.removeInnerNode(removedNode.getObjectInfo().getID());
        return false;
    }
    
    public boolean removeNode(int objectID){
        var removedNode = getNode(objectID);

        if(removedNode == null)
            return false;

        boolean result = allElements.removeNode(objectID);
        if(result){
            if(removedNode instanceof DeploymentTargetView){
                var DTV = (DeploymentTargetView) removedNode;
                Annotation states = DTV.getStatesAnnotation();
                Annotation stateTransitions = DTV.getStateTransitionsAnnotation();
                Annotation stateOperations = DTV.getStateOperationsAnnotation();
                root.getChildren().remove(states);
                root.getChildren().remove(states.getLine());
                root.getChildren().remove(stateTransitions);
                root.getChildren().remove(stateTransitions.getLine());
                root.getChildren().remove(stateOperations);
                root.getChildren().remove(stateOperations.getLine());
            }
 
            if(!root.getChildren().remove(removedNode)){
                removeInnerNode(removedNode);
            }
        }
        return result;
    }
    
    @Override
    public CommunicationLinkView getConnection(int objectID){
        return allElements.getConnection(objectID);
    }
    
    public CommunicationLinkView createConnection(DeploymentTargetView source, DeploymentTargetView destination, int connectionModelID){
        var newConnection = new CommunicationLinkView(connectionModelID, source.getEmptySlot(), destination.getEmptySlot(), root);

        allElements.addConnection(newConnection, connectionModelID);
        root.getChildren().add(newConnection);
        newConnection.refreshLinePosition();
        return newConnection;
    }
    
    public CommunicationLinkView createConnection(int sourceID, int destinationID, int connectionModelID){
        var source = getDeploymentTargetView(sourceID);
        var destination = getDeploymentTargetView(destinationID);
        return createConnection(source, destination, connectionModelID);
    }
    
    public boolean removeConnection(int connectionModelID){
        var connection = getConnection(connectionModelID);

        if(connection == null)
            return false;

        connection.removeSlots();
        allElements.removeConnection(connectionModelID);
        root.getChildren().remove(connection);
        return true;
    }
}
