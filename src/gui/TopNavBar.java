package gui;

import java.awt.*;

import javax.swing.*;

/**
 * Barra superior de navegación (Inicio / Explorar / Reservas)
 * con botones tipo "app":
 * - Botones toggle (uno activo)
 * - Hover suave
 * - Indicador activo (línea inferior)
 */
public class TopNavBar extends JPanel {
    private static final long serialVersionUID = 1L;

    private final ButtonGroup group = new ButtonGroup();

    public final NavButton btnInicio;
    public final NavButton btnExplorar;
    public final NavButton btnReservas;

    public TopNavBar(String ventanaActiva, Runnable onInicio, Runnable onExplorar, Runnable onReservas, Font font) {
        super(new GridLayout(1, 3, 0, 0));
        setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

        // Iconos dibujados por código (sin depender de emojis/fuentes)
        btnInicio = new NavButton("Inicio", NavIcons.home(16));
        btnExplorar = new NavButton("Explorar", NavIcons.search(16));
        btnReservas = new NavButton("Reservas", NavIcons.book(16));

        if (font != null) {
            btnInicio.setFont(font);
            btnExplorar.setFont(font);
            btnReservas.setFont(font);
        }

        group.add(btnInicio);
        group.add(btnExplorar);
        group.add(btnReservas);

        add(btnInicio);
        add(btnExplorar);
        add(btnReservas);

        // Selección inicial
        setActive(ventanaActiva);

        // Acciones
        btnInicio.addActionListener(e -> {
            // Asegura estado toggle coherente
            btnInicio.setSelected(true);
            if (onInicio != null) onInicio.run();
        });
        btnExplorar.addActionListener(e -> {
            btnExplorar.setSelected(true);
            if (onExplorar != null) onExplorar.run();
        });
        btnReservas.addActionListener(e -> {
            btnReservas.setSelected(true);
            if (onReservas != null) onReservas.run();
        });

        applyTheme();
    }

    public void setActive(String ventanaActiva) {
        String v = (ventanaActiva == null) ? "" : ventanaActiva.toLowerCase();
        if (v.equals("explorar")) {
            btnExplorar.setSelected(true);
        } else if (v.equals("reservas")) {
            btnReservas.setSelected(true);
        } else {
            btnInicio.setSelected(true);
        }
        repaint();
    }

    public void applyTheme() {
        Color menuBg = JFramePrincipal.darkMode ? new Color(40, 40, 40) : Color.WHITE;
        setOpaque(true);
        setBackground(menuBg);
        repaint();
    }
}

