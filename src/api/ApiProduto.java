package api;

import static spark.Spark.*;

import com.google.gson.Gson;

import dao.ProdutoDAO;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Filter;

public class ApiProduto {

    //instâncias do DAO e GSON
    private static final ProdutoDAO dao = new ProdutoDAO();
    private static final Gson gson = new Gson();
    
    //Constante para garantir que as respostas sejão json
    private static final String APPLICATION_JSON = "application/json";

    public static void main(String[] args) {
        port(1111);

        after(new Filter(){
            @Override
            public void handle(Request request, Response response) throws Exception {
                response.type(APPLICATION_JSON);

            }
        });
        
        get("/produtos", new Route(){
            
        

            @Override
            public Object handle(Request request, Response response) throws Exception {
                return gson.toJson(dao.buscarTodos());
            }});
    }
}