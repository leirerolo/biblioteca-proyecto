package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import domain.Libro;

//hereda de inicio para heredar su cabecera
public class JFrameExplorar extends JFramePrincipal {
	private static final long serialVersionUID = 1L;
	private JTextField txtFiltro;
	private JPanel panelLibros;
	private JComboBox<String> opciones;
	
	public JFrameExplorar(List<Libro> libros) {
		super(libros, "explorar");
		this.libros = libros;
		this.inicializarPanelSuperior(); //hereda la cabecera del frame principal
		this.inicializarPanelCentral();
	}
	
	private void inicializarPanelCentral() {
		JPanel mainPanel = new JPanel(new BorderLayout());
		
		// --------- Cabecera (filtro texto + opciones filtro)
		JPanel cabecera = new JPanel(new BorderLayout());
		txtFiltro = new JTextField("Buscar por título...");
		txtFiltro.setForeground(Color.GRAY);
		
		//al hacer click o escribir en el filtro
		txtFiltro.addFocusListener(new java.awt.event.FocusAdapter() {
		    @Override
		    public void focusGained(java.awt.event.FocusEvent e) {
		        if (txtFiltro.getText().equals("Buscar por título...")) {
		            txtFiltro.setText("");
		            txtFiltro.setForeground(Color.BLACK);
		        }
		    }

		    @Override
		    public void focusLost(java.awt.event.FocusEvent e) {
		        if (txtFiltro.getText().isEmpty()) {
		            txtFiltro.setText("Buscar por título...");
		            txtFiltro.setForeground(Color.GRAY);
		        }
		    }
		});
		//document listener para el filtro de texto
		DocumentListener miTxtListener = new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				actualizarFiltro();
			}
			@Override
			public void removeUpdate(DocumentEvent e) {
				actualizarFiltro();
			}
			@Override
			public void changedUpdate(DocumentEvent e) {
			}
		};
		txtFiltro.getDocument().addDocumentListener(miTxtListener);
		opciones = new JComboBox<>(new String[]{"Ordenar", "Por autor", "Por valoración"});
		
		//listener para la selección
		opciones.addActionListener((e) -> {
			filtrarLibros();
		});
		cabecera.add(txtFiltro, BorderLayout.CENTER);
		cabecera.add(opciones, BorderLayout.EAST);
		mainPanel.add(cabecera, BorderLayout.NORTH);
		
		
		//----------- Centro: libros --------------
		panelLibros = new JPanel();
		panelLibros.setLayout(new BoxLayout(panelLibros, BoxLayout.Y_AXIS));
	    panelLibros.setBackground(Color.WHITE);
	    panelLibros.setOpaque(true);
	    panelLibros.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

	    //scroll para cuando haya libros
	    JScrollPane scrollPane = new JScrollPane(panelLibros, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
	            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	    scrollPane.getVerticalScrollBar().setUnitIncrement(16);

	    mainPanel.add(scrollPane, BorderLayout.CENTER);
	    this.add(mainPanel, BorderLayout.CENTER); // añadir al panel central

	    // Mostrar mensaje inicial o todos los libros
	    filtrarLibros();
	    
	}

	private void filtrarLibros() {
		
	    panelLibros.removeAll(); 
	    
	    String tit = txtFiltro.getText().trim().toLowerCase();
	    if (tit.equals("buscar por título...")) {
	        tit = "";
	    }
	    
	    String op = (String) opciones.getSelectedItem();
	    List<Libro> listaLibros = new ArrayList<>();
	    
	    // sin filtro - mostrar lista en orden default (fichero csv)
	    if (tit.isEmpty()) {
	    	listaLibros.addAll(libros);
	    } else {
	        for (Libro libro : libros) {
	            if (libro.getTitulo().toLowerCase().contains(tit)) {
	            	listaLibros.add(libro);
	            }
	        }
	    }
	    
	    // aplicar orden segun filtro
	    ordenarLista(listaLibros);
	    
	    //si libros coincide mostrar libros, si no hay libros mostrar mensaje "sin resultado"
	    if (listaLibros.isEmpty()) {
	        JLabel mensaje = new JLabel("No hay coincidencias");
	        mensaje.setHorizontalAlignment(JLabel.CENTER);
	        panelLibros.add(mensaje);
	    } else {
	    	
	    	Color noSeleccionado = Color.white;
	    	Color seleccionado = new Color(245, 245, 245);
	    	
	        for (Libro l : listaLibros) {
	            JPanel panelLibro = new JPanel(new BorderLayout());
	            panelLibro.setPreferredSize(new Dimension(800, 160));
	            panelLibro.setMaximumSize(new Dimension(800, 160));
	            panelLibro.setAlignmentX(Component.LEFT_ALIGNMENT);
	            
	            panelLibro.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

	    	    panelLibro.setBackground(noSeleccionado);
	            panelLibro.setOpaque(true);
	            
	            JLabel portada = new JLabel(l.getPortada(), JLabel.CENTER);
	            panelLibro.add(portada, BorderLayout.WEST);

	            JPanel panelInfo = new JPanel(new GridLayout(3, 1, 0, 10));

	            panelInfo.setBackground(noSeleccionado);
	            panelInfo.setOpaque(true);
	            
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
	            
	            panelLibro.addMouseListener(new MouseAdapter() {
	            	//info libro + reservas
	                @Override
	                public void mouseClicked(MouseEvent e) {
	                    JDialogLibro infoLibro = new JDialogLibro(JFrameExplorar.this, l);
	                    infoLibro.setVisible(true);
	                }

	                //al pasar raton
					@Override
					public void mouseEntered(MouseEvent e) {
						panelLibro.setBackground(seleccionado);
                        panelInfo.setBackground(seleccionado);
                        panelLibro.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
                        panelLibro.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 2));
					}

					@Override
					public void mouseExited(MouseEvent e) {
						panelLibro.setBackground(noSeleccionado);
                        panelInfo.setBackground(noSeleccionado);
                        panelLibro.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                        panelLibro.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
					}
	                
	            });

	            panelLibros.add(panelLibro);
	            
	        }
	        
	    }
	    
	    panelLibros.revalidate();
	    panelLibros.repaint();
	}
	
	
	private void ordenarLista(List<Libro> lista) {
		String op = (String) opciones.getSelectedItem();
		
		if(op.equals("Ordenar")) {
			lista.sort(Libro.COMPARADOR_TITULO);
		}else if (op.equals("Por autor")) {
			Collections.sort(lista);
		}else if (op.equals("Por valoración")) {
			Collections.sort(lista, new Libro());
		}
	}
	
	private void actualizarFiltro() {
		String texto = txtFiltro.getText();
		
		if (!texto.equals("Buscar por título...") && !texto.isEmpty()) {
			txtFiltro.setForeground(Color.BLACK);
		} else if (texto.isEmpty()) {
	        txtFiltro.setForeground(Color.GRAY);
		}
		
		filtrarLibros();
	}
}
