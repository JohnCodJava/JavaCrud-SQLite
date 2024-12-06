import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class JavaCrud {
    private JPanel Main;
    private JTextField txtName;
    private JTextField txtPrice;
    private JTextField txtqty;
    private JButton saveButton;
    private JButton deleteButton;
    private JButton updateButton;
    private JTextField txtpid;
    private JButton searchButton;

    private Connection connection;

    public static void main(String[] args) {
        JFrame frame = new JFrame("JavaCrud");
        frame.setContentPane(new JavaCrud().Main);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public JavaCrud() {
        connectToDatabase();
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveData();
            }
        });
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchData();
            }
        });
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteData();
            }
        });
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateData();
            }
        });
    }

    private void connectToDatabase() {
        String url = "jdbc:sqlite:resources/database.db";
        try {
            connection = DriverManager.getConnection(url);
            System.out.println("Connection successful!");
            createTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS products (" +
                "pid INTEGER PRIMARY KEY AUTOINCREMENT," +
                "pname VARCHAR(255) NOT NULL," +
                "price DOUBLE NOT NULL," +
                "qty INTEGER NOT NULL)";

        try (Statement statement = connection.createStatement()) {
            statement.execute(createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveData() {
        String name = txtName.getText();
        String price = txtPrice.getText();
        String qty = txtqty.getText();

        String insertSQL = "INSERT INTO products (pname, price, qty) VALUES (?, ?, ?)";

        try (PreparedStatement pst = connection.prepareStatement(insertSQL)) {
            pst.setString(1, name);
            pst.setDouble(2, Double.parseDouble(price));
            pst.setInt(3, Integer.parseInt(qty));
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Data Saved!");

            txtName.setText("");
            txtPrice.setText("");
            txtqty.setText("");
            txtqty.requestFocus();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void searchData() {
        try {
            String pid = txtpid.getText();
            String selectSQL = "SELECT pname, price, qty FROM products WHERE pid = ?";
            PreparedStatement pst = connection.prepareStatement(selectSQL);
            pst.setString(1, pid);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String name = rs.getString("pname");
                String price = rs.getString("price");
                String qty = rs.getString("qty");

                txtName.setText(name);
                txtPrice.setText(price);
                txtqty.setText(qty);
            } else {
                txtName.setText("");
                txtPrice.setText("");
                txtqty.setText("");
                JOptionPane.showMessageDialog(null, "Product not found!");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void deleteData() {
        try {
            String pid = txtpid.getText();
            String deleteSQL = "DELETE FROM products WHERE pid = ?";
            PreparedStatement pst = connection.prepareStatement(deleteSQL);
            pst.setString(1, pid);
            int rowsAffected = pst.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Data Deleted!");
                txtName.setText("");
                txtPrice.setText("");
                txtqty.setText("");
                txtpid.setText("");
            } else {
                JOptionPane.showMessageDialog(null, "Product not found!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateData() {
        try {
            String pid = txtpid.getText();
            String name = txtName.getText();
            String price = txtPrice.getText();
            String qty = txtqty.getText();

            String updateSQL = "UPDATE products SET pname = ?, price = ?, qty = ? WHERE pid = ?";
            PreparedStatement pst = connection.prepareStatement(updateSQL);
            pst.setString(1, name);
            pst.setDouble(2, Double.parseDouble(price));
            pst.setInt(3, Integer.parseInt(qty));
            pst.setString(4, pid);
            int rowsAffected = pst.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Data Updated!");
            } else {
                JOptionPane.showMessageDialog(null, "Product not found!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
