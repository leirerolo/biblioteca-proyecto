package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import domain.User;
import persistence.AppState;
import persistence.AppStateStore;
import persistence.AuthService;

public class JDialogLogin extends JDialog {

    private static final long serialVersionUID = 1L;
    private final JPanel contentPanel = new JPanel();

    private JComboBox<String> cbUsuarios;
    private JPasswordField pfPassword;

    private User loggedUser = null;

    private final AppState state;
    private final AuthService auth;

    public JDialogLogin(java.awt.Frame parent) {
        super(parent, "Iniciar sesión", true);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        setSize(360, 211);
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

        // Campo usuario
        cbUsuarios = new JComboBox<>();
        cbUsuarios.setEditable(true);

        // Campo contraseña
        pfPassword = new JPasswordField();

        // Añadir campos con iconos
        contentPanel.add(crearCampoConIcono("user.png", cbUsuarios, 40));
        contentPanel.add(crearCampoConIcono("lock.png", pfPassword, 80));


        JTextField editorUsuario = (JTextField) cbUsuarios.getEditor().getEditorComponent();
        
        final boolean[] placeholderActivo = {true};
        
        editorUsuario.setText("Usuario...");
        editorUsuario.setForeground(Color.GRAY);
        
        cbUsuarios.setSelectedItem("");
        
        editorUsuario.addMouseListener(new MouseAdapter() { 
        	@Override 
        	public void mousePressed(MouseEvent e) { 
        		if (placeholderActivo[0]) { 
        			editorUsuario.setText(""); 
        			editorUsuario.setForeground(Color.BLACK); 
        			placeholderActivo[0] = false; 
        		} 
        	} 
        });
        

        editorUsuario.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (editorUsuario.getText().equals("Usuario...")) {
                    editorUsuario.setText("");
                    editorUsuario.setForeground(Color.BLACK);
                }
            }
        });

        
        editorUsuario.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (editorUsuario.getText().isEmpty()) {
                    editorUsuario.setText("Usuario...");
                    editorUsuario.setForeground(Color.GRAY);
                }
            }
        });



        pfPassword.setText("Contraseña...");
        pfPassword.setForeground(Color.GRAY);
        pfPassword.setEchoChar((char) 0);

        pfPassword.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (new String(pfPassword.getPassword()).equals("Contraseña...")) {
                    pfPassword.setText("");
                    pfPassword.setForeground(Color.BLACK);
                    pfPassword.setEchoChar('*');
                }
            }
        });

        pfPassword.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (new String(pfPassword.getPassword()).isEmpty()) {
                    pfPassword.setText("Contraseña...");
                    pfPassword.setForeground(Color.GRAY);
                    pfPassword.setEchoChar((char) 0);
                }
            }
        });

        // Cargar usuarios guardados
        if (state.getSavedCredentials().isEmpty()) {
            cbUsuarios.addItem("No hay usuarios guardados");
            cbUsuarios.setSelectedItem("");
        } 
        
        else {
            for (String u : state.getSavedCredentials().keySet()) {
                cbUsuarios.addItem(u);
            }
        }

        // Mostrar contraseña
        JCheckBox cbMostrarCont = new JCheckBox("Mostrar contraseña");
        cbMostrarCont.setBounds(62, 110, 240, 20);
        contentPanel.add(cbMostrarCont);

        
        cbMostrarCont.addActionListener(e -> {
            if (cbMostrarCont.isSelected()) {
                if (!new String(pfPassword.getPassword()).equals("Contraseña...")) {
                    pfPassword.setEchoChar((char) 0);
                }
            } else {
                if (!new String(pfPassword.getPassword()).equals("Contraseña...")) {
                    pfPassword.setEchoChar('*');
                }
            }
        });

        // Autocompletar contraseña
        cbUsuarios.addActionListener(e -> {
            String user = (String) cbUsuarios.getSelectedItem();
            if (user != null && state.getSavedCredentials().containsKey(user)) {
                pfPassword.setText(state.getSavedCredentials().get(user));
                pfPassword.setForeground(Color.BLACK);
                pfPassword.setEchoChar('*');
            }
        });

        //olvidar usuario
        JPopupMenu menuOlvidar = new JPopupMenu();
        JMenuItem itemOlvidar = new JMenuItem("Olvidar usuario");
        menuOlvidar.add(itemOlvidar);

        itemOlvidar.addActionListener(e -> {
            String user = (String) cbUsuarios.getSelectedItem();
            if (user == null || user.equals("No hay usuarios guardados")) return;

            state.removeCredential(user);
            AppStateStore.save(state);

            cbUsuarios.removeItem(user);

            if (cbUsuarios.getItemCount() == 0) {
                cbUsuarios.addItem("No hay usuarios guardados");
            }

            cbUsuarios.setSelectedItem("");
            pfPassword.setText("Contraseña...");
            pfPassword.setForeground(Color.GRAY);
            pfPassword.setEchoChar((char) 0);
        });

        MouseAdapter popupListener = new MouseAdapter() {
            private void mostrarMenu(MouseEvent e) {
                String user = (String) cbUsuarios.getSelectedItem();
                if (user == null || user.equals("No hay usuarios guardados")) return;
                menuOlvidar.show(e.getComponent(), e.getX(), e.getY());
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    mostrarMenu(e);
                }
            }
        };

        cbUsuarios.addMouseListener(popupListener);
        cbUsuarios.getEditor().getEditorComponent().addMouseListener(popupListener);

        //Botones inferior
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
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

        cbUsuarios.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) intentarLogin();
            }
        });
    }

    private JPanel crearCampoConIcono(String iconName, JComponent campo, int y) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panel.setBounds(20, y, 300, 28);

        String ruta = System.getProperty("user.dir") + "/icons/" + iconName;

        ImageIcon rawIcon = new ImageIcon(ruta);
        ImageIcon scaledIcon = new ImageIcon(rawIcon.getImage().getScaledInstance(24, 24, java.awt.Image.SCALE_SMOOTH));

        JLabel icono = new JLabel(scaledIcon);
        icono.setPreferredSize(new Dimension(24, 24));

        campo.setPreferredSize(new Dimension(240, 24));

        panel.add(icono);
        panel.add(campo);

        return panel;
    }

    private void abrirRegistro() {
        java.awt.Window owner = getOwner();
        java.awt.Frame frameOwner = (owner instanceof java.awt.Frame) ? (java.awt.Frame) owner : null;

        JDialogRegistro reg = new JDialogRegistro(frameOwner, state);
        reg.setLocationRelativeTo(this);
        reg.setVisible(true);

        if (reg.getCreatedUser() != null && reg.getCreatedUser().getUsuario() != null) {

            String nuevoUser = reg.getCreatedUser().getUsuario();

            if (cbUsuarios.getItemCount() == 1 &&
                cbUsuarios.getItemAt(0).equals("No hay usuarios guardados")) {
                cbUsuarios.removeAllItems();
            }

            boolean existe = false;
            for (int i = 0; i < cbUsuarios.getItemCount(); i++) {
                if (cbUsuarios.getItemAt(i).equals(nuevoUser)) {
                    existe = true;
                    break;
                }
            }
            if (!existe) cbUsuarios.addItem(nuevoUser);

            cbUsuarios.setSelectedItem("");
            pfPassword.setText("Contraseña...");
            pfPassword.setForeground(Color.GRAY);
            pfPassword.setEchoChar((char) 0);
            pfPassword.requestFocusInWindow();
        }
    }

    private void intentarLogin() {

        String rawUsername = (String) cbUsuarios.getEditor().getItem();
        String rawPassword = new String(pfPassword.getPassword());

        String username = (rawUsername == null || rawUsername.equals("Usuario...")) ? "" : rawUsername;
        String password = (rawPassword == null || rawPassword.equals("Contraseña...")) ? "" : rawPassword;

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Introduce usuario y contraseña.", "Faltan datos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        auth.login(username, password).ifPresentOrElse(u -> {

            this.loggedUser = u;
            User.setLoggedIn(u);
            u.cargarReservas();
            u.verificarPenalizacion();

            if (!state.getSavedCredentials().containsKey(username)) {

                int opcion = JOptionPane.showConfirmDialog(
                        this,
                        "¿Le gustaría recordar este usuario?",
                        "Recordar usuario",
                        JOptionPane.YES_NO_OPTION
                );

                if (opcion == JOptionPane.YES_OPTION) {

                    state.saveCredential(username, password);
                    AppStateStore.save(state);

                    boolean existe = false;
                    for (int i = 0; i < cbUsuarios.getItemCount(); i++) {
                        if (cbUsuarios.getItemAt(i).equals(username)) {
                            existe = true;
                            break;
                        }
                    }

                    if (cbUsuarios.getItemCount() == 1 &&
                        cbUsuarios.getItemAt(0).equals("No hay usuarios guardados")) {
                        cbUsuarios.removeAllItems();
                    }

                    if (!existe) {
                        cbUsuarios.addItem(username);
                    }
                }
            }

            dispose();

        }, () -> {
            JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos.", "No se pudo iniciar sesión", JOptionPane.ERROR_MESSAGE);
        });
    }

    public User getLoggedUser() { return loggedUser; }
    public AppState getAppState() { return state; }
}




