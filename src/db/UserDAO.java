package db;

import domain.User;
import java.sql.*;
import java.time.LocalDate;

public class UserDAO {
	public User getUserById(int id) throws SQLException {
        String sql = "SELECT id, nombre, apellido, email, password, avatar_path, usuario, penalizacion_hasta FROM User WHERE id = ?";
        User user = null;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) { 
            
            pstmt.setInt(1, id); 
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) { // Solo esperamos un resultado 
                    user = construirUser(rs);
                }
            }
        }
        return user;
    }
	
	public User login(String email, String password) throws SQLException {
        String sql = "SELECT * FROM User WHERE (usuario = ? OR email = ?) AND password = ?"; 
        User user = null;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            
        	pstmt.setString(1, email);
            pstmt.setString(2, email); 
            pstmt.setString(3, password); 
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    user = construirUser(rs);
                    
                    User.setLoggedIn(user); 
                }
            }
        }
        return user;
    }


	public int registerUser(User user) throws SQLException {
	    String sql = "INSERT INTO User(nombre, apellido, email, password, avatar_path, penalizacion_hasta, usuario, rol) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
	    int idGenerado = -1;
	
	    try (Connection con = DBConnection.getConnection();
	         PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
	        
	        pstmt.setString(1, user.getNombre());
	        pstmt.setString(2, user.getApellido()); 
	        pstmt.setString(3, user.getEmail());
	        pstmt.setString(4, user.getPassword());
	        pstmt.setString(5, user.getAvatarPath());
	        pstmt.setString(6, user.getPenalizacionHasta() != null ? user.getPenalizacionHasta().toString() : null);
	        pstmt.setString(7, user.getUsuario());
	        pstmt.setString(8, user.getRol() != null ? user.getRol().name() : "USER");
	        
	        int affectedRows = pstmt.executeUpdate(); 
	        
	        if (affectedRows > 0) {
	            try (ResultSet rs = pstmt.getGeneratedKeys()) {
	                if (rs.next()) {
	                    idGenerado = rs.getInt(1);
	                    user.setId(idGenerado);
	                }
	            }
	        }
	    }
	    return idGenerado;
	}
	
	private User construirUser(ResultSet rs) throws SQLException {
	    int id = rs.getInt("id");
	    String nombre = rs.getString("nombre");
	    String apellido = rs.getString("apellido");
	    String email = rs.getString("email");
	    String password = rs.getString("password");
	    String avatarPath = rs.getString("avatar_path");
	    String usuarioStr = rs.getString("usuario");
	    String penalizacionStr = rs.getString("penalizacion_hasta");
	    String rolStr = rs.getString("rol");
	    
	    
	    // Usamos el constructor existente, luego establecemos campos adicionales
	    User user = new User(id, nombre, apellido); 
	    user.setEmail(email);
	    user.setPassword(password);
	    user.setAvatarPath(avatarPath);
	    user.setUsuario(usuarioStr);
	    
	    // Convertimos el String a LocalDate (si no es null)
	    if (penalizacionStr != null) {
	        user.setPenalizacionHasta(LocalDate.parse(penalizacionStr));
	    }
	    if (rolStr != null) {
	        try {
	            user.setRol(User.Rol.valueOf(rolStr));
	        } catch (IllegalArgumentException e) {
	            user.setRol(User.Rol.USER);
	        }
	    } else {
	        user.setRol(User.Rol.USER);
	    }
	    return user;
	}
	
	public void updatePenalizacion(User user) throws SQLException {
	    String sql = "UPDATE User SET penalizacion_hasta = ? WHERE id = ?";
	    
	    try (Connection con = DBConnection.getConnection();
	         PreparedStatement pstmt = con.prepareStatement(sql)) {
	        
	        String penalizacionStr = (user.getPenalizacionHasta() != null) ? user.getPenalizacionHasta().toString() : null;
	        
	        pstmt.setString(1, penalizacionStr);
	        pstmt.setInt(2, user.getId());
	        
	        pstmt.executeUpdate();
	    }
	}
	
	public boolean actualizarpellido(User user) throws SQLException {
	    String sql = "UPDATE User SET \"apellido\" = ? WHERE id = ?";
	    int filasAfectadas = 0;
	
	    try (Connection con = DBConnection.getConnection();
	         PreparedStatement pstmt = con.prepareStatement(sql)) {
	        
	        // 1. Establecer el nuevo apellido
	        pstmt.setString(1, user.getApellido());
	        
	        // 2. Establecer el ID del usuario a actualizar
	        pstmt.setInt(2, user.getId()); 
	
	        // Ejecutar la consulta de actualización
	        filasAfectadas = pstmt.executeUpdate();
	        
	    } catch (SQLException e) {
	        System.err.println("ERROR al actualizar apellido del usuario en BD: " + e.getMessage());
	        throw e; 
	    }
	    
	    return filasAfectadas > 0;
	}
	
	public boolean actualizarEmailYAvatar(User user) throws SQLException {
	    
	    String sql = "UPDATE User SET email = ?, avatar_path = ? WHERE id = ?";
	    int filasAfectadas = 0;
	
	    try (Connection con = DBConnection.getConnection();
	         PreparedStatement pstmt = con.prepareStatement(sql)) {
	
	        pstmt.setString(1, user.getEmail());
	        pstmt.setString(2, user.getAvatarPath());
	        pstmt.setInt(3, user.getId()); 
	
	        filasAfectadas = pstmt.executeUpdate();
	        
	    } catch (SQLException e) {
	        System.err.println("ERROR al actualizar email y avatar del usuario en BD: " + e.getMessage());
	        throw e;
	    }
	    
	    return filasAfectadas > 0;
	}
	
	public User getUserByUsuario(String usuario) throws SQLException {
	    String sql = "SELECT * FROM User WHERE usuario = ?";

	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {

	        stmt.setString(1, usuario);

	        try (ResultSet rs = stmt.executeQuery()) {

	            if (rs.next()) {
	                User user = new User(
	                    rs.getString("nombre"),
	                    rs.getString("apellidos"),
	                    rs.getString("correo"),
	                    rs.getString("telefono"),
	                    rs.getString("usuario"),
	                    rs.getString("password"),
	                    User.Rol.valueOf(rs.getString("rol"))
	                );
	                return user;
	            }
	        }
	    }

	    return null; // si no existe
	}

}
