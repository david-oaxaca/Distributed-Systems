/*
  Servicio.java
  Servicio web tipo REST
  Carlos Pineda Guerrero, Octubre 2021
*/

package negocio;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.QueryParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.Response;

import java.sql.*;
import javax.sql.DataSource;
import javax.naming.Context;
import javax.naming.InitialContext;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.*;

// la URL del servicio web es http://localhost:8080/Servicio/rest/ws
// donde:
//	"Servicio" es el dominio del servicio web (es decir, el nombre de archivo Servicio.war)
//	"rest" se define en la etiqueta <url-pattern> de <servlet-mapping> en el archivo WEB-INF\web.xml
//	"ws" se define en la siguiente anotacin @Path de la clase Servicio

@Path("ws")
public class Servicio {
    static DataSource pool = null;
    static {
        try {
            Context ctx = new InitialContext();
            pool = (DataSource) ctx.lookup("java:comp/env/jdbc/datasource_Servicio");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static Gson j = new GsonBuilder().registerTypeAdapter(byte[].class, new AdaptadorGsonBase64())
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").create();

    
  @POST
  @Path("registrar_articulo")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(MediaType.APPLICATION_JSON)
  public Response alta(@FormParam("articulo") Articulo articulo) throws Exception
  {
        Connection conexion = pool.getConnection();
	    
        if (articulo.nombre == null || articulo.nombre.equals(""))
            return Response.status(400).entity(j.toJson(new Error("Se debe ingresar un nombre de articulo"))).build();

        if (articulo.descripcion == null || articulo.descripcion.equals(""))
            return Response.status(400).entity(j.toJson(new Error("Se debe ingresar la descripcion del articulo")))
                    .build();

        if (articulo.precio <= 0.0f)
            return Response.status(400).entity(j.toJson(new Error("Ingresar un precio real"))).build();

        if (articulo.cantidad <= 0)
            return Response.status(400).entity(j.toJson(new Error("Ingresar una cantidad valida"))).build();

        if (articulo.foto == null)
            return Response.status(400).entity(j.toJson(new Error("Se debe ingresar una fotografia del articulo")))
                    .build();

        try {
            conexion.setAutoCommit(false);
            PreparedStatement stmt_1 = conexion.prepareStatement("Insert into articulos values(0, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            try {
                stmt_1.setString(1, articulo.nombre);
                stmt_1.setString(2, articulo.descripcion);
                stmt_1.setFloat(3, articulo.precio);
                stmt_1.setInt(4, articulo.cantidad);
                stmt_1.executeUpdate();
                ResultSet rs = stmt_1.getGeneratedKeys();
	        try{
	          if(rs.next())
	            articulo.id_articulo=rs.getInt(1);
	        }finally{
	          rs.close();
	        }
            } finally {
                stmt_1.close();
            }

            if (articulo.foto != null) {
                PreparedStatement stmt_2 = conexion.prepareStatement("INSERT INTO foto_articulos VALUES (0,?,?)");
                try {
                    stmt_2.setBytes(1, articulo.foto);
                    stmt_2.setInt(2, articulo.id_articulo);
                    stmt_2.executeUpdate();
                } finally {
                     stmt_2.close();
                }
             }

              return Response.ok().entity(j.toJson(articulo.id_articulo)).build();

        } catch (Exception e) {
            return Response.status(400).entity(j.toJson(new Error(e.getMessage()))).build();
	}finally{
	      conexion.setAutoCommit(true);
	      conexion.close();
        }    
  } 

  @POST
  @Path("buscar_articulo")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(MediaType.APPLICATION_JSON)
  public Response consultaArticulos(@FormParam("descripcion") String descripcion) throws Exception
  {
    Connection conexion = pool.getConnection();
    ArrayList<Articulo> articulos_busqueda = new ArrayList<Articulo>();

    try {
        PreparedStatement stmt_1 = conexion.prepareStatement("SELECT a.id_articulo, a.nombre, a.descripcion, a.precio, a.cantidad_almacen, b.foto FROM articulos a LEFT OUTER JOIN foto_articulos b ON a.id_articulo = b.id_articulo WHERE a.descripcion LIKE ?");

        try {
            stmt_1.setString(1, "%"+descripcion+"%");
            ResultSet rs = stmt_1.executeQuery();
            try {

                while(rs.next())
                {
                  Articulo a = new Articulo();
                  a.id_articulo = rs.getInt(1);
                  a.nombre = rs.getString(2);
                  a.descripcion = rs.getString(3);
                  a.precio = rs.getFloat(4);
                  a.cantidad = rs.getInt(5);
                  a.foto = rs.getBytes(6);

                  //Añadimos el objeto "Articulo" al ArrayList
                  articulos_busqueda.add(a);
                }

                if(articulos_busqueda.size() > 0){
                  return Response.ok().entity(j.toJson(articulos_busqueda)).build();
                }else{
                  return Response.status(400).entity(j.toJson(new Error("No hay articulos que coincidan :("))).build();
                }

            } finally {
                rs.close();
            }
        } finally {
            stmt_1.close();
        }
    } catch (Exception e) {
        return Response.status(400).entity(j.toJson(new Error(e.getMessage()))).build();
    } finally {
        conexion.close();
    }
  }

  @POST
  @Path("comprar_articulo")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(MediaType.APPLICATION_JSON)
  public Response comprar(@FormParam("articulo") Articulo articulo) throws Exception
  {
    Connection conexion= pool.getConnection();
    int stock = 0;
    int id_carrito_articulo;

    if (articulo.cantidad < 0)
      return Response.status(400).entity(j.toJson(new Error("Se debe ingresar una cantidad de articulos a comprar valida"))).build();

    try
    {

      PreparedStatement stmt_1 = conexion.prepareStatement("SELECT cantidad_almacen FROM articulos WHERE id_articulo = ?");
      try
      {
        stmt_1.setInt(1,articulo.id_articulo);

        ResultSet rs = stmt_1.executeQuery();
        try
        {
          if (rs.next())
            stock = rs.getInt(1);
        }
        finally
        {
          rs.close();
        }
      }
      finally
      {
        stmt_1.close();
      }

      conexion.setAutoCommit(false);

      if(articulo.cantidad <= stock){

        PreparedStatement stmt_2 = conexion.prepareStatement("INSERT INTO carrito_compra(id_carrito_articulo, id_articulo, cantidad) VALUES (0,?,?)",  Statement.RETURN_GENERATED_KEYS);
        ResultSet keys = null;
        try
        {
          stmt_2.setInt(1,articulo.id_articulo);
          stmt_2.setInt(2,articulo.cantidad);
          stmt_2.executeUpdate();
          keys = stmt_2.getGeneratedKeys();
          keys.next();
          id_carrito_articulo = keys.getInt(1);
        }
        finally
        {
          stmt_2.close();
          keys.close();
        }
        conexion.commit();


        PreparedStatement stmt_3 = conexion.prepareStatement("UPDATE articulos SET cantidad_almacen=? WHERE id_articulo=?");
        try
        {
          stmt_3.setInt(1,articulo.cantidad);
          stmt_3.setInt(2,articulo.id_articulo);
          stmt_3.executeUpdate();
        }
        finally
        {
          stmt_3.close();
        }

      }else{
        return Response.status(400).entity(j.toJson(new Error("No hay suficientes articulos para la compra :("))).build();
      }

      

    }
    catch (Exception e)
    {
      conexion.rollback();
      return Response.status(400).entity(j.toJson(new Error(e.getMessage()))).build();
    }
    finally
    {
      conexion.setAutoCommit(true);
      conexion.close();
    }
    return Response.ok().entity(j.toJson(id_carrito_articulo)).build();
  }

  @POST
  @Path("consulta_carrito")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(MediaType.APPLICATION_JSON)
  public Response consultaCarrito() throws Exception
  {
    ArrayList <Articulo> articulos_carrito = new ArrayList<Articulo>();
    Connection conexion= pool.getConnection();
    try
    {

      PreparedStatement stmt_1 = conexion.prepareStatement("SELECT a.id_articulo, a.nombre, a.descripcion, a.precio, b.cantidad, c.foto FROM carrito_compra b LEFT OUTER JOIN articulos a ON a.id_articulo = b.id_articulo LEFT OUTER JOIN foto_articulos c ON b.id_articulo = c.id_articulo");
      
      try
      {

        ResultSet rs = stmt_1.executeQuery();
        try
        {
          while (rs.next())
          {
            Articulo a = new Articulo();
            a.id_articulo = rs.getInt(1);
            a.nombre = rs.getString(2);
            a.descripcion = rs.getString(3);
            a.precio = rs.getFloat(4);
            a.cantidad = rs.getInt(5);
            a.foto = rs.getBytes(6);

            //Añadimos el objeto "Articulo" al ArrayList
            articulos_carrito.add(a);
          }

          if(articulos_carrito.size() > 0){
            return Response.ok().entity(j.toJson(articulos_carrito)).build();
          }else{
            return Response.status(400).entity(j.toJson(new Error("No hay articulos que coincidan :("))).build();
          }
        }
        catch(Exception e){
          return Response.status(400).entity(j.toJson(new Error("No hay articulos que coincidan :("))).build();
        }
        finally
        {
          rs.close();
        }
      }
      finally
      {
        stmt_1.close();
      }
    }
     catch (Exception e)
    {
      return Response.status(400).entity(j.toJson(new Error(e.getMessage()))).build();
    }
    finally
    {
      conexion.close();
    }
  }



}


