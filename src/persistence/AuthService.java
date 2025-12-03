package persistence;


import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.SQLException;
import java.util.Optional;


import domain.User;
import db.UserDAO;


public class AuthService {
	private final AppState state;
	private final UserDAO userDAO;
	
	public AuthService(AppState state) { 
		this.state = state;
		this.userDAO= new UserDAO();}
	
	private static String normalize(String s) {
        return s == null ? "" : s.trim();
    }

	public static String sha256(String text) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] hash = md.digest(text.getBytes(StandardCharsets.UTF_8));
			StringBuilder sb = new StringBuilder();
			for (byte b : hash) sb.append(String.format("%02x", b));
			return sb.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


	/** Registra un usuario nuevo. Devuelve el User creado. Lanza IllegalArgumentException si username o email existen. */
	public User register(String username, String email, String nombre, String apellido, String rawPassword) {
		final String u = normalize(username).toLowerCase();
        final String e = normalize(email).toLowerCase();
        final String n = normalize(nombre);
        final String a = normalize(apellido);
        final String p = normalize(rawPassword);
        
        if (u.isEmpty()) throw new IllegalArgumentException("El nombre de usuario es obligatorio");
        if (p.isEmpty()) throw new IllegalArgumentException("La contraseña es obligatoria");

		// Comprobar unicidad
        String hash = sha256(p);
        
      
        User user = new User(n,a,e.isEmpty() ? null:e, /*avatarPath*/ null, u, hash, User.Rol.USER);
                try {
                    
                    int idGenerado = userDAO.registerUser(user);
                    
                    if (idGenerado > 0) {
                    	 return user;
                    } else {
                        throw new IllegalStateException("No se ha podido insertar el usuario en la BD. invalid ID.");
                    }
                } catch (SQLException ex) {
                    System.err.println("Error en la persistencia del usuario en la BD " + ex.getMessage());
                    
                    if (ex.getMessage().contains("UNIQUE constraint failed") || ex.getMessage().contains("SQLITE_CONSTRAINT")) { 
                         throw new IllegalArgumentException("El usuario/correo ya esta en uso.");
                    }
                    throw new RuntimeException("Unkown error in the data base " + ex.getMessage(), ex);
                }
            }


        	/** * Intenta loguear utilizando la Base de Datos. 
             * Devuelve el User si coincide el hash de contraseña. 
             */
	public Optional<User> login(String username, String rawPassword) {
		String u = normalize(username).toLowerCase();
		
	    final String p = normalize(rawPassword); 
		String hash = sha256(p); 
		
		try {
			User user = userDAO.login(u, hash); 
			return Optional.ofNullable(user);
			
		} catch (SQLException e) {
			System.err.println("Eroare la logare din BD: " + e.getMessage());
			return Optional.empty();
		}
	}
}