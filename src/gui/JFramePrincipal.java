package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.MatteBorder;
import javax.swing.table.JTableHeader;

import db.LibroDAO;
import domain.*;
import domain.User.Rol;
import persistence.AppState;
import persistence.AppStateStore;

import theme.DarkMode;


public class JFramePrincipal extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private final AppState state;
	
	private static final Preferences prefs = Preferences.userRoot().node("biblioteca");
	protected static boolean darkMode = prefs.getBoolean("darkMode", false);
	
	protected JPanel headerPanel;
	protected JPanel menuPanel;
	protected JLabel lblInicio; 
	protected JLabel lblExplorar; 
	protected JLabel lblReservas; 
	protected JLabel lblPerfil; 
	protected JLabel lblToggleDark;
	protected JLabel lblBiblio;

	private User currentUser;
	protected List<Libro> libros;
	
	//predefinir las fuentes
	protected Font fuenteTitulo = new Font("Comic Sans MS", Font.BOLD, 22);
	protected Font fuenteMenu = new Font("Comic Sans MS", Font.BOLD, 18);
	
	private String ventanaActiva;
	
	private DefaultListModel<String> modeloReservas;
	
	//para el admin
	private final LibroDAO libroDAO = new LibroDAO();
	
	public JFramePrincipal(List<Libro> libros, String ventanaActiva, AppState state) {
        this.libros = (libros != null) ? libros : new ArrayList<>();
        this.ventanaActiva = ventanaActiva;
        this.state = (state != null) ? state : AppStateStore.load();

        if (this.currentUser == null) {
            this.currentUser = domain.User.getLoggedIn();
        }
        if (this.currentUser != null) {
            this.currentUser.cargarReservas();
        }

        this.setTitle("Biblioteca");
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setSize(600, 800);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        
        this.inicializarPanelSuperior();
        this.aplicarTema();
        
        //evento de teclado, exit only tras clickar control + e
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_E && e.isControlDown()) {
                    int opcion = JOptionPane.showConfirmDialog(
                        JFramePrincipal.this,
                        "¿Seguro que quieres salir?",
                        "Confirmar salida",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                    );
                    if (opcion == JOptionPane.YES_OPTION) {
                        System.exit(0);
                    }
                }
            }
        });
        
        this.setFocusable(true);
        this.requestFocusInWindow();
        
        
    }
	public JFramePrincipal(List<Libro> libros, String ventanaActiva) {
	    this(libros, ventanaActiva, null); // carga AppState desde disco si viene null
	}
	
	public void setCurrentUser(User u) {
        this.currentUser = u;
    }
	public List<Libro> getLibros() { return libros; }
	public AppState getAppState() { return state; }
	
	protected void inicializarPanelSuperior() {		
		JPanel upperPanel = new JPanel(new BorderLayout());
		
		// Cabecera: nombre de la biblioteca, y botón para el perfil
		headerPanel  = new JPanel(new BorderLayout());
		headerPanel .setBackground(new Color(0, 160, 220));
		
		lblBiblio = new JLabel("Biblio.O");
		lblBiblio.setFont(fuenteTitulo);
		lblBiblio.setForeground(Color.WHITE);
		headerPanel.add(lblBiblio, BorderLayout.WEST);

		
		lblPerfil = new JLabel("Mi perfil");
		lblPerfil.setFont(fuenteTitulo);
		lblPerfil.setForeground(Color.white);
		headerPanel.add(lblPerfil, BorderLayout.EAST);


		// boton claro / oscuro
		lblToggleDark = new JLabel(darkMode ? "☀️" : "🌙", JLabel.CENTER);
		lblToggleDark.setFont(new Font("SansSerif", Font.PLAIN, 22));
		lblToggleDark.setForeground(Color.WHITE);
		lblToggleDark.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		headerPanel.add(lblToggleDark , BorderLayout.CENTER);

		lblToggleDark.addMouseListener(new MouseAdapter() {
		    @Override
		    public void mouseClicked(java.awt.event.MouseEvent e) {
		        darkMode = !darkMode;
		        prefs.putBoolean("darkMode", darkMode);

		        lblToggleDark.setText(darkMode ? "☀️" : "🌙");
		        Navigator.applyThemeAll();
		    }
		});
		


		//headerPanel.add(lblPerfil, BorderLayout.EAST);
		
		lblPerfil.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lblPerfil.addMouseListener(new MouseAdapter() {
			 @Override public void mouseClicked(java.awt.event.MouseEvent e) {
	                User u = (currentUser != null) ? currentUser : User.getLoggedIn();
	                if (u == null) {
	                    JOptionPane.showMessageDialog(
	                            JFramePrincipal.this,
	                            "Inicia sesión para ver tu perfil.",
	                            "Perfil",
	                            JOptionPane.INFORMATION_MESSAGE
	                    );
	                    return;
	                }
	                JFramePerfil perfilWin = new JFramePerfil(u);
	                perfilWin.setVisible(true);
	            }
	        });
        upperPanel.add(headerPanel , BorderLayout.NORTH);

		// Menú de navegación: inicio, explorar, reservas
        menuPanel = new JPanel(new GridLayout(1,3));
        menuPanel.setBackground(Color.white);
		
        lblInicio = new JLabel("Inicio", JLabel.CENTER);
        lblInicio.setFont(fuenteMenu);
        menuPanel.add(lblInicio);

        lblExplorar = new JLabel("Explorar", JLabel.CENTER); 
        lblExplorar.setFont(fuenteMenu); 
        menuPanel.add(lblExplorar);
		
        lblReservas = new JLabel("Reservas", JLabel.CENTER); 
        lblReservas.setFont(fuenteMenu); 
        menuPanel.add(lblReservas);
		
		upperPanel.add(menuPanel, BorderLayout.CENTER);
		upperPanel.setBorder(new MatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
		add(upperPanel, BorderLayout.NORTH);
		
		
		// ************** CLICK EN "labels de menu" *****************
		
	    //cambia color segun la ventana activa
		if ("inicio".equalsIgnoreCase(ventanaActiva)) { 
			estaActivo(lblInicio, lblExplorar, lblReservas); 
		} else if ("explorar".equalsIgnoreCase(ventanaActiva)) { 
			estaActivo(lblExplorar, lblInicio, lblReservas); 
		} else if ("reservas".equalsIgnoreCase(ventanaActiva)) { 
			estaActivo(lblReservas, lblInicio, lblExplorar); 
		}
	    
	 // Navegación (mantengo tus Navigator + dispose)
		lblInicio.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (!"inicio".equalsIgnoreCase(ventanaActiva)) {
                    Navigator.showInicio();
                    JFramePrincipal.this.dispose();
                }
            }
        });

		lblExplorar.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (!"explorar".equalsIgnoreCase(ventanaActiva)) {
                    Navigator.showExplorar();
                    JFramePrincipal.this.dispose();
                }
            }
        });

		lblReservas.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (!"reservas".equalsIgnoreCase(ventanaActiva)) {
                    Navigator.showReservas();
                    JFramePrincipal.this.dispose();
                }
            }
        });
    }

	// ================== CONTENIDO CENTRAL ==================

    protected void renderInicioTop6(JPanel mainPanel) {
    	mainPanel.setLayout(new BorderLayout());
    	mainPanel.setOpaque(true);
    	mainPanel.setBackground(
    	    darkMode ? new Color(30,30,30) : new Color(245,245,245)
    	);
        if (libros == null || libros.isEmpty()) {
            JLabel l = new JLabel("No hay libros para mostrar", JLabel.CENTER);
            l.setFont(new Font("SansSerif", Font.PLAIN, 16));
            mainPanel.add(l, BorderLayout.CENTER);
            return;
        }

        // Top 6 por valoración (desc) – compatible con JDK 8+
        List<Libro> top6 = libros.stream()
                .sorted((a, b) -> Double.compare(b.getValoracion(), a.getValoracion()))
                .limit(6)
                .collect(Collectors.toList());

        JPanel grid = new JPanel(new GridLayout(0, 3, 16, 16));
        grid.setOpaque(true);

        for (Libro lib : top6) grid.add(buildBookCard(lib));

        JLabel titulo = new JLabel("Mejor valorados", JLabel.LEFT);
        titulo.setFont(new Font("Comic Sans MS", Font.BOLD, 20));

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(true);
        wrapper.add(titulo, BorderLayout.NORTH);
        wrapper.add(grid, BorderLayout.CENTER);

        mainPanel.add(new JScrollPane(wrapper), BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private JPanel buildBookCard(Libro lib) {
        JPanel card = new JPanel(new BorderLayout());
        card.setOpaque(true);
        card.setBorder(new MatteBorder(1, 1, 1, 1, new Color(230, 230, 230)));

        // Portada
        JLabel img = new JLabel();
        img.setHorizontalAlignment(JLabel.CENTER);
        if (lib.getPortada() != null) img.setIcon(lib.getPortada());
        card.add(img, BorderLayout.CENTER);

        // Texto (título + autor + valoración)
        JPanel info = new JPanel(new GridLayout(0, 1));
        JLabel t = new JLabel(lib.getTitulo());
        t.setFont(new Font("SansSerif", Font.BOLD, 12));
        JLabel a = new JLabel(lib.getAutor());
        a.setFont(new Font("SansSerif", Font.PLAIN, 11));
        JLabel r = new JLabel("★ " + String.format("%.1f", lib.getValoracion()));
        r.setFont(new Font("SansSerif", Font.PLAIN, 11));

        info.add(t);
        info.add(a);
        info.add(r);
        info.setOpaque(true);;
        card.add(info, BorderLayout.SOUTH);

        // (Opcional) click
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                // Ej: Navigator.showExplorar();
            }
        });

        return card;
    }

    protected void cargarReservasUsuarioEnLista() {
        if (modeloReservas == null || currentUser == null) return;
        modeloReservas.clear();

        currentUser.cargarReservas();
        List<Reserva> mias = currentUser.getReservas();
        if (mias == null) return;

        for (Reserva r : mias) {
            String titulo    = (r.getLibro() != null) ? r.getLibro().getTitulo() : "(sin libro)";
            String autor     = (r.getLibro() != null) ? r.getLibro().getAutor()  : "";
            String fecha     = String.valueOf(r.getFecha());
            String restantes = String.valueOf(r.getDiasRestantes());
            String valoracion = String.valueOf(r.getValoracionUsuario());
            
            modeloReservas.addElement(String.format("%s — %s | %s | %s días restantes | Valoración: %s",
                    titulo, autor, fecha, restantes, valoracion));
        }
    }

    // ventana activa (helper)
    private void estaActivo(JLabel activo, JLabel... otros) {
        Color colorActivo = darkMode ? new Color(180, 120, 255) : new Color(150, 0, 150);
        Color colorNormal = darkMode ? new Color(200, 200, 200) : Color.BLACK;

        activo.setForeground(colorActivo);
        //activo.setOpaque(false);

        for (JLabel otro : otros) {
            otro.setForeground(colorNormal);
            //otro.setOpaque(false);
        }
    }

    
    public void aplicarTema() {
        Color fondo, texto, headerBg, menuBg;

        if (darkMode) {
            fondo = new Color(30, 30, 30);
            texto = new Color(230, 230, 230);
            headerBg = new Color(20, 20, 20);
            menuBg = new Color(40, 40, 40);
        } else {
            fondo = new Color(245, 245, 245);
            texto = Color.BLACK;
            headerBg = new Color(0, 160, 220);
            menuBg = Color.WHITE;
        }


        // Aplicar al contenido central
        aplicarTemaRecursivo(this.getContentPane(), fondo, texto, menuBg);

        // Barra superior
        if (headerPanel != null) {
            headerPanel.setBackground(headerBg);
            lblBiblio.setForeground(Color.WHITE); 
            if (lblPerfil != null) lblPerfil.setForeground(Color.WHITE);
            if (lblToggleDark != null) lblToggleDark.setForeground(Color.WHITE);
        }

        // Menú
        if (menuPanel != null) {
            menuPanel.setBackground(menuBg);
            if (lblInicio != null) lblInicio.setForeground(darkMode ? new Color(200, 200, 200) : Color.BLACK);
            if (lblExplorar != null) lblExplorar.setForeground(darkMode ? new Color(200, 200, 200) : Color.BLACK);
            if (lblReservas != null) lblReservas.setForeground(darkMode ? new Color(200, 200, 200) : Color.BLACK);
        }

        // Reaplicar morado al activo
        if ("inicio".equalsIgnoreCase(ventanaActiva)) {
            estaActivo(lblInicio, lblExplorar, lblReservas);
        } else if ("explorar".equalsIgnoreCase(ventanaActiva)) {
            estaActivo(lblExplorar, lblInicio, lblReservas);
        } else if ("reservas".equalsIgnoreCase(ventanaActiva)) {
            estaActivo(lblReservas, lblInicio, lblExplorar);
        }

        repaint();
        revalidate();
    }



    
    private void aplicarTemaRecursivo(Component comp, Color fondo, Color texto, Color menuBg) {


        if (comp instanceof JComponent jc) {
            Object flag = jc.getClientProperty("theme-card");
            if (flag != null && flag.equals(true)) {
                Color fondoCard = darkMode ? new Color(40,40,40) : Color.WHITE;
                
                jc.setBackground(fondoCard);
                return;
            }
        }
    	
    	if (comp instanceof JPanel) {
            if (comp == headerPanel || comp == menuPanel) return;
            JComponent jc = (JComponent) comp;
            jc.setOpaque(true);
            jc.setBackground(fondo);
        }

    	if (comp instanceof JLabel lbl) {
    	    if (lbl.getParent() instanceof javax.swing.table.JTableHeader) {
    	        lbl.setForeground(Color.BLACK);
    	        return;
    	    }

    	    String text = lbl.getText();
    	    if ("Reservas".equals(text) || "Mejor valorados".equals(text)) {
    	        if (darkMode) {
    	            lbl.setForeground(new Color(144,213,255));
    	        } else {
    	            lbl.setForeground(new Color(0,102,204));
    	        }
    	    } else {
    	        lbl.setForeground(texto);
    	    }
    	}

        if (comp instanceof JScrollPane) {
        	JScrollPane sp = (JScrollPane) comp;
            sp.setOpaque(true);
            sp.setBackground(fondo);
            sp.getViewport().setOpaque(true);
            sp.getViewport().setBackground(fondo);

            Component view = sp.getViewport().getView();
            if (view != null) {
                aplicarTemaRecursivo(view, fondo, texto, menuBg);
            }
            return;
        }

        if (comp instanceof JTable table) {
            table.setBackground(fondo);
            table.setForeground(texto);

            JTableHeader header = table.getTableHeader();
            header.setBackground(menuBg);
            
            header.setForeground(Color.BLACK);
        }


        if (comp instanceof JButton btn) {
        	 String text = btn.getText();

        	    if ("Devolver préstamo".equals(text)) {
        	        if (darkMode) {
        	            btn.setBackground(new Color(120, 80, 180)); // ejemplo
        	            btn.setForeground(Color.BLACK);
        	        } else {
        	            btn.setBackground(new Color(200, 200, 200));
        	            btn.setForeground(Color.BLACK);
        	        }
        	    } else {
        	        btn.setBackground(menuBg);
        	        btn.setForeground(texto);
        	    }
        }

        if (comp instanceof JComboBox) {
            comp.setBackground(menuBg);
            comp.setForeground(texto);
        }

        if (comp instanceof JCheckBox) {
            comp.setBackground(fondo);
            comp.setForeground(texto);
        }

        if (comp instanceof Container) {
            for (Component child : ((Container) comp).getComponents()) {
                aplicarTemaRecursivo(child, fondo, texto, menuBg);
            }
        }
        

    }



    
    
    
    // ****************** ADMIN ***************************
 // Añadir libro
    public void agregarLibro(Libro libro) throws SQLException {
        if(User.getLoggedIn().getRol() != Rol.ADMIN) return;
        
        libroDAO.insertaLibro(libro);
        this.libros.add(libro);
        Navigator.inicio.refrescarTopLibros();
    }

    // Eliminar libro
    public void eliminarLibro(Libro libro) throws SQLException {
        if(User.getLoggedIn().getRol() != Rol.ADMIN) return;
        libroDAO.deleteLibro(libro);
        this.libros.remove(libro);
        Navigator.inicio.refrescarTopLibros();
    }

}