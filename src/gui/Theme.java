package gui;

import java.awt.Color;


public enum Theme {
	LIGHT(new Color(245,245,245), new Color(230,230,250), Color.BLACK),
	DARK(new Color(40,40,40), new Color(60,60,60), Color.WHITE),
	DEFAULT(new Color(245,245,245), new Color(230,230,250), Color.BLACK); 
	
	public final Color backgroundMain;
	public final Color backgroundPanel;
	public final Color textColor;
	
	Theme(Color backgroundMain, Color backgroundPanel, Color textColor){
		this.backgroundMain= backgroundMain;
		this.backgroundPanel= backgroundPanel;
		this.textColor= textColor;
	}

}
