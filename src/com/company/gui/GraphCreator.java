package com.company.gui;

import com.company.graphs.PaintingGraph;
import com.company.gui.PaintGraphField;

import javax.swing.*;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

public class GraphCreator {
    private JPanel rootPanel;
    private JPanel canvasField;
    private JPanel optionVPanel;
    private JPanel optionHPanel;
    private JButton deleteSelectButton;
    private JButton browseButton;
    private JButton applyButton;
    private JButton debugButton;
    private JButton saveButton;
    private JTextField addWeightTextField;
    private JButton addWeightToSelectButton;
    private JButton clearButton;
    private JTextField ResultTextField;
    private PaintGraphField paintGraphField;


    public static void init() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e){}
        JFrame frame = new JFrame("GraphCreator");
        frame.setContentPane(new GraphCreator().rootPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public GraphCreator(){

        paintGraphField = new PaintGraphField();
        //paintGraphField.setCBselectFrom(selectFromComboBox);
       // paintGraphField.setCBselectTo(selectToComboBox);
       /* PlainDocument doc = (PlainDocument)maxSpeedTextField.getDocument();
        doc.setDocumentFilter(new DigitFilter());
        PlainDocument doc1 = (PlainDocument)maxTimeTextField.getDocument();
        doc1.setDocumentFilter(new DoubleDigitFilter());*/
        setPaintField();
        setButtonsPropeteries();
        saveButton.addActionListener(e->save());
        paintGraphField.setResultField(ResultTextField);
        paintGraphField.createDemo();
        paintGraphField.printHelp();

    }

    private void setPaintField(){
        canvasField.setLayout(new FlowLayout());
        canvasField.add(paintGraphField);
    }
    private void setButtonsPropeteries(){

        deleteSelectButton.addActionListener(e -> paintGraphField.deleteSelected());
        addWeightToSelectButton.addActionListener(e -> paintGraphField.setSelectedEdgeWeight(addWeightTextField.getText()));
        browseButton.addActionListener(e -> paintGraphField.browse());
        debugButton.addActionListener(e -> help());
        clearButton.addActionListener(e -> paintGraphField.clearAll());

    }

    private void save(){
        FileDialog dialog = new FileDialog((JFrame) null, "Choose file");

        dialog.setDirectory("src\\input\\");
        dialog.setFile("*.gph");
        dialog.setMode(FileDialog.SAVE);
        dialog.setVisible(true);

        if (dialog.getDirectory() != null && dialog.getFile() != null) {
            paintGraphField.save(dialog.getDirectory()+dialog.getFile()+".gph");
        }

    }

    public void help(){
        String msg =
                "Для добавления точки удерживайте Alt.\n" +
                "Для добавления связи между двумя точками удерживайте Ctrl и выберите точки.\n" +
                "Для удаления связи между двумя точками удерживайте Shift и выберите точки.\n" +
                "Для нахождения сопротивления между двумя точками удерживайте S и выберите точки" +
                "Для перемещения вершины удерживайте левую кнопку мыши.";
        JOptionPane.showMessageDialog(null, msg);
    }

}
