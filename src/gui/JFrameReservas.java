package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import domain.Libro;
import domain.Reserva;
import domain.User;

public class JFrameReservas extends JFramePrincipal{
	private static final long serialVersionUID = 1L;
	private JTable tablaReservas;
	private DefaultTableModel modeloTabla;
	private User user = User.getLoggedIn(); //la ventana de reservas es del user que ha iniciado sesión

	public JFrameReservas(List<Libro> libros) {
		super(libros,"reservas");
		this.libros = new ArrayList<>();
		
		if (this.user.getReservas()!=null) {
			for (Reserva reserva : this.user.getReservas()) {
				this.libros.add(reserva.getLibro());
			}
		}
		this.inicializarPanelSuperior(); //hereda de JFramePrincipal
		this.inicializarPanelCentral();
	}
	
	private void inicializarPanelCentral() {		
		JPanel contentPanel = new JPanel(new BorderLayout());
	    contentPanel.setBackground(Color.WHITE);
		
		//cabecera
		JLabel lblReservas = new JLabel("Reservas");
		lblReservas.setFont(fuenteTitulo);
		lblReservas.setForeground(new Color(0, 102, 204));
		lblReservas.setHorizontalAlignment(JLabel.LEFT);
		lblReservas.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
		contentPanel.add(lblReservas, BorderLayout.NORTH);
		
		//Centro: zona de libros
		//Si no hay reservas:
		if(libros.isEmpty()) {
			JLabel mensaje = new JLabel("No hay reservas...");
			mensaje.setHorizontalAlignment(JLabel.CENTER);
			contentPanel.add(mensaje, BorderLayout.CENTER);
			
		//Si hay reservas --> TABLA
		} else {
			String[] columnas = {"Título", "Autor", "Fecha reserva", "Días restantes"};
			//modelo de la tabla: celdas no editables, y filas a 0 (luego añadimos)
			modeloTabla = new DefaultTableModel(columnas, 0) { 
				@Override
				public boolean isCellEditable(int row, int col) {
					return false;
				}
			};
			
			for (Reserva reserva : user.getReservas()) {
				Object[] fila = {reserva.getLibro().getTitulo(), reserva.getLibro().getAutor(), reserva.getFecha(), reserva.getDuracion()};
				modeloTabla.addRow(fila);
			}
			tablaReservas = new JTable(modeloTabla);
			tablaReservas.setFillsViewportHeight(true); //para que llene el espacio
			tablaReservas.setRowHeight(25);
			tablaReservas.setBackground(Color.WHITE);
			tablaReservas.getTableHeader().setBackground(new Color(230, 230, 250));
            tablaReservas.getTableHeader().setFont(fuenteMenu);
            
            JScrollPane scrollPane = new JScrollPane(tablaReservas);
            contentPanel.add(scrollPane, BorderLayout.CENTER);
		}
		this.add(contentPanel, BorderLayout.CENTER);

				
	}
	
	
}
