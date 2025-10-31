package domain;

public class User {
	private int id;
	private String nombre;
	private String apellido;
	private String email;
	private String fotoPath; // ruta al imagen del perfil
	
	//constructor para los objetos que estan en la BD
	public User(int id, String nombre, String apellido) {
		this.id=id;
		this.nombre=nombre;
		this.apellido=apellido;
		this.email="";
		this.fotoPath="";
	}
	
	//constructor para los objetos nuevos
	public User(String nombre, String apellido) {
		this.nombre=nombre;
		this.apellido=apellido;
		this.email="";
		this.fotoPath="";
	}
	
	//constructor para login simulado
	public User(String nombre, String apellido, String email, String fotoPath) {
		this.nombre=nombre;
		this.apellido=apellido;
		this.email=email;
		this.fotoPath=fotoPath;
	}
	
	public int getId() {return id;}
	public String getNombre() {return nombre;}
	public String getApellido() {return apellido;}
	public String getEmail() {return email;}
	public String getFotoPath() {return fotoPath;}
	
	public void setId(int id) { this.id = id; }
	public void setNombre(String nombre) { this.nombre = nombre; }
	public void setApellido(String apellido) { this.apellido = apellido; }
	public void setEmail(String email) { this.email = email; }
	public void setFotoPath(String fotoPath) { this.fotoPath = fotoPath; }
}
