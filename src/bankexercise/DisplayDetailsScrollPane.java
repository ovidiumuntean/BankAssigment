package bankexercise;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.HashMap;
import java.util.Map;

public class DisplayDetailsScrollPane extends JFrame{

    private JFrame jFrame;
    private JPanel jPanel;
    private JTable jTable;
    private JScrollPane scrollPane;
    private String col[];
    private HashMap<Integer, BankAccount> table = new HashMap<Integer, BankAccount>();

    public DisplayDetailsScrollPane(String title, HashMap accounts){
        this.table = accounts;
        this.jFrame = new JFrame(title);
        this.col = new String[]{"ID","Number","Name", "Account Type", "Balance", "Overdraft"};
        DefaultTableModel tableModel = new DefaultTableModel(col, 0);
        this.jTable = new JTable(tableModel);
        this.scrollPane = new JScrollPane(jTable);
        jTable.setAutoCreateRowSorter(true);

        for (Map.Entry<Integer, BankAccount> entry : table.entrySet()) {


            Object[] objs = {entry.getValue().getAccountID(), entry.getValue().getAccountNumber(),
                    entry.getValue().getFirstName().trim() + " " + entry.getValue().getSurname().trim(),
                    entry.getValue().getAccountType(), entry.getValue().getBalance(),
                    entry.getValue().getOverdraft()};

            tableModel.addRow(objs);
        }
        jFrame.setSize(600,500);
        jFrame.add(scrollPane);
        //frame.pack();
        jFrame.setVisible(true);
    }


}
