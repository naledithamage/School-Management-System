import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentManagementSystem extends JFrame {
    private JTextField idField, nameField, phoneField;
    private JButton addButton, viewButton, searchButton, deleteButton;

    // Database credentials
    private static final String DB_URL = "jdbc:mysql://localhost:3306/student_db";
    private static final String USER = "root";
    private static final String PASS = "password";

    // List to store students
    private List<Student> students = new ArrayList<>();

    public StudentManagementSystem() {
        // Set up the frame
        setTitle("Student Management System");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());

        // Create and add components
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // ID label and text field
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("ID:"), gbc);
        idField = new JTextField(20);
        gbc.gridx = 1;
        add(idField, gbc);

        // Name label and text field
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Name:"), gbc);
        nameField = new JTextField(20);
        gbc.gridx = 1;
        add(nameField, gbc);

        // Phone label and text field
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Phone:"), gbc);
        phoneField = new JTextField(20);
        gbc.gridx = 1;
        add(phoneField, gbc);

        // Buttons
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        addButton = new JButton("Add");
        viewButton = new JButton("View");
        searchButton = new JButton("Search");
        deleteButton = new JButton("Delete");

        buttonPanel.add(addButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(deleteButton);

        add(buttonPanel, gbc);

        // Add button action listener
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addStudent();
            }
        });

        // View button action listener
        viewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewStudents();
            }
        });

        // Search button action listener
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchStudent();
            }
        });

        // Delete button action listener
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteStudent();
            }
        });

        setVisible(true);
    }

    private void addStudent() {
        String id = idField.getText();
        String name = nameField.getText();
        String phone = phoneField.getText();

        // Validate input
        if (!validateID(id)) {
            showError("Invalid ID format. Please enter a valid ID.");
            return;
        }

        if (!validatePhone(phone)) {
            showError("Invalid phone number format. Please enter a valid phone number.");
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            String sql = "INSERT INTO students (id, name, phone) VALUES (?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            pstmt.setString(2, name);
            pstmt.setString(3, phone);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Student added successfully!");

        } catch (SQLException se) {
            se.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding student: " + se.getMessage());
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    private void viewStudents() {
        students.clear();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            String sql = "SELECT * FROM students";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                String phone = rs.getString("phone");
                students.add(new Student(id, name, phone));
            }

            displayStudents();

        } catch (SQLException se) {
            se.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching students: " + se.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    private void searchStudent() {
        String id = idField.getText();

        if (!validateID(id)) {
            showError("Invalid ID format. Please enter a valid ID.");
            return;
        }

        students.clear();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            String sql = "SELECT * FROM students WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                String name = rs.getString("name");
                String phone = rs.getString("phone");
                students.add(new Student(id, name, phone));
                displayStudents();
            } else {
                JOptionPane.showMessageDialog(this, "No student found with ID: " + id);
            }

        } catch (SQLException se) {
            se.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error searching student: " + se.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    private void deleteStudent() {
        String id = idField.getText();

        if (!validateID(id)) {
            showError("Invalid ID format. Please enter a valid ID.");
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            String sql = "DELETE FROM students WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Student deleted successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "No student found with ID: " + id);
            }

        } catch (SQLException se) {
            se.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting student: " + se.getMessage());
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    private boolean validateID(String id) {
        return id.matches("\\d+");
    }

    private boolean validatePhone(String phone) {
        return phone.matches("\\d{10}");
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Invalid Input", JOptionPane.ERROR_MESSAGE);
    }

    private void displayStudents() {
        StringBuilder sb = new StringBuilder();
        for (Student student : students) {
            sb.append("ID: ").append(student.getId()).append(", Name: ").append(student.getName()).append(", Phone: ").append(student.getPhone()).append("\n");
        }
        JOptionPane.showMessageDialog(this, sb.toString(), "Student List", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        new StudentManagementSystem();
    }

}