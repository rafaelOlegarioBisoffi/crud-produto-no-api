package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Categoria;
import util.ConnectionFactory;

public class CategoriaDAO {
    // ------------------------------------
    // READ
    // ------------------------------------

    public List<Categoria> buscarTodos() {
        List<Categoria> categorias = new ArrayList<>();
        // query SQL para selecionar todos os campos
        String sql = "SELECT * FROM categorias";
        // o bloco try-with-resources garante que Connection, PreparedStatement e
        // ResultSet
        // serão fechados automaticamente, mesmo que ocorram exceções
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            // itera sobre cada linha retornada pelo banco
            while (rs.next()) {
                // cria um novo objeto Categoria a partir dos dados da linha atual do ResultSet
                Categoria categoria = new Categoria(
                        rs.getLong("id"),
                        rs.getString("nome"));
                categorias.add(categoria);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar categorias: " + e.getMessage());
            e.printStackTrace();
        }
        return categorias;
    }

    // ------------------------------------
    // READ BY ID
    // ------------------------------------
    public Categoria buscarPorId(Long id) {

        Categoria categoria = null;

        // o '?' é um placeholder que será preenchido pelo PreparedStatement
        String sql = "SELECT id, nome FROM categorias WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            // define o valor do parâmetro (o '?' na posição 1)
            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                // se houver resultado, move o cursor e mapeia o objeto
                if (rs.next()) {
                    categoria = new Categoria(
                            rs.getLong("id"),
                            rs.getString("nome"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar categoria por ID: " + id + ". Detalhes: " + e.getMessage());
            e.printStackTrace();
        }
        return categoria;
    }

    public Categoria buscarPorNome(String nome) {

        Categoria categoria = null;

        // o '?' é um placeholder que será preenchido pelo PreparedStatement
        String sql = "SELECT id, nome FROM categorias WHERE nome = ? ";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            // define o valor do parâmetro (o '?' na posição 1)
            stmt.setString(1, nome);

            try (ResultSet rs = stmt.executeQuery()) {
                // se houver resultado, move o cursor e mapeia o objeto
                if (rs.next()) {
                    categoria = new Categoria(
                            rs.getLong("id"),
                            rs.getString("nome"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar categoria por nome: " + nome + ". Detalhes: " + e.getMessage());
            e.printStackTrace();
        }
        return categoria;
    }

    // ------------------------------------
    // CREATE
    // ------------------------------------
    public void inserir(Categoria categoria) {

        // usa Statement.RETURN_GENERATED_KEYS para solicitar o ID gerado
        String sql = "INSERT INTO categorias (nome) VALUES (?)";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // define os parâmetros da query
            stmt.setString(1, categoria.getNome());

            // executa a inserção
            stmt.executeUpdate();

            // recupera a chave gerada (o novo ID)
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    // define o ID no objeto Categoria que foi passado (importante para a API)
                    categoria.setId(rs.getLong(1));
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao inserir categoria: " + categoria.getNome() + ". Detalhes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ------------------------------------
    // UPDATE
    // ------------------------------------
    public void atualizar(Categoria categoria) {

        // a atualização precisa do ID no WHERE e dos novos valores
        String sql = "UPDATE categorias SET nome = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            // define os parâmetros (os novos valores)
            stmt.setString(1, categoria.getNome());

            // define o ID no WHERE (o último '?')
            stmt.setLong(5, categoria.getId());

            // executa a atualização
            int linhasAfetadas = stmt.executeUpdate();
            System.out.println("Categoria ID " + categoria.getId() + " atualizado. Linhas afetadas: " + linhasAfetadas);

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar categoria ID: " + categoria.getId() + ". Detalhes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ------------------------------------
    // DELETE
    // ------------------------------------
    public void deletar(Long id) {

        // a exclusão precisa do ID no WHERE
        String sql = "DELETE FROM categorias WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            // define o ID do categoria a ser deletado
            stmt.setLong(1, id);

            // executa a exclusão
            int linhasAfetadas = stmt.executeUpdate();
            System.out.println("Tentativa de deletar Categoria ID " + id + ". Linhas afetadas: " + linhasAfetadas);

        } catch (SQLException e) {
            System.err.println("Erro ao deletar categoria ID: " + id + ". Detalhes: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

