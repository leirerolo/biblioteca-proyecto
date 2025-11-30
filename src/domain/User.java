package domain;


import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import db.LibroDAO;
import db.ReservaDAO; 
import db.UserDAO; 
import java.sql.SQLException; 

import javax.swing.JOptionPane;



public class User implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private final transient ReservaDAO reservaDAO = new ReservaDAO();
	private final transient UserDAO userDAO = new UserDAO();
	private final transient LibroDAO libroDAO = new LibroDAO();
	
    private int id;
    private String nombre;
    private String apellido;
    private String email;       
    private String avatarPath;   
    private ArrayList<Reserva> reservas;
    private static User LOGGED_IN = null;
    private String password;
    private String usuario;
    
    //atributo para cuando el user esté penalizado por haberse pasado de plazo
    private LocalDate penalizacionHasta;
    

    // ya existente
    public User(int id, String nombre, String apellido) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.reservas= new ArrayList<>();
    }

    //CREA NUEVO
    public User(String nombre, String password) {
    	this.nombre=nombre;
    	this.password = password;
    	this.reservas= new ArrayList<>();
    }

    // OPCIONAL: constructor completo
    public User(int id, String nombre, String apellido, String email, String avatarPath) {
        this(id, nombre, apellido);
        this.email = email;
        this.avatarPath = avatarPath;
        this.reservas= new ArrayList<>();
    }

    public User(String nombre, String apellido, String email, String avatarPath, String usuario, String password) {
        this.id = 0; 
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.avatarPath = avatarPath;
        this.usuario = usuario;
        this.password = password; 
        
        this.reservas = new ArrayList<>();
        this.penalizacionHasta = null;
    }
    // getters/setters
    public int getId() { 
    	return id; 
    }
    public void setId(int id) { 
    	this.id = id; 
    }
    
    public String getUsuario() { 
    	return usuario; 
    }
    public void setUsuario(String usuario) { 
    	this.usuario = usuario; 
    }

    public String getNombre() { 
    	return nombre; 
    }
    public void setNombre(String nombre) { 
    	this.nombre = nombre; 
    }

    public String getApellido() { 
    	return apellido; 
    }
    public void setApellido(String apellido) { 
    	this.apellido = apellido; 
    }

    public ArrayList<Reserva> getReservas() {
		return reservas;
	}

	public void setReservas(ArrayList<Reserva> reservas) {
		this.reservas = reservas;
	}
	public LocalDate getPenalizacionHasta() {
		return penalizacionHasta;
	}
	public void setPenalizacionHasta(LocalDate penalizacionHasta) {
		this.penalizacionHasta = penalizacionHasta;
	}
	
	public String getEmail() { 
    	return email; 
    }
    public void setEmail(String email) { 
    	this.email = email; 
    }
    public String getPassword() {
    	return password;
    }
    public void setPassword(String password) {
    	this.password=password;
    }
    public String getAvatarPath() { 
    	return avatarPath; 
    }
    public void setAvatarPath(String avatarPath) { 
    	this.avatarPath = avatarPath; 
    }
     
    public static void setLoggedIn(User u) { 
    	LOGGED_IN = u; 
    }
    public static User getLoggedIn() { 
    	return LOGGED_IN; 
    }
    public static boolean isLoggedIn() { 
    	return LOGGED_IN != null; 
    }
    
    //obtener si el user está penalizado
    public boolean estaPenalizado() {
    	return penalizacionHasta !=null && LocalDate.now().isBefore(penalizacionHasta);
    }
    

    public void agregarReserva(Reserva reserva) {
        if (this.reservas == null) {
            this.reservas = new ArrayList<>();
        }
        
        this.reservas.add(reserva);
        
        try {
            reservaDAO.insertaReserva(reserva);
            System.out.println("Reserva para libro " + reserva.getLibro().getTitulo() + " guardada en BD.");
        } catch (SQLException e) {
            System.err.println("Error al insertar la reserva en BD: " + e.getMessage());
            this.reservas.remove(reserva);
        }
    }
    
  
    public void cargarReservas() {
        if (this.reservas == null) {
            this.reservas = new ArrayList<>();
        }
        this.reservas.clear(); 
        
        try {
            
            List<Reserva> reservasBD = reservaDAO.getReservasByUser(this);
            this.reservas.addAll(reservasBD);
            System.out.println("Reservas del usuario " + this.id + " cargadas desde BD.");
            
        } catch (SQLException e) {
            System.err.println("Error al cargar las reservas desde la BD para el usuario " + this.id + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    public void actualizarReserva(Reserva reserva) throws SQLException {
        reservaDAO.actualizaReserva(reserva);
        

        if (reserva.getValoracionUsuario() > 0) { 
            reserva.getLibro().aplicarNuevaValoracion(reserva.getValoracionUsuario()); 
   
            LibroDAO libroDAO = new LibroDAO(); 
            libroDAO.updateRating(reserva.getLibro()); 
        }
        
        System.out.println("Reserva y/o rating actualizados en BD para libro ID: " + reserva.getLibro().getId());
    }
    
    public static User login(String email, String password) {
        UserDAO dao = new UserDAO();
        try {
            return dao.login(email, password); 
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, 
                "Eroare la conectarea la baza de date: " + e.getMessage(), 
                "Eroare BD", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    //COMPROBAR SI ESTÁ PENALIZADO
    public void verificarPenalizacion() {
    	this.cargarReservas(); 
		boolean tieneAtrasos = false;
		
		//cargamos sus reservas
		List<Reserva> reservas = this.getReservas();
		if (reservas==null || reservas.isEmpty()) return;
		
		for (Reserva r : reservas) {
			if (r.getDiasRestantes() <= 0) {
				tieneAtrasos=true;
				break;
			}
		}
		//si se le ha pasado algún plazo, se le penaliza
		//no podrá reservar durante 21 días
		if (tieneAtrasos) {
			LocalDate penalizacionHasta = LocalDate.now().plusDays(21);
			this.setPenalizacionHasta(penalizacionHasta);
			
			try {
				userDAO.updatePenalizacion(this); 
			} catch (SQLException e) {
				System.err.println("Error al guardar la penalización en BD: " + e.getMessage());
			}
			
			//Muestro el mensaje de penalización
			JOptionPane.showMessageDialog(null, 
					"Tienes reservas fuera de plazo.\n" + "No podrás reservar nuevos libros hasta el " + penalizacionHasta + ".",
					"Penalización",
					JOptionPane.WARNING_MESSAGE);
			
		//ya estaba penalizado de antes --> mensaje de recordatorio
		} else if (this.estaPenalizado()) {
			JOptionPane.showMessageDialog(null,
					"Estás penalizad@ hasta el " + this.getPenalizacionHasta() + ". No puedes realizar nuevas reservas hasta esa fecha.", 
					"Penalización activa", JOptionPane.WARNING_MESSAGE);
		}
	}
}