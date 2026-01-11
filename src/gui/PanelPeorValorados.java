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

import db.LibroDAO;
import domain.Libro;



public class PanelPeorValorados extends JPanel {

    private static final long serialVersionUID = 1L;

    JPanel mainPanel;
    private final Color AZUL_TARJETA = new Color(70, 130, 180);

    public PanelPeorValorados() {
        setLayout(new BorderLayout());
        setBackground(Theme.DEFAULT.backgroundMain);

        // Contenedor con márgenes para que se vea el rosa
        JPanel container = new JPanel(new BorderLayout());
        container.setBorder(new EmptyBorder(20, 20, 20, 20));
        container.setOpaque(false);
        add(container, BorderLayout.CENTER);

        mainPanel = new JPanel(new GridLayout(2, 3, 15, 15));
        mainPanel.setBackground(Theme.DEFAULT.backgroundMain);
        mainPanel.setOpaque(true);

        container.add(mainPanel, BorderLayout.CENTER);

        cargarPeorValorados();
    }

    private void cargarPeorValorados() {

        LibroDAO libroDAO = new LibroDAO();
        mainPanel.removeAll();

        try {
            List<Object[]> datos = libroDAO.getLibrosPeorValorados(6);

            int usados = 0;

            for (Object[] obj : datos) {
                Libro libro = (Libro) obj[0];
                double media = (double) obj[1];

                mainPanel.add(crearPanelLibro(libro, media));
                usados++;
            }

            while (usados < 6) {
                JPanel panel = new JPanel(new BorderLayout());
                panel.setBackground(AZUL_TARJETA);
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
        setBackground(theme.backgroundMain);
        mainPanel.setBackground(theme.backgroundMain);

        for (Component c : mainPanel.getComponents()) {
            if (c instanceof JPanel card) {
                // Si es una de las tarjetas con contenido, actualizamos subcomponentes
                actualizarComponentesRecursivo(card, theme);
            }
        }
        revalidate();
        repaint();
    }

    private void actualizarComponentesRecursivo(JPanel parent, Theme theme) {
        for (Component child : parent.getComponents()) {
            if (child instanceof JLabel) {
                child.setForeground(Color.WHITE); 
            } else if (child instanceof JPanel panel) {
                panel.setBackground(AZUL_TARJETA);
                actualizarComponentesRecursivo(panel, theme);
            }
        }
    }
    
    private JPanel crearPanelLibro(Libro libro, double media) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(AZUL_TARJETA); // Fondo azul
        panel.setOpaque(true);
        panel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
        
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
        JLabel lblTitulo = new JLabel(libro.getTitulo(), JLabel.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setForeground(Color.WHITE); // Texto blanco
        
        // Valoración
        JLabel lblMedia = new JLabel("Valoración media: " + String.format("%.2f", media), JLabel.CENTER);
        lblMedia.setFont(new Font("Arial", Font.PLAIN, 14));
        lblMedia.setForeground(Color.WHITE); // Texto blanco

        JPanel info = new JPanel(new GridLayout(2, 1));
        info.setBackground(AZUL_TARJETA); // Fondo azul para el panel de texto
        info.setOpaque(true);
        info.add(lblTitulo);
        info.add(lblMedia);

        panel.add(lblPortada, BorderLayout.CENTER);
        panel.add(info, BorderLayout.SOUTH);

        return panel;
    }
}


