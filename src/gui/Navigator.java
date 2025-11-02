package gui;

import java.awt.Window;
import java.util.List;

import domain.Libro;

public class Navigator {

	private static JFrameInicio inicio;
	private static JFrameExplorar explorar;
	private static JFrameReservas reservas;
	private static JFramePrincipal principal;
	
	public static void init(List<Libro> libros) {
        inicio = new JFrameInicio(libros);
        explorar = new JFrameExplorar(libros);
        reservas = new JFrameReservas(libros);
    }
	
	public static void showInicio() {
		hideAll();
		inicio.setVisible(true);
	}
	
	public static void showReservas() {
		hideAll();
		reservas.setVisible(true);
	}
	
	public static void showExplorar() {
		hideAll();
		explorar.setVisible(true);
	}
	
	private static void hideAll() {
		if (inicio != null) inicio.setVisible(false);
        if (explorar != null) explorar.setVisible(false);
        if (reservas != null) reservas.setVisible(false);
        if (principal != null) principal.setVisible(false);
	}

}
