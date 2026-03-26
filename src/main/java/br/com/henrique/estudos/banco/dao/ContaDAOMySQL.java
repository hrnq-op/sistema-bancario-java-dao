package br.com.henrique.estudos.banco.dao;

import br.com.henrique.estudos.banco.exceptions.ValorInvalidoException;
import br.com.henrique.estudos.banco.model.*;
import br.com.henrique.estudos.banco.util.Conexao;
import br.com.henrique.estudos.banco.exceptions.CpfInvalidoException;
import br.com.henrique.estudos.banco.exceptions.ConfiguracaoBancoException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContaDAOMySQL implements IContaDAO {

    private final Connection conn;

    public ContaDAOMySQL() {
        try {
            this.conn = Conexao.getConnection();
        } catch (SQLException e) {
            throw new ConfiguracaoBancoException("Erro crítico: Não foi possível conectar ao MySQL. " + e.getMessage());
        }
    }

    @Override
    public void salvar(Conta conta) {
        String sqlCliente = "INSERT INTO clientes (nome, cpf, profissao, login, senha) VALUES (?, ?, ?, ?, ?)";
        String sqlConta = "INSERT INTO contas (agencia, numero, saldo, limite, tipo_conta, cliente_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement psCliente = conn.prepareStatement(sqlCliente, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement psConta = conn.prepareStatement(sqlConta)) {

            conn.setAutoCommit(false);

            psCliente.setString(1, conta.getTitular().getNome());
            psCliente.setString(2, conta.getTitular().getCpf());
            psCliente.setString(3, conta.getTitular().getProfissao());
            psCliente.setString(4, conta.getTitular().getLogin());
            psCliente.setString(5, conta.getTitular().getSenha());
            psCliente.executeUpdate();

            int clienteId;
            try (ResultSet rs = psCliente.getGeneratedKeys()) {
                if (rs.next()) {
                    clienteId = rs.getInt(1);
                } else {
                    throw new SQLException("Falha ao obter o ID do cliente inserido.");
                }
            }

            psConta.setInt(1, conta.getAgencia());
            psConta.setInt(2, conta.getNumero());
            psConta.setDouble(3, conta.getSaldo());

            if (conta instanceof ContaCorrente) {
                psConta.setDouble(4, ((ContaCorrente) conta).getLimite());
                psConta.setString(5, "CORRENTE");
            } else {
                psConta.setDouble(4, 0.0);
                psConta.setString(5, "POUPANCA");
            }

            psConta.setInt(6, clienteId);
            psConta.executeUpdate();

            conn.commit();
            System.out.println("Sucesso: Cliente e Conta salvos no banco de dados.");

        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); }
            catch (SQLException rollbackEx) { System.err.println("Erro crítico ao tentar rollback: " + rollbackEx.getMessage()); }
            throw new ConfiguracaoBancoException("Erro ao salvar conta: " + e.getMessage());
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); }
            catch (SQLException e) { System.err.println("Erro ao resetar AutoCommit: " + e.getMessage()); }
        }
    }

    @Override
    public void atualizar(Conta conta) {
        String sql = "UPDATE contas SET saldo = ?, limite = ? WHERE numero = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, conta.getSaldo());
            double limite = (conta instanceof ContaCorrente) ? ((ContaCorrente) conta).getLimite() : 0.0;
            ps.setDouble(2, limite);
            ps.setInt(3, conta.getNumero());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar conta: " + e.getMessage());
        }
    }

    @Override
    public void deletar(int numero) {
        String sqlBuscaId = "SELECT cliente_id FROM contas WHERE numero = ?";
        String sqlDelConta = "DELETE FROM contas WHERE numero = ?";
        String sqlDelCliente = "DELETE FROM clientes WHERE id = ?";

        try {
            conn.setAutoCommit(false);

            int idCliente = -1;
            try (PreparedStatement psBusca = conn.prepareStatement(sqlBuscaId)) {
                psBusca.setInt(1, numero);
                ResultSet rs = psBusca.executeQuery();
                if (rs.next()) {
                    idCliente = rs.getInt("cliente_id");
                }
            }

            if (idCliente == -1) {
                System.out.println("Aviso: Conta " + numero + " não encontrada.");
                conn.rollback();
                return;
            }

            try (PreparedStatement psConta = conn.prepareStatement(sqlDelConta)) {
                psConta.setInt(1, numero);
                psConta.executeUpdate();
            }

            try (PreparedStatement psCliente = conn.prepareStatement(sqlDelCliente)) {
                psCliente.setInt(1, idCliente);
                psCliente.executeUpdate();
            }

            conn.commit();
            System.out.println("Sucesso: Conta " + numero + " e seu titular foram removidos.");

        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) {
                System.err.println("Erro: " + ex.getMessage());
            }
            throw new ConfiguracaoBancoException("Erro ao excluir conta e cliente: " + e.getMessage());
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException e) {
                System.err.print("Erro: " + e.getMessage());
            }
        }
    }

    @Override
    public Conta buscarPorNumero(int numero){
        String sql = "SELECT c.*, cl.nome, cl.cpf, cl.profissao, cl.login, cl.senha " +
                "FROM contas c INNER JOIN clientes cl ON c.cliente_id = cl.id WHERE c.numero = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, numero);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Cliente cliente;
                    try {
                        cliente = new Cliente(
                                rs.getString("nome"),
                                rs.getString("cpf"),
                                rs.getString("profissao"),
                                rs.getString("login"),
                                rs.getString("senha")
                        );
                    } catch (CpfInvalidoException | ValorInvalidoException e) {
                        System.err.println("Erro ao carregar dados do cliente do banco: " + e.getMessage());
                        return null;
                    }

                    String tipo = rs.getString("tipo_conta");
                    double saldoDoBanco = rs.getDouble("saldo");
                    if ("CORRENTE".equals(tipo)) {
                        ContaCorrente cc = new ContaCorrente(
                                rs.getInt("agencia"),
                                rs.getInt("numero"),
                                cliente,
                                rs.getDouble("limite")
                        );
                        cc.setSaldo(saldoDoBanco);
                        return cc;
                    } else {
                        ContaPoupanca cp = new ContaPoupanca(
                                rs.getInt("agencia"),
                                rs.getInt("numero"),
                                cliente
                        );
                        cp.setSaldo(saldoDoBanco);
                        return cp;
                    }
                }
            }
        } catch (SQLException | ValorInvalidoException e) {
            System.err.println("Erro de SQL ao buscar a conta: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Conta buscarPorLogin(String login, String senha) {
        String sql = "SELECT c.*, cl.nome, cl.cpf, cl.profissao, cl.login, cl.senha " +
                "FROM contas c INNER JOIN clientes cl ON c.cliente_id = cl.id " +
                "WHERE cl.login = ? AND cl.senha = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, login);
            ps.setString(2, senha);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {

                    Cliente cliente;
                    try {
                        cliente = new Cliente(
                                rs.getString("nome"),
                                rs.getString("cpf"),
                                rs.getString("profissao"),
                                rs.getString("login"),
                                rs.getString("senha")
                        );
                    } catch (CpfInvalidoException | ValorInvalidoException e) {
                        System.err.println("Erro ao carregar dados do cliente do banco: " + e.getMessage());
                        return null;
                    }

                    String tipo = rs.getString("tipo_conta");
                    double saldoDoBanco = rs.getDouble("saldo");

                    if ("CORRENTE".equals(tipo)) {
                        ContaCorrente cc = new ContaCorrente(
                                rs.getInt("agencia"),
                                rs.getInt("numero"),
                                cliente,
                                rs.getDouble("limite")
                        );
                        cc.setSaldo(saldoDoBanco);
                        return cc;
                    } else {
                        ContaPoupanca cp = new ContaPoupanca(
                                rs.getInt("agencia"),
                                rs.getInt("numero"),
                                cliente
                        );
                        cp.setSaldo(saldoDoBanco);
                        return cp;
                    }
                }
            }
        } catch (SQLException | ValorInvalidoException e) {
            System.err.println("Erro ao buscar por login: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Conta> listarTodos() {
        List<Conta> lista = new ArrayList<>();
        String sql = "SELECT c.*, cl.nome, cl.cpf, cl.profissao, cl.login, cl.senha " +
                "FROM contas c INNER JOIN clientes cl ON c.cliente_id = cl.id";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Cliente cliente;
                try {
                    cliente = new Cliente(
                            rs.getString("nome"),
                            rs.getString("cpf"),
                            rs.getString("profissao"),
                            rs.getString("login"),
                            rs.getString("senha")
                    );
                } catch (CpfInvalidoException | ValorInvalidoException e) {
                    System.err.println("ERRO DE INTEGRIDADE: Dados no banco violam as regras do sistema! " + e.getMessage());
                    continue;
                }

                String tipo = rs.getString("tipo_conta");
                double saldoDoBanco = rs.getDouble("saldo");

                if ("CORRENTE".equals(tipo)) {
                    ContaCorrente cc = new ContaCorrente(
                            rs.getInt("agencia"), rs.getInt("numero"),
                            cliente, rs.getDouble("limite")
                    );
                    cc.setSaldo(saldoDoBanco);
                    lista.add(cc);
                } else {
                    ContaPoupanca cp = new ContaPoupanca(
                            rs.getInt("agencia"), rs.getInt("numero"), cliente
                    );
                    cp.setSaldo(saldoDoBanco);
                    lista.add(cp);
                }
            }
        } catch (SQLException | ValorInvalidoException e) {
            System.err.println("Erro ao listar contas: " + e.getMessage());
        }
        return lista;
    }

    public void fecharConexao() throws SQLException {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            throw new SQLException (e.getMessage());
        }
    }
}