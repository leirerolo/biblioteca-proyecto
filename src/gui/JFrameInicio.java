package gui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;

import db.ReservaDAO;
import domain.Libro;
import domain.Reserva;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JFrameInicio extends JFramePrincipal {

    private static final long serialVersionUID = 1L;
    
    //panel principal que contiene los libros
    private JPanel mainPanel;

    public JFrameInicio(List<Libro> libros) {
    	super(libros, "inicio");
        this.libros = libros;
        
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        
        //hereda de JFramePrincipal
		this.inicializarPanelCentral();
		aplicarTema();
    }

    private void inicializarPanelCentral(){
    	mainPanel = new JPanel(new BorderLayout());
    	//mainPanel.setBackground(JFramePrincipal.darkMode ? new Color(30,30,30) : Color.WHITE);
    	mainPanel.setBackground(Color.white);
    	mainPanel.setOpaque(true);
    	this.add(mainPanel, BorderLayout.CENTER);
    	
    	
	    if (libros == null || libros.isEmpty()) {
            JLabel l = new JLabel("No hay libros para mostrar", JLabel.CENTER);
            l.setFont(new Font("SansSerif", Font.PLAIN, 16));
            mainPanel.add(l, BorderLayout.CENTER);
            return;
        }
	    
	    //para que se actualice el panel de libros, si se modifican las valoraciones
	    mainPanel.add(crearPanelTopLibros(), BorderLayout.CENTER);
	    
	    mainPanel.revalidate();
	    mainPanel.repaint(); 
	    
    } 
    
    //método para construir el panel de libros del top 6
    private JPanel crearPanelTopLibros() {
    	Color fondo = JFramePrincipal.darkMode ? new Color(30,30,30) : Color.WHITE;

    	List<Libro> disponibles = new ArrayList<>();
    	try {
    	    ReservaDAO reservaDAO = new ReservaDAO();
    	    List<Reserva> reservasActivas = reservaDAO.getTodasLasReservasActivas();
    	    
    	    //AYUDA DE IA: USO DE CHAT GPT para aclarar la función stream()
    	    disponibles = libros.stream()
    	        .filter(lib -> reservasActivas.stream()
    	            .noneMatch(r -> r.getLibro().equals(lib)))
    	        .sorted((a, b) -> Double.compare(b.getValoracion(), a.getValoracion()))
    	        .limit(6)
    	        .collect(Collectors.toList());
    	    
    	} catch (SQLException ex) {
    	    ex.printStackTrace();
    	}


        JPanel grid = new JPanel(new GridLayout(0, 3, 12, 12));
        //grid.setBackground(fondo);
        grid.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        for (Libro lib : disponibles) {
        	grid.add(buildBookCard(lib));
        }

        JLabel titulo = new JLabel("Mejor valorados", JLabel.LEFT);
        titulo.setFont(fuenteTitulo);
        titulo.setForeground(new Color(0,102,204));
        titulo.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(fondo);
        wrapper.add(titulo, BorderLayout.NORTH);
        wrapper.add(grid, BorderLayout.CENTER);
        
        return wrapper;
    }
    
    private JPanel buildBookCard(Libro lib) {
        JPanel card = new JPanel(new BorderLayout());
        Color fondoCard = JFramePrincipal.darkMode ? new Color(40,40,40) : Color.WHITE;
        card.setBackground(fondoCard);
        //card.setOpaque(true);
        
        
        Border bordeNormal = new MatteBorder(1, 1, 1, 1, new Color(180, 180, 180)); // gris claro
        Border bordeHover;

        if (JFramePrincipal.darkMode) {
            bordeHover = new MatteBorder(2, 2, 2, 2, Color.WHITE); // hover blanco en dark mode
        } else {
            bordeHover = new MatteBorder(2, 2, 2, 2, new Color(100, 100, 100)); // hover gris oscuro en light mode
        }

        card.setBorder(bordeNormal);
        

        // Portada
        JLabel img = new JLabel();
        img.setHorizontalAlignment(JLabel.CENTER);
        if (lib.getPortada() != null) img.setIcon(lib.getPortada());
        card.add(img, BorderLayout.CENTER);

        // Texto (título + autor + valoración)
        JPanel info = new JPanel(new GridLayout(0, 1));
        info.setBackground(JFramePrincipal.darkMode ? new Color(40,40,40) : Color.WHITE);
        JLabel t = new JLabel(lib.getTitulo());
        t.setFont(new Font("SansSerif", Font.BOLD, 12));
        JLabel a = new JLabel(lib.getAutor());
        a.setFont(new Font("SansSerif", Font.PLAIN, 11));
        JLabel r = new JLabel("★ " + String.format("%.2f", lib.getValoracion()));
        r.setFont(new Font("SansSerif", Font.PLAIN, 11));

        info.add(t);
        info.add(a);
        info.add(r);
        info.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        card.add(info, BorderLayout.SOUTH);

        card.setOpaque(true); 
        info.setOpaque(true); 
        img.setOpaque(true);
        img.setBackground(card.getBackground());
        img.putClientProperty("theme-card", true);

        
        Color noSeleccionado = JFramePrincipal.darkMode ? new Color(40,40,40) : Color.WHITE;
        Color seleccionado = JFramePrincipal.darkMode ? new Color(60,60,60) : new Color(245,245,245);

        
        MouseAdapter listener = new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBorder(bordeHover);
                card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBorder(bordeNormal);
                card.setCursor(Cursor.getDefaultCursor());
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                new JDialogLibro(JFrameInicio.this, lib).setVisible(true);
            }
        };


        // Añadir el listener a card
        addMouseListenerRecursively(card, listener);

        return card;
    }
    
    //método para refrescar los libros
    public void refrescarTopLibros() {
    	if (mainPanel!=null) {
    		mainPanel.removeAll();
    		
    		mainPanel.add(crearPanelTopLibros(), BorderLayout.CENTER);
    		
    		mainPanel.revalidate();
    		mainPanel.repaint();
    	}
    }
    
    private void addMouseListenerRecursively(Component comp, MouseAdapter listener) {
        comp.addMouseListener(listener);
        if (comp instanceof Container cont) {
            for (Component child : cont.getComponents()) {
                addMouseListenerRecursively(child, listener);
            }
        }
    }

}
