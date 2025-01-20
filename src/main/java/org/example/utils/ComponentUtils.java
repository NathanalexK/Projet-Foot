package org.example.utils;

import java.awt.*;

public class ComponentUtils {
    public static void updateContainer(Container container){
        container.invalidate();
        container.validate();
        container.repaint();
    }
}
