package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

import domain.Libro;

public class JDialogLibro extends JDialog {
	private static final long serialVersionUID = 1L;
	private Font fuenteTitulo = new Font("Comic Sans MS", Font.BOLD, 22);
	private Font fuenteMenu = new Font("Comic Sans MS", Font.BOLD, 18);
	
	public JDialogLibro(JFrame padre, Libro libro) {
		super(padre, "Información del libro", true);
		this.setSize(400, 250);
		this.setLayout(new BorderLayout());
		
		//Información del libro: center 
		JPanel panelInfo = new JPanel(new BorderLayout());
		panelInfo.setBorder(new MatteBorder(0,0,1,0,Color.GRAY));
		JLabel portada = new JLabel(libro.getPortada(), JLabel.CENTER);
		panelInfo.add(portada, BorderLayout.WEST);
		
		JPanel panelDatos = new JPanel(new GridLayout(3,1,0,10));
		JLabel titulo = new JLabel(libro.getTitulo(), JLabel.LEFT);
		titulo.setFont(fuenteTitulo);
    	titulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    	titulo.setBorder(new MatteBorder(0,0,1,0,Color.GRAY));
    	panelDatos.add(titulo);
    	
    	JLabel autor = new JLabel(libro.getAutor(), JLabel.LEFT);
    	autor.setFont(fuenteMenu);
    	autor.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    	panelDatos.add(autor);
    	
    	JLabel valoracion = new JLabel(String.valueOf(libro.getValoracion()), JLabel.LEFT);
    	valoracion.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    	panelDatos.add(valoracion);
    	
    	panelInfo.add(panelDatos, BorderLayout.CENTER);
    	this.add(panelInfo, BorderLayout.CENTER);
    	
		
		//Botones cerrar y reservar: south
		JPanel panelBotones = new JPanel();
		JButton cerrar = new JButton("Cerrar");
		cerrar.setBackground(Color.RED);
		JButton reservar = new JButton("Reservar");
		reservar.setBackground(Color.GREEN);
		panelBotones.add(cerrar);
		panelBotones.add(reservar);
		
		//COMPORTAMIENTO DE LOS BOTONES
		cerrar.addActionListener((e) -> {
			this.dispose();
		});
		
		this.add(panelBotones, BorderLayout.SOUTH);
		this.setResizable(false);
		this.setLocationRelativeTo(padre);
	}
	
}
