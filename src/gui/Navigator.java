package gui;

import java.awt.Window;

public class Navigator {

    /** Cierra la ventana actual y abre el JFrameInicio. */
    public static void irAInicio(Window ventanaActual) {
        if (ventanaActual != null) {
            ventanaActual.dispose();
        }
        JFrameInicio.open();
    }
}
