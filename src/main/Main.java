package main;

import domain.*;
import gui.*;

import java.awt.Image;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import persistence.AppState;

public class Main {
    public static List<Libro> librosGlobales; // para acceder a ella desde User

    public static void main(String[] args) {
    	//COMENTADOS: para cuando funcione la BD 
    	//DBManager.createTables(); 
    	//List<Libro> libros = BookDAO.getAllBooks(); 
    	// verificar si la BD está vacía
    	/*if (libros.isEmpty()) { System.out.println("La BD está vacía, cargando desde el csv..."); 
    	 * List<Libro> librosCSV = cargarLibrosCSV("libros.csv"); 
    	 * for (Libro libro: librosCSV) { BookDAO.insertBook(libro); 
    	 * //añadimos el libro a la BD usando PreparedStatement } 
    	 * System.out.println("Se han cargado todos los libros"); 
    	 * libros = BookDAO.getAllBooks(); }*/
        SwingUtilities.invokeLater(() -> {
            // 1) Cargar catálogo (CSV)
            List<Libro> libros = cargarLibrosCSV("libros.csv");
            librosGlobales = libros;

            if (libros.isEmpty()) {
                System.out.println("No se han encontrado libros para mostrar");
                System.exit(0);
                return;
            }

            // 2) Mostrar login (integrado con AppState/AuthService)
            JDialogLogin dlg = new JDialogLogin(null);
            dlg.setVisible(true);

            User u = dlg.getLoggedUser();
            if (u != null) {
                persistence.AppState state = dlg.getAppState();

                Navigator.init(librosGlobales); // si lo usas
                JFramePrincipal frame = new JFramePrincipal(librosGlobales, "inicio", state);
                frame.setCurrentUser(u);
                
                JFrameInicio frameSalida = new JFrameInicio(librosGlobales);
                frameSalida.setVisible(true);
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
                    ImageIcon original = new ImageIcon(pathImagenCSV);
                    if (original.getImage() != null) {
                        Image img = original.getImage().getScaledInstance(120, 160, Image.SCALE_SMOOTH);
                        portada = new ImageIcon(img);
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
    public static void guardarLibrosCSV(String fichero, List<Libro> libros) {
    	try (PrintWriter pw = new PrintWriter(fichero)) {
    		
    		for (Libro l : libros) {
    			pw.println(l.getTitulo() + ";"
    					+ l.getAutor() + ";" 
    					+ l.getPortadaPath() + ";"
    					+ String.format("%.2f", l.getValoracion()) + ";"
    					+ l.getNumValoraciones());
    		}
    		
    	} catch(Exception e) {
    		System.err.println("Error al guardar CSV: " + e.getMessage());
    	}
    }
}

