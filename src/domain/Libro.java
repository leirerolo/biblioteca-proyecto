package domain;


import java.util.Comparator;
import java.util.Objects;

import javax.swing.ImageIcon;

public class Libro implements Comparable<Libro>, Comparator<Libro>{
	private int id; //para la BD
	private String titulo;
	private String autor;
	private ImageIcon portada;
	private double valoracion;
	private String portadaPath; // para poder acceder a la imagen a traves de la BD
	
  // constructor para leer el libro desde la BD
	public Libro(int id, String titulo, String autor, double valoracion, String portadaPath) {
		this.id= id;
		this.titulo = titulo;
		this.autor = autor;
		this.valoracion = valoracion;
		this.portadaPath= portadaPath;
		this.portada= loadImage(portadaPath);
	}
	
	// constructor para poder cargar el libro desde el CSV
	
	public Libro(String titulo, String autor, ImageIcon portada, double valoracion, String portadaPath) {
		this.titulo= titulo;
		this.autor= autor;
		this.portada= portada;
		this.valoracion= valoracion;
		this.portadaPath = portadaPath;
	}
	
	//para poder cargar las imagenes desde la BD
	
	private ImageIcon loadImage(String path) {
		if(path == null || path.isEmpty()) return null;
		
		String resourcePath = path.replaceFirst("resources images/", "");
		try {
			
			java.net.URL imageUrl = getClass().getClassLoader().getResource(resourcePath);
			
			if (imageUrl != null) {
				
				ImageIcon original= new ImageIcon(imageUrl);
			if (original.getImage()!= null) {
				java.awt.Image img= original.getImage().getScaledInstance(120,160, java.awt.Image.SCALE_SMOOTH);
				return new ImageIcon(img);
			}
			}else {
				System.err.println("Error al cargar la imagen");
			}
		} catch (Exception e) {
			System.err.println("Error al cargar la imagen:  " + path);
		}
		return null;
	}
	public Libro() {
		
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
	
	public double getValoracion() {
		return valoracion;
	}

	public void setValoracion(double valoracion) {
		this.valoracion = valoracion;
	}

	@Override
	public String toString() {
		return "Libro [titulo=" + titulo + ", autor=" + autor + ", portada=" + portada + ", valoracion=" + valoracion
				+ "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(autor, portada, titulo, valoracion);
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
				&& Double.doubleToLongBits(valoracion) == Double.doubleToLongBits(other.valoracion);
	}

	//COMPARAR POR AUTOR
	@Override
	public int compareTo(Libro o) {
		return this.getAutor().compareTo(o.getAutor());
	}

	@Override
	public int compare(Libro o1, Libro o2) {
		return Double.compare(o2.getValoracion(), o1.getValoracion());
	}
	
	
}

