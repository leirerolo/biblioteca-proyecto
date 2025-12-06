package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

import domain.Libro;

public class JFrameAdmin extends JFrame {
	
	private static final long serialVersionUID = 1L;
	protected List<Libro> libros;
	
	//predefinir las fuentes
	protected Font fuenteTitulo = new Font("Comic Sans MS", Font.BOLD, 22);
	protected Font fuenteMenu = new Font("Comic Sans MS", Font.BOLD, 18);
	
	// Paneles principales
    private JPanel panelLateral;
    private JPanel panelCentral;

    // Labels de menú
    private JLabel lblUsuarios;
    private JLabel lblMasReservados;
    private JLabel lblPeorValorados;
    private JLabel lblGestionLibros;
	
	public JFrameAdmin(List<Libro> libros) {
		this.libros = libros;
		this.inicializarPanelPrincipal();
		
		this.setTitle("Administrador");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(1200, 700); //formato ordenador para el admin
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		
		
	}
	
	public List<Libro> getLibros() { 
		return libros; 
	}

	// **************** CREAR PANELES *****************************
	protected void inicializarPanelPrincipal() {
	
		//PANEL LATERAL IZQUIERDO: menú
		panelLateral = new JPanel(new GridLayout(0,1,0,10));
		panelLateral.setBackground(new Color(80,15,80));
		panelLateral.setOpaque(true);
		panelLateral.setPreferredSize(new Dimension(250, this.getHeight()));
		panelLateral.setBorder(new MatteBorder(0,0,0,3, Color.WHITE));
		
		//labels del menú
		lblUsuarios = crearLblMenu("Usuarios");
		lblMasReservados = crearLblMenu("Más reservados");
		lblPeorValorados = crearLblMenu("Peor valorados");
		lblGestionLibros = crearLblMenu("Añadir/Eliminar libros");
		
		//añadir al panel
		panelLateral.add(lblUsuarios);
		panelLateral.add(lblMasReservados);
		panelLateral.add(lblPeorValorados);
		panelLateral.add(lblGestionLibros);
		
		this.add(panelLateral, BorderLayout.WEST);
		
		
		//PANEL CENTRAL: "subventanas"
		panelCentral = new JPanel(new BorderLayout());
		panelCentral.setBackground(new Color(120,30,115));
		panelCentral.setOpaque(true);
		this.add(panelCentral, BorderLayout.CENTER);
		
		//subventana por defecto: usuarios
		mostrarUsuarios();
		
		//al hacer click en alguna otra
		lblUsuarios.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mostrarUsuarios();
            }
        });

        lblMasReservados.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mostrarMasReservados();
            }
        });

        lblPeorValorados.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mostrarPeorValorados();
            }
        });

        lblGestionLibros.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mostrarGestionLibros();
            }
        });
	}
	
	
	private JLabel crearLblMenu(String texto) {
		JLabel label = new JLabel(texto, JLabel.CENTER);
		label.setFont(fuenteMenu);
		label.setOpaque(true);
		label.setBackground(new Color(80,15,80));
		label.setForeground(Color.WHITE);
		label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		label.setBorder(new MatteBorder(0, 0, 2, 0, Color.WHITE));
		return label;
	}
	
	// ******************** SUBVENTANAS ****************************
	private void mostrarUsuarios() {
        panelCentral.removeAll();
        
        PanelUsers panelUsers = new PanelUsers();
        panelCentral.add(panelUsers, BorderLayout.CENTER);
        
        panelCentral.revalidate();
        panelCentral.repaint();
        resaltarMenu(lblUsuarios);
    }

    private void mostrarMasReservados() {
        panelCentral.removeAll();
        JLabel l = new JLabel("Top 6 Libros más Reservados", JLabel.CENTER);
        l.setFont(fuenteTitulo);
        panelCentral.add(l, BorderLayout.CENTER);
        panelCentral.revalidate();
        panelCentral.repaint();
        resaltarMenu(lblMasReservados);
    }

    private void mostrarPeorValorados() {
        panelCentral.removeAll();
        JLabel l = new JLabel("Top 6 Libros Peor Valorados", JLabel.CENTER);
        l.setFont(fuenteTitulo);
        panelCentral.add(l, BorderLayout.CENTER);
        panelCentral.revalidate();
        panelCentral.repaint();
        resaltarMenu(lblPeorValorados);
    }

    private void mostrarGestionLibros() {
        panelCentral.removeAll();
        JLabel l = new JLabel("Añadir / Eliminar Libros", JLabel.CENTER);
        l.setFont(fuenteTitulo);
        panelCentral.add(l, BorderLayout.CENTER);
        panelCentral.revalidate();
        panelCentral.repaint();
        resaltarMenu(lblGestionLibros);
    }

    // Resaltar opción seleccionada
    private void resaltarMenu(JLabel seleccionado) {
        for (Component c : panelLateral.getComponents()) {
            if (c instanceof JLabel) {
                JLabel lbl = (JLabel) c;
                lbl.setBackground(lbl == seleccionado ? new Color(240,180,220) : new Color(80,15,80));
            }
        }
    }
}
