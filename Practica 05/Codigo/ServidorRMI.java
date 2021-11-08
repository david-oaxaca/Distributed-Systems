import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.Naming;

/******
*
* @author: David Oaxaca
*
* Escuela: IPN - ESCOM
* Clase: Sistemas Distribuidos
* Profesor: Carlos Pineda Guerrero
* 
* Forma de compilarlo:
* javac ServidorRMI.java
*
* Forma de ejecutarlo:
* rmiregistry&  	-> Para el uso de objetos distribuidos mediante RMI
* java ServidorRMI
*
* Notas: Este programa es el servidor de la multiplicación de matrices
* utilizando objetos distribuidos
* 
*
*****/

public class ServidorRMI{
	public static void main(String [] args) throws Exception
	{
		String url = "rmi://localhost/prueba";
		ClaseRMI obj = new ClaseRMI();

		//Registra la instancia en el rmiregistry
		Naming.rebind(url, obj);
	}
}

