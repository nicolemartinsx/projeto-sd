
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import org.json.JSONObject;

public class VagasEmpresa extends javax.swing.JFrame {

    private boolean atualizacao = false;
    private Integer idVaga = null;

    public VagasEmpresa() {
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

                SwingUtilities.invokeLater(() -> {
                    switch (mensagem.getString("operacao")) {
                        case "listarVagas":
                            switch (mensagem.getInt("status")) {
                                case 201:
                                    DefaultTableModel model = (DefaultTableModel) tblVagas.getModel();
                                    for (Object vagas : mensagem.getJSONArray("vagas")) {
                                        model.addRow(new String[]{
                                            String.valueOf(((JSONObject) vagas).getInt("idVaga")),
                                            ((JSONObject) vagas).getString("nome")}
                                        );
                                    }
                                    break;

                                default:
                                    JOptionPane.showMessageDialog(null, mensagem.getString("mensagem"), "Erro", JOptionPane.ERROR_MESSAGE);
                                    break;

                            }
                            break;

                        case "visualizarVaga":
                            switch (mensagem.getInt("status")) {
                                case 201:
                                    this.atualizacao = true;
                                    this.txtFaixaSalarial.setText(String.valueOf(mensagem.getDouble("faixaSalarial")));
                                    this.txtDescricao.setText(mensagem.getString("descricao"));
                                    this.txtEstado.setText(mensagem.getString("estado"));

                                    int[] indices = new int[mensagem.getJSONArray("competencias").length()];
                                    int counter = 0;
                                    for (int i = 0; i < this.listaCompetencias.getModel().getSize(); i++) {
                                        String competencia = this.listaCompetencias.getModel().getElementAt(i);
                                        for (int j = 0; j < mensagem.getJSONArray("competencias").length(); j++) {
                                            if (competencia.equals(mensagem.getJSONArray("competencias").getString(j))) {
                                                indices[counter] = i;
                                                counter++;
                                            }
                                        }
                                    }
                                    this.listaCompetencias.setSelectedIndices(indices);

                                    this.lblTituloDialog.setText("Atualizar vaga");
                                    this.btnSalvarDialog.setText("Atualizar");
                                    this.dialogo.setVisible(true);
                                    break;

                                default:
                                    JOptionPane.showMessageDialog(null, mensagem.getString("mensagem"), "Erro", JOptionPane.ERROR_MESSAGE);
                                    break;
                            }
                            break;

                        case "cadastrarVaga":
                        case "atualizarVaga":
                        case "apagarVaga":
                            switch (mensagem.getInt("status")) {
                                case 201:
                                    JOptionPane.showMessageDialog(null, mensagem.getString("mensagem"), "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                                    dialogo.setVisible(false);
                                    this.listarVagas();
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
        dialogo.pack();
        dialogo.setLocationRelativeTo(null);

        this.listarVagas();
    }

    private void listarVagas() {
        DefaultTableModel model = (DefaultTableModel) tblVagas.getModel();
        while (model.getRowCount() > 0) {
            model.removeRow(0);
        }
        JSONObject requisicao = new JSONObject();
        requisicao.put("operacao", "listarVagas");
        requisicao.put("email", AuthenticationModel.getInstance().getEmail());
        requisicao.put("token", AuthenticationModel.getInstance().getToken());
        System.out.println("Cliente enviou: " + requisicao);
        SocketModel.getInstance().getOut().println(requisicao);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dialogo = new javax.swing.JDialog();
        lblTituloDialog = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtNomeVaga = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtFaixaSalarial = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtDescricao = new javax.swing.JTextArea();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        listaCompetencias = new javax.swing.JList<>();
        btnCancelarDialog = new javax.swing.JButton();
        btnSalvarDialog = new javax.swing.JButton();
        txtEstado = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblVagas = new javax.swing.JTable();
        btnVoltar = new javax.swing.JButton();
        btnExcluir = new javax.swing.JButton();
        btnAtualizar = new javax.swing.JButton();
        btnAdicionar = new javax.swing.JButton();

        dialogo.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        dialogo.setTitle("CRIAR VAGA");
        dialogo.getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblTituloDialog.setText("Criação de vaga");
        dialogo.getContentPane().add(lblTituloDialog, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, -1, -1));

        jLabel2.setText("Nome");
        dialogo.getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, -1, -1));
        dialogo.getContentPane().add(txtNomeVaga, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 80, 290, -1));

        jLabel3.setText("Competencias (CTRL+Click)");
        dialogo.getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 350, -1, -1));

        txtFaixaSalarial.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        dialogo.getContentPane().add(txtFaixaSalarial, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 140, 290, -1));

        jLabel4.setText("Descrição");
        dialogo.getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 180, -1, -1));

        txtDescricao.setColumns(20);
        txtDescricao.setRows(5);
        jScrollPane1.setViewportView(txtDescricao);

        dialogo.getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 200, 290, 70));

        jLabel5.setText("Faixa salarial");
        dialogo.getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 120, -1, -1));

        jLabel6.setText("Estado");
        dialogo.getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 290, -1, -1));

        listaCompetencias.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Python", "C#", "C++", "JS", "PHP", "Swift", "Java", "Go", "SQL", "Ruby", "HTML", "CSS", "NOSQL", "Flutter", "TypeScript", "Perl", "Cobol", "dotNet", "Kotlin", "Dart" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        listaCompetencias.setToolTipText("Selecione");
        listaCompetencias.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        listaCompetencias.setLayoutOrientation(javax.swing.JList.HORIZONTAL_WRAP);
        listaCompetencias.setValueIsAdjusting(true);
        jScrollPane2.setViewportView(listaCompetencias);

        dialogo.getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 370, 290, 80));

        btnCancelarDialog.setText("Cancelar");
        btnCancelarDialog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarDialogActionPerformed(evt);
            }
        });
        dialogo.getContentPane().add(btnCancelarDialog, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 470, -1, -1));

        btnSalvarDialog.setText("Criar");
        btnSalvarDialog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarDialogActionPerformed(evt);
            }
        });
        dialogo.getContentPane().add(btnSalvarDialog, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 470, -1, -1));
        dialogo.getContentPane().add(txtEstado, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 310, 290, -1));

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("VAGAS");
        setPreferredSize(new java.awt.Dimension(410, 350));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel7.setText("Vagas");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, -1, -1));

        tblVagas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Nome"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblVagas.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN);
        jScrollPane3.setViewportView(tblVagas);
        if (tblVagas.getColumnModel().getColumnCount() > 0) {
            tblVagas.getColumnModel().getColumn(0).setResizable(false);
            tblVagas.getColumnModel().getColumn(0).setPreferredWidth(50);
            tblVagas.getColumnModel().getColumn(1).setResizable(false);
            tblVagas.getColumnModel().getColumn(1).setPreferredWidth(310);
        }

        getContentPane().add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, 370, 190));

        btnVoltar.setText("Voltar");
        btnVoltar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVoltarActionPerformed(evt);
            }
        });
        getContentPane().add(btnVoltar, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 270, -1, -1));

        btnExcluir.setText("Excluir");
        btnExcluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcluirActionPerformed(evt);
            }
        });
        getContentPane().add(btnExcluir, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 270, 70, -1));

        btnAtualizar.setText("Atualizar");
        btnAtualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAtualizarActionPerformed(evt);
            }
        });
        getContentPane().add(btnAtualizar, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 270, -1, -1));

        btnAdicionar.setText("Adicionar");
        btnAdicionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdicionarActionPerformed(evt);
            }
        });
        getContentPane().add(btnAdicionar, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 270, -1, -1));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnVoltarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVoltarActionPerformed
        this.dispose();
        new Inicio();
    }//GEN-LAST:event_btnVoltarActionPerformed

    private void btnAdicionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdicionarActionPerformed
        this.atualizacao = false;
        this.txtNomeVaga.setText(null);
        this.txtFaixaSalarial.setText(null);
        this.txtDescricao.setText(null);
        this.txtEstado.setText(null);
        int[] indices = {};
        this.listaCompetencias.setSelectedIndices(indices);
        this.lblTituloDialog.setText("Adicionar vaga");
        this.btnSalvarDialog.setText("Cadastrar");
        this.dialogo.setVisible(true);
    }//GEN-LAST:event_btnAdicionarActionPerformed

    private void btnAtualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAtualizarActionPerformed
        if (this.tblVagas.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(null, "Selecione uma linha na tabela", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        this.idVaga = Integer.valueOf(this.tblVagas.getValueAt(this.tblVagas.getSelectedRow(), 0).toString());
        this.txtNomeVaga.setText(this.tblVagas.getValueAt(this.tblVagas.getSelectedRow(), 1).toString());
        JSONObject requisicao = new JSONObject();
        requisicao.put("operacao", "visualizarVaga");
        requisicao.put("email", AuthenticationModel.getInstance().getEmail());
        requisicao.put("token", AuthenticationModel.getInstance().getToken());
        requisicao.put("idVaga", this.idVaga);
        System.out.println("Cliente enviou: " + requisicao);
        SocketModel.getInstance().getOut().println(requisicao);
    }//GEN-LAST:event_btnAtualizarActionPerformed

    private void btnExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcluirActionPerformed
        if (this.tblVagas.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(null, "Selecione uma linha na tabela", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int resposta = JOptionPane.showConfirmDialog(null, "Deseja apagar esta vaga?", "Excluir", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (resposta == JOptionPane.YES_OPTION) {
            JSONObject requisicao = new JSONObject();
            requisicao.put("operacao", "apagarVaga");
            requisicao.put("email", AuthenticationModel.getInstance().getEmail());
            requisicao.put("token", AuthenticationModel.getInstance().getToken());
            requisicao.put("idVaga", this.tblVagas.getValueAt(this.tblVagas.getSelectedRow(), 0).toString());
            System.out.println("Cliente enviou: " + requisicao);
            SocketModel.getInstance().getOut().println(requisicao);
        }
    }//GEN-LAST:event_btnExcluirActionPerformed

    private void btnSalvarDialogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalvarDialogActionPerformed
        if (txtNomeVaga.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Preencha o nome da vaga!", "Erro", JOptionPane.ERROR_MESSAGE);
        } else if (txtFaixaSalarial.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Preencha a faixa salarial da vaga!", "Erro", JOptionPane.ERROR_MESSAGE);
        } else if (txtDescricao.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Preencha o descrição da vaga!", "Erro", JOptionPane.ERROR_MESSAGE);
        } else if (txtEstado.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Preencha o estado da vaga!", "Erro", JOptionPane.ERROR_MESSAGE);
        } else if (listaCompetencias.getSelectedValuesList().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Selecione ao menos uma competência!", "Erro", JOptionPane.ERROR_MESSAGE);
        } else {
            JSONObject requisicao = new JSONObject();
            requisicao.put("operacao", "cadastrarVaga");
            if (this.atualizacao) {
                requisicao.put("operacao", "atualizarVaga");
                requisicao.put("idVaga", idVaga);
            }
            requisicao.put("email", AuthenticationModel.getInstance().getEmail());
            requisicao.put("token", AuthenticationModel.getInstance().getToken());
            requisicao.put("nome", txtNomeVaga.getText());
            requisicao.put("faixaSalarial", Double.parseDouble(txtFaixaSalarial.getText().replaceAll(",", ".")));
            requisicao.put("descricao", txtDescricao.getText());
            requisicao.put("estado", txtEstado.getText());
            requisicao.put("competencias", listaCompetencias.getSelectedValuesList());
            System.out.println("Cliente enviou: " + requisicao);
            SocketModel.getInstance().getOut().println(requisicao);
            dialogo.setVisible(false);
        }
    }//GEN-LAST:event_btnSalvarDialogActionPerformed

    private void btnCancelarDialogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarDialogActionPerformed
        this.dialogo.setVisible(false);
    }//GEN-LAST:event_btnCancelarDialogActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdicionar;
    private javax.swing.JButton btnAtualizar;
    private javax.swing.JButton btnCancelarDialog;
    private javax.swing.JButton btnExcluir;
    private javax.swing.JButton btnSalvarDialog;
    private javax.swing.JButton btnVoltar;
    private javax.swing.JDialog dialogo;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblTituloDialog;
    private javax.swing.JList<String> listaCompetencias;
    private javax.swing.JTable tblVagas;
    private javax.swing.JTextArea txtDescricao;
    private javax.swing.JTextField txtEstado;
    private javax.swing.JFormattedTextField txtFaixaSalarial;
    private javax.swing.JTextField txtNomeVaga;
    // End of variables declaration//GEN-END:variables
}
