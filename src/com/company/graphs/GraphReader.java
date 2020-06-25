package com.company.graphs;

import com.company.gui.Point;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Array;
import java.util.*;

public class GraphReader {
    public static final String VERTEX_KEYWORD = "Vertices";

    private static MatrixWeightedGraph<String> read_func(String file) throws Exception {
        FileReader reader = new FileReader(new File(file));
        Scanner fileScan = new Scanner(reader);
        if (!fileScan.hasNextLine())
            throw new Exception("Invalid file structure!");
        String[] vertexNames = fileScan.nextLine().split("(\\s|[;:,])+");
        if (!vertexNames[0].equals(VERTEX_KEYWORD))
            throw new Exception("Invalid file structure!");
        Map<String, Integer> vertexIndexes = new HashMap<>();
        for (int index = 1; index < vertexNames.length; index++)
            vertexIndexes.put(vertexNames[index], index - 1);
        int[][] matrix = new int[vertexIndexes.size()][vertexIndexes.size()];
        int row = 0;
        while (fileScan.hasNextLine()) {
            if (row >= matrix.length)
                throw new Exception("Invalid file structure!");
            String[] matrixRow = fileScan.nextLine().split("(\\s|[,;])+");
            if (matrixRow.length != matrix.length)
                throw new Exception("Invalid file structure!");
            for (int index = row; index < matrixRow.length; index++) {
                matrix[row][index] = Integer.parseInt(matrixRow[index]);
                matrix[index][row] = Integer.parseInt(matrixRow[index]);
            }
            row++;
        }
        if (row < matrix.length)
            throw new Exception("Invalid file structure!");
        reader.close();
        fileScan.close();
        return new MatrixWeightedGraph<>(vertexIndexes, matrix);
    }

    public static MatrixWeightedGraph<String> read(String file) {
        try {
            return read_func(file);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static GraphReadingStruct read_table(String file) throws Exception {
        FileReader reader = new FileReader(new File(file));
        Scanner fileScan = new Scanner(reader);
        if (!fileScan.hasNextLine())
            throw new Exception("Invalid file structure!");
        String[] vertexNames = fileScan.nextLine().split("(\\s|[;:,])+");
        if (!vertexNames[0].equals(VERTEX_KEYWORD))
            throw new Exception("Invalid file structure!");
        Vector<String> vertices = new Vector<>();
        for (int index = 1; index < vertexNames.length; index++)
            vertices.add(vertexNames[index]);
        Vector<Vector<String>> vectors = new Vector<>(vertices.size());
        int row = 0;
        while (fileScan.hasNextLine()) {
            if (row >= vertices.size())
                throw new Exception("Invalid file structure!");
            String[] matrixRow = fileScan.nextLine().split("(\\s|[,;])+");
            if (matrixRow.length != vertices.size())
                throw new Exception("Invalid file structure!");
            vectors.add(new Vector<>(Arrays.asList(matrixRow)));
            row++;
        }
        if (row < vertices.size())
            throw new Exception("Invalid file structure!");
        reader.close();
        fileScan.close();
        return new GraphReadingStruct(vectors, vertices);
    }

    public static GraphReadingStruct readInTable(String file) {
        try {
            return read_table(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static MatrixWeightedGraph<String> getGraphFromTable(Vector<Vector<String>> vectors, Vector<String> columnHeaders) {
        int[][] matrix = new int[vectors.size()][vectors.size()];
        for (int index = 0; index < matrix.length; index++) {
            matrix[index] = toPrimitive(vectors.get(index).toArray(new String[0]));
        }
        Map<String, Integer> vertices = new HashMap<>();
        for (int i = 0; i < matrix.length; i++) {
            vertices.put(columnHeaders.get(i), i);
        }
        return new MatrixWeightedGraph<String>(vertices, matrix);
    }

    public static int[] toPrimitive(Integer[] array) {
        int[] res = new int[array.length];
        for (int index = 0; index < array.length; index++) {
            res[index] = array[index];
        }
        return res;
    }

    public static int[] toPrimitive(String[] array) {
        int[] res = new int[array.length];
        for (int index = 0; index < array.length; index++) {
            res[index] = Integer.parseInt(array[index]);
        }
        return res;
    }

    public static PaintingGraph<TraceData> readPaitingGraph(String file){
        try {
            return readPaintGraph(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

   private static PaintingGraph<TraceData> readPaintGraph(String file) throws FileNotFoundException {
        FileReader reader = new FileReader(new File(file));
        Scanner fileScan = new Scanner(reader);
        boolean isVertexRead = false;
        PaintingGraph<TraceData> graph = new PaintingGraph<>();
        while (fileScan.hasNextLine()){
            String current = fileScan.nextLine().trim();
            if(!isVertexRead &&current.equals("[Vertex]")){
                isVertexRead = true;
                continue;
            }else if(isVertexRead && current.equals("[Links]")){
                isVertexRead = false;
                continue;
            }else if(current.equals(""))continue;
            else if(current.equals("[End]"))break;
            if(isVertexRead){
                String[] array = current.split(":");
                String[] values = array[1].substring(1, array[1].length() - 1).split(",");
                graph.addVertex(Integer.parseInt(array[0]), new Point(Integer.parseInt(values[0]), Integer.parseInt(values[1])));
            }else {
                String[] array = current.split("-");
                graph.setLinkToVertex(Integer.parseInt(array[0]),Integer.parseInt(array[2]), new TraceData(Integer.parseInt(array[1])), false);
            }
        }
        return graph;
    }

    public static PaintingGraph<TraceData> readFromList(ArrayList<String> lst){
        boolean isVertexRead = false;
        PaintingGraph<TraceData> graph = new PaintingGraph<>();
        String current = "";
        for (String str : lst){
            current = str;
            if(!isVertexRead &&current.equals("[Vertex]")){
                isVertexRead = true;
                continue;
            }else if(isVertexRead && current.equals("[Links]")){
                isVertexRead = false;
                continue;
            }else if(current.equals(""))continue;
            else if(current.equals("[End]"))break;
            if(isVertexRead){
                String[] array = current.split(":");
                String[] values = array[1].substring(1, array[1].length() - 1).split(",");
                graph.addVertex(Integer.parseInt(array[0]), new Point(Integer.parseInt(values[0]), Integer.parseInt(values[1])));
            }else {
                String[] array = current.split("-");
                graph.setLinkToVertex(Integer.parseInt(array[0]),Integer.parseInt(array[2]), new TraceData(Integer.parseInt(array[1])), false);
            }
        }
        graph.updateDefaultNames();
        return graph;
    }

}
