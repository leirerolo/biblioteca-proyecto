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
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import domain.Libro;

public class JFrameReservas extends JFramePrincipal{
	
	private JTextField txtFiltro;
	private JComboBox<String> opciones;
	private JPanel panelLibros;

	public JFrameReservas(List<Libro> libros) {
		super(libros);
		this.libros = libros;
		this.inicializarPanelSuperior(); //hereda de JFramePrincipal
		this.inicializarPanelCentral();
	}
	
	private void inicializarPanelCentral() {
		JPanel mainPanel = new JPanel(new BorderLayout());
		
		JPanel contentPanel = new JPanel(new BorderLayout());
	    contentPanel.setBackground(Color.WHITE);
		
		// (filtro texto + opciones filtro)
		JPanel cabecera = new JPanel(new BorderLayout());
		txtFiltro = new JTextField("Buscar por título...");
		txtFiltro.setForeground(Color.GRAY);
		
		//editar filtro
		txtFiltro.addFocusListener(new java.awt.event.FocusAdapter() {

			@Override
			public void focusGained(FocusEvent e) {
				if(txtFiltro.getText().equals("Buscar por título...")) {
					txtFiltro.setText("");
		            txtFiltro.setForeground(Color.BLACK);
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if(txtFiltro.getText().isEmpty()) {
					txtFiltro.setText("Buscar por título...");
		            txtFiltro.setForeground(Color.GRAY);
				}
			}
			
		});
		
		//actualizar filtro
		DocumentListener miTxtListener = new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e) {
				actualizarFiltro();
				
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				actualizarFiltro();
				
			}

			@Override
			public void changedUpdate(DocumentEvent e) {			
			}
			
		};
		
		txtFiltro.getDocument().addDocumentListener(miTxtListener);
		opciones = new JComboBox<>(new String[]{"Ordenar", "Por autor", "Por valoración"});
		
		//listener para la selección
		opciones.addActionListener((e) -> {
			filtrarLibros();
		});
		cabecera.add(txtFiltro, BorderLayout.CENTER);
		cabecera.add(opciones, BorderLayout.EAST);
		mainPanel.add(cabecera, BorderLayout.NORTH);
		
		//cabezera
		JLabel lblReservas = new JLabel("Reservas");
		lblReservas.setFont(fuenteTitulo);
		lblReservas.setForeground(new Color(0, 102, 204));
		lblReservas.setHorizontalAlignment(JLabel.LEFT);
		lblReservas.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
		contentPanel.add(lblReservas, BorderLayout.NORTH);
		
		
		//Centro: xona de libros
	    
		panelLibros = new JPanel();
		panelLibros.setLayout(new BoxLayout(panelLibros, BoxLayout.Y_AXIS));
		panelLibros.setBackground(Color.WHITE);
		panelLibros.setOpaque(true);
		panelLibros.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
	
		//lista de libros (esto es para probar)
//	    JPanel librosReserva = new JPanel();
//	    librosReserva.setBackground(Color.WHITE);
//	    librosReserva.setLayout(new GridLayout(libros.size(), 1, 5, 5));
//	    
	    for (Libro libro : libros) {
            JLabel lblLibro = new JLabel(libro.getTitulo() + " - " + libro.getAutor());
            lblLibro.setOpaque(true);
            lblLibro.setBackground(new Color(245, 245, 245));
            lblLibro.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
            panelLibros.add(lblLibro); 
            System.out.println(libro.getTitulo());
        }	
	    
	    

		//scroll para cuando haya libros
		JScrollPane scrollPane = new JScrollPane(panelLibros, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);

		contentPanel.add(scrollPane, BorderLayout.CENTER);
		mainPanel.add(contentPanel, BorderLayout.CENTER);
		this.add(mainPanel, BorderLayout.CENTER); // añadir al panel central

		// Mostrar mensaje inicial o todos los libros
		filtrarLibros();
		

	    mainPanel.add(contentPanel,BorderLayout.CENTER);
		
	}
	
	private void actualizarFiltro() {
			String texto = txtFiltro.getText();
			
			if (!texto.equals("Buscar por título...") && !texto.isEmpty()) {
				txtFiltro.setForeground(Color.BLACK);
			} else if (texto.isEmpty()) {
				txtFiltro.setText("Buscar por título...");
		        txtFiltro.setForeground(Color.GRAY);
			}
			
			filtrarLibros();
	}
	
	private void filtrarLibros() {
		panelLibros.removeAll(); //vaciar
		String tit = txtFiltro.getText().toLowerCase();
		if (tit.equals("Buscar por título...")) {
			tit = ""; //vaciar filtro para poder escribir
		}
		ArrayList<Libro> filtrados = new ArrayList<>();
		
		if (tit.isEmpty()) {
			filtrados.addAll(libros);
		} else {
			for (Libro libro : libros) {
				if (libro.getTitulo().toLowerCase().startsWith(tit)) {
	    			filtrados.add(libro);
	    		}
			}
		}
		String op = (String) opciones.getSelectedItem();
		if (op!=null && !op.equals("Ordenar")) {
			ordenarLista(filtrados);
		}
		if (filtrados.isEmpty()) {
			JLabel mensaje = new JLabel("No hay reservas...");
			mensaje.setHorizontalAlignment(JLabel.CENTER);
			panelLibros.add(mensaje);
			
		} else {
			for (Libro l : filtrados) {
		    	JPanel panelLibro = new JPanel(new BorderLayout());
		        panelLibro.setPreferredSize(new Dimension(Integer.MAX_VALUE, 120));
		        panelLibro.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
		        panelLibro.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

		    	//west: portada
		    	JLabel portada = new JLabel(l.getPortada(), JLabel.CENTER);
		    	panelLibro.add(portada, BorderLayout.WEST);
		    	
		    	//center: info
		    	JPanel panelInfo = new JPanel(new GridLayout(3,1,0,10));
		    	JLabel titulo = new JLabel(l.getTitulo(), JLabel.LEFT);
		    	titulo.setFont(fuenteTitulo);
		    	titulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		    	panelInfo.add(titulo);
		    	
		    	JLabel autor = new JLabel(l.getAutor(), JLabel.LEFT);
		    	autor.setFont(fuenteMenu);
		    	autor.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		    	panelInfo.add(autor);
		    	
		    	JLabel valoracion = new JLabel(String.valueOf(l.getValoracion()), JLabel.LEFT);
		    	valoracion.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		    	panelInfo.add(valoracion);
		    	
		    	panelLibro.add(panelInfo, BorderLayout.CENTER);
		    	panelLibros.add(panelLibro);
		    }
		}
		panelLibros.revalidate();
		panelLibros.repaint();
	}
	
	private void ordenarLista(List<Libro> lista) {
		String op = (String) opciones.getSelectedItem();
		if (op.equals("Por autor")) {
			Collections.sort(lista);
		}else if (op.equals("Por valoración")) {
			Collections.sort(lista, new Libro());
		}
	}
	
}
