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
import persistence.AppState;
import persistence.AppStateStore;
import persistence.AuthService;

public class JDialogLogin extends JDialog {
    private static final long serialVersionUID = 1L;
    private final JPanel contentPanel = new JPanel();

    private JTextField tfUsuario;
    private JPasswordField pfPassword;

    private User loggedUser = null;

    private final AppState state;
    private final AuthService auth;

    public JDialogLogin(java.awt.Frame parent) {
        super(parent, "Iniciar sesión", true);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        setSize(360, 220);
        setLocationRelativeTo(parent);

        this.state = AppStateStore.load();
        this.auth  = new AuthService(state);

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

        JButton btnRegister = new JButton("Registrarme");
        btnRegister.addActionListener((ActionEvent e) -> abrirRegistro());
        buttonPane.add(btnRegister);

        JButton okButton = new JButton("Entrar");
        okButton.addActionListener((ActionEvent e) -> intentarLogin());
        buttonPane.add(okButton);
        getRootPane().setDefaultButton(okButton);

        JButton cancelButton = new JButton("Cancelar");
        cancelButton.addActionListener((ActionEvent e) -> {
            loggedUser = null;
            User.setLoggedIn(null);
            dispose();
        });
        buttonPane.add(cancelButton);

        pfPassword.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) intentarLogin();
            }
        });
        tfUsuario.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) intentarLogin();
            }
        });
    }

    // --- Acciones ---

    private void abrirRegistro() {
        // owner de este JDialog es el mismo Frame que le pasaste al constructor
        java.awt.Window owner = getOwner();
        java.awt.Frame frameOwner = (owner instanceof java.awt.Frame) ? (java.awt.Frame) owner : null;

        // usa 'state' (sin guion bajo)
        JDialogRegistro reg = new JDialogRegistro(frameOwner, state);
        reg.setLocationRelativeTo(this);
        reg.setVisible(true);

        // Autocompletar usuario si se creó
        if (reg.getCreatedUser() != null && reg.getCreatedUser().getUsuario() != null) {
            tfUsuario.setText(reg.getCreatedUser().getUsuario());
            pfPassword.setText("");
            pfPassword.requestFocusInWindow();
        }
    }

    private void intentarLogin() {
        String username = tfUsuario.getText().trim();
        String password = new String(pfPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Introduce usuario y contraseña.", "Faltan datos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        auth.login(username, password).ifPresentOrElse(u -> {
            this.loggedUser = u;
            User.setLoggedIn(u);
            u.cargarReservas();
            u.verificarPenalizacion();
            dispose(); // <-- MUY IMPORTANTE: dejar que Main continúe
        }, () -> {
            JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos.", "No se pudo iniciar sesión", JOptionPane.ERROR_MESSAGE);
        });
    }
    // --- Getters públicos ---

    public User getLoggedUser() { return loggedUser; }

    // lo pasamos al JFramePrincipal para que pueda persistir otros cambios si hace falta
    public AppState getAppState() { return state; }
}


