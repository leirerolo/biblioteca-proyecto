package gui;

import java.awt.BorderLayout;
import java.awt.Font;
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
	private Font fuenteMenu = new Font("Comic Sans MS", Font.BOLD, 18);

	public JDialogValorar(JDialog padre, Reserva reserva, JFrameReservas frameReservas) {
		super(padre, "Valorar libro",true);
		this.setSize(400,200);
		this.setLayout(new BorderLayout());
		this.setLocationRelativeTo(padre);
		this.setResizable(false);
		
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
        
        //añadimos
        panelSlider.add(lblValor, BorderLayout.NORTH);
        panelSlider.add(slider, BorderLayout.CENTER);
        
        //boton cancelar y aceptar
        JPanel panelBotones = new JPanel();
        JButton aceptar = new JButton("Aceptar");
        JButton cancelar = new JButton("Cancelar");

        panelBotones.add(aceptar);
        panelBotones.add(cancelar);
        
        //añadir al dialog 
        this.add(panelSlider, BorderLayout.CENTER);
        this.add(panelBotones, BorderLayout.SOUTH);
        
        //------------------listener de botones--------------------
        
        //cancelar
        cancelar.addActionListener((e) -> {
        	dispose();
        });
        
        //aceptar
        aceptar.addActionListener(e -> {
            double nuevaValoracion = slider.getValue() / 10.0;
            double original = reserva.getLibro().getValoracion();
            double media = (original + nuevaValoracion) / 2.0;
            
            reserva.setValoracionUsuario(nuevaValoracion); //guarda la valoracion puesta en el usuario
            reserva.getLibro().setValoracion(media); //hace la media y actualiza

            JOptionPane.showMessageDialog(this, "¡Gracias por valorar!\nNueva valoración: " + String.format("%.2f", media), "Valoración actualizada", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            
            if (frameReservas != null) {
                frameReservas.actualizarReservas();
                frameReservas.getUser().guardarReservasCSV();
            }
        });
        
		
	}
}
