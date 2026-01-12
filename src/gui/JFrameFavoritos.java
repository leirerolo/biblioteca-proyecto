package gui;

import db.FavoritoDAO;
import domain.Libro;
import domain.User;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Ventana simple para mostrar y gestionar los libros favoritos del usuario.
 */
public class JFrameFavoritos extends JFrame {

    private static final long serialVersionUID = 1L;

    private final User user;
    private final FavoritoDAO favoritoDAO = new FavoritoDAO();

    private JPanel panelLista;
    private JLabel lblTitulo;

    private Font fuenteTitulo = new Font("Comic Sans MS", Font.BOLD, 22);
    private Font fuenteMenu = new Font("Comic Sans MS", Font.BOLD, 16);

    public JFrameFavoritos(User user) {
        super("Mis favoritos");
        this.user = user;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(650, 700);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        //root.setBackground(Color.WHITE);

        // Cabecera
        JPanel header = new JPanel(new BorderLayout());
        //header.setBackground(new Color(0, 160, 220));
        header.setBorder(new MatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        lblTitulo = new JLabel("Mis favoritos");
        lblTitulo.setFont(fuenteTitulo);
        //lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        JButton btnRefrescar = new JButton("Refrescar");
        btnRefrescar.addActionListener(e -> {
        	cargarYRender();
        	applyTheme(JFramePrincipal.darkMode);
        });

        header.add(lblTitulo, BorderLayout.WEST);
        header.add(btnRefrescar, BorderLayout.EAST);

        root.add(header, BorderLayout.NORTH);

        // Lista
        panelLista = new JPanel();
        panelLista.setLayout(new BoxLayout(panelLista, BoxLayout.Y_AXIS));
        //panelLista.setBackground(Color.WHITE);
        panelLista.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        //para que no se agrande cuando hay pocos favoritos
        panelLista.add(Box.createVerticalGlue());

        JScrollPane scroll = new JScrollPane(panelLista, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        root.add(scroll, BorderLayout.CENTER);

        setContentPane(root);

        cargarYRender();
        applyTheme(JFramePrincipal.darkMode);
    }

    private void cargarYRender() {
        panelLista.removeAll();

        if (user == null) {
            panelLista.add(new JLabel("Inicia sesión para ver favoritos."));
            repaint();
            return;
        }

        try {
            List<Libro> favoritos = favoritoDAO.getFavoritosByUser(user);
            lblTitulo.setText("Mis favoritos (" + favoritos.size() + ")");

            if (favoritos.isEmpty()) {
                JLabel vacio = new JLabel("Aún no has marcado ningún libro como favorito.");
                vacio.setFont(fuenteMenu);
                vacio.setAlignmentX(Component.CENTER_ALIGNMENT); //centro horizontal
                panelLista.add(vacio);
                
            } else {
                for (Libro l : favoritos) {
                	JPanel fila = buildRow(l);
                	fila.setAlignmentX(Component.CENTER_ALIGNMENT); //para que no se estire
                    panelLista.add(fila);
                    panelLista.add(Box.createRigidArea(new Dimension(0, 8)));
                }
            }
            //para empujar las filas hacia arriba
            panelLista.add(Box.createVerticalGlue());
            
        } catch (SQLException e) {
            JLabel err = new JLabel("Error al cargar favoritos: " + e.getMessage());
            panelLista.add(err);
        }

        panelLista.revalidate();
        panelLista.repaint();
    }

    private JPanel buildRow(Libro l) {
        JPanel row = new JPanel();
        row.setLayout(new BorderLayout(10,0));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200)); //altura fija para que no se expandan
        //row.setBackground(Color.WHITE);
        row.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JLabel portada = new JLabel(l.getPortada(), JLabel.CENTER);
        portada.setPreferredSize(new Dimension(140, 180));
        row.add(portada, BorderLayout.WEST);

        JPanel info = new JPanel();
        //info.setBackground(Color.WHITE);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel(l.getTitulo());
        titulo.setFont(fuenteTitulo.deriveFont(18f));
        titulo.setBorder(BorderFactory.createEmptyBorder(8, 8, 4, 8));

        JLabel autor = new JLabel(l.getAutor());
        autor.setFont(fuenteMenu);
        autor.setBorder(BorderFactory.createEmptyBorder(0, 8, 4, 8));

        JLabel val = new JLabel("★ " + String.format("%.2f", l.getValoracion()));
        val.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));

        info.add(titulo);
        info.add(autor);
        info.add(val);

        row.add(info, BorderLayout.CENTER);

        JPanel acciones = new JPanel(new GridLayout(2, 1, 6, 6));
        //acciones.setBackground(Color.WHITE);
        acciones.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnAbrir = new JButton("Abrir");
        btnAbrir.addActionListener(e -> {
            JDialogLibro infoLibro = new JDialogLibro(this, l);
            infoLibro.setVisible(true);
        });

        JButton btnQuitar = new JButton("Quitar");
        btnQuitar.setBackground(new Color(255, 230, 150));
        btnQuitar.addActionListener(e -> {
            try {
                favoritoDAO.removeFavorito(user, l);
                cargarYRender();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "No se pudo quitar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        acciones.add(btnAbrir);
        acciones.add(btnQuitar);

        row.add(acciones, BorderLayout.EAST);

        // Clic en toda la fila abre detalles
        row.setCursor(new Cursor(Cursor.HAND_CURSOR));
        row.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                JDialogLibro infoLibro = new JDialogLibro(JFrameFavoritos.this, l);
                infoLibro.setVisible(true);
            }
        });

        return row;
    }
    
    public void applyTheme(boolean darkMode) {
        Color fondo, texto, headerBg, cardBg, botonBg, botonHover;

        if (darkMode) {
            fondo = new Color(18, 25, 35);
            texto = new Color(220, 230, 240);
            headerBg = new Color(20, 40, 70); 
            cardBg = new Color(25, 35, 50);
            botonBg = new Color(40, 90, 160);
            botonHover = new Color(70, 120, 190);
        } else {
            fondo = Color.WHITE;
            texto = Color.BLACK;
            headerBg = new Color(0, 160, 220);
            cardBg = Color.WHITE;
            botonBg = new Color(90,170,255);
            botonHover = new Color(60,140,230);
        }

        aplicarRecursivo(getContentPane(), fondo, texto, cardBg, botonBg, botonHover);

        Component header = ((BorderLayout)getContentPane().getLayout()).getLayoutComponent(BorderLayout.NORTH);
        if (header instanceof JPanel h) {
            h.setBackground(headerBg);
            lblTitulo.setForeground(Color.WHITE);
        }

        repaint();
        revalidate();
    }

    private void aplicarRecursivo(Component comp, Color fondo, Color texto, Color cardBg, Color botonBg, Color botonHover) {

        if (comp instanceof JPanel panel) {
            if (panel.getBorder() instanceof MatteBorder || panel.getBorder() instanceof javax.swing.border.LineBorder) {
                panel.setBackground(cardBg);
            } else {
                panel.setBackground(fondo);
            }
        }

        if (comp instanceof JLabel lbl) {
            lbl.setForeground(texto);
        }

        if (comp instanceof JButton btn) {
            btn.setBackground(botonBg);
            btn.setForeground(Color.WHITE);

            for (var ml : btn.getMouseListeners()) {
                if (ml.getClass().getName().contains("ThemeHover")) {
                    btn.removeMouseListener(ml);
                }
            }

            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override 
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    btn.setBackground(botonHover);
                }
                @Override 
                public void mouseExited(java.awt.event.MouseEvent e) {
                    btn.setBackground(botonBg);
                }
            });
        }

        if (comp instanceof JScrollPane sp) {
            sp.getViewport().setBackground(fondo);
        }

        if (comp instanceof Container cont) {
            for (Component child : cont.getComponents()) {
                aplicarRecursivo(child, fondo, texto, cardBg, botonBg, botonHover);
            }
        }
    }

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
