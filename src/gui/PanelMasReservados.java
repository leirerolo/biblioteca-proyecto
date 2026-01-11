package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.sql.SQLException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import db.ReservaDAO;
import domain.Libro;

public class PanelMasReservados extends JPanel {
	private static final long serialVersionUID = 1L;

	JPanel mainPanel;
	private Theme initialTheme= Theme.LIGHT;
    public PanelMasReservados() {
        setLayout(new BorderLayout());

        setBackground(initialTheme.backgroundMain);
        
        
        // Panel contenedor con márgenes para separar mainPanel de los bordes
        JPanel container = new JPanel(new BorderLayout());
        container.setBorder(new EmptyBorder(20, 20, 20, 20)); // margen arriba, izquierda, abajo, derecha
        container.setOpaque(false); // para que se vea el fondo del PanelMasReservados
        add(container, BorderLayout.CENTER);
        
        mainPanel = new JPanel(new GridLayout(2,3,15,15));
        mainPanel.setBackground(initialTheme.backgroundMain);
        mainPanel.setOpaque(true);
        container.add(mainPanel, BorderLayout.CENTER);
        
        cargarMasReservados();
        this.setVisible(true);
        this.setOpaque(true);
    }

    private void cargarMasReservados() {
        ReservaDAO reservaDAO = new ReservaDAO();
        mainPanel.removeAll(); //limpiar panel antes de añadir
        
        try {
        	List<Object[]> datos = reservaDAO.getLibrosMasReservados(6);

            int usados = 0;

            for (Object[] obj : datos) {
                Libro libro = (Libro) obj[0];
                int reservas = (int) obj[1];

                mainPanel.add(crearPanelLibro(libro, reservas));
                usados++;
            }

            // Rellenar huecos vacíos
            //si no hay 6 libros que hayan sido reservados mínimo 1 vez,
            //dejamos paneles vacíos
            while (usados < 6) {
            	JPanel panel = new JPanel(new BorderLayout());
            	panel.setBackground(Color.WHITE);
                panel.setOpaque(true);
            	
            	mainPanel.add(panel);
                usados++;
            }
            mainPanel.revalidate();
            mainPanel.repaint();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void applyTheme(Theme theme) {
        this.setBackground(theme.backgroundMain);
        mainPanel.setBackground(theme.backgroundMain);

        for (Component c : mainPanel.getComponents()) {
            if (c instanceof JPanel card) {
            	card.setBackground(theme.cardBackground);
            	actualizarComponentesRecursivo(card, theme);
            }
        }
        revalidate();
        repaint();
    }
    private void actualizarComponentesRecursivo(JPanel parent, Theme theme) {
        for (Component child : parent.getComponents()) {
            if (child instanceof JLabel lbl) {
            	lbl.setForeground(theme.cardText);
            } else if (child instanceof JPanel panel) {
            	panel.setBackground(theme.cardBackground);
            	actualizarComponentesRecursivo(panel, theme);
            }
        }
    }

    private JPanel crearPanelLibro(Libro libro, int reservas) {
        JPanel panel = new JPanel(new BorderLayout());
        
        //Color azulLibro = new Color(70, 130, 180); // Steel Blue
        panel.setBackground(initialTheme.cardBackground);
        panel.setOpaque(true);
        
        // Portada
        JLabel lblPortada = new JLabel();
        lblPortada.setHorizontalAlignment(JLabel.CENTER);

        if (libro.getPortada() != null) {
            ImageIcon icon = libro.getPortada();
            lblPortada.setIcon(new ImageIcon(
                icon.getImage().getScaledInstance(160, 220, Image.SCALE_SMOOTH)
            ));
        }

        // Título
        JLabel lblTitulo = new JLabel(libro.getTitulo());
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setHorizontalAlignment(JLabel.CENTER);
        lblTitulo.setForeground(initialTheme.cardText);
        
        // Número reservas
        JLabel lblR = new JLabel("Reservado " + reservas + " veces");
        lblR.setFont(new Font("Arial", Font.PLAIN, 14));
        lblR.setHorizontalAlignment(JLabel.CENTER);
        lblR.setForeground(initialTheme.cardText);

        JPanel info = new JPanel(new GridLayout(2, 1));
        info.setBackground(initialTheme.cardBackground);
        info.setOpaque(true);
        info.add(lblTitulo);
        info.add(lblR);

        panel.add(lblPortada, BorderLayout.CENTER);
        panel.add(info, BorderLayout.SOUTH);

        panel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
        
        return panel;
    }
}
