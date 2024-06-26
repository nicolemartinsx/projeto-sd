
import java.net.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import org.json.JSONObject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.json.JSONArray;

public class Server extends Thread {

    private final Socket clientSocket;
    private final Connection conn;
    private final Map<String, String> tokens = new HashMap<>();

    public static void main(String[] args) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
        // Registra o driver do MySQL
        Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();

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
                System.out.println("Esperando conexão...");
                while (true) {
                    new Server(serverSocket.accept());
                }
            } catch (IOException e) {
                System.err.println("Erro na conexão");
                System.exit(1);
            }
        } catch (IOException e) {
            System.err.println("Não foi possível escutar na porta: " + porta);
            System.exit(1);

        } finally {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                System.err.println("Não foi possível fechar a porta: " + porta);
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
        System.out.println("Cliente conectado");
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

                String operacao = requisicao.getString("operacao");
                if (!operacao.contains("loginCandidato") && !operacao.contains("loginEmpresa") && !operacao.contains("cadastrarCandidato") && !operacao.contains("cadastrarEmpresa")) {
                    if (!requisicao.has("token") || !this.tokens.containsKey(requisicao.getString("token"))) {
                        resposta.put("status", "401");
                        resposta.put("mensagem", "Token inválido");
                        System.out.println("Servidor enviou: " + resposta);
                        out.println(resposta);
                        continue;
                    }
                }

                switch (requisicao.getString("operacao")) {
                    case "cadastrarCandidato":
                        try (PreparedStatement emailPS = conn.prepareStatement("SELECT id_candidato FROM candidato WHERE email = ?;")) {
                            emailPS.setString(1, requisicao.getString("email"));
                            try (ResultSet emailCount = emailPS.executeQuery()) {
                                if (!emailCount.next()) {
                                    try (PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO candidato (nome, email, senha) VALUES (?, ?, ?);")) {
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
                            ex.printStackTrace();
                            resposta.put("status", 404);
                            resposta.put("mensagem", "Erro");
                        }
                        break;

                    case "cadastrarEmpresa":
                        try (PreparedStatement emailPS = conn.prepareStatement("SELECT id_empresa FROM empresa WHERE email = ?;")) {
                            emailPS.setString(1, requisicao.getString("email"));
                            try (ResultSet emailCount = emailPS.executeQuery()) {
                                if (!emailCount.next()) {
                                    try (PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO empresa (razao_social, cnpj, email, ramo, descricao, senha) VALUES (?, ?, ?, ?, ?, ?);")) {
                                        preparedStatement.setString(1, requisicao.getString("razaoSocial"));
                                        preparedStatement.setString(2, requisicao.getString("cnpj"));
                                        preparedStatement.setString(3, requisicao.getString("email"));
                                        preparedStatement.setString(4, requisicao.getString("ramo"));
                                        preparedStatement.setString(5, requisicao.getString("descricao"));
                                        preparedStatement.setString(6, requisicao.getString("senha"));
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
                            ex.printStackTrace();
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
                                    System.err.println("Usuário " + requisicao.getString("email") + " logado");
                                } else {
                                    resposta.put("status", 401);
                                    resposta.put("mensagem", "Login ou senha incorretos");
                                }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            resposta.put("status", 404);
                            resposta.put("mensagem", "Erro");
                        }
                        break;

                    case "loginEmpresa":
                        try (PreparedStatement preparedStatement = conn.prepareStatement("SELECT id_empresa FROM empresa WHERE email = ? AND senha = ?;")) {
                            preparedStatement.setString(1, requisicao.getString("email"));
                            preparedStatement.setString(2, requisicao.getString("senha"));
                            try (ResultSet count = preparedStatement.executeQuery()) {
                                if (count.next()) {
                                    resposta.put("status", 200);
                                    String token = UUID.randomUUID().toString();
                                    tokens.put(token, requisicao.getString("email"));
                                    resposta.put("token", token);
                                    System.err.println("Usuário " + requisicao.getString("email") + " logado");
                                } else {
                                    resposta.put("status", 401);
                                    resposta.put("mensagem", "Login ou senha incorretos");
                                }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            resposta.put("status", 404);
                            resposta.put("mensagem", "Erro");
                        }
                        break;

                    case "logout":
                        System.err.println("Usuário " + tokens.get(requisicao.getString("token")) + " deslogado");
                        tokens.remove(requisicao.getString("token"));
                        resposta.put("status", 204);
                        break;

                    case "atualizarCandidato":
                        try (PreparedStatement emailPS = conn.prepareStatement("SELECT id_candidato FROM candidato WHERE email = ?;")) {
                            emailPS.setString(1, requisicao.getString("email"));
                            try (ResultSet emailCount = emailPS.executeQuery()) {
                                if (emailCount.next()) {
                                    try (PreparedStatement preparedStatement = conn.prepareStatement("UPDATE candidato SET nome = ?, senha = ? WHERE email = ?;")) {
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
                            ex.printStackTrace();
                            resposta.put("status", 404);
                            resposta.put("mensagem", "Erro");
                        }
                        break;

                    case "atualizarEmpresa":
                        try (PreparedStatement emailPS = conn.prepareStatement("SELECT id_empresa FROM empresa WHERE email = ?;")) {
                            emailPS.setString(1, requisicao.getString("email"));
                            try (ResultSet emailCount = emailPS.executeQuery()) {
                                if (emailCount.next()) {
                                    try (PreparedStatement preparedStatement = conn.prepareStatement("UPDATE projetosd.empresa SET razao_social = ?, cnpj = ?, senha = ?, ramo = ?, descricao = ? WHERE email = ?;")) {
                                        preparedStatement.setString(1, requisicao.getString("razaoSocial"));
                                        preparedStatement.setString(2, requisicao.getString("cnpj"));
                                        preparedStatement.setString(3, requisicao.getString("senha"));
                                        preparedStatement.setString(4, requisicao.getString("ramo"));
                                        preparedStatement.setString(5, requisicao.getString("descricao"));
                                        preparedStatement.setString(6, requisicao.getString("email"));

                                        preparedStatement.executeUpdate();
                                    }
                                    resposta.put("status", 201);
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
                            ex.printStackTrace();
                            resposta.put("status", 404);
                            resposta.put("mensagem", "Erro");
                        }
                        break;

                    case "filtrarCandidatos":
                        JSONObject filtrosCandidatos = requisicao.getJSONObject("filtros");
                        boolean tipoAllCandidatos = filtrosCandidatos.getString("tipo").equals("ALL");
                        boolean tipoAndCandidatos = filtrosCandidatos.getString("tipo").equals("AND");
                        List<Object> filtroCompetenciasExperiencias = filtrosCandidatos.getJSONArray("competenciasExperiencias").toList();
                        String sqlCandidatos = "SELECT DISTINCT c.id_candidato,c.nome,c.email FROM candidato c";
                        if (!tipoAllCandidatos) {
                            sqlCandidatos += " JOIN candidatocompetencia cc ON c.id_candidato = cc.id_candidato WHERE";
                            sqlCandidatos += filtroCompetenciasExperiencias.stream().map((ce) -> " EXISTS(SELECT 1 FROM candidatocompetencia cc1 WHERE cc1.id_candidato=c.id_candidato AND cc1.id_competencia=(SELECT id_competencia FROM competencia WHERE competencia=?)AND cc1.experiencia>=?)").collect(Collectors.joining(tipoAndCandidatos ? "AND" : "OR"));
                        }
                        try (PreparedStatement candidatosPS = conn.prepareStatement(sqlCandidatos)) {
                            int index = 1;
                            for (Object competenciaExperiencia : filtroCompetenciasExperiencias) {
                                candidatosPS.setString(index++, (String) ((HashMap) competenciaExperiencia).get("competencia"));
                                candidatosPS.setInt(index++, (Integer) ((HashMap) competenciaExperiencia).get("experiencia"));
                            }
                            try (ResultSet candidatosRS = candidatosPS.executeQuery()) {
                                JSONArray candidatos = new JSONArray();
                                while (candidatosRS.next()) {
                                    JSONObject candidato = new JSONObject();
                                    candidato.put("idCandidato", candidatosRS.getInt("id_candidato"));
                                    candidato.put("nome", candidatosRS.getString("nome"));
                                    candidato.put("email", candidatosRS.getString("email"));
                                    JSONArray competenciasExperiencias = new JSONArray();
                                    try (PreparedStatement candidatoCompetenciaPS = conn.prepareStatement("SELECT id_competencia, experiencia FROM candidatocompetencia where id_candidato = ?;")) {
                                        candidatoCompetenciaPS.setInt(1, candidatosRS.getInt("id_candidato"));
                                        try (ResultSet candidatoCompetenciaRS = candidatoCompetenciaPS.executeQuery()) {
                                            while (candidatoCompetenciaRS.next()) {
                                                try (PreparedStatement competenciaPS = conn.prepareStatement("SELECT competencia FROM competencia where id_competencia = ?;")) {
                                                    competenciaPS.setInt(1, candidatoCompetenciaRS.getInt("id_competencia"));
                                                    try (ResultSet competenciaRS = competenciaPS.executeQuery()) {
                                                        if (competenciaRS.next()) {
                                                            JSONObject competenciaExperiencia = new JSONObject();
                                                            competenciaExperiencia.put("competencia", competenciaRS.getString("competencia"));
                                                            competenciaExperiencia.put("experiencia", candidatoCompetenciaRS.getInt("experiencia"));
                                                            competenciasExperiencias.put(competenciaExperiencia);
                                                        }
                                                    }
                                                }
                                            }
                                            resposta.put("status", 201);
                                            resposta.put("competenciaExperiencia", competenciasExperiencias);
                                        }
                                    }
                                    candidato.put("competenciaExperiencia", competenciasExperiencias);

                                    candidatos.put(candidato);
                                }
                                resposta.put("status", 201);
                                resposta.put("candidatos", candidatos);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            resposta.put("status", 422);
                            resposta.put("mensagem", "Erro");
                        }
                        break;

                    case "visualizarEmpresa":
                        try (PreparedStatement ps = conn.prepareStatement("SELECT razao_social, cnpj, ramo, descricao, senha FROM empresa WHERE email = ?;")) {
                            ps.setString(1, requisicao.getString("email"));
                            try (ResultSet rs = ps.executeQuery()) {
                                if (rs.next()) {
                                    resposta.put("status", 201);
                                    resposta.put("razaoSocial", rs.getString("razao_social"));
                                    resposta.put("cnpj", rs.getString("cnpj"));
                                    resposta.put("senha", rs.getString("senha"));
                                    resposta.put("ramo", rs.getString("ramo"));
                                    resposta.put("descricao", rs.getString("descricao"));
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

                    case "apagarCandidato":
                        try (PreparedStatement ps = conn.prepareStatement("SELECT id_candidato FROM candidato WHERE email = ?;")) {
                            ps.setString(1, requisicao.getString("email"));
                            try (ResultSet rs = ps.executeQuery()) {
                                if (rs.next()) {
                                    try (PreparedStatement psd = conn.prepareStatement("DELETE FROM candidato WHERE email = ?;")) {
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

                    case "apagarEmpresa":
                        try (PreparedStatement ps = conn.prepareStatement("SELECT id_empresa FROM empresa WHERE email = ?;")) {
                            ps.setString(1, requisicao.getString("email"));
                            try (ResultSet rs = ps.executeQuery()) {
                                if (rs.next()) {
                                    try (PreparedStatement psd = conn.prepareStatement("DELETE FROM empresa WHERE email = ?;")) {
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

                    case "visualizarCompetenciaExperiencia":
                        try (PreparedStatement candidatoPS = conn.prepareStatement("SELECT id_candidato FROM candidato WHERE email = ?;")) {
                            candidatoPS.setString(1, requisicao.getString("email"));
                            try (ResultSet candidato = candidatoPS.executeQuery()) {
                                if (candidato.next()) {
                                    try (PreparedStatement candidatoCompetenciaPS = conn.prepareStatement("SELECT id_competencia, experiencia FROM candidatocompetencia where id_candidato = ?;")) {
                                        candidatoCompetenciaPS.setInt(1, candidato.getInt("id_candidato"));
                                        try (ResultSet candidatoCompetenciaRS = candidatoCompetenciaPS.executeQuery()) {
                                            JSONArray competenciasExperiencias = new JSONArray();
                                            while (candidatoCompetenciaRS.next()) {
                                                try (PreparedStatement competenciaPS = conn.prepareStatement("SELECT competencia FROM competencia where id_competencia = ?;")) {
                                                    competenciaPS.setInt(1, candidatoCompetenciaRS.getInt("id_competencia"));
                                                    try (ResultSet competenciaRS = competenciaPS.executeQuery()) {
                                                        if (competenciaRS.next()) {
                                                            JSONObject competenciaExperiencia = new JSONObject();
                                                            competenciaExperiencia.put("competencia", competenciaRS.getString("competencia"));
                                                            competenciaExperiencia.put("experiencia", candidatoCompetenciaRS.getInt("experiencia"));
                                                            competenciasExperiencias.put(competenciaExperiencia);
                                                        } else {
                                                            resposta.put("status", 422);
                                                            resposta.put("mensagem", "Competência não encontrada");
                                                        }
                                                    }
                                                }
                                            }
                                            resposta.put("status", 201);
                                            resposta.put("competenciaExperiencia", competenciasExperiencias);
                                        }
                                    }
                                } else {
                                    resposta.put("status", 422);
                                    resposta.put("mensagem", "Candidato não encontrado");
                                }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            resposta.put("status", 404);
                            resposta.put("mensagem", "Erro");
                        }
                        break;

                    case "cadastrarCompetenciaExperiencia":
                        try (PreparedStatement candidatoPS = conn.prepareStatement("SELECT id_candidato FROM candidato WHERE email = ?;")) {
                            candidatoPS.setString(1, requisicao.getString("email"));
                            try (ResultSet candidato = candidatoPS.executeQuery()) {
                                if (candidato.next()) {
                                    JSONArray competenciasExperiencias = requisicao.getJSONArray("competenciaExperiencia");
                                    for (int i = 0; i < competenciasExperiencias.length(); i++) {
                                        JSONObject competenciaExperiencia = (JSONObject) competenciasExperiencias.get(i);
                                        try (PreparedStatement competenciaPS = conn.prepareStatement("SELECT id_competencia FROM competencia WHERE competencia = ?;")) {
                                            competenciaPS.setString(1, competenciaExperiencia.getString("competencia"));
                                            try (ResultSet competencia = competenciaPS.executeQuery()) {
                                                if (competencia.next()) {
                                                    try (PreparedStatement candidatoCompetenciaPS = conn.prepareStatement("SELECT id_candidato_competencia FROM candidatocompetencia WHERE id_candidato = ? AND id_competencia = ?;")) {
                                                        candidatoCompetenciaPS.setInt(1, candidato.getInt("id_candidato"));
                                                        candidatoCompetenciaPS.setInt(2, competencia.getInt("id_competencia"));
                                                        try (ResultSet candidatoCompetencia = candidatoCompetenciaPS.executeQuery()) {
                                                            if (!candidatoCompetencia.next()) {
                                                                try (PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO candidatocompetencia (id_candidato, id_competencia, experiencia) VALUES (?, ?, ?);")) {
                                                                    preparedStatement.setInt(1, candidato.getInt("id_candidato"));
                                                                    preparedStatement.setInt(2, competencia.getInt("id_competencia"));
                                                                    preparedStatement.setInt(3, competenciaExperiencia.getInt("experiencia"));
                                                                    preparedStatement.executeUpdate();
                                                                }
                                                                resposta.put("status", 201);
                                                                resposta.put("mensagem", "Competencia/Experiencia cadastrada com sucesso");
                                                            } else {
                                                                resposta.put("status", 422);
                                                                resposta.put("mensagem", "Competência já cadastrada");
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    resposta.put("status", 422);
                                                    resposta.put("mensagem", "Competência não encontrada");
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    resposta.put("status", 422);
                                    resposta.put("mensagem", "Candidato não encontrado");
                                }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            resposta.put("status", 422);
                            resposta.put("mensagem", "Erro");
                        }
                        break;

                    case "atualizarCompetenciaExperiencia":
                        try (PreparedStatement candidatoPS = conn.prepareStatement("SELECT id_candidato FROM candidato WHERE email = ?;")) {
                            candidatoPS.setString(1, requisicao.getString("email"));
                            try (ResultSet candidato = candidatoPS.executeQuery()) {
                                if (candidato.next()) {
                                    JSONArray competenciasExperiencias = requisicao.getJSONArray("competenciaExperiencia");
                                    for (int i = 0; i < competenciasExperiencias.length(); i++) {
                                        JSONObject competenciaExperiencia = (JSONObject) competenciasExperiencias.get(i);
                                        try (PreparedStatement competenciaPS = conn.prepareStatement("SELECT id_competencia FROM competencia WHERE competencia = ?;")) {
                                            competenciaPS.setString(1, competenciaExperiencia.getString("competencia"));
                                            try (ResultSet competencia = competenciaPS.executeQuery()) {
                                                if (competencia.next()) {
                                                    try (PreparedStatement candidatoCompetenciaPS = conn.prepareStatement("SELECT id_candidato_competencia FROM candidatocompetencia WHERE id_candidato = ? AND id_competencia = ?;")) {
                                                        candidatoCompetenciaPS.setInt(1, candidato.getInt("id_candidato"));
                                                        candidatoCompetenciaPS.setInt(2, competencia.getInt("id_competencia"));
                                                        try (ResultSet candidatoCompetencia = candidatoCompetenciaPS.executeQuery()) {
                                                            if (candidatoCompetencia.next()) {
                                                                try (PreparedStatement preparedStatement = conn.prepareStatement("UPDATE candidatocompetencia SET experiencia = ? WHERE id_candidato_competencia = ?")) {
                                                                    preparedStatement.setInt(1, competenciaExperiencia.getInt("experiencia"));
                                                                    preparedStatement.setInt(2, candidatoCompetencia.getInt("id_candidato_competencia"));
                                                                    preparedStatement.executeUpdate();
                                                                }
                                                                resposta.put("status", 201);
                                                                resposta.put("mensagem", "Competencia/Experiencia atualizada com sucesso");
                                                            } else {
                                                                resposta.put("status", 422);
                                                                resposta.put("mensagem", "Competência ainda não cadastrada");
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    resposta.put("status", 422);
                                                    resposta.put("mensagem", "Competência não encontrada");
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    resposta.put("status", 422);
                                    resposta.put("mensagem", "Candidato não encontrado");
                                }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            resposta.put("status", 422);
                            resposta.put("mensagem", "Erro");
                        }
                        break;

                    case "apagarCompetenciaExperiencia":
                        try (PreparedStatement candidatoPS = conn.prepareStatement("SELECT id_candidato FROM candidato WHERE email = ?;")) {
                            candidatoPS.setString(1, requisicao.getString("email"));
                            try (ResultSet candidato = candidatoPS.executeQuery()) {
                                if (candidato.next()) {
                                    JSONArray competenciasExperiencias = requisicao.getJSONArray("competenciaExperiencia");
                                    for (int i = 0; i < competenciasExperiencias.length(); i++) {
                                        JSONObject competenciaExperiencia = (JSONObject) competenciasExperiencias.get(i);
                                        try (PreparedStatement competenciaPS = conn.prepareStatement("SELECT id_competencia FROM competencia WHERE competencia = ?;")) {
                                            competenciaPS.setString(1, competenciaExperiencia.getString("competencia"));
                                            try (ResultSet competencia = competenciaPS.executeQuery()) {
                                                if (competencia.next()) {
                                                    try (PreparedStatement candidatoCompetenciaPS = conn.prepareStatement("SELECT id_candidato_competencia FROM candidatocompetencia WHERE id_candidato = ? AND id_competencia = ?;")) {
                                                        candidatoCompetenciaPS.setInt(1, candidato.getInt("id_candidato"));
                                                        candidatoCompetenciaPS.setInt(2, competencia.getInt("id_competencia"));
                                                        try (ResultSet candidatoCompetencia = candidatoCompetenciaPS.executeQuery()) {
                                                            if (candidatoCompetencia.next()) {
                                                                try (PreparedStatement preparedStatement = conn.prepareStatement("DELETE FROM candidatocompetencia WHERE id_candidato_competencia = ?")) {
                                                                    preparedStatement.setInt(1, candidatoCompetencia.getInt("id_candidato_competencia"));
                                                                    preparedStatement.executeUpdate();
                                                                }
                                                                resposta.put("status", 201);
                                                                resposta.put("mensagem", "Competencia/Experiencia apagada com sucesso");
                                                            } else {
                                                                resposta.put("status", 422);
                                                                resposta.put("mensagem", "Competência ainda não cadastrada");
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    resposta.put("status", 422);
                                                    resposta.put("mensagem", "Competência não encontrada");
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    resposta.put("status", 422);
                                    resposta.put("mensagem", "Candidato não encontrado");
                                }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            resposta.put("status", 422);
                            resposta.put("mensagem", "Erro");
                        }
                        break;

                    case "listarVagas":
                        try (PreparedStatement empresaPS = conn.prepareStatement("SELECT id_empresa FROM empresa WHERE email = ?;")) {
                            empresaPS.setString(1, requisicao.getString("email"));
                            try (ResultSet empresa = empresaPS.executeQuery()) {
                                if (empresa.next()) {

                                    try (PreparedStatement vagasPS = conn.prepareStatement("SELECT id_vaga, nome FROM vaga where id_empresa = ?;")) {
                                        vagasPS.setInt(1, empresa.getInt("id_empresa"));
                                        try (ResultSet vagasRS = vagasPS.executeQuery()) {
                                            JSONArray vagas = new JSONArray();
                                            while (vagasRS.next()) {
                                                JSONObject vaga = new JSONObject();
                                                vaga.put("nome", vagasRS.getString("nome"));
                                                vaga.put("idVaga", vagasRS.getInt("id_vaga"));
                                                vagas.put(vaga);
                                            }
                                            resposta.put("status", 201);
                                            resposta.put("vagas", vagas);
                                        }
                                    }
                                } else {
                                    resposta.put("status", 422);
                                    resposta.put("mensagem", "Empresa não encontrada");
                                }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            resposta.put("status", 404);
                            resposta.put("mensagem", "Erro");
                        }
                        break;

                    case "filtrarVagas":
                        JSONObject filtros = requisicao.getJSONObject("filtros");
                        boolean tipoTodos = filtros.getString("tipo").equals("ALL");
                        boolean tipoAnd = filtros.getString("tipo").equals("AND");
                        List<Object> filtroCompetencias = filtros.getJSONArray("competencias").toList();
                        String sql = "SELECT DISTINCT v.id_vaga, v.nome, v.faixa_salarial, v.descricao, v.estado FROM vaga v JOIN vagacompetencia vc ON v.id_vaga=vc.id_vaga JOIN competencia c ON vc.id_competencia=c.id_competencia";
                        String divulgavelSql = " AND estado IN('Divulgavel', 'Divulgável', 'divulgavel', 'divulgável')";
                        if (!tipoTodos) {
                            sql += " WHERE c.competencia IN(" + String.join(",", Collections.nCopies(filtroCompetencias.size(), "?")) + ")" + divulgavelSql + " GROUP BY v.id_vaga";
                            if (tipoAnd) {
                                sql += " HAVING COUNT(vc.id_vaga_competencia) >= ?";
                            }
                        } else {
                            sql += divulgavelSql;
                        }
                        try (PreparedStatement vagasPS = conn.prepareStatement(sql)) {
                            int index = 1;
                            for (Object competencia : filtroCompetencias) {
                                vagasPS.setString(index++, (String) competencia);
                            }
                            if (tipoAnd) {
                                vagasPS.setInt(index, filtroCompetencias.size());
                            }
                            try (ResultSet vagasRS = vagasPS.executeQuery()) {
                                JSONArray vagas = new JSONArray();
                                while (vagasRS.next()) {
                                    JSONObject vaga = new JSONObject();
                                    vaga.put("idVaga", vagasRS.getInt("id_vaga"));
                                    vaga.put("nome", vagasRS.getString("nome"));
                                    vaga.put("faixaSalarial", vagasRS.getDouble("faixa_salarial"));
                                    vaga.put("descricao", vagasRS.getString("descricao"));
                                    vaga.put("estado", vagasRS.getString("estado"));
                                    JSONArray competencias = new JSONArray();
                                    try (PreparedStatement competenciasPS = conn.prepareStatement("SELECT competencia.competencia as nome FROM vagacompetencia JOIN competencia ON vagacompetencia.id_competencia = competencia.id_competencia where vagacompetencia.id_vaga = ?;")) {
                                        competenciasPS.setInt(1, vagasRS.getInt("id_vaga"));
                                        try (ResultSet competenciasRS = competenciasPS.executeQuery()) {
                                            while (competenciasRS.next()) {
                                                competencias.put(competenciasRS.getString("nome"));
                                            }
                                        }
                                    }
                                    vaga.put("competencias", competencias);

                                    vagas.put(vaga);
                                }
                                resposta.put("status", 201);
                                resposta.put("vagas", vagas);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            resposta.put("status", 422);
                            resposta.put("mensagem", "Erro");
                        }
                        break;


                    case "visualizarVaga":
                        try (PreparedStatement vagasPS = conn.prepareStatement("SELECT faixa_salarial, descricao, estado FROM vaga where id_vaga = ?;")) {
                            vagasPS.setInt(1, requisicao.getInt("idVaga"));
                            try (ResultSet vagaRS = vagasPS.executeQuery()) {
                                if (vagaRS.next()) {

                                    JSONArray competencias = new JSONArray();
                                    try (PreparedStatement competenciasPS = conn.prepareStatement("SELECT competencia.competencia as nome FROM vagacompetencia JOIN competencia ON vagacompetencia.id_competencia = competencia.id_competencia where vagacompetencia.id_vaga = ?;")) {
                                        competenciasPS.setInt(1, requisicao.getInt("idVaga"));
                                        try (ResultSet competenciasRS = competenciasPS.executeQuery()) {
                                            while (competenciasRS.next()) {
                                                competencias.put(competenciasRS.getString("nome"));
                                            }
                                        }
                                    }

                                    resposta.put("status", 201);
                                    resposta.put("faixaSalarial", vagaRS.getDouble("faixa_salarial"));
                                    resposta.put("descricao", vagaRS.getString("descricao"));
                                    resposta.put("estado", vagaRS.getString("estado"));
                                    resposta.put("competencias", competencias);
                                } else {
                                    resposta.put("status", 404);
                                    resposta.put("mensagem", "Vaga não encontrada");
                                }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            resposta.put("status", 404);
                            resposta.put("mensagem", "Erro");
                        }
                        break;

                    case "cadastrarVaga":
                        try (PreparedStatement empresaPS = conn.prepareStatement("SELECT id_empresa FROM empresa WHERE email = ?;")) {
                            empresaPS.setString(1, requisicao.getString("email"));
                            try (ResultSet empresa = empresaPS.executeQuery()) {
                                if (empresa.next()) {
                                    try (PreparedStatement vagaPS = conn.prepareStatement("INSERT INTO vaga (id_empresa, nome, faixa_salarial, descricao, estado) VALUES (?, ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS)) {
                                        vagaPS.setInt(1, empresa.getInt("id_empresa"));
                                        vagaPS.setString(2, requisicao.getString("nome"));
                                        vagaPS.setDouble(3, requisicao.getDouble("faixaSalarial"));
                                        vagaPS.setString(4, requisicao.getString("descricao"));
                                        vagaPS.setString(5, requisicao.getString("estado"));
                                        vagaPS.executeUpdate();

                                        try (ResultSet generatedKeys = vagaPS.getGeneratedKeys()) {
                                            if (generatedKeys.next()) {

                                                // Associando as competencias a vaga
                                                JSONArray competencias = requisicao.getJSONArray("competencias");
                                                for (int i = 0; i < competencias.length(); i++) {
                                                    try (PreparedStatement competenciaPS = conn.prepareStatement("SELECT id_competencia FROM competencia WHERE competencia = ?;")) {
                                                        competenciaPS.setString(1, (String) competencias.get(i));
                                                        try (ResultSet competenciaRS = competenciaPS.executeQuery()) {
                                                            if (competenciaRS.next()) {
                                                                try (PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO vagacompetencia (id_vaga, id_competencia) VALUES (?, ?);")) {
                                                                    preparedStatement.setInt(1, generatedKeys.getInt(1));
                                                                    preparedStatement.setInt(2, competenciaRS.getInt("id_competencia"));
                                                                    preparedStatement.executeUpdate();
                                                                }
                                                                resposta.put("status", 201);
                                                                resposta.put("mensagem", "Vaga cadastrada com sucesso");

                                                            } else {
                                                                resposta.put("status", 422);
                                                                resposta.put("mensagem", "Competência não encontrada");
                                                            }
                                                        }
                                                    }
                                                }

                                            } else {
                                                resposta.put("status", 422);
                                                resposta.put("mensagem", "Falha ao criar vaga");
                                            }
                                        }
                                    }
                                } else {
                                    resposta.put("status", 422);
                                    resposta.put("mensagem", "Empresa não encontrada");
                                }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            resposta.put("status", 422);
                            resposta.put("mensagem", "Erro");
                        }
                        break;

                    case "atualizarVaga":
                        try (PreparedStatement vagaPS = conn.prepareStatement("UPDATE vaga SET nome = ?, faixa_salarial = ?, descricao = ?, estado = ? WHERE id_vaga = ?;")) {
                            vagaPS.setString(1, requisicao.getString("nome"));
                            vagaPS.setDouble(2, requisicao.getDouble("faixaSalarial"));
                            vagaPS.setString(3, requisicao.getString("descricao"));
                            vagaPS.setString(4, requisicao.getString("estado"));
                            vagaPS.setInt(5, requisicao.getInt("idVaga"));
                            vagaPS.executeUpdate();

                            try (PreparedStatement apagarCompetenciasPS = conn.prepareStatement("DELETE FROM vagacompetencia WHERE id_vaga = ?;")) {
                                apagarCompetenciasPS.setInt(1, requisicao.getInt("idVaga"));
                                apagarCompetenciasPS.executeUpdate();

                                // Associando as competencias a vaga
                                JSONArray competencias = requisicao.getJSONArray("competencias");
                                for (int i = 0; i < competencias.length(); i++) {
                                    try (PreparedStatement competenciaPS = conn.prepareStatement("SELECT id_competencia FROM competencia WHERE competencia = ?;")) {
                                        competenciaPS.setString(1, (String) competencias.get(i));
                                        try (ResultSet competenciaRS = competenciaPS.executeQuery()) {
                                            if (competenciaRS.next()) {
                                                try (PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO vagacompetencia (id_vaga, id_competencia) VALUES (?, ?);")) {
                                                    preparedStatement.setInt(1, requisicao.getInt("idVaga"));
                                                    preparedStatement.setInt(2, competenciaRS.getInt("id_competencia"));
                                                    preparedStatement.executeUpdate();
                                                }
                                                resposta.put("status", 201);
                                                resposta.put("mensagem", "Vaga atualizada com sucesso");
                                            } else {
                                                resposta.put("status", 422);
                                                resposta.put("mensagem", "Competência não encontrada");
                                            }
                                        }
                                    }
                                }
                            }

                        } catch (Exception ex) {
                            ex.printStackTrace();
                            resposta.put("status", 422);
                            resposta.put("mensagem", "Erro");
                        }
                        break;

                    case "apagarVaga":
                        try (PreparedStatement apagarCompetenciasPS = conn.prepareStatement("DELETE FROM vagacompetencia WHERE id_vaga = ?;")) {
                            apagarCompetenciasPS.setInt(1, requisicao.getInt("idVaga"));
                            apagarCompetenciasPS.executeUpdate();

                            try (PreparedStatement vagaPS = conn.prepareStatement("DELETE FROM vaga WHERE id_vaga = ?")) {
                                vagaPS.setInt(1, requisicao.getInt("idVaga"));
                                vagaPS.executeUpdate();

                                resposta.put("status", 201);
                                resposta.put("mensagem", "Vaga apagada com sucesso");
                            }

                        } catch (Exception ex) {
                            ex.printStackTrace();
                            resposta.put("status", 422);
                            resposta.put("mensagem", "Erro");
                        }
                        break;

                    case "enviarMensagem":
                        try (PreparedStatement empresaPS = conn.prepareStatement("SELECT id_empresa FROM empresa WHERE email = ?;")) {
                            empresaPS.setString(1, requisicao.getString("email"));
                            try (ResultSet empresaRS = empresaPS.executeQuery()) {
                                if (empresaRS.next()) {
                                    JSONArray candidatos = requisicao.getJSONArray("candidatos");
                                    for (int i = 0; i < candidatos.length(); i++) {
                                        Integer idCandidato = (Integer) candidatos.get(i);
                                        try (PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO candidatomensagem (id_candidato, id_empresa) VALUES (?, ?);")) {
                                            preparedStatement.setInt(1, idCandidato);
                                            preparedStatement.setInt(2, empresaRS.getInt("id_empresa"));
                                            preparedStatement.executeUpdate();
                                        }
                                    }
                                    resposta.put("status", 201);
                                } else {
                                    resposta.put("status", 422);
                                    resposta.put("mensagem", "Empresa não encontrada");
                                }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            resposta.put("status", 404);
                            resposta.put("mensagem", "Erro");
                        }
                        break;

                    case "receberMensagem":
                        try (PreparedStatement mensagemPS = conn.prepareStatement("SELECT DISTINCT e.razao_social as nome, e.email, e.ramo FROM candidatomensagem AS cm JOIN empresa AS e ON cm.id_empresa=e.id_empresa WHERE cm.id_candidato=(SELECT id_candidato FROM candidato WHERE email=?);")) {
                            mensagemPS.setString(1, requisicao.getString("email"));
                            try (ResultSet mensagemRS = mensagemPS.executeQuery()) {
                                try (PreparedStatement apagarPS = conn.prepareStatement("DELETE FROM candidatomensagem WHERE id_candidato=(SELECT id_candidato FROM candidato WHERE email=?);")) {
                                    apagarPS.setString(1, requisicao.getString("email"));
                                    apagarPS.executeUpdate();

                                    JSONArray empresas = new JSONArray();
                                    while (mensagemRS.next()) {
                                        JSONObject empresa = new JSONObject();
                                        empresa.put("nome", mensagemRS.getString("nome"));
                                        empresa.put("email", mensagemRS.getString("email"));
                                        empresa.put("ramo", mensagemRS.getString("ramo"));
                                        empresas.put(empresa);
                                    }
                                    resposta.put("status", 201);
                                    resposta.put("empresas", empresas);
                                }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            resposta.put("status", 422);
                            resposta.put("mensagem", "Erro");
                        }
                        break;

                    default:
                        System.err.println("Servidor recebeu operação não registrada: " + requisicao.getString("operacao"));
                        break;

                }
                System.out.println("Servidor enviou: " + resposta);
                out.println(resposta);
            }
            out.close();
            in.close();
            clientSocket.close();
        } catch (SocketException ex) {
            System.out.println("Cliente desconectado");
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
