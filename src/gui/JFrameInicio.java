package gui;

import javax.swing.*;

import domain.Libro;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.List;

public class JFrameInicio extends JFramePrincipal {

    private static final long serialVersionUID = 1L;

    public JFrameInicio(List<Libro> libros) {
    	super(libros, "inicio");
        this.libros = libros;
        
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        //setSize(900, 600);
        setLocationRelativeTo(null);
        
        this.inicializarPanelSuperior(); //hereda de JFramePrincipal
		this.inicializarPanelCentral();
    }

    private void inicializarPanelCentral(){
    	JPanel mainPanel = new JPanel(new BorderLayout());
    	mainPanel.setBackground(Color.WHITE);
    	
    	/* YA TENEMOS BUSCADOR Y EXPLORAR
        // --- Barra superior con buscador y botón para ejemplo ---
        JPanel top = new JPanel(new BorderLayout(8, 8));
        top.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        JTextField tfBuscar = new JTextField();
        JButton btnBuscar = new JButton("Buscar");
        JPanel search = new JPanel(new BorderLayout(6, 6));
        search.add(tfBuscar, BorderLayout.CENTER);
        search.add(btnBuscar, BorderLayout.EAST);

        
        JButton btnExplorador = new JButton("Explorador (demo)");
        btnExplorador.addActionListener((ActionEvent e) -> {
            // Abre cualquier otra ventana de tu app (ejemplo)
            // new JFrameExplorador().setVisible(true);
            // dispose(); // si quieres cerrar Inicio al ir al explorador
            JOptionPane.showMessageDialog(this, "Aquí abrirías tu JFrameExplorador");
            Navigator.showExplorar();
        });

        top.add(search, BorderLayout.CENTER);
        top.add(btnExplorador, BorderLayout.EAST);
        
        mainPanel.add(top);*/

    	// --- Cabecera ---
	    JLabel lblPopulares = new JLabel("Populares");
	    lblPopulares.setFont(fuenteTitulo);
	    lblPopulares.setForeground(new Color(0, 102, 204));
	    lblPopulares.setHorizontalAlignment(JLabel.LEFT);
	    lblPopulares.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
	    mainPanel.add(lblPopulares, BorderLayout.NORTH);
	    
	    // --- Cuadrícula de libros ---
	    JPanel gridPanel = new JPanel(new GridLayout(0, 2, 15, 15)); //creará las filas que se necesiten, con dos columnas en cada una
	    gridPanel.setBackground(Color.WHITE);
	    gridPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

	    // Si hay libros, los mostramos
	    if (libros != null && !libros.isEmpty()) {
	    	Collections.sort(libros, new Libro()); //ordenar por valoración
	        
	    	Color noSeleccionado = Color.white;
	    	Color seleccionado = new Color(245, 245, 245);
	    	
	    	for (Libro libro : libros) {
	            JPanel libroPanel = new JPanel(new BorderLayout());
	            libroPanel.setBorder(javax.swing.BorderFactory.createLineBorder(Color.LIGHT_GRAY));
	            libroPanel.setBackground(Color.WHITE);

	            // Portada
	            JLabel portada = new JLabel(libro.getPortada(), JLabel.CENTER);          
	            libroPanel.add(portada, BorderLayout.CENTER);

	            // Título
	            JLabel titulo = new JLabel(libro.getTitulo(), JLabel.CENTER);
	            libroPanel.add(titulo, BorderLayout.SOUTH);
	            
	            
	            // ********* CLICK EN UN LIBRO *****************************
	            MouseAdapter mouseAdapter = new MouseAdapter() {
	    			@Override
	    			public void mouseClicked(MouseEvent e) {
	    				JDialogLibro infoLibro = new JDialogLibro(JFrameInicio.this, libro);
	    				infoLibro.setVisible(true);
	    			}
	    		};
	    		
	    		//al pasar raton
	    		libroPanel.addMouseListener(new MouseAdapter() {
	    			@Override
	    	        public void mouseEntered(MouseEvent e) {
	    	            libroPanel.setBackground(seleccionado);
	    	            libroPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
	    	            libroPanel.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 2));
	    	        }

	    	        @Override
	    	        public void mouseExited(MouseEvent e) {
	    	            libroPanel.setBackground(noSeleccionado);
	    	            libroPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	    	            libroPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
	    	        }
	    		});
	    		
	    		libroPanel.addMouseListener(mouseAdapter);
	    		gridPanel.add(libroPanel);
	        }
	    } else {
	        gridPanel.add(new JLabel("No hay libros disponibles", JLabel.CENTER));
	    }

	    // Añadimos todo al contentPanel
	    JPanel scrollable = new JPanel(new BorderLayout());
	    javax.swing.JScrollPane scrollPane = new JScrollPane(gridPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	    scrollPane.setBorder(null);
	    scrollPane.getVerticalScrollBar().setUnitIncrement(16);
	    scrollable.add(scrollPane, BorderLayout.CENTER);

	    mainPanel.add(scrollable, BorderLayout.CENTER);

	    // Añadir al panel central
	    add(mainPanel, BorderLayout.CENTER);


        
        /*// Montaje
        JPanel panelCentralInicio = new JPanel(new BorderLayout());
        //panelCentralInicio.add(top, BorderLayout.NORTH);
        panelCentralInicio.add(center, BorderLayout.CENTER);
        getContentPane().remove(1); 
        getContentPane().add(panelCentralInicio, BorderLayout.CENTER);

    
        revalidate();
        repaint();*/
        
        
    } 
        
 
}
