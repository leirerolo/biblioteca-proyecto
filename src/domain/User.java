// domain/User.java
package domain;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import main.Main;

public class User implements Serializable{
	private static final long serialVersionUID = 1L;
	
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
    
 // --- CONFIG: carpeta donde guardamos los CSV de reservas
    private static final Path DATA_DIR = Paths.get("data");
    
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
    
 // Helpers de persistencia CSV
    private String safeBaseForFilename() {
        String base = (this.getUsuario() != null && !this.getUsuario().isBlank())
                ? this.getUsuario()
                : this.getNombre();
        if (base == null || base.isBlank()) base = "user";
        // Sanea: solo letras, números, guion y guion_bajo
        return base.replaceAll("[^\\p{Alnum}_-]", "_");
    }

    private File reservasFile() {
        try {
            if (!Files.exists(DATA_DIR)) Files.createDirectories(DATA_DIR);
        } catch (IOException e) {
            e.printStackTrace(); // Si falla, seguimos en el directorio actual
        }
        return DATA_DIR.resolve("reservas_" + safeBaseForFilename() + ".csv").toFile();
    }
    
    //GUARDAR RESERVAS EN MEMORIA
    //cada vez que el user hace una nueva reserva, se guarda en su fichero
    public void guardarReservasCSV() {
    	if (this.getReservas()==null || this.getReservas().isEmpty()) return;
    	
    	try (PrintWriter writer = new PrintWriter(new FileWriter("reservas_"+this.getNombre()+".csv"))) {
    		for (Reserva r : this.getReservas()) {
    			writer.println(r.getLibro().getTitulo() + ";" +
    							r.getLibro().getAutor() + ";" +
    							r.getFecha() + ";" + 
    							r.getDuracion() + ";" + 
    							r.getProlongaciones());
    		}
    	} catch(IOException e) {
    		e.printStackTrace();
    	}
    }
    
    //CARGAR RESERVAS DEL USER cada vez que hace loggin
    public void cargarReservasCSV() {
    	File file = new File("reservas_"+this.getNombre()+".csv");
    	this.reservas=new ArrayList<>();
    	
    	if (!file.exists()) return; 
    	
    	//ayuda de CHAT GPT para el BufferedReader
    	try (BufferedReader br = new BufferedReader(new FileReader(file))) {
    		String linea;
    		
    		while ((linea = br.readLine()) != null) {
    			String[] datos = linea.split(";");
    			if (datos.length>= 5) {
    				String titulo = datos[0];
    				String autor = datos[1];
    				LocalDate fecha = LocalDate.parse(datos[2]);
    				int duracion = Integer.parseInt(datos[3]);
    				int prolongaciones = Integer.parseInt(datos[4]);
    				
    				//Buscar el libro en nuestra lista de libros de la biblioteca
    				Libro libro = null;
    				for (Libro l : Main.librosGlobales) {
    					if (l.getTitulo().equalsIgnoreCase(titulo)) {
    						libro = l;
    						break;
    					}
    				}
    				//Crear la reserva con el libro 
    				Reserva r = new Reserva(libro, this, fecha);
    				r.setDuracion(duracion);
    				r.setProlongaciones(prolongaciones);
    				reservas.add(r);
    			}
    		}
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    }
    
    //COMPROBAR SI ESTÁ PENALIZADO
    public void verificarPenalizacion() {
    	this.cargarReservasCSV();
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
