package gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.basic.BasicProgressBarUI;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import db.ReservaDAO;
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

	private JFramePrincipal mainFrame;
	//para la devolucion
	private JButton btnDevolver;
	private JButton btnHistorial;
	private JProgressBar progressBar;
	private JPanel panelIzq;
	private JPanel panelCen;
	private JPanel panelDer;
	private JPanel panelDevolucion;
	
	private List<Reserva> listaReservas = new ArrayList<>();
	
	
	public JFrameReservas(JFramePrincipal mainFrame, List<Libro> libros) {
		super(libros,"reservas");
		this.mainFrame = mainFrame;
		
		this.inicializarPanelCentral();
		actualizarReservas();
		//aplicarTema();
	}
	
	public User getUser() {
		return user;
	}
	public void actualizarReservaEnBD(Reserva reserva) {
        try {
            user.actualizarReserva(reserva); 
            System.out.println("Reserva ID " + reserva.getLibro().getId() + " actualizada en la BD (Prolongación).");
        
            //refrescar tabla para que se actualicen los días restantes
            int fila = listaReservas.indexOf(reserva);
            if (fila!=-1) {
            	//hago que el jtable redibuje la fila
            	modeloTabla.fireTableRowsUpdated(fila, fila);
            }
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
						
						//para añadir complejidad: mostramos los días restantes mediante una barra de progreso
						JProgressBar barra = new JProgressBar(0, 28);
					    barra.setValue(Math.max(dias,0));
					    barra.setString(dias + " días");
					    barra.setStringPainted(true);
					    
					    //texto en negrita y negro
					    barra.setFont(barra.getFont().deriveFont(java.awt.Font.BOLD));
					    BasicProgressBarUI ui = new BasicProgressBarUI() {
					    	protected Color getSelectionForeground() {
					    		return Color.BLACK;
					    	}
					    	protected Color getSelectionBackground() {
					    		return Color.BLACK;
					    	}
					    };
					    barra.setUI(ui);
					    
					    //color de la barra de progreso según los días que quedan
					    if(dias >= 14) barra.setForeground(Color.GREEN); //quedan 2 semanas o más --> verde
					    else if(dias >= 7) barra.setForeground(Color.ORANGE); //quedan entre 1 y 2 semanas --> naranja
					    else barra.setForeground(Color.RED); //queda menos --> rojo
					    
					    //borde inferor para separar filas
					    barra.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2));
					    return barra;
					    
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
				celda.setBorder(new MatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
				return celda;
			}
		};
		tablaReservas.setDefaultRenderer(Object.class, miCellRenderer);
		
		// Renderizado específico para la columna título: portada centrada + título debajo
		tablaReservas.getColumnModel().getColumn(0).setCellRenderer(new TableCellRenderer() {
		    @Override
		    public Component getTableCellRendererComponent(JTable table, Object value,
		            boolean isSelected, boolean hasFocus, int row, int column) {

		        Reserva r = listaReservas.get(row);

		        // Panel principal vertical
		        JPanel panel = new JPanel();
		        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		        panel.setOpaque(true);
		        panel.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);

		        // Espacio superior
		        panel.add(Box.createRigidArea(new java.awt.Dimension(0, 5)));

		        // Escalar la portada según la altura de la fila
		        int filaAltura = table.getRowHeight();
		        int portadaAltura = (int)(filaAltura * 0.7); // ahora 70% de la altura
		        int portadaAncho = (int)(portadaAltura * 0.7); // proporción aproximada

		        // Escalar imagen
		        javax.swing.Icon icon = r.getLibro().getPortada();
		        if (icon instanceof javax.swing.ImageIcon) {
		            java.awt.Image img = ((javax.swing.ImageIcon) icon).getImage();
		            java.awt.Image imgEscalada = img.getScaledInstance(portadaAncho, portadaAltura, java.awt.Image.SCALE_SMOOTH);
		            icon = new javax.swing.ImageIcon(imgEscalada);
		        }

		        JLabel lblPortada = new JLabel(icon);
		        lblPortada.setAlignmentX(Component.CENTER_ALIGNMENT);
		        panel.add(lblPortada);

		        // Espacio extra para empujar título más abajo
		        panel.add(Box.createVerticalGlue());

		        // Título centrado debajo de la portada
		        JLabel lblTitulo = new JLabel(r.getLibro().getTitulo());
		        lblTitulo.setFont(fuenteMenu.deriveFont(12f));
		        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
		        panel.add(lblTitulo);

		        // Pequeño margen inferior
		        panel.add(Box.createRigidArea(new java.awt.Dimension(0, 5)));
		        panel.setBorder(new MatteBorder(0, 0, 1, 0, new Color(220, 220, 220))); //borde
		        return panel;
		    }
		});



		// Ajuste de altura de fila
		tablaReservas.setRowHeight(90);



		tablaReservas.setFillsViewportHeight(true); //para que llene el espacio
		tablaReservas.setRowHeight(90);
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
        btnDevolver.setForeground(Color.BLACK);
        btnHistorial = new JButton("Ver historial");
        btnHistorial.setBackground(new Color(230, 230, 250));
        btnHistorial.addActionListener(e -> {
            JDialogHistorialReservas dlg = new JDialogHistorialReservas(this, user);
            dlg.setVisible(true);
        });
        btnDevolver.setEnabled(false);//está deshabilitado hasta que se seleccione un préstamo
        
        panelDevolucion = new JPanel(new BorderLayout());
        panelDevolucion.setBackground(Color.WHITE);
        panelDevolucion.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelDevolucion.setOpaque(true);

        JPanel panelBotones = new JPanel(new GridLayout(1,2,10,0));
        panelBotones.setBackground(Color.WHITE);
        panelBotones.add(btnDevolver);
        panelBotones.add(btnHistorial);
        panelDevolucion.add(panelBotones, BorderLayout.CENTER);
        
        
           
        
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
        				
        				//marcar como libro devuelto en la base de datos
        				ReservaDAO reservaDAO = new ReservaDAO();
        				try {
							reservaDAO.marcarComoDevuelto(seleccionada);
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
        				
        				//quitar de la lista de reservas
        				listaReservas.remove(seleccionada);
        				//quitar de la lista del user
        				user.getReservas().remove(seleccionada);
        				
        				//volvemos a añadir el libro a la biblioteca
        				// (vuelve a estar disponible)
        				if (!libros.contains(seleccionada.getLibro())) {
        					libros.add(seleccionada.getLibro());
        				}
        				
        				//actualizar tabla de reservas
        				actualizarReservas();
        				        				
        				//actualizar paneles de populares y explorar
        				if (Navigator.inicio != null) {
        					Navigator.inicio.refrescarTopLibros();
        				}
        				if (Navigator.explorar !=null) {
        					Navigator.explorar.filtrarLibros();
        				}
        				
        				dialogSimulacion.dispose(); //cerrar diálogo al terminar
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
		for (Reserva r : reservas) {
		    if (!r.isDevuelto()) {  // <-- solo las activas
		        listaReservas.add(r);
		    }
		}
		
        
        CardLayout cl = (CardLayout) (panelTableContainer.getLayout());

        if (!listaReservas.isEmpty()) {
        	modeloTabla.setRowCount(listaReservas.size());
        	cl.show(panelTableContainer, "TABLA");
        }else {
        	cl.show(panelTableContainer, "MENSAJE");
        }
        
        //limpiar la selección y deshabilitar el botón de devolver
        tablaReservas.clearSelection();
        btnDevolver.setEnabled(false);
        
		this.revalidate();
		this.repaint();
	}
}

