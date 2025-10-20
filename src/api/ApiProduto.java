package api;

import static spark.Spark.*;

import com.google.gson.Gson;

import dao.ProdutoDAO;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Filter;

public class ApiProduto {

    // instâncias do DAO e GSON
    private static final ProdutoDAO dao = new ProdutoDAO();
    private static final Gson gson = new Gson();

    // constante para garantir que as respostas sejam JSON
    private static final String APPLICATION_JSON = "application/json";

    public static void main(String[] args) {
        // configura a porta do serviço
        port(1111);

        // filtro para definir o tipo de conteúdo como JSON
        after(new Filter() {
            @Override
            public void handle(Request request, Response response) {
                response.type(APPLICATION_JSON);
            }
        });

        // GET /produtos - Buscar todos
        get("/produtos", new Route() {
            @Override
            public Object handle(Request request, Response response) {
                return gson.toJson(dao.buscarTodos());
            }
        });

    }

}
