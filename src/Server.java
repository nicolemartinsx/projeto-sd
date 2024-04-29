
import java.net.*;
import java.io.*;
import org.json.JSONObject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server extends Thread {

    private final Socket clientSocket;
    private final Connection conn;
    private final Map<String, String> tokens = new HashMap<>();

    public static void main(String[] args) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {
        // Registra o driver do MySQL
        Class.forName("com.mysql.cj.jdbc.Driver").newInstance();

        ServerSocket serverSocket = null;

        Integer porta;
        if (args.length == 1) {
            porta = Integer.valueOf(args[0]);
        } else {
            System.out.println("Porta: ");
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            porta = Integer.valueOf(br.readLine());
        }

        try {
            serverSocket = new ServerSocket(porta);
            try {
                System.out.println("Accepting new connections.");
                while (true) {
                    new Server(serverSocket.accept());
                }
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port: 22000.");
            System.exit(1);

        } finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                System.err.println("Could not close port: 10008.");
                System.exit(1);
            }
        }
    }

    public Server(Socket clientSoc) throws SQLException {
        clientSocket = clientSoc;
        conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1/projetosd?"
                + "user=root&password=");
        start();
    }

    @Override
    public void run() {
        try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Servidor recebeu: " + inputLine);

                JSONObject requisicao = new JSONObject(inputLine);
                JSONObject resposta = new JSONObject();
                resposta.put("operacao", requisicao.getString("operacao"));

                switch (requisicao.getString("operacao")) {
                    case "cadastrarCandidato":
                        try (PreparedStatement emailPS = conn.prepareStatement("SELECT id_candidato FROM candidato WHERE email = ?;")) {
                            emailPS.setString(1, requisicao.getString("email"));
                            try (ResultSet emailCount = emailPS.executeQuery()) {
                                if (!emailCount.next()) {
                                    try (PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO `candidato` (`nome`, `email`, `senha`) VALUES (?, ?, ?);")) {
                                        preparedStatement.setString(1, requisicao.getString("nome"));
                                        preparedStatement.setString(2, requisicao.getString("email"));
                                        preparedStatement.setString(3, requisicao.getString("senha"));
                                        preparedStatement.executeUpdate();
                                    }
                                    resposta.put("status", 201);
                                    String token = UUID.randomUUID().toString();
                                    tokens.put(token, requisicao.getString("email"));
                                    resposta.put("token", token);
                                } else {
                                    resposta.put("status", 422);
                                    resposta.put("mensagem", "E-mail já cadastrado");
                                }
                            }
                        } catch (Exception ex) {
                            resposta.put("status", 404);
                            resposta.put("mensagem", "Erro");
                        }
                        break;

                    case "loginCandidato":
                        try (PreparedStatement preparedStatement = conn.prepareStatement("SELECT id_candidato FROM candidato WHERE email = ? AND senha = ?;")) {
                            preparedStatement.setString(1, requisicao.getString("email"));
                            preparedStatement.setString(2, requisicao.getString("senha"));
                            try (ResultSet count = preparedStatement.executeQuery()) {
                                if (count.next()) {
                                    resposta.put("status", 200);
                                    String token = UUID.randomUUID().toString();
                                    tokens.put(token, requisicao.getString("email"));
                                    resposta.put("token", token);
                                } else {
                                    resposta.put("status", 401);
                                    resposta.put("mensagem", "Login ou senha incorretos");
                                }
                            }
                        } catch (Exception ex) {
                            resposta.put("status", 404);
                            resposta.put("mensagem", "Erro");
                        }
                        break;

                    case "logout":
                        tokens.remove(requisicao.getString("token"));
                        resposta.put("status", 204);
                        break;

                    case "atualizarCandidato":
                        try (PreparedStatement emailPS = conn.prepareStatement("SELECT id_candidato FROM candidato WHERE email = ?;")) {
                            emailPS.setString(1, requisicao.getString("email"));
                            try (ResultSet emailCount = emailPS.executeQuery()) {
                                if (emailCount.next()) {
                                    try (PreparedStatement preparedStatement = conn.prepareStatement("UPDATE `candidato` SET `nome` = ?, `senha` = ? WHERE email = ?;")) {
                                        preparedStatement.setString(1, requisicao.getString("nome"));
                                        preparedStatement.setString(2, requisicao.getString("senha"));
                                        preparedStatement.setString(3, requisicao.getString("email"));
                                        preparedStatement.executeUpdate();
                                    }
                                    resposta.put("status", 201);
                                } else {
                                    resposta.put("status", 404);
                                    resposta.put("mensagem", "E-mail não encontrado");
                                }
                            }
                        } catch (Exception ex) {
                            resposta.put("status", 404);
                            resposta.put("mensagem", "Erro");
                        }
                        break;

                    case "visualizarCandidato":
                        try (PreparedStatement ps = conn.prepareStatement("SELECT nome, senha FROM candidato WHERE email = ?;")) {
                            ps.setString(1, requisicao.getString("email"));
                            try (ResultSet rs = ps.executeQuery()) {
                                if (rs.next()) {
                                    resposta.put("status", 201);
                                    resposta.put("nome", rs.getString("nome"));
                                    resposta.put("senha", rs.getString("senha"));
                                } else {
                                    resposta.put("status", 404);
                                    resposta.put("mensagem", "E-mail não encontrado");
                                }
                            }
                        } catch (Exception ex) {
                            resposta.put("status", 404);
                            resposta.put("mensagem", "Erro");
                        }
                        break;

                    case "apagarCandidato":
                        try (PreparedStatement ps = conn.prepareStatement("SELECT id_candidato FROM candidato WHERE email = ?;")) {
                            ps.setString(1, requisicao.getString("email"));
                            try (ResultSet rs = ps.executeQuery()) {
                                if (rs.next()) {
                                    try (PreparedStatement psd = conn.prepareStatement("DELETE FROM `candidato` WHERE `email` = ?;")) {
                                        psd.setString(1, requisicao.getString("email"));
                                        psd.executeUpdate();

                                        resposta.put("status", 201);
                                    }
                                } else {
                                    resposta.put("status", 404);
                                    resposta.put("mensagem", "E-mail não encontrado");
                                }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            resposta.put("status", 404);
                            resposta.put("mensagem", "Erro");
                        }
                        break;

                    default:
                        throw new AssertionError();

                }
                System.out.println("Servidor enviou: " + resposta);
                out.println(resposta);
            }
            out.close();
            in.close();
            clientSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
