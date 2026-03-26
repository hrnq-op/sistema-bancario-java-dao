package br.com.henrique.estudos.banco;

import br.com.henrique.estudos.banco.dao.ContaDAOMySQL;
import br.com.henrique.estudos.banco.model.*;
import br.com.henrique.estudos.banco.service.ContaService;
import br.com.henrique.estudos.banco.util.ConfigLoader;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner entrada = new Scanner(System.in);
        ContaDAOMySQL dao = new ContaDAOMySQL();
        ContaService service = new ContaService(dao);

        int opcao = -1;

        while (opcao != 0) {
            System.out.println("\n=== BANCO ===");
            System.out.println("1- Sou Cliente");
            System.out.println("2- Sou Gerente (Painel Administrativo)");
            System.out.println("0- Sair do Sistema");
            System.out.print("Escolha seu perfil: ");

            try {
                opcao = entrada.nextInt();
                entrada.nextLine();

                switch (opcao) {
                    case 1:
                        loginCliente(entrada, service, dao);
                        break;
                    case 2:
                        loginGerente(entrada, service, dao);
                        break;
                    case 0:
                        System.out.println("Encerrando...");
                        dao.fecharConexao();
                        break;
                    default:
                    System.out.println("Opção inválida!");
                }
            } catch (Exception e) {
                System.err.println("Erro na entrada: " + e.getMessage());
                entrada.nextLine();
            }
        }
    }

    private static void loginCliente(Scanner entrada, ContaService service, ContaDAOMySQL dao) {
        System.out.println("\n--- LOGIN DO CLIENTE ---");
        System.out.print("Login: ");
        String login = entrada.nextLine();
        System.out.print("Senha: ");
        String senha = entrada.nextLine();

        Conta contaLogada = dao.buscarPorLogin(login, senha);

        if (contaLogada == null) {
            System.err.println("ACESSO NEGADO! Login ou senha incorretos.");
        }else {
            System.out.println("\nBem-vindo(a), " + contaLogada.getTitular().getNome() + "!");
            menuCliente(entrada, service, dao, contaLogada);
        }
    }

    private static void loginGerente(Scanner entrada, ContaService service, ContaDAOMySQL dao) {
        System.out.print("Nome do Gerente: ");
        String nomeInformado = entrada.nextLine();
        System.out.print("Senha: ");
        String senhaInformada = entrada.nextLine();

        String nomeConfig = ConfigLoader.get("gerente.nome");
        String senhaConfig = ConfigLoader.get("gerente.senha");

        if (nomeInformado.equals(nomeConfig) && senhaInformada.equals(senhaConfig)) {
            menuGerente(entrada, service, dao);
        } else {
            System.err.println("ACESSO NEGADO! Credenciais incorretas.");
        }
    }

    private static void menuCliente(Scanner entrada, ContaService service, ContaDAOMySQL dao, Conta contaLogada) {
        int op = -1;

        while (op != 0) {
            System.out.println("\n--- ÁREA DO CLIENTE ---");
            System.out.println("Conta: " + contaLogada.getNumero() + " | Titular: " + contaLogada.getTitular().getNome());
            System.out.println("1- Consultar Saldo");
            System.out.println("2- Sacar");
            System.out.println("3- Depositar");
            System.out.println("4- Transferência");
            System.out.println("0- Sair");
            System.out.print("Opção: ");

            try {
                op = entrada.nextInt();
                entrada.nextLine();

                switch (op) {
                    case 1:
                        Conta atualizada = dao.buscarPorNumero(contaLogada.getNumero());
                        System.out.println("\n[SALDO ATUAL]\n" + atualizada);
                        break;
                    case 2:
                        System.out.print("Valor do saque: R$ ");
                        double valorSaque = entrada.nextDouble();
                        service.sacar(contaLogada.getNumero(), valorSaque);
                        break;
                    case 3:
                        System.out.print("Valor do depósito: R$ ");
                        double valorDep = entrada.nextDouble();
                        service.depositar(contaLogada.getNumero(), valorDep);
                        break;
                    case 4:
                        System.out.print("Número da conta de DESTINO: ");
                        int dest = entrada.nextInt();
                        System.out.print("Valor da transferência: R$ ");
                        double valor = entrada.nextDouble();
                        service.transferir(contaLogada.getNumero(), dest, valor);
                        break;
                    case 0:
                        System.out.println("Saindo da área do cliente...");
                        break;
                    default:
                        System.out.println("Opção inválida!");
                }
            } catch (Exception e) {
                System.err.println("ERRO: " + e.getMessage());
                entrada.nextLine();
            }
        }
    }

    private static void menuGerente(Scanner entrada, ContaService service, ContaDAOMySQL dao) {
        int opGerente = -1;
        while (opGerente != 0) {
            System.out.println("\n=== PAINEL ADMINISTRATIVO (GERENTE) ===");
            System.out.println("1- Criar Conta Corrente");
            System.out.println("2- Criar Conta Poupança");
            System.out.println("3- Aplicar Rendimento Mensal (Todas as Poupanças)");
            System.out.println("4- Listar Todas as Contas do Banco");
            System.out.println("5- Deletar Conta");
            System.out.println("0- Voltar ao Menu Inicial");
            System.out.print("Opção: ");
            opGerente = entrada.nextInt();
            entrada.nextLine();

            switch (opGerente) {
                case 1:
                    try {
                        System.out.print("Nome do Titular: ");
                        String nome = entrada.nextLine();
                        System.out.print("CPF: ");
                        String cpf = entrada.nextLine();
                        System.out.print("Profissão: ");
                        String prof = entrada.nextLine();
                        System.out.print("Login do cliente: ");
                        String login = entrada.nextLine();
                        System.out.print("Senha do cliente: ");
                        String senha = entrada.nextLine();
                        System.out.print("Número da Conta: ");
                        int numCC = entrada.nextInt();
                        System.out.print("Limite do Cheque Especial: R$ ");
                        double limite = entrada.nextDouble();
                        entrada.nextLine();
                        Cliente c = new Cliente(nome, cpf, prof, login, senha);
                        ContaCorrente cc = new ContaCorrente(1, numCC, c, limite);
                        service.salvarNovaConta(cc);
                    } catch (Exception e) {
                        System.err.println("ERRO ao criar conta corrente: " + e.getMessage());
                    }
                    break;
                case 2:
                    try {
                        System.out.print("Nome do Titular: ");
                        String nomeP = entrada.nextLine();
                        System.out.print("CPF: ");
                        String cpfP = entrada.nextLine();
                        System.out.print("Profissão: ");
                        String profP = entrada.nextLine();
                        System.out.print("Login do cliente: ");
                        String loginP = entrada.nextLine();
                        System.out.print("Senha do cliente: ");
                        String senhaP = entrada.nextLine();
                        System.out.print("Número da Conta: ");
                        int numCP = entrada.nextInt();
                        entrada.nextLine();
                        Cliente c2 = new Cliente(nomeP, cpfP, profP, loginP, senhaP);
                        ContaPoupanca cp = new ContaPoupanca(1, numCP, c2);
                        service.salvarNovaConta(cp);
                    } catch (Exception e) {
                        System.err.println("ERRO ao criar conta poupança: " + e.getMessage());
                    }
                    break;
                case 3:
                    service.aplicarRendimentoMensal();
                    break;
                case 4:
                    dao.listarTodos().forEach(System.out::println);
                    break;
                case 5:
                    System.out.print("Número da conta para deletar: ");
                    int num = entrada.nextInt();
                    dao.deletar(num);
                    break;
                case 0:
                    System.out.println("Saindo do painel de gerente...");
                    break;
            }
        }
    }
}