
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.json.JSONObject;

public class CadastroEmpresa extends javax.swing.JFrame {

    private boolean atualizacao = false;

    public CadastroEmpresa(boolean atualizacao) {
        this.atualizacao = atualizacao;
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
                    Logger.getLogger(CadastroEmpresa.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("Cliente recebeu: " + inputLine);
                JSONObject mensagem = new JSONObject(inputLine);

                SwingUtilities.invokeLater(() -> {
                    switch (mensagem.getString("operacao")) {
                        case "cadastrarEmpresa":
                            switch (mensagem.getInt("status")) {
                                case 201:
                                    AuthenticationModel model = AuthenticationModel.getInstance();
                                    model.setCandidato(false);
                                    model.setEmail(txtEmail.getText());
                                    model.setToken(mensagem.getString("token"));
                                    JOptionPane.showMessageDialog(null, "Cadastrado com sucesso", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                                    this.dispose();
                                    Inicio inicio = new Inicio();
                                    inicio.setVisible(true);
                                    break;

                                default:
                                    JOptionPane.showMessageDialog(null, mensagem.getString("mensagem"), "Erro", JOptionPane.ERROR_MESSAGE);
                                    break;

                            }
                            break;

                        case "atualizarEmpresa":
                            switch (mensagem.getInt("status")) {
                                case 201:
                                    JOptionPane.showMessageDialog(null, "Atualizado com sucesso", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                                    this.dispose();
                                    new Inicio();
                                    break;
                                default:
                                    JOptionPane.showMessageDialog(null, mensagem.getString("mensagem"), "Erro", JOptionPane.ERROR_MESSAGE);
                                    break;
                            }
                            break;

                        case "visualizarEmpresa":
                            switch (mensagem.getInt("status")) {
                                case 201:
                                    this.txtRazaoSocial.setText(mensagem.getString("razaoSocial"));
                                    this.txtCNPJ.setText(mensagem.getString("cnpj"));
                                    this.txtDescricao.setText(mensagem.getString("descricao"));
                                    this.txtRamo.setText(mensagem.getString("ramo"));
                                    this.txtEmail.setText(AuthenticationModel.getInstance().getEmail());
                                    this.txtPassword.setText(mensagem.getString("senha"));
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

        if (atualizacao) {
            jLabel2.setText("Atualização de cadastro");
            btnCadastrar.setText("Atualizar cadastro");
            txtEmail.setEnabled(false);

            JSONObject message = new JSONObject();
            message.put("operacao", "visualizarEmpresa");
            message.put("email", AuthenticationModel.getInstance().getEmail());
            message.put("token", AuthenticationModel.getInstance().getToken());
            System.out.println("Cliente enviou: " + message);
            SocketModel.getInstance().getOut().println(message);
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

        btnCadastrar = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        txtPassword = new javax.swing.JPasswordField();
        txtEmail = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtRazaoSocial = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtCNPJ = new javax.swing.JTextField();
        txtRamo = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtDescricao = new javax.swing.JTextArea();
        btnVoltar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("CADASTRAR EMPRESA");
        setLocation(new java.awt.Point(200, 200));

        btnCadastrar.setText("Cadastrar");
        btnCadastrar.addActionListener(this::btnCadastrarActionPerformed);

        jLabel2.setText("Cadastro de Empresa");

        jLabel4.setText("Razão social");

        jLabel1.setText("Senha");

        jLabel5.setText("Email");

        jLabel6.setText("CNPJ");

        jLabel7.setText("Ramo");

        jLabel8.setText("Descrição");

        txtDescricao.setColumns(20);
        txtDescricao.setRows(5);
        jScrollPane1.setViewportView(txtDescricao);

        btnVoltar.setText("Voltar");
        btnVoltar.addActionListener(this::btnVoltarActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel1)
                        .addComponent(jLabel2)
                        .addComponent(jLabel5)
                        .addComponent(jLabel4)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(txtPassword, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 253, Short.MAX_VALUE)
                            .addComponent(txtEmail, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtRazaoSocial, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtCNPJ, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtRamo, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING))
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnVoltar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnCadastrar)))
                .addContainerGap(29, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(jLabel2)
                .addGap(24, 24, 24)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtRazaoSocial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtCNPJ, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtRamo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCadastrar)
                    .addComponent(btnVoltar))
                .addContainerGap(30, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnCadastrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCadastrarActionPerformed
        String razaoSocial = txtRazaoSocial.getText();
        String email = txtEmail.getText();
        String cnpj = txtCNPJ.getText();
        String ramo = txtRamo.getText();
        String descricao = txtDescricao.getText();
        String senha = new String(txtPassword.getPassword());

        if (razaoSocial.length() < 6 || razaoSocial.length() > 30) {
            JOptionPane.showMessageDialog(null, "Razão social deve conter entre 6 e 30 caracteres!", "Erro", JOptionPane.ERROR_MESSAGE);
        } else if (email.length() < 7 || email.length() > 50) {
            JOptionPane.showMessageDialog(null, "Email deve conter entre 7 e 50 caracteres!", "Erro", JOptionPane.ERROR_MESSAGE);
        } else if (!email.matches("^([_a-zA-Z0-9-]+(\\.[_a-zA-Z0-9-]+)*@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*(\\.[a-zA-Z]{1,6}))?$")) {
            JOptionPane.showMessageDialog(null, "Email inválido!", "Erro", JOptionPane.ERROR_MESSAGE);
        } else if (cnpj.length() != 14) {
            JOptionPane.showMessageDialog(null, "CNPJ deve conter 14 caracteres!", "Erro", JOptionPane.ERROR_MESSAGE);
        } else if (!cnpj.matches("\\d+")) {
            JOptionPane.showMessageDialog(null, "CNPJ deve conter apenas números!", "Erro", JOptionPane.ERROR_MESSAGE);
        } else if (senha.length() < 3 || senha.length() > 8) {
            JOptionPane.showMessageDialog(null, "Senha deve conter entre 3 e 8 caracteres!", "Erro", JOptionPane.ERROR_MESSAGE);
        } else if (!senha.matches("\\d+")) {
            JOptionPane.showMessageDialog(null, "Senha deve conter apenas números!", "Erro", JOptionPane.ERROR_MESSAGE);
        } else {
            JSONObject message = new JSONObject();
            message.put("operacao", "cadastrarEmpresa");
            if (this.atualizacao) {
                message.put("operacao", "atualizarEmpresa");
                message.put("token", AuthenticationModel.getInstance().getToken());
            }
            message.put("razaoSocial", razaoSocial);
            message.put("email", email);
            message.put("cnpj", cnpj);
            message.put("ramo", ramo);
            message.put("descricao", descricao);
            message.put("senha", senha);
            System.out.println("Cliente enviou: " + message);
            SocketModel.getInstance().getOut().println(message);
        }
    }//GEN-LAST:event_btnCadastrarActionPerformed

    private void btnVoltarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVoltarActionPerformed
        this.dispose();
        if (!this.atualizacao) {
            new Login();
        } else {
            new Inicio();
        }
    }//GEN-LAST:event_btnVoltarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCadastrar;
    private javax.swing.JButton btnVoltar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField txtCNPJ;
    private javax.swing.JTextArea txtDescricao;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtRamo;
    private javax.swing.JTextField txtRazaoSocial;
    // End of variables declaration//GEN-END:variables
}
