package gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import domain.Libro;
import domain.Reserva;
import domain.User;
import threads.HiloDevoluciones;

public class JFrameReservas extends JFramePrincipal{
	private static final long serialVersionUID = 1L;
	
	private JTable tablaReservas;
	private DefaultTableModel modeloTabla;
	private JPanel panelTableContainer;
	private JLabel lblNoReservas;
	private User user = User.getLoggedIn(); //la ventana de reservas es del user que ha iniciado sesión

	//para la devolucion
	private JButton btnDevolver;
	private JProgressBar progressBar;
	private JPanel panelIzq;
	private JPanel panelCen;
	private JPanel panelDer;
	private JPanel panelDevolucion;
	
	private List<Reserva> listaReservas = new ArrayList<>();
	
	public JFrameReservas(List<Libro> libros) {
		super(libros,"reservas");

		this.inicializarPanelCentral();
		actualizarReservas();
	}
	
	public User getUser() {
		return user;
	}
	public void actualizarReservaEnBD(Reserva reserva) {
        try {
            user.actualizarReserva(reserva); 
            System.out.println("Reserva ID " + reserva.getLibro().getId() + " actualizada en la BD (Prolongación).");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al prolongar la reserva en la Base de Datos: " + e.getMessage(), 
                "Error de BD", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
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
		
		//Centro: 
		String[] columnas = {"Título", "Autor", "Fecha reserva", "Días restantes"};
		//modelo de la tabla: celdas no editables, y filas a 0 (luego añadimos)
		modeloTabla = new DefaultTableModel(columnas, 0) { 
			@Override
			public boolean isCellEditable(int row, int col) {
				return false;
			}
			
			@Override
            public Object getValueAt(int row, int col) {
                if(row >= listaReservas.size()) return ""; //por si está vacía
                Reserva r = listaReservas.get(row);
                switch(col) {
                    case 0: return r.getLibro().getTitulo();
                    case 1: return r.getLibro().getAutor();
                    case 2: return r.getFecha();
                    case 3: return r.getDiasRestantes();
                    default: return "";
                }
            }
		};
			
		tablaReservas = new JTable(modeloTabla);
		//añado la cabecera
		for (int i=0; i<columnas.length; i++) {
			tablaReservas.getColumnModel().getColumn(i).setHeaderValue(columnas[i]);
		}
			
		//RENDERIZADO DE LA TABLA
		//pinta la fila de amarillo pastel si quedan 7 días o menos
		//y de rojo si se ha pasado el plazo
		TableCellRenderer miCellRenderer = new TableCellRenderer() {

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				String text = (value != null) ? value.toString() : ""; //por si el get value at no me devuelve nada
				JLabel celda = new JLabel(text);
				celda.setOpaque(true);
				celda.setHorizontalAlignment(SwingConstants.CENTER);
				celda.setFont(table.getFont());
					
				//colores por defecto
				Color fondo = Color.white;
				Color texto = Color.black;
					
				//columna "Días restantes" --> índice 3
				if (column == 3) {
					try {
						int dias = Integer.parseInt(value.toString());
						if (dias<=0) {
							fondo = new Color(255, 102, 102); //rojo suave
						} else if (dias<=7) {
							fondo = new Color(255, 255, 153); //amarillo suave
						}
					} catch(NumberFormatException e) {
						//excepción por si no es numérico.
					}
				}
				//si está seleccionada la fila, la pintamos con colores de selección
				if (isSelected) {
					fondo = table.getSelectionBackground();
					texto = table.getSelectionForeground();
				}
				celda.setBackground(fondo);
				celda.setForeground(texto);
				return celda;
			}
		};
		tablaReservas.setDefaultRenderer(Object.class, miCellRenderer);
		tablaReservas.setFillsViewportHeight(true); //para que llene el espacio
		tablaReservas.setRowHeight(25);
		tablaReservas.setBackground(Color.WHITE);
		tablaReservas.getTableHeader().setBackground(new Color(230, 230, 250));
        tablaReservas.getTableHeader().setFont(fuenteMenu);
  
        JScrollPane scrollPane = new JScrollPane(tablaReservas);
            
            
        lblNoReservas = new JLabel("NO HAY RESERVAS");
        lblNoReservas.setHorizontalAlignment(JLabel.CENTER);
            
        JPanel panelMensajevacio = new JPanel(new GridBagLayout());
        panelMensajevacio.setBackground(Color.WHITE);
        panelMensajevacio.add(lblNoReservas, new GridBagConstraints());
            
        panelTableContainer= new JPanel(new CardLayout());
        panelTableContainer.setBackground(Color.WHITE);
            
        panelTableContainer.add(scrollPane, "TABLA");
        panelTableContainer.add(panelMensajevacio, "MENSAJE");
            
        contentPanel.add(panelTableContainer, BorderLayout.CENTER);
        
        
        //botón para la devolución
        btnDevolver = new JButton("Devolver préstamo");
        btnDevolver.setEnabled(false);//está deshabilitado hasta que se seleccione un préstamo
        
        panelDevolucion = new JPanel(new BorderLayout());
        panelDevolucion.setBackground(Color.WHITE);
        panelDevolucion.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelDevolucion.setOpaque(true);

        panelDevolucion.add(btnDevolver);
        
        
           
        
        //LISTENER PARA HABILITAR BOTÓN
        tablaReservas.getSelectionModel().addListSelectionListener(e -> {
        	int row = tablaReservas.getSelectedRow();
        	btnDevolver.setEnabled(row != -1); //se habilita si hay alguna fila seleccionada
        });
        
        
        //LISTENER BOTÓN DEVOLVER
        btnDevolver.addActionListener(e -> {
        	int row = tablaReservas.getSelectedRow();
        	if (row == -1) return;
        	
        	Reserva seleccionada = listaReservas.get(row);
        	btnDevolver.setEnabled(false);
        	
        	//crear dialogo
        	JDialog dialogSimulacion = new JDialog(this, "Devolviendo libro...", false);
        	dialogSimulacion.setSize(400,200);
        	dialogSimulacion.setLayout(new BorderLayout());
        	
        	//componentes para el diálogo
        	JPanel panelCuadrados = new JPanel(new GridLayout(1,3,10,0));
            panelIzq = new JPanel();
            panelCen = new JPanel();
            panelDer = new JPanel();
            panelIzq.setBackground(Color.LIGHT_GRAY);
            panelDer.setBackground(Color.LIGHT_GRAY);
            panelIzq.setOpaque(true);
            panelDer.setOpaque(true);
            panelCen.setBackground(Color.RED);
            panelCen.setOpaque(true);
            
            panelCuadrados.add(panelIzq);
            panelCuadrados.add(panelCen);
            panelCuadrados.add(panelDer);
            
            progressBar = new JProgressBar(0,100);
            dialogSimulacion.add(panelCuadrados, BorderLayout.CENTER);
            dialogSimulacion.add(progressBar, BorderLayout.SOUTH);
            
            dialogSimulacion.setLocationRelativeTo(this);
        	
            //creo el hilo para la simulación
        	HiloDevoluciones hilo = new HiloDevoluciones(
        			seleccionada, user, panelIzq, panelCen, panelDer,
        			progressBar, () -> { //al terminar el hilo:
        				dialogSimulacion.dispose(); //cerrar diálogo al terminar
        				actualizarReservas(); //actualizar tabla
        			});
        	hilo.start();
        	
        	dialogSimulacion.setVisible(true);
        });
        actualizarReservas(); //actualizo porque el hilo puede haber cambiado las reservas
        this.add(contentPanel);
        
        
        //LISTENER DOBLE CLICK: dialog reserva
        tablaReservas.addMouseListener(new java.awt.event.MouseAdapter() {
           	@Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
               if (evt.getClickCount() == 2) { 
                   int row = tablaReservas.getSelectedRow();
                   if (row != -1) {
                       String tituloSeleccionado = (String) tablaReservas.getValueAt(row, 0); 
                       Reserva reservaSeleccionada = null;
                       for (Reserva r : user.getReservas()) { 
                           if (r.getLibro().getTitulo().equals(tituloSeleccionado)) {
                               reservaSeleccionada = r;
                               break;
                           }
                       }
                            
                       if (reservaSeleccionada != null) {
                           JDialogReserva infoReserva = new JDialogReserva(JFrameReservas.this, reservaSeleccionada);
                           infoReserva.setVisible(true);
                       }
                   }
               }
           	}
         });
         actualizarReservas();
         this.add(contentPanel, BorderLayout.CENTER);
         this.add(panelDevolucion, BorderLayout.SOUTH);
	}
	
	public void actualizarReservas() {
		listaReservas.clear();
		
        List<Reserva> reservas = (this.user.getReservas() != null) ? this.user.getReservas() : new ArrayList<>();
        listaReservas.addAll(reservas);
        CardLayout cl = (CardLayout) (panelTableContainer.getLayout());

        if (!listaReservas.isEmpty()) {
        	for (Reserva reserva: listaReservas) {
        		this.libros.add(reserva.getLibro());
            }
        	modeloTabla.setRowCount(listaReservas.size());
        	cl.show(panelTableContainer, "TABLA");
        }else {
        	cl.show(panelTableContainer, "MENSAJE");
        }
		this.revalidate();
		this.repaint();
	}
}
