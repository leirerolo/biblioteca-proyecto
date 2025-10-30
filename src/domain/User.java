package domain;

public class User {
	private int id;
	private String nombre;
	private String apellido;
	
	// constructor para los objetos que ya están en la BD (tienen ID)
	public User(int id, String nombre, String apellido) {
		this.id= id;
		this.nombre= nombre;
		this.apellido= apellido;
	}
	
	// constructor para los objetos nuevos (sin ID, los va a asignar la BD)
	public User(String nombre, String apellido) {
		this.nombre= nombre;
		this.apellido= apellido;
	}
	
	public int getID() {
		return id;
	}
	
	public void setID(int id) {
		this.id= id;
	}
	
	public String getNombre() {
		return nombre;
	}
	
	public String getApellido() {
		return apellido;
	}
}
