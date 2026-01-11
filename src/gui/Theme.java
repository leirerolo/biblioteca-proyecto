package gui;

import java.awt.Color;

public enum Theme {

    LIGHT(
        new Color(245,245,245),
        new Color(230,230,250),
        Color.BLACK, 
        new Color(180,210,255),   
        Color.BLACK 
    ),

    DARK(
        new Color(40,40,40),
        new Color(60,60,60),
        Color.WHITE,
        new Color(30,60,120),
        Color.WHITE
    ),

    DEFAULT(
        new Color(245,245,245),
        new Color(230,230,250),
        Color.BLACK,
        new Color(180,210,255),
        Color.BLACK
    );

    public final Color backgroundMain;
    public final Color backgroundPanel;
    public final Color textColor;

    public final Color cardBackground;
    public final Color cardText;

    Theme(Color backgroundMain, Color backgroundPanel, Color textColor,
          Color cardBackground, Color cardText) {

        this.backgroundMain = backgroundMain;
        this.backgroundPanel = backgroundPanel;
        this.textColor = textColor;
        this.cardBackground = cardBackground;
        this.cardText = cardText;
    }
}
