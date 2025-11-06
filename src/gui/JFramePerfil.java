package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.*;
import javax.swing.border.MatteBorder;

import domain.User;

public class JFramePerfil extends JFrame {
    private static final long serialVersionUID = 1L;

    private final User user;
    private JLabel lblNombre;
    private JLabel lblApellido;
    private JLabel lblEmail;
    private JLabel lblAvatar;

    //fuentes para la letra
	private Font fuenteMenu = new Font("Comic Sans MS", Font.BOLD, 15);
	
    public JFramePerfil(User user) {
        super("Mi perfil");
        this.user = user;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(360, 250);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout(12, 12));

        // Panel datos
        JPanel datos = new JPanel(new GridLayout(3, 1, 10, 10));
        datos.setBackground(Color.WHITE);
        datos.setOpaque(true);
        lblNombre = new JLabel("Nombre: " + n(user.getNombre()));
        lblNombre.setFont(fuenteMenu);
    	lblNombre.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    	lblNombre.setBorder(new MatteBorder(0,0,1,0,Color.GRAY));
        lblApellido = new JLabel("Apellido: " + n(user.getApellido()));
        lblApellido.setFont(fuenteMenu);
    	lblApellido.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    	lblApellido.setBorder(new MatteBorder(0,0,1,0,Color.GRAY));
        lblEmail   = new JLabel("Email: "   + n(user.getEmail()));
        lblEmail.setFont(fuenteMenu);
    	lblEmail.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        //datos.add(new JLabel("ID: " + user.getId()));
        datos.add(lblNombre);
        datos.add(lblApellido);
        datos.add(lblEmail);

        // Avatar
        lblAvatar = new JLabel("", SwingConstants.CENTER);
        //lblAvatar.setVerticalAlignment(SwingConstants.TOP);
        cargarAvatarEn(lblAvatar, user.getAvatarPath());

        // Botón Editar
        JButton btnEditar = new JButton("Editar");
        btnEditar.addActionListener(e -> editarPerfil());

        JPanel right = new JPanel(new BorderLayout(8,8));
        right.add(datos, BorderLayout.CENTER);

        root.add(lblAvatar, BorderLayout.WEST);
        root.add(right, BorderLayout.CENTER);
        root.add(btnEditar, BorderLayout.SOUTH);
        root.setBackground(Color.WHITE);
        root.setOpaque(true);
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
    	Image img = null;
    	
        // 1) Si el usuario ya tiene ruta guardada, la usamos
        if (avatarPath != null && !avatarPath.isEmpty()) {
            File f = new File(avatarPath);
            if (f.exists()) {
            	img = new ImageIcon(avatarPath).getImage();
            }
        }

        // 2) Buscar en la carpeta "user avatar" del proyecto
        if (img == null) {
        	File baseDir = new File("user avatar"); // relativo al directorio de ejecución
	        // Ejemplo: intentamos "<user avatar>/<nombre>.png", si no, "avatar.png"
	        File candidate = (user.getNombre() != null && !user.getNombre().isEmpty())
	                ? new File(baseDir, user.getNombre() + ".png")
	                : null;
	        
	        if (candidate != null && candidate.exists()) {
	        	img = new ImageIcon(candidate.getAbsolutePath()).getImage();
	        } else {
	        	File fallback = new File(baseDir, "avatar.png");
	        	if (fallback.exists()) {
	        		img = new ImageIcon(fallback.getAbsolutePath()).getImage();
	        	}
	        }
        }
        
        // 3) Si hay imagen, la hacemos circular
        //AYUDA DE CHAT GPT PARA CONSEGUIR LA IMAGEN EN FORMA CIRCULAR
        if (img!=null) {
        	Image scaled = img.getScaledInstance(110, 110, Image.SCALE_SMOOTH);
        	ImageIcon circularIcon = new ImageIcon(crearImagenCircular(scaled));
        	target.setIcon(circularIcon);
        	target.setText("");
        	return;
        }
        // 4) Último recurso: icono textual
        target.setIcon(null);
        target.setText("👤");
    }

    //USO DE CHAT GPT PARA EL MÉTODO
    private Image crearImagenCircular(Image img) {
        // Asegurar que la imagen esté completamente cargada
        ImageIcon icon = new ImageIcon(img);
        int width = icon.getIconWidth();
        int height = icon.getIconHeight();

        // Evitar valores inválidos
        if (width <= 0 || height <= 0) {
            width = height = 110; // tamaño por defecto
        }

        int size = Math.min(width, height);
        BufferedImage output = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = output.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Círculo de recorte
        g2.setClip(new java.awt.geom.Ellipse2D.Float(0, 0, size, size));

        // Dibujar imagen recortada
        g2.drawImage(img, 0, 0, size, size, null);
        g2.dispose();

        return output;
    }


    private static String n(String s) {
        return (s == null || s.isEmpty()) ? "(sin rellenar)" : s;
    }
}
