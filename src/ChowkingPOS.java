/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author eduho
 */
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class ChowkingPOS extends javax.swing.JFrame {

    private java.util.ArrayList<MenuItem> currentMenu = new java.util.ArrayList<>();
    private java.util.ArrayList<CartItem> cart = new java.util.ArrayList<>();
    private javax.swing.table.DefaultTableModel cartTableModel;
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ChowkingPOS.class.getName());
    // LAST TRANSACTION MEMORY nya ---
    private java.util.ArrayList<CartItem> lastTransactionItems = new java.util.ArrayList<>();
    private double lastTotal = 0;
    private double lastCash = 0;
    private double lastChange = 0;

    /**
     * Creates new form ChowkingPOS
     */
    public ChowkingPOS() {
        initComponents();
        customInit();
    }

    // --- 2. CUSTOM INIT ---
    public void customInit() {
        // Setup Cart Table Model
        String[] columns = {"Item", "Qty", "Price", "Total"};
        cartTableModel = new javax.swing.table.DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblCart.setModel(cartTableModel);

        // Setup Table Styling
        tblCart.setRowHeight(40);
        tblCart.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        javax.swing.table.DefaultTableCellRenderer rightRenderer = new javax.swing.table.DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        tblCart.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);
        tblCart.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
        tblCart.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);

        // Double click to edit quantity
        tblCart.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editSelectedItem();
                }
            }
        });

        // Setup Menu Grid Layout (3 Columns)
        pnlMenuGrid.setLayout(new GridLayout(0, 3, 15, 15));

        // Create Category Buttons Programmatically
        createCategoryButtons();

        // Load initial menu
        loadMenuCategory("Rice Meals");
    }

    // --- 3. LOGIC METHODS ---
    private void createCategoryButtons() {
        pnlCategories.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));

        // CHANGED: Specific Chowking Categories
        String[] categories = {"Rice Meals", "Noodles & Dim Sum", "Sides & Add-ons", "Dessert & Beverages"};

        for (String cat : categories) {
            JButton catBtn = new JButton(cat);
            catBtn.setBackground(Color.decode("#ffb81c")); // Uses Chowking Red
            catBtn.setForeground(Color.WHITE);
            catBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
            catBtn.setPreferredSize(new Dimension(150, 50)); // Made slightly wider for long names
            catBtn.addActionListener(e -> loadMenuCategory(cat));
            pnlCategories.add(catBtn);
        }
    }
    
    private void reprintAsciiPopup() {
        // 1. Safety Check
        if (lastTransactionItems.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, "No previous transaction to reprint.");
            return;
        }

        StringBuilder sb = new StringBuilder();

        // 2. GET TIME
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        String dateTimeStr = now.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm:ss"));

        // 3. RE-CALCULATE
        double vatableSales = lastTotal / 1.12;
        double vatAmount = lastTotal - vatableSales;
        int totalItems = 0;
        for (CartItem c : lastTransactionItems) totalItems += c.quantity;

        // 4. BUILD STRING (Using SAVED Data)
        sb.append("\t     CHOWKING POS SYSTEM\n");
        sb.append("\t   San Jose del Monte Branch\n");
        sb.append("\t   " + dateTimeStr + " (REPRINT)\n");
        sb.append("\t   -------------------------\n\n");

        for (int i = 0; i < lastTransactionItems.size(); i++) {
            CartItem c = lastTransactionItems.get(i);
            sb.append(String.format("\t %d. %-15s - %.2f x %d = P%.2f\n",
                    i + 1,
                    truncate(c.item.name, 15),
                    c.item.price,
                    c.quantity,
                    c.item.price * c.quantity));
        }

        sb.append("\t ----------------------------------------\n");
        sb.append(String.format("\t %.0f Item(s)                     %.2f\n", (double) totalItems, lastTotal));
        sb.append(String.format("\t TOTAL DUE                     %.2f\n", lastTotal));
        sb.append(String.format("\t CASH                          %.2f\n", lastCash));
        sb.append(String.format("\t CHANGE DUE                    %.2f\n\n", lastChange));

        sb.append(String.format("\t VATable Sales                 %.2f\n", vatableSales));
        sb.append(String.format("\t VAT Amount                    %.2f\n", vatAmount));
        
        sb.append("\n\t This serves as your OFFICIAL RECEIPT.\n");
        sb.append("\t =====================================\n");
        sb.append("\t          ** REPRINT COPY ** \n");
        sb.append("\t =====================================\n");

        // 5. SHOW POPUP
        javax.swing.JTextArea textArea = new javax.swing.JTextArea(sb.toString());
        textArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));
        textArea.setEditable(false);

        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(textArea);
        scrollPane.setPreferredSize(new java.awt.Dimension(400, 500));

        javax.swing.JOptionPane.showMessageDialog(this, scrollPane, "Reprint Receipt", javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }
    

    private void loadMenuCategory(String category) {
        pnlMenuGrid.removeAll();
        currentMenu.clear();

        // CHANGED: Added image filenames (Make sure these match your actual files!)
        // CHANGED: Complete Chowking Menu from your list
        switch (category) {
            case "Rice Meals":
                // Chao Fan Series
                currentMenu.add(new MenuItem("Pork Chao Fan", 59.00, "/images/Pork-Chao-Fan.png"));
                currentMenu.add(new MenuItem("Beef Chao Fan", 69.00, "/images/beef_chao_fan.png"));
                currentMenu.add(new MenuItem("Siomai Chao Fan", 89.00, "/images/siomai_chao_fan.png"));
                currentMenu.add(new MenuItem("Spicy Chao Fan", 65.00, "/images/Spicy-Chao-Fan.png"));

                // Chicken & Lauriats
                currentMenu.add(new MenuItem("1pc Chicken Rice", 95.00, "/images/chicken_1pc.png"));
                currentMenu.add(new MenuItem("8pc Chicken Rice", 385.00, "/images/chicken_8pc.png"));
                currentMenu.add(new MenuItem("Chicken Lauriat", 198.00, "/images/lauriat_chicken.png"));
                currentMenu.add(new MenuItem("Sweet 'n' Sour Pork Lauriat", 185.00, "/images/lauriat_pork.png"));
                currentMenu.add(new MenuItem("Sweet 'n' Sour Fish Lauriat", 185.00, "/images/lauriat_fish.png"));

                // Chef Specials
                currentMenu.add(new MenuItem("Sweet 'n' Sour Pork Rice", 115.00, "/images/pork_rice.png"));
                currentMenu.add(new MenuItem("Braised Beef Rice", 155.00, "/images/beef_rice.png"));
                currentMenu.add(new MenuItem("Lumpiang Shanghai Rice", 89.00, "/images/shanghai_rice.png"));
                break;

            case "Noodles & Dim Sum":
                // Noodles
                currentMenu.add(new MenuItem("Pancit Canton", 65.00, "/images/pancit.png"));
                currentMenu.add(new MenuItem("Beef Wonton Mami", 145.00, "/images/beef_wonton.png"));
                currentMenu.add(new MenuItem("Wonton Mami", 115.00, "/images/wonton_mami.png"));
                currentMenu.add(new MenuItem("Beef Mami", 125.00, "/images/beef_mami.png"));

                // Siopao
                currentMenu.add(new MenuItem("Chunky Asado Siopao", 43.00, "/images/asado_siopao.png"));
                currentMenu.add(new MenuItem("Bola-Bola Siopao", 53.00, "/images/bola_siopao.png"));
                currentMenu.add(new MenuItem("3pc Siopao Box", 125.00, "/images/siopao_box.png"));

                // Siomai
                currentMenu.add(new MenuItem("Siomai (4pcs)", 45.00, "/images/siomai_4pc.png"));
                currentMenu.add(new MenuItem("Siomai (2pcs)", 25.00, "/images/siomai_2pc.png"));
                break;

            case "Sides & Add-ons":
                currentMenu.add(new MenuItem("Chicharap", 49.00, "/images/chicharap.png"));
                currentMenu.add(new MenuItem("Kangkong w/ Bagoong", 55.00, "/images/kangkong.png"));
                currentMenu.add(new MenuItem("Wonton Soup", 35.00, "/images/wonton_soup.png"));
                currentMenu.add(new MenuItem("Lumpiang Shanghai (4pcs)", 55.00, "/images/shanghai_side.png"));
                currentMenu.add(new MenuItem("Buchi (3pcs)", 52.00, "/images/buchi.png"));
                currentMenu.add(new MenuItem("Extra Plain Rice", 25.00, "/images/rice_plain.png"));
                currentMenu.add(new MenuItem("Extra Egg Fried Rice", 45.00, "/images/rice_egg.png"));
                currentMenu.add(new MenuItem("Asian Spicy Sauce", 10.00, "/images/sauce.png"));
                break;

            case "Dessert & Beverages":
                // Halo-Halo
                currentMenu.add(new MenuItem("Halo-Halo Supreme", 119.00, "/images/halohalo.png"));

                // Milksha
                currentMenu.add(new MenuItem("Honey Pearl Black Tea", 95.00, "/images/milksha_honey.png"));
                currentMenu.add(new MenuItem("Black Tea Latte Pudding", 105.00, "/images/milksha_pudding.png"));
                currentMenu.add(new MenuItem("Valrhona Cocoa Milk", 115.00, "/images/milksha_cocoa.png"));

                // Standard Drinks
                currentMenu.add(new MenuItem("Iced Tea", 45.00, "/images/iced_tea.png"));
                currentMenu.add(new MenuItem("Coke", 40.00, "/images/coke.png"));
                currentMenu.add(new MenuItem("Coke Zero", 40.00, "/images/coke_zero.png"));
                currentMenu.add(new MenuItem("Sprite", 40.00, "/images/sprite.png"));
                break;
        }

        // Generate Buttons with Images
        for (MenuItem item : currentMenu) {
            // 1. Create Button with HTML for text formatting
            JButton itemBtn = new JButton("<html><center>" + item.name + "<br>P" + item.price + "</center></html>");

            // 2. Load and Resize Image
            try {
                // Load the image from the project resources
                java.net.URL imgURL = getClass().getResource(item.imageFile);
                if (imgURL != null) {
                    ImageIcon originalIcon = new ImageIcon(imgURL);

                    // RESIZE: Scale image to fit button (e.g., 100x80 pixels)
                    Image img = originalIcon.getImage();
                    Image newImg = img.getScaledInstance(100, 80, java.awt.Image.SCALE_SMOOTH);
                    itemBtn.setIcon(new ImageIcon(newImg));
                } else {
                    System.err.println("Image not found: " + item.imageFile);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 3. Styling to put Image ON TOP of Text
            itemBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
            itemBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

            itemBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
            itemBtn.setBackground(Color.WHITE);
            itemBtn.setPreferredSize(new Dimension(160, 140)); // Made button taller for image

            // 4. Action Listener (Same as before)
            itemBtn.addActionListener(e -> {
                String qtyStr = NumpadDialog.show(this, "Qty for " + item.name);
                if (qtyStr != null && !qtyStr.isEmpty()) {
                    try {
                        int qty = Integer.parseInt(qtyStr);
                        if (qty > 0) {
                            addToCart(item, qty);
                        }
                    } catch (NumberFormatException ex) {
                    }
                }
            });

            pnlMenuGrid.add(itemBtn);
        }

        pnlMenuGrid.revalidate();
        pnlMenuGrid.repaint();
    }

    private void addToCart(MenuItem item, int qty) {
        boolean found = false;
        for (CartItem c : cart) {
            if (c.item.name.equals(item.name)) {
                c.quantity += qty;
                found = true;
                break;
            }
        }
        if (!found) {
            cart.add(new CartItem(item, qty));
        }
        updateCartTable();
    }

    private void updateCartTable() {
        cartTableModel.setRowCount(0);
        double total = 0;
        for (CartItem c : cart) {
            double lineTotal = c.item.price * c.quantity;
            total += lineTotal;
            cartTableModel.addRow(new Object[]{c.item.name, c.quantity, String.format("%.2f", c.item.price), String.format("%.2f", lineTotal)});
        }
        double vat = total * 0.12;
        lblTotal.setText(String.format("P%.2f", total));
        // Ensure you have a lblTax in your GUI design, or comment this out
        if (lblTax != null) {
            lblTax.setText(String.format("P%.2f", vat));
        }
    }

    // Call this from your "Remove" Button
    private void removeSelectedItem() {
        int row = tblCart.getSelectedRow();
        if (row != -1) {
            cart.remove(row);
            updateCartTable();
        }
    }

    // Call this from your "Reset" Button
    private void clearCart() {
        cart.clear();
        updateCartTable();
    }

    // Call this from "Pay" Button
    private void processPayment() {
        if (cart.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, "Cart is empty!");
            return;
        }

        double totalAmount = 0;
        for (CartItem c : cart) {
            totalAmount += (c.item.price * c.quantity);
        }

        String cashStr = NumpadDialog.show(this, "Total: P" + totalAmount + "\nEnter Cash:");
        if (cashStr != null) {
            try {
                double cash = Double.parseDouble(cashStr);
                if (cash >= totalAmount) {
                    double change = cash - totalAmount;

                    // 1. SAVE FOR REPRINT
                    lastTransactionItems.clear();
                    lastTransactionItems.addAll(cart);
                    lastTotal = totalAmount;
                    lastCash = cash;
                    lastChange = change;

                    // 2. SHOW ASCII POPUP (Missing in your code!)
                    showAsciiReceiptPopup(totalAmount, cash, change);

                    // 3. PRINT TO PDF
                    printReceipt(totalAmount, cash, change);

                    // 4. CLEAR
                    clearCart();
                } else {
                    javax.swing.JOptionPane.showMessageDialog(this, "Insufficient Cash!");
                }
            } catch (NumberFormatException e) {
                // Handle invalid number input
            }
        }
    }

    private void editSelectedItem() {
        int row = tblCart.getSelectedRow();
        if (row != -1) {
            CartItem c = cart.get(row);
            String qtyStr = NumpadDialog.show(this, "New Qty (0 to remove):");
            if (qtyStr != null) {
                int q = Integer.parseInt(qtyStr);
                if (q <= 0) {
                    cart.remove(row);
                } else {
                    c.quantity = q;
                }
                updateCartTable();
            }
        }
    }

    // --- 4. RECEIPT PRINTING ---
    private void printReceipt(double total, double cash, double change) {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(new ReceiptPrintable(new ArrayList<>(cart), total, cash, change));
        if (job.printDialog()) {
            try {
                job.print();
            } catch (PrinterException e) {
                e.printStackTrace();
            }
        }
    }

    private void showAsciiReceiptPopup(double total, double cash, double change) {
        StringBuilder sb = new StringBuilder();

        // --- 1. GET DATE & TIME ---
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm:ss");
        String dateTimeStr = now.format(formatter);

        // --- 2. CALCULATE VAT (Philippine Standard) ---
        double vatableSales = total / 1.12;
        double vatAmount = total - vatableSales;
        double vatExempt = 0.00;
        double vatZeroRated = 0.00;
        int totalItems = 0;
        for (CartItem c : cart) {
            totalItems += c.quantity;
        }

        // --- 3. BUILD THE STRING ---
        // Header
        sb.append("\t     CHOWKING POS SYSTEM\n");
        sb.append("\t   San Jose del Monte Branch\n");
        sb.append("\t   " + dateTimeStr + "\n"); // <--- ADDED DATE HERE
        sb.append("\t   -------------------------\n\n");

        // Loop Items
        for (int i = 0; i < cart.size(); i++) {
            CartItem c = cart.get(i);
            sb.append(String.format("\t %d. %-15s - %.2f x %d = P%.2f\n",
                    i + 1,
                    truncate(c.item.name, 15),
                    c.item.price,
                    c.quantity,
                    c.item.price * c.quantity));
        }

        // Footer Totals
        sb.append("\t ----------------------------------------\n");
        sb.append(String.format("\t %.0f Item(s)                     %.2f\n", (double) totalItems, total));
        sb.append(String.format("\t TOTAL DUE                     %.2f\n", total));
        sb.append(String.format("\t CASH                          %.2f\n", cash));
        sb.append(String.format("\t CHANGE DUE                    %.2f\n\n", change));

        // Tax Breakdown
        sb.append(String.format("\t VATable Sales                 %.2f\n", vatableSales));
        sb.append(String.format("\t VAT-Exempt Sales              %.2f\n", vatExempt));
        sb.append(String.format("\t VATable Zero-Rated Sales      %.2f\n", vatZeroRated));
        sb.append(String.format("\t VAT Amount                    %.2f\n", vatAmount));

        // Footer Messages
        sb.append("\n\t This serves as your OFFICIAL RECEIPT.\n");
        sb.append("\t    CHOWKING DELIVERY? CALL #9-88-88\n");
        sb.append("\t    For any feedback, contact us at\n");
        sb.append("\t    Email: feedback@chowking.com.ph\n");
        sb.append("\t     ACCREDITATION NO.: 030-000330515\n\n");
        sb.append("\t =====================================\n");
        sb.append("\t    THANK YOU FOR ORDERING ONLINE!  \n");
        sb.append("\t =====================================\n");

        // --- 4. DISPLAY IN SCROLLABLE POPUP ---
        javax.swing.JTextArea textArea = new javax.swing.JTextArea(sb.toString());
        textArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));
        textArea.setEditable(false);

        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(textArea);
        scrollPane.setPreferredSize(new java.awt.Dimension(400, 500));

        javax.swing.JOptionPane.showMessageDialog(this, scrollPane, "Official Receipt", javax.swing.JOptionPane.PLAIN_MESSAGE);
    }

// Helper to prevent long names from breaking the ASCII layout
    private String truncate(String str, int width) {
        if (str.length() > width) {
            return str.substring(0, width);
        }
        return str;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        headerPanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        pnlCategories = new javax.swing.JPanel();
        pnlMenuGrid = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblCart = new javax.swing.JTable();
        lblTax = new javax.swing.JLabel();
        lblTotal = new javax.swing.JLabel();
        removeBtn = new javax.swing.JButton();
        resetBtn = new javax.swing.JButton();
        payBtn = new javax.swing.JButton();
        reprintBtn = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        headerPanel.setPreferredSize(new java.awt.Dimension(1366, 130));
        headerPanel.setLayout(new java.awt.BorderLayout());

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/header.png"))); // NOI18N
        headerPanel.add(jLabel2, java.awt.BorderLayout.CENTER);

        getContentPane().add(headerPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1366, 50));

        pnlCategories.setBackground(new java.awt.Color(255, 255, 255));
        pnlCategories.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        getContentPane().add(pnlCategories, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 710, 60));

        pnlMenuGrid.setBackground(new java.awt.Color(245, 245, 245));
        pnlMenuGrid.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        getContentPane().add(pnlMenuGrid, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, 710, 640));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setLayout(null);

        tblCart.setBackground(new java.awt.Color(240, 240, 240));
        tblCart.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Item", "Quantity", "Price", "Total"
            }
        ));
        jScrollPane2.setViewportView(tblCart);

        jPanel2.add(jScrollPane2);
        jScrollPane2.setBounds(730, 10, 630, 520);

        lblTax.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblTax.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTax.setText("P0.00");
        jPanel2.add(lblTax);
        lblTax.setBounds(1140, 530, 220, 20);

        lblTotal.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblTotal.setForeground(new java.awt.Color(255, 51, 51));
        lblTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotal.setText("P0.00");
        jPanel2.add(lblTotal);
        lblTotal.setBounds(1140, 550, 220, 32);

        removeBtn.setText("Remove");
        removeBtn.addActionListener(this::removeBtnActionPerformed);
        jPanel2.add(removeBtn);
        removeBtn.setBounds(730, 600, 140, 50);

        resetBtn.setText("Reset");
        resetBtn.addActionListener(this::resetBtnActionPerformed);
        jPanel2.add(resetBtn);
        resetBtn.setBounds(910, 600, 140, 50);

        payBtn.setBackground(new java.awt.Color(0, 102, 0));
        payBtn.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        payBtn.setForeground(new java.awt.Color(255, 255, 255));
        payBtn.setText("PAY / CHECKOUT");
        payBtn.addActionListener(this::payBtnActionPerformed);
        jPanel2.add(payBtn);
        payBtn.setBounds(730, 660, 630, 50);

        reprintBtn.setText("Reprint");
        reprintBtn.addActionListener(this::reprintBtnActionPerformed);
        jPanel2.add(reprintBtn);
        reprintBtn.setBounds(1080, 600, 140, 50);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel1.setText("TOTAL:");
        jPanel2.add(jLabel1);
        jLabel1.setBounds(730, 560, 80, 20);

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel3.setText("VAT (12%):");
        jPanel2.add(jLabel3);
        jLabel3.setBounds(730, 540, 80, 20);

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 50, 1366, 718));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void removeBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeBtnActionPerformed
        removeSelectedItem();
    }//GEN-LAST:event_removeBtnActionPerformed

    private void payBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_payBtnActionPerformed
        processPayment();
    }//GEN-LAST:event_payBtnActionPerformed

    private void resetBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetBtnActionPerformed
        clearCart();
    }//GEN-LAST:event_resetBtnActionPerformed

    private void reprintBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reprintBtnActionPerformed
        reprintAsciiPopup();
    }//GEN-LAST:event_reprintBtnActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new ChowkingPOS().setVisible(true));
    }

    // --- 5. INNER CLASSES ---
    static class MenuItem {

        String name;
        double price;
        String imageFile;

        public MenuItem(String n, double p, String img) {
            this.name = n;
            this.price = p;
            this.imageFile = img;
        }
    }

    static class CartItem {

        MenuItem item;
        int quantity;

        public CartItem(MenuItem i, int q) {
            item = i;
            quantity = q;
        }
    }

    // Defines the PDF/Print Layout
    static class ReceiptPrintable implements Printable {

        List<CartItem> items;
        double total, cash, change;

        public ReceiptPrintable(List<CartItem> items, double t, double c, double ch) {
            this.items = items;
            total = t;
            cash = c;
            change = ch;
        }

        public int print(Graphics g, PageFormat pf, int page) {
            if (page > 0) {
                return NO_SUCH_PAGE;
            }
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            int y = 20;

            // CHANGED: Chowking Header
            g2d.setFont(new Font("Monospaced", Font.BOLD, 12));
            g2d.drawString("CHOWKING POS RECEIPT", 10, y);
            y += 20;
            g2d.drawString("San Jose del Monte Branch", 10, y);
            y += 20; // Example Location
            g2d.drawString("---------------------", 10, y);
            y += 20;

            g2d.setFont(new Font("Monospaced", Font.PLAIN, 10));
            for (CartItem c : items) {
                g2d.drawString(c.quantity + "x " + c.item.name, 10, y);
                // Align price to right (approximate)
                g2d.drawString(String.format("%.2f", c.item.price * c.quantity), 160, y);
                y += 15;
            }
            y += 10;
            g2d.drawString("---------------------", 10, y);
            y += 15;
            g2d.setFont(new Font("Monospaced", Font.BOLD, 12));
            g2d.drawString("TOTAL:  " + String.format("%.2f", total), 10, y);
            y += 15;
            g2d.drawString("CASH:   " + String.format("%.2f", cash), 10, y);
            y += 15;
            g2d.drawString("CHANGE: " + String.format("%.2f", change), 10, y);

            return PAGE_EXISTS;
        }
    }

    // The Touch Numpad (Kept exactly the same)
    static class NumpadDialog extends JDialog {

        private String value = "";
        private JTextField display;
        private boolean confirmed = false;

        public NumpadDialog(Frame owner, String title) {
            super(owner, title, true);
            setSize(300, 400);
            setLocationRelativeTo(owner);
            setLayout(new BorderLayout());
            display = new JTextField();
            display.setFont(new Font("SansSerif", Font.BOLD, 24));
            add(display, BorderLayout.NORTH);
            JPanel p = new JPanel(new GridLayout(4, 3));
            String[] k = {"7", "8", "9", "4", "5", "6", "1", "2", "3", "C", "0", "OK"};
            for (String s : k) {
                JButton b = new JButton(s);
                b.addActionListener(e -> {
                    if (s.equals("C")) {
                        value = "";
                        display.setText("");
                    } else if (s.equals("OK")) {
                        confirmed = true;
                        dispose();
                    } else {
                        value += s;
                        display.setText(value);
                    }
                });
                p.add(b);
            }
            add(p, BorderLayout.CENTER);
        }

        public static String show(Frame owner, String title) {
            NumpadDialog d = new NumpadDialog(owner, title);
            d.setVisible(true);
            return d.confirmed ? d.value : null;
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel headerPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblTax;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JButton payBtn;
    private javax.swing.JPanel pnlCategories;
    private javax.swing.JPanel pnlMenuGrid;
    private javax.swing.JButton removeBtn;
    private javax.swing.JButton reprintBtn;
    private javax.swing.JButton resetBtn;
    private javax.swing.JTable tblCart;
    // End of variables declaration//GEN-END:variables
}
