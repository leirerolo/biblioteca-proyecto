package gui;


import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Frame;
import java.sql.SQLException;
import java.util.Hashtable;


import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;


import db.LibroDAO;
import db.ValoracionDAO;
import domain.Libro;
import domain.Reserva;
import domain.User;



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
            
            // 1. Establecer la nueva valoración LOCALMENTE en el objeto Reserva
            reserva.setValoracionUsuario(nuevaValoracion);
            
            try {
                User currentUser = User.getLoggedIn();
                if (currentUser == null) {
                    JOptionPane.showMessageDialog(this, "Error: Usuario no autentificado.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // 2. Persistir en la BD: Llama a User.actualizarReserva(), que maneja la BD.
                currentUser.actualizarReserva(reserva); 
                
                //guardar también en la tabla de valoraciones
                ValoracionDAO valoracionDAO = new ValoracionDAO();
                valoracionDAO.insertaVal(reserva.getLibro().getId(), nuevaValoracion, currentUser.getUsuario());
                
                //actualizar también la media de valoraciones y el número en la tabla libro
                LibroDAO libroDAO = new LibroDAO();
                libroDAO.actualizarValoracionMedia(reserva.getLibro().getId());
                
                
                JOptionPane.showMessageDialog(this, 
                        "¡Gracias por valorar!\nValoración personal guardada: " + String.format("%.1f", nuevaValoracion) +
                        "\nLa valoración media del libro ha sido actualizada.", 
                        "Valoración actualizada", JOptionPane.INFORMATION_MESSAGE);
                
                dispose();
                
                // 3. Actualizar la UI
                
                // Recargar/Refrescar las listas de reservas
                if (frameReservas != null) {
                    frameReservas.cargarReservasUsuarioEnLista(); 
                }
                
                // Actualiza etiqueta en JDialogReserva si está abierta
                if (padre instanceof JDialogReserva) {
                    ((JDialogReserva) padre).actualizarValoracion();
                }
                
                // Refrescar panel de inicio
                for (Frame frame : JFrame.getFrames()) {
                    if (frame instanceof JFrameInicio) {
                        ((JFrameInicio) frame).refrescarTopLibros();
                    }
                }
                
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, 
                        "Error al guardar el rating en la Base de Datos: " + ex.getMessage(), 
                        "Error BD", JOptionPane.ERROR_MESSAGE);
            }
        });
	}
}