package servicios;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServiciosDataBase {
    private static ServiciosDataBase instancia;
    private String URL = "jdbc:h2:tcp://localhost/~/pruebaTep"; //Modo Server...


    public ServiciosDataBase() {
    }

    /**
     * Retornando la instancia.
     * @return
     */
    public static ServiciosDataBase getInstancia(){
        if(instancia==null){
            instancia = new ServiciosDataBase();
        }
        return instancia;
    }
}
