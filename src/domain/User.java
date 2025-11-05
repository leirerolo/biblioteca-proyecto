// domain/User.java
package domain;

import java.util.ArrayList;
import java.util.List;

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
}
