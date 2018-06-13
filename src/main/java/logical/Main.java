package logical;


import freemarker.template.Configuration;

import servicios.ServiciosBootStrap;
import servicios.ServiciosDataBase;

import servicios.ServiciosUsuario;
import spark.ModelAndView;
import spark.Session;
import spark.template.freemarker.FreeMarkerEngine;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static spark.Spark.get;
import static spark.Spark.post;

import static spark.Spark.staticFiles;


public class Main {

    private static List<Usuario> misUsuarios = new ArrayList<>();

    private static String idUsuarioActual;


    public static void main(String[] args) throws SQLException {


        staticFiles.location("/templates");

        Configuration cfg = new Configuration(Configuration.VERSION_2_3_0);
        cfg.setClassForTemplateLoading(Main.class, "/templates");
        FreeMarkerEngine freeMarkerEngine = new FreeMarkerEngine(cfg);


        Usuario user = new Usuario("aavgc", "Adonis", "1234", true, false);
        Usuario ericUser = new Usuario("ericlavega96", "Eric", "1234", true, false);

        misUsuarios.add(user);
        misUsuarios.add(ericUser);


        //Pruebas conexion BD modo Server
        ServiciosBootStrap.iniciarBD();

        ServiciosDataBase.getInstancia().testConexion();

        ServiciosBootStrap.crearTablas();

        ServiciosUsuario SU = new ServiciosUsuario();
        SU.crearAdmin();
        //ServiciosBootStrap.detenetBD();


        List<Articulo> articulos = new ArrayList<>();
        List<Comentario> comentarios = new ArrayList<>();
        List<Comentario> comentarios2 = new ArrayList<>();
        List<Etiqueta> tags = new ArrayList<>();
        List<Etiqueta> tags2 = new ArrayList<>();

        tags.add(new Etiqueta("deportes"));
        tags.add(new Etiqueta("moda"));

        tags2.add(new Etiqueta("noticia"));
        tags2.add(new Etiqueta("negocios"));
        tags2.add(new Etiqueta("farándula"));

        Articulo articulo = new Articulo("prueba", "prueba prueba prueba",
                user, new Date(), comentarios, tags);
        comentarios.add(new Comentario("prueba prueba prueba", user, articulo));
        comentarios.add(new Comentario("probando uno dos tres", ericUser, articulo));
        articulos.add(articulo);

        Articulo articulo2 = new Articulo("Segundo Articulo", "Conenido del segundo artículo. \n" +
                "Segundo parrafo con contenido del artículo.",
                ericUser, new Date(), comentarios2, tags2);
        comentarios2.add(new Comentario("Prueba #1", user, articulo));
        comentarios2.add(new Comentario("Segundo comentario.", ericUser, articulo));
        articulos.add(articulo2);

        get("/iniciarSesion", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("titulo", "Iniciar Sesión-Artículos A&E");
            return new ModelAndView(attributes, "login.ftl");
        }, freeMarkerEngine);

        get("/", (request, response) -> {
            Usuario logUser = request.session(true).attribute("usuario");
            if(logUser == null)
                System.out.println("No login");
            else
                System.out.println("El usuario " + logUser.getUsername() + " se ha logueado");
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("titulo", "Página de artículos A&E");
            attributes.put("logUser", logUser);
            attributes.put("tagsCol1", tagsColumnas(2, 1, getAllTags(articulos)));
            attributes.put("tagsCol2", tagsColumnas(2, 2, getAllTags(articulos)));
            attributes.put("articulos", articulos);

            return new ModelAndView(attributes, "index.ftl");
        }, freeMarkerEngine);

        get("/post", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            Usuario logUser = request.session(true).attribute("usuario");

            attributes.put("articulo", articulo);
            attributes.put("logUser", logUser);
            attributes.put("tagsCol1", tagsColumnas(2, 1, getAllTags(articulos)));
            attributes.put("tagsCol2", tagsColumnas(2, 2, getAllTags(articulos)));
            attributes.put("titulo", "Artículos A&E - Post");
            return new ModelAndView(attributes, "verArticulo.ftl");
        }, freeMarkerEngine);

        post("/procesarUsuario", (request, response) -> {
            try {
                String usernameAVerificar = request.queryParams("username");
                String passwordsAVerificar = request.queryParams("password");
                Usuario logUser = SU.buscarUsuario(usernameAVerificar,passwordsAVerificar);
                if (logUser != null) {
                    request.session(true);
                    request.session().attribute("usuario", logUser);
                    response.redirect("/");
                } else {
                    System.out.println(logUser);
                    response.redirect("/iniciarSesion");
                }
            } catch (Exception e) {
                System.out.println("Error al intentar iniciar sesión " + e.toString());
            }
            return "";
        });

        get("/gestionarUsuarios", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            Usuario logUser = request.session(true).attribute("usuario");
            attributes.put("titulo", "Gestion de Usuarios-Artículos A&E");
            attributes.put("logUser", logUser);
            return new ModelAndView(attributes, "usuariosIndex.ftl");
        }, freeMarkerEngine);

        post("/registrarNuevoUsuario", (request, response) -> {
            // try {
            String nombre = request.queryParams("nombre");
            String username = request.queryParams("username");
            String password = request.queryParams("password");
            String isAdmin = request.queryParams("isAdmin");
            String isAutor = request.queryParams("isAutor");

            //System.out.println("Permiso admin: "+ isAdmin + " : Permiso autor " + isAutor );

            //Usuario nuevoUsuario = new Usuario(nombre,username,password,
            //        !isAdmin.equals(null)||isAdmin.equals("on"),
            //        !isAutor.equals(null)||isAutor.endsWith("on"));

            Usuario nuevoUsuario = new Usuario(nombre, username, password, true, false);
            misUsuarios.add(nuevoUsuario);
            SU.crearUsuario(nuevoUsuario);

            response.redirect("/listaUsuarios");

            //} catch (Exception e) {
            //    System.out.println("Error al registrar un usuario " + e.toString());
            //}
            return "";
        });

        get("/listaUsuarios", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            List<Usuario> usuariosEncontrados = SU.listaUsuario();
            attributes.put("titulo", "Lista de Usuarios");
            attributes.put("listaUsuarios", usuariosEncontrados);
            return new ModelAndView(attributes, "listaUsuarios.ftl");
        }, freeMarkerEngine);

        get("/visualizarUsuario/:id", (request, response) -> {

            idUsuarioActual = request.params("id");
            List<Usuario> usuariosEncontrados = SU.listaUsuario();
            Usuario logUser = request.session(true).attribute("usuario");
            Usuario usuario = usuariosEncontrados.get(Integer.parseInt(idUsuarioActual));

            Map<String, Object> attributes = new HashMap<>();
            attributes.put("titulo", "Visualizar Usuario");
            attributes.put("usuario", usuario);
            attributes.put("logUser", logUser);
            attributes.put("idUsuario", idUsuarioActual);

            return new ModelAndView(attributes, "visualizarUsuario.ftl");
        }, freeMarkerEngine);

        get("/editarUsuario/:id", (request, response) -> {

            idUsuarioActual = request.params("id");
            List<Usuario> usuariosEncontrados = SU.listaUsuario();
            Usuario logUser = request.session(true).attribute("usuario");
            Usuario usuario = usuariosEncontrados.get(Integer.parseInt(idUsuarioActual));

            Map<String, Object> attributes = new HashMap<>();
            attributes.put("titulo", "Editar Usuario");
            attributes.put("logUser", logUser);
            attributes.put("usuario", usuario);

            return new ModelAndView(attributes, "editarUsuario.ftl");
        }, freeMarkerEngine);

        post("/salvarUsuarioEditado", (request, response) -> {
            try {

                Usuario usuarioEditado = misUsuarios.get(Integer.parseInt(idUsuarioActual));

                String nombre = request.queryParams("nombre");
                String username = request.queryParams("username");
                String password = request.queryParams("password");
                //Faltan los permisos

                usuarioEditado.setNombre(nombre);
                usuarioEditado.setUsername(username);
                usuarioEditado.setPassword(password);
                usuarioEditado.setAdministrador(true);
                usuarioEditado.setAutor(false);

                response.redirect("/listaUsuarios");
            } catch (Exception e) {
                System.out.println("Error al editar al usuario: " + e.toString());
            }
            return "";
        });

        get("/eliminarUsuario/:id", (request, response) -> {

            idUsuarioActual = request.params("id");

            misUsuarios.remove(Integer.parseInt(idUsuarioActual));

            response.redirect("/listaUsuarios");
            return "";
        });

        get("/logout", (resquest, response) ->
        {
            Session ses = resquest.session(true);
            ses.invalidate();
            response.redirect("/");
            return "";
        });

        get("/publicarArticulo", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            Usuario logUser = request.session(true).attribute("usuario");
            attributes.put("titulo", "Publicar Artículo");
            attributes.put("logUser", logUser);
            System.out.println("Mostrar formulario del articulo");
            return new ModelAndView(attributes, "publicarArticulo.ftl");
        }, freeMarkerEngine);

       /* post("/procesarArticulo", (request, response) -> {
            System.out.println("Entró al post");

            try {
                String titulo = request.queryParams("title");
                System.out.println("Entro en 1 " + titulo);
                String cuerpo = request.queryParams("cuerpo");
                System.out.println("Entro en 2" + cuerpo);
                List<Comentario> articuloComentarios = new ArrayList<>();
                String[] articuloEtiquetas = request.queryParams("etiquetas").split(",");
                System.out.println("Entro en 3" + articuloEtiquetas);

                Articulo nuevoArticulo = new Articulo(titulo,cuerpo,misUsuarios.get(0),new Date(),articuloComentarios,crearEtiquetas(articuloEtiquetas));
                System.out.println("Se anadió el articulo");
                articulos.add(nuevoArticulo);
                System.out.println("Funciono!");

            } catch (Exception e) {
                System.out.println("Error al intentar publicar articulo" + e.toString());
            }
            response.redirect("/");
            System.out.println("Redirecciona...");
            return "";
        });
    }
    */
        post("/procesarArticulo/", (request, response) -> {
            try {
                String titulo = request.queryParams("title");
                String cuerpo = request.queryParams("cuerpo");
                Usuario autor = misUsuarios.get(0);
                Date fecha = new Date();
                List<Comentario> articuloComentarios = new ArrayList<>();
                String[] etiquetas = request.queryParams("etiquetas").split(",");
                List<Etiqueta> articuloEtiquetas = crearEtiquetas(etiquetas);

                Articulo nuevoArticulo = new Articulo(titulo,cuerpo,autor,fecha,articuloComentarios,articuloEtiquetas);

                response.redirect("/");
            } catch (Exception e) {
                System.out.println("Error al publicar artículo: " + e.toString());
            }
            return "";
        });


        get("/leerArticuloCompleto", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            Usuario logUser = request.session(true).attribute("usuario");
            attributes.put("titulo", "Artículo");
            attributes.put("logUser", logUser);
            return new ModelAndView(attributes, "verArticulo.ftl");
        }, freeMarkerEngine);

    }

    public static boolean verificarUsuario(String nombreUsuario,String password){
        boolean usuarioRegistrado = false;
        for (Usuario usuario: misUsuarios){
            if (usuario.getUsername().equals(nombreUsuario)  && usuario.getPassword().equals(password)){
                    usuarioRegistrado = true;
            }
        }
        return usuarioRegistrado;
    }

    public static List<String> tagsColumnas(int numColum,int c, List<String> tags){
        List<String> columnaTag = new ArrayList<>();
        int size = tags.size();
        if(tags.size()%2!=0 && numColum>c)
            size++;
        int halfSizeLow = ((size/numColum))*(c - 1);
        int halfSizeHigh = size/numColum*c;

        for(int i = halfSizeLow; i < halfSizeHigh; i++){
            columnaTag.add(tags.get(i));
        }
        return columnaTag;
    }

    public static List<String> getAllTags(List<Articulo> articulos){
        List<String> tags = new ArrayList<>();

        for(Articulo A : articulos)
            for(Etiqueta E : A.getListaEtiquetas())
                if(!tags.contains(E.tagsTransform()))
                    tags.add(E.tagsTransform());

        return tags;
    }

    public static List<Etiqueta> crearEtiquetas(String[] etiquetas){
        int i = 0;
        List<Etiqueta> etiquetasList = new ArrayList<>();
        for (String etiqueta : etiquetas ){
            etiquetasList.add(new Etiqueta(etiqueta));
            i++;
        }
        return etiquetasList;
    }


}
