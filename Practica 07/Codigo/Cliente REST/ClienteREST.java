import java.net.URL;
import java.net.HttpURLConnection;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.Scanner; 


/******
*
* @author: David Oaxaca
*
* Forma de compilarlo:
* javac -cp gson-2.3.1.jar ClienteREST.java AdaptadorGsonBase64.java
*
* Forma de ejecutarlo:
* java -cp gson-2.3.1.jar;. ClienteREST.java 
*
*
*****/

class ClienteREST{

	public static void Alta(Usuario usuario) throws Exception{
		URL url = new URL("http://20.120.31.23:8080/Servicio/rest/ws/alta_usuario");

		HttpURLConnection conexion = (HttpURLConnection) url.openConnection();

		conexion.setDoOutput(true);

		conexion.setRequestMethod("POST");

		conexion.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		Gson j = new GsonBuilder().registerTypeAdapter(byte[].class,new AdaptadorGsonBase64()).create();
		String parametros = "usuario=" + URLEncoder.encode(j.toJson(usuario), "UTF-8");

		OutputStream os = conexion.getOutputStream();

		os.write(parametros.getBytes());
		os.flush();

		if(conexion.getResponseCode() == 200){

			BufferedReader br = new BufferedReader(new InputStreamReader((conexion.getInputStream())));

			String respuesta;

			while ((respuesta = br.readLine()) != null ) System.out.println("\nID del usuario creado: " + respuesta +  "\n");
			
		}else{
			
			BufferedReader br = new BufferedReader(new InputStreamReader((conexion.getErrorStream())));
			String respuesta;

			System.out.println("ERROR: ");
			while((respuesta = br.readLine()) != null) System.out.println(respuesta);
		}
		conexion.disconnect();
	}

	public static void Consulta(int id_usuario) throws Exception{
		Scanner sc = new Scanner(System.in);
		URL url = new URL("http://20.120.31.23:8080/Servicio/rest/ws/consulta_usuario");

		HttpURLConnection conexion = (HttpURLConnection) url.openConnection();

		conexion.setDoOutput(true);

		conexion.setRequestMethod("POST");

		conexion.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

		String parametros = "id_usuario=" + URLEncoder.encode( "" + id_usuario, "UTF-8");

		OutputStream os = conexion.getOutputStream();

		os.write(parametros.getBytes());
		os.flush();

		if(conexion.getResponseCode() == 200){

			BufferedReader br = new BufferedReader(new InputStreamReader((conexion.getInputStream())));

			Usuario usuario_consultado = new Usuario();
			char opc2 = ' ';
			String respuesta;

			Gson j = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                
			while((respuesta = br.readLine()) != null){
				usuario_consultado = (Usuario) j.fromJson(respuesta, Usuario.class);
				System.out.println("\n************************************************************\n");
				System.out.println("E-mail: " + usuario_consultado.email);
				System.out.println("Nombre: " + usuario_consultado.nombre);
				System.out.println("Apellido paterno: " + usuario_consultado.apellido_materno);
				System.out.println("Apellido materno: " + usuario_consultado.apellido_paterno);
				System.out.println("Fecha de nacimiento: " + usuario_consultado.fecha_nacimiento);
				System.out.println("Telefono: " + usuario_consultado.telefono);
				System.out.println("Genero: " + usuario_consultado.genero);
				System.out.println("\n************************************************************\n");

			} 
			
			System.out.println("\u00bfDesea modificar los datos del usuario? (s/n): ");
			opc2 = sc.next().charAt(0);
			sc.nextLine();
			if(opc2 == 's'){
				System.out.println("Introduzca los siguientes datos: ");
				Usuario usuario = new Usuario(); 

				usuario.id_usuario = id_usuario;

				System.out.print("\nIntroduzca el e-mail: ");
			  	usuario.email = sc.nextLine();
			  	if(usuario.email.equals("")) usuario.email = usuario_consultado.email;

			  	System.out.print("\nIntroduzca el nombre: ");
				usuario.nombre = sc.nextLine();
				if(usuario.nombre.equals("")) usuario.nombre = usuario_consultado.nombre;

				System.out.print("\nIntroduzca el apellido paterno: ");
				usuario.apellido_paterno = sc.nextLine();
				if(usuario.apellido_paterno.equals("")) usuario.apellido_paterno = usuario_consultado.apellido_paterno;

				System.out.print("\nIntroduzca el apellido materno: ");
				usuario.apellido_materno = sc.nextLine();
				if(usuario.apellido_materno.equals("")) usuario.apellido_materno = usuario_consultado.apellido_materno;

				System.out.print("\nIntroduzca la fecha de nacimiento: ");
				usuario.fecha_nacimiento = sc.nextLine();
				if(usuario.fecha_nacimiento.equals("")) usuario.fecha_nacimiento = usuario_consultado.fecha_nacimiento;				

				System.out.print("\nIntroduzca el numero de telefono: ");
				usuario.telefono = sc.nextLine();
				if(usuario.telefono.equals("")) usuario.telefono = usuario_consultado.telefono;	

				System.out.print("\nIntroduzca el genero: ");
				usuario.genero = sc.nextLine();
				if(usuario.genero.equals("")) usuario.genero = usuario_consultado.genero;	

				System.out.println("Guardando modificaciones...");
				Modificar(usuario);
			}
		}else{
			BufferedReader br = new BufferedReader(new InputStreamReader((conexion.getErrorStream())));
			String respuesta;

			System.out.println("ERROR: ");
			while((respuesta = br.readLine()) != null) System.out.println(respuesta);
			
		}
		conexion.disconnect();

	}

	public static void Modificar(Usuario usuario) throws Exception{
		URL url = new URL("http://20.120.31.23:8080/Servicio/rest/ws/modifica_usuario");

		HttpURLConnection conexion = (HttpURLConnection) url.openConnection();

		conexion.setDoOutput(true);

		conexion.setRequestMethod("POST");

		conexion.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

		Gson j = new GsonBuilder().registerTypeAdapter(byte[].class,new AdaptadorGsonBase64()).create();
		String parametros = "usuario=" + URLEncoder.encode(j.toJson(usuario), "UTF-8");

		OutputStream os = conexion.getOutputStream();

		os.write(parametros.getBytes());
		os.flush();

		if(conexion.getResponseCode() == 200){

			BufferedReader br = new BufferedReader(new InputStreamReader((conexion.getInputStream())));

			String respuesta;

			System.out.println("El usuario ha sido modificado");
			while ((respuesta = br.readLine()) != null ) System.out.println(respuesta);
			
		}else{
			BufferedReader br = new BufferedReader(new InputStreamReader((conexion.getErrorStream())));
			String respuesta;

			System.out.println("ERROR: ");
			while((respuesta = br.readLine()) != null) System.out.println(respuesta);
		}
		conexion.disconnect();

	}

	public static void Borrar(int id_usuario) throws Exception{
		URL url = new URL("http://20.120.31.23:8080/Servicio/rest/ws/borra_usuario");

		HttpURLConnection conexion = (HttpURLConnection) url.openConnection();

		conexion.setDoOutput(true);

		conexion.setRequestMethod("POST");

		conexion.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

		String parametros = "id_usuario=" + URLEncoder.encode("" + id_usuario, "UTF-8");

		OutputStream os = conexion.getOutputStream();

		os.write(parametros.getBytes());
		os.flush();

		if(conexion.getResponseCode() == 200){

			BufferedReader br = new BufferedReader(new InputStreamReader((conexion.getInputStream())));

			String respuesta;
			System.out.println("El usuario ha sido borrado");

			while ((respuesta = br.readLine()) != null ) System.out.println(respuesta);
			
		}else{
			BufferedReader br = new BufferedReader(new InputStreamReader((conexion.getErrorStream())));
			String respuesta;

			System.out.println("ERROR: ");
			while((respuesta = br.readLine()) != null) System.out.println(respuesta);
		}
		conexion.disconnect();

		
	}

	public static void main(String [] args) throws Exception{
		Scanner sc = new Scanner(System.in);
		Usuario usuario;
		int id_usuario;
		char opc = ' ';
		while(opc != 'd'){
			System.out.println("\n");
			System.out.println("a. Alta usuario");
			System.out.println("b. Consulta usuario");
			System.out.println("c. Borra usuario");
			System.out.println("d. Salir");
			System.out.print("\n Opci\u00f3n: ");
			opc = sc.next().charAt(0);
			sc.nextLine();
			System.out.println("\n");
			switch(opc){ 
				case 'a':
					System.out.println("Introduzca los siguientes datos: ");
					usuario = new Usuario(); 

					System.out.print("\nIntroduzca el e-mail: ");
				  	usuario.email = sc.nextLine();
				  	System.out.print("\nIntroduzca el nombre: ");
					usuario.nombre = sc.nextLine();
					System.out.print("\nIntroduzca el apellido paterno: ");
					usuario.apellido_paterno = sc.nextLine();
					System.out.print("\nIntroduzca el apellido materno: ");
					usuario.apellido_materno = sc.nextLine();
					System.out.print("\nIntroduzca la fecha de nacimiento: ");
					usuario.fecha_nacimiento = sc.nextLine();
					System.out.print("\nIntroduzca el numero de telefono: ");
					usuario.telefono = sc.nextLine();
					System.out.print("\nIntroduzca el genero: ");
					usuario.genero = sc.nextLine();


					System.out.println("\nDando de alta al usuario (Al terminar se desplegara su ID)...\n");
					Alta(usuario);

					break;
				case 'b':
					System.out.print("\nIntroduzca el ID de usuario: ");
					id_usuario = sc.nextInt();

					System.out.println("\nRealizando consulta...\n");
					Consulta(id_usuario);

					break;
				case 'c':
					System.out.print("\nIntroduzca el ID de usuario: ");
					id_usuario = sc.nextInt();

					System.out.println("\nBorrando usuario...\n");
					Borrar(id_usuario);

					break;
				default:
					break;
			}

		}
	}

}

class Usuario {
    int id_usuario;
    String email;
    String nombre;
    String apellido_paterno;
    String apellido_materno;
    String fecha_nacimiento;
    String telefono;
    String genero;
    byte[] foto;

    public Usuario(){}
}

