package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import domain.User;

public class JFramePerfil extends JFrame {

	private static final long serialVersionUID = 1L;
	private final User user;
	
	public JFramePerfil(User user) {
		super("Perfil de usuario");
		this.user = user;
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(420, 260);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());
		
		JPanel content = new JPanel(new BorderLayout());
		content.setBorder(new EmptyBorder(16,16,16,16));
		add(content, BorderLayout.CENTER);
		
		// Foto
		JLabel lblFoto = new JLabel();
		lblFoto.setPreferredSize(new Dimension(140, 140));
		lblFoto.setHorizontalAlignment(JLabel.CENTER);
		ImageIcon icon = loadProfileImage(user != null ? user.getFotoPath() : null);
		if (icon != null) {
			Image scaled = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
			lblFoto.setIcon(new ImageIcon(scaled));
		} else {
			lblFoto.setText("Sin foto");
		}
		content.add(lblFoto, BorderLayout.WEST);
		
		// Datos
		JPanel datos = new JPanel();
		datos.setLayout(new javax.swing.BoxLayout(datos, javax.swing.BoxLayout.Y_AXIS));
		datos.setBorder(new EmptyBorder(0,16,0,0));
		datos.add(new JLabel("Nombre: " + safe(user != null ? user.getNombre() : "")));
		datos.add(new JLabel("Apellido: " + safe(user != null ? user.getApellido() : "")));
		datos.add(new JLabel("Email: " + safe(user != null ? user.getEmail() : "")));
		content.add(datos, BorderLayout.CENTER);
		
		// Botonera
		JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton btnCerrar = new JButton("Cerrar");
		btnCerrar.addActionListener(e -> dispose());
		south.add(btnCerrar);
		add(south, BorderLayout.SOUTH);
	}
	
	private String safe(String s) { return s == null ? "" : s; }
	
	private ImageIcon loadProfileImage(String path) {
		try {
			if (path != null && !path.trim().isEmpty()) {
				return new ImageIcon(path);
			}
		} catch (Exception ignored) {}
		return null;
	}
}