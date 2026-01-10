package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;



import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import utils.RecursiveSorter;
import utils.RecursiveSearch;



import db.ReservaDAO;
import db.FavoritoDAO;
import domain.Libro;
import domain.Reserva;

//hereda de inicio para heredar su cabecera
public class JFrameExplorar extends JFramePrincipal {
	private static final long serialVersionUID = 1L;
	private JTextField txtFiltro;
	private JPanel panelLibros;
	private JComboBox<String> opciones;
	private JCheckBox chkSoloFavoritos;
	
	public JFrameExplorar(List<Libro> libros) {
		super(libros, "explorar");
		this.libros = libros;
		this.inicializarPanelCentral();
		this.filtrarLibros();
		//aplicarTema();
	}
	
	private void inicializarPanelCentral() {
		JPanel mainPanel = new JPanel(new BorderLayout());
		
		// --------- Cabecera (filtro texto + opciones filtro)
		JPanel cabecera = new JPanel(new BorderLayout());
		txtFiltro = new JTextField("Buscar por título o autor...");
		txtFiltro.setForeground(Color.GRAY);
		
		//al hacer click o escribir en el filtro
		txtFiltro.addFocusListener(new java.awt.event.FocusAdapter() {
		    @Override
		    public void focusGained(java.awt.event.FocusEvent e) {
		        if (txtFiltro.getText().equals("Buscar por título o autor...")) {
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
		
		txtFiltro.addKeyListener(new KeyAdapter() {
		    @Override
		    public void keyPressed(KeyEvent e) {
		        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_E) {
		            int opcion = JOptionPane.showConfirmDialog(
		                JFrameExplorar.this,
		                "¿Seguro que quieres salir?",
		                "Confirmar salida",
		                JOptionPane.YES_NO_OPTION,
		                JOptionPane.QUESTION_MESSAGE
		            );
		            if (opcion == JOptionPane.YES_OPTION) {
		                System.exit(0);
		            }
		        } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
		        	buscarYMostrarLibroPorTituloExacto();
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
		chkSoloFavoritos = new JCheckBox("⭐ Solo favoritos");
		chkSoloFavoritos.setBackground(Color.WHITE);
		chkSoloFavoritos.addActionListener(e -> filtrarLibros());
		
		//listener para la selección
		opciones.addActionListener((e) -> {
			filtrarLibros();
		});
		cabecera.add(chkSoloFavoritos, BorderLayout.WEST);
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

	    
	    
	}

	public void filtrarLibros() {
		
	    panelLibros.removeAll(); 
	    
	    String tit = txtFiltro.getText().trim().toLowerCase();
	    if (tit.equals("buscar por título o autor...")) {
	        tit = "";
	    }
	    
	    List<Libro> listaLibros = new ArrayList<>();
	    
	    //cargo de la base de datos de reservas las que estén activas ahora
	    List<Reserva> reservasActivas = new ArrayList<>();
	    
	    try {
	    	ReservaDAO reservaDAO = new ReservaDAO();
	    	reservasActivas = reservaDAO.getTodasLasReservasActivas();
	   
	    } catch(SQLException e) {
	    	e.printStackTrace();
	    }
	    

	    // precargar favoritos si el usuario lo pide
	    Set<Integer> favoriteIds = new HashSet<>();
	    boolean soloFav = chkSoloFavoritos != null && chkSoloFavoritos.isSelected();
	    if (soloFav) {
	        try {
	            if (domain.User.isLoggedIn()) {
	                FavoritoDAO fdao = new FavoritoDAO();
	                for (Libro fl : fdao.getFavoritosByUser(domain.User.getLoggedIn())) {
	                    favoriteIds.add(fl.getId());
	                }
	            } else {
	                // si no hay sesión, no hay favoritos
	                soloFav = false;
	                if (chkSoloFavoritos != null) chkSoloFavoritos.setSelected(false);
	            }
	        } catch (SQLException ex) {
	            ex.printStackTrace();
	        }
	    }

	    // sin filtro - mostrar lista en orden por defecto 
	    for (Libro libro : libros) {
	    	boolean reservado = false;
	    	
	    	for (Reserva r : reservasActivas) {
	    		if (r.getLibro().getId() == libro.getId()) {
	    			reservado = true;
	    			break; //si está reservado, no se añade a la lista
	    		}
	    	}
	    	if (!reservado) {
	    		if (soloFav && !favoriteIds.contains(libro.getId())) {
	    			continue;
	    		}
	    		if (tit.isEmpty() || libro.getTitulo().toLowerCase().contains(tit) || libro.getAutor().toLowerCase().contains(tit)) {
	    			if (!listaLibros.contains(libro)) {
	    				listaLibros.add(libro);
	    			}
	    		}
	    	}
	    }
	    
	    // aplicar orden segun filtro
	    ordenarLista(listaLibros);
	    
	    //si libros coincide mostrar libros, si no hay libros mostrar mensaje "sin resultado"
	    if (listaLibros.isEmpty()) {
	    	JLabel mensaje = new JLabel("No hay coincidencias");
	    	mensaje.setAlignmentX(Component.CENTER_ALIGNMENT);   // centra en BoxLayout
	    	mensaje.setHorizontalAlignment(JLabel.CENTER);
	    	mensaje.setFont(new Font("SansSerif", Font.BOLD, 20));  // letra más grande

	    	mensaje.setForeground(JFramePrincipal.darkMode ? new Color(230,230,230) : Color.BLACK);

	    	panelLibros.add(mensaje);

	    
	    } else {
	    	
	    	Color noSeleccionado = JFramePrincipal.darkMode ? new Color(40,40,40) : Color.WHITE;
	    	Color seleccionado    = JFramePrincipal.darkMode ? new Color(60,60,60) : new Color(245,245,245);

	    	
	        for (Libro l : listaLibros) {
	            JPanel panelLibro = new JPanel(new BorderLayout());
	            panelLibro.setPreferredSize(new Dimension(800, 160));
	            panelLibro.setMaximumSize(new Dimension(800, 160));
	            panelLibro.setAlignmentX(Component.LEFT_ALIGNMENT);
	            panelLibro.setOpaque(true);
	            
	            Border bordeNormal = new MatteBorder(1, 1, 1, 1, new Color(180, 180, 180)); // gris claro
	            Border bordeHover;

	            if (JFramePrincipal.darkMode) {
	                bordeHover = new MatteBorder(2, 2, 2, 2, Color.WHITE); // hover blanco en dark mode
	            } else {
	                bordeHover = new MatteBorder(2, 2, 2, 2, new Color(80, 80, 80)); // hover gris oscuro en light mode
	            }

	            panelLibro.setBorder(bordeNormal);
	            
	            
	            //panelLibro.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

	    	    panelLibro.setBackground(noSeleccionado);
	            panelLibro.setOpaque(true);
	            
	            JLabel portada = new JLabel(l.getPortada(), JLabel.CENTER);
	            panelLibro.add(portada, BorderLayout.WEST);

	            JPanel panelInfo = new JPanel(new GridLayout(3, 1, 0, 10));

	            panelInfo.setBackground(noSeleccionado);
	            panelInfo.setOpaque(true);
	            portada.setOpaque(true);
	            
	            JLabel titulo = new JLabel(l.getTitulo(), JLabel.LEFT);
	            titulo.setFont(fuenteTitulo);
	            titulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	            panelInfo.add(titulo);
	            
	            JLabel autor = new JLabel(l.getAutor(), JLabel.LEFT);
	            autor.setFont(fuenteMenu);
	            autor.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	            panelInfo.add(autor);

	            JLabel valoracion = new JLabel("★ " + String.format("%.2f", l.getValoracion()), JLabel.LEFT);
	            valoracion.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	            panelInfo.add(valoracion);

	            panelLibro.add(panelInfo, BorderLayout.CENTER);
	            
//	            MouseAdapter hoverListener = new MouseAdapter() {
//	            	//al hacer click, se abre el diálogo de la info del libro
//	                @Override
//	                public void mouseClicked(MouseEvent e) {
//	                    JDialogLibro infoLibro = new JDialogLibro(JFrameExplorar.this, l);
//	                    infoLibro.setVisible(true);
//	                }
//
//	                //al pasar ratón, cambia a formato selección
//	                @Override
//					public void mouseEntered(MouseEvent e) {
//	                	//System.out.println("Mouse dentro de: " + l.getTitulo());
//						panelLibro.setBackground(seleccionado);
//                        panelLibro.setCursor(new Cursor(Cursor.HAND_CURSOR));
//                        panelLibro.repaint();
//                        //panelLibro.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 2));
//					}
//
//					@Override
//					public void mouseExited(MouseEvent e) {
//						panelLibro.setBackground(noSeleccionado);
//                        panelLibro.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
//                        panelLibro.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
//                        panelLibro.repaint();
//					}
//	            };
	            
	            MouseAdapter listener = new MouseAdapter() {

	                @Override
	                public void mouseEntered(MouseEvent e) {
	                    panelLibro.setBorder(bordeHover);
	                    panelLibro.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	                }

	                @Override
	                public void mouseExited(MouseEvent e) {
	                	panelLibro.setBorder(bordeNormal);
	                	panelLibro.setCursor(Cursor.getDefaultCursor());
	                }

	                @Override
	                public void mouseClicked(MouseEvent e) {
	                    new JDialogLibro(JFrameExplorar.this, l).setVisible(true);
	                }
	            };
	            
//	            panelLibro.addMouseListener(hoverListener);
//	            portada.addMouseListener(hoverListener);
//	            panelInfo.addMouseListener(hoverListener);
	            
	            addMouseListenerRecursively(panelLibro, listener);
	            
	            panelLibros.add(panelLibro);
	        }
	    }
	    panelLibros.revalidate();
	    panelLibros.repaint();
	    
	    this.revalidate();
	    this.repaint();
	    this.aplicarTema();

	}
	
	/**
	 * Búsqueda EXACTA por título usando recursividad (búsqueda binaria).
	 *
	 * Uso: escribe el título y pulsa ENTER.
	 */
	private void buscarYMostrarLibroPorTituloExacto() {
		if (txtFiltro == null) return;
		String query = txtFiltro.getText();
		if (query == null) return;
		query = query.trim();
		if (query.isEmpty()) return;
		if (query.equalsIgnoreCase("Buscar por título o autor...") || query.equalsIgnoreCase("Buscar por título...")) {
			return;
		}
		
		// Copia + ordenación por título (con MergeSort recursivo)
		List<Libro> copia = new ArrayList<>(libros);
		RecursiveSorter.mergeSort(copia, Libro.COMPARADOR_TITULO);
		
		// Búsqueda binaria recursiva
		Libro encontrado = RecursiveSearch.binarySearchByTitle(copia, query);
		if (encontrado != null) {
			new JDialogLibro(this, encontrado).setVisible(true);
		} else {
			java.awt.Toolkit.getDefaultToolkit().beep();
			JOptionPane.showMessageDialog(
				this,
				"No existe un libro con el título exacto:\n\"" + query + "\"\n\nTip: escribe el título exactamente y pulsa ENTER.",
				"No encontrado",
				JOptionPane.INFORMATION_MESSAGE
			);
		}
	}
	
	
	private void ordenarLista(List<Libro> lista) {
		String op = (String) opciones.getSelectedItem();
		
		// Ordenación hecha con recursividad (MergeSort) -> ver utils/RecursiveSorter
		if (op.equals("Ordenar")) {
			// Por título
			RecursiveSorter.mergeSort(lista, Libro.COMPARADOR_TITULO);
		} else if (op.equals("Por autor")) {
			// Por autor (Libro implements Comparable -> autor)
			RecursiveSorter.mergeSort(lista, (l1, l2) -> l1.compareTo(l2));
		} else if (op.equals("Por valoración")) {
			// Por valoración (descendente)
			RecursiveSorter.mergeSort(lista, (l1, l2) -> Double.compare(l2.getValoracion(), l1.getValoracion()));
		}
	}
	
	private void actualizarFiltro() {
		String texto = txtFiltro.getText();
		
		if (!texto.equals("Buscar por título o autor...") && !texto.isEmpty()) {
			txtFiltro.setForeground(Color.BLACK);
		} else if (texto.isEmpty()) {
	        txtFiltro.setForeground(Color.GRAY);
		}
		
		filtrarLibros();
	}
	
    private void addMouseListenerRecursively(Component comp, MouseAdapter listener) {
        comp.addMouseListener(listener);
        if (comp instanceof Container cont) {
            for (Component child : cont.getComponents()) {
                addMouseListenerRecursively(child, listener);
            }
        }
    }
}

