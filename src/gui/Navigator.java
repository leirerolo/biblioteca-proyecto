package gui;

import java.awt.Window;
import java.util.List;

import domain.Libro;

public class Navigator {

	protected static JFrameInicio inicio;
	protected static JFrameExplorar explorar;
	protected static JFrameReservas reservas;
	protected static JFramePrincipal principal;
	
	public static void init(JFramePrincipal principalFrame, List<Libro> libros) {
        inicio = new JFrameInicio(libros);
        explorar = new JFrameExplorar(libros);
        reservas = new JFrameReservas(principalFrame, libros);
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
	
	private static void hideAll() {
		if (inicio != null) inicio.setVisible(false);
        if (explorar != null) explorar.setVisible(false);
        if (reservas != null) reservas.setVisible(false);
        if (principal != null) principal.setVisible(false);
	}

}
