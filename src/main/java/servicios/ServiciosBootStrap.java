package servicios;

import logical.Articulo;
import org.h2.tools.Server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ServiciosBootStrap {

    private static final String baseDeDatos = "jdbc:h2:tcp://localhost/~/practica3-JDBC";
    private static final String us = "admin";
    private static final String psw = "";


    /**
     *
     * @throws SQLException
     */
    public static void startDb() throws SQLException {
        Server.createTcpServer("-tcpPort", "9092", "-tcpAllowOthers").start();
    }

    /**
     *
     * @throws SQLException
     */
    public static void stopDb() throws SQLException {
        Server.shutdownTcpServer("tcp://localhost:9092", "", true, true);
    }


    /**
     * Metodo para recrear las tablas necesarios
     * @throws SQLException
     */
    public static void crearTablas() throws  SQLException{

        EjecutarQuery("create table if not exists Articulos\n" +
                "  (\n" +
                "    id bigint auto_increment PRIMARY KEY,\n" +
                "    titulo varchar(100),\n" +
                "    cuerpo CLOB,\n" +
                "    autor varchar(1000),\n" +
                "    fecha date,\n" +
                "    FOREIGN KEY (autor) REFERENCES Usuarios(username)\n" +
                "  )");

        EjecutarQuery("create table if not exists Comentarios\n" +
                "  (\n" +
                "    id bigint auto_increment PRIMARY KEY,\n" +
                "    comentario varchar(1000),\n" +
                "    autor varchar(1000),\n" +
                "    articulo bigint,\n" +
                "    FOREIGN KEY (autor) REFERENCES Usuarios(username),\n" +
                "    FOREIGN KEY (articulo) REFERENCES Articulos(id)\n" +
                "  )");

        EjecutarQuery("create table if not exists Etiquetas\n" +
                "  (\n" +
                "    id bigint auto_increment PRIMARY KEY,\n" +
                "    etiqueta varchar(1000)\n" +
                "  )");

        EjecutarQuery("create table if not exists ArticulosEtiquetas\n" +
                "  (\n" +
                "    id bigint auto_increment PRIMARY KEY,\n" +
                "    articulo bigint,\n" +
                "    etiqueta bigint,\n" +
                "    FOREIGN KEY (articulo) REFERENCES Articulos(id),\n" +
                "    FOREIGN KEY (etiqueta) REFERENCES Etiquetas(id)\n" +
                "  )");

        EjecutarQuery("create table if not exists Usuarios\n" +
                "  (\n" +
                "    username varchar(1000) PRIMARY KEY,\n" +
                "    nombre varchar(1000),\n" +
                "    password varchar(1000),\n" +
                "    administrador boolean,\n" +
                "    autor boolean\n" +
                "  )");
    }


    private static void EjecutarQuery(String query)
    {
        try
        {
            Class.forName("org.h2.Driver");
            Connection connection = DriverManager.getConnection(baseDeDatos, us, psw);
            Statement statement = connection.createStatement();

            statement.executeUpdate(query);
            statement.close();
            connection.close();
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
}
