package gui;

import db.ReservaDAO;
import domain.Reserva;
import domain.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * Diálogo para ver el historial de reservas devueltas.
 */
public class JDialogHistorialReservas extends JDialog {

    private static final long serialVersionUID = 1L;
    
    private List<Reserva> reservasOriginal = new ArrayList<>();

    public JDialogHistorialReservas(JFrame padre, User user) {
        super(padre, "Historial de reservas", true);
        setSize(900, 600);
        setLocationRelativeTo(padre);
        setLayout(new BorderLayout());
        

        String[] cols = {"Título", "Autor", "Fecha reserva", "Duración (días)", "Prolongaciones", "Valoración usuario"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable table = new JTable(model);
        table.setRowHeight(26);
        add(new JScrollPane(table), BorderLayout.CENTER);

        
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JTextField txtTitulo = new JTextField(10);
        JTextField txtAutor = new JTextField(10);
        JComboBox<String> cbProlong = new JComboBox<>(new String[]{"", "0", "1", "2"});
       
        JComboBox<String> cbAnio = new JComboBox<>();
        cbAnio.addItem("");
        for (int a = 2010; a <= 2035; a++) {
        	cbAnio.addItem(String.valueOf(a));
        }
        
        JComboBox<String> cbMes = new JComboBox<>();
        cbMes.addItem("");
        for (int m = 1; m <= 12; m++) {
        	cbMes.addItem(String.valueOf(m));
        }
        
        JComboBox<String> cbDia = new JComboBox<>();
        cbDia.addItem("");
        for (int d = 1; d <= 31; d++) {
        	cbDia.addItem(String.valueOf(d));
        }
        
        JButton btnBuscar = new JButton("Buscar");
        JButton btnLimpiar = new JButton("Limpiar");
        
        panelBusqueda.add(new JLabel("Título:"));
        panelBusqueda.add(txtTitulo);
        
        panelBusqueda.add(new JLabel("Autor:"));
        panelBusqueda.add(txtAutor);
        
        panelBusqueda.add(new JLabel("Prolong.:"));
        panelBusqueda.add(cbProlong);
        
        panelBusqueda.add(new JLabel("Fecha (A/M/D):"));
        panelBusqueda.add(cbAnio);
        panelBusqueda.add(cbMes);
        panelBusqueda.add(cbDia);
        
        panelBusqueda.add(btnBuscar);
        panelBusqueda.add(btnLimpiar);
        
        add(panelBusqueda, BorderLayout.NORTH);
        
        
        
        JButton cerrar = new JButton("Cerrar");
        cerrar.addActionListener(e -> dispose());
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(cerrar);
        add(south, BorderLayout.SOUTH);

        // cargar datos
        try {
            ReservaDAO dao = new ReservaDAO();
            reservasOriginal = dao.getHistorialReservasByUser(user);
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
        
        
        
        
        //acciones
        
        btnBuscar.addActionListener(e -> {
        	List<Reserva> filtradas = new ArrayList<>();
        	
        	for (Reserva r : reservasOriginal) {
        		boolean coincide = true;
        		
        		//filtro titulo
        		String t = txtTitulo.getText().trim().toLowerCase();
        		if (!t.isEmpty()) { 
        			coincide &= r.getLibro().getTitulo().toLowerCase().contains(t); 
        		}
        		
        		//filttro autor
        		String a = txtAutor.getText().trim().toLowerCase(); 
        		if (!a.isEmpty()) { 
        			coincide &= r.getLibro().getAutor().toLowerCase().contains(a); 
        		}
        		
        		//filtro prolong
        		String prolongSel = (String) cbProlong.getSelectedItem(); 
        		if (prolongSel != null && !prolongSel.isEmpty()) { 
        			coincide &= r.getProlongaciones() == Integer.parseInt(prolongSel); 
        		}
        		
        		//filtro fecha
        		LocalDate fecha = r.getFecha();
        		String anioSel = (String) cbAnio.getSelectedItem(); 
        		String mesSel = (String) cbMes.getSelectedItem(); 
        		String diaSel = (String) cbDia.getSelectedItem(); 
        		
        		if (anioSel != null && !anioSel.isEmpty()) { 
        			coincide &= fecha.getYear() == Integer.parseInt(anioSel); 
        		} 
        		
        		if (mesSel != null && !mesSel.isEmpty()) { 
        			coincide &= fecha.getMonthValue() == Integer.parseInt(mesSel); 
        		} 
        		
        		if (diaSel != null && !diaSel.isEmpty()) { 
        			coincide &= fecha.getDayOfMonth() == Integer.parseInt(diaSel); 
        		} 
        		
        		if (coincide) filtradas.add(r);
        	}
        	
        	cargarEnTabla(model, filtradas);
        	
        });
        
        btnLimpiar.addActionListener(e -> { 
        	txtTitulo.setText(""); 
        	txtAutor.setText(""); 
        	cbProlong.setSelectedIndex(0); 
        	cbAnio.setSelectedIndex(0); 
        	cbMes.setSelectedIndex(0); 
        	cbDia.setSelectedIndex(0); 
        	cargarEnTabla(model, reservasOriginal); 
        });
        
        
        
    }
    
    private void cargarEnTabla(DefaultTableModel model, List<Reserva> lista) { 
    	model.setRowCount(0); 
    	
    	if (lista.isEmpty()) { 
    		model.addRow(new Object[]{"(sin resultados)", "", "", "", "", ""}); 
    		return; 
    	} 
    	
    	for (Reserva r : lista) { 
    		model.addRow(new Object[]{ 
    				r.getLibro().getTitulo(), 
    				r.getLibro().getAutor(), 
    				r.getFecha(), 
    				r.getDuracion(), 
    				r.getProlongaciones(), 
    				r.getValoracionUsuario() > 0 ? String.format("%.1f", r.getValoracionUsuario()) : "-" 
    			}); 
    	} 
    }
}
