package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import domain.Genero;

public class GenreChip extends JLabel {
    private static final long serialVersionUID = 1L;

    private final Genero genero;
    private final int arc = 16;

    public GenreChip(Genero genero) {
        this.genero = (genero != null) ? genero : Genero.DESCONOCIDO;
        setText(this.genero.getNombre());
        setFont(new Font("SansSerif", Font.BOLD, 12));
        setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        setOpaque(false); // pintamos nosotros el fondo redondeado
    }

    public Genero getGenero() {
        return genero;
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        return new Dimension(d.width, Math.max(d.height, 22));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color bg = getBackgroundForGenero(genero, JFramePrincipal.darkMode);
            Color fg = getForegroundForBackground(bg, JFramePrincipal.darkMode);

            // Fondo
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

            // Borde (un poco más oscuro)
            g2.setColor(darken(bg, JFramePrincipal.darkMode ? 0.18f : 0.12f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);

            setForeground(fg);
        } finally {
            g2.dispose();
        }
        super.paintComponent(g);
    }

    private static Color getBackgroundForGenero(Genero genero, boolean darkMode) {
        Color base;
        switch (genero) {
            case FANTASIA -> base = new Color(142, 116, 255);
            case CLASICO -> base = new Color(255, 197, 87);
            case DISTOPIA -> base = new Color(255, 110, 110);
            case REALISMO_MAGICO -> base = new Color(90, 200, 250);
            case MISTERIO -> base = new Color(64, 156, 255);
            case THRILLER -> base = new Color(255, 89, 204);
            case TERROR -> base = new Color(60, 60, 60);
            case ROMANTICA -> base = new Color(255, 125, 170);
            case INFANTIL -> base = new Color(78, 209, 140);
            case HISTORICA -> base = new Color(185, 140, 95);
            case FILOSOFICA -> base = new Color(120, 200, 160);
            case NOVELA_PSICOLOGICA -> base = new Color(155, 155, 155);
            case SATIRICA -> base = new Color(255, 165, 0);
            default -> base = new Color(170, 170, 170);
        }

        // En dark mode bajamos brillo para que no “cante”
        if (darkMode) {
            base = darken(base, 0.25f);
        }

        // Un pelín de transparencia para estilo “chip”
        return new Color(base.getRed(), base.getGreen(), base.getBlue(), 210);
    }

    private static Color getForegroundForBackground(Color bg, boolean darkMode) {
        double lum = (0.2126 * bg.getRed() + 0.7152 * bg.getGreen() + 0.0722 * bg.getBlue()) / 255.0;
        if (darkMode) return lum < 0.55 ? Color.WHITE : new Color(20, 20, 20);
        return lum < 0.60 ? Color.WHITE : new Color(20, 20, 20);
    }

    private static Color darken(Color c, float amount) {
        amount = Math.max(0f, Math.min(1f, amount));
        int r = (int) Math.max(0, c.getRed() * (1f - amount));
        int g = (int) Math.max(0, c.getGreen() * (1f - amount));
        int b = (int) Math.max(0, c.getBlue() * (1f - amount));
        int a = c.getAlpha();
        return new Color(r, g, b, a);
    }
}

