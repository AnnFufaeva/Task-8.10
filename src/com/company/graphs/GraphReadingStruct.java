package com.company.graphs;

import java.util.Vector;

public class GraphReadingStruct {
    private Vector<Vector<String>> data;
    private Vector<String> vertexNames;

    public GraphReadingStruct(Vector<Vector<String>> data, Vector<String> vertexNames) {
        this.data = data;
        this.vertexNames = vertexNames;
    }

    public Vector<Vector<String>> getData() {
        return data;
    }

    public Vector<String> getVertexNames() {
        return vertexNames;
    }
}
