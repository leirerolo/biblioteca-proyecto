package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import domain.Libro;
import domain.Reserva;
import domain.User;

public class JFrameReservas extends JFramePrincipal{
	private static final long serialVersionUID = 1L;
	private JPanel panelLibros;
	private User user = User.getLoggedIn(); //la ventana de reservas es del user que ha iniciado sesión

	public JFrameReservas(List<Libro> libros) {
		super(libros);
		this.libros = new ArrayList<>();
		
		if (this.user.getReservas()!=null) {
			for (Reserva reserva : this.user.getReservas()) {
				this.libros.add(reserva.getLibro());
			}
		}
		this.inicializarPanelSuperior(); //hereda de JFramePrincipal
		this.inicializarPanelCentral();
	}
	
	private void inicializarPanelCentral() {		
		JPanel contentPanel = new JPanel(new BorderLayout());
	    contentPanel.setBackground(Color.WHITE);
		
		//cabecera
		JLabel lblReservas = new JLabel("Reservas");
		lblReservas.setFont(fuenteTitulo);
		lblReservas.setForeground(new Color(0, 102, 204));
		lblReservas.setHorizontalAlignment(JLabel.LEFT);
		lblReservas.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
		contentPanel.add(lblReservas, BorderLayout.NORTH);
		
		//Centro: zona de libros
		panelLibros = new JPanel();
		panelLibros.setLayout(new BoxLayout(panelLibros, BoxLayout.Y_AXIS));
		panelLibros.setBackground(Color.WHITE);
		panelLibros.setOpaque(true);
		panelLibros.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
	
	    for (Libro libro : libros) {
            JLabel lblLibro = new JLabel(libro.getTitulo() + " - " + libro.getAutor());
            lblLibro.setOpaque(true);
            lblLibro.setBackground(new Color(245, 245, 245));
            lblLibro.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
            panelLibros.add(lblLibro); 
            System.out.println(libro.getTitulo());
        }	
	    
		//scroll para cuando haya libros
		JScrollPane scrollPane = new JScrollPane(panelLibros, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);

		contentPanel.add(scrollPane, BorderLayout.CENTER);
		this.add(contentPanel, BorderLayout.CENTER);

		// Mostrar mensaje inicial o todos los libros
		filtrarLibros();
				
	}
	
	
	private void filtrarLibros() {
		
		if (libros.isEmpty()) {
			JLabel mensaje = new JLabel("No hay reservas...");
			mensaje.setHorizontalAlignment(JLabel.CENTER);
			panelLibros.add(mensaje);
			
		} else {
			for (Libro l : libros) {
		    	JPanel panelLibro = new JPanel(new BorderLayout());
		        panelLibro.setPreferredSize(new Dimension(Integer.MAX_VALUE, 120));
		        panelLibro.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
		        panelLibro.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

		    	//west: portada
		    	JLabel portada = new JLabel(l.getPortada(), JLabel.CENTER);
		    	panelLibro.add(portada, BorderLayout.WEST);
		    	
		    	//center: info
		    	JPanel panelInfo = new JPanel(new GridLayout(3,1,0,10));
		    	JLabel titulo = new JLabel(l.getTitulo(), JLabel.LEFT);
		    	titulo.setFont(fuenteTitulo);
		    	titulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		    	panelInfo.add(titulo);
		    	
		    	JLabel autor = new JLabel(l.getAutor(), JLabel.LEFT);
		    	autor.setFont(fuenteMenu);
		    	autor.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		    	panelInfo.add(autor);
		    	
		    	JLabel valoracion = new JLabel(String.valueOf(l.getValoracion()), JLabel.LEFT);
		    	valoracion.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		    	panelInfo.add(valoracion);
		    	
		    	panelLibro.add(panelInfo, BorderLayout.CENTER);
		    	panelLibros.add(panelLibro);
		    }
		}
		panelLibros.revalidate();
		panelLibros.repaint();
	}
	
}
