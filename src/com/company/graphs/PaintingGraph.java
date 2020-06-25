package com.company.graphs;

import com.company.gui.Point;
import com.company.utils.Geometry;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.util.*;
import java.util.List;

/**
 * @param <W> класс веса ребра графа
 */
public class PaintingGraph<W extends TableData> {
    private static final int DEFAULT_HEIGHT = 30;
    private static final int MIN_WIDTH = 60;
    private static final int FONT_SIZE = 20;
    private static final int LINE_STROKE = 4;
    private static final int DEFAULT_RADIUS = 10;
    private static final int DEFAULT_DISTANCE = 100;
    private static final int LINE_RESISTOR = 20;
    private static final int RESISTOR_WIDTH = 40;

    private Color fontColor = new Color(0, 0, 0);
    private Color backgroundColor = new Color(138, 134, 134);
    private Color selectedBackgroundColor = new Color(54, 138, 138);
    private Color selectedEdgeColor = new Color(138, 127, 69);
    private Color edgeColor = new Color(138, 47, 51);

    /**
     * Хранит следующее имя вершины
     */
    private Integer next_default_name;
    private Integer next_default_nameEdge;
    /**
     * Класс, описывающий вершину графа
     */
    private class Vertex {
        Integer name; // имя вершины
        Point leftVertex; // координата левой верхней вершины прямоугольника
        //int width;
        //int height = DEFAULT_HEIGHT;
        int radius = DEFAULT_RADIUS;
        Vector<Integer> links; // содержит имена связанных вершин

        Vertex(Integer value, Point point) {
            this.name = value;
            leftVertex = point;
            //width = Math.max(MIN_WIDTH, value.length() * FONT_SIZE);

        }

        /**
         * @return Точка центра окружности, являющейся вершиной
         */
        Point getOvalCenter() {
            return new Point(leftVertex.x + radius/2, leftVertex.y + radius/2);
        }
        void setName(Integer name) {
            this.name = name;
           // width = Math.max(MIN_WIDTH, this.name.length() * FONT_SIZE);
        }

        boolean isNotHaveLink() {
            return links == null || links.isEmpty();
        }

        /**
         * Устанавливает связь с вершиной
         *
         * @param value - имя вершины, с которой устанавливаем связь.
         */
        void setLink(Integer value) {
            if (links == null)
                links = new Vector<>();
            if (!links.contains(value))
                links.add(value);
        }

        /**
         * Проверяет связь с вершиной
         *
         * @param vertexName - имя проверяемой вершины
         * @return <b>true</b>, если вершина связана с проверяемой, иначе <b>false</b>
         */
        boolean isHaveLink(Integer vertexName) {
            if(links == null)  return false;
            if (links.isEmpty()) return false;
            for(Integer name : links){
                if (name.equals(vertexName)) return true;
            }
            return false;
        }

        /**
         * Удаляет связь с вершиной
         *
         * @param vertexName - имя удаляемой вершины
         */
        void deleteLink(Integer vertexName) {
            if (!isHaveLink(vertexName))
                return;
            links.remove(vertexName);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof PaintingGraph.Vertex)) return false;
            Vertex vertex = (PaintingGraph.Vertex) o;
            return name.equals(vertex.name);
        }


    }


    /**
     * Класс, описывающий ребро <b>взвешенного</b> графа
     */
    private class Edge {
        Integer name;
        Vertex vertex1; // инцидентная ребру вершина
        Vertex vertex2; // инцидентная ребру вершина
        TraceData weight; // вес ребра

        Edge(Integer value, Vertex vertex1, Vertex vertex2, TraceData weight) {
            this.vertex1 = vertex1;
            this.vertex2 = vertex2;
            this.weight = weight;
            this.name = value;
        }

        /**
         * Проверяет инцидентность вершины с данным ребром
         *
         * @param vertex - проверяемая вершина
         * @return true, если вершина инцидентна ребру, иначе - false
         */
        boolean isContains(Vertex vertex) {
            return vertex1.equals(vertex) || vertex2.equals(vertex);
        }

        /**
         * Проверяет инцидентность вершины с данным ребром
         *
         * @param name - имя проверяемой вершины
         * @return true, если вершина инцидентна ребру, иначе - false
         */
        boolean isContains(Integer name) {
            return vertex1.name.equals(name) || vertex2.name.equals(name);
        }

        Integer getLinkedName(Integer vertexName){
            if(vertexName.equals(vertex1.name)){
                return vertex2.name;
            }else if(vertexName.equals(vertex2.name))
                return vertex1.name;
            else return null;
        }

        void setName(Integer name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof PaintingGraph.Edge)) return false;
            Edge edge = (Edge) o;
            return (vertex1.equals(edge.vertex1) &&
                    vertex2.equals(edge.vertex2)) ||
                    (vertex1.equals(edge.vertex2) &&
                            vertex2.equals(edge.vertex1));
        }


    }

    private Map<Integer, Vertex> vertex_list; // список вершин графа
    private Map<Integer,Edge> edges; // список ребер графа
    private Vertex selectedVertex; // хранит ссылку на выделенную в редакторе вершину
    private Edge selectedEdge; // хранит ссылку на выделенное в редакторе ребро

    public PaintingGraph() {
        this.vertex_list = new HashMap<>();
        edges = new HashMap<>();
        next_default_name = 0;
        next_default_nameEdge = 0;
    }
    public PaintingGraph(PaintingGraph other){

    }

    public Integer getMaxVertexName(){
        Integer n = -1;
        for(Integer name : vertex_list.keySet()){
            if (name > n) n = name;
        }
        return n;
    }

    public Integer getMaxEdgesName(){
        Integer n = -1;
        for(Integer name : edges.keySet()){
            if (name > n) n = name;
        }
        return n;
    }

    public void updateDefaultNames(){
        next_default_name = getMaxVertexName() + 1;
        next_default_nameEdge = getMaxEdgesName() + 1;
    }

    public void addVertex(Integer value, Point point) {
        if (vertex_list.containsKey(value)) {// нельзя добавить вершину с одинаковым именем
            return;
        }
        vertex_list.put(value, new Vertex(value, point));
    }

    /**
     * @param point точка левого верхнего угла прямоугольника рисуемой вершины
     */
    public void addVertex(Point point) {
        if (vertex_list.containsKey(String.valueOf(next_default_name))) {
            return;
        }
        vertex_list.put(next_default_name, new Vertex(next_default_name, point));
        next_default_name++;
    }

    /**
     * Устанавливает связь(двустороннюю) между вершинами, <b>которые уже добавлены в граф</b>
     *
     * @param vertex_name имя вершины, с которой устанавливаем связь
     * @param link_name   имя вершины, которую привязывем к <b>vertex_name</b>
     */
    public void setLinkToVertex(Integer vertex_name, Integer link_name, TraceData weight, Boolean flag) {
        if (!vertex_list.containsKey(vertex_name) || !vertex_list.containsKey(link_name)) {
            return;
        }
        if (!flag) {
            if (vertex_list.get(vertex_name).isHaveLink(link_name)) {
                JOptionPane.showMessageDialog(null, "Соединение между точками уже установлено.\nВыберите другие точки.");
                return;
            }
        }
        if(getDistance(vertex_list.get(vertex_name), vertex_list.get(link_name)) >= DEFAULT_DISTANCE) {
            vertex_list.get(vertex_name).setLink(link_name);
            vertex_list.get(link_name).setLink(vertex_name);
            edges.put(next_default_nameEdge, new Edge(next_default_nameEdge, vertex_list.get(vertex_name), vertex_list.get(link_name), weight));
            next_default_nameEdge++;
        }
        else{
            JOptionPane.showMessageDialog(null, "Расстояние между точками слишком маленькое.\nРаздвиньте точки.");
        }


    }

    /**
     * Удаляет ребро, которому инцидентны указанные вершины
     *
     * @param vertex1 инцидентная ребру вершина
     * @param vertex2 инцидентная ребру вершина
     */
    private void removeEdge(Integer vertex1, Integer vertex2) {
        int size = edges.size();
        for (int index = 0; index < size; index++) {
            Edge e = edges.get(index);
            if (e.isContains(vertex1) && e.isContains(vertex2)) {
                edges.remove(index);
                return;
            }
        }
    }

    public void debug() {
        System.out.println("Debug");
    }

    /**
     * @param point точка, принадлежащая полю редактора, по которой ищем компонент графа
     */
    public void select(Point point) {
        // Поскольку ребра соединяют центры окружностей вершин, сначала ищем точку в вершинах
        selectVertex(point);
        if (selectedVertex != null) return;
        // если не попали в вершину - ищем ребро
        selectEdge(point);
    }

    /**
     * @param point точка, принадлежащая полю редактора, по которой ищем вершину графа
     */
    public void selectVertex(Point point) {
        for (Map.Entry<Integer, Vertex> kv :
                vertex_list.entrySet()) {
            Vertex current = kv.getValue();
            //если точка попадает в окружность вершины, выделяем её
            if (Geometry.inOval(point, current.leftVertex, current.radius)) {
                selectedVertex = current;
                return;
            }
        }
        // если не нашли вершины, то снимаем выделение
        selectedVertex = null;
    }

    public void selectEdge(Point point) {
        Edge e;
        for (Integer i : edges.keySet()) {
            e = edges.get(i);
            // проверяем, лежит ли точка на ОТРЕЗКЕ линии(заданной толщины) прямой, ограниченной центрами окружностей вершин
            if (Geometry.isLayToStrongCut(point, e.vertex1.getOvalCenter(), e.vertex2.getOvalCenter(), LINE_STROKE)) {
                selectedEdge = e;
                return;
            }
            Point v1 = e.vertex1.getOvalCenter();
            Point v2 = e.vertex2.getOvalCenter();
            double d = Math.sqrt((v1.x - v2.x)*(v1.x - v2.x)+(v1.y - v2.y)*(v1.y- v2.y));
            int lengthRes = RESISTOR_WIDTH;
            double l = (d - lengthRes)/2;
            double x1 = v1.x + (v2.x - v1.x) * l / d;
            double y1 = v1.y + (v2.y - v1.y) * l / d;
            double x2 = v2.x - (x1 - v1.x);
            double y2 = v2.y - (y1 - v1.y);
            Point res1 = new Point( (int) x1, (int) y1);
            Point res2 = new Point( (int) x2, (int) y2);
            if(Geometry.isLayToStrongCut(point, res1, res2, LINE_RESISTOR )) {
                selectedEdge = e;
                return;
            }
        }
        // если не нашли ребра, то снимаем выделение
        selectedEdge = null;
    }

    public void unSelect(){
        selectedVertex = null;
        selectedEdge = null;
    }
    /**
     * Удаляет вершину и ее связи из графа
     *
     * @param vertex удаляемая вершина
     */
    private void deleteVertex(Vertex vertex) {
        if (vertex == null) return;
        if (!vertex_list.containsKey(vertex.name)) return;
        //поиск и удаление ребер, которым инцидентна данная вершина
        Set<Integer> s = edges.keySet();
        Integer[] st = s.toArray(new Integer[s.size()]);

        for (Integer index : st) {
            Edge e = edges.get(index);
            boolean p = e.isContains(vertex);
            if (p) {
                edges.remove(index);
            }
        }
        if (vertex.links != null)
        {
            for (Integer ind : vertex.links){
                Vertex v = vertex_list.get(ind);
                v.deleteLink(vertex.name);
            }
        }

        vertex_list.remove(vertex.name); // удаление вершины из списка
    }

    /**
     * Удалаяет указанное ребро
     *
     * @param e удаляемое ребро
     */
    private void deleteEdge(Edge e) {
        //удаляем связь между вершинами
        e.vertex1.deleteLink(e.vertex2.name);
        e.vertex2.deleteLink(e.vertex1.name);
        edges.remove(e.name);
    }

    /**
     * Удаляет выделенную вершину
     */
    public void deleteSelectedVertex() {
        if (selectedVertex != null)
            deleteVertex(selectedVertex);
        selectedVertex = null;
    }

    /**
     * Удаляет выбранное ребро
     */
    public void deleteSelectedEdge() {
        if (selectedEdge != null) {
            deleteEdge(selectedEdge);
        }
        selectedEdge = null;
    }

    /**
     * Удаляет выбранный компонент(вершину или ребро)
     */
    public void deleteSelected() {
        // Вершина и ребро выбраны сразу быть не могут
        if (selectedEdge == null) deleteSelectedVertex();
        else deleteSelectedEdge();
    }

    public boolean isSelectedVertex() {
        return selectedVertex != null;
    }

    /**
     * @return true, если есть выбранное ребро, иначе - false
     */
    public boolean isSelectedEdge() {
        return selectedEdge != null;
    }

    /**
     * @return имя выбранной вершины
     */
    public Integer getSelectedVertexName() {
        if (selectedVertex != null)
            return selectedVertex.name;
        else return null;
    }

    /**
     * Удаляет связь между указанными вершинами
     *
     * @param vertex_name_1 имя вершины
     * @param vertex_name_2 имя вершины
     */
    public void deleteLink(Integer vertex_name_1, Integer vertex_name_2) {
        Vertex vertex1 = vertex_list.get(vertex_name_1);
        Vertex vertex2 = vertex_list.get(vertex_name_2);

        if (vertex1 == null || vertex2 == null) return;
        if (vertex1.isHaveLink(vertex_name_2)) {
            vertex1.deleteLink(vertex_name_2);
            vertex2.deleteLink(vertex_name_1);
            removeEdge(vertex_name_1, vertex_name_2);
        }

    }

    /**
     * Перемещает выбранной координату вершины
     *
     * @param toMove точка, к которой перемещаем координаты вершины
     */
    public void moveSelectedVertex(Point toMove) {
        if (selectedVertex == null) return;
        //вычисляем вектор перемещения
        com.company.gui.Vector vector_move = new com.company.gui.Vector(toMove.x - selectedVertex.leftVertex.x - selectedVertex.radius / 2,
                toMove.y - selectedVertex.leftVertex.y - selectedVertex.radius / 2);
        selectedVertex.leftVertex.addVector(vector_move);
    }

    /**
     * Возвращает вес выделенного ребра
     *
     * @return вес выделленного ребра
     */
    public TraceData getSelectedEdgeWeight() {
        return selectedEdge == null ? null : selectedEdge.weight;
    }

    public void setSelectedEdgeWeight(String data) {
        Object[] obj = new Object[1];
        obj[0] = data;
        selectedEdge.weight.setData(obj);
    }

    /**
     * Отрисовывет граф
     *
     * @param g ресурс графики
     */
    public void drawGraph(Graphics2D g) {
        //======Отрисока обычных ребер========
        g.setColor(edgeColor);
        Edge e;
        for (Integer i : edges.keySet()) {
            e = edges.get(i);
            if (e == selectedEdge) continue;
            Point v1 = e.vertex1.getOvalCenter();
            Point v2 = e.vertex2.getOvalCenter();
            drawResistor(g, v1, v2, e.weight.toString());

        }
        //===================================
        // Отрисовка выделенного ребра
        if (selectedEdge != null) {
            g.setColor(selectedEdgeColor);
            Point v1 = selectedEdge.vertex1.getOvalCenter();
            Point v2 = selectedEdge.vertex2.getOvalCenter();
            drawResistor(g, v1, v2,  selectedEdge.weight.toString());
        }
        // Отрисовка обычных вершин
        for (Map.Entry<Integer, Vertex> kv :
                vertex_list.entrySet()) {
            g.setColor(backgroundColor);
            Vertex current = kv.getValue();
            if (current == selectedVertex) continue;
            g.fillOval(current.leftVertex.x, current.leftVertex.y, current.radius, current.radius);
            g.setColor(selectedBackgroundColor);
            g.drawString(kv.getKey().toString(), current.leftVertex.x ,
                    current.leftVertex.y);
        }
        // отрисовка выделенной вершины
        if (selectedVertex != null) {
            g.setColor(selectedBackgroundColor);
            g.fillOval(selectedVertex.leftVertex.x, selectedVertex.leftVertex.y,
                        selectedVertex.radius, selectedVertex.radius);
            g.setColor(selectedBackgroundColor);
            g.drawString(selectedVertex.name.toString(), selectedVertex.leftVertex.x ,
                    selectedVertex.leftVertex.y);
            /*g.drawString(selectedVertex.name, selectedVertex.leftVertex.x +
                            (selectedVertex.width - (selectedVertex.name.length() - 1) * FONT_SIZE) / 2,
                    selectedVertex.leftVertex.y + 2 * selectedVertex.height / 3);*/
        }
    }

    public Vector<Integer> getVerexList(){
        return new Vector<>(vertex_list.keySet());
    }



    public String[] getLinkNames(String vertexName){
        Vertex vertex = vertex_list.get(vertexName);
        if(vertex == null)return null;
        return vertex.links.toArray(new String[0]);
    }

    public HashMap<Integer, TraceData> getWeightMapForVertex(Integer vertexName){
        Vertex vertex = vertex_list.get(vertexName);
        if(vertex == null)return null;
        HashMap<Integer, TraceData> weightMap = new HashMap<>(vertex.links.size());
        Edge e;
        for (int i = 0; i < edges.size(); i++) {
            e = edges.get(i);
            Integer link = e.getLinkedName(vertexName);
            if(link != null)
                weightMap.put(link, e.weight);
        }
        return weightMap;
    }

    public int size(){
        return vertex_list.size();
    }

    private void writeInFile(String path) throws IOException {
        FileWriter writer = new FileWriter(new File(path));

        Set<Map.Entry<Integer, Vertex>> entrySet = vertex_list.entrySet();
        writer.write("[Vertex]");
        writer.write(System.lineSeparator());

        for (Map.Entry<Integer, Vertex> kv :
                entrySet) {
            writer.write(kv.getKey() + ":");
            writer.write(kv.getValue().leftVertex.toString());
            writer.write(System.lineSeparator());
        }
        writer.write(System.lineSeparator());
        writer.write("[Links]");
        writer.write(System.lineSeparator());

        Edge e;
        for (Integer i : edges.keySet()) {
            e = edges.get(i);
            writer.write(e.vertex1.name+"-");
            writer.write(e.weight.toString());
            writer.write("-" + e.vertex2.name);
            writer.write(System.lineSeparator());
        }
        writer.write("[End]");
        writer.flush();
        writer.close();
    }
    public void createDemolist(ArrayList<String> lst){
        lst.add("[Vertex]") ;
        lst.add("0:[236,361]");
        lst.add("1:[492,373]");
        lst.add("2:[261,186]");
        lst.add("3:[491,189]");
        lst.add("");
        lst.add("[Links]");
        lst.add("1-1-0");
        lst.add("0-1-2");
        lst.add("3-1-2");
        lst.add("1-1-3");
        lst.add("3-1-0");
        lst.add("[End]");
    }


    public void saveToList(ArrayList<String> lst){
        Set<Map.Entry<Integer, Vertex>> entrySet = vertex_list.entrySet();
        lst.add("[Vertex]") ;
        //writer.write("[Vertex]");
        //writer.write(System.lineSeparator());

        for (Map.Entry<Integer, Vertex> kv :
                entrySet) {
            lst.add( kv.getKey() + ":" + kv.getValue().leftVertex.toString() );

           // writer.write(kv.getKey() + ":");
           // writer.write(kv.getValue().leftVertex.toString());
            //writer.write(System.lineSeparator());
        }
        lst.add("[Links]");
        //writer.write(System.lineSeparator());
        ///writer.write("[Links]");
        //writer.write(System.lineSeparator());

        Edge e;
        for (Integer i : edges.keySet()) {
            e = edges.get(i);
            lst.add( e.vertex1.name+"-" + e.weight.toString() + "-" + e.vertex2.name );
           // writer.write(e.vertex1.name+"-");
           // writer.write(e.weight.toString());
           // writer.write("-" + e.vertex2.name);
            //writer.write(System.lineSeparator());
        }
        lst.add("[End]");
       // writer.write("[End]");
       // writer.flush();
       // writer.close();
    }

    public void save(String path) {
        try {
            writeInFile(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void renameVertex(Integer lastName, Integer newName) {
        if (lastName == newName) return;
        if (vertex_list.containsKey(newName)) {
            changeNames(lastName, newName);
            return;
        }
        Vertex vertex = vertex_list.get(lastName);
        vertex_list.remove(vertex.name);
        vertex.setName(newName);
        vertex_list.put(newName, vertex);
        renameLinks(vertex_list.get(newName), lastName);

    }

    public void changeNames(Integer name1, Integer name2){
        renameVertex(name1, (-1));
        renameVertex(name2, name1);
        renameVertex((-1), name2);
    }
    private void renameLinks(Vertex vertex, Integer lastname){
        Vertex v;
        for (Integer link : vertex.links){
            v = vertex_list.get(link);
            if (v.links != null){
                int index = v.links.indexOf(lastname);
                if (index != -1)  {
                    v.links.removeElementAt(index);
                    v.links.add(vertex.name);
                }
            }
        }
    }

    private void renameNameForSolution(Integer[] new_index, Integer index, Integer newName, Integer[] arr, Vertex v){
        new_index[index] = newName;
        if (vertex_list.containsKey(newName)){
            int newi = newName;
            for (int j = 0; j < arr.length; j++){
                if (arr[j] == newName) {
                    newi = j;
                    break;
                }
            }
            new_index[newi] = v.name;
        }
        renameVertex(v.name, newName);
    }


    public void renameGraphForSolution(int from, int to){
        Map<Integer, Vertex> new_vertex_list = new HashMap<>(); // список вершин графа
        Map<Integer,Edge> new_edges = new HashMap<>(); // список ребер графа
        Integer size = vertex_list.size();
        int n = 1;
        Integer[] arr = vertex_list.keySet().toArray(new Integer[vertex_list.keySet().size()]);

        Integer[] new_index = new Integer[arr.length];
        for (int index = 0; index < arr.length; index++) {
            Vertex v;
            if (new_index[index] != null) {
                v = vertex_list.get(new_index[index]);
            }
            else  v = vertex_list.get(arr[index]);

            if (arr[index] == from){
                if (v.name != 0){
                    renameNameForSolution(new_index, index, 0, arr, v);

                }
                new_vertex_list.put(0, v);
            }
            else {
                if (arr[index] == to){
                    if (v.name !=  size - 1) {
                        renameNameForSolution(new_index, index, size - 1, arr, v);
                    }
                    new_vertex_list.put(size - 1, v);
                }
                else{
                    if (v.name !=  n) {
                        renameNameForSolution(new_index, index, n, arr, v);
                    }
                    new_vertex_list.put(n, v);
                    n++;
                }
            }
        }

        n = 0;
        for (Integer index : edges.keySet()) {
            Edge v = edges.get(index);
            v.setName(n);
            new_edges.put(n, v);
            n++;
        }
        vertex_list = new_vertex_list;
        edges = new_edges;
        next_default_name = vertex_list.size();
        next_default_nameEdge = edges.size();
    }

    public boolean createGraphForSolution(int from, int to){

        Boolean[] visited = new Boolean[getMaxVertexName()+1];
        //List<List<Integer>> indOfWay = new ArrayList<>();
        ArrayList<Integer>[] indOfWay = new ArrayList[getMaxVertexName()+1];
        for(int i = 0; i < indOfWay.length; i++){
            indOfWay[i] = new ArrayList<>();
        }
        Queue<Integer> queue = new LinkedList<>();
        List<Boolean[]> ways = new ArrayList<>();
        List<Integer> result = new ArrayList<>();

        queue.add(from);
        visited[from]= true;
        Boolean[] way0 = new Boolean[getMaxVertexName()+1];
        way0[from] = true;
        ways.add(way0);
        indOfWay[from].add(0);

        while (queue.size() > 0) {
            Integer curr = queue.remove();
            Vertex crr = vertex_list.get(curr);
            for ( int i = 0; i < indOfWay[curr].size(); i++){
                boolean flag = true;
                int ind = indOfWay[curr].get(i);
                Boolean[] wayCopy = ways.get(ind).clone();
                if (crr.links != null) {
                    for (int k = 0; k < crr.links.size(); k++) {
                        Vertex ver = vertex_list.get(crr.links.get(k));
                        Integer v = ver.name;
                        if (ways.get(ind)[v] != null) {
                            continue;
                        }
                        if (flag) {
                            flag = false;
                            ways.get(ind)[v] = true;
                            indOfWay[v].add(ind);
                        } else {
                            Boolean[] wayi = wayCopy.clone();
                            wayi[v] = true;
                            ways.add(wayi);
                            indOfWay[v].add(ways.size() - 1);
                        }
                        if (v == to) {
                            result.add(indOfWay[v].get(indOfWay[v].size() - 1));
                        } else {
                            if (visited[v] == null) {
                                queue.add(v);
                                visited[v] = true;
                            }
                        }
                    }
                }
            }


        }
        if (result.size() > 0) {
            Boolean flag = true;
            for (int i = 0; i < ways.get(0).length; i++) {
                for (int j = 0; j < result.size(); j++) {
                    if (ways.get(result.get(j))[i] != null) {
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    Vertex vertex = vertex_list.get(i);
                    deleteVertex(vertex);
                } else flag = true;
            }
            simplify(from, to);
            renameGraphForSolution(from, to);
            return true;
        }
        return false;
    }

    public boolean checkDistance(Point point, Integer vertexName){
        Vertex vertex = vertex_list.get(vertexName);
        if(vertex.links == null) return true;
        if(vertex.links.isEmpty()) return true;
        int x0 = point.x;
        int y0 = point.y;
        Vertex ver;
        for (Integer v : vertex.links){
            ver = vertex_list.get(v);
            if (Math.sqrt((x0 - ver.getOvalCenter().x)*(x0 - ver.getOvalCenter().x)
                    + (y0 - ver.getOvalCenter().y)*(y0 - ver.getOvalCenter().y)) < DEFAULT_DISTANCE) return false;
        }
        return true;
    }
     private void drawResistor(Graphics2D g, Point v1, Point v2, String w){
         double d = Math.sqrt((v1.x - v2.x)*(v1.x - v2.x)+(v1.y - v2.y)*(v1.y- v2.y));
         int lengthRes = RESISTOR_WIDTH;
         float heightRes = LINE_RESISTOR;
         double l = (d - lengthRes)/2;
         double x1 = v1.x + (v2.x - v1.x) * l / d;
         double y1 = v1.y + (v2.y - v1.y) * l / d;
         double x2 = v2.x - (x1 - v1.x);
         double y2 = v2.y - (y1 - v1.y);
         double cx = v1.x + (v2.x - v1.x) / 2.0;
         double cy = v1.y + (v2.y - v1.y) / 2.0;
         Stroke last = g.getStroke();

         g.drawLine(v1.x, v1.y, (int) x1,  (int) y1);
         g.drawLine((int) x2, (int) y2, v2.x, v2.y);
         g.setStroke(new BasicStroke(heightRes));
         g.drawLine((int) x1,  (int) y1, (int) x2, (int) y2);
         g.setStroke(last);
         Font lastF = g.getFont();
         g.setFont(new Font("Times New Roman", Font.PLAIN, FONT_SIZE));
         g.setColor(backgroundColor);
         g.drawString(w, (int) cx, (int) cy);
         g.setColor(edgeColor);
         g.setFont(lastF);
     }

     public Double solution(){
        Double[][] matrix = createMatrixEquation ();
        int n = matrix.length;
        int m = matrix[0].length;

        Integer[] where = new Integer[n];
        for (int col=0, row=0; col<m && row<n; ++col) {
            int sel = row;
            for (int i=row; i<n; ++i) {
                if (Math.abs(matrix[i][col]) > Math.abs(matrix[sel][col]))
                     sel = i;
            }
            if (sel != row){
                for (int i=col; i<m; ++i){
                    double a = matrix[sel][i];
                    matrix[sel][i] = matrix[row][i];
                    matrix[row][i] = a;
                }
            }

            where[col] = row;
            if (matrix[row][col] != 0){
                for (int i=0; i<n; ++i){
                    if (i != row && matrix[i][col] !=0) {
                        Double c = matrix[i][col] / matrix[row][col];
                        for (int j=col; j<m; ++j)
                            matrix[i][j] -= matrix[row][j] * c;
                    }
                }
            }
            ++row;
        }
        int k = where[vertex_list.size() - 1];
        double ans = Math.abs(matrix[k][matrix[0].length - 1] / matrix[k][vertex_list.size() - 1]);
        return ans;
     }

     public Double[][] createMatrixEquation (){

         Double[][] equationUI = new Double[edges.size()][3];
         Double[][] equationI = new Double[vertex_list.size()][edges.size() + 1];
         Vertex ver;
         //Integer[] i = new Integer[edges.size()];
         //Integer[] u = new Integer[vertex_list.size()];

         int k = 0;
         Edge e;
         for (int i = 0; i < edges.size(); i++) {
             e = edges.get(i);
             equationUI[k][0] = (double ) e.vertex1.name;
             equationUI[k][1] = (double ) e.vertex2.name;
             equationUI[k][2] = Double.parseDouble(e.weight.toString());
             k++;
         }
         for (int i = 0; i < edges.size() + 1; i++) {
             for (int j = 0; j < vertex_list.size(); j++) {
                 if (i == edges.size()){
                     if (j == 0){
                         equationI[j][i] = -1.;
                     }
                     else {
                         if (j == vertex_list.size() - 1){
                             equationI[j][i] = 1.;
                         }
                         else{
                             equationI[j][i] = 0.;
                         }
                     }
                 }
                 else {
                     e = edges.get(i);
                     ver = vertex_list.get(j);
                     if (e.vertex1 == ver) {
                         if (e.vertex1.name > e.vertex2.name) equationI[j][i] = -1.;
                         else if (e.vertex1.name < e.vertex2.name) equationI[j][i] = 1.;
                     }
                     if (e.vertex2 == ver) {
                         if (e.vertex1.name > e.vertex2.name) equationI[j][i] = 1.;
                         else if (e.vertex1.name < e.vertex2.name) equationI[j][i] = -1.;
                     }
                 }

             }
         }


         Double[][] matrix = new Double[edges.size() + vertex_list.size() + 1][edges.size() + vertex_list.size() + 1];
         for (int i = 0; i < edges.size(); i++){
             for (int j = 0; j < vertex_list.size(); j++) {
                 if (j == equationUI[i][0]) {
                     if (equationUI[i][0] < equationUI[i][1]) {
                         matrix[i][j] = 1.;
                     } else {
                         if (equationUI[i][0] > equationUI[i][1]) {
                             matrix[i][j] = -1.;
                         }
                     }
                 }
                 else {
                     if (j == equationUI[i][1]) {
                         if (equationUI[i][0] < equationUI[i][1]) {
                             matrix[i][j] = -1.;
                         } else {
                             if (equationUI[i][0] > equationUI[i][1]) {
                                 matrix[i][j] = 1.;
                             }
                         }
                     }
                     else  matrix[i][j] = 0.;
                 }
             }

             for (int j = vertex_list.size(); j < edges.size() + vertex_list.size(); j++) {
                 if(j == vertex_list.size() + i)
                     matrix[i][j] = equationUI[i][2];
                 else matrix[i][j] = 0.;
             }
             matrix[i][edges.size() + vertex_list.size()] = 0.;
         }
         for (int i = edges.size(); i < edges.size() + vertex_list.size(); i++){
             for(int j = 0; j < vertex_list.size(); j++){
                 matrix[i][j] = 0.;
             }
             for (int j = vertex_list.size(); j < edges.size() + vertex_list.size() + 1; j++ ){
                 if (equationI[i - edges.size()][j - vertex_list.size()] != null)
                    matrix[i][j] = equationI[i - edges.size()][j - vertex_list.size()];
                 else matrix[i][j] = 0.;
             }
         }
         matrix[edges.size() + vertex_list.size()][0] = 1.;
         for(int j = 1; j < edges.size() + vertex_list.size() + 1; j++){
             matrix[edges.size() + vertex_list.size()][j] = 0.;
         }
         return matrix;
     }

     private Double getDistance(Vertex ver1, Vertex ver2){
        Point v1 = ver1.getOvalCenter();
        Point v2 = ver2.getOvalCenter();
        return  Math.sqrt((v1.x - v2.x)*(v1.x - v2.x)+(v1.y - v2.y)*(v1.y- v2.y));
     }

     public void simplify(Integer from, Integer to){
         Integer[] arr = vertex_list.keySet().toArray(new Integer[vertex_list.keySet().size()]);
         Vertex ver;
         for (Integer v :arr){
             ver = vertex_list.get(v);
             if (ver.name == from || ver.name == to) continue;
             if (ver.links == null){
                 deleteVertex(ver);
                 continue;
             }
             if (ver.links.size() == 2){
                 Edge e1 = findEdgeForVertex(ver.name, ver.links.get(0));
                 Edge e2 = findEdgeForVertex(ver.name, ver.links.get(1));
                 Integer w = e1.weight.getResistance() + e2.weight.getResistance();
                 setLinkToVertex(ver.links.get(0), ver.links.get(1), new TraceData(w), true);
                 deleteVertex(ver);
             }

         }
     }

     private Edge findEdgeForVertex(Integer v1, Integer v2){
        Edge e = null;
        for (Integer ed : edges.keySet()){
            e = edges.get(ed);
            if ((e.vertex1.name == v1 && e.vertex2.name == v2) || (e.vertex1.name == v2 && e.vertex2.name == v1))
                return e;
        }
        return null;
     }
}
