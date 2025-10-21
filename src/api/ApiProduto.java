package api;

import static spark.Spark.*;

import com.google.gson.Gson;

import dao.ProdutoDAO;
import model.Produto;
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

         get("/produto/:id", new Route() {
            @Override
            public Object handle(Request request, Response response) {
                Long id = Long.parseLong(request.params(":id"));
                Produto p = dao.buscarPorId(id);
                if(p != null) {
                    return gson.toJson(p);
                } else {
                    response.status(404);
                    return "{\"mensagem\":\"Produto não encontrado\"}";
                }
            }
        });

        post("/produto", new Route() {
            @Override
            public Object handle(Request request, Response response) {
                
                Produto body = gson.fromJson(request.body(), Produto.class);

                if (body == null) {
                    response.status(400);
                    return "{\"mensagem\":\"Requisição inválida\"}";
                }

                dao.inserir(body);
                response.status(201);
                return gson.toJson(body);
            }
        });

        
        put("/produto/:id", new Route() {
            @Override
            public Object handle(Request request, Response response) {
                Long id = Long.parseLong(request.params(":id"));

                Produto produtoExistente = dao.buscarPorId(id);
                if (produtoExistente == null) {
                    response.status(404);
                    return "{\"mensagem\":\"Produto não encontrado\"}";
                }
                Produto body = gson.fromJson(request.body(), Produto.class);
                  if (body == null) {
                    response.status(400);
                    return "{\"mensagem\":\"Requisição inválida\"}";
                }
                produtoExistente.setEstoque(body.getEstoque());
                produtoExistente.setNome(body.getNome());
                produtoExistente.setPreco(body.getPreco());

              

                dao.atualizar(produtoExistente);
                response.status(200);
                return gson.toJson(body);
            }
        });

          delete("/produto/:id", new Route() {
            @Override
            public Object handle(Request request, Response response) {
                Long id = Long.parseLong(request.params(":id"));

                Produto produtoExistente = dao.buscarPorId(id);
                if (produtoExistente == null) {
                    response.status(404);
                    return "{\"mensagem\":\"Produto não encontrado\"}";
                }
                
                dao.deletar(id);
                response.status(200);
                return "{\"mensagem\":\"Produto Excluido com sucesso\"}";
                
            }
        });

    }

    

}
