package App;

import javax.swing.*;

import java.awt.*;
import java.io.*;
import java.net.*;

public class Servidor {

	public static void main(String[] args) {

		MarcoServidor mimarco = new MarcoServidor();

		mimarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}
}

class MarcoServidor extends JFrame implements Runnable {

	public MarcoServidor() {

		setBounds(1200, 300, 280, 350);

		JPanel milamina = new JPanel();

		milamina.setLayout(new BorderLayout());

		areatexto = new JTextArea();

		milamina.add(areatexto, BorderLayout.CENTER);

		add(milamina);

		setVisible(true);

		Thread hilo = new Thread(this);

		hilo.start();

	}

	private JTextArea areatexto;

	// una vez implementado el metodo se crea el hilo en el constructor, luego de
	// hacerlo visible
	@Override
	public void run() {

		// utilizando la clase ServerSocket la inicializamos dentro del metodo del hilo
		try {
			ServerSocket ser = new ServerSocket(9999);// aclaro el puerto por el cual va a recibir el dato
			
			/*creamos 3 variables para almacenar info qe llega del objeto y 1 variable para recibir el objeto*/
			
			String nick, ip, msg;
			
			paqueteEnvio paquete;
			
			
			while (true) {
				
				// creo una variable Socket que acepta las conexiones que vegan del exterior
				Socket soc = ser.accept();
				
//---------------------------DETECTA ONLINE --------------------------------------------------------------------------------------------------
				
				// dentro de loc almaceno la direccion del cliente que se acaba de conectar
				InetAddress loc = soc.getInetAddress();
				
				String dir = loc.getHostAddress();
				
				System.out.println("Online- "+dir);
				
/*------------------------------------------------------------------------------------------------------------------------------------------*/
				//recibimos el objeto enviado al servidor
				ObjectInputStream entrada = new ObjectInputStream(soc.getInputStream());
				
				//Leo el Objeto obtenido
				paquete=(paqueteEnvio) entrada.readObject();
				
				if(paquete.getMsg().equals(" Online")) return;
				
				// inicializamos las variables con los datos recibidos 
				nick=paquete.getNick();ip=paquete.getIp();msg=paquete.getMsg();
				
				// imprimimos en pantalla el mensaje
				areatexto.append(nick +": "+msg + "\n" );

				//ENVIAMOS EL PAQUETE AL DESTINATARIO
				Socket envia_destinatario = new Socket(ip,9999);
				
				// creamos un objeto para poder enviar el Objeto recibido
				ObjectOutputStream paqueteEnvio = new ObjectOutputStream(envia_destinatario.getOutputStream());
				
				//enviamos el Objeto
				paqueteEnvio.writeObject(paquete);
				
				//cerramos el socket
				entrada.close();
				paqueteEnvio.close();
				envia_destinatario.close();
				soc.close();
				
				
				/* LINEAS PARA RECIBIR DATOS STRING
				
				// ahora debemos crear un flujo de datos de entrada
				DataInputStream entrada = new DataInputStream(soc.getInputStream());
				
				// almacenamos en la variable el mensaje que viaja por el socket
				String mensaje = entrada.readUTF();
				// imprimimos en pantalla el mensaje
				areatexto.append("\n"+ mensaje);
				// cerramos la conexion
				soc.close();
				*/
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}

	}

}
