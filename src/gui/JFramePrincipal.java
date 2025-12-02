package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.MatteBorder;

import domain.*;
import main.Main;
import persistence.AppState;
import persistence.AppStateStore;

public class JFramePrincipal extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private final AppState state;
	
	private User currentUser;
	protected List<Libro> libros;
	
	//predefinir las fuentes
	protected Font fuenteTitulo = new Font("Comic Sans MS", Font.BOLD, 22);
	protected Font fuenteMenu = new Font("Comic Sans MS", Font.BOLD, 18);
	
	private String ventanaActiva;
	
	private DefaultListModel<String> modeloReservas;
	
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

        this.inicializarPanelSuperior();
        
        this.setTitle("Biblioteca");
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setSize(600, 800);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        
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
                        System.exit(0); // 🔧 cerrar toda la aplicación
                    }
                }
            }
        });
        
        this.setFocusable(true);
        this.requestFocusInWindow();
        
// esto no hace nada ???
//        //al cerrar, guarda el csv actualizado
//        this.addWindowListener(new WindowAdapter() {
//        	@Override
//        	public void windowClosing(WindowEvent e) {
//        	
//        		System.exit(0); //cierra la app
//        	}
//        });
        
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
		JPanel header = new JPanel(new BorderLayout());
		header.setBackground(new Color(0, 160, 220));
		
		JLabel biblio = new JLabel("Biblio.O");
		biblio.setFont(fuenteTitulo);
		biblio.setForeground(Color.WHITE);
		header.add(biblio, BorderLayout.WEST);
		
		JLabel perfil = new JLabel("Mi perfil");
		perfil.setFont(fuenteTitulo);
		perfil.setForeground(Color.white);
		header.add(perfil, BorderLayout.EAST);
		
		perfil.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		perfil.addMouseListener(new MouseAdapter() {
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
        upperPanel.add(header, BorderLayout.NORTH);

		// Menú de navegación: inicio, explorar, reservas
		JPanel menu = new JPanel(new GridLayout(1,3));
		menu.setBackground(Color.white);
		
		JLabel inicio = new JLabel("Inicio", JLabel.CENTER);
		inicio.setFont(fuenteMenu);
		menu.add(inicio);

		JLabel explorar = new JLabel("Explorar", JLabel.CENTER);
		explorar.setFont(fuenteMenu);
		menu.add(explorar);
		
		JLabel reservas = new JLabel("Reservas", JLabel.CENTER);
		reservas.setFont(fuenteMenu);
		menu.add(reservas);
		
		upperPanel.add(menu, BorderLayout.CENTER);
		upperPanel.setBorder(new MatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
		add(upperPanel, BorderLayout.NORTH);
		
		
		// ************** CLICK EN "labels de menu" *****************
		
	    //cambia color segun la ventana activa
		if ("inicio".equalsIgnoreCase(ventanaActiva)) {
            estaActivo(inicio, explorar, reservas);
        } else if ("explorar".equalsIgnoreCase(ventanaActiva)) {
            estaActivo(explorar, inicio, reservas);
        } else if ("reservas".equalsIgnoreCase(ventanaActiva)) {
            estaActivo(reservas, inicio, explorar);
        }
	    
	 // Navegación (mantengo tus Navigator + dispose)
		inicio.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (!"inicio".equalsIgnoreCase(ventanaActiva)) {
                    Navigator.showInicio();
                    JFramePrincipal.this.dispose();
                }
            }
        });

        explorar.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (!"explorar".equalsIgnoreCase(ventanaActiva)) {
                    Navigator.showExplorar();
                    JFramePrincipal.this.dispose();
                }
            }
        });

        reservas.addMouseListener(new java.awt.event.MouseAdapter() {
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
        grid.setBackground(Color.WHITE);
        grid.setBorder(new MatteBorder(16, 16, 16, 16, Color.WHITE));

        for (Libro lib : top6) grid.add(buildBookCard(lib));

        JLabel titulo = new JLabel("Mejor valorados", JLabel.LEFT);
        titulo.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        titulo.setBorder(new MatteBorder(12, 16, 8, 16, Color.WHITE));

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.add(titulo, BorderLayout.NORTH);
        wrapper.add(grid, BorderLayout.CENTER);

        mainPanel.add(new JScrollPane(wrapper), BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private JPanel buildBookCard(Libro lib) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new MatteBorder(1, 1, 1, 1, new Color(230, 230, 230)));

        // Portada
        JLabel img = new JLabel();
        img.setHorizontalAlignment(JLabel.CENTER);
        if (lib.getPortada() != null) img.setIcon(lib.getPortada());
        card.add(img, BorderLayout.CENTER);

        // Texto (título + autor + valoración)
        JPanel info = new JPanel(new GridLayout(0, 1));
        info.setBackground(Color.WHITE);
        JLabel t = new JLabel(lib.getTitulo());
        t.setFont(new Font("SansSerif", Font.BOLD, 12));
        JLabel a = new JLabel(lib.getAutor());
        a.setFont(new Font("SansSerif", Font.PLAIN, 11));
        JLabel r = new JLabel("★ " + String.format("%.1f", lib.getValoracion()));
        r.setFont(new Font("SansSerif", Font.PLAIN, 11));

        info.add(t);
        info.add(a);
        info.add(r);
        info.setBorder(new MatteBorder(8, 8, 8, 8, Color.WHITE));
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
        Color colorActivo = new Color(150, 0, 150);
        Color colorNormal = Color.BLACK;

        activo.setForeground(colorActivo);
        activo.setOpaque(true);

        for (JLabel otro : otros) {
            otro.setForeground(colorNormal);
            otro.setOpaque(false);
        }
    }
}