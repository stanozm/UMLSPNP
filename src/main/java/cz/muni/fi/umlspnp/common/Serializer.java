package cz.muni.fi.umlspnp.common;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import cz.muni.fi.umlspnp.controllers.MainController;
import cz.muni.fi.umlspnp.controllers.deploymentdiagram.DeploymentDiagramController;
import cz.muni.fi.umlspnp.controllers.sequencediagram.SequenceDiagramController;
import cz.muni.fi.umlspnp.models.ConnectionFailure;
import cz.muni.fi.umlspnp.models.MainModel;
import cz.muni.fi.umlspnp.models.OperationEntry;
import cz.muni.fi.umlspnp.models.OperationType;
import cz.muni.fi.umlspnp.models.deploymentdiagram.Artifact;
import cz.muni.fi.umlspnp.models.deploymentdiagram.CommunicationLink;
import cz.muni.fi.umlspnp.models.deploymentdiagram.DeploymentDiagram;
import cz.muni.fi.umlspnp.models.deploymentdiagram.DeploymentTarget;
import cz.muni.fi.umlspnp.models.deploymentdiagram.State;
import cz.muni.fi.umlspnp.models.deploymentdiagram.StateOperation;
import cz.muni.fi.umlspnp.models.deploymentdiagram.StateTransition;
import cz.muni.fi.umlspnp.models.sequencediagram.Activation;
import cz.muni.fi.umlspnp.models.sequencediagram.Lifeline;
import cz.muni.fi.umlspnp.models.sequencediagram.Loop;
import cz.muni.fi.umlspnp.models.sequencediagram.Message;
import cz.muni.fi.umlspnp.models.sequencediagram.MessageFailureType;
import cz.muni.fi.umlspnp.models.sequencediagram.SequenceDiagram;
import cz.muni.fi.umlspnp.views.DiagramView;
import cz.muni.fi.umlspnp.views.MainView;
import cz.muni.fi.umlspnp.views.common.BasicRectangle;
import cz.muni.fi.umlspnp.views.common.ConnectionSlot;
import cz.muni.fi.umlspnp.views.common.ConnectionView;
import cz.muni.fi.umlspnp.views.common.NamedRectangle;
import cz.muni.fi.umlspnp.views.deploymentdiagram.CommunicationLinkView;
import cz.muni.fi.umlspnp.views.deploymentdiagram.DeploymentDiagramView;
import cz.muni.fi.umlspnp.views.sequencediagram.ActivationView;
import cz.muni.fi.umlspnp.views.sequencediagram.LifelineView;
import cz.muni.fi.umlspnp.views.sequencediagram.LoopView;
import cz.muni.fi.umlspnp.views.sequencediagram.MessageView;
import cz.muni.fi.umlspnp.views.sequencediagram.SequenceDiagramView;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;
import org.hildan.fxgson.FxGson;

/**
 * Serializes and deserializes the UML models and views to/from their JSON representation.
 *
 */
public class Serializer {
    private final Gson origGson;
    private final Gson gson;
    
    private final MainController mainController;
    private final MainModel mainModel;
    private final MainView mainView;
    
    public Serializer(MainController mainController) {
        this.mainController = mainController;
        this.mainModel = mainController.getModel();
        this.mainView = mainController.getView();
        
        origGson = FxGson.coreBuilder()
            .setPrettyPrinting()
            .excludeFieldsWithoutExposeAnnotation()
            .create();
        
        var mainControllerSerializer = createMainControllerSerializer();
        var artifactSerializer = createArtifactSerializer();
        var communicationLinkSerializer = createCommunicationLinkSerializer();
        var lifelineSerializer = createLifelineSerializer();
        var messageSerializer = createMessageSerializer();
        var rectangleSerializer = createRectangleSerializer();
        var connectionSlotSerializer = createConnectionSlotSerializer();
        var connectionViewSerializer = createConnectionViewSerializer();
        var lifelineViewSerializer = createLifelineViewSerializer(rectangleSerializer);
        
        var mainControllerDeserializer = createMainControllerDeserializer();
        var deploymentDiagramDeserializer = createDeploymentDiagramDeserializer();
        var sequenceDiagramDeserializer = createSequenceDiagramDeserializer();
        var deploymentDiagramViewDeserializer = createDeploymentDiagramViewDeserializer();
        var sequenceDiagramViewDeserializer = createSequenceDiagramViewDeserializer();
        
        gson = FxGson.coreBuilder()
                .setPrettyPrinting()
                .excludeFieldsWithoutExposeAnnotation()

                /* Serializers */
                .registerTypeAdapter(MainController.class, mainControllerSerializer)
                // Models
                .registerTypeAdapter(Artifact.class, artifactSerializer)
                .registerTypeAdapter(DeploymentTarget.class, artifactSerializer)
                .registerTypeAdapter(CommunicationLink.class, communicationLinkSerializer)
                .registerTypeAdapter(Lifeline.class, lifelineSerializer)
                .registerTypeAdapter(Message.class, messageSerializer)
                // Views
                .registerTypeAdapter(BasicRectangle.class, rectangleSerializer)
                .registerTypeAdapter(NamedRectangle.class, rectangleSerializer)
                .registerTypeAdapter(ConnectionSlot.class, connectionSlotSerializer)
                .registerTypeAdapter(CommunicationLinkView.class, connectionViewSerializer)
                .registerTypeAdapter(LifelineView.class, lifelineViewSerializer)
                .registerTypeAdapter(ActivationView.class, rectangleSerializer)
                .registerTypeAdapter(MessageView.class, connectionViewSerializer)
                .registerTypeAdapter(LoopView.class, rectangleSerializer)
                
                /* Deserializers */
                .registerTypeAdapter(MainController.class, mainControllerDeserializer)
                // Models
                .registerTypeAdapter(DeploymentDiagram.class, deploymentDiagramDeserializer)
                .registerTypeAdapter(SequenceDiagram.class, sequenceDiagramDeserializer)
                // Views
                .registerTypeAdapter(DeploymentDiagramView.class, deploymentDiagramViewDeserializer)
                .registerTypeAdapter(SequenceDiagramView.class, sequenceDiagramViewDeserializer)
                .create();
    }
    
    public String toJson(MainController mainController) {
        return gson.toJson(mainController);
    }

    public void fromJson(String json) {
        gson.fromJson(json, MainController.class);
    }

    public boolean saveToFile(File file) {
        try {
            var writer = new FileWriter(file);
            gson.toJson(mainController, writer);
            writer.close();
            return true;
        } catch (IOException ex) {
            System.err.println("Error: unable to save file. " + ex.getMessage());
        }
        return false;
    }
    
    public boolean loadFromFile(File file) {
        try {
            var reader = new FileReader(file);
            gson.fromJson(reader, MainController.class);
            reader.close();
            return true;
        } catch (IOException ex) {
            System.err.println("Error: unable to load file. " + ex.getMessage());
        }
        return false;
    }
    
    private JsonSerializer<MainController> createMainControllerSerializer() {
        return new JsonSerializer<MainController>() {
            @Override
            public JsonElement serialize(MainController src, Type typeOfSrc, JsonSerializationContext context) {
                var modelTree = context.serialize(src.getModel(), MainModel.class);
                var viewTree = context.serialize(src.getView(), MainView.class);

                var json = new JsonObject();
                json.add("model", modelTree);
                json.add("view", viewTree);
                return json;
            }
        };
    }
            
    private JsonSerializer<Artifact> createArtifactSerializer() {
        return new JsonSerializer<Artifact>() {  
            @Override
            public JsonElement serialize(Artifact src, Type typeOfSrc, JsonSerializationContext context) {
                var element = origGson.toJsonTree(src);
                JsonObject json = element.getAsJsonObject();
                var parent = src.getParent();
                if(src instanceof DeploymentTarget)
                    json.addProperty("type", "DeploymentTarget");
                else
                    json.addProperty("type", "Artifact");

                if(parent == null)
                    json.add("parentId", null);
                else
                    json.addProperty("parentId", parent.getObjectInfo().getID());
                json.addProperty("objectId", src.getObjectInfo().getID());
                return json;
            }
        };
    }
            
    private JsonSerializer<CommunicationLink> createCommunicationLinkSerializer() {
        return new JsonSerializer<CommunicationLink>() {  
            @Override
            public JsonElement serialize(CommunicationLink src, Type typeOfSrc, JsonSerializationContext context) {
                var element = origGson.toJsonTree(src);
                JsonObject json = element.getAsJsonObject();
                var dtFrom = src.getFirst();
                var dtTo = src.getSecond();
                json.addProperty("firstDtId", dtFrom.getObjectInfo().getID());
                json.addProperty("secondDtId", dtTo.getObjectInfo().getID());
                return json;
            }
        };
    }

    private JsonSerializer<Lifeline> createLifelineSerializer() {
        return new JsonSerializer<Lifeline>() {  
            @Override
            public JsonElement serialize(Lifeline src, Type typeOfSrc, JsonSerializationContext context) {
                var element = origGson.toJsonTree(src);
                JsonObject json = element.getAsJsonObject();
                var artifact = src.getArtifact();
                json.addProperty("artifactId", artifact.getObjectInfo().getID());
                return json;
            }
        };
    }

    private JsonSerializer<Message> createMessageSerializer() {
        return new JsonSerializer<Message>() {  
            @Override
            public JsonElement serialize(Message src, Type typeOfSrc, JsonSerializationContext context) {
                var element = origGson.toJsonTree(src);
                JsonObject json = element.getAsJsonObject();
                var activationFrom = src.getFrom();
                var activationTo = src.getTo();
                json.addProperty("fromActivationId", activationFrom.getObjectInfo().getID());
                json.addProperty("toActivationId", activationTo.getObjectInfo().getID());
                return json;
            }
        };
    }
            
    private JsonSerializer<BasicRectangle> createRectangleSerializer() {
        return new JsonSerializer<BasicRectangle>() {  
            @Override
            public JsonElement serialize(BasicRectangle src, Type typeOfSrc, JsonSerializationContext context) {
                JsonObject json = new JsonObject();
                json.addProperty("objectId", src.getObjectInfo().getID());
                json.addProperty("width", src.getWidth());
                json.addProperty("height", src.getHeight());
                json.addProperty("x", src.getTranslateX());
                json.addProperty("y", src.getTranslateY());
                return json;
            }
        };
    }
    
    private JsonSerializer<LifelineView> createLifelineViewSerializer(JsonSerializer<BasicRectangle> rectangleSerializer) {
        return new JsonSerializer<LifelineView>() {  
            @Override
            public JsonElement serialize(LifelineView src, Type typeOfSrc, JsonSerializationContext context) {
                JsonObject json = rectangleSerializer.serialize(src, typeOfSrc, context).getAsJsonObject();
                json.add("ActivationViews", context.serialize(src.getActivationViews()));
                return json;
            }
        };
    }

    private JsonSerializer<ConnectionSlot> createConnectionSlotSerializer() {
        return new JsonSerializer<ConnectionSlot>() {  
            @Override
            public JsonElement serialize(ConnectionSlot src, Type typeOfSrc, JsonSerializationContext context) {
                JsonObject json = new JsonObject();
                json.addProperty("x", src.getTranslateX());
                json.addProperty("y", src.getTranslateY());
                return json;
            }
        };
    }
            
    private JsonSerializer<ConnectionView> createConnectionViewSerializer() {
        return new JsonSerializer<ConnectionView>() {  
            @Override
            public JsonElement serialize(ConnectionView src, Type typeOfSrc, JsonSerializationContext context) {
                JsonObject json = new JsonObject();
                json.addProperty("objectId", src.getObjectInfo().getID());

                var fromSlot = context.serialize(src.getSourceConnectionSlot(), ConnectionSlot.class);
                json.add("fromSlot", fromSlot);

                var toSlot = context.serialize(src.getDestinationConnectionSlot(), ConnectionSlot.class);
                json.add("toSlot", toSlot);

                return json;
            }
        };
    }

    private JsonDeserializer<MainController> createMainControllerDeserializer() {
        return new JsonDeserializer<MainController>() {
            @Override
            public MainController deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
                mainModel.clear();
                mainView.reinit();

                JsonObject jobj = je.getAsJsonObject();
                var modelJson = jobj.get("model");
                var viewJson = jobj.get("view");

                jdc.deserialize(modelJson, MainModel.class);
                jdc.deserialize(viewJson, MainView.class);

                return null;
            }
        };
    }

    private JsonDeserializer<DeploymentDiagram> createDeploymentDiagramDeserializer() {
        return new JsonDeserializer<DeploymentDiagram>() {
            @Override
            public DeploymentDiagram deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
                var result = origGson.fromJson(je, DeploymentDiagram.class);
                mainModel.setDeploymentDiagram(result);
                mainController.setDeploymentDiagramController(new DeploymentDiagramController(mainModel, mainView));

                recreateDeploymentDiagram(result);

                JsonObject jobj = je.getAsJsonObject();
                var elements = jobj.get("allElements").getAsJsonObject();
                var nodes = elements.get("allNodes").getAsJsonObject().entrySet();
                var links = elements.get("allConnections").getAsJsonObject().entrySet();

                var tmpArtifacts = new HashSet<JsonElement>();
                var tmpDeploymentTargets = new HashSet<JsonElement>();
                nodes.forEach(entry -> {
                    var node = entry.getValue().getAsJsonObject();
                    var nodeType = node.get("type").getAsString();
                    if(nodeType.equals("Artifact"))
                        tmpArtifacts.add(entry.getValue());
                    else if(nodeType.equals("DeploymentTarget"))
                        tmpDeploymentTargets.add(entry.getValue());
                });
                deserializeDeploymentTargets(origGson, result, tmpDeploymentTargets);
                deserializeArtifacts(origGson, result, tmpArtifacts);

                var tmpLinks = new HashSet<JsonElement>();
                links.forEach(entry -> {
                    tmpLinks.add(entry.getValue());
                });

                deserializeCommunicationLinks(origGson, result, tmpLinks);
                return result;
            }
        };
    }

    private JsonDeserializer<SequenceDiagram> createSequenceDiagramDeserializer() {
        return new JsonDeserializer<SequenceDiagram>() {
            @Override
            public SequenceDiagram deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
                var dd = mainModel.getDeploymentDiagram();
                var result = origGson.fromJson(je, SequenceDiagram.class);
                mainModel.setSequenceDiagram(result);
                mainController.setSequenceDiagramController(new SequenceDiagramController(mainModel, mainView));

                recreateSequenceDiagram(dd, result);

                JsonObject jobj = je.getAsJsonObject();
                var elements = jobj.get("allElements").getAsJsonObject();
                var lifelines = elements.get("allNodes").getAsJsonObject().entrySet();
                var messages = elements.get("allConnections").getAsJsonObject().entrySet();

                var tmpLifelines = new HashSet<JsonElement>();
                lifelines.forEach(entry -> {
                    tmpLifelines.add(entry.getValue());
                });
                deserializeLifelines(origGson, dd, result, tmpLifelines);

                var tmpMessages = new HashSet<JsonElement>();
                messages.forEach(entry -> {
                    tmpMessages.add(entry.getValue());
                });
                deserializeMessages(origGson, dd, result, tmpMessages);

                return result;
            }
        };
    }
            
            
    private JsonDeserializer<DeploymentDiagramView> createDeploymentDiagramViewDeserializer() {
        return new JsonDeserializer<DeploymentDiagramView>() {  
            @Override
            public DeploymentDiagramView deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
                var ddv = mainView.getDeploymentDiagramView();

                JsonObject jobj = je.getAsJsonObject();
                var elements = jobj.get("allElements").getAsJsonObject();
                var nodes = elements.get("allNodes").getAsJsonObject().entrySet();
                var links = elements.get("allConnections").getAsJsonObject().entrySet();

                nodes.forEach(entry -> {
                    deserializeRectangle(ddv, entry.getValue());
                });

                links.forEach(entry -> {
                    deserializeConnectionView(ddv, entry.getValue());
                });

                return null;
            }
        };
    }

    private JsonDeserializer<SequenceDiagramView> createSequenceDiagramViewDeserializer() {
        return new JsonDeserializer<SequenceDiagramView>() {  
            @Override
            public SequenceDiagramView deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
                var sdv = mainView.getSequenceDiagramView();

                JsonObject jobj = je.getAsJsonObject();
                var loops = jobj.get("loopViews").getAsJsonObject().entrySet();
                var elements = jobj.get("allElements").getAsJsonObject();
                var lifelines = elements.get("allNodes").getAsJsonObject().entrySet();
                var messages = elements.get("allConnections").getAsJsonObject().entrySet();

                lifelines.forEach(entry -> {
                    var element = entry.getValue();
                    var jsonObj = element.getAsJsonObject();
                    var activationsArr = jsonObj.get("ActivationViews").getAsJsonArray();
                    deserializeRectangle(sdv, element);
                    activationsArr.forEach(activation -> {
                        deserializeRectangle(sdv, activation);
                    });
                });

                messages.forEach(entry -> {
                    deserializeConnectionView(sdv, entry.getValue());
                });

                loops.forEach(entry -> {
                    deserializeRectangle(sdv, entry.getValue());
                });

                mainController.getSequenceDiagramController().refresh();
                return null;
            }
        };
    }

    private void recreateDeploymentDiagram(DeploymentDiagram dd) {
        var tmpOpTypes = new HashSet<>(dd.getOperationTypes());
        tmpOpTypes.forEach(opType -> {
            dd.removeOperationType(opType);
            dd.addOperationType(new OperationType(opType.getName()));
        });
        
        var tmpRedundancyGroups = new HashSet<>(dd.getRedundancyGroups());
        tmpRedundancyGroups.forEach(rg -> {
            dd.removeRedundancyGroup(rg);
        });
        tmpRedundancyGroups.forEach(rg -> {
            dd.addRedundancyGroup(rg.getGroupID());
        });
        
        var tmpLinkTypes = new HashSet<>(dd.getAllLinkTypes());
        tmpLinkTypes.forEach(lt -> {
            dd.removeLinkType(lt);
            dd.createLinkType(lt.getName(), lt.getRate());
        });
    }
        
    private void recreateSequenceDiagram(DeploymentDiagram dd, SequenceDiagram sd) {
        var tmpLoops = new HashSet<>(sd.getLoops());
        sd.clearLoops();
        
        tmpLoops.forEach(loop -> {
            var newLoop = new Loop();
            newLoop.setId(loop.getObjectInfo().getID());
            sd.addLoop(newLoop);
            newLoop.setIterations(loop.getIterations());
            newLoop.setRestartRate(loop.getRestartRate());
        });
    }
    
    private void recreateArtifact(DeploymentDiagram dd, Artifact a, Integer parentId) {
        DeploymentTarget parent = null;
        parent = (DeploymentTarget) dd.getNode(parentId);
        var newArtifact = new Artifact(a.getNameProperty().getValue(),
                                       parent);

        newArtifact.getObjectInfo().setID(a.getObjectInfo().getID());
        parent.addInnerNode(newArtifact);
        dd.addNode(newArtifact);
    }
    
    private void recreateDeploymentTarget(DeploymentDiagram dd, DeploymentTarget dt, Integer parentId) {
        DeploymentTarget parent = null;
        if(parentId != null)
            parent = (DeploymentTarget) dd.getNode(parentId);
        var newDT = new DeploymentTarget(dd.getElementContainer(),
                                         dt.getNameProperty().getValue(),
                                         parent);

        newDT.getObjectInfo().setID(dt.getObjectInfo().getID());

        if(parent != null) {
            parent.addInnerNode(newDT);
        }
        
        dd.addNode(newDT);
        
        dt.getStates().forEach(state -> {
            var newState = new State(state.nameProperty().getValue());
            newState.setDefault(state.isDefaultProperty().getValue());
            newState.setStateDOWN(state.isStateDOWN());
            newState.setLocked(state.isLocked());
            newDT.addState(newState);
        });
        
        dt.getStateTransitions().forEach(stateTransition -> {
            State stateFrom = newDT.getState(stateTransition.getStateFrom().nameProperty().getValue());
            State stateTo = newDT.getState(stateTransition.getStateTo().nameProperty().getValue());

            newDT.addStateTransition(new StateTransition(stateFrom,
                                                         stateTo,
                                                         stateTransition.nameProperty().getValue(),
                                                         stateTransition.rateProperty().getValue()));
        });
        
        dt.getStateOperations().forEach(stateOperation -> {
            var newOperation = new StateOperation(newDT.getState(stateOperation.getState().nameProperty().getValue()));
            stateOperation.getOperationEntries().forEach(entry -> {
                OperationType operationType = dd.getOperationType(entry.getOperationType().getName());
                newOperation.addOperationEntry(new OperationEntry(operationType, entry.getSpeedLimit()));
            });

            newDT.addStateOperation(newOperation);
        });

        var rg = dt.getRedundancyGroup();
        if(rg != null) {
            newDT.setRedundancyGroup(dd.getRedundancyGroup(rg.getGroupID()));
        }
    }
    
    private void recreateCommunicationLink(DeploymentDiagram dd, CommunicationLink cl, Integer firstDtId, Integer secondDtId) {
        var firstDt = dd.getDeploymentTarget(firstDtId);
        var secondDt = dd.getDeploymentTarget(secondDtId);
        var newLink = new CommunicationLink(firstDt, secondDt, dd.getAllLinkTypes());
        newLink.setId(cl.getObjectInfo().getID());
        
        dd.addCommunicationLink(newLink);
        firstDt.addInnerConnection(newLink);
        secondDt.addInnerConnection(newLink);

        var linkTypeName = cl.getLinkType().getName();
        newLink.setLinkType(dd.getLinkType(linkTypeName));
        
        cl.getLinkFailures().forEach(failure -> {
            var newFailure = new ConnectionFailure(failure.nameProperty().getValue(), failure.rateProperty().getValue());
            newLink.addLinkFailure(newFailure);
        });
    }
    
    private void recreateLifeline(DeploymentDiagram dd, SequenceDiagram sd, Lifeline lifeline, Integer artifactId) {
        var artifact = dd.getNode(artifactId);
        var newLifeline = new Lifeline(artifact);
        newLifeline.setId(lifeline.getObjectInfo().getID());

        sd.addLifeline(newLifeline);

        lifeline.getActivations().forEach(activation -> {
            var newActivation = new Activation(newLifeline);
            newActivation.setId(activation.getObjectInfo().getID());
            newLifeline.addActivation(newActivation);
        });
    }
    
    private void recreateMessage(DeploymentDiagram dd, SequenceDiagram sd, Message m, Integer fromActivationId, Integer toActivationId) {
        var fromActivation = sd.getActivation(fromActivationId);
        var toActivation = sd.getActivation(toActivationId);
        var newMessage = new Message(fromActivation, toActivation);
        newMessage.setId(m.getObjectInfo().getID());
        
        sd.addMessage(newMessage);
        
        newMessage.nameProperty().setValue(m.nameProperty().getValue());
        
        var executionTime = m.getExecutionTime();
        if(executionTime != null)
            newMessage.setExecutionTime(m.getExecutionTimeValue());
        
        var messageSize = m.getMessageSize();
        if(messageSize != null)
            newMessage.setMessageSize(messageSize.messageSizeProperty().getValue());
        
        var opType = m.getOperationType();
        if(opType != null)
            newMessage.setOperationType(dd.getOperationType(opType.getName()));
        
        m.getMessageFailures().forEach(failure -> {
            newMessage.addMessageFailure(new MessageFailureType(failure.nameProperty().getValue(),
                                                                failure.rateProperty().getValue(),
                                                                failure.causeHWfailProperty().getValue()));
        });
    }

    private void deserializeArtifacts(Gson origGson, DeploymentDiagram result, Set<JsonElement> tmpNodes) {
        tmpNodes.forEach(node -> {
            var artifact = origGson.fromJson(node, Artifact.class);
            var nodeObj = node.getAsJsonObject();
            var parentId = nodeObj.get("parentId").getAsInt();
            recreateArtifact(result, artifact, parentId);
        });
    }
    
    private void deserializeDeploymentTargets(Gson origGson, DeploymentDiagram result, Set<JsonElement> tmpNodes) {
        var removedNodes = new HashSet<JsonElement>();
        // The deserialization needs to be performed in the correct order (due to parental structure)
        while(tmpNodes.size() > 0) {
            removedNodes.clear();
            for(var node : tmpNodes) {
                var dt = origGson.fromJson(node, DeploymentTarget.class);
                var nodeObj = node.getAsJsonObject();
                var parentId = nodeObj.get("parentId");
                if(parentId.isJsonNull()) {
                    recreateDeploymentTarget(result, dt, null);
                    removedNodes.add(node);
                }
                else if(result.getNode(parentId.getAsInt()) != null) {
                    recreateDeploymentTarget(result, dt, parentId.getAsInt());
                    removedNodes.add(node);
                }
            }
            tmpNodes.removeIf(node -> removedNodes.contains(node));
        }
    }
    
    private void deserializeCommunicationLinks(Gson origGson, DeploymentDiagram result, Set<JsonElement> tmpLinks) {
        for(var link : tmpLinks) {
             var cl = origGson.fromJson(link, CommunicationLink.class);
             var linkObj = link.getAsJsonObject();
             var firstDtId = linkObj.get("firstDtId");
             var secondDtId = linkObj.get("secondDtId");
            recreateCommunicationLink(result, cl, firstDtId.getAsInt(), secondDtId.getAsInt());
         }
    }
    
    private void deserializeLifelines(Gson origGson, DeploymentDiagram dd, SequenceDiagram result, Set<JsonElement> tmpLifelines) {
        for(var lifeline : tmpLifelines) {
             var l = origGson.fromJson(lifeline, Lifeline.class);
             var lifelineObj = lifeline.getAsJsonObject();
             var artifactId = lifelineObj.get("artifactId");
            recreateLifeline(dd, result, l, artifactId.getAsInt());
         }
    }

    private void deserializeMessages(Gson origGson, DeploymentDiagram dd, SequenceDiagram result, Set<JsonElement> tmpMessages) {
        for(var message : tmpMessages) {
             var m = origGson.fromJson(message, Message.class);
             var messageObj = message.getAsJsonObject();
             var fromActivationId = messageObj.get("fromActivationId");
             var toActivationId = messageObj.get("toActivationId");
            recreateMessage(dd, result, m, fromActivationId.getAsInt(), toActivationId.getAsInt());
         }
    }
    
    private void deserializeRectangle(DiagramView dv, JsonElement rectangle) {
        var nodeObj = rectangle.getAsJsonObject();

        var nodeId = nodeObj.get("objectId").getAsInt();
        var width = nodeObj.get("width").getAsDouble();
        var height = nodeObj.get("height").getAsDouble();
        var x = nodeObj.get("x").getAsDouble();
        var y = nodeObj.get("y").getAsDouble();

        var nodeView = dv.getNode(nodeId);
        nodeView.changeDimensions(width, height);
        nodeView.setTranslateX(x);
        nodeView.setTranslateY(y);
    }
    
    private void deserializeConnectionView(DiagramView dv, JsonElement connection) {
        var connectionObj = connection.getAsJsonObject();

        var connectionId = connectionObj.get("objectId").getAsInt();
        var fromSlotObj = connectionObj.get("fromSlot").getAsJsonObject();
        var fromSlotX = fromSlotObj.get("x").getAsDouble();
        var fromSlotY = fromSlotObj.get("y").getAsDouble();
        
        var toSlotObj = connectionObj.get("toSlot").getAsJsonObject();
        var toSlotX = toSlotObj.get("x").getAsDouble();
        var toSlotY = toSlotObj.get("y").getAsDouble();
        
        var connectionView = dv.getConnection(connectionId);
        var sourceSlot = connectionView.getSourceConnectionSlot();
        var destinationSlot = connectionView.getDestinationConnectionSlot();
        
        sourceSlot.setTranslateX(fromSlotX);
        sourceSlot.setTranslateY(fromSlotY);
        
        destinationSlot.setTranslateX(toSlotX);
        destinationSlot.setTranslateY(toSlotY);
    }
}
