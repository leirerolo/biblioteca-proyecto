package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class JFrameInicio extends JFrame {

    private static final long serialVersionUID = 1L;

    public JFrameInicio() {
        super("Biblioteca - Inicio");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

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
        });

        top.add(search, BorderLayout.CENTER);
        top.add(btnExplorador, BorderLayout.EAST);

        // --- Área central "Populares" (placeholder) ---
        JPanel center = new JPanel();
        center.setLayout(new BorderLayout());
        JLabel lbTitulo = new JLabel("Populares", SwingConstants.LEFT);
        lbTitulo.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        lbTitulo.setFont(lbTitulo.getFont().deriveFont(Font.BOLD, 18f));
        center.add(lbTitulo, BorderLayout.NORTH);

        // Grid de tarjetas de libros (placeholder visual)
        JPanel grid = new JPanel(new GridLayout(2, 4, 12, 12));
        grid.setBorder(BorderFactory.createEmptyBorder(8, 12, 12, 12));
        for (int i = 1; i <= 8; i++) {
            JPanel card = new JPanel(new BorderLayout());
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 220, 220)),
                    BorderFactory.createEmptyBorder(8, 8, 8, 8)
            ));
            card.add(new JLabel("Libro " + i, SwingConstants.CENTER), BorderLayout.CENTER);
            grid.add(card);
        }
        center.add(new JScrollPane(grid), BorderLayout.CENTER);

        // --- Menú con "Inicio" (por si abres este frame desde otros y quieres unificar) ---
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Navegación");
        JMenuItem miInicio = new JMenuItem("Inicio");
        miInicio.addActionListener(a -> Navigator.irAInicio(this));
        menu.add(miInicio);
        menuBar.add(menu);
        setJMenuBar(menuBar);

        // Montaje
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(top, BorderLayout.NORTH);
        getContentPane().add(center, BorderLayout.CENTER);
    }

    /** Atajo cómodo para abrir el inicio en el EDT */
    public static void open() {
        SwingUtilities.invokeLater(() -> new JFrameInicio().setVisible(true));
    }
}
