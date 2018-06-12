package servicios;

import logical.Articulo;
import logical.Comentario;
import logical.Etiqueta;
import logical.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServiciosUsuario {

    public List<Usuario> listaUsuario() {
        List<Usuario> lista = new ArrayList<>();
        Connection con = null; //objeto conexion.
        try {

            String query = "select * from usuarios";
            con = ServiciosDataBase.getInstancia().getConexion(); //referencia a la conexion.
            //
            PreparedStatement prepareStatement = con.prepareStatement(query);
            ResultSet rs = prepareStatement.executeQuery();
            while(rs.next()){
                Usuario u = new Usuario();
                u.setUsername(rs.getString("username"));
                u.setNombre(rs.getString("nombre"));
                u.setPassword(rs.getString("password"));
                u.setAdministrador(rs.getBoolean("administrador"));
                u.setAutor(rs.getBoolean("autor"));

                lista.add(u);
            }

        } catch (SQLException ex) {
            Logger.getLogger(ServiciosUsuario.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            try {
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(ServiciosUsuario.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return lista;
    }

    public List<Articulo> listaArticulo() {
        List<Articulo> lista = new ArrayList<>();
        Connection con = null; //objeto conexion.
        try {

            String query = "select * from articulos";
            con = ServiciosDataBase.getInstancia().getConexion();

            PreparedStatement prepareStatement = con.prepareStatement(query);
            ResultSet rs = prepareStatement.executeQuery();
            while(rs.next()){
                Articulo o = new Articulo();
                o.setId(rs.getLong("id"));
                o.setTitulo(rs.getString("titulo"));
                o.setCuerpo(rs.getString("cuerpo"));
                o.setAutor((Usuario)rs.getObject("fecha"));

                o.setListaEtiquetas(listaEtiquetaArticulo(o));
                o.setListaComentarios(listaComentario(o));

                lista.add(o);
            }

        } catch (SQLException ex) {
            Logger.getLogger(ServiciosUsuario.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            try {
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(ServiciosUsuario.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return lista;
    }

    public List<Etiqueta> listaEtiquetaArticulo(Articulo art) {
        List<Etiqueta> lista = new ArrayList<>();
        Connection con = null; //objeto conexion.
        try {

            String query = "select e.ID, e.ETIQUETA from ETIQUETAS e, ARTICULOSETIQUETAS ae where ae.ETIQUETA = e.ID AND ae.ARTICULO == " + art.getId();
            con = ServiciosDataBase.getInstancia().getConexion();

            PreparedStatement prepareStatement = con.prepareStatement(query);
            ResultSet rs = prepareStatement.executeQuery();
            while(rs.next()){
                Etiqueta o = new Etiqueta();
                o.setId(rs.getLong("e.ID"));
                o.setEtiqueta(rs.getString("e.ETIQUETA"));

                lista.add(o);
            }

        } catch (SQLException ex) {
            Logger.getLogger(ServiciosUsuario.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            try {
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(ServiciosUsuario.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return lista;
    }

    public List<Comentario> listaComentario(Articulo art) {
        List<Comentario> lista = new ArrayList<>();
        Connection con = null; //objeto conexion.
        try {

            String query = "select * from COMENTARIOS where ARTICULO ==" + art.getId();
            con = ServiciosDataBase.getInstancia().getConexion();

            PreparedStatement prepareStatement = con.prepareStatement(query);
            ResultSet rs = prepareStatement.executeQuery();
            while(rs.next()){
                Comentario o = new Comentario();
                o.setId(rs.getLong("ID"));
                o.setComentario(rs.getString("COMENTARIO"));
                o.setArticulo((Articulo) rs.getObject("COMENTARIO"));
                o.setAutor(buscarComentador(o));
                lista.add(o);
            }

        } catch (SQLException ex) {
            Logger.getLogger(ServiciosUsuario.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            try {
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(ServiciosUsuario.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return lista;
    }

    public Usuario buscarComentador(Comentario cmt) {
        Usuario comentador = new Usuario();
        Connection con = null; //objeto conexion.
        try {

            String query = "select u.USERNAME, u.NOMBRE, u.PASSWORD, u.ADMINISTRADOR, u.AUTOR from USUARIOS u, COMENTARIOS c where c.ID = " + cmt.getId() + " AND u.USERNAME = c.AUTOR";
            con = ServiciosDataBase.getInstancia().getConexion();

            PreparedStatement prepareStatement = con.prepareStatement(query);
            ResultSet rs = prepareStatement.executeQuery();
            while(rs.next()){
                comentador.setUsername(rs.getString("u.USERNAME"));
                comentador.setNombre(rs.getString("u.NOMBRE"));
                comentador.setPassword(rs.getString("u.PASSWORD"));
                comentador.setAdministrador(rs.getBoolean("u.ADMINISTRADOR"));
                comentador.setAutor(rs.getBoolean("u.AUTOR"));
            }

        } catch (SQLException ex) {
            Logger.getLogger(ServiciosUsuario.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            try {
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(ServiciosUsuario.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return comentador;
    }

    public List<Etiqueta> listaEtiquetas() {
        List<Etiqueta> lista = new ArrayList<>();
        Connection con = null; //objeto conexion.
        try {

            String query = "select * from ETIQUETAS";
            con = ServiciosDataBase.getInstancia().getConexion();

            PreparedStatement prepareStatement = con.prepareStatement(query);
            ResultSet rs = prepareStatement.executeQuery();
            while(rs.next()){
                Etiqueta o = new Etiqueta();
                o.setId(rs.getLong("id"));
                o.setEtiqueta(rs.getString("etiqueta"));

                lista.add(o);
            }

        } catch (SQLException ex) {
            Logger.getLogger(ServiciosUsuario.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            try {
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(ServiciosUsuario.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return lista;
    }

    public Usuario buscarAutor(Articulo art) {
        Usuario autor = new Usuario();
        Connection con = null; //objeto conexion.
        try {

            String query = "select * from USUARIOS where  AUTOR =" + art.getId();
            con = ServiciosDataBase.getInstancia().getConexion();

            PreparedStatement prepareStatement = con.prepareStatement(query);
            ResultSet rs = prepareStatement.executeQuery();
            while(rs.next()){
                autor.setUsername(rs.getString("USERNAME"));
                autor.setNombre(rs.getString("NOMBRE"));
                autor.setPassword(rs.getString("PASSWORD"));
                autor.setAdministrador(rs.getBoolean("ADMINISTRADOR"));
                autor.setAutor(rs.getBoolean("AUTOR"));
            }

        } catch (SQLException ex) {
            Logger.getLogger(ServiciosUsuario.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            try {
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(ServiciosUsuario.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return autor;
    }

    public Usuario buscarUsuario(String username, String password) {
        Usuario user = null;

        Connection con = null; //objeto conexion.
        try {

            String query = "select * from USUARIOS where USERNAME = " + username + " AND PASSWORD = " + password;
            con = ServiciosDataBase.getInstancia().getConexion();

            PreparedStatement prepareStatement = con.prepareStatement(query);
            ResultSet rs = prepareStatement.executeQuery();
            while(rs.next()){
                user = new Usuario();
                user.setUsername(rs.getString("USERNAME"));
                user.setNombre(rs.getString("NOMBRE"));
                user.setPassword(rs.getString("PASSWORD"));
                user.setAdministrador(rs.getBoolean("ADMINISTRADOR"));
                user.setAutor(rs.getBoolean("AUTOR"));
            }

        } catch (SQLException ex) {
            Logger.getLogger(ServiciosUsuario.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            try {
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(ServiciosUsuario.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return user;
    }

    public boolean crearEtiqueta(Etiqueta o){
        boolean ok =false;

        Connection con = null;
        try {

            String query = "insert into ETIQUETAS(ID, ETIQUETA) values(?,?)";
            con = ServiciosDataBase.getInstancia().getConexion();
            //
            PreparedStatement prepareStatement = con.prepareStatement(query);
            //Antes de ejecutar seteo los parametros.
            prepareStatement.setLong(1, o.getId());
            prepareStatement.setString(2, o.getEtiqueta());
            //
            int fila = prepareStatement.executeUpdate();
            ok = fila > 0 ;

        } catch (SQLException ex) {
            Logger.getLogger(ServiciosUsuario.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            try {
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(ServiciosUsuario.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return ok;
    }

    public boolean crearComentario(Comentario o){
        boolean ok = false;

        Connection con = null;
        try {

            String query = "insert into COMENTARIOS(ID, COMENTARIO, AUTOR, ARTICULO) values(?,?,?,?)";
            con = ServiciosDataBase.getInstancia().getConexion();
            //
            PreparedStatement prepareStatement = con.prepareStatement(query);
            //Antes de ejecutar seteo los parametros.
            prepareStatement.setLong(1, o.getId());
            prepareStatement.setString(2, o.getComentario());
            prepareStatement.setString(3, o.getAutor().getUsername());
            prepareStatement.setLong(4, o.getArticulo().getId());
            //
            int fila = prepareStatement.executeUpdate();
            ok = fila > 0 ;

        } catch (SQLException ex) {
            Logger.getLogger(ServiciosUsuario.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            try {
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(ServiciosUsuario.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return ok;
    }

    public boolean crearUsuario(Usuario o){
        boolean ok = false;

        Connection con = null;
        try {

            String query = "insert into USUARIOS(USERNAME, NOMBRE, PASSWORD, ADMINISTRADOR, AUTOR) values(?,?,?,?,?)";
            con = ServiciosDataBase.getInstancia().getConexion();
            //
            PreparedStatement prepareStatement = con.prepareStatement(query);
            //Antes de ejecutar seteo los parametros.
            prepareStatement.setString(1, o.getUsername());
            prepareStatement.setString(2, o.getNombre());
            prepareStatement.setString(3, o.getPassword());
            prepareStatement.setBoolean(4, o.isAdministrador());
            prepareStatement.setBoolean(5, o.isAutor());
            //
            int fila = prepareStatement.executeUpdate();
            ok = fila > 0 ;

        } catch (SQLException ex) {
            Logger.getLogger(ServiciosUsuario.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            try {
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(ServiciosUsuario.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return ok;
    }

    public boolean crearArticulo(Articulo o){
        boolean ok = false;

        Connection con = null;
        try {

            String query = "insert into ARTICULOS(ID, TITULO, CUERPO, AUTOR) values(?,?,?,?)";
            con = ServiciosDataBase.getInstancia().getConexion();
            //
            PreparedStatement prepareStatement = con.prepareStatement(query);
            //Antes de ejecutar seteo los parametros.
            prepareStatement.setLong(1, o.getId());
            prepareStatement.setString(2, o.getTitulo());
            prepareStatement.setString(3, o.getCuerpo());
            prepareStatement.setString(4, o.getAutor().getUsername());

            for(Etiqueta e : o.getListaEtiquetas()){
                crearArticuloEtiqueta(o, e);
            }


            //
            int fila = prepareStatement.executeUpdate();
            ok = fila > 0 ;

        } catch (SQLException ex) {
            Logger.getLogger(ServiciosUsuario.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            try {
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(ServiciosUsuario.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return ok;
    }

    public boolean crearArticuloEtiqueta(Articulo o, Etiqueta E){
        boolean ok = false;

        Connection con = null;
        try {
            String query = "insert into ARTICULOSETIQUETAS(ID, ARTICULO, ETIQUETA) values(?,?,?)";
            con = ServiciosDataBase.getInstancia().getConexion();
            //
            PreparedStatement prepareStatement = con.prepareStatement(query);
            //Antes de ejecutar seteo los parametros.
            prepareStatement.setLong(1, o.getId());
            prepareStatement.setLong(2, o.getId());
            prepareStatement.setLong(3, E.getId());

            //
            int fila = prepareStatement.executeUpdate();
            ok = fila > 0 ;

        } catch (SQLException ex) {
            Logger.getLogger(ServiciosUsuario.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            try {
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(ServiciosUsuario.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return ok;
    }

    public boolean actualizarEstudiante(Usuario o){
        boolean ok =false;

        Connection con = null;
        try {

            String query = "update USUARIOS set NOMBRE=?, PASSWORD=?, ADMINISTRADOR=?, AUTOR=? where USERNAME= ?";
            con = ServiciosDataBase.getInstancia().getConexion();
            //
            PreparedStatement prepareStatement = con.prepareStatement(query);
            //Antes de ejecutar seteo los parametros.
            prepareStatement.setString(1, o.getNombre());
            prepareStatement.setString(2, o.getPassword());
            prepareStatement.setBoolean(3, o.isAdministrador());
            prepareStatement.setBoolean(4, o.isAutor());
            //Indica el where...
            prepareStatement.setString(5, o.getUsername());
            //
            int fila = prepareStatement.executeUpdate();
            ok = fila > 0 ;

        } catch (SQLException ex) {
            Logger.getLogger(ServiciosUsuario.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            try {
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(ServiciosUsuario.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return ok;
    }

    public boolean actualizarArticulo(Articulo o){
        boolean ok =false;

        Connection con = null;
        try {

            String query = "update ARTICULOS set TITULO=?, CUERPO=?, AUTOR=?,  where ID= ?";
            con = ServiciosDataBase.getInstancia().getConexion();
            //
            PreparedStatement prepareStatement = con.prepareStatement(query);
            //Antes de ejecutar seteo los parametros.
            prepareStatement.setString(1, o.getTitulo());
            prepareStatement.setString(2, o.getCuerpo());
            prepareStatement.setString(3, o.getAutor().getUsername());
            //Indica el where...
            prepareStatement.setLong(4, o.getId());
            //
            int fila = prepareStatement.executeUpdate();
            ok = fila > 0 ;

        } catch (SQLException ex) {
            Logger.getLogger(ServiciosUsuario.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            try {
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(ServiciosUsuario.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return ok;
    }





}