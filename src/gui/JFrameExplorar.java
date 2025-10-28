package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import domain.Libro;

//hereda de inicio para heredar su cabecera
public class JFrameExplorar extends JFramePrincipal {
	private static final long serialVersionUID = 1L;
	private List<Libro> libros;
	private JTextField txtFiltro;
	private JPanel gridPanel;
	private JComboBox<String> opciones;
	
	public JFrameExplorar(List<Libro> libros) {
		super(libros);
		this.inicializarPanelSuperior();
		this.inicializarPanelCentral();
	}
	
	private void inicializarPanelCentral() {
		JPanel mainPanel = new JPanel(new BorderLayout());
		
		// --------- Cabecera (filtro texto + opciones filtro)
		JPanel cabecera = new JPanel(new BorderLayout());
		txtFiltro = new JTextField("Buscar por título...");
		txtFiltro.setForeground(Color.GRAY);
		
		//document listener para el filtro de texto
		DocumentListener miTxtListener = new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				filtrarLibros();
			}
			@Override
			public void removeUpdate(DocumentEvent e) {
				filtrarLibros();
			}
			@Override
			public void changedUpdate(DocumentEvent e) {
			}
		};
		txtFiltro.getDocument().addDocumentListener(miTxtListener);
		opciones = new JComboBox<>(new String[]{"Ordenar", "Por autor", "Por valoración"});
		
		//listener para la selección
		opciones.addActionListener((e) -> {
			String op = (String) opciones.getSelectedItem();
			if (op.equals("Por autor")) {
				ordenarAutor();
			} else if (op.equals("Por valoración")) {
				
			}
		});
		cabecera.add(txtFiltro, BorderLayout.CENTER);
		cabecera.add(opciones, BorderLayout.EAST);
		mainPanel.add(cabecera, BorderLayout.NORTH);
		
		
		//----------- Centro: libros --------------
		gridPanel = new JPanel(new GridLayout(0,1,0,10));
	    gridPanel.setBackground(Color.WHITE);
	    gridPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

	    //scroll para cuando haya libros
	    JScrollPane scrollPane = new JScrollPane(gridPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
	            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	    scrollPane.getVerticalScrollBar().setUnitIncrement(16);

	    mainPanel.add(scrollPane, BorderLayout.CENTER);
	    this.add(mainPanel, BorderLayout.CENTER); // añadir al panel central

	    // Mostrar mensaje inicial o todos los libros
	    filtrarLibros();
	    
	}
	
	private void filtrarLibros() {
		gridPanel.removeAll(); //vaciar
		String tit = txtFiltro.getText().toLowerCase();
		String op = (String) opciones.getSelectedItem();
		
		// Si el filtro está vacío
	    if ((tit.equals("") || tit.equals("Buscar por título..."))) {
	    	JLabel mensaje = new JLabel("No hay coincidencias");
	    	mensaje.setHorizontalAlignment(JLabel.CENTER);
	    	gridPanel.add(mensaje);
	    }
	    
	    //Mostrar libros que coincidan
	    else {
	    	ArrayList<Libro> filtrados = new ArrayList<>();
	    	for (Libro libro : libros) {
	    		if (libro.getTitulo().toLowerCase().contains(tit)) {
	    			filtrados.add(libro);
	    		}
	    	}
    	//si se ha seleccionado alguna opción, ordenamos los libros en función de esta
	    	if (!op.equals("Ordenar")) {
	    		if (op.equals("Por autor")) {
	    			
	    		}
	    	}
	    }
		
		
	}
	private void ordenarAutor() {
		Collections.sort(libros);
	}
}
