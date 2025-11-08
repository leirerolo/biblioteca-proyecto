package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

import domain.Reserva;

public class JDialogReserva extends JDialog {
	private static final long serialVersionUID = 1L;
	private Font fuenteTitulo = new Font("Comic Sans MS", Font.BOLD,22);
	private Font fuenteMenu = new Font("Comic Sans MS", Font.BOLD, 18);

	private static final int MAX_PROLONGACIONES = 2;
	private static final int DIAS_ADICIONALES = 7;
	
	private JLabel lblProlongaciones;
	
	private JLabel puntuacion;
	private Reserva reserva;
	
	public JDialogReserva(JFrame padre, Reserva reserva) {
		super(padre, "Información de la Reserva",true);
		this.setSize(500,300);
		this.setLayout(new BorderLayout());
		this.reserva = reserva;
		
		JPanel panelInfo= new JPanel(new BorderLayout());
		panelInfo.setBorder(new MatteBorder(0,0,1,0,Color.GRAY));
		
		//west: portada
		JLabel portada  = new JLabel(reserva.getLibro().getPortada(), JLabel.CENTER);
		panelInfo.add(portada, BorderLayout.WEST);
		
		//center: detalles
		JPanel panelDatos = new JPanel(new GridLayout(5,1,0,10));
		
		JLabel titulo = new JLabel(reserva.getLibro().getTitulo(), JLabel.LEFT);
		titulo.setFont(fuenteTitulo);
		titulo.setBorder(BorderFactory.createEmptyBorder(10,10,5,10));
		titulo.setBorder(new MatteBorder(0,0,1,0,Color.GRAY));
        panelDatos.add(titulo);
        
        JLabel autor = new JLabel("Autor: " + reserva.getLibro().getAutor(), JLabel.LEFT);
        autor.setFont(fuenteMenu);
        autor.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        panelDatos.add(autor);
        
        puntuacion = new JLabel("Valoracion: " + "★ " + reserva.getLibro().getValoracion(), JLabel.LEFT);
        puntuacion.setFont(fuenteMenu);
        puntuacion.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        panelDatos.add(puntuacion);
        
        JLabel fecha = new JLabel("Fecha Reserva: " + reserva.getFecha(), JLabel.LEFT);
        fecha.setFont(fuenteMenu);
        fecha.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
        panelDatos.add(fecha);
        
        lblProlongaciones = new JLabel("Prolongaciones: " + reserva.getProlongaciones() + " / " + MAX_PROLONGACIONES, JLabel.LEFT);
        lblProlongaciones.setFont(fuenteMenu);
        lblProlongaciones.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        panelDatos.add(lblProlongaciones);
		
        panelInfo.add(panelDatos,BorderLayout.CENTER);
        this.add(panelInfo, BorderLayout.CENTER);
        
        // south: botones
        JPanel panelBotones = new JPanel();
		JButton cerrar = new JButton("Cerrar");
		cerrar.setBackground(Color.RED);
		JButton prolongar = new JButton("Prolongar");
		prolongar.setBackground(new Color(0, 153, 204)); 

		
		panelBotones.add(cerrar);
		panelBotones.add(prolongar);
		
		//boton valoracion
        JButton valorar = new JButton("Valorar");
        valorar.setBackground(Color.CYAN);
        panelBotones.add(valorar);
        
		cerrar.addActionListener((e) -> {
			this.dispose();
		});
		
		prolongar.addActionListener((e) -> {
			if (reserva.getProlongaciones() < MAX_PROLONGACIONES) {
				reserva.setDuracion(reserva.getDuracion() + DIAS_ADICIONALES);
				reserva.setProlongaciones(reserva.getProlongaciones() + 1);
				lblProlongaciones.setText("Prolongaciones: " + reserva.getProlongaciones() + " / " + MAX_PROLONGACIONES);
				JOptionPane.showMessageDialog(this,"Reserva prolongada correctamente. Plazo extendido con " + DIAS_ADICIONALES + " días.","Prolongación exitosa",JOptionPane.INFORMATION_MESSAGE);
				
				if (padre instanceof JFrameReservas) {
					((JFrameReservas) padre).actualizarReservas();
					((JFrameReservas) padre).getUser().guardarReservasCSV(); //guardo la reserva actualizada
				}
			} else {
				JOptionPane.showMessageDialog(this, "No se puede prolongar más. Máximo alcanzado (" +  MAX_PROLONGACIONES + ")", "Limite de prolongación", JOptionPane.WARNING_MESSAGE);
			}
			
		});
		
		valorar.addActionListener((e)->{
			//ventana de valoracion
			JFrameReservas frameReservas = null;
		    if (padre instanceof JFrameReservas) {
		        frameReservas = (JFrameReservas) padre;
		    }
			
			JDialogValorar dialogValorar = new JDialogValorar(this , reserva, frameReservas);
			dialogValorar.setVisible(true);
		});
		
		this.add(panelBotones, BorderLayout.SOUTH);
		this.setResizable(false);
		this.setLocationRelativeTo(padre);
	}
	
	public void actualizarValoracion() {
	    puntuacion.setText("Valoracion: " + String.format("%.2f", reserva.getLibro().getValoracion()));
	}

}
