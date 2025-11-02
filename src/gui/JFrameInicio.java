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
    	super(libros);
        this.libros = libros;
        
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        //setSize(900, 600);
        setLocationRelativeTo(null);
        
        this.inicializarPanelSuperior();
		this.inicializarPanelCentral();
    }

    private void inicializarPanelCentral(){
    	JPanel mainPanel = new JPanel(new BorderLayout());
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

        // --- Área central "Populares" (placeholder) ---
        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(Color.white);

        JLabel lbTitulo = new JLabel("Populares");
        lbTitulo.setFont(fuenteTitulo);
        lbTitulo.setForeground(new Color(0, 102, 204));
        lbTitulo.setHorizontalAlignment(JLabel.LEFT);
		lbTitulo.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        center.add(lbTitulo, BorderLayout.NORTH);

        // Grid de tarjetas de libros (placeholder visual)
        JPanel grid = new JPanel(new GridLayout(0, 2, 12, 12));
        grid.setBackground(Color.white);
        grid.setBorder(BorderFactory.createEmptyBorder(8, 12, 12, 12));
        
        // Si hay libros, los mostramos
	    if (libros != null && !libros.isEmpty()) {
	        Collections.sort(libros, new Libro());//ordenar por valoracion
	        for (Libro libro : libros) {
	        	JPanel libroPanel = new JPanel(new BorderLayout());
	            libroPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
	            libroPanel.setBackground(Color.WHITE);
	
	            // Portada
	            JLabel portada = new JLabel(libro.getPortada(), JLabel.CENTER);
	            portada.setForeground(Color.GRAY);
	            portada.setOpaque(true);
	            portada.setBackground(new Color(245, 245, 245));
	            libroPanel.add(portada, BorderLayout.CENTER);
	
	            // Título
	            JLabel titulo = new JLabel(libro.getTitulo(), JLabel.CENTER);
	            libroPanel.add(titulo, BorderLayout.SOUTH);
	
	            grid.add(libroPanel);
	            
	            // ********* CLICK EN UN LIBRO *****************************
	            MouseAdapter mouseAdapter = new MouseAdapter() {
	    			@Override
	    			public void mouseClicked(MouseEvent e) {
	    				JDialogLibro infoLibro = new JDialogLibro(JFrameInicio.this, libro);
	    				infoLibro.setVisible(true);
	    			}
	    		};
	    		libroPanel.addMouseListener(mouseAdapter);
	    		grid.add(libroPanel);
	        }
	    }else {
	    	grid.add(new JLabel("No hay libros disponibles", JLabel.CENTER));
	    }
	        
        JScrollPane scrollPane = new JScrollPane(grid);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        center.add(scrollPane, BorderLayout.CENTER);


        // Montaje
        JPanel panelCentralInicio = new JPanel(new BorderLayout());
        //panelCentralInicio.add(top, BorderLayout.NORTH);
        panelCentralInicio.add(center, BorderLayout.CENTER);
        getContentPane().remove(1); 
        getContentPane().add(panelCentralInicio, BorderLayout.CENTER);

    
        revalidate();
        repaint();
        
        
    } 
        
 
}
