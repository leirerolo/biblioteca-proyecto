package gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Frame;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;

import domain.Reserva;

public class JDialogValorar extends JDialog{
	private static final long serialVersionUID = 1L;
	private Font fuenteMenu = new Font("Comic Sans MS", Font.BOLD, 18);

	public JDialogValorar(JDialog padre, Reserva reserva, JFrameReservas frameReservas) {
		super(padre, "Valorar libro",true);
		this.setSize(400,200);
		this.setLayout(new BorderLayout());
		this.setLocationRelativeTo(padre);
		this.setResizable(false);
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
        //slider
        JPanel panelSlider = new JPanel(new BorderLayout());
        JSlider slider = new JSlider(10, 50, 30);
        slider.setMajorTickSpacing(10);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        
        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        labelTable.put(10, new JLabel("1"));
        labelTable.put(20, new JLabel("2"));
        labelTable.put(30, new JLabel("3"));
        labelTable.put(40, new JLabel("4"));
        labelTable.put(50, new JLabel("5"));
        slider.setLabelTable(labelTable);
        
    	double valorDecimalInicio = slider.getValue() / 10.0;
        JLabel lblValor = new JLabel("Valoración: " + String.format("%.1f", valorDecimalInicio), JLabel.CENTER);
        lblValor.setFont(fuenteMenu);
        lblValor.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        slider.addChangeListener(e -> {
        	double valorDecimal = slider.getValue() / 10.0;
            lblValor.setText("Valoración: " + String.format("%.1f", valorDecimal));
        });
        
        panelSlider.add(lblValor, BorderLayout.NORTH);
        panelSlider.add(slider, BorderLayout.CENTER);
        
        //boton cancelar y aceptar
        JPanel panelBotones = new JPanel();
        JButton aceptar = new JButton("Aceptar");
        JButton cancelar = new JButton("Cancelar");

        panelBotones.add(cancelar);
        panelBotones.add(aceptar);
        
        //añadir al dialog 
        this.add(panelSlider, BorderLayout.CENTER);
        this.add(panelBotones, BorderLayout.SOUTH);
        
        //------------------listener de botones--------------------
        
        //cancelar
        cancelar.addActionListener((e) -> {
        	dispose();
        });
        
        //aceptar
        aceptar.addActionListener((e) -> {
            double nuevaValoracion = slider.getValue() / 10.0;
            String tituloLibro = reserva.getLibro().getTitulo();
            double valoracionInicial = reserva.getLibro().getValoracionOriginal();
            
            //media de todas las valoraciones del mismo libro
            double suma = valoracionInicial;
            int contador = 1;
            
            for (Reserva r : frameReservas.getUser().getReservas()) {
            	if (r != reserva && r.getLibro().getTitulo().equals(tituloLibro) && r.getValoracionUsuario() > 0.0) {
                    suma += r.getValoracionUsuario();
                    contador++;
                }
            }
            
            
         // si usuario ya valoro antes, reemplazar valoracion
            if (reserva.getValoracionUsuario() > 0.0) {
            	suma += nuevaValoracion;
                contador++;
                
            } else {// primera valoracion
                suma += nuevaValoracion;
                contador++;      
            }
            
            double mediaGlobal = suma / contador;
            
            reserva.setValoracionUsuario(nuevaValoracion);//actualiza valoracion usuario
			//System.out.println(nuevaValoracion);
            
            //actualiza valoracion
            for (Reserva r : frameReservas.getUser().getReservas()) {
                if (r.getLibro().getTitulo().equals(tituloLibro)) {
                    r.getLibro().setValoracion(mediaGlobal);
                }
            }
            
            
            
            JOptionPane.showMessageDialog(this, "¡Gracias por valorar!\nNueva valoración: " + String.format("%.2f", mediaGlobal), "Valoración actualizada", JOptionPane.INFORMATION_MESSAGE);
            
            dispose();
            
            //actualiza y guarda
            if (frameReservas != null) {
                frameReservas.actualizarReservas();
                frameReservas.getUser().guardarReservasCSV();
            }
            
         // Actualiza etiqueta en JDialogReserva si está abierta
            if (padre instanceof JDialogReserva) {
                ((JDialogReserva) padre).actualizarValoracion();
            }
            
            //refrescar panel de inicio si está abierto
            for (Frame frame : JFrame.getFrames()) {
            	if (frame instanceof JFrameInicio) {
            		((JFrameInicio) frame).refrescarTopLibros();
            	}
            }
        });
	}
}
