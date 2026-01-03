package gui;


import java.util.List;

import domain.Libro;

public class Navigator {

	protected static JFrameInicio inicio;
	protected static JFrameExplorar explorar;
	protected static JFrameReservas reservas;
	protected static JFramePrincipal principal;
	protected static JFrameAdmin admin;
	
	public static void init(JFramePrincipal principalFrame, List<Libro> libros) {
		principal = principalFrame;
		inicio = new JFrameInicio(libros);
        explorar = new JFrameExplorar(libros);
        reservas = new JFrameReservas(principalFrame, libros);
        admin = new JFrameAdmin(libros);
    }
	
	public static void showInicio() {
		hideAll();
		inicio.setVisible(true);
	}
	
	public static void showReservas() {
		hideAll();
		
		if(reservas != null) {
			reservas.actualizarReservas();
			reservas.setVisible(true);
		}
	}
	
	public static void showExplorar() {
		hideAll();
		explorar.setVisible(true);
	}
	// ************* ADMIN *************************
	public static void showAdmin() {
		hideAll();
		if (admin != null) admin.setVisible(true);
	}
	
	private static void hideAll() {
		if (inicio != null) inicio.setVisible(false);
        if (explorar != null) explorar.setVisible(false);
        if (reservas != null) reservas.setVisible(false);
        if (principal != null) principal.setVisible(false);
        if (admin != null) admin.setVisible(false);
	}
	
	public static void applyThemeAll() {

	    if (inicio != null) {
	        inicio.aplicarTema();
	        if (inicio.lblToggleDark != null)
	            inicio.lblToggleDark.setText(JFramePrincipal.darkMode ? "☀️" : "🌙");
	    }

	    if (explorar != null) {
	        explorar.aplicarTema();
	        if (explorar.lblToggleDark != null)
	            explorar.lblToggleDark.setText(JFramePrincipal.darkMode ? "☀️" : "🌙");
	    }

	    if (reservas != null) {
	        reservas.aplicarTema();
	        if (reservas.lblToggleDark != null)
	            reservas.lblToggleDark.setText(JFramePrincipal.darkMode ? "☀️" : "🌙");
	    }

	    if (principal != null) {
	        principal.aplicarTema();
	        if (principal.lblToggleDark != null)
	            principal.lblToggleDark.setText(JFramePrincipal.darkMode ? "☀️" : "🌙");
	    }
	}



}
