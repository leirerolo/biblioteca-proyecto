package gui;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.prefs.Preferences;

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
    private Theme currentTheme = Theme.LIGHT;
    private JPanel panelBotones;
    private JLabel titulo;

    
	private static final Preferences prefs = Preferences.userRoot().node("biblioteca");
	protected static boolean darkMode = prefs.getBoolean("darkMode", false);

    public PanelGestionLibros(List<Libro> libros) {
        this.libros = libros;
        setLayout(new BorderLayout(10,10));
        Theme initialTheme = Theme.LIGHT; 
        
        setBackground(initialTheme.backgroundMain);
        setBorder(new EmptyBorder(10,10,10,10));

        titulo = new JLabel("Gestión de Libros", JLabel.CENTER);
        titulo.setFont(new Font("Comic Sans MS", Font.BOLD, 22));
        titulo.setForeground(Color.BLUE);
        add(titulo, BorderLayout.NORTH);

        modeloLibros = new DefaultListModel<>();
        listaLibros = new JList<>(modeloLibros);
        listaLibros.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaLibros.setCellRenderer(new LibroCellRenderer(initialTheme));

        JScrollPane scroll = new JScrollPane(listaLibros);
        add(scroll, BorderLayout.CENTER);

         panelBotones = new JPanel(new FlowLayout());
        panelBotones.setBackground(initialTheme.backgroundMain);

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
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Añadir libro", true);
        dialog.setSize(500, 350);
        dialog.setLayout(new GridBagLayout());
        dialog.setLocationRelativeTo(this);
        
        dialog.getContentPane().setBackground(currentTheme.backgroundMain);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Componentes
        JLabel lblTitulo = new JLabel("Título:");
        JTextField tfTitulo = new JTextField(20);
        JLabel lblAutor = new JLabel("Autor:");
        JTextField tfAutor = new JTextField(20);
        JLabel lblGenero = new JLabel("Género:");
        JComboBox<Genero> jcomboGenero = new JComboBox<>(Genero.values());
        JLabel lblPortada = new JLabel("Portada:");
        JTextField tfPortada = new JTextField();
        tfPortada.setEditable(false);
        JButton btnExaminar = new JButton("Examinar");
        JButton btnGuardar = new JButton("Guardar");

        // Aplicar theme
        Component[] components = {lblTitulo, lblAutor, lblGenero, lblPortada, tfTitulo, tfAutor, tfPortada, jcomboGenero, btnExaminar, btnGuardar};
        for (Component c : components) {
            c.setForeground(currentTheme.textColor);
            if (c instanceof JTextField || c instanceof JComboBox || c instanceof JButton) {
                c.setBackground(currentTheme.backgroundPanel);
                // Optional: add a border for dark mode visibility
                if (currentTheme == Theme.DARK && c instanceof JTextField) {
                    ((JTextField)c).setCaretColor(Color.WHITE); 
                    ((JTextField)c).setBorder(BorderFactory.createLineBorder(currentTheme.textColor, 1));
                }
            }
        }

       // LAYOUT
        gbc.gridx = 0; gbc.gridy = 0; dialog.add(lblTitulo, gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 2; dialog.add(tfTitulo, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; dialog.add(lblAutor, gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.gridwidth = 2; dialog.add(tfAutor, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1; dialog.add(lblPortada, gbc);
        gbc.gridx = 1; gbc.gridy = 2; dialog.add(tfPortada, gbc);
        gbc.gridx = 2; gbc.gridy = 2; dialog.add(btnExaminar, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1; dialog.add(lblGenero, gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.gridwidth = 2; dialog.add(jcomboGenero, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        dialog.add(btnGuardar, gbc);

       
        btnExaminar.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                tfPortada.setText(fc.getSelectedFile().getAbsolutePath());
            }
        });

        btnGuardar.addActionListener(e -> {
        	try {
                String titulo = tfTitulo.getText().trim();
                String autor = tfAutor.getText().trim();
                String rutaPortada = tfPortada.getText().trim();
                domain.Genero genero = (domain.Genero) jcomboGenero.getSelectedItem();
                
                if (titulo.isEmpty() || autor.isEmpty() || rutaPortada.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Todos los campos son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                ImageIcon portada = new ImageIcon(rutaPortada);
                domain.Libro nuevo = new domain.Libro(titulo, autor, portada, 0, rutaPortada, genero);

               
                libros.add(nuevo); 
                
                new db.LibroDAO().insertaLibro(nuevo); 

                refrescarLista(); 
                
                dialog.dispose();
                
                JOptionPane.showMessageDialog(this, "Libro añadido con éxito: " + titulo);
                
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Error al añadir libro: " + ex.getMessage(), "Error DB", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.setVisible(true);
    }

    public void setTheme(Theme theme) {
        this.currentTheme = theme; 
        setBackground(theme.backgroundMain);
        
        if (panelBotones != null) {
            panelBotones.setBackground(theme.backgroundMain);
        }

        listaLibros.setBackground(theme.backgroundPanel);
        listaLibros.setForeground(theme.textColor);

        btnAñadir.setBackground(theme.backgroundPanel);
        btnAñadir.setForeground(theme.textColor);
        //btnEliminar.setBackground(theme.backgroundPanel);
        btnEliminar.setForeground(theme.textColor);

        for (Component c : getComponents()) {
        	if (c instanceof JLabel lbl) {
        	    if (lbl == titulo) {
        	        if (theme == Theme.DARK) {
        	            lbl.setForeground(new Color(144, 213, 255));
        	        } else {
        	            lbl.setForeground(Color.BLUE); 
        	        }
        	    } else {
        	        lbl.setForeground(theme.textColor);
        	    }
        	}

        }
        
        if (darkMode) { 
        	btnEliminar.setBackground(new Color(120, 80, 180)); 
        	btnEliminar.setForeground(Color.BLACK);
        } else { 
        	btnEliminar.setBackground(new Color(200, 200, 200)); 
        	btnEliminar.setForeground(Color.black); 
        }

        listaLibros.setCellRenderer(new LibroCellRenderer(theme));

        revalidate();
        repaint();
    }



    // Renderer para mostrar título + portada en la lista
    private static class LibroCellRenderer extends JPanel implements ListCellRenderer<Libro> {
		private static final long serialVersionUID = 1L;
		
		private JLabel lblPortada = new JLabel();
        private JLabel lblTitulo = new JLabel();
        private Theme theme;


        public LibroCellRenderer(Theme theme) {
        	this.theme= theme;
            setLayout(new BorderLayout(5,5));
            lblTitulo.setForeground(theme.textColor);
            add(lblPortada, BorderLayout.WEST);
            add(lblTitulo, BorderLayout.CENTER);
            setBorder(new EmptyBorder(5,5,5,5));
            setOpaque(true);
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

            if (isSelected) {
            	setBackground(list.getSelectionBackground()); 
                lblTitulo.setForeground(list.getSelectionForeground());
            } else {
                setBackground(theme.backgroundPanel);
                lblTitulo.setForeground(theme.textColor);
            }

            return this;
        }
    }
}