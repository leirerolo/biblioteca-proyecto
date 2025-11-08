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
        add(mainPanel, BorderLayout.CENTER);
    	
//    	// --- Cabecera ---
//	    JLabel lblPopulares = new JLabel("Populares");
//	    lblPopulares.setFont(fuenteTitulo);
//	    lblPopulares.setForeground(new Color(0, 102, 204));
//	    lblPopulares.setHorizontalAlignment(JLabel.LEFT);
//	    lblPopulares.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
//	    mainPanel.add(lblPopulares, BorderLayout.NORTH);
//	    
	    if (libros == null || libros.isEmpty()) {
            JLabel l = new JLabel("No hay libros para mostrar", JLabel.CENTER);
            l.setFont(new Font("SansSerif", Font.PLAIN, 16));
            mainPanel.add(l, BorderLayout.CENTER);
            return;
        }

        // Top 6 por valoración (desc) – compatible con JDK 8+
        List<Libro> top6 = libros.stream()
                .sorted((a, b) -> Double.compare(b.getValoracion(), a.getValoracion()))
                .limit(6)
                .collect(Collectors.toList());

        JPanel grid = new JPanel(new GridLayout(0, 3, 16, 16));
        grid.setBackground(Color.WHITE);
        grid.setBorder(new MatteBorder(16, 16, 16, 16, Color.WHITE));

        for (Libro lib : top6) grid.add(buildBookCard(lib));

        JLabel titulo = new JLabel("Mejor valorados", JLabel.LEFT);
        titulo.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        titulo.setBorder(new MatteBorder(12, 16, 8, 16, Color.WHITE));

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.add(titulo, BorderLayout.NORTH);
        wrapper.add(grid, BorderLayout.CENTER);

        mainPanel.add(new JScrollPane(wrapper), BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
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
        JLabel r = new JLabel("★ " + String.format("%.1f", lib.getValoracion()));
        r.setFont(new Font("SansSerif", Font.PLAIN, 11));

        info.add(t);
        info.add(a);
        info.add(r);
        info.setBorder(new MatteBorder(8, 8, 8, 8, Color.WHITE));
        card.add(info, BorderLayout.SOUTH);
        
        Color noSeleccionado = Color.white;
    	Color seleccionado = new Color(245, 245, 245);

        // (Opcional) click
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override 
            public void mouseClicked(java.awt.event.MouseEvent e) {
            	JDialogLibro dialog = new JDialogLibro(JFrameInicio.this, lib);
                dialog.setVisible(true);
            }
            
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
        
 
}
