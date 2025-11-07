package gui;


import javax.swing.*;
import java.awt.*;


import domain.User;
import persistence.AppState;
import persistence.AuthService;


public class JDialogRegistro extends JDialog {
	private static final long serialVersionUID = 1L;

	private final JTextField tfUsuario = new JTextField();
	private final JTextField tfEmail = new JTextField();
	private final JTextField tfNombre = new JTextField();
	private final JTextField tfApellido= new JTextField();
	private final JPasswordField pfPass= new JPasswordField();

	private User createdUser; // null si cancelado


	public JDialogRegistro(Frame parent, AppState state) {
		super(parent, "Crear cuenta", true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(380, 320);
		setLocationRelativeTo(parent);

		AuthService auth = new AuthService(state);


		JPanel form = new JPanel(new GridLayout(0,2,8,8));
		form.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
		form.add(new JLabel("Usuario:")); form.add(tfUsuario);
		form.add(new JLabel("Email:")); form.add(tfEmail);
		form.add(new JLabel("Nombre:")); form.add(tfNombre);
		form.add(new JLabel("Apellido:")); form.add(tfApellido);
		form.add(new JLabel("Contraseña:")); form.add(pfPass);


		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton btnCancel = new JButton("Cancelar");
		JButton btnOk = new JButton("Crear cuenta");
		buttons.add(btnCancel); buttons.add(btnOk);
	

		getContentPane().add(form, BorderLayout.CENTER);
		getContentPane().add(buttons, BorderLayout.SOUTH);


		btnCancel.addActionListener(e -> { createdUser = null; dispose(); });
		btnOk.addActionListener(e -> {
			try {
				String usuario = tfUsuario.getText().trim();
				String email = tfEmail.getText().trim();
				String nombre = tfNombre.getText().trim();
				String apellido = tfApellido.getText().trim();
				String pass = new String(pfPass.getPassword());
				if (usuario.isEmpty() || email.isEmpty() || pass.isEmpty()) {
					JOptionPane.showMessageDialog(this, "Usuario, email y contraseña son obligatorios", "Campos requeridos", JOptionPane.WARNING_MESSAGE);
					return;
				}
				createdUser = auth.register(usuario, email, nombre, apellido, pass);
				JOptionPane.showMessageDialog(this, "Cuenta creada para '"+usuario+"'", "OK", JOptionPane.INFORMATION_MESSAGE);
				dispose();
			} catch (IllegalArgumentException ex) {
				JOptionPane.showMessageDialog(this, ex.getMessage(), "No se pudo registrar", JOptionPane.ERROR_MESSAGE);
			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(this, "Error inesperado: "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		});
	}


	public User getCreatedUser() { return createdUser; }
}