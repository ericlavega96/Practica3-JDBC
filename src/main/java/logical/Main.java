package logical;

import freemarker.template.Configuration;
import spark.ModelAndView;
import spark.template.freemarker.FreeMarkerEngine;

import java.util.*;

import static spark.Spark.get;
import static spark.Spark.post;

import static spark.Spark.staticFiles;

public class Main {
    public static void main(String[] args) {

        staticFiles.location("/templates");

        Configuration cfg = new Configuration(Configuration.VERSION_2_3_0);
        cfg.setClassForTemplateLoading(Main.class, "/templates");
        FreeMarkerEngine freeMarkerEngine = new FreeMarkerEngine(cfg);

        Usuario user = new Usuario("aavgc","Adonis", "1234", false, false);
        List<Articulo> articulos = new ArrayList<>();
        List<Comentario> comentarios = new ArrayList<>();
        Articulo articulo = new Articulo(1, "prueba", "prueba prueba prueba",user,new Date(),comentarios, null);
        comentarios.add(new Comentario(1, "prueba prueba prueba", user, articulo));
        comentarios.add(new Comentario(2, "probando uno dos tres", user, articulo));
        articulos.add(articulo);


        get("/", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("titulo", "Página de artículos A&E");
            return new ModelAndView(attributes, "index.ftl");
        }, freeMarkerEngine);

        get("/cargarArticulo", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("articulo", articulo);
            attributes.put("comentarios", articulo.getListaComentarios());
            attributes.put("titulo", "Artículos A&E - Post");
            return new ModelAndView(attributes, "post.ftl");
        }, freeMarkerEngine);

        /*

        <#list articulo.comentarios as comentario>
                  <h5 class="mt-0">comentario.autor.nombre</h5>
                    comentario.comentario
                <#else>
                  <h5 class="mt-0">No hay comentarios disponibles</h5>
                </#list>
         */

    }
}
