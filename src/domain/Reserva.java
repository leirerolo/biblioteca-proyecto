package domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class Reserva implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private Libro libro;
	private LocalDate fecha;
	private int duracion; //en días
	private int prolongaciones;
	private User user;
	private boolean devuelto = false; //por defecto, al crear la reserva no está devuelta
	
	private double valoracionUsuario;
	
	//Por defecto se crea la reserva con 14 días (2 semanas) de plazo
	public Reserva(Libro libro, User user) {
		this.libro=libro;
		this.fecha=LocalDate.now();
		this.duracion=14;
		this.prolongaciones=0;
		this.user=user;
		
		this.valoracionUsuario = 0;
	}
	
	//para cuando cargamos desde CSV
	public Reserva(Libro libro, User user, LocalDate fecha) {
		this.libro=libro;
		this.user=user;
		this.fecha=fecha;
		this.duracion=14;
		this.prolongaciones=0;
		this.valoracionUsuario=0;
	}

	public Libro getLibro() {
		return libro;
	}
	public void setLibro(Libro libro) {
		this.libro = libro;
	}
	public LocalDate getFecha() {
		return fecha;
	}
	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}
	public User getUser() {
		return this.user;
	}
	public void setUser(User user) {
		this.user=user;
	}
	public int getDuracion() {
		return duracion;
	}
	public void setDuracion(int duracion) {
		this.duracion = duracion;
	}
	public int getProlongaciones() {
		return prolongaciones;
	}
	public void setProlongaciones(int prolongaciones) {
		this.prolongaciones=prolongaciones;
	}
	/** Devuelve la fecha de vencimiento = fecha + duración */
    public LocalDate getVencimiento() {
        return this.fecha.plusDays(this.duracion);
    }
	
	 /** Indica si la reserva ya está vencida (hoy es posterior al vencimiento) */
    public boolean isVencida() {
        return LocalDate.now().isAfter(getVencimiento());
    }
    
    
	//obtener valoracion y calificar
	public double getValoracionUsuario() {
		return valoracionUsuario;
	}

	 public void setValoracionUsuario(double valoracionUsuario) {
		 this.valoracionUsuario = valoracionUsuario;
	 }
	 
	public boolean isDevuelto() {
		return devuelto;
	}

	public void setDevuelto(boolean devuelto) {
		this.devuelto = devuelto;
	}

	//método para prolongar la reserva
	public void prolongar() {
		//solo se puede prolongar hasta 2 veces
		if (this.getProlongaciones()<2) {
			this.setProlongaciones(this.getProlongaciones()+1);
			this.setDuracion(this.getDuracion()+7); //se suma una semana al plazo
		}
	}
	
	//método para obtener el plazo restante
	public int getDiasRestantes() {
		LocalDate hoy = LocalDate.now();
		LocalDate vencimiento = this.fecha.plusDays(duracion);
		int diasRestantes = (int)ChronoUnit.DAYS.between(hoy, vencimiento); //CHAT GPT para ChronoUnit.days.between
		
		//ya ha vencido: ponemos a -1
		if (diasRestantes<0) {
			return 0;
		}
		return diasRestantes;
	}

	@Override
	public int hashCode() {
		return Objects.hash(libro, user);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Reserva other = (Reserva) obj;
		return Objects.equals(libro, other.libro) && Objects.equals(user, other.user);
	}
	
	@Override
    public String toString() {
        return "Reserva{" +
                "libro=" + (libro != null ? libro.getTitulo() : "null") +
                ", fecha=" + fecha +
                ", duracion=" + duracion +
                ", prolongaciones=" + prolongaciones +
                ", vencimiento=" + getVencimiento() +
                ", valoracion=" + getValoracionUsuario() + '}';
    }
	
	
}
