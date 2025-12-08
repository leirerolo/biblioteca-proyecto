package gui;

import java.awt.BorderLayout;
import java.awt.Color;
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

    public PanelPeorValorados() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 180, 220));

        // Contenedor con márgenes para que se vea el rosa
        JPanel container = new JPanel(new BorderLayout());
        container.setBorder(new EmptyBorder(20, 20, 20, 20));
        container.setOpaque(false);
        add(container, BorderLayout.CENTER);

        mainPanel = new JPanel(new GridLayout(2, 3, 15, 15));
        mainPanel.setBackground(new Color(240, 180, 220));
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
                panel.setBackground(Color.WHITE);
                mainPanel.add(panel);
                usados++;
            }

            mainPanel.revalidate();
            mainPanel.repaint();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private JPanel crearPanelLibro(Libro libro, double media) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setOpaque(true);
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

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

        // Valoración
        JLabel lblMedia = new JLabel("Valoración media: " + String.format("%.2f", media), JLabel.CENTER);
        lblMedia.setFont(new Font("Arial", Font.PLAIN, 14));

        JPanel info = new JPanel(new GridLayout(2, 1));
        info.setBackground(Color.WHITE);
        info.add(lblTitulo);
        info.add(lblMedia);

        panel.add(lblPortada, BorderLayout.CENTER);
        panel.add(info, BorderLayout.SOUTH);

        return panel;
    }
}
