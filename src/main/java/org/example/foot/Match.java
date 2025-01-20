package org.example.foot;

import org.example.database.ConnectionManager;
import org.example.utils.Constante;
import org.opencv.core.Point;

import javax.swing.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class Match {
    /*
        .cages[0][0]------------------------x
        |                                   |
        |                                   |
        |                                   |
        x------------------------------cages[0][1]
     */
    private Map<String, Integer> pointsEquipe = new HashMap<>();
    private Map<String, Integer> arretsEquipe = new HashMap<>();
    private List<Set> sets = new ArrayList<>();
    private Integer id;
    private String nom;
    private Consumer<Map<String, Integer>> onScoreChange;
    private Consumer<Map<String, Integer>> onArretChange;

    public Match() throws Exception {
        pointsEquipe.put(Constante.EQUIPE_BLEU, 0);
        pointsEquipe.put(Constante.EQUIPE_ROUGE, 0);

        arretsEquipe.put(Constante.EQUIPE_BLEU, 0);
        arretsEquipe.put(Constante.EQUIPE_ROUGE, 0);

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet res = null;

        try {
            conn = ConnectionManager.getConnection();
            ps = conn.prepareStatement("INSERT INTO match VALUES (default, ?) RETURNING id");
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            res = ps.executeQuery();

            if(res.next()) {
                this.setId(res.getInt(1));
            }

            ps = conn.prepareStatement("SELECT nom_equipe, point, arret FROM score");
            res = ps.executeQuery();

            while (res.next()) {
                this.getPointsEquipe().put(res.getString(1), res.getInt(2));
                this.getArretsEquipe().put(res.getString(1), res.getInt(3));
            }

        } catch (Exception e) {
            throw e;
        } finally {
            if(res != null) res.close();
            if(ps != null) ps.close();
            if(conn != null) conn.close();
        }

    }



    public void ajouterPoint(Equipe e) throws Exception {
        int currentPoint = this.getPointsEquipe().get(e.getNom());
        this.getPointsEquipe().put(e.getNom(), currentPoint + 1);

        Integer idEquipe = e.getNom().equalsIgnoreCase(Constante.EQUIPE_BLEU) ? 1 : 2;
        Connection conn = null;
        PreparedStatement ps = null;
//        ResultSet res = null;
        try {
            conn = ConnectionManager.getConnection();
            ps = conn.prepareStatement("INSERT INTO equipe_marquant VALUES (default, ?, ?)");
            ps.setInt(1, this.getId());
            ps.setInt(2, idEquipe);
            ps.executeUpdate();

            ps = conn.prepareStatement("UPDATE score SET point = ? WHERE nom_equipe = ?");
            ps.setInt(1, this.getPointsEquipe().get(e.getNom()));
            ps.setString(2, e.getNom());
            ps.executeUpdate();

        } catch (Exception ex) {
            throw ex;

        } finally {
//            if(res != null) res.close();
            if(ps != null) ps.close();
            if(conn != null);
        }

        if(onScoreChange != null) {
            onScoreChange.accept(this.getPointsEquipe());
        }
    }

    public void ajouterArret(Equipe e) throws Exception{
        int currentPoint = this.getArretsEquipe().get(e.getNom());

        this.getArretsEquipe().put(e.getNom(), currentPoint + 1);

        Integer idEquipe = e.getNom().equalsIgnoreCase(Constante.EQUIPE_BLEU) ? 1 : 2;
        Connection conn = null;
        PreparedStatement ps = null;
//        ResultSet res = null;
        try {
            conn = ConnectionManager.getConnection();
//            ps = conn.prepareStatement("INSERT INTO equipe_marquant VALUES (default, ?, ?)");
//            ps.setInt(1, this.getId());
//            ps.setInt(2, idEquipe);
//            ps.executeUpdate();

            ps = conn.prepareStatement("UPDATE score SET arret = ? WHERE nom_equipe = ?");
            ps.setInt(1, this.getArretsEquipe().get(e.getNom()));
            ps.setString(2, e.getNom());
            ps.executeUpdate();

        } catch (Exception ex) {
            throw ex;

        } finally {
//            if(res != null) res.close();
            if(ps != null) ps.close();
            if(conn != null);
        }

        if(onArretChange != null) {
            onArretChange.accept(this.getArretsEquipe());
        }
    }

    public Map<String, Integer> getPointsEquipe() {
        return pointsEquipe;
    }

    public void setPointsEquipe(Map<String, Integer> pointsEquipe) {
        this.pointsEquipe = pointsEquipe;
    }

    public void resetScore() throws Exception {
        Connection conn = null;
        PreparedStatement ps = null;
//        ResultSet res = null;

        try {
            conn = ConnectionManager.getConnection();
            ps = conn.prepareStatement("UPDATE score SET point = 0, arret = 0 WHERE 1=1 ");
            ps.executeUpdate();


            this.getPointsEquipe().put(Constante.EQUIPE_BLEU, 0);
            this.getPointsEquipe().put(Constante.EQUIPE_ROUGE, 0);

            this.getArretsEquipe().put(Constante.EQUIPE_BLEU, 0);
            this.getArretsEquipe().put(Constante.EQUIPE_ROUGE, 0);

            if(this.onScoreChange != null) {
                this.onScoreChange.accept(this.getPointsEquipe());
            }

            if(this.onArretChange != null) {
                this.onArretChange.accept(this.getArretsEquipe());
            }


        } catch (Exception e) {
            throw e;
        } finally {
//            if(res != null) res.close();
            if(ps != null) ps.close();
            if(conn != null) conn.close();
        }
    }

    //    public int getPointEquipe1() {
//        return pointEquipe1;
//    }
//
//    public void setPointEquipe1(int pointEquipe1) {
//        this.pointEquipe1 = pointEquipe1;
//    }
//
//    public int getPointEquipe2() {
//        return pointEquipe2;
//    }
//
//    public void setPointEquipe2(int pointEquipe2) {
//        this.pointEquipe2 = pointEquipe2;
//    }

    public List<Set> getSets() {
        return sets;
    }

    public void setSets(List<Set> sets) {
        this.sets = sets;
    }

    public void addSet(Set set) throws Exception {
        this.getSets().add(set);

        Equipe equipeMarquant = set.getEquipeMarquant();
        if(equipeMarquant != null) {
            this.ajouterPoint(equipeMarquant);
        }

        JOptionPane.showMessageDialog(null, equipeMarquant != null ? equipeMarquant.getNom(): "Aucun equipe a marqué!");
    }

    public void addSet(String image1, String image2) throws Exception {
        Set set = new Set(image1, image2);
        set.setMatch(this);
        this.addSet(set);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Consumer<Map<String, Integer>> getOnScoreChange() {
        return onScoreChange;
    }

    public void setOnScoreChangeListener(Consumer<Map<String, Integer>> onScoreChange) {
        this.onScoreChange = onScoreChange;
    }

    public Map<String, Integer> getArretsEquipe() {
        return arretsEquipe;
    }

    public void setArretsEquipe(Map<String, Integer> arretsEquipe) {
        this.arretsEquipe = arretsEquipe;
    }

    public void setOnScoreChange(Consumer<Map<String, Integer>> onScoreChange) {
        this.onScoreChange = onScoreChange;
    }

    public Consumer<Map<String, Integer>> getOnArretChange() {
        return onArretChange;
    }

    public void setOnArretChangeListener(Consumer<Map<String, Integer>> onArretChange) {
        this.onArretChange = onArretChange;
    }
}
