package com.company.graphs;

public class EdgeName {
    private String vertexName1, vertexName2;

    public EdgeName(String vertexName1, String vertexName2) {
        this.vertexName1 = vertexName1;
        this.vertexName2 = vertexName2;
    }

    public String getVertexName1() {
        return vertexName1;
    }

    public String getVertexName2() {
        return vertexName2;
    }

    public boolean equalsName(String name1, String name2){
        return (vertexName1.equals(name1) && vertexName2.equals(name2))||
                (vertexName2.equals(name1) && vertexName1.equals(name2));
    }

    @Override
    public String toString() {
        return vertexName1 + "-" + vertexName2;
    }
}
