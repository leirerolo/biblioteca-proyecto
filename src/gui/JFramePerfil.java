// gui/JFramePerfil.java
package gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.io.File;
import javax.swing.*;

import domain.User;

public class JFramePerfil extends JFrame {
    private static final long serialVersionUID = 1L;

    private final User user;
    private JLabel lblNombre;
    private JLabel lblApellido;
    private JLabel lblEmail;
    private JLabel lblAvatar;

    public JFramePerfil(User user) {
        super("Mi perfil");
        this.user = user;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(460, 300);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout(12, 12));

        // Panel datos
        JPanel datos = new JPanel(new GridLayout(0, 1, 6, 6));
        lblNombre = new JLabel("Nombre: " + n(user.getNombre()));
        lblApellido = new JLabel("Apellido: " + n(user.getApellido()));
        lblEmail   = new JLabel("Email: "   + n(user.getEmail()));
        datos.add(new JLabel("ID: " + user.getID()));
        datos.add(lblNombre);
        datos.add(lblApellido);
        datos.add(lblEmail);

        // Avatar
        lblAvatar = new JLabel("", SwingConstants.CENTER);
        lblAvatar.setVerticalAlignment(SwingConstants.TOP);
        cargarAvatarEn(lblAvatar, user.getAvatarPath());

        // Botón Editar
        JButton btnEditar = new JButton("Editar");
        btnEditar.addActionListener(e -> editarPerfil());

        JPanel right = new JPanel(new BorderLayout(8,8));
        right.add(datos, BorderLayout.CENTER);
        right.add(btnEditar, BorderLayout.SOUTH);

        root.add(lblAvatar, BorderLayout.WEST);
        root.add(right, BorderLayout.CENTER);

        setContentPane(root);
    }

    private void editarPerfil() {
        JDialogEditarPerfil dlg = new JDialogEditarPerfil(this, user);
        dlg.setVisible(true);
        if (dlg.isAccepted()) {
            // Actualizar el modelo
            user.setApellido(dlg.getApellido());
            user.setEmail(dlg.getEmail());
            user.setAvatarPath(dlg.getSelectedAvatarPath());

            // Refrescar UI
            refrescarDatos();
        }
    }

    private void refrescarDatos() {
        lblNombre.setText("Nombre: " + n(user.getNombre()));
        lblApellido.setText("Apellido: " + n(user.getApellido()));
        lblEmail.setText("Email: " + n(user.getEmail()));
        cargarAvatarEn(lblAvatar, user.getAvatarPath());
    }

    private void cargarAvatarEn(JLabel target, String avatarPath) {
        // 1) Si el usuario ya tiene ruta guardada, úsala
        if (avatarPath != null && !avatarPath.isEmpty()) {
            File f = new File(avatarPath);
            if (f.exists()) {
                ImageIcon icon = new ImageIcon(avatarPath);
                Image scaled = icon.getImage().getScaledInstance(110, 110, Image.SCALE_SMOOTH);
                target.setIcon(new ImageIcon(scaled));
                target.setText("");
                return;
            }
        }

        // 2) Buscar en la carpeta "user avatar" del proyecto
        File baseDir = new File("user avatar"); // relativo al directorio de ejecución
        // Ejemplo: intenta "<user avatar>/<nombre>.png", si no, "avatar.png"
        File candidate = (user.getNombre() != null && !user.getNombre().isEmpty())
                ? new File(baseDir, user.getNombre() + ".png")
                : null;

        if (candidate != null && candidate.exists()) {
            ImageIcon icon = new ImageIcon(candidate.getAbsolutePath());
            Image scaled = icon.getImage().getScaledInstance(110, 110, Image.SCALE_SMOOTH);
            target.setIcon(new ImageIcon(scaled));
            target.setText("");
            return;
        }

        File fallback = new File(baseDir, "avatar.png");
        if (fallback.exists()) {
            ImageIcon icon = new ImageIcon(fallback.getAbsolutePath());
            Image scaled = icon.getImage().getScaledInstance(110, 110, Image.SCALE_SMOOTH);
            target.setIcon(new ImageIcon(scaled));
            target.setText("");
            return;
        }

        // 3) Último recurso: icono textual
        target.setIcon(null);
        target.setText("👤");
    }


    private static String n(String s) {
        return (s == null || s.isEmpty()) ? "(sin rellenar)" : s;
    }
}
