import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;
import java.io.FileOutputStream;

/******
*
* @author: David Oaxaca
* 
* Materia: Sistemas distribuidos
* Profesor: Carlos Pineda
* Grupo: 4CV12
* Practica 02: Implementación de un token ring para el incremento de un contador
* 
* Forma de compilarlo:
* javac Token.java
*
* Forma de ejecutarlo:
* java Token [Numero de nodo] [Dirección IP del servidor]
*
*
*****/


class Token{
	
	static DataInputStream entrada;
	static DataOutputStream salida;
	static boolean inicio = true;
	static String ip;
	static int nodo;
	static long token = 0;

	static class Worker extends Thread{

		public void run(){

			//Algoritmo 1

			try{

				System.setProperty("javax.net.ssl.keyStore","keystore_servidor.jks");
        		System.setProperty("javax.net.ssl.keyStorePassword", "1234567");

				SSLServerSocketFactory socket_factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
				ServerSocket servidor = socket_factory.createServerSocket(20000+nodo);

				Socket conexion = servidor.accept();
				entrada = new DataInputStream(conexion.getInputStream());

			}catch(Exception e){
				e.printStackTrace();
			}

		}

	}

	public static void main(String[] args) throws Exception{


		if(args.length != 2){

			System.err.println("Se debe pasar como parametros el numero del nodo y la IP del siguiente nodo en el anillo");
			System.exit(1);

		}

		nodo = Integer.valueOf(args[0]);
		ip = args[1];

		//Algoritmo 2

		Worker w = new Worker();
		w.start();

		Socket conexion = null;

		System.setProperty("javax.net.ssl.trustStore", "keystore_cliente.jks");
		System.setProperty("javax.net.ssl.trustStorePassword", "123456");

		for(;;){
			try{
				SSLSocketFactory cliente = (SSLSocketFactory) SSLSocketFactory.getDefault();
        		conexion = cliente.createSocket(ip, 20000 + (nodo+1)%4);
				break;
			}catch(Exception e){

				Thread.sleep(500);

			}
		}
			

		salida = new DataOutputStream(conexion.getOutputStream());

		w.join();

		for (;;) {
			
			if(nodo == 0){
				if(inicio == true){
					inicio = false;
					token = 1;
				}else{

					token = entrada.readLong() + 1;
					
					
					System.out.println("Nodo: " + nodo);
					System.out.println("Token: " + token);
				}
			}else{
				token = entrada.readLong() + 1;

				if(token > 1000)break;
				
				System.out.println("Nodo: " + nodo);
				System.out.println("Token: " + token);
			}

			if(nodo == 0 && token >= 1000) break;
			

			salida.writeLong(token);
		}

		salida.writeLong(token);

		salida.close();
        entrada.close();
        conexion.close();


	}
}