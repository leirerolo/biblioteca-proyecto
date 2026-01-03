package theme;

import java.awt.*;

import javax.swing.JButton;

public class DarkMode {

    // Colores del modo oscuro
    public static final Color BACKGROUND = new Color(18, 18, 18);
    public static final Color FOREGROUND = new Color(230, 230, 230);
    public static final Color BUTTON_BG = new Color(40, 40, 40);
    public static final Color BUTTON_FG = new Color(220, 220, 220);

    // Método para aplicar el tema oscuro
    public static void apply(Component comp) {
        if (comp instanceof JButton || comp instanceof javax.swing.JToggleButton) {
            comp.setBackground(BUTTON_BG);
            comp.setForeground(BUTTON_FG);
        } else {
            comp.setBackground(BACKGROUND);
            comp.setForeground(FOREGROUND);
        }

        if (comp instanceof Container) {
            for (Component child : ((Container) comp).getComponents()) {
                apply(child);
            }
        }
    }
}
