package main;

import domain.*;
import domain.User.Rol;
import gui.*;

import java.awt.Image;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.sql.SQLException;


import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import persistence.AppState;
import db.DBConnection;
import db.LibroDAO;
import db.UserDAO;

public class Main { 
    public static List<Libro> librosGlobales; // para acceder a ella desde User

    private static final transient LibroDAO libroDAO = new LibroDAO();
    
    public static void main(String[] args) {
    	DBConnection.createTables();
    	
    	// *************** ADMIN **************************
    	//el usuario administrador lo predefinimos
    	//será siempre este, y cuando se haga loggin con sus datos, se entrará como administrador a la app
    	UserDAO user = new UserDAO();
    	User admin = new User("Admin", "Root", "admin@lib.com", null, "admin", "1234", Rol.ADMIN);
    	try {
    		user.registerUser(admin);
    	} catch(SQLException e) {
    		e.printStackTrace();
    	}
    	
    	List<Libro> libros = new ArrayList<>();
    	try {
            libros = libroDAO.getTodosLosLibros();
            
            if (libros.isEmpty()) { 
                System.out.println("La BD está vacía. Cargando datos iniciales desde 'libros.csv'...");
                
                List<Libro> librosCSV = cargarLibrosCSV("libros.csv"); 
                
                for (Libro libro: librosCSV) {  
                    libroDAO.insertaLibro(libro); 
                }
                System.out.println("Se han cargado " + librosCSV.size() + " libros en la base de datos.");
                libros = libroDAO.getTodosLosLibros(); 
            }
            
            librosGlobales = libros; 
            
            if (librosGlobales.isEmpty()) {
                System.out.println("No se han encontrado libros para mostrar. Cerrando.");
                System.exit(0);
                return;
            }

        } catch (SQLException e) {
            System.err.println("ERROR  de conexión a la BD: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
            return;
        }
    	
        SwingUtilities.invokeLater(() -> {

            // 2) Mostrar login (integrado con AppState/AuthService)
            JDialogLogin dlg = new JDialogLogin(null);
            dlg.setVisible(true);

            User u = dlg.getLoggedUser();
            if (u != null) {
                persistence.AppState state = dlg.getAppState();
                JFramePrincipal principalFrame = new JFramePrincipal(librosGlobales, "inicio", state);
                principalFrame.setCurrentUser(u);
                
                // ******************* ADMINISTRADOR ***********************
                // abre su ventana de administrador
                /*if (u.getRol() == Rol.ADMIN) {
                	JFrameAdmin adminFrame = new JFrameAdmin(librosGlobales);
                	adminFrame.setVisible(true);
                	Navigator.init(adminFrame, librosGlobales);
                	Navigator.showAdmin();
                	
                	
                // **************** USER NORMAL *****************************
                //Abre inicio
                } else {*/
                	JFrameInicio inicioFrame = new JFrameInicio(librosGlobales);
                	inicioFrame.setVisible(true);
                	Navigator.init(inicioFrame, librosGlobales);
                	Navigator.showInicio();
                //}
                
            } else {
                System.out.println("Login cancelado. Cerrando aplicación.");
                System.exit(0);
            }
            
        });
    }

    private static List<Libro> cargarLibrosCSV(String fichero) {
        File f = new File(fichero);
        List<Libro> listaLibros = new ArrayList<>();
        try (Scanner sc = new Scanner(f)) {
            while (sc.hasNextLine()) {
                String linea = sc.nextLine();
                if (linea.isBlank()) continue;

                String[] campos = linea.split(";");
                String pathImagenCSV = campos[2].trim();
                double valoracion = Double.parseDouble(campos[3].trim().replace(",", "."));
                int numValoraciones = 1;
                
                //si el fichero ya ha sido actualizado con nuevas valoraciones
                if (campos.length>4) {
                	numValoraciones = Integer.parseInt(campos[4].trim());
                }


                
                ImageIcon portada = null;
                try {
                	File imgFile = new File(pathImagenCSV);
                    if (imgFile.exists()) {
                        ImageIcon original = new ImageIcon(imgFile.getAbsolutePath());
                        Image img = original.getImage().getScaledInstance(120, 160, Image.SCALE_SMOOTH);
                        portada = new ImageIcon(img);
                    } else {
                        System.err.println("No se encontró la imagen: " + imgFile.getAbsolutePath());
                    }
                } catch (Exception e) {
                    System.err.println("Warning: la imagen no ha podido ser cargada: " + pathImagenCSV);
                }

                Libro libro = new Libro(campos[0].trim(), campos[1].trim(), portada, valoracion, pathImagenCSV);
                libro.setNumValoraciones(numValoraciones);
                listaLibros.add(libro);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error al leer el fichero CSV: " + e.getMessage());
        }
        return listaLibros;
    }
    
    //para la persistencia de valoraciones
    
    }


