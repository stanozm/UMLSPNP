package cz.muni.fi.umlspnp.common;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Constants and functions used in the modeling part of the project.
 * 
 */
public class Utils {
    public static boolean __DEBUG_CREATE_SAMPLE_DATA = true;
    
    public static String SPNP_NAME_RESTRICTION_REGEX = "^([a-zA-Z])[a-zA-Z0-9\\s_]*$";
    public static String SPNP_NAME_RESTRICTION_REPLACE_REGEX = "[^a-zA-Z0-9_]";
    private static int objectIDCounter = 0;
    
    public static int generateObjectID(){
        return ++objectIDCounter;
    }

    public static double getStringWidth(String text){
        final Text t = new Text(text);
        Scene _unused = new Scene(new Group(t));
        t.applyCss();
        return t.getLayoutBounds().getWidth();
    }
    
    public static String shortenString(String text, int maxLength){
        String shortenedString = text;
        if (text.length() > maxLength){
            shortenedString = text.substring(0, maxLength).concat("...");
        }
        return shortenedString;
    }
    
    public static Point2D getPositionRelativeTo(Node child, Node parent, Point2D coordinates){
        Point2D finalPosition = coordinates;
        var currentNode = child;
        while(currentNode.getParent() != null){
            finalPosition = currentNode.localToParent(finalPosition);
            currentNode = currentNode.getParent();
            if(currentNode.equals(parent))
                return finalPosition;
        }
        return null;
    }
    
    public static double getAngle(Point2D sourcePoint, Point2D destinationPoint) {
        double angleRadians = (double) Math.atan2(destinationPoint.getY() - sourcePoint.getY(), destinationPoint.getX() - sourcePoint.getX());
        if(angleRadians < 0)
            return angleRadians + Math.PI * 2;
        return angleRadians;
    }
    
    public static Alert createAlertDialog(String title, String header, String errorMessage){
        Alert errorDialog = new Alert(Alert.AlertType.ERROR);
        errorDialog.setTitle(title);
        errorDialog.setHeaderText(header);
        errorDialog.setContentText(errorMessage);
        return errorDialog;
    }
    
    public static void setStageIcon(Class appClass, Stage stage) {
        var icon = new Image(appClass.getResourceAsStream("/icons/icon.png"));
        stage.getIcons().add(icon);
    }
}
