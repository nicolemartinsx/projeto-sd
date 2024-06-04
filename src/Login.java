
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.json.JSONObject;

public class Login extends javax.swing.JFrame {

    public Login() {
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
                    Logger.getLogger(CadastroCandidato.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("Cliente recebeu: " + inputLine);
                JSONObject mensagem = new JSONObject(inputLine);
                switch (mensagem.getString("operacao")) {
                    case "loginCandidato":
                    case "loginEmpresa":
                        switch (mensagem.getInt("status")) {
                            case 200:
                                AuthenticationModel model = AuthenticationModel.getInstance();
                                model.setCandidato("loginCandidato".equals(mensagem.getString("operacao")));
                                model.setEmail(txtLogin.getText());
                                model.setToken(mensagem.getString("token"));
                                this.dispose();
                                Inicio inicio = new Inicio();
                                inicio.setVisible(true);
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
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        rbgTipo = new javax.swing.ButtonGroup();
        jLabel4 = new javax.swing.JLabel();
        txtLogin = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        txtPassword = new javax.swing.JPasswordField();
        btnLogin = new javax.swing.JButton();
        btnCadastrar = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        rbCandidato = new javax.swing.JRadioButton();
        rbEmpresa = new javax.swing.JRadioButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("LOGIN");
        setLocation(new java.awt.Point(0, 0));
        setResizable(false);

        jLabel4.setText("Login");

        jLabel1.setText("Senha");

        btnLogin.setText("Login");
        btnLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoginActionPerformed(evt);
            }
        });

        btnCadastrar.setText("Cadastrar");
        btnCadastrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCadastrarActionPerformed(evt);
            }
        });

        jLabel2.setText("Login");

        rbgTipo.add(rbCandidato);
        rbCandidato.setText("Candidato");
        rbCandidato.setActionCommand("loginCandidato");

        rbgTipo.add(rbEmpresa);
        rbEmpresa.setText("Empresa");
        rbEmpresa.setActionCommand("loginEmpresa");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(rbCandidato)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(rbEmpresa))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(btnCadastrar)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnLogin))
                        .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(txtPassword, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(txtLogin, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(35, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbCandidato)
                    .addComponent(rbEmpresa))
                .addGap(26, 26, 26)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtLogin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnLogin)
                    .addComponent(btnCadastrar))
                .addContainerGap(28, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoginActionPerformed
        String login = txtLogin.getText();
        String password = new String(txtPassword.getPassword());

        if (rbgTipo.getSelection() == null) {
            JOptionPane.showMessageDialog(null, "Selecione o tipo!", "Erro", JOptionPane.ERROR_MESSAGE);
        } else if (login.length() < 7 || login.length() > 50) {
            JOptionPane.showMessageDialog(null, "Email deve conter entre 7 e 50 caracteres!", "Erro", JOptionPane.ERROR_MESSAGE);
        } else if (!login.matches("^([_a-zA-Z0-9-]+(\\.[_a-zA-Z0-9-]+)*@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*(\\.[a-zA-Z]{1,6}))?$")) {
            JOptionPane.showMessageDialog(null, "Email inválido!", "Erro", JOptionPane.ERROR_MESSAGE);
        } else if (password.length() < 3 || password.length() > 8) {
            JOptionPane.showMessageDialog(null, "Senha deve conter entre 3 e 8 caracteres!", "Erro", JOptionPane.ERROR_MESSAGE);
        } else if (!password.matches("\\d+")) {
            JOptionPane.showMessageDialog(null, "Senha deve conter apenas números!", "Erro", JOptionPane.ERROR_MESSAGE);
        } else {
            JSONObject message = new JSONObject();
            message.put("operacao", rbgTipo.getSelection().getActionCommand());
            message.put("email", login);
            message.put("senha", password);
            System.out.println("Cliente enviou: " + message);
            SocketModel.getInstance().getOut().println(message);
        }
    }//GEN-LAST:event_btnLoginActionPerformed

    private void btnCadastrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCadastrarActionPerformed
        if (rbgTipo.getSelection() == null) {
            JOptionPane.showMessageDialog(null, "Selecione o tipo!", "Erro", JOptionPane.ERROR_MESSAGE);
        } else {
            this.dispose();
            if ("loginCandidato".equals(rbgTipo.getSelection().getActionCommand())) {
                new CadastroCandidato(false);
            } else {
                new CadastroEmpresa(false);
            }
        }

    }//GEN-LAST:event_btnCadastrarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCadastrar;
    private javax.swing.JButton btnLogin;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JRadioButton rbCandidato;
    private javax.swing.JRadioButton rbEmpresa;
    private javax.swing.ButtonGroup rbgTipo;
    private javax.swing.JTextField txtLogin;
    private javax.swing.JPasswordField txtPassword;
    // End of variables declaration//GEN-END:variables
}
