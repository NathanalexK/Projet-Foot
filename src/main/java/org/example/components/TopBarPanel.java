package org.example.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.File;

public class TopBarPanel extends JPanel {
    private JFrame caller = null;
    private JTextField pathTextField = new JTextField();
    private JButton navigateBtn = new JButton("parcourir...");


    public TopBarPanel() {
        setLayout(new FlowLayout());

        pathTextField.setColumns(30);

        navigateBtn.addActionListener((e) -> {
            JFileChooser fileChooser = new JFileChooser();
//            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int result = fileChooser.showOpenDialog(null);

            if(result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                pathTextField.setText(selectedFile.getAbsolutePath());

                if(caller != null && caller instanceof MainWindow mw) {
                    try {
                        mw.setImagePath(selectedFile.getAbsolutePath());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

            }
        });

        add(pathTextField);
        add(navigateBtn);

    }

    public JFrame getCaller() {
        return caller;
    }

    public void setCaller(JFrame caller) {
        this.caller = caller;
    }

    public void onNavigateBtnClick(MouseEvent e) {

    }
}
