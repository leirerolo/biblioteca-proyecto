package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

import domain.Libro;
import domain.Reserva;
import domain.User;

public class JDialogLibro extends JDialog {
	private static final long serialVersionUID = 1L;
	private Font fuenteTitulo = new Font("Comic Sans MS", Font.BOLD, 22);
	private Font fuenteMenu = new Font("Comic Sans MS", Font.BOLD, 18);
	
	public JDialogLibro(JFrame padre, Libro libro) {
		super(padre, "Información del libro", true);
		this.setSize(400, 250);
		this.setLayout(new BorderLayout());
		
		//Información del libro: center 
		JPanel panelInfo = new JPanel(new BorderLayout());
		panelInfo.setBorder(new MatteBorder(0,0,1,0,Color.GRAY));
		JLabel portada = new JLabel(libro.getPortada(), JLabel.CENTER);
		panelInfo.add(portada, BorderLayout.WEST);
		
		JPanel panelDatos = new JPanel(new GridLayout(3,1,0,10));
		JLabel titulo = new JLabel(libro.getTitulo(), JLabel.LEFT);
		titulo.setFont(fuenteTitulo);
		titulo.setBorder(
				BorderFactory.createCompoundBorder(
					new MatteBorder(0, 0, 1, 0, Color.GRAY),
					BorderFactory.createEmptyBorder(10, 12, 10, 10)
				)
		);

		panelDatos.add(titulo);
		
		JLabel autor = new JLabel(libro.getAutor(), JLabel.LEFT);
		autor.setFont(fuenteMenu);
		autor.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panelDatos.add(autor);
		
		JLabel valoracion = new JLabel(String.format("%.2f", libro.getValoracion()), JLabel.LEFT);
		valoracion.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panelDatos.add(valoracion);
		
		panelInfo.add(panelDatos, BorderLayout.CENTER);
		this.add(panelInfo, BorderLayout.CENTER);
		
		//Botones cerrar y reservar: south
		JPanel panelBotones = new JPanel();
		JButton cerrar = new JButton("Cerrar");
		cerrar.setBackground(Color.RED);
		JButton reservar = new JButton("Reservar");
		reservar.setBackground(Color.GREEN);
		
		JButton btnFavorito = new JButton("⭐ Favorito");
		btnFavorito.setBackground(new Color(255, 230, 150));
		
		panelBotones.add(cerrar);
		panelBotones.add(btnFavorito);
		panelBotones.add(reservar);
		
		//COMPORTAMIENTO DE LOS BOTONES
		cerrar.addActionListener((e) -> this.dispose());
		
		// Favoritos (requiere sesión)
		User uFav = User.getLoggedIn();
		if (uFav == null) {
			btnFavorito.setEnabled(false);
			btnFavorito.setToolTipText("Inicia sesión para usar favoritos");
		} else {
			boolean esFav = uFav.esFavorito(libro);
			btnFavorito.setText(esFav ? "★ Quitar favorito" : "⭐ Añadir favorito");
		}
		
		btnFavorito.addActionListener(ev -> {
			User u = User.getLoggedIn();
			if (u == null) {
				// Toast en vez de JOptionPane (más moderno y menos intrusivo)
				Toast.showToast(this, "Inicia sesión para usar favoritos 🔒", Toast.Type.WARNING);
				return;
			}
			boolean ahora = u.toggleFavorito(libro);
			btnFavorito.setText(ahora ? "★ Quitar favorito" : "⭐ Añadir favorito");
			Toast.showToast(this, ahora ? "Añadido a favoritos ⭐" : "Eliminado de favoritos 🗑️", ahora ? Toast.Type.SUCCESS : Toast.Type.INFO);
		});

		reservar.addActionListener((e) -> {
			User user = User.getLoggedIn(); //obtengo el user que tiene la sesión iniciada
			if (user == null) {
				Toast.showToast(this, "Inicia sesión para reservar 📌", Toast.Type.WARNING);
				return;
			}
			Reserva nueva = new Reserva(libro, user);
			
			//primero actualizamos sus reservas
			//y aplicamos la penalización si tiene algún atraso
			user.cargarReservas();
			user.verificarPenalizacion();
			
			if (user.estaPenalizado()) {
				JOptionPane.showMessageDialog(
					this,
					"No puedes reservar. Penalización activa hasta " + user.getPenalizacionHasta() + ".",
					"Penalización activa",
					JOptionPane.WARNING_MESSAGE
				);
				return;
			}
			
			if (!user.getReservas().contains(nueva)) {
				user.agregarReserva(nueva);
				
				// Toast “Reserva realizada” (lo enganchamos a la ventana padre para que se vea bien)
				java.awt.Window owner = getOwner();
				Toast.showToast(owner != null ? owner : this, "Reserva realizada ✅", Toast.Type.SUCCESS);
				
				this.dispose();
				//obtenemos el padre, que es jframe principal o hereda de él
				JFramePrincipal padreFrame = (JFramePrincipal) getParent();
				if (padreFrame.libros.contains(libro)) {
					//elimino el libro de la lista de libros que tiene la ventana padre
					padreFrame.libros.remove(libro);
				}
				
				if (Navigator.inicio != null) {
					Navigator.inicio.refrescarTopLibros(); // para que desaparezca de inicio
				}
				//abro la ventana de reservas con esta ya incluida
				Navigator.showReservas();
				
			} else {
				Toast.showToast(this, "Ya has reservado este libro", Toast.Type.WARNING);
			}
			
		});
		
		this.add(panelBotones, BorderLayout.SOUTH);
		this.setResizable(false);
		this.setLocationRelativeTo(padre);
	}
	
}
