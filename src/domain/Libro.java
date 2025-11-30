package domain;


import java.util.Comparator;
import db.LibroDAO;

import java.io.File;
import java.sql.SQLException;
import java.util.Objects;

import javax.swing.ImageIcon;

public class Libro implements Comparable<Libro>, Comparator<Libro>{
	private static final transient LibroDAO libroDAO = new LibroDAO();
	private int id; //para la BD
	private String titulo;
	private String autor;
	private ImageIcon portada;
	
	private double valoracionOriginal; // del CSV
    private double valoracionMedia; //media actual
    private int numValoraciones; // contador de valoraciones para hacer la media
	
    private String portadaPath; // para poder acceder a la imagen a traves de la BD
	
  // constructor para leer el libro desde la BD
	public Libro(int id, String titulo, String autor, double valoracion, String portadaPath) {
		this.id= id;
		this.titulo = titulo;
		this.autor = autor;
		this.valoracionOriginal = valoracion;
        this.valoracionMedia = valoracion;
		this.portadaPath= portadaPath;
		this.portada= loadImage(portadaPath);
	}
	
	// constructor para poder cargar el libro desde el CSV
	
	public Libro(String titulo, String autor, ImageIcon portada, double valoracion, String portadaPath) {
		this.titulo= titulo;
		this.autor= autor;
		this.portada= portada;
		this.valoracionOriginal = valoracion;
        this.valoracionMedia = valoracion;
		this.portadaPath = portadaPath;
	}
	
	public void setId(int id) {
        this.id = id;
    }
	
	
	private ImageIcon loadImage(String path) {
		if(path == null || path.isEmpty()) return null;
		
		//String resourcePath = path.replaceFirst("images/", "");
		try {
			
			//java.net.URL imageUrl = getClass().getClassLoader().getResource(resourcePath);
			
//			if (imageUrl != null) {
//				
//				ImageIcon original= new ImageIcon(imageUrl);
//				if (original.getImage()!= null) {
//					java.awt.Image img= original.getImage().getScaledInstance(120,160, java.awt.Image.SCALE_SMOOTH);
//					return new ImageIcon(img);
//				}
//			}else {
//				System.err.println("Error al cargar la imagen");
//			}
			
			File imgFile = new File(path); 
	        if (imgFile.exists()) {
	            ImageIcon original = new ImageIcon(imgFile.getAbsolutePath());
	            if (original.getImage() != null) {
	                java.awt.Image img = original.getImage().getScaledInstance(120, 160, java.awt.Image.SCALE_SMOOTH);
	                return new ImageIcon(img);
	            }
	        } else {
	            System.err.println("No se encontró la imagen en: " + imgFile.getAbsolutePath());
	        }
			
		} catch (Exception e) {
			System.err.println("Error al cargar la imagen:  " + path);
		}
		return null;
	}
	public Libro() {
		
	}
	public int getNumValoraciones() {
		return numValoraciones;
	}
	public void setNumValoraciones(int numValoraciones) {
		this.numValoraciones=numValoraciones;
	}
	
	public int getId() {
		return id;
	}


	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getAutor() {
		return autor;
	}

	public void setAutor(String autor) {
		this.autor = autor;
	}

	public ImageIcon getPortada() {
		return portada;
	}

	public void setPortada(ImageIcon portada) {
		this.portada = portada;
	}

	public String getPortadaPath() {
		return portadaPath;
	}
	
	public void setPortadaPath(String portadaPath) {
		this.portadaPath = portadaPath;
	}
	
    public double getValoracionOriginal() {
        return valoracionOriginal;
    }

    public double getValoracion() {
        return valoracionMedia;
    }

    public void setValoracion(double valoracionMedia) {
        this.valoracionMedia = valoracionMedia;
    }
    
	
	

	
	
public void aplicarNuevaValoracion(double nuevaValoracion) {
        
        double totalAcumulado = this.valoracionMedia * this.numValoraciones;
        this.numValoraciones++;
        double nuevoTotal = totalAcumulado + nuevaValoracion;
        this.valoracionMedia = nuevoTotal / this.numValoraciones;
        
        try {
            libroDAO.updateRating(this); 
            System.out.println("Rating actualizado para '" + this.titulo + "' y persistido en la Base de Datos.");
            
        } catch (SQLException e) {
            System.err.println("Error al guardar el nuevo rating en la BD: " + e.getMessage());
            this.numValoraciones--; 
            this.valoracionMedia = totalAcumulado / this.numValoraciones; 
        }
    }

	@Override
	public String toString() {
		return "Libro [titulo=" + titulo + ", autor=" + autor + ", portada=" + portada + 
				", valoracionOriginal=" + valoracionOriginal + ", valoracionMedia=" + valoracionMedia
				+ "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(autor, portada, titulo, valoracionOriginal);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Libro other = (Libro) obj;
		return Objects.equals(autor, other.autor) && Objects.equals(portada, other.portada)
				&& Objects.equals(titulo, other.titulo)
				&& Double.doubleToLongBits(valoracionOriginal) == Double.doubleToLongBits(other.valoracionOriginal);
	}

	//COMPARAR POR AUTOR
	@Override
	public int compareTo(Libro o) {
		return this.getAutor().compareTo(o.getAutor());
	}

	//comparar por valoracion
	@Override
	public int compare(Libro o1, Libro o2) {
		return Double.compare(o2.getValoracion(), o1.getValoracion());
	}
	
	//comparar por titulo
	public static final Comparator<Libro> COMPARADOR_TITULO = new Comparator<Libro>() {
	    @Override
	    public int compare(Libro l1, Libro l2) {
	        return l1.getTitulo().compareToIgnoreCase(l2.getTitulo());
	    }
	};
	
}

