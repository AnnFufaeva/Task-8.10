package com.company.gui;

import com.company.graphs.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.Vector;

public class PaintGraphField extends JPanel {
    private PaintingGraph<TraceData> graph;
    private boolean isAlt = false;
    private boolean isControl = false;
    private boolean isShift = false;
    private boolean isS = false;
    private ArrayDeque<Integer> setLinks;
    private JTextField ResultField = new JTextField();

    PaintGraphField() {
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.BLACK);
        //ResultField.setText("Для получения справки о работе с программой нажмите кнопку 'Помощь'");
        graph = new PaintingGraph<>();
        setLinks = new ArrayDeque<>();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Обрабатываем нажание на поле
                graph.select(new Point(e.getX(), e.getY()));

                // Действия во время нажатия клавиш
                if (isControl ^ isShift ^ isS) {
                    if (graph.isSelectedVertex()) {
                        if (setLinks.size() == 1){
                            if(!setLinks.peek().equals(graph.getSelectedVertexName()))
                                setLinks.push(graph.getSelectedVertexName());
                        }
                        else{
                            setLinks.push(graph.getSelectedVertexName()); // Добавляем в очередь выделяемые вершины
                        }
                    } else {
                        setLinks.clear();

                    }
                    if (setLinks.size() == 2) { // Когда выделили 2е вершины, то добавляем им связь
                        if (isControl) {
                            graph.setLinkToVertex(setLinks.pop(), setLinks.pop(), new TraceData(0));
                            releaseControl();
                        }
                        else  {
                            if (isShift) graph.deleteLink(setLinks.pop(), setLinks.pop());
                            else {
                                doResult(setLinks.pop(), setLinks.pop());
                                //JOptionPane.showMessageDialog(null, "ляля");
                            }
                        }
                        graph.unSelect();
                    }
                }
                else {
                    setLinks.clear();
                }
                repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                requestFocusInWindow();
                //Обработка клика, добавляющего вершину
                if (isAlt) {
                    graph.addVertex(new Point(e.getX(), e.getY()));
                    graph.unSelect();
                }
                repaint();
            }
        });
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // Не двигаем, если двигать нечего или зажат Ctrl
                if (!graph.isSelectedVertex() || isControl) return;
                if (graph.checkDistance(new Point(e.getX(), e.getY()), graph.getSelectedVertexName())) {
                    graph.moveSelectedVertex(new Point(e.getX(), e.getY()));
                    repaint();
                }
            }
        });

        // Регистация нажатий/отпусканий клавиш
        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                isAlt = e.isAltDown();
                isControl = e.isControlDown();
                isShift = e.isShiftDown();
                isS = (e.getKeyCode() == 'S');
                //if (isS) JOptionPane.showMessageDialog(null, "jj");
            }

            @Override
            public void keyReleased(KeyEvent e) {
                isAlt = e.isAltDown();
                isControl = e.isControlDown();
                isShift = e.isShiftDown();
                isS = (e.getKeyCode() == 'S');
            }
        });
        setFocusable(true);
    }
    public void releaseControl(){
        isControl = false;
    }

    /**
     * Функция удаляет выделенный элемент(ребро или вершину)
     * Является обрерткой, вызывающей функцию в <b>графе<b/>
     */
    public void deleteSelected() {
        graph.deleteSelected();

        repaint();
    }

    public void doResult(Integer name1, Integer name2) {
        ResultField.setText("");
        PaintingGraph<TraceData> new_graph = new PaintingGraph<>();
        ArrayList<String> lst = new ArrayList<String>();
        graph.saveToList(lst);
        new_graph = GraphReader.readFromList(lst);
        Boolean flag = new_graph.createGraphForSolution(name1, name2);
        if (flag){
            ResultField.setText("Сопротивление между точками " + name1+ " и "+ name2 + ": " + new_graph.solution() + " Ом");
            new_graph.solution();
        }
        else ResultField.setText("Выбранные точки не соединены резисторами.");
        //graph = new_graph;
        repaint();
    }

    public void createDemo(){
        ArrayList<String> lst = new ArrayList<String>();
        PaintingGraph<TraceData> new_graph = new PaintingGraph<>();
        new_graph.createDemolist(lst);
        new_graph = GraphReader.readFromList(lst);
        graph = new_graph;
        repaint();
    }

    public void printHelp(){
        ResultField.setText("Для получения справки о работе с программой нажмите кнопку 'Помощь'");
    }

    public void setResultField(JTextField ResultField)
    {
        this.ResultField = ResultField;
    }

    public void clearAll(){
        graph = new PaintingGraph<>();
        repaint();
    }

    public void setSelectedEdgeWeight(String data) {
        if (graph.isSelectedEdge()) {
            graph.setSelectedEdgeWeight(data);
        }
        repaint();
    }

    public void save(String path) {
        graph.save(path);
    }

    public void browse(){
        FileDialog dialog = new FileDialog((JFrame) null, "Choose file");

        dialog.setDirectory("src\\input\\");
        dialog.setFile("*.gph");
        dialog.setVisible(true);

        if (dialog.getDirectory() != null && dialog.getFile() != null) {
            PaintingGraph<TraceData> t = GraphReader.readPaitingGraph(dialog.getDirectory() + dialog.getFile());
            if (t != null){
                graph = t;
                graph.updateDefaultNames();
               // CBselectFrom.setModel(new DefaultComboBoxModel<>(graph.getVerexList()));
                //CBselectTo.setModel(new DefaultComboBoxModel<>(graph.getVerexList()));
                repaint();
            }
        }
    }

    public void solution() {
       Double result = graph.solution();
       System.out.print(result);
    }

    public void debug(String vertexFrom, String vertexTo, double timeLimit, int maxSpeed) {
        if(vertexFrom.equals(vertexTo)){
            viewInfo("Выберите разные города!");
            return;
        }

        solution();
    }

    private void viewInfo(String message){
        JOptionPane.showMessageDialog(null, message,
                "INFO", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        //BufferedImage image = new BufferedImage((int)size.getWidth(), (int)size.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(4));
        graph.drawGraph(g2d);
        //g.drawImage(image, 0,0,this);
    }
}
