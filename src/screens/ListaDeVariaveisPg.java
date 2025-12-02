package screens;

import Models.HomePageModel;
import ilcompiler.memoryvariable.MemoryVariable;
import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class ListaDeVariaveisPg extends javax.swing.JFrame {

    private JTable variablesTable;
    private DefaultTableModel tableModel;
    
    // Mapa para atualização rápida sem recriar a tabela (Correção do bug de atualização)
    private final Map<String, Integer> rowMap = new HashMap<>();

    public ListaDeVariaveisPg() {
        initComponents();
        setupVariablesTable();
        setTitle("Monitor de Variáveis");
        this.setResizable(false);
    }

    public ListaDeVariaveisPg(Map<String, Boolean> inputs, Map<String, Boolean> outputs) {
        this();
        updateDataTable(inputs, outputs);
    }

    private void setupVariablesTable() {
        String[] columns = {"ID", "CurrentValue", "Counter", "MaxTimer", "EndTimer"};

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Impede edição direta
            }
        };

        variablesTable = new JTable(tableModel);
        
        // --- VISUAL ORIGINAL RESTAURADO ---
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(tableModel);
        variablesTable.setRowSorter(sorter);

        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
        sorter.setSortKeys(sortKeys);
        
        variablesTable.setDefaultRenderer(Object.class, new TableCellRenderer() {
            private final DefaultTableCellRenderer DEFAULT_RENDERER = new DefaultTableCellRenderer();

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component renderer = DEFAULT_RENDERER.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (column == 1 && value instanceof Boolean) {
                    // Cores originais do seu projeto
                    renderer.setBackground((Boolean) value ? new Color(144, 238, 144) : new Color(255, 99, 71));
                    renderer.setForeground((Boolean) value ? Color.BLACK : Color.WHITE);
                } else {
                    renderer.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
                    renderer.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
                }
                return renderer;
            }
        });

        jScrollPane1.setViewportView(variablesTable);
    }

    public void updateDataTable(Map<String, Boolean> inputs, Map<String, Boolean> outputs) {
        // Atualiza Entradas
        for (Map.Entry<String, Boolean> entry : inputs.entrySet()) {
            upsertRow(entry.getKey(), entry.getValue(), null, null, null);
        }
        // Atualiza Saídas
        for(Map.Entry<String, Boolean> entry : outputs.entrySet()){
            upsertRow(entry.getKey(), entry.getValue(), null, null, null);
        }
        // Atualiza Memórias
        for (Map.Entry<String, MemoryVariable> entry : HomePageModel.getMemoryVariables().entrySet()) {
            String key = entry.getKey();
            MemoryVariable mem = entry.getValue();
            
            if (key.startsWith("T")) {
                upsertRow(key, mem.currentValue, mem.counter, mem.maxTimer, mem.endTimer);
            } else if (key.startsWith("C")) {
                upsertRow(key, null, mem.counter, mem.maxTimer, mem.endTimer);
            } else {
                upsertRow(key, mem.currentValue, null, null, null);
            }
        }
        
        variablesTable.repaint();
    }
    
    // Método para atualizar linha existente em vez de apagar tudo (Evita o piscar e falhas)
    private void upsertRow(String id, Object val, Object count, Object preset, Object dn) {
        if (rowMap.containsKey(id)) {
            int rowIndex = rowMap.get(id);
            if (rowIndex < tableModel.getRowCount()) {
                tableModel.setValueAt(val, rowIndex, 1);
                tableModel.setValueAt(count, rowIndex, 2);
                tableModel.setValueAt(preset, rowIndex, 3);
                tableModel.setValueAt(dn, rowIndex, 4);
            }
        } else {
            tableModel.addRow(new Object[]{id, val, count, preset, dn});
            rowMap.put(id, tableModel.getRowCount() - 1);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        Lista_de_variaveis = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        Lista_de_variaveis.setColumns(20);
        Lista_de_variaveis.setRows(5);
        jScrollPane1.setViewportView(Lista_de_variaveis);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 371, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(25, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 423, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(27, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea Lista_de_variaveis;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}