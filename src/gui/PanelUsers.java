package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import db.ReservaDAO;
import db.UserDAO;
import domain.Reserva;
import domain.User;

import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class PanelUsers extends JPanel {
    private static final long serialVersionUID = 1L;
    protected Font fuenteMenu = new Font("Comic Sans MS", Font.BOLD, 18);
    
    private JTable tablaUsers;
    private DefaultTableModel modeloTabla;

    public PanelUsers() {
        setLayout(new BorderLayout());

        String[] columnas = {"ID", "Usuario", "Email", "Reservas"};

        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tablaUsers = new JTable(modeloTabla);
        tablaUsers.setFillsViewportHeight(true);
        
        //para que el encabezado sea más visual
        tablaUsers.getTableHeader().setBackground(new Color(230, 230, 250));
        tablaUsers.getTableHeader().setFont(fuenteMenu);
        
        //renderers personalizados a cada columna
        tablaUsers.getColumnModel().getColumn(0).setCellRenderer(new IDRenderer());
        tablaUsers.getColumnModel().getColumn(1).setCellRenderer(new UsuarioRenderer());
        tablaUsers.getColumnModel().getColumn(2).setCellRenderer(new EmailRenderer());
        tablaUsers.getColumnModel().getColumn(3).setCellRenderer(new MultiReservationRenderer());

        //JScrollPane para que tenga scroll si hay muchas filas
        JScrollPane scroll = new JScrollPane(tablaUsers);
        add(scroll, BorderLayout.CENTER);

        cargarUsuarios();
    }
    
    public void applyTheme(Theme theme) {
        setBackground(theme.backgroundMain);
        tablaUsers.setBackground(theme.backgroundMain);
        tablaUsers.setForeground(theme.textColor);
        
        if (theme == Theme.DARK) {
            tablaUsers.getTableHeader().setBackground(new Color(30, 30, 30)); 
        } else {
            tablaUsers.getTableHeader().setBackground(new Color(230, 230, 250));
        }

        tablaUsers.getTableHeader().setForeground(Color.BLUE);
        
        ((IDRenderer) tablaUsers.getColumnModel().getColumn(0).getCellRenderer()).setTheme(theme);
        ((UsuarioRenderer) tablaUsers.getColumnModel().getColumn(1).getCellRenderer()).setTheme(theme);
        ((EmailRenderer) tablaUsers.getColumnModel().getColumn(2).getCellRenderer()).setTheme(theme);
        
        TableCellRenderer rendererReservas = tablaUsers.getColumnModel().getColumn(3).getCellRenderer();
        if (rendererReservas instanceof MultiReservationRenderer) {
            ((MultiReservationRenderer) rendererReservas).setTheme(theme);
        }

        revalidate();
        repaint();
    }
    
    
    private void cargarUsuarios() {
        modeloTabla.setRowCount(0); // limpiar tabla

        UserDAO userDAO = new UserDAO();
        ReservaDAO reservaDAO = new ReservaDAO();

        try {
            List<User> usuarios = userDAO.getTodosLosUsuarios();
            
            for (User u : usuarios) {
            	//solo añadimos los usuarios con rol USER
            	if (u.getRol() == User.Rol.USER) {
            		List<Reserva> reservas = reservaDAO.getReservasByUser(u);
	                modeloTabla.addRow(new Object[]{
	                        u.getId(),
	                        u.getUsuario(),
	                        u.getEmail(),
	                        reservas // enviamos la lista directamente al renderer
	                });
            	}
                
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
 // Renderer para la columna ID
    static class IDRenderer extends DefaultTableCellRenderer {
        private Theme theme = Theme.LIGHT; 

        public void setTheme(Theme theme) { this.theme = theme; }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (!isSelected) {
                c.setBackground(row % 2 == 0 ? theme.backgroundMain : theme.backgroundPanel);
                c.setForeground(theme.textColor);
            }
            return c;
        }
    }

    // Renderer para la columna Usuario
 // Renderer for Usuario Column
    static class UsuarioRenderer extends DefaultTableCellRenderer {
        private Theme theme = Theme.LIGHT;
        public void setTheme(Theme theme) { this.theme = theme; }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            lbl.setFont(new Font("Arial", Font.PLAIN, 14));
            
            if (!isSelected) {
                lbl.setBackground(row % 2 == 0 ? theme.backgroundMain : theme.backgroundPanel);
                lbl.setForeground(theme.textColor);
            }
            lbl.setOpaque(true);
            return lbl;
        }
    }

    // Renderer para la columna del email
    static class EmailRenderer extends DefaultTableCellRenderer {
        private Theme theme = Theme.LIGHT;
        public void setTheme(Theme theme) { this.theme = theme; }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            lbl.setFont(new Font("Arial", Font.ITALIC, 14));
            
            if (!isSelected) {
                lbl.setBackground(row % 2 == 0 ? theme.backgroundMain : theme.backgroundPanel);
                lbl.setForeground(theme == Theme.DARK ? new Color(200, 200, 200) : Color.DARK_GRAY);
            }
            lbl.setOpaque(true);
            return lbl;
        }
    }
   

    // Renderer para mostrar reservas de forma visual en cada fila
    static class MultiReservationRenderer extends JPanel implements TableCellRenderer {
        private static final long serialVersionUID = 1L;
        private Theme currentTheme = Theme.LIGHT;
        
        public MultiReservationRenderer() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setOpaque(true);
        }

        public void setTheme(Theme theme) {
            this.currentTheme = theme;
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            removeAll();
            
            Color filaColor;
            if (isSelected) {
                filaColor = table.getSelectionBackground();
            } else {
                filaColor = (row % 2 == 0) ? currentTheme.backgroundMain : currentTheme.backgroundPanel;
            }
            setBackground(filaColor);
            
            if (value instanceof List) {
                List<Reserva> reservas = (List<Reserva>) value;
                for (Reserva r : reservas) {
                    JLabel lbl = new JLabel(r.getLibro().getTitulo() + " - " + r.getDiasRestantes() + " dias");
                    lbl.setOpaque(true);
                    lbl.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
                    lbl.setFont(new Font("Arial", Font.PLAIN, 14));

                    int dias = r.getDiasRestantes();
                    
                    if (currentTheme == Theme.DARK) {
                        if (dias <= 2) {
                            lbl.setBackground(new Color(150, 0, 0));
                            lbl.setForeground(Color.WHITE);
                        } else if (dias <= 5) {
                            lbl.setBackground(new Color(180, 100, 0)); 
                            lbl.setForeground(Color.WHITE);
                        } else {
                            lbl.setBackground(new Color(0, 100, 0)); 
                            lbl.setForeground(Color.WHITE);
                        }
                    } else {
                        if (dias <= 2) {
                            lbl.setBackground(new Color(255, 102, 102));
                            lbl.setForeground(Color.BLACK);
                        } else if (dias <= 5) {
                            lbl.setBackground(new Color(255, 204, 102));
                            lbl.setForeground(Color.BLACK);
                        } else {
                            lbl.setBackground(new Color(204, 255, 204));
                            lbl.setForeground(Color.BLACK);
                        }
                    }

                    lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
                    add(lbl);
                    add(Box.createVerticalStrut(2));
                }
            }
            
            int altura = getPreferredSize().height;
            if (altura < 25) altura = 25;
            if (table.getRowHeight(row) != altura) {
                table.setRowHeight(row, altura);
            }

            return this;
        }
    }

}
