import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class studentgrade extends JFrame {
    private JTextField nameField, gradeField;
    private JButton addButton, sortButton, deleteButton;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel summaryLabel;

    public studentgrade() {
        setTitle("Student Grade Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null); // Center the window

        getContentPane().setBackground(new Color(220, 245, 250));
        setLayout(new GridBagLayout());

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(220, 245, 250));
        mainPanel.setPreferredSize(new Dimension(400, 450)); // Fixed logical width

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("Student Grade Tracker");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(titleLabel, gbc);

        // Name input
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(new JLabel("Name:"), gbc);

        nameField = new JTextField(12);
        gbc.gridx = 1;
        mainPanel.add(nameField, gbc);

        // Grade input
        gbc.gridx = 0;
        gbc.gridy++;
        mainPanel.add(new JLabel("Grade:"), gbc);

        gradeField = new JTextField(12);
        gbc.gridx = 1;
        mainPanel.add(gradeField, gbc);

        // Add button
        addButton = new JButton("Add");
        addButton.setBackground(new Color(180, 230, 190));
        gbc.gridx = 1;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(addButton, gbc);

        // Sort and Delete buttons
        sortButton = new JButton("Sort by Grade");
        sortButton.setBackground(new Color(255, 230, 200));
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(sortButton, gbc);

        deleteButton = new JButton("Delete Selected");
        deleteButton.setBackground(new Color(255, 180, 180));
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(deleteButton, gbc);

        JButton clearAllButton = new JButton("Clear All");
clearAllButton.setBackground(new Color(200, 200, 255));
gbc.gridx = 0;
gbc.gridy++;
gbc.gridwidth = 2;
gbc.anchor = GridBagConstraints.CENTER;
add(clearAllButton, gbc);
clearAllButton.addActionListener(e -> clearAllRecords());


        // Table
        tableModel = new DefaultTableModel(new String[]{"Name", "Grade"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(350, 150));
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(scrollPane, gbc);

        // Summary
        summaryLabel = new JLabel("Summary: Average=  Highest=  Low= ");
        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(summaryLabel, gbc);

        // Add mainPanel to center of the window
        add(mainPanel, new GridBagConstraints());

        // Action Listeners
        addButton.addActionListener(e -> addStudent());
        deleteButton.addActionListener(e -> deleteSelected());
        sortButton.addActionListener(e -> sortTable());

        setMinimumSize(new Dimension(450, 500)); // Keeps form clean on resize
        setVisible(true);
    }

    private void addStudent() {
        String name = nameField.getText().trim();
        String gradeText = gradeField.getText().trim();
        if (name.isEmpty() || gradeText.isEmpty()) return;

        try {
            double grade = Double.parseDouble(gradeText);
            if (grade < 0 || grade > 100) {
                JOptionPane.showMessageDialog(this, "Grade must be between 0 and 100.");
                return;
            }

            tableModel.addRow(new Object[]{name, grade});
            nameField.setText("");
            gradeField.setText("");
            updateSummary();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric grade.");
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row != -1) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete this entry?",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                tableModel.removeRow(row);
                updateSummary();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a row to delete.");
        }
    }

    private void sortTable() {
        List<Object[]> rows = new ArrayList<Object[]>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            rows.add(new Object[]{tableModel.getValueAt(i, 0), tableModel.getValueAt(i, 1)});
        }

        rows.sort((o1, o2) -> Double.compare(
                Double.parseDouble(o2[1].toString()), Double.parseDouble(o1[1].toString()))
        );

        tableModel.setRowCount(0);
        for (Object[] row : rows) {
            tableModel.addRow(row);
        }
    }

    private void updateSummary() {
        int count = tableModel.getRowCount();
        if (count == 0) {
            summaryLabel.setText("Summary: Average=  Highest=  Low= ");
            return;
        }

        double sum = 0, high = Double.MIN_VALUE, low = Double.MAX_VALUE;

        for (int i = 0; i < count; i++) {
            double grade = Double.parseDouble(tableModel.getValueAt(i, 1).toString());
            sum += grade;
            if (grade > high) high = grade;
            if (grade < low) low = grade;
        }

        double avg = sum / count;
        summaryLabel.setText(String.format("Summary: Average= %.1f  Highest= %.1f  Low= %.1f", avg, high, low));
    }
    private void clearAllRecords() {
    int confirm = JOptionPane.showConfirmDialog(this,
            "This will remove all student records. Are you sure?",
            "Confirm Reset",
            JOptionPane.YES_NO_OPTION);

    if (confirm == JOptionPane.YES_OPTION) {
        tableModel.setRowCount(0); // Remove all rows
        updateSummary();           // Reset summary
        nameField.setText("");     // Clear input fields
        gradeField.setText("");
    }
}


    public static void main(String[] args) {
        SwingUtilities.invokeLater(studentgrade::new);
    }
}
