import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/******
*
* @author: David Oaxaca
*
* Escuela: IPN - ESCOM
* Clase: Sistemas Distribuidos
* Profesor: Carlos Pineda Guerrero
* 
* Forma de compilarlo:
* javac ClaseRMI.java
*
* 
*
*****/

public class ClaseRMI extends UnicastRemoteObject implements InterfaceRMI
{

  // es necesario que el contructor ClaseRMI() invoque el constructor de la superclase
  public ClaseRMI() throws RemoteException
  {
    super( );
  }
  
  /*
  ***Metodos previamente usados en un ejemplo visto en clase:

  public String mayusculas(String s) throws RemoteException
  {
    return s.toUpperCase();
  }
  public int suma(int a,int b)
  {
    return a + b;
  }
  */

  /***
  *
  * Parametros: Dos matrices de tipo double (double [][]) las cuales multipicaran y un int de las dimensiones de estas matrices (NxN)
  * 
  * Return:     Una matriz de tipo double (double[][]) la cual sera el producto de las dos matrices pasadas
  * 
  * Brief:      Esta funcion obtiene la matriz producto de dos matrices 
  *
  ***/

  public double[][] multiplica_matrices(double[][] A, double[][] B, int N) throws RemoteException
  {
    
    double [][] C = new double[N/3][N/3];
    for(int i = 0; i < N/3; i++)
      for(int j = 0; j < N/3; j++)
        for(int k = 0; k < N; k++)
          C[i][j] += A[i][k] * B[j][k];
    return C;
  }
  
}