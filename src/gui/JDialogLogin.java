package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import domain.User;

public class JDialogLogin extends JDialog {
    private static final long serialVersionUID = 1L;
    private final JPanel contentPanel = new JPanel();
    private JTextField tfUsuario;
    private JPasswordField pfPassword;
    private User loggedUser = null;

    public JDialogLogin(java.awt.Frame parent) {
        super(parent, "Iniciar sesión", true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);
        setSize(360, 200);
        setLocationRelativeTo(parent);

        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(14, 14, 14, 14));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(null);

        JLabel lblTitulo = new JLabel("Acceso a la biblioteca");
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitulo.setBounds(10, 0, 324, 28);
        contentPanel.add(lblTitulo);

        JLabel lblUsuario = new JLabel("Usuario:");
        lblUsuario.setBounds(10, 44, 100, 16);
        contentPanel.add(lblUsuario);

        tfUsuario = new JTextField();
        tfUsuario.setBounds(120, 40, 214, 24);
        contentPanel.add(tfUsuario);

        JLabel lblPass = new JLabel("Contraseña:");
        lblPass.setBounds(10, 78, 100, 16);
        contentPanel.add(lblPass);

        pfPassword = new JPasswordField();
        pfPassword.setBounds(120, 74, 214, 24);
        contentPanel.add(pfPassword);

        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        getContentPane().add(buttonPane, BorderLayout.SOUTH);

        JButton okButton = new JButton("Entrar");
        okButton.addActionListener((ActionEvent e) -> intentarLogin());
        buttonPane.add(okButton);
        getRootPane().setDefaultButton(okButton);

        JButton cancelButton = new JButton("Cancelar");
        cancelButton.addActionListener((ActionEvent e) -> {
            loggedUser = null;
            // (opcional) limpia sesión global si la hubiera
            User.setLoggedIn(null);
            dispose();
        });
        buttonPane.add(cancelButton);

        // Enter en password para aceptar
        pfPassword.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) intentarLogin();
            }
        });
        // (opcional) Enter en usuario también
        tfUsuario.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) intentarLogin();
            }
        });
    }

    private void intentarLogin() {
        String username = tfUsuario.getText().trim();
        String password = new String(pfPassword.getPassword());
        if (!username.isEmpty() && !password.isEmpty()) {
            // Demo: acepta cualquier usuario no vacío
            loggedUser = new User(username, ""); // usa tu constructor existente sin ID

            // >>> CLAVE: fija la sesión global para que Perfil/Explorador/Reservas la lean
            User.setLoggedIn(loggedUser);
            loggedUser.cargarReservasCSV(); // cargo las reservas que puede tener de antes

            dispose();
        } else {
            JOptionPane.showMessageDialog(
                this,
                "Introduce usuario y contraseña.",
                "Faltan datos",
                JOptionPane.WARNING_MESSAGE
            );
        }
    }

    public User getLoggedUser() { return loggedUser; }
}

