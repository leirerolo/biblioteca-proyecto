package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Window;
import java.util.Map;
import java.util.WeakHashMap;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * Toast notifications (mensajitos pequeños abajo).
 *
 * Uso:
 *   Toast.showToast(this, "Añadido a favoritos ⭐", Toast.Type.SUCCESS);
 */
public final class Toast {

    public enum Type { INFO, SUCCESS, WARNING, ERROR }

    private static final int MARGIN_BOTTOM = 18;
    private static final int MARGIN_SIDE = 16;
    private static final int STACK_GAP = 8;

    private static final Map<Window, Integer> STACK = new WeakHashMap<>();

    private Toast() {}

    public static void showToast(java.awt.Component parent, String message) {
        showToast(parent, message, Type.INFO);
    }

    public static void showToast(java.awt.Component parent, String message, Type type) {
        if (parent == null || message == null || message.isBlank()) return;

        Runnable r = () -> {
            Window owner = (parent instanceof Window w) ? w : SwingUtilities.getWindowAncestor(parent);
            if (owner == null) return;

            int stackIndex = reserveStackIndex(owner);

            JWindow toast = new JWindow(owner);
            toast.setAlwaysOnTop(true);
            toast.setFocusableWindowState(false);

            ToastPanel panel = new ToastPanel(message, type);
            toast.setContentPane(panel);
            toast.pack();

            // posición (abajo-centro) + stacking
            try {
                var p = owner.getLocationOnScreen();
                Dimension s = owner.getSize();
                Dimension ts = toast.getSize();

                int x = p.x + (s.width - ts.width) / 2;
                int y = p.y + s.height - ts.height - MARGIN_BOTTOM - (stackIndex * (ts.height + STACK_GAP));

                // evita que se salga por los lados
                x = Math.max(p.x + MARGIN_SIDE, Math.min(x, p.x + s.width - ts.width - MARGIN_SIDE));

                toast.setLocation(x, y);
            } catch (Exception ignored) {
                // si no se puede calcular, lo dejamos por defecto
            }

            // intentamos transparencia (si está soportado)
            boolean supportsOpacity = true;
            try {
                toast.setOpacity(0f);
            } catch (Exception ex) {
                supportsOpacity = false;
            }

            toast.setVisible(true);

            final int fadeIntervalMs = 25;
            final float fadeStep = 0.08f;
            final int visibleMs = 2200;

            if (!supportsOpacity) {
                // sin opacidad: visible y luego desaparece
                new Timer(visibleMs, ev -> {
                    ((Timer) ev.getSource()).stop();
                    toast.setVisible(false);
                    toast.dispose();
                    releaseStackIndex(owner);
                }).start();
                return;
            }

            // Fade in
            Timer fadeIn = new Timer(fadeIntervalMs, null);
            fadeIn.addActionListener(ev -> {
                float op = toast.getOpacity();
                op = Math.min(1f, op + fadeStep);
                try { toast.setOpacity(op); } catch (Exception ignored2) {}
                if (op >= 1f) {
                    fadeIn.stop();

                    // Mantener visible
                    new Timer(visibleMs, ev2 -> {
                        ((Timer) ev2.getSource()).stop();

                        // Fade out
                        Timer fadeOut = new Timer(fadeIntervalMs, null);
                        fadeOut.addActionListener(ev3 -> {
                            float o = toast.getOpacity();
                            o = Math.max(0f, o - fadeStep);
                            try { toast.setOpacity(o); } catch (Exception ignored3) {}
                            if (o <= 0f) {
                                fadeOut.stop();
                                toast.setVisible(false);
                                toast.dispose();
                                releaseStackIndex(owner);
                            }
                        });
                        fadeOut.start();

                    }).start();
                }
            });
            fadeIn.start();
        };

        if (SwingUtilities.isEventDispatchThread()) r.run();
        else SwingUtilities.invokeLater(r);
    }

    private static synchronized int reserveStackIndex(Window w) {
        int idx = STACK.getOrDefault(w, 0);
        STACK.put(w, idx + 1);
        return idx;
    }

    private static synchronized void releaseStackIndex(Window w) {
        int idx = STACK.getOrDefault(w, 1) - 1;
        if (idx <= 0) STACK.remove(w);
        else STACK.put(w, idx);
    }

    private static final class ToastPanel extends JComponent {
        private static final long serialVersionUID = 1L;

        private final String message;
        private final Type type;

        ToastPanel(String message, Type type) {
            this.message = message;
            this.type = (type == null) ? Type.INFO : type;

            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

            JLabel label = new JLabel(message);
            label.setFont(new Font("SansSerif", Font.BOLD, 13));
            label.setForeground(getTextColor());
            setLayout(new java.awt.BorderLayout());
            add(label, java.awt.BorderLayout.CENTER);
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension d = super.getPreferredSize();
            // un mínimo para que se vea “toast”
            return new Dimension(Math.max(d.width, 240), d.height);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int arc = 18;
                Color bg = getBgColor();

                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

                // borde suave
                Color border = JFramePrincipal.darkMode ? new Color(255, 255, 255, 45) : new Color(0, 0, 0, 45);
                g2.setColor(border);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);

            } finally {
                g2.dispose();
            }
            super.paintComponent(g);
        }

        private Color getBgColor() {
            boolean dark = JFramePrincipal.darkMode;
            return switch (type) {
                case SUCCESS -> dark ? new Color(30, 120, 70, 220) : new Color(45, 160, 95, 220);
                case WARNING -> dark ? new Color(150, 110, 20, 220) : new Color(230, 170, 35, 220);
                case ERROR   -> dark ? new Color(150, 45, 45, 220)  : new Color(210, 60, 60, 220);
                case INFO    -> dark ? new Color(45, 45, 45, 220)   : new Color(30, 30, 30, 220);
            };
        }

        private Color getTextColor() {
            // casi siempre blanco queda mejor en toast
            return new Color(245, 245, 245);
        }
    }
}
