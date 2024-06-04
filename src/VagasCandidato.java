
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.json.JSONArray;
import org.json.JSONObject;

public class VagasCandidato extends javax.swing.JFrame {

    private JSONArray vagas = null;

    public VagasCandidato() {
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
                    case "filtrarVagas":
                        switch (mensagem.getInt("status")) {
                            case 200:
                                this.vagas = mensagem.getJSONArray("vagas");

                                DefaultTableModel model = (DefaultTableModel) tblVagas.getModel();
                                for (Object vaga : this.vagas) {
                                    model.addRow(new String[]{
                                        String.valueOf(((JSONObject) vaga).getInt("idVaga")),
                                        ((JSONObject) vaga).getString("nomeVaga")}
                                    );
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

        jLabel7 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblVagas = new javax.swing.JTable();
        btnVoltar = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        listaCompetencias = new javax.swing.JList<>();
        btnBuscar = new javax.swing.JButton();
        cmbTipo = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        btnVisualizar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("VAGAS");
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

        getContentPane().add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 150, 370, 190));

        btnVoltar.setText("Voltar");
        btnVoltar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVoltarActionPerformed(evt);
            }
        });
        getContentPane().add(btnVoltar, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 350, -1, -1));

        jLabel8.setText("Competencias (CTRL+Click)");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, -1, -1));

        listaCompetencias.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Python", "C#", "C++", "JS", "PHP", "Swift", "Java", "Go", "SQL", "Ruby", "HTML", "CSS", "NOSQL", "Flutter", "TypeScript", "Perl", "Cobol", "dotNet", "Kotlin", "Dart" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        listaCompetencias.setToolTipText("Selecione");
        listaCompetencias.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        listaCompetencias.setLayoutOrientation(javax.swing.JList.HORIZONTAL_WRAP);
        listaCompetencias.setValueIsAdjusting(true);
        jScrollPane4.setViewportView(listaCompetencias);

        getContentPane().add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 80, 240, 60));

        btnBuscar.setText("Buscar");
        btnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarActionPerformed(evt);
            }
        });
        getContentPane().add(btnBuscar, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 120, 110, -1));

        cmbTipo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "OR", "AND" }));
        getContentPane().add(cmbTipo, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 80, 110, -1));

        jLabel1.setText("Tipo");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 60, -1, -1));

        btnVisualizar.setText("Visualizar");
        btnVisualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVisualizarActionPerformed(evt);
            }
        });
        getContentPane().add(btnVisualizar, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 350, -1, -1));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnVoltarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVoltarActionPerformed
        this.dispose();
        new Inicio();
    }//GEN-LAST:event_btnVoltarActionPerformed

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        DefaultTableModel model = (DefaultTableModel) tblVagas.getModel();
        while (model.getRowCount() > 0) {
            model.removeRow(0);
        }
        JSONObject requisicao = new JSONObject();
        requisicao.put("operacao", "filtrarVagas");
        requisicao.put("token", AuthenticationModel.getInstance().getToken());
        JSONObject filtros = new JSONObject();
        filtros.put("tipo", cmbTipo.getSelectedItem().toString());
        filtros.put("competencias", listaCompetencias.getSelectedValuesList());
        requisicao.put("filtros", filtros);
        System.out.println("Cliente enviou: " + requisicao);
        SocketModel.getInstance().getOut().println(requisicao);
    }//GEN-LAST:event_btnBuscarActionPerformed

    private void btnVisualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVisualizarActionPerformed
        int idVaga = Integer.parseInt(this.tblVagas.getValueAt(this.tblVagas.getSelectedRow(), 0).toString());
        for (int i = 0; i < vagas.length(); i++) {
            JSONObject vaga = vagas.getJSONObject(i);
            if (vaga.getInt("idVaga") == idVaga) {
                JSONArray competencias = vaga.getJSONArray("competencias");
                JOptionPane.showMessageDialog(null,
                        "Nome: " + vaga.getString("nomeVaga") + "\n"
                        + "Faixa salarial: " + vaga.getDouble("faixaSalarial") + "\n"
                        + "Descrição: " + vaga.getString("descricao") + "\n"
                        + "Estado: " + vaga.getString("estado") + "\n"
                        + "Competências: " + competencias.join(", ")
                );
                break;
            }
        }
    }//GEN-LAST:event_btnVisualizarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnVisualizar;
    private javax.swing.JButton btnVoltar;
    private javax.swing.JComboBox<String> cmbTipo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JList<String> listaCompetencias;
    private javax.swing.JTable tblVagas;
    // End of variables declaration//GEN-END:variables
}
