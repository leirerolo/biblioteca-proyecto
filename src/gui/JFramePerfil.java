package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
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

import domain.Libro;
import domain.User;
import java.sql.SQLException;
import java.util.List;

public class JFramePerfil extends JFrame {
    private static final long serialVersionUID = 1L;

    private final User user;
    private JLabel lblNombre;
    private JLabel lblApellido;
    private JLabel lblEmail;
    private JLabel lblAvatar;

    //fuentes para la letra
	private Font fuenteMenu = new Font("Comic Sans MS", Font.BOLD, 15);
	
	// Colores
	private final Color COLOR_PRINCIPAL = new Color(90,170,255);
	private final Color COLOR_HOVER = new Color(60,140,230);
	
    public JFramePerfil(User user) {
        super("Mi perfil");
        this.user = user;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(360, 250);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBackground(Color.WHITE);
        
        JPanel header = new JPanel();
        header.setBackground(COLOR_PRINCIPAL);
        header.setBorder(
        		BorderFactory.createEmptyBorder(12,12,12,12)
        		);
        
        JLabel lblTitulo = new JLabel("Mi perfil");
        lblTitulo.setFont(new Font("Comic Sans MS", Font.BOLD,20));
        lblTitulo.setForeground(Color.WHITE);
        header.add(lblTitulo);
        
        root.add(header, BorderLayout.NORTH);

        // Panel datos
        JPanel datos = new JPanel(new GridLayout(3, 1, 10, 10));
        datos.setBackground(Color.WHITE);
        datos.setOpaque(true);
        lblNombre = new JLabel("Nombre: " + n(user.getNombre()));
        lblNombre.setFont(fuenteMenu);
        lblNombre.setBorder(
        		BorderFactory.createCompoundBorder(
        	        new MatteBorder(0,0,1,0,Color.GRAY),
        	        BorderFactory.createEmptyBorder(10, 10, 10, 10)
        		)
        	);
        lblApellido = new JLabel("Apellido: " + n(user.getApellido()));
        lblApellido.setFont(fuenteMenu);
        lblApellido.setBorder(
        	    BorderFactory.createCompoundBorder(
        	        new MatteBorder(0,0,1,0,Color.GRAY),
        	        BorderFactory.createEmptyBorder(10, 10, 10, 10)
        	    )
        	);
        lblEmail   = new JLabel("Email: "   + n(user.getEmail()));
        lblEmail.setFont(fuenteMenu);
        lblEmail.setBorder(
        	    BorderFactory.createEmptyBorder(10, 10, 10, 10)
        	);


        //datos.add(new JLabel("ID: " + user.getId()));
        datos.add(lblNombre);
        datos.add(lblApellido);
        datos.add(lblEmail);

        // Avatar
        lblAvatar = new JLabel("", SwingConstants.CENTER);
        
       JPanel avatarPanel = new JPanel(new BorderLayout());
       avatarPanel.setBackground(Color.WHITE);
       avatarPanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,10));
       avatarPanel.add(lblAvatar, BorderLayout.NORTH);
        //lblAvatar.setVerticalAlignment(SwingConstants.TOP);
        cargarAvatarEn(lblAvatar, user.getAvatarPath());

        
        // Botón Editar
        JButton btnEditar = new JButton("Editar");
        estilizarBoton(btnEditar);
        btnEditar.addActionListener(e -> editarPerfil());

        
        JButton btnFavoritos = new JButton("Mis favoritos");
        estilizarBoton(btnFavoritos);
        btnFavoritos.addActionListener(e -> {
            JFrameFavoritos favWin = new JFrameFavoritos(user);
            favWin.setVisible(true);
        });
        
        
        //Boton Log out
        
        JButton btnLogout = new JButton("Cerrar sesión");
        estilizarBoton(btnLogout);
        btnLogout.setBackground(new Color(220,53,69));
        
        btnLogout.addActionListener(e-> logout());
        
        JPanel right = new JPanel(new BorderLayout(8,8));
        right.setBackground(Color.WHITE);
        right.add(datos, BorderLayout.CENTER);

        root.add(avatarPanel, BorderLayout.WEST);
        root.add(right, BorderLayout.CENTER);
        
        
        JPanel panelBotones = new JPanel(new GridLayout(1,3,10,0));
        panelBotones.setBackground(Color.WHITE);
        panelBotones.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelBotones.add(btnEditar);
        panelBotones.add(btnFavoritos);
        panelBotones.add(btnLogout);
        
        root.add(panelBotones, BorderLayout.SOUTH);
        root.setBackground(Color.WHITE);
        root.setOpaque(true);
        setContentPane(root);
        pack();
        setMinimumSize(new Dimension(420, 420));
        setLocationRelativeTo(null);

    }
    
    
    private void logout() {
    	
    	int confirm = JOptionPane.showConfirmDialog(this,
    			"¿Estas seguro de que deseas cerrar sesion?",
    					"Cerrar sesion", JOptionPane.YES_NO_OPTION);
    	
    	if(confirm == JOptionPane.YES_OPTION) {
    		java.util.List<domain.Libro> lista = null;
    		
    		for (java.awt.Window w : java.awt.Window.getWindows()) {
    			if(w instanceof JFramePrincipal) {
    				lista= ((JFramePrincipal) w).getLibros();
    				break;
    			}
    		}
    		
    		domain.User.setLoggedIn(null);
    		
    		for(java.awt.Window window : java.awt.Window.getWindows()) {
    			window.dispose();
    		}
    		
    		JDialogLogin loginDlg = new JDialogLogin(null);
    		loginDlg.setVisible(true);
    		
    		if(loginDlg.getLoggedUser() != null && lista != null) {
    		
				new JFrameInicio(lista).setVisible(true);
    		}
    	}
    }
    
    
    private void editarPerfil() {
        JDialogEditarPerfil dlg = new JDialogEditarPerfil(this, user);
        dlg.setVisible(true);
        if (dlg.isAccepted()) {
        	try {
        		
            // Actualizar el modelo
        		user.setNombre(dlg.getNombre());
	            user.setApellido(dlg.getApellido());
	            user.setEmail(dlg.getEmail());
	            user.setAvatarPath(dlg.getSelectedAvatarPath());
	
	            boolean bdNombre = user.guardarCambiosNombre();
	            boolean bdApellido = user.guardarCambiosApellidoYNombre();
	            boolean bdEmailAvatar = user.guardarCambiosEmailYAvatar();
	
	            if (bdNombre && bdApellido && bdEmailAvatar) {
	                JOptionPane.showMessageDialog(this, "Perfil actualizado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
	                refrescarDatos();
	            } else {
	                 JOptionPane.showMessageDialog(this, "Error al guardar el perfil en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
	            }
	
	            
	        } catch (SQLException ex){
	            JOptionPane.showMessageDialog(this, 
	                "Error de conexión o de base de datos: " + ex.getMessage(), 
	                "Error de BD", JOptionPane.ERROR_MESSAGE);
	        }
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
    
    private void estilizarBoton(JButton btn) {
    	btn.setFocusPainted(false);
    	btn.setBackground(COLOR_PRINCIPAL);
    	btn.setForeground(Color.WHITE);
    	btn.setFont(fuenteMenu);
    	btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    	
    	btn.addMouseListener(new java.awt.event.MouseAdapter() {
    		public void mouseEntered(java.awt.event.MouseEvent e) {
    			btn.setBackground(COLOR_HOVER);
    		}
    		
    		
    		public void mouseExited(java.awt.event.MouseEvent e) {
    			btn.setBackground(COLOR_PRINCIPAL);
    		}
    	});
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

        g2.setClip(null);
        g2.setColor(new Color(30, 30, 30));
        g2.setStroke(new java.awt.BasicStroke(1.5f));
        g2.draw(new Ellipse2D.Float(0.75f, 0.75f, size - 1.5f, size - 1.5f));
        
        g2.dispose();
        
        
        return output;
    }


    private static String n(String s) {
        return (s == null || s.isEmpty()) ? "(sin rellenar)" : s;
    }
}
