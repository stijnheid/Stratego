package tools.search;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.TableColumn;

/**
 *
 * @author s122041
 */
public class table extends JFrame{
    
    JTable table;
    
    public table() {
        setLayout(new FlowLayout());
        
        String[] columnNames = {"1", "2", "3", "4", "5", "6"};
        
        String[] rowNames = {"A", "B", "C", "D", "E", "F"};
        
        Object[][] data = {
            {"", "", "", "", "", ""},
            {"", "", "", "", "", ""},
            {"", "", "", "", "", ""},
            {"", "", "", "", "", ""},
            {"", "", "", "", "", ""},
            {"", "", "", "", "", ""},
        };
        
        table = new JTable(data, columnNames);
        table.setPreferredScrollableViewportSize(new Dimension(300, 200));
        table.setFillsViewportHeight(true);
        for (int i = 0; i <=table.getColumnCount() - 1; i++){
            TableColumn column = table.getColumnModel().getColumn(i);
            column.setMaxWidth(5);
        }
        
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane);
    }
    
    public static void main(String args[]) {
        table gui = new table();
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.setSize(500, 300);
        gui.setVisible(true);
        gui.setTitle("JTable");
    }
}
