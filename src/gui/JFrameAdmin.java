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

import javax.swing.BorderFactory;
import javax.swing.JButton;
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
	private final Color COLOR_MENU_LIGHT = new Color(200, 220, 240);   
	private final Color COLOR_HOVER_LIGHT = new Color(170, 200, 230);  
	private final Color COLOR_SELECTED_LIGHT = new Color(140, 180, 220); 
	
	private final Color COLOR_MENU_DARK = new Color(30, 50, 80);        
	private final Color COLOR_HOVER_DARK = new Color(45, 70, 110);      
	private final Color COLOR_SELECTED_DARK = new Color(60, 90, 140);
	
	// Paneles principales
    private JPanel panelLateral;
    private JPanel panelCentral;

    // Labels de menú
    private JLabel lblUsuarios;
    private JLabel lblMasReservados;
    private JLabel lblPeorValorados;
    private JLabel lblGestionLibros;
    
    private Theme currentTheme = Theme.LIGHT; // default

	
	public JFrameAdmin(List<Libro> libros) {
		this.libros = libros;
		this.inicializarPanelPrincipal();
		
		this.setTitle("Administrador");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(1200, 700); //formato ordenador para el admin
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		
		applyTheme(currentTheme);
		
		
	}
	
	public List<Libro> getLibros() { 
		return libros; 
	}

	// **************** CREAR PANELES *****************************
	protected void inicializarPanelPrincipal() {
	
		//PANEL LATERAL IZQUIERDO: menú
		panelLateral = new JPanel(new GridLayout(0,1,0,0));
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
        
        JButton btnToggleTheme = new JButton(currentTheme == Theme.LIGHT ? "MODO OSCURO 🌙" : "MODO CLARO ☀"); 
        btnToggleTheme.setFont(new Font("Segoe UI Emoji", Font.BOLD, 12));
        btnToggleTheme.setFocusPainted(false);
        btnToggleTheme.setPreferredSize(new Dimension(200,40));
        btnToggleTheme.setBorder(null);
        btnToggleTheme.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnToggleTheme.setBackground(new Color(225, 225, 225));
        btnToggleTheme.setForeground(Color.BLACK);
        btnToggleTheme.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1));
        panelLateral.add(btnToggleTheme);
        
        btnToggleTheme.addActionListener(e -> {
            currentTheme = (currentTheme == Theme.LIGHT) ? Theme.DARK : Theme.LIGHT;
            applyTheme(currentTheme);
            btnToggleTheme.setText(currentTheme == Theme.LIGHT ?"MODO OSCURO 🌙" : "MODO CLARO ☀");
        });

	}
	
	
	private JLabel crearLblMenu(String texto) {
		JLabel label = new JLabel(texto, JLabel.CENTER);
		label.setFont(fuenteMenu);
		label.setOpaque(true);
		label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		
		label.setBackground(getColorMenu());
	    label.setForeground(currentTheme == Theme.LIGHT ? Color.BLACK : Color.WHITE);
	    
	    label.setBorder(new MatteBorder(0, 0, 2, 0, Color.WHITE));
	    
		label.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent e) {
				if(label.getBackground() != getColorSelected()) {
					label.setBackground(getColorHover());
				}
			}
			public void mouseExited(MouseEvent e) {
				if(label.getBackground() != getColorSelected()) {
					label.setBackground(getColorMenu());
				}
			}
		});
		return label;
	}
	 private Color getColorMenu() {
	        return currentTheme == Theme.LIGHT ? COLOR_MENU_LIGHT : COLOR_MENU_DARK;
	    }

	    private Color getColorHover() {
	        return currentTheme == Theme.LIGHT ? COLOR_HOVER_LIGHT : COLOR_HOVER_DARK;
	    }

	    private Color getColorSelected() {
	        return currentTheme == Theme.LIGHT ? COLOR_SELECTED_LIGHT : COLOR_SELECTED_DARK;
	    }
	
	// ******************** SUBVENTANAS ****************************
	private void mostrarUsuarios() {
        panelCentral.removeAll();
        
        PanelUsers panelUsers = new PanelUsers();
        panelUsers.applyTheme(currentTheme);
        panelCentral.add(panelUsers, BorderLayout.CENTER);
        
        panelCentral.revalidate();
        panelCentral.repaint();
        resaltarMenu(lblUsuarios);
    }

    private void mostrarMasReservados() {
        panelCentral.removeAll();
        
        PanelMasReservados panelMasReservados = new PanelMasReservados();
        panelMasReservados.applyTheme(currentTheme);
        panelCentral.add(panelMasReservados, BorderLayout.CENTER);
        
        panelCentral.revalidate();
        panelCentral.repaint();
        resaltarMenu(lblMasReservados);
    }

    private void mostrarPeorValorados() {
        panelCentral.removeAll();
        
        PanelPeorValorados panel = new PanelPeorValorados();
        panel.applyTheme(currentTheme);
        panelCentral.add(panel, BorderLayout.CENTER);
        
        panelCentral.revalidate();
        panelCentral.repaint();
        resaltarMenu(lblPeorValorados);
    }

    private void mostrarGestionLibros() {
        panelCentral.removeAll();
        
        PanelGestionLibros panelGestion = new PanelGestionLibros(libros);
        
        panelCentral.add(panelGestion, BorderLayout.CENTER); 
        panelGestion.setTheme(currentTheme);
        //panelCentral.add(panelGestion, BorderLayout.CENTER);

        panelCentral.revalidate();
        panelCentral.repaint();
        resaltarMenu(lblGestionLibros);
    }

    // Resaltar opción seleccionada
    private void resaltarMenu(JLabel seleccionado) {
        for (Component c : panelLateral.getComponents()) {
            if (c instanceof JLabel) {
                JLabel lbl = (JLabel) c;
                lbl.setBackground(lbl == seleccionado ? getColorSelected() : getColorMenu());
            }
        }
    }
    
    private void resaltarMenuActual() {
        if (panelCentral.getComponents().length > 0) {
            Component actual = panelCentral.getComponent(0);
            if (actual instanceof PanelUsers) resaltarMenu(lblUsuarios);
            else if (actual instanceof PanelMasReservados) resaltarMenu(lblMasReservados);
            else if (actual instanceof PanelPeorValorados) resaltarMenu(lblPeorValorados);
            else if (actual instanceof PanelGestionLibros) resaltarMenu(lblGestionLibros);
        }
    }
    public void applyTheme(Theme theme) {
        this.currentTheme = theme;
        panelLateral.setBackground(getColorMenu());
        panelLateral.setBorder(new MatteBorder(0, 0, 0, 3, theme.textColor));
        
        
        for (Component c : panelLateral.getComponents()) {
            if (c instanceof JLabel lbl) {
            	lbl.setForeground(theme.textColor);
            	lbl.setBackground(getColorMenu());
                lbl.setBorder(new MatteBorder(0, 0, 2, 0, theme.textColor));
            	
                
                
                
            }
            if (c instanceof JButton btn) {
                btn.setBackground(theme.backgroundPanel);
                btn.setForeground(theme.textColor);
            }
        }
        
        resaltarMenuActual();
        panelCentral.setBackground(theme.backgroundMain);
        for (Component c : panelCentral.getComponents()) {
            if (c instanceof PanelUsers p) p.applyTheme(theme);
            else if (c instanceof PanelMasReservados p) p.applyTheme(theme);
            else if (c instanceof PanelPeorValorados p) p.applyTheme(theme);
            else if (c instanceof PanelGestionLibros p) p.setTheme(theme);
        }

        revalidate();
        repaint();
    }
}
