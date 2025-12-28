package gui;

import db.ReservaDAO;
import domain.Reserva;
import domain.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Diálogo para ver el historial de reservas devueltas.
 */
public class JDialogHistorialReservas extends JDialog {

    private static final long serialVersionUID = 1L;

    public JDialogHistorialReservas(JFrame padre, User user) {
        super(padre, "Historial de reservas", true);
        setSize(700, 400);
        setLocationRelativeTo(padre);
        setLayout(new BorderLayout());

        String[] cols = {"Título", "Autor", "Fecha reserva", "Duración (días)", "Prolongaciones", "Valoración usuario"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable table = new JTable(model);
        table.setRowHeight(26);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton cerrar = new JButton("Cerrar");
        cerrar.addActionListener(e -> dispose());
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(cerrar);
        add(south, BorderLayout.SOUTH);

        // cargar datos
        try {
            ReservaDAO dao = new ReservaDAO();
            List<Reserva> hist = dao.getHistorialReservasByUser(user);

            for (Reserva r : hist) {
                model.addRow(new Object[]{
                        r.getLibro().getTitulo(),
                        r.getLibro().getAutor(),
                        r.getFecha(),
                        r.getDuracion(),
                        r.getProlongaciones(),
                        r.getValoracionUsuario() > 0 ? String.format("%.1f", r.getValoracionUsuario()) : "-"
                });
            }

            if (hist.isEmpty()) {
                model.addRow(new Object[]{"(sin historial)", "", "", "", "", ""});
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar historial: " + ex.getMessage(), "Error BD", JOptionPane.ERROR_MESSAGE);
        }
    }
}
