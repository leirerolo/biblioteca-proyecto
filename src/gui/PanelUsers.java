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
		private static final long serialVersionUID = 1L;

		public IDRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(new Font("Arial", Font.BOLD, 14));
        }
		@Override
	    public Component getTableCellRendererComponent(JTable table, Object value,
	                                                   boolean isSelected, boolean hasFocus,
	                                                   int row, int column) {
	        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	        c.setFont(new Font("Arial", Font.BOLD, 14));
	        
	        if (!isSelected) {
	            if (row % 2 == 0) c.setBackground(new Color(245, 245, 245));
	            else c.setBackground(Color.WHITE);
	        }
	        
	        return c;
	    }
    }

    // Renderer para la columna Usuario
    static class UsuarioRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;

		public UsuarioRenderer() {
            setFont(new Font("Arial", Font.PLAIN, 14));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            lbl.setFont(new Font("Arial", Font.PLAIN, 14));
            
            if (!isSelected) {
                if (row % 2 == 0) lbl.setBackground(new Color(245, 245, 245));
                else lbl.setBackground(Color.WHITE);
            }
            lbl.setOpaque(true);
            
            return lbl;
        }
    }
    
    // Renderer para la columna Email
    static class EmailRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;

		public EmailRenderer() {
            setFont(new Font("Arial", Font.ITALIC, 14));
            setForeground(Color.DARK_GRAY);
        }
		@Override
	    public Component getTableCellRendererComponent(JTable table, Object value,
	                                                   boolean isSelected, boolean hasFocus,
	                                                   int row, int column) {
	        JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	        lbl.setFont(new Font("Arial", Font.ITALIC, 14));
	        
	        if (!isSelected) {
	            if (row % 2 == 0) lbl.setBackground(new Color(245, 245, 245));
	            else lbl.setBackground(Color.WHITE);
	        }

	        lbl.setOpaque(true);
	        return lbl;
	    }
    }

    // Renderer para mostrar reservas de forma visual en cada fila
    static class MultiReservationRenderer extends JPanel implements TableCellRenderer {
        private static final long serialVersionUID = 1L;

        public MultiReservationRenderer() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {

            removeAll(); // Limpiar reservas previas
            Color filaColor = row % 2 == 0 ? new Color(245, 245, 245) : Color.WHITE;
            if (isSelected) filaColor = table.getSelectionBackground();
            setBackground(filaColor);
            
            if (value instanceof List) {
                List<Reserva> reservas = (List<Reserva>) value;

                for (Reserva r : reservas) {
                    JLabel lbl = new JLabel(r.getLibro().getTitulo() + " - " + r.getDiasRestantes() + " días");
                    lbl.setOpaque(true);
                    lbl.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
                    lbl.setFont(new Font("Arial", Font.PLAIN, 14));

                    // Colorear según días restantes
                    int dias = r.getDiasRestantes();
                    if (dias <= 2) {
                        lbl.setBackground(new Color(255, 102, 102)); // rojo para urgente
                        lbl.setForeground(Color.WHITE);
                    } else if (dias <= 5) {
                        lbl.setBackground(new Color(255, 204, 102)); // naranja
                        lbl.setForeground(Color.BLACK);
                    } else {
                        lbl.setBackground(new Color(204, 255, 204)); // verde
                        lbl.setForeground(Color.BLACK);
                    }

                    // Separador visual entre reservas
                    lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
                    add(lbl);
                    add(Box.createVerticalStrut(2));
                }
            }

            setBorder(hasFocus ? BorderFactory.createLineBorder(Color.BLUE, 1) : null);

            // Ajustar altura de fila según contenido
            int altura = getPreferredSize().height;
            //por si tiene 0 reservas, altura mínima a 20
            if (altura < 20) altura = 20;
            //para los que sí tienen reservas
            if (table.getRowHeight(row) != altura) {
                table.setRowHeight(row, altura);
            }

            return this;
        }
    }

}
