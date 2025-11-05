package domain;

import java.time.LocalDate;

public class Reserva {
	private Libro libro;
	private LocalDate fecha;
	private int duracion; //en días
	private int prolongaciones;
	private User user;
	
	//Por defecto se crea la reserva con 14 días (2 semanas) de plazo
	public Reserva(Libro libro, User user) {
		this.libro=libro;
		this.fecha=LocalDate.now();
		this.duracion=14;
		this.prolongaciones=0;
		this.user=user;
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
	
	//método para prolongar la reserva
	public void prolongar() {
		//solo se puede prolongar hasta 2 veces
		if (this.getProlongaciones()<2) {
			this.setProlongaciones(this.getProlongaciones()+1);
			this.setDuracion(this.getDuracion()+7); //se suma una semana al plazo
		}
	}
}
