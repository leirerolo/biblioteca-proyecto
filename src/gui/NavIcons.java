package gui;

import java.awt.*;

import javax.swing.*;

/**
 * Iconos dibujados por código (sin depender de emojis ni fuentes del sistema).
 *
 * Se pintan con el color foreground del componente que los usa, así que cambian
 * automáticamente cuando el botón está activo/inactivo.
 */
public final class NavIcons {
    private NavIcons() {}

    public static Icon home(int size) {
        return new DrawIcon(Type.HOME, size);
    }

    public static Icon search(int size) {
        return new DrawIcon(Type.SEARCH, size);
    }

    public static Icon book(int size) {
        return new DrawIcon(Type.BOOK, size);
    }

    private enum Type { HOME, SEARCH, BOOK }

    private static final class DrawIcon implements Icon {
        private final Type type;
        private final int size;

        private DrawIcon(Type type, int size) {
            this.type = type;
            this.size = Math.max(12, size);
        }

        @Override public int getIconWidth() { return size; }
        @Override public int getIconHeight() { return size; }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

                Color color = (c != null && c.getForeground() != null) ? c.getForeground() : Color.BLACK;
                g2.setColor(color);
                g2.setStroke(new BasicStroke(Math.max(1.6f, size / 10f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                switch (type) {
                    case HOME:
                        drawHome(g2, x, y, size);
                        break;
                    case SEARCH:
                        drawSearch(g2, x, y, size);
                        break;
                    case BOOK:
                        drawBook(g2, x, y, size);
                        break;
                }
            } finally {
                g2.dispose();
            }
        }

        private static void drawHome(Graphics2D g2, int x, int y, int s) {
            int pad = Math.max(2, s / 8);
            int left = x + pad;
            int top = y + pad;
            int right = x + s - pad;
            int bottom = y + s - pad;

            // Tejado
            int roofTopX = x + s / 2;
            int roofTopY = top;
            g2.drawLine(left, top + s / 3, roofTopX, roofTopY);
            g2.drawLine(roofTopX, roofTopY, right, top + s / 3);

            // Casa (cuerpo)
            int bodyTop = top + s / 3;
            g2.drawRoundRect(left, bodyTop, right - left, bottom - bodyTop, s / 6, s / 6);

            // Puerta
            int doorW = Math.max(3, s / 5);
            int doorH = Math.max(4, s / 3);
            int doorX = x + s / 2 - doorW / 2;
            int doorY = bottom - doorH;
            g2.drawRoundRect(doorX, doorY, doorW, doorH, s / 8, s / 8);
        }

        private static void drawSearch(Graphics2D g2, int x, int y, int s) {
            int pad = Math.max(2, s / 10);
            int r = (s - pad * 2) * 2 / 5; // radio aprox

            int cx = x + pad + r;
            int cy = y + pad + r;

            // Lupa
            g2.drawOval(cx - r, cy - r, r * 2, r * 2);
            // Mango
            int hx1 = cx + r - 1;
            int hy1 = cy + r - 1;
            int hx2 = x + s - pad;
            int hy2 = y + s - pad;
            g2.drawLine(hx1, hy1, hx2, hy2);
        }

        private static void drawBook(Graphics2D g2, int x, int y, int s) {
            int pad = Math.max(2, s / 10);
            int left = x + pad;
            int top = y + pad;
            int w = s - pad * 2;
            int h = s - pad * 2;

            // Dos páginas (rectángulos redondeados)
            int half = w / 2;
            g2.drawRoundRect(left, top, half, h, s / 6, s / 6);
            g2.drawRoundRect(left + half, top, w - half, h, s / 6, s / 6);

            // Lomo
            g2.drawLine(left + half, top + 2, left + half, top + h - 2);

            // Líneas (texto)
            int lineY1 = top + h / 3;
            int lineY2 = top + h / 2;
            g2.drawLine(left + 4, lineY1, left + half - 4, lineY1);
            g2.drawLine(left + 4, lineY2, left + half - 8, lineY2);
        }
    }
}
