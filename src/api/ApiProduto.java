package api;

import static spark.Spark.*;

import com.google.gson.Gson;

import dao.ProdutoDAO;
import dao.CategoriaDAO;
import model.Categoria;
import model.Produto;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Filter;

public class ApiProduto {

    // instâncias do DAO e GSON
    private static final ProdutoDAO dao = new ProdutoDAO();
    private static final CategoriaDAO daoc = new CategoriaDAO();
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
                Long id = Long.parseLong(request.params(":id").replaceAll("\\D", ""));
                Produto p = dao.buscarPorId(id);
                if(p != null) {
                    return gson.toJson(p);
                } else {
                    response.status(404);
                    return "{\"mensagem\":\"Produto não encontrado\"}";
                }
            }
        });

        get("/categorias/:id/produtos", new Route() {
            @Override
            public Object handle(Request request, Response response) {
                Long id = Long.parseLong(request.params(":id").replaceAll("\\D", ""));
                return gson.toJson(dao.buscarPorCategoria(id));
            }
        });

        get("/categorias/produtos", new Route() {
            @Override
            public Object handle(Request request, Response response) {
                String nome =request.queryParams("nome");
                Categoria c = daoc.buscarPorNome(nome);

                if(c == null){
                     response.status(404);
                    return "{\"mensagem\":\"Categoria não encontrado\"}";
                }

                return gson.toJson(dao.buscarPorCategoria(c.getId()));
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
                Long id = Long.parseLong(request.params(":id").replaceAll("\\D", ""));

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
                Long id = Long.parseLong(request.params(":id").replaceAll("\\D", ""));

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

        
        // GET /categorias - Buscar todos
        get("/categorias", new Route() {
            @Override
            public Object handle(Request request, Response response) {
                return gson.toJson(daoc.buscarTodos());
            }
        });

         get("/categorias/:id", new Route() {
            @Override
            public Object handle(Request request, Response response) {
                Long id = Long.parseLong(request.params(":id").replaceAll("\\D", ""));
                Categoria c = daoc.buscarPorId(id);
                if(c != null) {
                    return gson.toJson(c);
                } else {
                    response.status(404);
                    return "{\"mensagem\":\"categoria não encontrada\"}";
                }
            }
        });

        post("/categoria", new Route() {
            @Override
            public Object handle(Request request, Response response) {
                
                Categoria body = gson.fromJson(request.body(), Categoria.class);

                if (body == null) {
                    response.status(400);
                    return "{\"mensagem\":\"Requisição inválida\"}";
                }

                Categoria jaExiste = daoc.buscarPorNome(body.getNome().trim());

                if(jaExiste instanceof Categoria){
                    response.status(409);
                    return "{\"mensagem\":\"Categoria com esse nome já existe\"}";
                }

                daoc.inserir(body);
                response.status(201);
                return gson.toJson(body);
            }
        });

        
        put("/categoria/:id", new Route() {
            @Override
            public Object handle(Request request, Response response) {
                Long id = Long.parseLong(request.params(":id").replaceAll("\\D", ""));

                Categoria categoriaExistente = daoc.buscarPorId(id);
                if (categoriaExistente == null) {
                    response.status(404);
                    return "{\"mensagem\":\"Categoria não encontrado\"}";
                }
                Categoria body = gson.fromJson(request.body(), Categoria.class);
                  if (body == null) {
                    response.status(400);
                    return "{\"mensagem\":\"Requisição inválida\"}";
                }
                categoriaExistente.setNome(body.getNome());

                daoc.atualizar(categoriaExistente);
                response.status(200);
                return gson.toJson(body);
            }
        });

          delete("/categoria/:id", new Route() {
            @Override
            public Object handle(Request request, Response response) {
                Long id = Long.parseLong(request.params(":id").replaceAll("\\D", ""));

                Categoria categoriaExistente = daoc.buscarPorId(id);
                if (categoriaExistente == null) {
                    response.status(404);
                    return "{\"mensagem\":\"Categoria não encontrado\"}";
                }
                
                dao.deletar(id);
                response.status(200);
                return "{\"mensagem\":\"Categoria Excluido com sucesso\"}";
                
            }
        });

    }

    

}
