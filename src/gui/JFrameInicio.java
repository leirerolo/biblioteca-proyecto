package gui;

import javax.swing.*;
import javax.swing.border.MatteBorder;

import domain.Libro;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class JFrameInicio extends JFramePrincipal {

    private static final long serialVersionUID = 1L;
    
    //panel principal que contiene los libros
    private JPanel mainPanel;

    public JFrameInicio(List<Libro> libros) {
    	super(libros, "inicio");
        this.libros = libros;
        
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        this.inicializarPanelSuperior(); //hereda de JFramePrincipal
		this.inicializarPanelCentral();
    }

    private void inicializarPanelCentral(){
    	mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        add(mainPanel, BorderLayout.CENTER);
    	
	    if (libros == null || libros.isEmpty()) {
            JLabel l = new JLabel("No hay libros para mostrar", JLabel.CENTER);
            l.setFont(new Font("SansSerif", Font.PLAIN, 16));
            mainPanel.add(l, BorderLayout.CENTER);
            return;
        }
	    
	    //para que se actualice el panel de libros, si se modifican las valoraciones
	    mainPanel.add(new JScrollPane(crearPanelTopLibros()), BorderLayout.CENTER);
	    mainPanel.revalidate();
	    mainPanel.repaint(); 
    } 
    
    //método para construir el panel de libros del top 6
    private JPanel crearPanelTopLibros() {
    	// Top 6 por valoración (descendente)
        List<Libro> top6 = libros.stream()
                .sorted((a, b) -> Double.compare(b.getValoracion(), a.getValoracion()))
                .limit(6)
                .collect(Collectors.toList());

        JPanel grid = new JPanel(new GridLayout(0, 3, 16, 16));
        grid.setBackground(Color.WHITE);
        grid.setBorder(new MatteBorder(16, 16, 16, 16, Color.WHITE));

        for (Libro lib : top6) grid.add(buildBookCard(lib));

        JLabel titulo = new JLabel("Mejor valorados", JLabel.LEFT);
        titulo.setFont(fuenteTitulo);
        titulo.setForeground(new Color(0,102,204));
        titulo.setBorder(new MatteBorder(12, 16, 8, 16, Color.WHITE));

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.add(titulo, BorderLayout.NORTH);
        wrapper.add(grid, BorderLayout.CENTER);
        
        return wrapper;
    }
    
    private JPanel buildBookCard(Libro lib) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new MatteBorder(1, 1, 1, 1, new Color(230, 230, 230)));

        // Portada
        JLabel img = new JLabel();
        img.setHorizontalAlignment(JLabel.CENTER);
        if (lib.getPortada() != null) img.setIcon(lib.getPortada());
        card.add(img, BorderLayout.CENTER);

        // Texto (título + autor + valoración)
        JPanel info = new JPanel(new GridLayout(0, 1));
        info.setBackground(Color.WHITE);
        JLabel t = new JLabel(lib.getTitulo());
        t.setFont(new Font("SansSerif", Font.BOLD, 12));
        JLabel a = new JLabel(lib.getAutor());
        a.setFont(new Font("SansSerif", Font.PLAIN, 11));
        JLabel r = new JLabel("★ " + String.format("%.2f", lib.getValoracion()));
        r.setFont(new Font("SansSerif", Font.PLAIN, 11));

        info.add(t);
        info.add(a);
        info.add(r);
        info.setBorder(new MatteBorder(8, 8, 8, 8, Color.WHITE));
        card.add(info, BorderLayout.SOUTH);
        
        Color noSeleccionado = Color.white;
    	Color seleccionado = new Color(245, 245, 245);

        //Eventos de ratón
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new java.awt.event.MouseAdapter() {
        	//al hacer click, se abre el diálogo de la info del libro
            @Override 
            public void mouseClicked(java.awt.event.MouseEvent e) {
            	JDialogLibro dialog = new JDialogLibro(JFrameInicio.this, lib);
                dialog.setVisible(true);
            }
            //al posar el ratón, cambia a formato selección
            @Override
	        public void mouseEntered(MouseEvent e) {
	            card.setBackground(seleccionado);
	            card.setCursor(new Cursor(Cursor.HAND_CURSOR));
	            card.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 2));
	        }

	        @Override
	        public void mouseExited(MouseEvent e) {
	        	card.setBackground(noSeleccionado);
	        	card.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	        	card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
	        }
        });

        return card;
    }
    
    //método para refrescar los libros
    public void refrescarTopLibros() {
    	if (mainPanel!=null) {
    		mainPanel.removeAll();
    		mainPanel.add(new JScrollPane(crearPanelTopLibros()), BorderLayout.CENTER);
    		mainPanel.revalidate();
    		mainPanel.repaint();
    	}
    }
}
