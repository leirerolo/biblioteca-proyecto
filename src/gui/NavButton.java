package gui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;

/**
 * Botón de navegación estilo "app" (hover + indicador activo).
 *
 * - Usa JToggleButton para poder marcar el activo.
 * - Dibuja una línea inferior cuando está seleccionado.
 * - Hover: cambia ligeramente el fondo.
 */
public class NavButton extends JToggleButton {
    private static final long serialVersionUID = 1L;

    private boolean hovered = false;

    public NavButton(String text) {
        this(text, null);
    }

    /**
     * @param text Texto del botón
     * @param icon Icono (no dependiente de emojis/fuentes). Si es null, se muestra solo el texto.
     */
    public NavButton(String text, Icon icon) {
        super(text);

        if (icon != null) {
            setIcon(icon);
            setIconTextGap(8);
            setHorizontalTextPosition(SwingConstants.RIGHT);
            setVerticalTextPosition(SwingConstants.CENTER);
        }

        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);

        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setHorizontalAlignment(SwingConstants.CENTER);
        setVerticalAlignment(SwingConstants.CENTER);

        // un pelín más de aire
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                hovered = true;
                repaint();
            }
            @Override public void mouseExited(MouseEvent e) {
                hovered = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // Colores según tema
            Color textNormal = JFramePrincipal.darkMode ? new Color(220, 220, 220) : new Color(30, 30, 30);
            Color hoverBg = JFramePrincipal.darkMode ? new Color(60, 60, 60) : new Color(235, 235, 235);
            Color selectedBg = JFramePrincipal.darkMode ? new Color(70, 70, 70) : new Color(245, 245, 245);
            Color accent = JFramePrincipal.darkMode ? new Color(180, 120, 255) : new Color(150, 0, 150);

            // Fondo en hover / seleccionado
            if (isSelected()) {
                g2.setColor(selectedBg);
                g2.fillRoundRect(6, 4, w - 12, h - 8, 16, 16);
            } else if (hovered) {
                g2.setColor(hoverBg);
                g2.fillRoundRect(6, 4, w - 12, h - 8, 16, 16);
            }

            // Indicador activo (underline)
            if (isSelected()) {
                g2.setColor(accent);
                g2.fillRoundRect(10, h - 6, w - 20, 4, 8, 8);
            }

            // Color del texto
            setForeground(isSelected() ? accent : textNormal);

            super.paintComponent(g2);
        } finally {
            g2.dispose();
        }
    }
}
