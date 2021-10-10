import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.Scanner;
import java.nio.charset.StandardCharsets;

/******
*
* @author: David Oaxaca
* 
* Materia: Sistemas distribuidos
* Profesor: Carlos Pineda
* Grupo: 4CV12
* Practica 01: Chat Multicast
*
* Forma de compilarlo:
* javac Chat.java
*
* Forma de ejecutarlo:
* java Chat [Nombre del usuario]
*
*
*****/

class Chat{

	static class Worker extends Thread{
		public void run(){

			try{

				System.setProperty("java.net.preferIPv4Stack", "true");

				MulticastSocket socket = new MulticastSocket(20000);
				InetSocketAddress grupo = new InetSocketAddress(InetAddress.getByName("230.0.0.0"), 20000);
				NetworkInterface netInter = NetworkInterface.getByName("em1");

				socket.joinGroup(grupo, netInter);

				// En un ciclo infinito se recibiran los mensajes enviados al
				// grupo 230.0.0.0 a través del puerto 20000 y se desplegarán en la pantalla.

				for(;;){

					byte [] a = recibe_mensaje_multicast(socket, 128);
					String mensaje_recibido = (new String(a, "UTF-8")).replaceAll("\0", "");
					System.out.println("\n\n" + mensaje_recibido + "\n");

					System.out.print("\nIngrese el mensaje a enviar: ");

				}

			}catch(Exception e){
				e.printStackTrace();

			}
			
		}
	}
	

	public static void main(String [] args) throws Exception{

		new Worker().start();

		String nombre = args[0];

		Scanner sc = new Scanner(System.in);
		String mensaje = "";

		System.setProperty("java.net.preferIPv4Stack", "true");

		// En un ciclo infinito se leerá cada mensaje del teclado y se enviara el mensaje al
		// grupo 230.0.0.0 a través del puerto 20000

		System.out.print("\n\nIngrese el mensaje a enviar: ");

		for(;;){
		
			mensaje = nombre + ":" + sc.nextLine();
			envia_mensaje_multicast(mensaje.getBytes(StandardCharsets.UTF_8), "230.0.0.0", 20000);
			

		}
	}


	static void envia_mensaje_multicast(byte[] buffer, String ip, int puerto) throws IOException{

		DatagramSocket socket = new DatagramSocket();
		socket.send(new DatagramPacket(buffer, buffer.length, InetAddress.getByName(ip), puerto));

		socket.close();

	}


	static byte[] recibe_mensaje_multicast(MulticastSocket socket, int longitud_mensaje) throws IOException{

		byte[] buffer = new byte[longitud_mensaje];
		DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);

		socket.receive(paquete);
		return paquete.getData();

	}

}