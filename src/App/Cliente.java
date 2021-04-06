package App;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.*; // importo paquete para utilizar el socket
import java.util.ArrayList;

import javax.swing.*;

public class Cliente {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		MarcoCliente mimarco = new MarcoCliente();

		mimarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

}

class MarcoCliente extends JFrame {

	public MarcoCliente() {

		setBounds(600, 300, 280, 350);

		LaminaMarcoCliente milamina = new LaminaMarcoCliente();

		add(milamina);

		setVisible(true);

		// agrego el evento para que funcione
		addWindowListener(new EnvioOnline());

	}

}

//----------------------------------------------- EVENTO DE VENTANA QUE DETECTA ONLINE AL CLIENTE QUE ABRE LA APP --------------------------------

class EnvioOnline extends WindowAdapter {

	// evento que se activa cuando se abre la ventana
	@Override
	public void windowOpened(WindowEvent r) {
		try {

			Socket m = new Socket("192.168.56.1", 9999);
			paqueteEnvio dato = new paqueteEnvio();
			dato.setMsg(" Online");

			// creo el flujo de datos
			ObjectOutputStream paq = new ObjectOutputStream(m.getOutputStream());
			// envio los datos mediante el flujo de datos creado
			paq.writeObject(dato);
			// cerramos el socket
			m.close();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

}

class LaminaMarcoCliente extends JPanel implements Runnable {

	public LaminaMarcoCliente() {

		nick = JOptionPane.showInputDialog("Ingrese nombre:");

		JLabel name = new JLabel("De-");
		add(name);

		var n = new JLabel(nick);
		add(n);

		JLabel texto = new JLabel("-CHAT-");
		add(texto);

		ip = new JComboBox<String>();

		add(ip);

		areatexto = new JTextArea(12, 20);

		add(areatexto);

		campo1 = new JTextField(20);

		add(campo1);

		miboton = new JButton("Enviar");

		// creo intancia de clase
		enviatexto env = new enviatexto();
		// añado evento al boton
		miboton.addActionListener(env);

		add(miboton);

		Thread h = new Thread(this);

		h.start();

	}

	@Override
	public void run() {

		// este hilo estara pendiente de entradas de datos

		try {

			// creo el server socket
			ServerSocket serv = new ServerSocket(9087);

			while (true) {
				// instancio la variable de tipo socket que retorna el metodo ACCEPT()
				Socket soc = serv.accept();

				// Creo las variables que almacenen los datos del Objeto
				paqueteEnvio datos;
				String nick, msg;

				// Obtengo el objeto enviado
				ObjectInputStream msn = new ObjectInputStream(soc.getInputStream());

				// capturo el Objeto con el metodo readObject y lo casteo
				datos = (paqueteEnvio) msn.readObject();

				// inicializo las variables
				nick = datos.getNick();
				msg = datos.getMsg();

				// las imprimo en la pantalla
				if (msg.equals(" Online")) {
					var ips = datos.getListaConect();
					areatexto.append(ips + " \n");
					for (String dir : ips) {
						ip.addItem(dir);
					}
				} else {
					areatexto.append(nick + ": " + msg + " " + datos.getIp() + " \n");
				}
				// cierro conexion de Socket
				soc.close();
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	// creo clase interna para enviar datos al presionar el boton
	private class enviatexto implements ActionListener {

		// para que funcione tenemos que instanciarlo antes de agregarlo a la lamina

		@Override
		public void actionPerformed(ActionEvent e) {
			// creamos el socket dentro de este metodo

			try {

				// estamos diciendo que se comunique con ese servidor ip y a ese puerto
				Socket miso = new Socket("192.168.56.1", 9999);

				/* ENVIAMOS DATOS DE TIPO OBJETO PR EL SERVIDOR */
				// empaquetamos los datos del mensaje
				paqueteEnvio env = new paqueteEnvio();
				env.setNick(nick);
				env.setIp((String) ip.getSelectedItem());
				env.setMsg(campo1.getText());

				// lo enviamos por el servidor mediante ObjectOutputStream para enviar objetos
				ObjectOutputStream f_salida = new ObjectOutputStream(miso.getOutputStream());

				// este metodo envia el objeto entero
				f_salida.writeObject(env);

				// cerramos conexion
				f_salida.close();
				miso.close();

				/*
				 * ENVIAMOS DATOS DE TIPOS STRING MEDIANTE SERVIDOR
				 * 
				 * //ahora debemos crear el flujo de datos para enviar la info // le decimos por
				 * donde van a circular los datos DataOutputStream data = new
				 * DataOutputStream(miso.getOutputStream());
				 * 
				 * //ingrasamos en el flujo lo que introduce en el cammpo de texto
				 * data.writeUTF(campo1.getText());
				 * 
				 * //imprimimos el mensaje enviado en el chat areatexto.append(nick+": "+
				 * campo1.getText());
				 * 
				 * 
				 * //cerramos flujo de datos data.close();
				 */

			} catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
				System.out.println(e1.getMessage());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				System.out.println(e1.getMessage());
			}

			// ahora debemos crear el flujo de datos para enviar la info
		}

	}

	private JTextArea areatexto;
	private String nick;
	private JComboBox<String> ip;
	private JTextField campo1;

	private JButton miboton;

}

// creo una clase que almacene los datos del mensaje empaquetado

class paqueteEnvio implements Serializable {
	// se debe serializar paa poder enviarlo por la red ( lo convertimos en grupo de
	// bytes)
	private String nick, ip, msg;

	// creo lista que contenga ips de conectados
	private ArrayList<String> ipConect = new ArrayList<String>();

	public String getNick() {
		return nick;
	}

	public String getIp() {
		return ip;
	}

	public String getMsg() {
		return msg;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public ArrayList<String> getListaConect() {
		return ipConect;
	}

	public void setListaConect(ArrayList<String> ipConect) {
		this.ipConect = ipConect;
	}
}