package persistence;


import java.io.Serializable;
import domain.User;


/** Empareja un User con el hash de su contraseña */
public class UserRecord implements Serializable {
	private static final long serialVersionUID = 1L;

	private final User user;
	private final String passwordHash; // SHA-256 en hex

	
	public UserRecord(User user, String passwordHash) {
		this.user = user;
		this.passwordHash = passwordHash;
	}


	public User getUser() { 
		return user;
	}
	public String getPasswordHash() { 
		return passwordHash;
	}
}