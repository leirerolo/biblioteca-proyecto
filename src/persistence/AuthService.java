package persistence;


import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Optional;


import domain.User;


public class AuthService {
	private final AppState state;

	public AuthService(AppState state) { this.state = state; }
	
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
        state.getUsers().forEach(ur -> {
            if (ur.getUser().getUsuario() != null &&
                ur.getUser().getUsuario().equalsIgnoreCase(u)) {
                throw new IllegalArgumentException("El nombre de usuario ya existe");
            }
            if (!e.isEmpty() && ur.getUser().getEmail() != null &&
                ur.getUser().getEmail().equalsIgnoreCase(e)) {
                throw new IllegalArgumentException("El email ya está registrado");
            }
        });


     // Crear User con ID autoincremental (tu User no tiene constructor vacío)
        int newId = state.nextUserId();
        User user = new User(newId, n, a, e.isEmpty() ? null : e, /*avatarPath*/ null);
        user.setUsuario(u);

        String hash = sha256(p);
        state.getUsers().add(new UserRecord(user, hash));
        AppStateStore.save(state);
        return user;
    }


	/** Intenta loguear. Devuelve el User si coincide el hash de contraseña. */
	public Optional<User> login(String username, String rawPassword) {
		String u = username.trim().toLowerCase();
		String hash = sha256(rawPassword);
		return state.getUsers().stream()
				.filter(ur -> ur.getUser().getUsuario().equalsIgnoreCase(u)
						&& ur.getPasswordHash().equals(hash))
				.map(UserRecord::getUser)
				.findFirst();
	}
	
}