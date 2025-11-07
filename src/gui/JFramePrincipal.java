package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

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
import persistence.AppState;

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
        this.libros = libros;
        this.ventanaActiva = ventanaActiva;
        this.state=state;

        if (this.currentUser == null) {
            this.currentUser = domain.User.getLoggedIn();
        }
        if (this.currentUser != null) {
            this.currentUser.cargarReservasCSV();
        }

        this.inicializarPanelSuperior();
        this.inicializarContenidoCentral();
        
        this.setTitle("Biblioteca");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(600, 800);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
     
    }
	public JFramePrincipal(List<Libro> libros, String ventanaActiva) {
	    this(libros, ventanaActiva, null); // carga AppState desde disco si viene null
	}
	
	public void setCurrentUser(User u) {
        this.currentUser = u;
    }
	
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
			@Override
            public void mouseClicked(MouseEvent e) {
                // Usa currentUser o sesión global como respaldo
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
	    if ("inicio".equals(ventanaActiva)) {
	        estaActivo(inicio, explorar, reservas);
	    } else if ("explorar".equals(ventanaActiva)) {
	        estaActivo(explorar, inicio, reservas);
	    } else if ("reservas".equals(ventanaActiva)) {
	        estaActivo(reservas, inicio, explorar);
	    }
	    
	 // Navegación (mantengo tus Navigator + dispose)
        inicio.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (!"inicio".equalsIgnoreCase(ventanaActiva)) {
                    Navigator.showInicio();
                    JFramePrincipal.this.dispose();
                }
            }
        });

        explorar.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (!"explorar".equalsIgnoreCase(ventanaActiva)) {
                    Navigator.showExplorar();
                    JFramePrincipal.this.dispose();
                }
            }
        });

        reservas.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (!"reservas".equalsIgnoreCase(ventanaActiva)) {
                    Navigator.showReservas();
                    JFramePrincipal.this.dispose();
                }
            }
        });
    }

    // ================== CONTENIDO CENTRAL (mínimo para “reservas”) ==================
    private void inicializarContenidoCentral() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        add(mainPanel, BorderLayout.CENTER);

        if ("reservas".equalsIgnoreCase(ventanaActiva)) {
            // Lista de reservas del usuario actual
            modeloReservas = new DefaultListModel<>();

            JList<String> lista = new JList<>(modeloReservas);
            mainPanel.add(new JScrollPane(lista), BorderLayout.CENTER);

            // Pie con acciones mínimas
            JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton btnGuardar = new JButton("Guardar CSV");
            btnGuardar.addActionListener(ev -> {
                if (currentUser != null) {
                    currentUser.guardarReservasCSV(); // usa tu método tal cual
                    JOptionPane.showMessageDialog(this, "Reservas guardadas.", "OK", JOptionPane.INFORMATION_MESSAGE);
                }
            });
            south.add(btnGuardar);
            mainPanel.add(south, BorderLayout.SOUTH);

            // Cargar al entrar
            cargarReservasUsuarioEnLista();
        } else {
            // Para “inicio” y “explorar” mantengo tu flujo (aquí puedes pintar lo que ya tengas)
            JPanel placeholder = new JPanel(new BorderLayout());
            JLabel l = new JLabel("Contenido de " + ventanaActiva, JLabel.CENTER);
            l.setFont(new Font("SansSerif", Font.PLAIN, 16));
            placeholder.add(l, BorderLayout.CENTER);
            mainPanel.add(placeholder, BorderLayout.CENTER);
        }
    }

    private void cargarReservasUsuarioEnLista() {
        if (modeloReservas == null) return;
        modeloReservas.clear();

        if (currentUser == null) return;

        // Asegura tener datos frescos del CSV
        currentUser.cargarReservasCSV();

        List<Reserva> mias = currentUser.getReservas();
        if (mias == null) return;

        // Muestra: Título — Autor — Fecha — Días restantes
        for (Reserva r : mias) {
            String titulo   = (r.getLibro() != null) ? r.getLibro().getTitulo() : "(sin libro)";
            String autor    = (r.getLibro() != null) ? r.getLibro().getAutor()  : "";
            String fecha    = String.valueOf(r.getFecha());
            String restantes= String.valueOf(r.getDiasRestantes());
            modeloReservas.addElement(
                String.format("%s — %s | %s | %s días restantes", titulo, autor, fecha, restantes)
            );
        }
    }

    // ventana activa (tu helper)
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