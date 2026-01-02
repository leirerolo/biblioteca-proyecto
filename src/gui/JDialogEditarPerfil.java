// gui/JDialogEditarPerfil.java
package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import domain.User;

public class JDialogEditarPerfil extends JDialog {
    private static final long serialVersionUID = 1L;

    private final JPanel content = new JPanel();
    private JTextField tfNombre;
    private JTextField tfApellido;
    private JTextField tfEmail;
    private JLabel lblAvatarPreview;
    private String selectedAvatarPath; // ruta elegida
    private boolean accepted = false;

    public JDialogEditarPerfil(JFrame parent, User user) {
        super(parent, "Editar perfil", true);
        setSize(420, 280);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        getContentPane().setLayout(new BorderLayout());
        content.setBorder(new EmptyBorder(12,12,12,12));
        content.setLayout(null);
        
        JLabel l0 = new JLabel("Nombre:"); 
        l0.setBounds(10, 10, 120, 24); 
        content.add(l0); 
        
        tfNombre = new JTextField(user.getNombre() != null ? user.getNombre() : ""); 
        tfNombre.setBounds(140, 10, 250, 28); 
        content.add(tfNombre);

        JLabel l1 = new JLabel("Apellido:");
        l1.setBounds(10, 46, 120, 24);
        content.add(l1);

        tfApellido = new JTextField(user.getApellido() != null ? user.getApellido() : "");
        tfApellido.setBounds(140, 46, 250, 28);
        content.add(tfApellido);

        JLabel l2 = new JLabel("Email:");
        l2.setBounds(10, 82, 120, 24);
        content.add(l2);

        tfEmail = new JTextField(user.getEmail() != null ? user.getEmail() : "");
        tfEmail.setBounds(140, 82, 250, 28);
        content.add(tfEmail);

        JLabel l3 = new JLabel("Avatar:");
        l3.setBounds(10, 118, 120, 24);
        content.add(l3);

        JButton btnElegir = new JButton("Elegir imagen...");
        btnElegir.setBounds(140, 118, 150, 24);
        btnElegir.addActionListener((ActionEvent e) -> elegirAvatar());
        content.add(btnElegir);

        lblAvatarPreview = new JLabel("Sin imagen", SwingConstants.CENTER);
        lblAvatarPreview.setBounds(300, 118, 90, 90);
        content.add(lblAvatarPreview);

        // Carga previa del avatar
        selectedAvatarPath = user.getAvatarPath();
        actualizarPreview(selectedAvatarPath);

        getContentPane().add(content, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton ok = new JButton("Guardar");
        ok.addActionListener((ActionEvent e) -> {
            accepted = true;
            dispose();
        });
        JButton cancel = new JButton("Cancelar");
        cancel.addActionListener((ActionEvent e) -> dispose());
        btns.add(ok);
        btns.add(cancel);
        getContentPane().add(btns, BorderLayout.SOUTH);
    }

    private void elegirAvatar() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Elige una imagen de avatar");
        int res = chooser.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            if (f != null && f.exists()) {
                selectedAvatarPath = f.getAbsolutePath(); // guardamos ruta directa
                actualizarPreview(selectedAvatarPath);
            }
        }
    }

    private void actualizarPreview(String path) {
        try {
            if (path != null && !path.isEmpty()) {
                ImageIcon icon = new ImageIcon(path);
                // ajuste rápido de tamaño de preview
                java.awt.Image scaled = icon.getImage().getScaledInstance(90, 90, java.awt.Image.SCALE_SMOOTH);
                lblAvatarPreview.setIcon(new ImageIcon(scaled));
                lblAvatarPreview.setText("");
            } else {
                lblAvatarPreview.setIcon(null);
                lblAvatarPreview.setText("Sin imagen");
            }
        } catch (Exception ex) {
            lblAvatarPreview.setIcon(null);
            lblAvatarPreview.setText("Sin imagen");
        }
    }

    public boolean isAccepted() { return accepted; }
    public String getNombre() { return tfNombre.getText().trim(); }
    public String getApellido() { return tfApellido.getText().trim(); }
    public String getEmail() { return tfEmail.getText().trim(); }
    public String getSelectedAvatarPath() { return selectedAvatarPath; }
}
