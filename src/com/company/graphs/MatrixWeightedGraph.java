package com.company.graphs;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MatrixWeightedGraph<V> {
    private Map<V, Integer> vertexIndexes;
    private int countVertices = 0;
    private int[][] matrix;

    public MatrixWeightedGraph() {
        vertexIndexes = new HashMap<>();
        matrix = new int[0][0];
    }

    public MatrixWeightedGraph(Map<V, Integer> vertexIndexes, int[][] matrix) {
        this.vertexIndexes = vertexIndexes;
        this.matrix = matrix;
        countVertices = matrix.length;
    }

    public void addVertex(V value, Map<V, Integer> weightMap) {
        if (!vertexIndexes.containsKey(value)) {
            vertexIndexes.put(value, countVertices++);
            expandMatrix(1);
        }

        countVertices += addIndex(countVertices, weightMap);
        expandMatrix(countVertices - matrix.length);
        setLink(value, weightMap);
    }

    public void addVertex(V value, V link, int weight){
        if(!vertexIndexes.containsKey(value)){
            vertexIndexes.put(value, countVertices++);
            expandMatrix(1);
        }
        countVertices += addIndex(countVertices, link);
        expandMatrix(countVertices - matrix.length);
        setLink(value, link, weight);
    }

    public void setLink(V vertex, V link, int weight){
        int vertexIndex = vertexIndexes.get(vertex);
            int linkVertexIndex = vertexIndexes.get(link);
            matrix[vertexIndex][linkVertexIndex] = weight;
            matrix[linkVertexIndex][vertexIndex] = weight;
    }

    private void expandMatrix(int exp) {
        if(exp == 0)return;
        if(matrix.length == 0){
            matrix = new int[exp][exp];
            return;
        }
        int newLength = matrix.length + exp;
        matrix = Arrays.copyOf(matrix, newLength);
        for (int index = 0; index < matrix.length; index++) {
            if (matrix[index] == null){
                matrix[index] = new int[newLength];
                continue;
            }
            matrix[index] = Arrays.copyOf(matrix[index], newLength);
        }
    }

    private int addIndex(int from, Map<V, Integer> weightMap) {
        int add = 0;
        for (Map.Entry<V, Integer> vw :
                weightMap.entrySet()) {
            if(!vertexIndexes.containsKey(vw.getKey())){
                vertexIndexes.put(vw.getKey(), from++);
                add++;
            }

        }
        return add;
    }

    private int addIndex(int from, V value){
        if(!vertexIndexes.containsKey(value)){
            vertexIndexes.put(value, from);
            return 1;
        }
        return 0;
    }

    public void setLink(V vertex, Map<V, Integer> weightMap){
        if(!vertexIndexes.containsKey(vertex))return;
        int vertexIndex = vertexIndexes.get(vertex);
        for(Map.Entry<V, Integer> vw:
                            weightMap.entrySet()){
            if(!vertexIndexes.containsKey(vw.getKey()))
                continue;
            int linkVertexIndex = vertexIndexes.get(vw.getKey());
            matrix[vertexIndex][linkVertexIndex] = vw.getValue();
            matrix[linkVertexIndex][vertexIndex] = vw.getValue();
        }

    }

    public Map<V, Integer> waysMap(V vertexName){

        Map<V, Integer> ways = new HashMap<>();
        ways.put(vertexName, 0);
        ArrayDeque<V> deque = new ArrayDeque<>();
        deque.push(vertexName);

        while (!deque.isEmpty()){
            for (V curr :
                    deque) {
                int distance = ways.get(curr);
                int vertexIndex = vertexIndexes.get(curr);
                for (int index = 0; index < matrix[vertexIndex].length ; index++) {
                    if(matrix[vertexIndex][index] > 0){
                        V foundVertex = getVertexName(index);
                        if(!ways.containsKey(foundVertex)){
                            ways.put(foundVertex, distance + matrix[vertexIndex][index]);
                            if (foundVertex != null) {
                                deque.addLast(foundVertex);
                            }
                        }

                    }
                }
                deque.pop();
            }
        }
        return ways;

    }

    private V getVertexName(int index){
        for (Map.Entry<V, Integer> kv:
                                    vertexIndexes.entrySet()){
            if(kv.getValue().equals(index))
                return kv.getKey();
        }
        return null;
    }
}
