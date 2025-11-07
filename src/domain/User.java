// domain/User.java
package domain;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import main.Main;

public class User {
    private int id;
    private String nombre;
    private String apellido;
    private String email;        // NUEVO
    private String avatarPath;   // NUEVO (p.ej. "/images/avatar.png" o ruta de archivo)
    private ArrayList<Reserva> reservas;
    private static User LOGGED_IN = null;
    private String password;

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
}
