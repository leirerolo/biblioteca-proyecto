package persistence;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import domain.Reserva;


/**
* Estado completo de la app que guardaremos en disco.
*/
public class AppState implements Serializable {
	private static final long serialVersionUID = 1L;


	/** Usuarios registrados junto con su hash de contraseña */
	private final List<UserRecord> users = new ArrayList<>();


	/** Todas las reservas existentes (se filtran por userId cuando haga falta) */
	private final List<Reserva> reservas = new ArrayList<>();


	/** Secuencia simple para asignar IDs a nuevos usuarios */
	private int nextUserId = 1;

	
	//recordar user
		private final Map<String, String> savedCredentials = new HashMap<>();
		
		public Map<String, String> getSavedCredentials() {
			return savedCredentials; 
		}
		
		public void saveCredential(String user, String pass) { 
			savedCredentials.put(user, pass); 
		}
		
		public void removeCredential(String user) { 
			savedCredentials.remove(user); 
		}
	

	public List<UserRecord> getUsers() { return users; }
	public List<Reserva> getReservas() { return reservas; }


	public int nextUserId() { return nextUserId++; }
}