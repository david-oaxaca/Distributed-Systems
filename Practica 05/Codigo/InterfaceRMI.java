import java.rmi.RemoteException;
import java.rmi.Remote;

/******
*
* @author: David Oaxaca
*
* Escuela: IPN - ESCOM
* Clase: Sistemas Distribuidos
* Profesor: Carlos Pineda Guerrero
* 
* Forma de compilarlo:
* javac InterfaceRMI.java
*
* 
*
*****/

public interface InterfaceRMI extends Remote
{
  //public String mayusculas(String name) throws RemoteException;
  //public int suma(int a,int b) throws RemoteException;
  public double[][] multiplica_matrices(double[][] A, double[][] B, int N) throws RemoteException;
  //public long checksum(int[][] m) throws RemoteException;
}