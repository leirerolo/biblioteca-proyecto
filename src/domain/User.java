// domain/User.java
package domain;

import java.util.List;

public class User {
    private int id;
    private String nombre;
    private String apellido;
    private String email;        // NUEVO
    private String avatarPath;   // NUEVO (p.ej. "/images/avatar.png" o ruta de archivo)
    private List<Reserva> reservas;

    // ya existente
    public User(int id, String nombre, String apellido) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
    }

    // útil para login demo (sin id)
    public User(String nombre, String apellido) {
        this.nombre = nombre;
        this.apellido = apellido;
    }

    // OPCIONAL: constructor completo
    public User(int id, String nombre, String apellido, String email, String avatarPath) {
        this(id, nombre, apellido);
        this.email = email;
        this.avatarPath = avatarPath;
    }

    // getters/setters
    public int getID() { 
    	return id; 
    }
    public void setID(int id) { 
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

    public String getEmail() { 
    	return email; 
    }
    public void setEmail(String email) { 
    	this.email = email; 
    }

    public String getAvatarPath() { 
    	return avatarPath; 
    }
    public void setAvatarPath(String avatarPath) { 
    	this.avatarPath = avatarPath; 
    }
}
