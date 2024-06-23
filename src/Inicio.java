
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.json.JSONArray;
import org.json.JSONObject;

public class Inicio extends javax.swing.JFrame {

    /**
     * Creates new form Inicio
     */
    public Inicio() {
        this.setVisible(true);

        new Thread(() -> {
            String inputLine = null;
            while (this.isVisible()) {
                try {
                    inputLine = SocketModel.getInstance().getIn().readLine();
                } catch (SocketTimeoutException ex) {
                    if (this.isVisible()) {
                        continue;
                    } else {
                        break;
                    }
                } catch (IOException ex) {
                    Logger.getLogger(Inicio.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("Cliente recebeu: " + inputLine);
                JSONObject mensagem = new JSONObject(inputLine);

                SwingUtilities.invokeLater(() -> {
                    switch (mensagem.getString("operacao")) {
                        case "visualizarCandidato":
                            switch (mensagem.getInt("status")) {
                                case 201:
                                    JOptionPane.showMessageDialog(null,
                                            "Nome: " + mensagem.getString("nome") + "\n"
                                            + "Email: " + AuthenticationModel.getInstance().getEmail() + "\n"
                                            + "Senha: " + mensagem.getString("senha")
                                    );
                                    break;

                                default:
                                    JOptionPane.showMessageDialog(null, mensagem.getString("mensagem"), "Erro", JOptionPane.ERROR_MESSAGE);
                                    break;

                            }
                            break;

                        case "visualizarEmpresa":
                            switch (mensagem.getInt("status")) {
                                case 201:
                                    JOptionPane.showMessageDialog(null,
                                            "Razão social: " + mensagem.getString("razaoSocial") + "\n"
                                            + "CNPJ: " + mensagem.getString("cnpj") + "\n"
                                            + "Senha: " + mensagem.getString("senha") + "\n"
                                            + "Ramo: " + mensagem.getString("ramo") + "\n"
                                            + "Descrição: " + mensagem.getString("descricao")
                                    );
                                    break;

                                default:
                                    JOptionPane.showMessageDialog(null, mensagem.getString("mensagem"), "Erro", JOptionPane.ERROR_MESSAGE);
                                    break;

                            }
                            break;

                        case "apagarCandidato":
                        case "apagarEmpresa":
                            switch (mensagem.getInt("status")) {
                                case 201:
                                    JOptionPane.showMessageDialog(null, "Cadastro apagado com sucesso");
                                    AuthenticationModel model = AuthenticationModel.getInstance();
                                    model.setCandidato(null);
                                    model.setToken(null);
                                    model.setEmail(null);
                                    this.dispose();
                                    new Login();
                                    break;

                                default:
                                    JOptionPane.showMessageDialog(null, mensagem.getString("mensagem"), "Erro", JOptionPane.ERROR_MESSAGE);
                                    break;

                            }
                            break;

                        case "logout":
                            AuthenticationModel model = AuthenticationModel.getInstance();
                            model.setCandidato(null);
                            model.setToken(null);
                            model.setEmail(null);
                            this.dispose();
                            new Login();
                            break;

                        case "receberMensagem":
                            switch (mensagem.getInt("status")) {
                                case 201:
                                    List<Object> empresas = mensagem.getJSONArray("empresas").toList();
                                    if (!empresas.isEmpty()) {
                                        JOptionPane.showMessageDialog(null, "Por favor entre em contato com a(s) empresa(s): " + empresas.stream().map((e) -> e.toString()).collect(Collectors.joining(", ")));
                                    }
                                    break;

                                default:
                                    JOptionPane.showMessageDialog(null, mensagem.getString("mensagem"), "Erro", JOptionPane.ERROR_MESSAGE);
                                    break;

                            }
                            break;

                        default:
                            System.err.println("Cliente recebeu operação não registrada: " + mensagem.getString("operacao"));
                            break;
                    }
                });
            }
        }).start();

        initComponents();

        // TODO
        this.btnLogados.setVisible(false);
        if (AuthenticationModel.getInstance().getCandidato()) {
            this.btnCandidatos.setVisible(false);
            JSONObject requisicao = new JSONObject();
            requisicao.put("operacao", "receberMensagem");
            requisicao.put("email", AuthenticationModel.getInstance().getEmail());
            requisicao.put("token", AuthenticationModel.getInstance().getToken());
            System.out.println("Cliente enviou: " + requisicao);
            SocketModel.getInstance().getOut().println(requisicao);
        } else {
            this.btnCompetencias.setVisible(false);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        btnPerfil = new javax.swing.JButton();
        btnAtualizar = new javax.swing.JButton();
        btnApagar = new javax.swing.JButton();
        btnLogout = new javax.swing.JButton();
        btnCompetencias = new javax.swing.JButton();
        btnVagas = new javax.swing.JButton();
        btnCandidatos = new javax.swing.JButton();
        btnLogados = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("HOME");

        jLabel2.setText("Bem vindo!");

        btnPerfil.setText("Perfil");
        btnPerfil.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPerfilActionPerformed(evt);
            }
        });

        btnAtualizar.setText("Atualizar cadastro");
        btnAtualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAtualizarActionPerformed(evt);
            }
        });

        btnApagar.setText("Apagar cadastro");
        btnApagar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnApagarActionPerformed(evt);
            }
        });

        btnLogout.setText("Logout");
        btnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogoutActionPerformed(evt);
            }
        });

        btnCompetencias.setText("Competências");
        btnCompetencias.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCompetenciasActionPerformed(evt);
            }
        });

        btnVagas.setText("Vagas");
        btnVagas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVagasActionPerformed(evt);
            }
        });

        btnCandidatos.setText("Buscar candidatos");
        btnCandidatos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCandidatosActionPerformed(evt);
            }
        });

        btnLogados.setText("Usuários online");
        btnLogados.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogadosActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel2)
                    .addComponent(btnCandidatos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnVagas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnCompetencias, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnPerfil, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnAtualizar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnApagar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnLogout, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnLogados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(40, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(btnPerfil)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnCompetencias)
                .addGap(12, 12, 12)
                .addComponent(btnVagas)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnCandidatos)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnAtualizar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnApagar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnLogout)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnLogados)
                .addContainerGap(23, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnPerfilActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPerfilActionPerformed
        JSONObject requisicao = new JSONObject();
        requisicao.put("operacao", AuthenticationModel.getInstance().getCandidato() ? "visualizarCandidato" : "visualizarEmpresa");
        requisicao.put("email", AuthenticationModel.getInstance().getEmail());
        requisicao.put("token", AuthenticationModel.getInstance().getToken());
        System.out.println("Cliente enviou: " + requisicao);
        SocketModel.getInstance().getOut().println(requisicao);
    }//GEN-LAST:event_btnPerfilActionPerformed

    private void btnAtualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAtualizarActionPerformed
        this.dispose();
        if (AuthenticationModel.getInstance().getCandidato()) {
            new CadastroCandidato(true);
        } else {
            new CadastroEmpresa(true);
        }
    }//GEN-LAST:event_btnAtualizarActionPerformed

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        JSONObject requisicao = new JSONObject();
        requisicao.put("operacao", "logout");
        requisicao.put("token", AuthenticationModel.getInstance().getToken());
        System.out.println("Cliente enviou: " + requisicao);
        SocketModel.getInstance().getOut().println(requisicao);
    }//GEN-LAST:event_btnLogoutActionPerformed

    private void btnApagarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnApagarActionPerformed
        JSONObject requisicao = new JSONObject();
        requisicao.put("operacao", AuthenticationModel.getInstance().getCandidato() ? "apagarCandidato" : "apagarEmpresa");
        requisicao.put("email", AuthenticationModel.getInstance().getEmail());
        requisicao.put("token", AuthenticationModel.getInstance().getToken());
        System.out.println("Cliente enviou: " + requisicao);
        SocketModel.getInstance().getOut().println(requisicao);
    }//GEN-LAST:event_btnApagarActionPerformed

    private void btnCompetenciasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCompetenciasActionPerformed
        this.dispose();
        new Competencias();
    }//GEN-LAST:event_btnCompetenciasActionPerformed

    private void btnVagasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVagasActionPerformed
        this.dispose();
        if (AuthenticationModel.getInstance().getCandidato()) {
            new VagasCandidato();
        } else {
            new VagasEmpresa();
        }
    }//GEN-LAST:event_btnVagasActionPerformed

    private void btnCandidatosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCandidatosActionPerformed
        this.dispose();
        new BuscarCandidatos();
    }//GEN-LAST:event_btnCandidatosActionPerformed

    private void btnLogadosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogadosActionPerformed
        this.dispose();
        new UsuariosOnline();
    }//GEN-LAST:event_btnLogadosActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnApagar;
    private javax.swing.JButton btnAtualizar;
    private javax.swing.JButton btnCandidatos;
    private javax.swing.JButton btnCompetencias;
    private javax.swing.JButton btnLogados;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnPerfil;
    private javax.swing.JButton btnVagas;
    private javax.swing.JLabel jLabel2;
    // End of variables declaration//GEN-END:variables
}
