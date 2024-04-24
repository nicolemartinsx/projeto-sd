
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

        try {
            serverSocket = new ServerSocket(22000);
            try {
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
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(),
                    true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Server recebeu: " + inputLine);

                JSONObject requisicao = new JSONObject(inputLine);
                JSONObject resposta = new JSONObject();
                resposta.put("operacao", requisicao.getString("operacao"));

                switch (requisicao.getString("operacao")) {
                    case "cadastrarCandidato" -> {
                        String emailQuery = "SELECT id_candidato FROM candidato WHERE email = ?;";
                        try (PreparedStatement emailPS = conn.prepareStatement(emailQuery)) {
                            emailPS.setString(1, requisicao.getString("email"));
                            try (ResultSet emailCount = emailPS.executeQuery()) {
                                if (!emailCount.next()) {
                                    String query = "INSERT INTO `candidato` (`nome`, `email`, `senha`) VALUES (?, ?, ?);";

                                    try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
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
                    }
                    case "loginCandidato" -> {
                        String query = "SELECT id_candidato FROM candidato WHERE email = ? AND senha = ?;";
                        try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
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
                    }
                    case "logout" -> {
                        tokens.remove(requisicao.getString("token"));
                        resposta.put("status", 204);
                    }
                    case "atualizarCandidato" -> {
                        String emailQuery = "SELECT id_candidato FROM candidato WHERE email = ?;";
                        try (PreparedStatement emailPS = conn.prepareStatement(emailQuery)) {
                            emailPS.setString(1, requisicao.getString("email"));
                            try (ResultSet emailCount = emailPS.executeQuery()) {
                                if (emailCount.next()) {
                                    String query = "UPDATE `candidato` SET `nome` = ?, `senha` = ? WHERE email = ?;";

                                    try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
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
                    }
                    case "visualizarCandidato" -> {
                        String emailQuery = "SELECT nome, senha FROM candidato WHERE email = ?;";
                        try (PreparedStatement ps = conn.prepareStatement(emailQuery)) {
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
                    }
                    case "apagarCandidato" -> {
                        String emailQuery = "SELECT id_candidato FROM candidato WHERE email = ?;";
                        try (PreparedStatement ps = conn.prepareStatement(emailQuery)) {
                            ps.setString(1, requisicao.getString("email"));
                            try (ResultSet rs = ps.executeQuery()) {
                                if (rs.next()) {
                                    String deleteQuery = "DELETE FROM `candidato` WHERE `email` = ?;";
                                    try (PreparedStatement psd = conn.prepareStatement(deleteQuery)) {
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
                    }

                    default ->
                        throw new AssertionError();
                }
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
