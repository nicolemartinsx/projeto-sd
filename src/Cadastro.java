
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.json.JSONObject;

public class Cadastro extends javax.swing.JFrame {

    private boolean atualizacao = false;

    public Cadastro(boolean atualizacao) {
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
                    Logger.getLogger(Cadastro.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("Cliente recebeu: " + inputLine);
                JSONObject mensagem = new JSONObject(inputLine);
                switch (mensagem.getString("operacao")) {
                    case "cadastrarCandidato":
                        switch (mensagem.getInt("status")) {
                            case 201:
                                AuthenticationModel model = AuthenticationModel.getInstance();
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

                    case "atualizarCandidato":
                        switch (mensagem.getInt("status")) {
                            case 201:
                                JOptionPane.showMessageDialog(null, "Atualizado com sucesso", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                                this.dispose();
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
            }
        }).start();
        initComponents();

        if (atualizacao) {
            txtEmail.setEnabled(false);
            txtEmail.setText(AuthenticationModel.getInstance().getEmail());
            labelCadastro.setText("Atualização de cadastro");
            btnCadastrar.setText("Atualizar cadastro");
            this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
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
        labelCadastro = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        txtPassword = new javax.swing.JPasswordField();
        txtEmail = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtNome = new javax.swing.JTextField();
        btnVoltar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setLocation(new java.awt.Point(200, 200));

        btnCadastrar.setText("Realizar cadastro");
        btnCadastrar.addActionListener(this::btnCadastrarActionPerformed);

        labelCadastro.setText("Cadastro de candidato");

        jLabel4.setText("Nome");

        jLabel1.setText("Senha");

        jLabel5.setText("Email");

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
                        .addComponent(labelCadastro)
                        .addComponent(jLabel5)
                        .addComponent(jLabel4)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(txtPassword, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 253, Short.MAX_VALUE)
                            .addComponent(txtEmail, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNome, javax.swing.GroupLayout.Alignment.LEADING)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnVoltar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 63, Short.MAX_VALUE)
                        .addComponent(btnCadastrar)))
                .addContainerGap(30, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(labelCadastro)
                .addGap(24, 24, 24)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCadastrar)
                    .addComponent(btnVoltar))
                .addContainerGap(40, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnCadastrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCadastrarActionPerformed
        String nome = txtNome.getText();
        String email = txtEmail.getText();
        String senha = new String(txtPassword.getPassword());

        if (nome.length() < 6 || nome.length() > 30) {
            JOptionPane.showMessageDialog(null, "Nome deve conter entre 6 e 30 caracteres!", "Erro", JOptionPane.ERROR_MESSAGE);
        } else if (email.length() < 7 || email.length() > 50) {
            JOptionPane.showMessageDialog(null, "Email deve conter entre 7 e 50 caracteres!", "Erro", JOptionPane.ERROR_MESSAGE);
        } else if (!email.matches("^([_a-zA-Z0-9-]+(\\.[_a-zA-Z0-9-]+)*@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*(\\.[a-zA-Z]{1,6}))?$")) {
            JOptionPane.showMessageDialog(null, "Email inválido!", "Erro", JOptionPane.ERROR_MESSAGE);
        } else if (senha.length() < 3 || senha.length() > 8) {
            JOptionPane.showMessageDialog(null, "Senha deve conter entre 3 e 8 caracteres!", "Erro", JOptionPane.ERROR_MESSAGE);
        } else if (!senha.matches("\\d+")) {
            JOptionPane.showMessageDialog(null, "Senha deve conter apenas números!", "Erro", JOptionPane.ERROR_MESSAGE);
        } else {
            JSONObject message = new JSONObject();
            message.put("operacao", this.atualizacao ? "atualizarCandidato" : "cadastrarCandidato");
            message.put("nome", nome);
            message.put("email", email);
            message.put("senha", senha);
            System.out.println("Cliente enviou: " + message);
            SocketModel.getInstance().getOut().println(message);
        }
    }//GEN-LAST:event_btnCadastrarActionPerformed

    private void btnVoltarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVoltarActionPerformed
        this.dispose();
        if (!this.atualizacao) {
            new Login();
        }

    }//GEN-LAST:event_btnVoltarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCadastrar;
    private javax.swing.JButton btnVoltar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel labelCadastro;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtNome;
    private javax.swing.JPasswordField txtPassword;
    // End of variables declaration//GEN-END:variables
}
