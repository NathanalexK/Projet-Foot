package org.example.components;

import org.example.foot.Jeu;

import javax.swing.*;
import java.awt.*;

public class BottomPanel extends JPanel {
    private JButton analyzeBtn = new JButton("Analyser");
    private MainWindow caller = null;

    public BottomPanel(MainWindow caller) {
        setLayout(new FlowLayout());
        setCaller(caller);

        analyzeBtn.addActionListener((e) -> {
            try {
                Jeu jeu = new Jeu(caller.getImagePath());
                jeu.rewriteImage("resultat.jpg");
                Thread.sleep(500);
                caller.setImagePath("resultat.jpg");

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });

        this.add(analyzeBtn);

    }

    public JButton getAnalyzeBtn() {
        return analyzeBtn;
    }

    public void setAnalyzeBtn(JButton analyzeBtn) {
        this.analyzeBtn = analyzeBtn;
    }

    public MainWindow getCaller() {
        return caller;
    }

    public void setCaller(MainWindow caller) {
        this.caller = caller;
    }
}
