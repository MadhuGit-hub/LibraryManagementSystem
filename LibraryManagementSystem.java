import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.io.*;

import net.proteanit.sql.DbUtils;

public class LibraryManagementSystem extends JFrame {
    private Connection conn;
    private JTextField bmBookIdField, bmBookNameField, bmSearchField;
    private JTextField umUserIdField, umUserNameField;
    private JTextField tmBookIdField, tmUserIdField;
    private JTable bmDataTable, umDataTable, tmDataTable, rpDataTable;
    private JLabel statusLabel;
    private CardLayout cardLayout;
    private JPanel contentPanel;

    public LibraryManagementSystem() {
        // Database Connection
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_db?useSSL=false&serverTimezone=Asia/Kolkata", "root", "5972");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database connection failed: " + e.getMessage() + "\nCheck MySQL credentials and server.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // Frame Setup
        setTitle("Library Management System");
        setSize(900, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 248, 255)); // Light blue background

        // Left Menu Panel
        JPanel menuPanel = new JPanel();
        menuPanel.setPreferredSize(new Dimension(200, 650));
        menuPanel.setBackground(new Color(70, 130, 180)); // Steel blue
        menuPanel.setLayout(new GridLayout(6, 1, 10, 10));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton homeBtn = new JButton("Home");
        JButton bookManagementBtn = new JButton("Book Management");
        JButton userManagementBtn = new JButton("User Management");
        JButton transactionsBtn = new JButton("Transactions");
        JButton reportsBtn = new JButton("Reports");
        JButton exitBtn = new JButton("Exit");

        styleButton(homeBtn, "Go to Home Page");
        styleButton(bookManagementBtn, "Manage Books");
        styleButton(userManagementBtn, "Manage Users");
        styleButton(transactionsBtn, "Issue/Return Books");
        styleButton(reportsBtn, "View Reports");
        styleButton(exitBtn, "Exit Application");

        menuPanel.add(homeBtn);
        menuPanel.add(bookManagementBtn);
        menuPanel.add(userManagementBtn);
        menuPanel.add(transactionsBtn);
        menuPanel.add(reportsBtn);
        menuPanel.add(exitBtn);
        add(menuPanel, BorderLayout.WEST);

        // Right Content Panel with CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(new Color(240, 248, 255));
        add(contentPanel, BorderLayout.CENTER);

        // Initialize Panels with ScrollPanes
        contentPanel.add(new JScrollPane(createHomePanel()), "Home");
        contentPanel.add(new JScrollPane(createBookManagementPanel()), "BookManagement");
        contentPanel.add(new JScrollPane(createUserManagementPanel()), "UserManagement");
        contentPanel.add(new JScrollPane(createTransactionsPanel()), "Transactions");
        contentPanel.add(new JScrollPane(createReportsPanel()), "Reports");

        // Status Label with System Date and Time
        statusLabel = new JLabel("Status: Ready");
        statusLabel.setForeground(new Color(0, 100, 0)); // Dark green
        updateStatusDateTime();
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBackground(new Color(240, 248, 255));
        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.SOUTH);

        // Menu Button Actions
        homeBtn.addActionListener(e -> {
            cardLayout.show(contentPanel, "Home");
            updateStatusDateTime();
        });
        bookManagementBtn.addActionListener(e -> {
            cardLayout.show(contentPanel, "BookManagement");
            loadBookTableData();
            updateStatusDateTime();
            bmBookIdField.requestFocus();
        });
        userManagementBtn.addActionListener(e -> {
            cardLayout.show(contentPanel, "UserManagement");
            loadUserTableData();
            updateStatusDateTime();
        });
        transactionsBtn.addActionListener(e -> {
            cardLayout.show(contentPanel, "Transactions");
            loadTransactionTableData();
            updateStatusDateTime();
        });
        reportsBtn.addActionListener(e -> {
            cardLayout.show(contentPanel, "Reports");
            showBookAvailability();
            updateStatusDateTime();
        });
        exitBtn.addActionListener(e -> System.exit(0));

        // Load initial home page
        cardLayout.show(contentPanel, "Home");
    }

    private void styleButton(JButton button, String tooltip) {
        button.setBackground(new Color(50, 205, 50)); // Green
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setToolTipText(tooltip);
    }

    private void updateStatusDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        String currentDateTime = sdf.format(new Date());
        try {
            String dateQuery = "SELECT CURDATE() AS current_date";
            PreparedStatement dateStmt = conn.prepareStatement(dateQuery);
            ResultSet rs = dateStmt.executeQuery();
            if (rs.next()) {
                String currentDate = rs.getString("current_date");
                statusLabel.setText("Status: Ready | Date: " + currentDate + " | Time: " + currentDateTime.split(" ")[1] + " IST");
            }
            dateStmt.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText("Status: Error fetching date | Time: " + currentDateTime.split(" ")[1] + " IST");
        }
    }

    private JPanel createHomePanel() {
        // Custom JPanel to draw background image
        JPanel panel = new JPanel(null) {
            private Image backgroundImage;

            // Load the background image
            {
                try {
                    backgroundImage = new ImageIcon("ba4eee915fdd441cb62be5dc85492187.jpg").getImage(); // Replace with your image path
                } catch (Exception e) {
                    e.printStackTrace();
                    backgroundImage = null; // Handle image loading failure
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    // Scale the image to fit the panel
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                } else {
                    // Fallback to original background color if image fails to load
                    g.setColor(new Color(173, 216, 230)); // Light blue
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        panel.setBorder(BorderFactory.createTitledBorder("Library Management System"));

        JLabel welcomeLabel = new JLabel("Welcome to Library Management System", SwingConstants.CENTER);
        welcomeLabel.setBounds(20, 50, 600, 50);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(new Color(0, 102, 204)); // Deep blue
        panel.add(welcomeLabel);

        JLabel subtitleLabel = new JLabel("Manage books, users, and transactions efficiently", SwingConstants.CENTER);
        subtitleLabel.setBounds(20, 100, 600, 30);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLabel.setForeground(Color.BLACK);
        panel.add(subtitleLabel);

        JTextArea featuresText = new JTextArea(
            "Features:\n" +
            "- Add, update, and delete books and users\n" +
            "- Issue and return books with fine calculation\n" +
            "- Track real-time transactions\n" +
            "- View and export borrowing history and book availability\n" +
            "- User-friendly interface with quick navigation"
        );
        featuresText.setBounds(20, 150, 600, 150);
        featuresText.setFont(new Font("Arial", Font.PLAIN, 14));
        featuresText.setBackground(new Color(173, 216, 230, 128)); // Semi-transparent light blue
        featuresText.setEditable(false);
        featuresText.setForeground(Color.BLACK);
        panel.add(featuresText);

        JLabel quickLinksLabel = new JLabel("Quick Links:");
        quickLinksLabel.setBounds(20, 320, 600, 30);
        quickLinksLabel.setFont(new Font("Arial", Font.BOLD, 16));
        quickLinksLabel.setForeground(Color.BLACK);
        panel.add(quickLinksLabel);

        JButton bookLinkBtn = new JButton("Books");
        bookLinkBtn.setBounds(20, 360, 120, 30);
        styleButton(bookLinkBtn, "Go to Book Management");
        bookLinkBtn.addActionListener(e -> cardLayout.show(contentPanel, "BookManagement"));
        panel.add(bookLinkBtn);

        JButton userLinkBtn = new JButton("Users");
        userLinkBtn.setBounds(150, 360, 120, 30);
        styleButton(userLinkBtn, "Go to User Management");
        userLinkBtn.addActionListener(e -> cardLayout.show(contentPanel, "UserManagement"));
        panel.add(userLinkBtn);

        JButton transLinkBtn = new JButton("Transactions");
        transLinkBtn.setBounds(280, 360, 120, 30);
        styleButton(transLinkBtn, "Go to Transactions");
        transLinkBtn.addActionListener(e -> cardLayout.show(contentPanel, "Transactions"));
        panel.add(transLinkBtn);

        JButton reportLinkBtn = new JButton("Reports");
        reportLinkBtn.setBounds(410, 360, 120, 30);
        styleButton(reportLinkBtn, "Go to Reports");
        reportLinkBtn.addActionListener(e -> cardLayout.show(contentPanel, "Reports"));
        panel.add(reportLinkBtn);

        JLabel versionLabel = new JLabel("Version 1.1 | Developed by xAI", SwingConstants.CENTER);
        versionLabel.setBounds(20, 400, 600, 30);
        versionLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        versionLabel.setForeground(Color.DARK_GRAY);
        panel.add(versionLabel);

        return panel;
    }

    private JPanel createBookManagementPanel() {
        JPanel panel = new JPanel(null);
        panel.setBackground(new Color(135, 206, 250)); // Sky blue
        panel.setBorder(BorderFactory.createTitledBorder("Book Management"));

        JLabel bookIdLabel = new JLabel("Book ID:");
        bookIdLabel.setBounds(20, 30, 80, 25);
        bookIdLabel.setForeground(Color.BLACK);
        panel.add(bookIdLabel);

        bmBookIdField = new JTextField();
        bmBookIdField.setBounds(110, 30, 250, 25);
        panel.add(bmBookIdField);

        JLabel bookNameLabel = new JLabel("Book Name:");
        bookNameLabel.setBounds(20, 70, 80, 25);
        bookNameLabel.setForeground(Color.BLACK);
        panel.add(bookNameLabel);

        bmBookNameField = new JTextField();
        bmBookNameField.setBounds(110, 70, 250, 25);
        panel.add(bmBookNameField);

        JButton addBookBtn = new JButton("Add Book");
        addBookBtn.setBounds(20, 110, 110, 30);
        styleButton(addBookBtn, "Add a new book");
        panel.add(addBookBtn);

        JButton updateBookBtn = new JButton("Update Book");
        updateBookBtn.setBounds(140, 110, 110, 30);
        styleButton(updateBookBtn, "Update selected book");
        updateBookBtn.setBackground(new Color(255, 165, 0)); // Orange
        panel.add(updateBookBtn);

        JButton deleteBookBtn = new JButton("Delete Book");
        deleteBookBtn.setBounds(260, 110, 110, 30);
        styleButton(deleteBookBtn, "Delete selected book");
        deleteBookBtn.setBackground(new Color(255, 69, 0)); // Red
        panel.add(deleteBookBtn);

        JButton clearBtn = new JButton("Clear");
        clearBtn.setBounds(380, 110, 110, 30);
        styleButton(clearBtn, "Clear input fields");
        panel.add(clearBtn);

        bmSearchField = new JTextField();
        bmSearchField.setBounds(20, 150, 250, 25);
        panel.add(bmSearchField);

        JButton searchBtn = new JButton("Search");
        searchBtn.setBounds(280, 150, 100, 25);
        styleButton(searchBtn, "Search books by name");
        panel.add(searchBtn);

        JButton showAllBtn = new JButton("Show All");
        showAllBtn.setBounds(390, 150, 100, 25);
        styleButton(showAllBtn, "Show all books");
        panel.add(showAllBtn);

        bmDataTable = new JTable();
        bmDataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(bmDataTable);
        scrollPane.setBounds(20, 190, 600, 350);
        panel.add(scrollPane);

        addBookBtn.addActionListener(e -> {
            addBook();
            loadBookTableData();
            updateStatusDateTime();
            bmBookIdField.requestFocus();
        });
        updateBookBtn.addActionListener(e -> {
            updateBook();
            loadBookTableData();
            updateStatusDateTime();
            bmBookIdField.requestFocus();
        });
        deleteBookBtn.addActionListener(e -> {
            deleteBook();
            loadBookTableData();
            updateStatusDateTime();
            bmBookIdField.requestFocus();
        });
        clearBtn.addActionListener(e -> {
            bmBookIdField.setText("");
            bmBookNameField.setText("");
            bmSearchField.setText("");
            loadBookTableData();
            statusLabel.setText(statusLabel.getText().split("\\| Time:")[0] + " | Fields cleared");
            bmBookIdField.requestFocus();
        });
        searchBtn.addActionListener(e -> {
            searchBooks();
            updateStatusDateTime();
        });
        showAllBtn.addActionListener(e -> {
            bmSearchField.setText("");
            loadBookTableData();
            updateStatusDateTime();
        });

        bmDataTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = bmDataTable.getSelectedRow();
                if (row >= 0) {
                    bmBookIdField.setText(bmDataTable.getValueAt(row, 0).toString());
                    bmBookNameField.setText(bmDataTable.getValueAt(row, 1) != null ? bmDataTable.getValueAt(row, 1).toString() : "");
                    bmBookIdField.requestFocus();
                }
            }
        });

        return panel;
    }

    private JPanel createUserManagementPanel() {
        JPanel panel = new JPanel(null);
        panel.setBackground(new Color(135, 206, 250)); // Sky blue
        panel.setBorder(BorderFactory.createTitledBorder("User Management"));

        JLabel userIdLabel = new JLabel("User ID:");
        userIdLabel.setBounds(20, 30, 80, 25);
        userIdLabel.setForeground(Color.BLACK);
        panel.add(userIdLabel);

        umUserIdField = new JTextField();
        umUserIdField.setBounds(110, 30, 250, 25);
        panel.add(umUserIdField);

        JLabel userNameLabel = new JLabel("User Name:");
        userNameLabel.setBounds(20, 70, 80, 25);
        userNameLabel.setForeground(Color.BLACK);
        panel.add(userNameLabel);

        umUserNameField = new JTextField();
        umUserNameField.setBounds(110, 70, 250, 25);
        panel.add(umUserNameField);

        JButton addUserBtn = new JButton("Add User");
        addUserBtn.setBounds(20, 110, 110, 30);
        styleButton(addUserBtn, "Add a new user");
        panel.add(addUserBtn);

        JButton updateUserBtn = new JButton("Update User");
        updateUserBtn.setBounds(140, 110, 110, 30);
        styleButton(updateUserBtn, "Update selected user");
        updateUserBtn.setBackground(new Color(255, 165, 0)); // Orange
        panel.add(updateUserBtn);

        JButton deleteUserBtn = new JButton("Delete User");
        deleteUserBtn.setBounds(260, 110, 110, 30);
        styleButton(deleteUserBtn, "Delete selected user");
        deleteUserBtn.setBackground(new Color(255, 69, 0)); // Red
        panel.add(deleteUserBtn);

        JButton clearBtn = new JButton("Clear");
        clearBtn.setBounds(380, 110, 110, 30);
        styleButton(clearBtn, "Clear input fields");
        panel.add(clearBtn);

        umDataTable = new JTable();
        umDataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(umDataTable);
        scrollPane.setBounds(20, 150, 600, 390);
        panel.add(scrollPane);

        addUserBtn.addActionListener(e -> {
            addUser();
            loadUserTableData();
            updateStatusDateTime();
        });
        updateUserBtn.addActionListener(e -> {
            updateUser();
            loadUserTableData();
            updateStatusDateTime();
        });
        deleteUserBtn.addActionListener(e -> {
            deleteUser();
            loadUserTableData();
            updateStatusDateTime();
        });
        clearBtn.addActionListener(e -> {
            umUserIdField.setText("");
            umUserNameField.setText("");
            statusLabel.setText(statusLabel.getText().split("\\| Time:")[0] + " | Fields cleared");
        });

        umDataTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = umDataTable.getSelectedRow();
                if (row >= 0) {
                    umUserIdField.setText(umDataTable.getValueAt(row, 0).toString());
                    umUserNameField.setText(umDataTable.getValueAt(row, 1) != null ? umDataTable.getValueAt(row, 1).toString() : "");
                }
            }
        });

        return panel;
    }

    private JPanel createTransactionsPanel() {
        JPanel panel = new JPanel(null);
        panel.setBackground(new Color(144, 238, 144)); // Light green
        panel.setBorder(BorderFactory.createTitledBorder("Transactions"));

        JLabel bookIdLabel = new JLabel("Book ID:");
        bookIdLabel.setBounds(20, 30, 80, 25);
        bookIdLabel.setForeground(Color.BLACK);
        panel.add(bookIdLabel);

        tmBookIdField = new JTextField();
        tmBookIdField.setBounds(110, 30, 250, 25);
        panel.add(tmBookIdField);

        JLabel userIdLabel = new JLabel("User ID:");
        userIdLabel.setBounds(20, 70, 80, 25);
        userIdLabel.setForeground(Color.BLACK);
        panel.add(userIdLabel);

        tmUserIdField = new JTextField();
        tmUserIdField.setBounds(110, 70, 250, 25);
        panel.add(tmUserIdField);

        JButton issueBookBtn = new JButton("Issue Book");
        issueBookBtn.setBounds(20, 110, 110, 30);
        styleButton(issueBookBtn, "Issue book to user");
        issueBookBtn.setBackground(new Color(65, 105, 225)); // Royal blue
        panel.add(issueBookBtn);

        JButton returnBookBtn = new JButton("Return Book");
        returnBookBtn.setBounds(140, 110, 110, 30);
        styleButton(returnBookBtn, "Return book from user");
        returnBookBtn.setBackground(new Color(138, 43, 226)); // Purple
        panel.add(returnBookBtn);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setBounds(260, 110, 110, 30);
        styleButton(refreshBtn, "Refresh transaction table");
        panel.add(refreshBtn);

        tmDataTable = new JTable();
        tmDataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(tmDataTable);
        scrollPane.setBounds(20, 150, 600, 390);
        panel.add(scrollPane);

        issueBookBtn.addActionListener(e -> {
            issueBook();
            loadTransactionTableData();
            updateStatusDateTime();
        });
        returnBookBtn.addActionListener(e -> {
            returnBook();
            loadTransactionTableData();
            updateStatusDateTime();
        });
        refreshBtn.addActionListener(e -> {
            loadTransactionTableData();
            updateStatusDateTime();
        });

        tmDataTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tmDataTable.getSelectedRow();
                if (row >= 0) {
                    tmBookIdField.setText(tmDataTable.getValueAt(row, 0).toString());
                    tmUserIdField.setText(tmDataTable.getValueAt(row, 1).toString());
                }
            }
        });

        return panel;
    }

    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(null);
        panel.setBackground(new Color(245, 245, 220)); // Beige
        panel.setBorder(BorderFactory.createTitledBorder("Reports"));

        JButton borrowingHistoryBtn = new JButton("Borrowing History");
        borrowingHistoryBtn.setBounds(20, 30, 150, 30);
        styleButton(borrowingHistoryBtn, "Show all borrowing history");
        panel.add(borrowingHistoryBtn);

        JButton userHistoryBtn = new JButton("User History");
        userHistoryBtn.setBounds(180, 30, 120, 30);
        styleButton(userHistoryBtn, "Show history for a specific user");
        panel.add(userHistoryBtn);

        JButton bookAvailabilityBtn = new JButton("Book Availability");
        bookAvailabilityBtn.setBounds(310, 30, 150, 30);
        styleButton(bookAvailabilityBtn, "Show book availability status");
        panel.add(bookAvailabilityBtn);

        JButton exportBtn = new JButton("Export CSV");
        exportBtn.setBounds(470, 30, 100, 30);
        styleButton(exportBtn, "Export current report to CSV");
        panel.add(exportBtn);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setBounds(580, 30, 100, 30);
        styleButton(refreshBtn, "Refresh report table");
        panel.add(refreshBtn);

        rpDataTable = new JTable();
        rpDataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(rpDataTable);
        scrollPane.setBounds(20, 70, 600, 470);
        panel.add(scrollPane);

        borrowingHistoryBtn.addActionListener(e -> {
            showBorrowingHistory();
            updateStatusDateTime();
        });
        userHistoryBtn.addActionListener(e -> {
            showUserHistory();
            updateStatusDateTime();
        });
        bookAvailabilityBtn.addActionListener(e -> {
            showBookAvailability();
            updateStatusDateTime();
        });
        exportBtn.addActionListener(e -> exportReportToCSV());
        refreshBtn.addActionListener(e -> {
            showBookAvailability();
            updateStatusDateTime();
        });

        return panel;
    }

    private void loadBookTableData() {
        try {
            String query = "SELECT * FROM books";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            bmDataTable.setModel(DbUtils.resultSetToTableModel(rs));
            stmt.close();
            rs.close();
            statusLabel.setText(statusLabel.getText().split("\\| Time:")[0] + " | Book table loaded");
        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText(statusLabel.getText().split("\\| Time:")[0] + " | Error loading book table: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error loading book table: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadUserTableData() {
        try {
            String query = "SELECT * FROM users";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            umDataTable.setModel(DbUtils.resultSetToTableModel(rs));
            stmt.close();
            rs.close();
            statusLabel.setText(statusLabel.getText().split("\\| Time:")[0] + " | User table loaded");
        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText(statusLabel.getText().split("\\| Time:")[0] + " | Error loading user table: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error loading user table: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadTransactionTableData() {
        try {
            String query = "SELECT i.book_id, i.user_id, b.book_name, u.user_name, i.issue_date, i.return_date, " +
                           "CASE WHEN i.return_date IS NULL THEN 'Issued' ELSE 'Returned' END AS status, " +
                           "CASE WHEN i.return_date IS NULL THEN DATEDIFF(CURDATE(), i.issue_date) ELSE DATEDIFF(i.return_date, i.issue_date) END AS days, " +
                           "CASE WHEN i.return_date IS NULL AND DATEDIFF(CURDATE(), i.issue_date) > 7 THEN (DATEDIFF(CURDATE(), i.issue_date) - 7) * 1 " +
                           "ELSE CASE WHEN DATEDIFF(i.return_date, i.issue_date) > 7 THEN (DATEDIFF(i.return_date, i.issue_date) - 7) * 1 ELSE 0 END END AS fine " +
                           "FROM issued_books i " +
                           "JOIN books b ON i.book_id = b.book_id " +
                           "JOIN users u ON i.user_id = u.user_id " +
                           "ORDER BY i.issue_date DESC";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            tmDataTable.setModel(DbUtils.resultSetToTableModel(rs));
            stmt.close();
            rs.close();
            statusLabel.setText(statusLabel.getText().split("\\| Time:")[0] + " | Transactions loaded");
        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText(statusLabel.getText().split("\\| Time:")[0] + " | Error loading transactions: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error loading transactions: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addBook() {
        String bookId = bmBookIdField.getText().trim();
        String bookName = bmBookNameField.getText().trim();
        if (bookId.isEmpty() || bookName.isEmpty()) {
            statusLabel.setText(statusLabel.getText().split("\\| Time:")[0] + " | Book ID and Name required");
            JOptionPane.showMessageDialog(this, "Please fill Book ID and Name", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (bookId.length() > 20) {
            statusLabel.setText(statusLabel.getText().split("\\| Time:")[0] + " | Book ID too long");
            JOptionPane.showMessageDialog(this, "Book ID must be 20 characters or less", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (bookName.length() > 100) {
            statusLabel.setText(statusLabel.getText().split("\\| Time:")[0] + " | Book Name too long");
            JOptionPane.showMessageDialog(this, "Book Name must be 100 characters or less", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            String query = "INSERT INTO books (book_id, book_name) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, bookId);
            stmt.setString(2, bookName);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                statusLabel.setText(statusLabel.getText().split("\\| Time:")[0] + " | Book added successfully");
                bmBookIdField.setText("");
                bmBookNameField.setText("");
            } else {
                statusLabel.setText(statusLabel.getText().split("\\| Time:")[0] + " | Failed to add book");
                JOptionPane.showMessageDialog(this, "Failed to add book", "Error", JOptionPane.ERROR_MESSAGE);
            }
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            String errorMsg = e.getMessage();
            if (e.getErrorCode() == 1062) {
                errorMsg = "Book ID already exists";
            } else if (e.getErrorCode() == 0) {
                errorMsg = "Database connection issue or table not found";
            }
            statusLabel.setText(statusLabel.getText().split("\\| Time:")[0] + " | Error adding book: " + errorMsg);
            JOptionPane.showMessageDialog(this, "Error adding book: " + errorMsg, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateBook() {
        String bookId = bmBookIdField.getText().trim();
        String bookName = bmBookNameField.getText().trim();
        if (bookId.isEmpty() || bookName.isEmpty()) {
            statusLabel.setText(statusLabel.getText().split("\\| Time:")[0] + " | Book ID and Name required");
            JOptionPane.showMessageDialog(this, "Please fill Book ID and Name", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            String query = "UPDATE books SET book_name = ? WHERE book_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, bookName);
            stmt.setString(2, bookId);
            int rows = stmt.executeUpdate();
            stmt.close();
            if (rows > 0) {
                statusLabel.setText(statusLabel.getText().split("\\| Time:")[0] + " | Book updated successfully");
                bmBookIdField.setText("");
                bmBookNameField.setText("");
            } else {
                statusLabel.setText(statusLabel.getText().split("\\| Time:")[0] + " | Book ID not found");
                JOptionPane.showMessageDialog(this, "Book ID not found", "Error", JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText(statusLabel.getText().split("\\| Time:")[0] + " | Error updating book: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error updating book: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteBook() {
        String bookId = bmBookIdField.getText().trim();
        if (bookId.isEmpty()) {
            statusLabel.setText(statusLabel.getText().split("\\| Time:")[0] + " | Book ID required");
            JOptionPane.showMessageDialog(this, "Please fill Book ID", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            String query = "DELETE FROM books WHERE book_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, bookId);
            int rows = stmt.executeUpdate();
            stmt.close();
            if (rows > 0) {
                statusLabel.setText(statusLabel.getText().split("\\| Time:")[0] + " | Book deleted successfully");
                bmBookIdField.setText("");
                bmBookNameField.setText("");
            } else {
                statusLabel.setText(statusLabel.getText().split("\\| Time:")[0] + " | Book ID not found");
                JOptionPane.showMessageDialog(this, "Book ID not found", "Error", JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText(statusLabel.getText().split("\\| Time:")[0] + " | Error deleting book: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error deleting book: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addUser() {
        String userId = umUserIdField.getText().trim();
        String userName = umUserNameField.getText().trim();
        if (userId.isEmpty() || userName.isEmpty()) {
            statusLabel.setText(statusLabel.getText().split("\\| Time:")[0] + " | User ID and Name required");
            JOptionPane.showMessageDialog(this, "Please fill User ID and Name", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            String query = "INSERT INTO users (user_id, user_name) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, userId);
            stmt.setString(2, userName);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                statusLabel.setText(statusLabel.getText().split("\\| Time:")[0] + " | User added successfully");
                umUserIdField.setText("");
                umUserNameField.setText("");
            } else {
                statusLabel.setText(statusLabel.getText().split("\\| Time:")[0] + " | Failed to add user");
                JOptionPane.showMessageDialog(this, "Failed to add user", "Error", JOptionPane.ERROR_MESSAGE);
            }
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            String errorMsg = e.getMessage();
            if (e.getErrorCode() == 1062) {
                errorMsg = "User ID already exists";
            }
            statusLabel.setText(statusLabel.getText().split("\\| Time:")[0] + " | Error adding user: " + errorMsg);
            JOptionPane.showMessageDialog(this, "Error adding user: " + errorMsg, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateUser() {
        String userId = umUserIdField.getText().trim();
        String userName = umUserNameField.getText().trim();
        if (userId.isEmpty() || userName.isEmpty()) {
            statusLabel.setText(statusLabel.getText().split("\\| Time:")[0] + " | User ID and Name required");
            JOptionPane.showMessageDialog(this, "Please fill User ID and Name", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            String query = "UPDATE users SET user_name = ? WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, userName);
            stmt.setString(2, userId);
            int rows = stmt.executeUpdate();
            stmt.close();
            if (rows > 0) {
                statusLabel.setText(statusLabel.getText().split("\\| Time:")[0] + " | User updated successfully");
                umUserIdField.setText("");
                umUserNameField.setText("");
            } else {
                statusLabel.setText(statusLabel.getText().split("\\| Time:")[0] + " | User ID not found");
                JOptionPane.showMessageDialog(this, "User ID not found", "Error", JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText(statusLabel.getText().split("\\| Time:")[0] + " | Error updating user: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error updating user: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteUser() {
        String userId = umUserIdField.getText().trim();
        if (userId.isEmpty()) {
            statusLabel.setText(statusLabel.getText().split("\\| Time:")[0] + " | User ID required");
            JOptionPane.showMessageDialog(this, "Please fill User ID", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            String checkQuery = "SELECT * FROM issued_books WHERE user_id = ? AND return_date IS NULL";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, userId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                statusLabel.setText(statusLabel.getText().split("\\| Time:")[0] + " | Cannot delete user with active loans");
                JOptionPane.showMessageDialog(this, "Cannot delete user with active loans", "Error", JOptionPane.WARNING_MESSAGE);
                checkStmt.close();
                rs.close();
                return;
            }
            checkStmt.close();
            rs.close();

            String query = "DELETE FROM users WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, userId);
            int rows = stmt.executeUpdate();
            stmt.close();
            if (rows > 0) {
                statusLabel.setText(statusLabel.getText().split("\\| Time:")[0] + " | User deleted successfully");
                umUserIdField.setText("");
                umUserNameField.setText("");
            } else {
                statusLabel.setText(statusLabel.getText().split("\\| Time:")[0] + " | User ID not found");
                JOptionPane.showMessageDialog(this, "User ID not found", "Error", JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText(statusLabel.getText().split("\\| Time:")[0] + " | Error deleting user: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error deleting user: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void issueBook() {
        String bookId = tmBookIdField.getText().trim();
        String userId = tmUserIdField.getText().trim();
        if (bookId.isEmpty() || userId.isEmpty()) {
            statusLabel.setText(statusLabel.getText().split("\\| Time:")[0] + " | Book ID and User ID required");
            JOptionPane.showMessageDialog(this, "Please fill Book ID and User ID", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            String checkBookQuery = "SELECT * FROM books WHERE book_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkBookQuery);
            checkStmt.setString(1, bookId);
            ResultSet rs = checkStmt.executeQuery();
            if (!rs.next()) {
                statusLabel.setText(statusLabel.getText().split("\\| Time:")[0] + " | Book ID not found");
                JOptionPane.showMessageDialog(this, "Book ID not found", "Error", JOptionPane.WARNING_MESSAGE);
                checkStmt.close();
                rs.close();
                return;
            }

            String checkUserQuery = "SELECT * FROM users WHERE user_id = ?";
            checkStmt = conn.prepareStatement(checkUserQuery);
            checkStmt.setString(1, userId);
            rs = checkStmt.executeQuery();
            if (!rs.next()) {
                statusLabel.setText(statusLabel.getText().split("\\| Time:")[0] + " | User ID not found");
                JOptionPane.showMessageDialog(this, "User ID not found. Please add user first.", "Error", JOptionPane.WARNING_MESSAGE);
                checkStmt.close();
                rs.close();
                return;
            }

            String checkIssueQuery = "SELECT * FROM issued_books WHERE book_id = ? AND return_date IS NULL";
            checkStmt = conn.prepareStatement(checkIssueQuery);
            checkStmt.setString(1, bookId);
            rs = checkStmt.executeQuery();
            if (rs.next()) {
                statusLabel.setText(statusLabel.getText().split("\\| Time:")[0] + " | Book already issued");
                JOptionPane.showMessageDialog(this, "Book is already issued", "Error", JOptionPane.WARNING_MESSAGE);
                checkStmt.close();
                rs.close();
                return;
            }
            checkStmt.close();
            rs.close();

            String query = "INSERT INTO issued_books (book_id, user_id, issue_date) VALUES (?, ?, CURDATE())";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, bookId);
            stmt.setString(2, userId);
            stmt.executeUpdate();
            stmt.close();
            statusLabel.setText(statusLabel.getText().split("\\| Time:")[0] + " | Book issued successfully");
            tmBookIdField.setText("");
            tmUserIdField.setText("");
        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText(statusLabel.getText().split("\\| Time:")[0] + " | Error issuing book: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error issuing book: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void returnBook() {
        String bookId = tmBookIdField.getText().trim();
        String userId = tmUserIdField.getText().trim();
        if (bookId.isEmpty() || userId.isEmpty()) {
            statusLabel.setText(statusLabel.getText().split("\\| Time:")[0] + " | Book ID and User ID required");
            JOptionPane.showMessageDialog(this, "Please fill Book ID and User ID", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            String query = "UPDATE issued_books SET return_date = CURDATE() WHERE book_id = ? AND user_id = ? AND return_date IS NULL";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, bookId);
            stmt.setString(2, userId);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                String fineQuery = "SELECT DATEDIFF(CURDATE(), issue_date) AS days FROM issued_books WHERE book_id = ? AND user_id = ? AND return_date = CURDATE()";
                PreparedStatement fineStmt = conn.prepareStatement(fineQuery);
                fineStmt.setString(1, bookId);
                fineStmt.setString(2, userId);
                ResultSet rs = fineStmt.executeQuery();
                if (rs.next()) {
                    int days = rs.getInt("days");
                    int fine = days > 7 ? (days - 7) * 1 : 0;
                    statusLabel.setText(statusLabel.getText().split("\\| Time:")[0] + " | Book returned. Fine: $" + fine);
                    JOptionPane.showMessageDialog(this, "Book returned successfully. Fine: $" + fine, "Success", JOptionPane.INFORMATION_MESSAGE);
                }
                fineStmt.close();
                rs.close();
                tmBookIdField.setText("");
                tmUserIdField.setText("");
            } else {
                statusLabel.setText(statusLabel.getText().split("\\| Time:")[0] + " | No such book issued to this user");
                JOptionPane.showMessageDialog(this, "No such book issued to this user", "Error", JOptionPane.WARNING_MESSAGE);
            }
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText(statusLabel.getText().split("\\| Time:")[0] + " | Error returning book: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error returning book: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchBooks() {
        try {
            String query = "SELECT * FROM books WHERE book_name LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, "%" + bmSearchField.getText().trim() + "%");
            ResultSet rs = stmt.executeQuery();
            bmDataTable.setModel(DbUtils.resultSetToTableModel(rs));
            stmt.close();
            rs.close();
            statusLabel.setText(statusLabel.getText().split("\\| Time:")[0] + " | Search completed");
        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText(statusLabel.getText().split("\\| Time:")[0] + " | Error searching books: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error searching books: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showBorrowingHistory() {
        try {
            String query = "SELECT i.issue_id, b.book_id, b.book_name, u.user_id, u.user_name, i.issue_date, i.return_date, " +
                          "CASE WHEN i.return_date IS NULL THEN DATEDIFF(CURDATE(), i.issue_date) ELSE DATEDIFF(i.return_date, i.issue_date) END AS days, " +
                          "CASE WHEN i.return_date IS NULL AND DATEDIFF(CURDATE(), i.issue_date) > 7 THEN (DATEDIFF(CURDATE(), i.issue_date) - 7) * 1 " +
                          "ELSE CASE WHEN DATEDIFF(i.return_date, i.issue_date) > 7 THEN (DATEDIFF(i.return_date, i.issue_date) - 7) * 1 ELSE 0 END END AS fine " +
                          "FROM issued_books i " +
                          "JOIN books b ON i.book_id = b.book_id " +
                          "JOIN users u ON i.user_id = u.user_id " +
                          "ORDER BY i.issue_date DESC";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            rpDataTable.setModel(DbUtils.resultSetToTableModel(rs));
            stmt.close();
            rs.close();
            statusLabel.setText(statusLabel.getText().split("\\| Time:")[0] + " | Borrowing history loaded");
        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText(statusLabel.getText().split("\\| Time:")[0] + " | Error loading borrowing history: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error loading borrowing history: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showUserHistory() {
        String userId = JOptionPane.showInputDialog(this, "Enter User ID for history:");
        if (userId == null || userId.trim().isEmpty()) {
            statusLabel.setText(statusLabel.getText().split("\\| Time:")[0] + " | User history cancelled");
            return;
        }
        userId = userId.trim();
        try {
            String query = "SELECT i.issue_id, b.book_id, b.book_name, u.user_id, u.user_name, i.issue_date, i.return_date, " +
                          "CASE WHEN i.return_date IS NULL THEN DATEDIFF(CURDATE(), i.issue_date) ELSE DATEDIFF(i.return_date, i.issue_date) END AS days, " +
                          "CASE WHEN i.return_date IS NULL AND DATEDIFF(CURDATE(), i.issue_date) > 7 THEN (DATEDIFF(CURDATE(), i.issue_date) - 7) * 1 " +
                          "ELSE CASE WHEN DATEDIFF(i.return_date, i.issue_date) > 7 THEN (DATEDIFF(i.return_date, i.issue_date) - 7) * 1 ELSE 0 END END AS fine " +
                          "FROM issued_books i " +
                          "JOIN books b ON i.book_id = b.book_id " +
                          "JOIN users u ON i.user_id = u.user_id " +
                          "WHERE u.user_id = ? " +
                          "ORDER BY i.issue_date DESC";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            rpDataTable.setModel(DbUtils.resultSetToTableModel(rs));
            stmt.close();
            rs.close();
            statusLabel.setText(statusLabel.getText().split("\\| Time:")[0] + " | User history loaded for " + userId);
        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText(statusLabel.getText().split("\\| Time:")[0] + " | Error loading user history: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error loading user history: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showBookAvailability() {
        try {
            String query = "SELECT b.book_id, b.book_name, " +
                          "CASE WHEN i.book_id IS NULL THEN 'Available' ELSE 'Issued' END AS status " +
                          "FROM books b " +
                          "LEFT JOIN issued_books i ON b.book_id = i.book_id AND i.return_date IS NULL";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            rpDataTable.setModel(DbUtils.resultSetToTableModel(rs));
            stmt.close();
            rs.close();
            statusLabel.setText(statusLabel.getText().split("\\| Time:")[0] + " | Book availability loaded");
        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText(statusLabel.getText().split("\\| Time:")[0] + " | Error loading book availability: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error loading book availability: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportReportToCSV() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Report as CSV");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV Files", "csv"));
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                if (!file.getName().toLowerCase().endsWith(".csv")) {
                    file = new File(file.getAbsolutePath() + ".csv");
                }
                try (FileWriter fw = new FileWriter(file)) {
                    // Write headers
                    for (int i = 0; i < rpDataTable.getColumnCount(); i++) {
                        fw.write(rpDataTable.getColumnName(i));
                        if (i < rpDataTable.getColumnCount() - 1) {
                            fw.write(",");
                        }
                    }
                    fw.write("\n");
                    // Write data
                    for (int i = 0; i < rpDataTable.getRowCount(); i++) {
                        for (int j = 0; j < rpDataTable.getColumnCount(); j++) {
                            Object value = rpDataTable.getValueAt(i, j);
                            fw.write(value != null ? value.toString().replace(",", "") : "");
                            if (j < rpDataTable.getColumnCount() - 1) {
                                fw.write(",");
                            }
                        }
                        fw.write("\n");
                    }
                    statusLabel.setText(statusLabel.getText().split("\\| Time:")[0] + " | Report exported to " + file.getName());
                    JOptionPane.showMessageDialog(this, "Report exported successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText(statusLabel.getText().split("\\| Time:")[0] + " | Error exporting report: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error exporting report: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LibraryManagementSystem().setVisible(true));
    }
}