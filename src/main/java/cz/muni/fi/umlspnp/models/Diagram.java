package cz.muni.fi.umlspnp.models;

public interface Diagram {
    public BasicNode getNode(int modelID);
    public Connection getConnection(int modelID);
}
