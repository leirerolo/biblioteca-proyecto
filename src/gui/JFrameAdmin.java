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
	private final Color COLOR_MENU = new Color(90,170,255);
	private final Color COLOR_HOVER = new Color(60,140,230);
	private final Color COLOR_SELECTED = new Color(135,200,255);
	private final Color COLOR_CENTRAL= new Color(100,180,255);
	
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
		panelLateral.setBackground(new Color(220,220,220));
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
		panelCentral.setBackground(new Color(245,245,245));
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
		label.setBackground(COLOR_MENU);
		label.setForeground(Color.WHITE);
		label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		label.setBorder(new MatteBorder(0, 0, 2, 0, Color.WHITE));
		
		label.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent e) {
				if(label.getBackground() != COLOR_SELECTED) {
					label.setBackground(COLOR_HOVER);
				}
			}
			public void mouseExited(MouseEvent e) {
				if(label.getBackground() != COLOR_SELECTED) {
					label.setBackground(COLOR_MENU);
				}
			}
		});
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
        
        PanelMasReservados panelMasReservados = new PanelMasReservados();
        panelCentral.add(panelMasReservados, BorderLayout.CENTER);
        
        panelCentral.revalidate();
        panelCentral.repaint();
        resaltarMenu(lblMasReservados);
    }

    private void mostrarPeorValorados() {
        panelCentral.removeAll();
        
        PanelPeorValorados panel = new PanelPeorValorados();
        panelCentral.add(panel, BorderLayout.CENTER);
        
        panelCentral.revalidate();
        panelCentral.repaint();
        resaltarMenu(lblPeorValorados);
    }

    private void mostrarGestionLibros() {
        panelCentral.removeAll();
        
        PanelGestionLibros panelGestion = new PanelGestionLibros(libros);
        panelCentral.add(panelGestion, BorderLayout.CENTER);

        panelCentral.revalidate();
        panelCentral.repaint();
        resaltarMenu(lblGestionLibros);
    }

    // Resaltar opción seleccionada
    private void resaltarMenu(JLabel seleccionado) {
        for (Component c : panelLateral.getComponents()) {
            if (c instanceof JLabel) {
                JLabel lbl = (JLabel) c;
                lbl.setBackground(lbl == seleccionado ? COLOR_SELECTED : COLOR_MENU);
            }
        }
    }
}
