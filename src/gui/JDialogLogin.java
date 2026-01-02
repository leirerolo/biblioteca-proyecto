package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
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

        cbUsuarios = new JComboBox<>(); 
        cbUsuarios.setEditable(true); 
        cbUsuarios.setBounds(120, 40, 214, 24); 
        contentPanel.add(cbUsuarios);
        
        if (state.getSavedCredentials().isEmpty()) { 
        	cbUsuarios.addItem("No hay usuarios guardados"); 
        	cbUsuarios.setSelectedItem(""); 
        } else { 
        	for (String u : state.getSavedCredentials().keySet()) { 
        		cbUsuarios.addItem(u); 
        	}
        }
        
        JPopupMenu menuOlvidar = new JPopupMenu();
        JMenuItem itemOlvidar = new JMenuItem("Olvidar usuario");
        menuOlvidar.add(itemOlvidar);

        JLabel lblPass = new JLabel("Contraseña:");
        lblPass.setBounds(10, 78, 100, 16);
        contentPanel.add(lblPass);

        pfPassword = new JPasswordField();
        pfPassword.setBounds(120, 74, 214, 24);
        contentPanel.add(pfPassword);
        
        JCheckBox cbMostrarCont = new JCheckBox("Mostrar contraseña");
        cbMostrarCont.setBounds(120,102,214,20);
        contentPanel.add(cbMostrarCont);
        
        //mostrar contraseña
        cbMostrarCont.addActionListener(e -> {
        	if (cbMostrarCont.isSelected()) { 
        		pfPassword.setEchoChar((char) 0); 
        	}else { 
        		pfPassword.setEchoChar('*');
        	}
        });
        
        //autocompletado usuario/contraseña
        cbUsuarios.addActionListener(e -> { 
        	String user = (String) cbUsuarios.getSelectedItem(); 
        	if (user != null && state.getSavedCredentials().containsKey(user)) { 
        		pfPassword.setText(state.getSavedCredentials().get(user)); 
        	} 
        });
        
        
        //olvidar usuario
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
            pfPassword.setText("");
        });
        
     // Listener para menú contextual "Olvidar usuario"
        MouseAdapter popupListener = new MouseAdapter() {

            private void mostrarMenu(MouseEvent e) {
                String user = (String) cbUsuarios.getSelectedItem();
                if (user == null || user.equals("No hay usuarios guardados")) return;

                // Mostrar el menú sobre el componente donde se hizo clic
                menuOlvidar.show(e.getComponent(), e.getX(), e.getY());
            }

            @Override
            public void mousePressed(MouseEvent e) {
                // Usamos botón derecho directamente, sin depender de isPopupTrigger
                if (SwingUtilities.isRightMouseButton(e)) {
                    mostrarMenu(e);
                }
            }
        };

        // Añadir el listener tanto al combo como al editor
        cbUsuarios.addMouseListener(popupListener);
        cbUsuarios.getEditor().getEditorComponent().addMouseListener(popupListener);



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
        cbUsuarios.addKeyListener(new KeyAdapter() {
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

            pfPassword.setText("");
            pfPassword.requestFocusInWindow();
        }
    }

    private void intentarLogin() {
    	String username = (String) cbUsuarios.getEditor().getItem();
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

            if (!state.getSavedCredentials().containsKey(username)) {

                int opcion = JOptionPane.showConfirmDialog(
                        this,
                        "¿Le gustaría recordar este usuario?",
                        "Recordar usuario",
                        JOptionPane.YES_NO_OPTION
                );

                if (opcion == JOptionPane.YES_OPTION) {

                    // Guardar credenciales
                    state.saveCredential(username, password);
                    AppStateStore.save(state);

                    // Añadir al combo si no estaba
                    boolean existe = false;
                    for (int i = 0; i < cbUsuarios.getItemCount(); i++) {
                        if (cbUsuarios.getItemAt(i).equals(username)) {
                            existe = true;
                            break;
                        }
                    }

                    // Si antes solo estaba "No hay usuarios guardados", lo quitamos
                    if (cbUsuarios.getItemCount() == 1 &&
                        cbUsuarios.getItemAt(0).equals("No hay usuarios guardados")) {
                        cbUsuarios.removeAllItems();
                    }

                    if (!existe) {
                        cbUsuarios.addItem(username);
                    }
                }
            }

            dispose(); // continuar a la app

        }, () -> {
            JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos.", "No se pudo iniciar sesión", JOptionPane.ERROR_MESSAGE);
        });

    }
    // --- Getters públicos ---

    public User getLoggedUser() { return loggedUser; }

    // lo pasamos al JFramePrincipal para que pueda persistir otros cambios si hace falta
    public AppState getAppState() { return state; }
}


