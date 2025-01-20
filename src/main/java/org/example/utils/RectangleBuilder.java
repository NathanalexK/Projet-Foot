package org.example.utils;

public class RectangleBuilder {
    Ligne verticaleGauche;
    Ligne verticaleDroite;
    Ligne horizontal;

    public boolean estRectangle() {
        return verticaleGauche != null && verticaleDroite != null && horizontal != null;
    }

}
