import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.BufferedOutputStream;

/******
*
* @author: David Oaxaca
* 
* Materia: Sistemas distribuidos
* Profesor: Carlos Pineda
* Grupo: 4CV12
* Practica 03: Multiplicación de matrices distribuida utilizando paso de mensajes
* 
* Forma de compilarlo:
* javac Matriz.java
*
* Forma de ejecutarlo:
* java Matriz [ID del nodo] [Dirección IP del servidor]
*
*
*****/


class Matriz
{
  static String ip;
  static String nodo;
  static int N = 1500;
  static long [][] A = new long[N][N];
  static long [][] B = new long[N][N];
  static long [][] C = new long[N][N];
  static long [][] A1 = new long[N/2][N];
  static long [][] B1 = new long[N/2][N];
  static long [][] A2 = new long[N/2][N];
  static long [][] B2 = new long[N/2][N];
  static long [][] C1;
  static long [][] C2;
  static long [][] C3;
  static long [][] C4;  

  static class Worker extends Thread{

    Socket conexion;
    Worker(Socket conexion){
      this.conexion = conexion;
    }

    public void run(){

      try{

        System.out.println("Nodo conectado, iniciando calculo de multiplicacion de matrices...");

        //Declaramos object streams para enviar las submatrices serializadas como un objeto (Y tambien un String)

        ObjectOutputStream object_salida = new ObjectOutputStream(conexion.getOutputStream());
        ObjectInputStream object_entrada = new ObjectInputStream(conexion.getInputStream());
        
        
        //Los objetos recibidos los casteamos a una variable, en este caso, de tipo String
        String id_nodo = (String) object_entrada.readObject();

        //De acuerdo con el ID de nodo recibido enviara las submatrices correspondientes

        switch(id_nodo){

          case "M2019630376-1":
            object_salida.writeObject(A1);
            object_salida.writeObject(B1);

            C1 = (long[][]) object_entrada.readObject();

            //Impresión de la submatriz que esta recibiendo
            //System.out.println("Matriz C1 recibida: ");
            //matrixPrint(C1);
            break;
          case "M2019630376-2":
            object_salida.writeObject(A1);
            object_salida.writeObject(B2);

            C2 = (long[][]) object_entrada.readObject();

            //Impresión de la submatriz que esta recibiendo
            //System.out.println("Matriz C2 recibida: ");
            //matrixPrint(C2);
            break;
          case "M2019630376-3":
            object_salida.writeObject(A2);
            object_salida.writeObject(B1);

            C3 = (long[][]) object_entrada.readObject();

            //Impresión de la submatriz que esta recibiendo
            //System.out.println("Matriz C3 recibida: ");
            //matrixPrint(C3);
            break;
          case "M2019630376-4":
            object_salida.writeObject(A2);
            object_salida.writeObject(B2);

            C4 = (long[][]) object_entrada.readObject();

            //Impresión de la submatriz que esta recibiendo
            //System.out.println("Matriz C4 recibida: ");
            //matrixPrint(C4);
            break;
          default:
            System.out.println("No es un nodo registrado para este sistema");
            break;

        }

        object_entrada.close();
        object_salida.close();
        conexion.close();

      }catch(Exception e){
        e.printStackTrace();
      }

    }

  }

  public static void main(String[] args) throws Exception
  {

    //Validamos el recibimiento de los parametros necesarios para la topologia
    if(args.length != 2){

      System.err.println("Se debe pasar como parametros el numero del nodo y la IP del servidor (En caso de ser el nodo 0 pasar su propia dirección IP)");
      System.exit(1);

    }

    nodo = args[0];
    ip = args[1];



    if(nodo.equals("M2019630376-0")){

      System.out.println("Servidor (Nodo 0) iniciado...");

      // inicializa las matrices A y B

      for (int i = 0; i < N; i++)
        for (int j = 0; j < N; j++)
        {
          A[i][j] = 2 * i + j;
          B[i][j] = 2 * i - j;
          C[i][j] = 0;
        }

      // Declaración de la matriz donde guardaremos a B transpuesta
      
      long [][] B_transpuesta = new long[N][N]; 

      // Copiamos la matriz B a la matriz donde haremos su transpuesta

      for (int i = 0; i < N; i++)
        for (int j = 0; j < N; j++)
        {
          B_transpuesta[i][j] = B[i][j];
        }

      // transpone la matriz B, guardada en la matriz B_transpuesta

      for (int i = 0; i < N; i++)
        for (int j = 0; j < i; j++)
        {
          long x = B_transpuesta[i][j];
          B_transpuesta[i][j] = B_transpuesta[j][i];
          B_transpuesta[j][i] = x;
        }

      // Creación de A1 y A2

      for (int i = 0; i < N/2; i++) {
        for (int j = 0; j < N; j++ ) {
          A1[i][j] = A[i][j];
          A2[i][j] = A[i + N/2][j];
        }
      }

      // Creación de B1 y B2

      for (int i = 0; i < N/2; i++) {
        for (int j = 0; j < N; j++ ) {
          B1[i][j] = B_transpuesta[i][j];
          B2[i][j] = B_transpuesta[i + N/2][j];
        }
      }

      //Inicializamos un server socket en el puerto 20000

      ServerSocket servidor = new ServerSocket(20000);

      //Creamos 4 objetos Worker que seran los hilos que enviaran las matrices a los otros nodos

      Worker[] hilos = new Worker[4];

      //Esperamos las conexiones propiamente para inicializar los hilos pasando la conexión

      for(int i = 0; i < 4; i++){

        Socket conexion = servidor.accept();
        hilos[i] = new Worker(conexion);
        hilos[i].start();

      }

      for(int i = 0; i < 4; i++){
        hilos[i].join();
      }

      // Une las matrices C1, C2, C3 y C4 para formar la matriz C, producto de las matrices A y B

      for (int i = 0; i < N/2; i++)
        for (int j = 0; j < N/2; j++){
          C[i][j] = C1[i][j];
          C[i][j + N/2] = C2[i][j];
          C[i + N/2][j] = C3[i][j];
          C[i + N/2][j + N/2] = C4[i][j];
        }

      //Calculo y despliegue del checksum de la matriz C
      System.out.println("\nChecksum de la matriz C: " + checksum(C) + "\n");  


      //Si N = 10, desplegamos las matrices A, B (Sin estar trapuesta) y C
      if(N == 10){

        System.out.println("Matriz A: \n");
        matrixPrint(A);

        System.out.println("Matriz B: \n");
        matrixPrint(B);

        System.out.println("Matriz C: \n");
        matrixPrint(C);

      }
      
    }else{

      System.out.println("Inicializacion del nodo...");    

      //Inicializamos un socket con lo que es la dirección del servidor pasada como parametro y el puerto 20000

      Socket conexion = new Socket(ip, 20000);


      //Declaramos object streams para enviar las submatrices serializadas como un objeto 

      ObjectOutputStream object_salida = new ObjectOutputStream(conexion.getOutputStream());
      ObjectInputStream object_entrada = new ObjectInputStream(conexion.getInputStream());
      

      System.out.println("Conexion con el servidor (Nodo 0) establecida...");        

      object_salida.writeObject(nodo);
      object_salida.flush();

      //Los objetos recibidos los casteamos a una variable, en este caso, de tipo long[][]

      long [][] Ax = (long[][])object_entrada.readObject();

      //Impresión de la submatriz que esta recibiendo
      //System.out.println("Matriz A recibida: ");
      //matrixPrint(Ax);

      long [][] Bx = (long[][])object_entrada.readObject();

      //Impresión de la submatriz que esta recibiendo
      //System.out.println("Matriz B recibida: ");
      //matrixPrint(Bx);

      object_salida.writeObject(multiplicaMatriz(Ax, Bx, true));

      System.out.println("Submatriz calculada y enviada, cerrando conexiones."); 

      object_salida.close();
      object_entrada.close();
      conexion.close();

    }


  }


  /*
  *
  * Parametros: Dos matrices de tipo long (long [][]) las cuales multipicara y un boolean para saber si la matriz 2 esta transpuesta 
  * Return:     Una matriz de tipo long (long[][]) la cual sera el producto de las dos matrices pasadas
  * Brief:      Esta funcion obtiene la matriz producto de dos matrices y dependiendo si es transpuesta sus dimensiones (mxn) en caso de que no sean del mismo tamaño
  *
  */


  public static long [][] multiplicaMatriz(long [][] matrix1, long [][] matrix2, boolean transpuesta){
    
    int cols = matrix1.length;

    //Si la matriz 2 (matrix2) esta transpuesta usa el tamaño de las columnas para crear el tamño de las filas de la nueva matriz
    int rows = transpuesta ? matrix2.length : matrix2[0].length;

    long [][] res = new long[cols][rows];

    for (int i = 0; i < cols; i++)
      for (int j = 0; j <rows; j++)
        for (int k = 0; k < N; k++)
           res[i][j] += matrix1[i][k] * matrix2[j][k];

     return res;

  }


  /*
  *
  * Parametros: Una matriz de tipo long (long [][]) la cual se imprimira con un formato
  * Return:     No retorna nada
  * Brief:      Imprime una matriz pasada como parametro usando printf() para darle un formato
  *
  */


  public static void matrixPrint(long [][] matrix){

    for (int i = 0; i < matrix.length; i++){
      for (int j = 0; j < matrix[i].length; j++)
      {
        
        //Para imprimir utilizamos un printf, que al igual que en C o Python, nos permite darle un formato a la cadena
        //En este caso, dando a entender que pasaremos 5 "argumentos", uno de ellos sera el numero, el resto se usaran como espacios

        System.out.printf("%5d",  matrix[i][j]);
         
      }

      System.out.println("");
        
        
    }

    System.out.println("\n");

  }

  /*
  *
  * Parametros: Una matriz de tipo long (long [][]) a la cual se le calculara el checksum 
  * Return:     Una variable de tipo long conteniendo el checksum de la matriz pasada
  * Brief:      Esta funcion calcula la checksum de una matriz pasada, mediante la suma de todo sus elementos
  *
  */
  
  public static long checksum(long [][] matrix){

    long checksum = 0;

    for (int i = 0; i < matrix.length; i++){
      for (int j = 0; j < matrix[i].length; j++){

        checksum += matrix[i][j];

      }
    }

    return checksum;
  }

}