import java.rmi.RemoteException;
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
* javac Matrices_RMI.java
*
* Forma de ejecutarlo:
* java Matriz [N]
*
* Notas: Este programa es el cliente de la multiplicación de matrices
* utilizando objetos distribuidos
* 
*
*****/


class Matrices_RMI{


	/*
	//Función para multiplicar matrices que sera usada de forma remota
	static double[][] multiplica_matrices(double[][] A, double[][] B, int N) throws RemoteException
	{
		
		double [][] C = new double[N/3][N/3];
		for(int i = 0; i < N/3; i++)
			for(int j = 0; j < N/3; j++)
				for(int k = 0; k < N; k++)
					C[i][j] += A[i][k] * B[j][k];
		return C;
	}
	*/

	/***
	*
	* Parametros: 	Matriz de tipo double (double [][]) que se separara, renglon en el que iniciara la separación
	* 				y dimenciones de la matriz (NxN) 
	* 
	* Return:     	Una submatriz de tipo double (NxN/3) de acuerdo con el el renglon_inicial de la separación
	* 
	* Brief:      	Esta funcion obtiene una submatriz despues de separa a una cuyo renglon inicial es pasado 
	* 				como parametro y se tiene predefinido que la separación sera en 1/3 de la orginal
	*
	***/

	static double [][] separa_matriz(double [][] A, int renglon_inicial, int N){
		
		double [][] M = new double[N/3][N];
		for(int i = 0; i < N/3; i++)
			for(int j = 0; j < N; j++)
				M[i][j] = A[i + renglon_inicial][j];
		return M;
	}

	/***
	*
	* Parametros: 	Matriz de tipo double (double [][]) en la que se acomodara una submatriz, renglon y columna en 
	* 				los que se empezara a acomodar y dimenciones de la matriz (NxN) 
	* 
	* Return:   	No se retorna nada
	* 
	* Brief:      	Esta funcion acomoda una submatriz dentro de otra empezando en el renglon y columna indicados
	*
	***/

	static void acomoda_matriz(double [][] C, double [][] c, int renglon, int columna, int N){
		
		for(int i = 0; i < N/3; i++)
			for(int j = 0; j < N/3; j++)
				C[i + renglon][j + columna] = c[i][j];
	}

	/***
	*
	* Parametros: 	Matriz de tipo double (double [][]) que sera transpuesta y dimenciones de la matriz (NxN) 
	* 
	* Return:   	Retorna una matriz transpuesta de tipo double
	* 
	* Brief:      	Esta funcion hace la transpocisión de una matriz
	*
	***/

	static double [][] transpone_matriz(double [][] m, int N){
		
		double [][] transpuesta = new double[N][N]; 

      	// Copiamos la matriz m a la matriz donde haremos su transpuesta

    	for (int i = 0; i < N; i++)
    		for (int j = 0; j < N; j++){
				transpuesta[i][j] = m[i][j];
        	}

      	// transpone la matriz m, guardada en la matriz transpuesta

		for (int i = 0; i < N; i++)
			for (int j = 0; j < i; j++){
				double x = transpuesta[i][j];
				transpuesta[i][j] = transpuesta[j][i];
				transpuesta[j][i] = x;
			}

		return transpuesta;
	}

	/***
	*
	* Parametros: 	Matriz de tipo double (double [][]) de la cual se calculara el checksum
	* 
	* Return:   	Se retorna un double que sera el checksum de la matriz pasada
	* 
	* Brief:      	Esta funcion acalcula el checksum de una matriz sumando todos sus elementos
	*
	***/

	static double checksum(double[][] m) throws RemoteException{
	    double s = 0;
	    for (int i = 0; i < m.length; i++)
	      for (int j = 0; j < m[0].length; j++)
	        s += m[i][j];
	    return s;
	}

	/***
	*
	* Parametros: Una matriz de tipo double (double [][]) la cual se imprimira con un formato
	* 
	* Return:     No retorna nada
	* 
	* Brief:      Imprime una matriz pasada como parametro 
	*
	***/


	public static void matrixPrint(double [][] matrix){

		for (int i = 0; i < matrix.length; i++){
			for (int j = 0; j < matrix[i].length; j++){

				//Para imprimir utilizamos un printf, que al igual que en C o Python, nos permite darle un formato a la cadena
				//En este caso, dando a entender que pasaremos 5 "argumentos", uno de ellos sera el numero, el resto se usaran como espacios

				System.out.printf("  %f",  matrix[i][j]);
			}
			System.out.println("");		    
		    
		}

		System.out.println("\n");

	}



	public static void main(String [] args)throws Exception{

			//Validamos el recibimiento de los parametros necesarios para la topologia
		if(args.length != 1){
			System.err.println("Se debe pasar como parametros el numero N de elementos en las columnas y filas de la matriz)");
			System.exit(1);

		}

		int N = Integer.valueOf(args[0]);

		double [][] A = new double[N][N];
		double [][] B = new double[N][N];
		double [][] C = new double[N][N];


		// En este caso el objeto remoto se llama "prueba", notar que se utiliza el puerto default 1099
	    String url1 = "rmi://52.188.125.96/prueba";
	    String url2 = "rmi://52.249.251.56/prueba";
	    String url3 = "rmi://20.185.194.19/prueba";
	    //IPs privadas de cada nodo
	    //1.- 52.188.125.96
	    //2.- 52.249.251.56
	    //3.- 20.185.194.19
	     

	    // Obtiene una referencia que "apunta" al objeto remoto asociado a la URL
	    InterfaceRMI r1 = (InterfaceRMI)Naming.lookup(url1);
	    InterfaceRMI r2 = (InterfaceRMI)Naming.lookup(url2);
	    InterfaceRMI r3 = (InterfaceRMI)Naming.lookup(url3);


		// inicializa las matrices A y B

		for (int i = 0; i < N; i++)
	        for (int j = 0; j < N; j++)
	        {
	      		//A[i][j] = 2 * i - j;
				//B[i][j] = i + 2 * j;
				A[i][j] = 4 * i + j;
				B[i][j] = 4 * i - j;
	        	C[i][j] = 0;
	        }

	    //Obtiene la matriz transpuesta

	    double[][] B_transpuesta = transpone_matriz(B, N);

	    //Separa las matrices A y B para la multiplicación de forma distribuida
	    double[][] A1 = separa_matriz(A, 0, N);
		double[][] A2 = separa_matriz(A, N/3, N);
		double[][] A3 = separa_matriz(A, 2*N/3, N);

		double[][] B1 = separa_matriz(B_transpuesta, 0, N);
		double[][] B2 = separa_matriz(B_transpuesta, N/3, N);
		double[][] B3 = separa_matriz(B_transpuesta, 2*N/3, N);


		//Multiplica las submatrices separadas
		double[][] C1 = r1.multiplica_matrices(A1, B1, N);
		double[][] C2 = r1.multiplica_matrices(A1, B2, N);
		double[][] C3 = r1.multiplica_matrices(A1, B3, N);
		double[][] C4 = r2.multiplica_matrices(A2, B1, N);
		double[][] C5 = r2.multiplica_matrices(A2, B2, N);
		double[][] C6 = r2.multiplica_matrices(A2, B3, N);
		double[][] C7 = r3.multiplica_matrices(A3, B1, N);
		double[][] C8 = r3.multiplica_matrices(A3, B2, N);
		double[][] C9 = r3.multiplica_matrices(A3, B3, N);

		//Acomoda los productos de las submatrices 

		acomoda_matriz(C, C1, 0, 0, N);
		acomoda_matriz(C, C2, 0, N/3, N);
		acomoda_matriz(C, C3, 0, 2*N/3, N);
		acomoda_matriz(C, C4, N/3, 0, N);
		acomoda_matriz(C, C5, N/3, N/3, N);
		acomoda_matriz(C, C6, N/3, 2*N/3, N);
		acomoda_matriz(C, C7, 2*N/3, 0, N);
		acomoda_matriz(C, C8, 2*N/3, N/3, N);
		acomoda_matriz(C, C9, 2*N/3, 2*N/3, N);

		//Si N = 9 imprime las matrices

		if(N == 9){
			System.out.println("Matriz A:\n");
		    matrixPrint(A);
		    System.out.println("Matriz B:\n");
		    matrixPrint(B);
			System.out.println("Matriz C:\n");
			matrixPrint(C);	
		}

		
		//Imprime el checksum de la matriz C resultante
		System.out.println("\nChecksum: " + checksum(C));

	}


}



//Checksum = 3420
