package domain;

import java.time.LocalDate;

public class Reserva {
	private Libro libro;
	private LocalDate fecha;
	private int duracion; //en días
	private int prolongaciones;
	
	//Por defecto se crea la reserva con 14 días (2 semanas) de plazo
	public Reserva(Libro libro, LocalDate fecha) {
		this.libro = libro;
		this.fecha = fecha;
		this.duracion = 14;
		this.prolongaciones=0; //nada más reservarlo, no se ha prolongado nunca
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
