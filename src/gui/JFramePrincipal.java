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
import java.util.Collections;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.MatteBorder;

import domain.*;

public class JFramePrincipal extends JFrame {
	private domain.User currentUser;
	public void setCurrentUser(domain.User u){ 
		this.currentUser = u; 
	}

	private static final long serialVersionUID = 1L;
	protected List<Libro> libros;
	//predefinir las fuentes
	protected Font fuenteTitulo = new Font("Comic Sans MS", Font.BOLD, 22);
	protected Font fuenteMenu = new Font("Comic Sans MS", Font.BOLD, 18);
	
	public JFramePrincipal(List<Libro> libros) {
		this.libros = libros;
		this.inicializarPanelSuperior();

		//this.inicializarPanelCentral();
		
		this.setTitle("Biblioteca");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(600, 800);
		this.setResizable(false);
		this.setLocationRelativeTo(null);
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
				if (currentUser == null) {
					JOptionPane.showMessageDialog(JFramePrincipal.this, "Inicia sesión para ver tu perfil.", "Perfil", JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				JFramePerfil perfilWin = new JFramePerfil(currentUser);
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
		JPanel mainPanel = new JPanel(new BorderLayout());
	    mainPanel.setBackground(Color.WHITE);
	    add(mainPanel, BorderLayout.CENTER);
		
		// ************** CLICK EN "labels de menu" *****************
		
		MouseAdapter mouseAdapterExplore = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Navigator.showExplorar();
				JFramePrincipal.this.dispose();
			}
		};
		explorar.addMouseListener(mouseAdapterExplore);
		
		MouseAdapter mouseAdapterReservas = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Navigator.showReservas();
				JFramePrincipal.this.dispose();
			}
		};
		reservas.addMouseListener(mouseAdapterReservas);
		
		MouseAdapter mouseAdapterInicio = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Navigator.showInicio();
				JFramePrincipal.this.dispose();
			}
		};
		inicio.addMouseListener(mouseAdapterInicio);
	}
}