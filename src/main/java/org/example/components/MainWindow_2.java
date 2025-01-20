package org.example.components;

import org.example.foot.Match;
import org.example.utils.ComponentUtils;
import org.example.utils.Constante;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

public class MainWindow_2 extends JFrame {
    ImageIcon photo = new ImageIcon();
//    private BottomPanel bottomPanel = null;
    private JPanel centerPanel = null;
    private JPanel bottomPanel = null;
    private JPanel topPanel = null;

    private ImagePanel imagePanel1 = null;
    private ImagePanel imagePanel2 = null;

    private String imageAvantTirPath = null;
    private String imageApresTirPath = null;

    private String imagepath = null;
    private Match match;

    public MainWindow_2(Match match) {
        setMatch(match);
        setTitle("Foot Hors Jeu");
        setSize(1400, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

//        TopBarPanel topPanel = new TopBarPanel();
//        topPanel.setCaller(this);

        centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(1, 2, 10, 10));

        imagePanel1 = new ImagePanel();
        imagePanel2 = new ImagePanel();

        imagePanel1.setTitle("Image Avant Tir");
        imagePanel2.setTitle("Image Après Tir");

        imagePanel1.setOnImageChangeListener((path) -> {
            this.setImageAvantTirPath(path);
        });

        imagePanel2.setOnImageChangeListener((path) -> {
            this.setImageApresTirPath(path);
        });



//        match.setOnScoreChangeListener();


//        centerPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        centerPanel.add(imagePanel1);
        centerPanel.add(imagePanel2);

        this.getContentPane().add(centerPanel, BorderLayout.CENTER);

        topPanel = new JPanel();
//        topPanel.add(new JLabel("Bleu " + match.getPointsEquipe().get(Constante.EQUIPE_BLEU) + " - " + match.getPointsEquipe().get(Constante.EQUIPE_ROUGE) + " Rouge") );
        topPanel.add(new JLabel(String.format("Bleu %s (%s) - (%s) %s Rouge", match.getPointsEquipe().get(Constante.EQUIPE_BLEU), match.getArretsEquipe().get(Constante.EQUIPE_BLEU), match.getArretsEquipe().get(Constante.EQUIPE_ROUGE), match.getPointsEquipe().get(Constante.EQUIPE_ROUGE))));

        this.getContentPane().add(topPanel, BorderLayout.NORTH);

//        bottomPanel = new BottomPanel(this);
        this.bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout());

        JButton confirmerBtn = new JButton("Ajouter au Match");
        confirmerBtn.addActionListener((e) -> {
            try {
                match.addSet(this.getImageAvantTirPath(), this.getImageApresTirPath());
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });

        JButton resetBtn = new JButton("Reinitialiser");
        resetBtn.addActionListener((e) -> {
            try {
                this.getMatch().resetScore();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
//            Connection conn = null;
//            PreparedStatement ps = null;
//            ResultSet res = null;
//            try {
//
//            } catch (Exception ex) {
//                ex.printStackTrace();
//                JOptionPane.showMessageDialog(this, ex.getMessage());
//            } finally {
//
//            }
        });

        bottomPanel.add(resetBtn);
        bottomPanel.add(confirmerBtn);



//        this.getContentPane().add(topPanel, BorderLayout.NORTH);
        this.getContentPane().add(bottomPanel, BorderLayout.SOUTH);

        match.setOnScoreChangeListener((score) -> this.changeScore(score));
        match.setOnArretChangeListener((arret) -> this.changeScore(null));

    }

    public void setImagePath(String imagePath) throws IOException {
        this.imagepath = imagePath;
    }

    public void changeScore(Map<String, Integer> newScore) {
        topPanel.removeAll();
        topPanel.add(new JLabel(String.format("Bleu %s (%s) - (%s) %s Rouge", match.getPointsEquipe().get(Constante.EQUIPE_BLEU), match.getArretsEquipe().get(Constante.EQUIPE_BLEU), match.getArretsEquipe().get(Constante.EQUIPE_ROUGE), match.getPointsEquipe().get(Constante.EQUIPE_ROUGE))));

//        topPanel.add(new JLabel("Bleu " + match.getPointsEquipe().get(Constante.EQUIPE_BLEU) + " - " + match.getPointsEquipe().get(Constante.EQUIPE_ROUGE) + " Rouge") );
        ComponentUtils.updateContainer(topPanel);
    }

    public String getImagePath() {
        return this.imagepath;
    }

    public void update() {

    }

    public String getImageAvantTirPath() {
        return imageAvantTirPath;
    }

    public void setImageAvantTirPath(String imageAvantTirPath) {
        this.imageAvantTirPath = imageAvantTirPath;
    }

    public String getImageApresTirPath() {
        return imageApresTirPath;
    }

    public void setImageApresTirPath(String imageApresTirPath) {
        this.imageApresTirPath = imageApresTirPath;
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }
}
