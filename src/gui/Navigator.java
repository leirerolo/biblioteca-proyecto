package gui;


import java.util.List;

import domain.Libro;

public class Navigator {

	protected static JFrameInicio inicio;
	protected static JFrameExplorar explorar;
	protected static JFrameReservas reservas;
	protected static JFramePrincipal principal;
	protected static JFrameAdmin admin;

	/**
	 * Limpia las referencias a ventanas cacheadas.
	 *
	 * IMPORTANTE: esta app reutiliza ventanas (static) para navegar.
	 * Si un usuario cierra sesión y entra otro distinto, hay que recrearlas
	 * o se seguirá viendo el estado del usuario anterior.
	 */
	public static void reset() {
		inicio = null;
		explorar = null;
		reservas = null;
		principal = null;
		admin = null;
	}
	
	public static void init(JFramePrincipal principalFrame, List<Libro> libros) {
		principal = principalFrame;
		inicio = new JFrameInicio(libros);
        explorar = new JFrameExplorar(libros);
        reservas = new JFrameReservas(principalFrame, libros);
        admin = new JFrameAdmin(libros);
    }

	/**
	 * Inicializa navegación en modo admin (solo ventana de administración).
	 * Sirve para los flujos de logout/login donde no existe un JFramePrincipal.
	 */
	public static void initAdmin(JFrameAdmin adminFrame) {
		principal = null;
		inicio = null;
		explorar = null;
		reservas = null;
		admin = adminFrame;
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
