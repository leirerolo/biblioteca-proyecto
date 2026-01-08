package recursividad;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.List;
import domain.Libro;

public class RecomendadorLibros extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private List<Libro> todosLibros; // lista completa de libros de la biblioteca
    private Map<String, JButton> botonesGeneros = new HashMap<>();
    private Set<String> generosSeleccionados = new HashSet<>();
    private JTextField txtCantidad;
    private JTextArea areaResultados;

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
        JPanel panelGeneros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelGeneros.setBorder(BorderFactory.createTitledBorder("Selecciona los géneros"));

        // Detectamos todos los géneros disponibles
        Set<String> todosGeneros = new TreeSet<>();
        for (Libro l : todosLibros) todosGeneros.add(l.getGenero().toString());

        // Creamos botones para cada género
        Color[] colores = {new Color(200,230,250), new Color(230,200,250), new Color(250,230,200)};
        int i = 0;
        for (String genero : todosGeneros) {
            JButton btn = new JButton(genero);
            btn.setBackground(colores[i % colores.length]);
            btn.setOpaque(true);
            btn.setBorderPainted(false);
            btn.addActionListener(e -> {
                if (generosSeleccionados.contains(genero)) {
                    generosSeleccionados.remove(genero);
                    btn.setBorderPainted(false);
                } else {
                    generosSeleccionados.add(genero);
                    btn.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
                }
            });
            botonesGeneros.put(genero, btn);
            panelGeneros.add(btn);
            i++;
        }

        // Panel para cantidad
        JPanel panelCantidad = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelCantidad.add(new JLabel("Cantidad de libros por combinación: "));
        txtCantidad = new JTextField(3);
        panelCantidad.add(txtCantidad);

        // Botón generar
        JButton btnGenerar = new JButton("Generar combinaciones");
        btnGenerar.addActionListener(this::generarCombinaciones);

        // Área de resultados
        areaResultados = new JTextArea();
        areaResultados.setEditable(false);
        JScrollPane scroll = new JScrollPane(areaResultados);

        // Layout
        JPanel topPanel = new JPanel(new GridLayout(2,1));
        topPanel.add(panelGeneros);
        topPanel.add(panelCantidad);

        this.add(topPanel, BorderLayout.NORTH);
        this.add(btnGenerar, BorderLayout.CENTER);
        this.add(scroll, BorderLayout.SOUTH);
    }

    private void generarCombinaciones(ActionEvent e) {
        areaResultados.setText(""); // limpiar resultados
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
            if (generosSeleccionados.contains(l.getGenero().toString())) librosFiltrados.add(l);
        }

        // Lista para almacenar resultados
        List<List<Libro>> resultados = new ArrayList<>();

        // Llamada recursiva
        generarRecursivo(librosFiltrados, new ArrayList<>(), resultados, cantidad, 0);

        // Mostrar resultados
        for (List<Libro> combo : resultados) {
            areaResultados.append(combo.toString() + "\n");
        }

        JOptionPane.showMessageDialog(this, "Se generaron " + resultados.size() + " combinaciones");
    }

    private void generarRecursivo(List<Libro> libros, List<Libro> actual, List<List<Libro>> resultados, int tamaño, int index) {
        if (actual.size() == tamaño) {
            // Verificar que hay al menos un libro de cada género seleccionado
            Set<String> generosEnCombo = new HashSet<>();
            for (Libro l : actual) generosEnCombo.add(l.getGenero().toString());
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
