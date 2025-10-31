package main;
import domain.*;
import gui.*;

import db.DBManager;
import db.BookDAO;
import domain.Libro;
import java.awt.Image;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;


public class Main {

	public static void main(String[] args) {
		
		DBManager.createTables();
	
		List<Libro> libros = BookDAO.getAllBooks();
		
		// verificar si la BD está vacía 
		if (libros.isEmpty()) {
			System.out.println("La BD está vacía, cargando desde el csv...");
			List<Libro> librosCSV = cargarLibrosCSV("libros.csv");
			
			for (Libro libro: librosCSV) {
				BookDAO.insertBook(libro); //añadimos el libro a la BD usando PreparedStatement
			}
			System.out.println("Se han cargado todos los libros");
	
		
			libros = BookDAO.getAllBooks();
	
		}
		final List<Libro> librosFinales = libros;
		
		if(!librosFinales.isEmpty()) {
			SwingUtilities.invokeLater(() -> {
				JFramePrincipal ventana = new  JFramePrincipal(librosFinales);
				ventana.setVisible(true);
            });
		} else {
			System.out.println("No se han encontrado ningún libro para mostrar");
		}
	}

	private static List<Libro> cargarLibrosCSV(String fichero) {
		File f = new File(fichero);
		List<Libro> listaLibros = new ArrayList<>();
		try {
			Scanner sc = new Scanner(f);
			while(sc.hasNextLine()) {
				String linea = sc.nextLine();
				String[] campos = linea.split(";");
				
				String pathImagenCSV = campos[2].trim();
				String pathImagenCompleto= "resources images/" + pathImagenCSV;
				double valoracion = Double.parseDouble(campos[3].trim().replace(",", "."));
				ImageIcon portada = null;
				
				try {
					ImageIcon original = new ImageIcon(pathImagenCompleto);
					if (original.getImage() != null) {
						java.awt.Image img = original.getImage().getScaledInstance(120,160, java.awt.Image.SCALE_SMOOTH);
						portada = new ImageIcon(img);
					}
				} catch (Exception e) {
					System.err.println("Warning: la imagen no ha podido ser cargada: " + pathImagenCompleto);
				}
				
				Libro libro = new Libro(campos[0].trim(), campos[1].trim(), portada, valoracion, pathImagenCompleto);
				listaLibros.add(libro);
			}
			
		} catch(FileNotFoundException e) {
			System.out.println("Error al leer el fichero CSV" + e.getMessage()); 
		}
		return listaLibros;
	}
	
	
}
