package logical;

import freemarker.template.Configuration;
import spark.ModelAndView;
import spark.template.freemarker.FreeMarkerEngine;

import java.util.*;

import static spark.Spark.get;
import static spark.Spark.post;

import static spark.Spark.staticFiles;

public class Main {

    private static List<Usuario> misUsuarios = new ArrayList<>();

    public static void main(String[] args) {

        staticFiles.location("/templates");

        Configuration cfg = new Configuration(Configuration.VERSION_2_3_0);
        cfg.setClassForTemplateLoading(Main.class, "/templates");
        FreeMarkerEngine freeMarkerEngine = new FreeMarkerEngine(cfg);

        Usuario user = new Usuario("aavgc","Adonis", "1234", false, false);
        Usuario ericUser = new Usuario("ericlavega96","Eric", "1234", false, false);

        misUsuarios.add(user);
        misUsuarios.add(ericUser);


        List<Articulo> articulos = new ArrayList<>();
        List<Comentario> comentarios = new ArrayList<>();
        List<Etiqueta> tags = new ArrayList<>();

        tags.add(new Etiqueta(1, "deportes"));
        tags.add(new Etiqueta(1, "moda"));

        Articulo articulo = new Articulo(1, "prueba", "prueba prueba prueba",
                user,new Date(),comentarios, tags);
        comentarios.add(new Comentario(1, "prueba prueba prueba", user, articulo));
        comentarios.add(new Comentario(2, "probando uno dos tres", ericUser, articulo));
        articulos.add(articulo);

        System.out.println(getAllTags(articulos));

        get("/registrarse", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("titulo", "Iniciar Sesión-Artículos A&E");
            return new ModelAndView(attributes, "login.ftl");
        }, freeMarkerEngine);

        get("/", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("titulo", "Página de artículos A&E");
            attributes.put("articulos", articulos);

            return new ModelAndView(attributes, "index.ftl");
        }, freeMarkerEngine);

        get("/post", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("articulo", articulo);
            attributes.put("tagsCol1", tagsColumnas(2,1,getAllTags(articulos)));
            attributes.put("tagsCol2", tagsColumnas(2,2,getAllTags(articulos)));
            attributes.put("titulo", "Artículos A&E - Post");
            return new ModelAndView(attributes, "post.ftl");
        }, freeMarkerEngine);

        post("/procesarUsuario", (request, response) -> {
            try {
                String usernameAVerificar = request.queryParams("username");
                String passwordsAVerificar = request.queryParams("password");
                if(verificarUsuario(usernameAVerificar,passwordsAVerificar)){
                    response.redirect("/");
                }else{
                    response.redirect("/registrarse");
                }
            } catch (Exception e) {
                System.out.println("Error al intentar iniciar sesión " + e.toString());
            }
            return "";
        });

        get("/gestionarUsuarios", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("titulo", "Gestion de Usuarios-Artículos A&E");
            return new ModelAndView(attributes, "usuariosIndex.ftl");
        }, freeMarkerEngine);

        post("/registrarNuevoUsuario", (request, response) -> {
           // try {
                String nombre = request.queryParams("nombre");
                String username = request.queryParams("username");
                String password = request.queryParams("password");
                String isAdmin = request.queryParams("isAdmin");
                String isAutor = request.queryParams("isAutor");

                System.out.println("Permiso admin: "+ isAdmin + " : Permiso autor " + isAutor );

                Usuario nuevoUsuario = new Usuario(nombre,username,password,true,true);
                misUsuarios.add(nuevoUsuario);

                response.redirect("/");

            //} catch (Exception e) {
            //    System.out.println("Error al registrar un usuario " + e.toString());
            //}
            return "";
        });
    }

    public static boolean verificarUsuario(String nombreUsuario,String password){
        boolean usuarioRegistrado = false;
        System.out.println("Nombre Usuario Real: "+ nombreUsuario + " : " + password);
        for (Usuario usuario: misUsuarios){
            System.out.println("Nombre Usuario: " + usuario.getUsername() + " : Password "+ usuario.getPassword());
            if (usuario.getUsername().equals(nombreUsuario)  && usuario.getPassword().equals(password)){
                    usuarioRegistrado = true;
            }
        }
        return usuarioRegistrado;
    }

    public static List<String> tagsColumnas(int numColum,int c, List<String> tags){
        List<String> columnaTag = new ArrayList<>();
        int halfSize = tags.size()/numColum;
        for(int i = tags.size()*(c - 1); i < halfSize * c; i++){
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
}
