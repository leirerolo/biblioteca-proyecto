package threads;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import domain.Reserva;
import domain.User;
/**
 * Lo que va a hacer este hilo es simular la devolución de préstamos
 * de libros en la biblioteca, ya que al ser una aplicación, no podemos
 * hacer de manera realista que alguien vaya a la biblioteca física y 
 * devuelva el libro, para después actualizar su ventana de reservas
 * 
 */
public class HiloDevoluciones extends Thread {
	private User user; //user que ha hecho loggin
	private Runnable reservaDevuelta; //para actualizar la gui
	private Reserva reserva; //reserva seleccionada en la tabla
	private JProgressBar progressBar; //para mostrar ejecución del hilo
	private JPanel panelIzq;
	private JPanel panelCen;
	private JPanel panelDer; //tres paneles para la simulación visual
	
	public HiloDevoluciones(Reserva reserva, User user, JPanel panelIzq, JPanel panelCen, JPanel panelDer, JProgressBar progressBar, Runnable reservaDevuelta) {
		this.reserva = reserva;
        this.user = user;
        this.panelIzq = panelIzq;
        this.panelCen = panelCen;
        this.panelDer = panelDer;
        this.progressBar = progressBar;
        this.reservaDevuelta = reservaDevuelta;   
	}
	@Override
	public void run() {
		try {
			/**
			 * Simulamos la devolución de préstamos de una biblioteca pública:
			 * 1) el libro se pone en el panel de la izquierda
			 * 2) se pasa al central, que en la realidad sería el sensor que lee el código de barras
			 * 		- el fondo del panel se "ilumina" de verde, simulando que se ha leído correctamente
			 * 3) pasa al panel de la derecha, indicando que ya ha sido procesado
			 */
			//1
			SwingUtilities.invokeLater(() -> {
				panelIzq.removeAll();
				panelCen.removeAll();
				panelDer.removeAll();
				
				panelIzq.add(new JLabel(reserva.getLibro().getPortada(), JLabel.CENTER));
				panelIzq.revalidate();
				panelIzq.repaint();
				    
				panelCen.setBackground(Color.RED); //lector de código de barras a la espera de detectar uno
				panelCen.setOpaque(true);
				panelCen.revalidate();
				panelCen.repaint();
				panelDer.setBackground(Color.LIGHT_GRAY);
				panelDer.setOpaque(true);
				panelDer.revalidate();
				panelDer.repaint();
				
				progressBar.setValue(0); //inicializa el proceso de la devolución
				progressBar.setStringPainted(true);
			});
			for (int i = 1; i<=100; i++) {
				try {
					Thread.sleep(30);
				} catch(InterruptedException e) {
					this.interrupt();
				}
				final int val = i;
				SwingUtilities.invokeLater(() -> {
					progressBar.setValue(val);
				});
				//2
				if (i == 33) { //ha pasado el primer segundo: 1er panel --> 2º
					SwingUtilities.invokeLater(() -> {
						panelIzq.removeAll();
						panelIzq.revalidate();
						panelIzq.repaint();
						
						panelCen.add(new JLabel(reserva.getLibro().getPortada(), JLabel.CENTER));
						panelCen.setBackground(Color.GREEN); //se ha leído el código de barras correctamente
						panelCen.revalidate();
						panelCen.repaint();
					});
				//3
				} else if (i == 66) { // 2º panel --> 3º
					SwingUtilities.invokeLater(() -> {
						panelCen.removeAll();
						panelDer.add(new JLabel(reserva.getLibro().getPortada(), JLabel.CENTER));
						panelDer.revalidate();
						panelDer.repaint();
						panelCen.setBackground(Color.RED);
						panelCen.revalidate();
						panelCen.repaint();
					});
				}
			}
			//al terminar, actualizo las reservas y la gui
			SwingUtilities.invokeLater(reservaDevuelta);
			
		} catch(Exception o) {
			o.printStackTrace();
		}
	}
}
