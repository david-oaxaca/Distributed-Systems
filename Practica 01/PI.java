import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/***********************************************************************
*
* @author: David Oaxaca
* 
* Materia: Sistemas distribuidos
* Profesor: Carlos Pineda
* Grupo: 4CV12
* Practica 01: Calculo de PI por medio de una topologia de estrella
* 
* Forma de compilarlo: 
* javac PI.java 
* 
* Forma de ejecutarlo: 
* java PI [numero de nodo]
*
*************************************************************************/

class PI{
	
	static Object obj = new Object();
	static float pi = 0;

	static class Worker extends Thread{
		Socket conexion;
		Worker(Socket conexion){

			this.conexion = conexion;
		}

		public void run(){
			//Algoritmo 1

			try{

				DataInputStream entrada = new DataInputStream(conexion.getInputStream());
				DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());

				float suma = entrada.readFloat();
				synchronized(obj){
					pi += suma;
				}

				entrada.close();
				salida.close();
				conexion.close();

			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}


	public static void main(String[] args) throws Exception{


		if(args.length != 1){

			System.err.println("Uso:");
			System.err.println("java PI <nodo>");
			System.exit(0);

		}

		int nodo = Integer.valueOf(args[0]);
		if(nodo == 0){

			//Algoritmo 2

			ServerSocket servidor = new ServerSocket(20000);
			Worker[] v = new Worker[4];

			for(int i = 0; i < 4; i++){
				Socket conexion = servidor.accept();
				v[i] = new Worker(conexion);
            	v[i].start();
			}

			for(int i = 0; i < 4; i++){
				v[i].join();
			}

			System.out.println(pi);

		}else{

			//Algoritmo 3

			Socket conexion = null;
			conexion = new Socket("localhost", 20000);
			
			DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());			
			DataInputStream entrada = new DataInputStream(conexion.getInputStream());

			float suma = 0;

			for(int i = 0; i < 1000000; i++){
				suma += (4.0/(8*i+2*(nodo-2)+3));
			}

			suma = nodo%2 == 0? -suma:suma;
			salida.writeFloat(suma);

			salida.close();
			entrada.close();
			conexion.close();
		}

	}
}