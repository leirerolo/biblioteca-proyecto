package recursividad;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.List;

import domain.Genero;
import domain.Libro;

public class RecomendadorLibros extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private List<Libro> todosLibros; // lista completa de libros de la biblioteca
    private Map<Genero, JButton> botonesGeneros = new HashMap<>();
    private Set<Genero> generosSeleccionados = new HashSet<>();
    private JTextField txtCantidad;
    private JPanel panelResults;
    private JScrollPane scrollResults;

    public RecomendadorLibros(List<Libro> todosLibros) {
        super("Recomendador de Libros");
        this.todosLibros = todosLibros;
        initGUI();
    }

    private void initGUI() {
        this.setSize(600, 500);
        this.setLayout(new BorderLayout());
        this.setLocationRelativeTo(null);

        // Panel de géneros
        JPanel panelGeneros = new JPanel();
        int columnas = 4;
        panelGeneros.setLayout(new GridLayout(0,columnas,10,10));
        panelGeneros.setBorder(BorderFactory.createTitledBorder("Selecciona los géneros"));
        panelGeneros.setBackground(Color.WHITE);
        panelGeneros.setOpaque(true);
        
        // Detectamos todos los géneros disponibles
        Set<Genero> todosGeneros = new TreeSet<>(Comparator.comparing(Genero::getNombre, String.CASE_INSENSITIVE_ORDER));
        //el comparator es para indicar que compare por nombre los géneros,
        //porque los enums por defecto comparan por orden (ordinal)
        for (Libro l : todosLibros) {
        	todosGeneros.add(l.getGenero());
        }

        // Creamos botones para cada género
        Color[] colores = {new Color(200,230,250), new Color(230,200,250), new Color(250,230,200)};
        int i = 0;
        for (Genero genero : todosGeneros) {
        	final int colorIndex = i; // variable final para la lambda
            JButton btn = new JButton(genero.getNombre());
            btn.setBackground(colores[colorIndex % colores.length]);
            btn.setOpaque(true);
            btn.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
            
            btn.addActionListener(e -> {
                if (generosSeleccionados.contains(genero)) {
                    generosSeleccionados.remove(genero);
                    btn.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
                    btn.setBackground(colores[colorIndex % colores.length]);
                } else {
                    generosSeleccionados.add(genero);
                    btn.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
                    btn.setBackground(colores[colorIndex % colores.length].darker());
                }
            });
            botonesGeneros.put(genero, btn);
            panelGeneros.add(btn);
            i++;
        }

        // Panel para cantidad
        JPanel panelCantidad = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelCantidad.setBackground(Color.WHITE);
        panelCantidad.setOpaque(true);
        panelCantidad.add(new JLabel("Cantidad de libros por combinación: "));
        txtCantidad = new JTextField(3);
        panelCantidad.add(txtCantidad);

        // Panel superior: géneros + cantidad + botón
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.add(panelGeneros);
        topPanel.add(Box.createRigidArea(new Dimension(0,5)));
        topPanel.add(panelCantidad);
        topPanel.add(Box.createRigidArea(new Dimension(0,5)));
        
        // Botón generar
        JButton btnGenerar = new JButton("Generar combinaciones");
        btnGenerar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnGenerar.addActionListener(this::generarCombinaciones);
        topPanel.add(btnGenerar);
        topPanel.add(Box.createRigidArea(new Dimension(0,5)));
        
        this.add(topPanel, BorderLayout.NORTH);
                
        
        // Panel de resultados
        panelResults = new JPanel();
        panelResults.setLayout(new BoxLayout(panelResults, BoxLayout.Y_AXIS));
        panelResults.setBackground(Color.WHITE);
        
        scrollResults = new JScrollPane(panelResults);
        scrollResults.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollResults.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollResults.getVerticalScrollBar().setUnitIncrement(16);
        scrollResults.setPreferredSize(new Dimension(780, 300));
        
        this.add(scrollResults, BorderLayout.CENTER);
    }

    private void generarCombinaciones(ActionEvent e) {
        panelResults.removeAll(); // limpiar resultados
        int cantidad;
        try {
            cantidad = Integer.parseInt(txtCantidad.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Cantidad inválida", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (generosSeleccionados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecciona al menos un género", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Filtramos libros por los géneros seleccionados
        List<Libro> librosFiltrados = new ArrayList<>();
        for (Libro l : todosLibros) {
            if (generosSeleccionados.contains(l.getGenero())) librosFiltrados.add(l);
        }

        // Lista para almacenar resultados
        List<List<Libro>> resultados = new ArrayList<>();

        // Llamada recursiva
        generarRecursivo(librosFiltrados, new ArrayList<>(), resultados, cantidad, 0);

        
        // ************** MOSTRAR RESULTADOS *******************
        //Cantidad de combinaciones
        JLabel lblCantidad = new JLabel(resultados.size() + " combinaciones generadas");
        lblCantidad.setFont(new Font("Arial", Font.BOLD, 14));
        panelResults.add(lblCantidad);
        panelResults.add(Box.createRigidArea(new Dimension(0,10)));
        
        //Paneles de combinaciones
        for (List<Libro> combo : resultados) {
        	// Contenedor para el combo, con FlowLayout para que se ajuste al contenido
		    JPanel panelComboContenedor = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
		    panelComboContenedor.setBackground(new Color(245,245,245));
            
            for (Libro l : combo) {
            	JPanel panelLibro = new JPanel();
            	panelLibro.setLayout(new BoxLayout(panelLibro, BoxLayout.Y_AXIS));
            	panelLibro.setBackground(Color.WHITE);
            	panelLibro.setOpaque(true);
            	panelLibro.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
            	
            	//tamaño fijo para el panel del libro
            	panelLibro.setPreferredSize(new Dimension(120,160));
            	panelLibro.setMaximumSize(new Dimension(120,160));
            	panelLibro.setMinimumSize(new Dimension(120,160));
            	
            	//añadir portada al panel
            	if (l.getPortada()!=null) {
            		Image img = l.getPortada().getImage().getScaledInstance(80, 120, Image.SCALE_SMOOTH);
            		JLabel lblPortada = new JLabel(new ImageIcon(img));
            		lblPortada.setAlignmentX(Component.CENTER_ALIGNMENT);
            		panelLibro.add(lblPortada);
            	}
            	
            	JLabel lblTitulo = new JLabel("<html><center>" + l.getTitulo() + "</center></html>");
            	lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
            	lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
            	panelLibro.add(lblTitulo);
            	
            	panelComboContenedor.add(panelLibro);
            	
            }
            
            //scroll horizontal por si hay muchos libros en la combinación
            JScrollPane scrollCombo = new JScrollPane(panelComboContenedor,
            		JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            scrollCombo.setPreferredSize(new Dimension(750, 160));
            scrollCombo.getHorizontalScrollBar().setUnitIncrement(16);
            
            panelResults.add(scrollCombo);
            panelResults.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        panelResults.revalidate();
        panelResults.repaint();
    }

    private void generarRecursivo(List<Libro> libros, List<Libro> actual, List<List<Libro>> resultados, int tamaño, int index) {
        if (actual.size() == tamaño) {
            // Verificar que hay al menos un libro de cada género seleccionado
            Set<Genero> generosEnCombo = new HashSet<>();
            for (Libro l : actual) generosEnCombo.add(l.getGenero());
            if (generosEnCombo.containsAll(generosSeleccionados)) {
                resultados.add(new ArrayList<>(actual));
            }
            return;
        }

        for (int i = index; i < libros.size(); i++) {
            actual.add(libros.get(i));
            generarRecursivo(libros, actual, resultados, tamaño, i + 1); // recursión
            actual.remove(actual.size() - 1); // backtracking
        }
    }
}
