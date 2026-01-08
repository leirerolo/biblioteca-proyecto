package gui;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import db.LibroDAO;
import db.ReservaDAO;
import domain.Genero;
import domain.Libro;

public class PanelGestionLibros extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private List<Libro> libros;
    private DefaultListModel<Libro> modeloLibros;
    private JList<Libro> listaLibros;
    private JButton btnEliminar, btnAñadir;

    public PanelGestionLibros(List<Libro> libros) {
        this.libros = libros;
        setLayout(new BorderLayout(10,10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(10,10,10,10));

        JLabel titulo = new JLabel("Gestión de Libros", JLabel.CENTER);
        titulo.setFont(new Font("Comic Sans MS", Font.BOLD, 22));
        add(titulo, BorderLayout.NORTH);

        modeloLibros = new DefaultListModel<>();
        listaLibros = new JList<>(modeloLibros);
        listaLibros.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaLibros.setCellRenderer(new LibroCellRenderer());

        JScrollPane scroll = new JScrollPane(listaLibros);
        add(scroll, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout());
        panelBotones.setBackground(Color.WHITE);

        btnEliminar = new JButton("Eliminar libro");
        btnAñadir = new JButton("Añadir libro");

        btnEliminar.setEnabled(false);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnAñadir);
        add(panelBotones, BorderLayout.SOUTH);

        // actualizar lista de libros disponibles
        refrescarLista();

        // habilitar botón eliminar al seleccionar
        listaLibros.addListSelectionListener(e -> {
            btnEliminar.setEnabled(listaLibros.getSelectedIndex() != -1);
        });

        // acción de eliminar
        btnEliminar.addActionListener(e -> eliminarLibro());

        // acción de añadir
        btnAñadir.addActionListener(e -> añadirLibro());
    }

    private void refrescarLista() {
        try {
            LibroDAO dao = new LibroDAO();
            List<Libro> disponibles = dao.getLibrosDisponibles(libros);
            modeloLibros.clear();
            for (Libro l : disponibles) {
                modeloLibros.addElement(l);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar libros: " + e.getMessage());
        }
    }

    private void eliminarLibro() {
        Libro seleccionado = listaLibros.getSelectedValue();
        if (seleccionado == null) return;

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Seguro que quieres eliminar el libro \"" + seleccionado.getTitulo() + "\"?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // eliminar libro de la lista principal y base de datos
                libros.remove(seleccionado);
                new LibroDAO().eliminarLibro(seleccionado); // necesitas implementar este método en DAO
                refrescarLista();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error al eliminar libro: " + e.getMessage());
            }
        }
    }

    private void añadirLibro() {
        // diálogo para añadir libro
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Añadir libro", true);
        dialog.setSize(500, 300); // más ancho y alto
        dialog.setLayout(new GridBagLayout());
        dialog.setLocationRelativeTo(this);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel lblTitulo = new JLabel("Título:");
        JTextField tfTitulo = new JTextField();
        JLabel lblAutor = new JLabel("Autor:");
        JTextField tfAutor = new JTextField();
        JLabel lblGenero = new JLabel("Género:");
        JComboBox<Genero> jcomboGenero = new JComboBox<>(Genero.values());
        JLabel lblPortada = new JLabel("Portada:");
        JTextField tfPortada = new JTextField();
        tfPortada.setEditable(false);
        JButton btnExaminar = new JButton("Examinar");
        

        // Examinar
        btnExaminar.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            int seleccion = fc.showOpenDialog(dialog);
            if (seleccion == JFileChooser.APPROVE_OPTION) {
                tfPortada.setText(fc.getSelectedFile().getAbsolutePath());
            }
        });

        // FILA TÍTULO
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        dialog.add(lblTitulo, gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 2;
        dialog.add(tfTitulo, gbc);

        // FILA AUTOR
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        dialog.add(lblAutor, gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.gridwidth = 2;
        dialog.add(tfAutor, gbc);

        //FILA GÉNERO
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1;
        dialog.add(lblGenero, gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.gridwidth = 2;
        dialog.add(jcomboGenero, gbc);
        
        // FILA PORTADA
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        dialog.add(lblPortada, gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 1;
        dialog.add(tfPortada, gbc);
        gbc.gridx = 2; gbc.gridy = 2; gbc.gridwidth = 1;
        dialog.add(btnExaminar, gbc);

        // BOTÓN GUARDAR
        JButton btnGuardar = new JButton("Guardar");
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        dialog.add(btnGuardar, gbc);

        btnGuardar.addActionListener(e -> {
            try {
                String titulo = tfTitulo.getText().trim();
                String autor = tfAutor.getText().trim();
                String rutaPortada = tfPortada.getText().trim();
                Genero genero = (Genero) jcomboGenero.getSelectedItem();
                
                if (titulo.isEmpty() || autor.isEmpty() || rutaPortada.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Todos los campos son obligatorios.");
                    return;
                }

                ImageIcon portada = new ImageIcon(rutaPortada);
                Libro nuevo = new Libro(titulo, autor, portada, 0, rutaPortada, genero);

                libros.add(nuevo);
                new LibroDAO().insertaLibro(nuevo);
                refrescarLista();
                dialog.dispose();
                
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Error al añadir libro: " + ex.getMessage());
            }
        });

        dialog.setVisible(true);
    }



    // Renderer para mostrar título + portada en la lista
    private static class LibroCellRenderer extends JPanel implements ListCellRenderer<Libro> {
		private static final long serialVersionUID = 1L;
		
		private JLabel lblPortada = new JLabel();
        private JLabel lblTitulo = new JLabel();

        public LibroCellRenderer() {
            setLayout(new BorderLayout(5,5));
            add(lblPortada, BorderLayout.WEST);
            add(lblTitulo, BorderLayout.CENTER);
            setBorder(new EmptyBorder(5,5,5,5));
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Libro> list, Libro value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            lblTitulo.setText(value.getTitulo() + " - " + value.getAutor());

            if (value.getPortada() != null) {
                lblPortada.setIcon(new ImageIcon(value.getPortada().getImage().getScaledInstance(50,70, Image.SCALE_SMOOTH)));
            } else {
                lblPortada.setIcon(null);
            }

            setBackground(isSelected ? Color.LIGHT_GRAY : Color.WHITE);
            return this;
        }
    }
}
