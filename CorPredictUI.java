import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.border.EmptyBorder;

public class CorPredictUI {

    public static void main(String[] args) {
        System.out.println(">>> [1/3] Starting Medical Terminal Engine...");
        
        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println(">>> [2/3] Building 3D Graphics...");
                JFrame frame = new JFrame("CorPredict-AI : Advanced Medical Terminal");
                frame.setSize(1000, 750); // Height thodi badhai naye field ke liye
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                
                GradientBackgroundPanel mainPanel = new GradientBackgroundPanel();
                mainPanel.setLayout(new BorderLayout());
                
                // Header
                JPanel header = new JPanel(new GridLayout(2, 1));
                header.setOpaque(false);
                header.setBorder(new EmptyBorder(20, 0, 10, 0));
                
                JLabel title = new JLabel("CORPREDICT - AI RISK ANALYSIS", SwingConstants.CENTER);
                title.setFont(new Font("Consolas", Font.BOLD, 32));
                title.setForeground(new Color(23, 177, 105)); 
                
                JLabel subTitle = new JLabel("SYSTEM STATUS: CONNECTED | MEMORY: RECORDING ACTIVE", SwingConstants.CENTER);
                subTitle.setFont(new Font("Consolas", Font.BOLD, 14));
                subTitle.setForeground(new Color(128, 196, 158));
                
                header.add(title);
                header.add(subTitle);
                mainPanel.add(header, BorderLayout.NORTH);

                // Center Form (Ab 6 rows hain)
                JPanel centerWrapper = new JPanel(new GridBagLayout());
                centerWrapper.setOpaque(false);
                
                GlassCardPanel formCard = new GlassCardPanel();
                formCard.setPreferredSize(new Dimension(650, 480)); 
                formCard.setLayout(new GridLayout(6, 2, 20, 20)); // Changed to 6 rows
                formCard.setBorder(new EmptyBorder(30, 40, 30, 40));

                // NEW: Patient Name Field
                CyberTextField nameField = new CyberTextField();
                addFormRow(formCard, "Patient Name:", nameField);

                CyberTextField ageField = new CyberTextField();
                addFormRow(formCard, "Patient Age (Years):", ageField);
                
                CyberComboBox genderBox = new CyberComboBox(new String[]{"Male (1)", "Female (0)"});
                addFormRow(formCard, "Patient Gender:", genderBox);
                
                CyberComboBox cpBox = new CyberComboBox(new String[]{"0: Typical Angina", "1: Atypical", "2: Non-anginal", "3: Asymptomatic"});
                addFormRow(formCard, "Chest Pain Type:", cpBox);
                
                CyberTextField bpField = new CyberTextField();
                addFormRow(formCard, "Resting BP (mmHg):", bpField);
                
                CyberTextField cholField = new CyberTextField();
                addFormRow(formCard, "Cholesterol (mg/dl):", cholField);

                centerWrapper.add(formCard);
                mainPanel.add(centerWrapper, BorderLayout.CENTER);

                // Action Button
                JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
                bottomPanel.setOpaque(false);
                bottomPanel.setBorder(new EmptyBorder(0, 0, 40, 0));
                
                CyberButton analyzeBtn = new CyberButton("INITIATE DIAGNOSTICS & SAVE");
                analyzeBtn.setPreferredSize(new Dimension(400, 60));
                bottomPanel.add(analyzeBtn);
                mainPanel.add(bottomPanel, BorderLayout.SOUTH);

                // API & Memory Logic
                analyzeBtn.addActionListener(e -> {
                    try {
                        String name = nameField.getText().trim();
                        if(name.isEmpty()) name = "UNKNOWN_PATIENT";

                        int age = Integer.parseInt(ageField.getText().trim());
                        int sex = genderBox.getSelectedIndex() == 0 ? 1 : 0;
                        int cp = cpBox.getSelectedIndex();
                        int bp = Integer.parseInt(bpField.getText().trim());
                        int chol = Integer.parseInt(cholField.getText().trim());

                        // Creating Unique Patient ID (e.g., CP-49204)
                        String uniqueID = "CP-" + (int)(Math.random() * 90000 + 10000);

                        int fbs = 0, restecg = 1, thalach = 150, exang = 0, slope = 1, ca = 0, thal = 2;
double oldpeak = 1.0;

                        String jsonInputString = String.format(
                            "{\"age\":%d, \"sex\":%d, \"cp\":%d, \"trestbps\":%d, \"chol\":%d, \"fbs\":%d, \"restecg\":%d, \"thalach\":%d, \"exang\":%d, \"oldpeak\":%f, \"slope\":%d, \"ca\":%d, \"thal\":%d}",
                            age, sex, cp, bp, chol, fbs, restecg, thalach, exang, oldpeak, slope, ca, thal
                        );

                        URL url = new URL("http://127.0.0.1:5000/predict");
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setRequestMethod("POST");
                        con.setRequestProperty("Content-Type", "application/json");
                        con.setDoOutput(true);

                        try(OutputStream os = con.getOutputStream()) {
                            byte[] input = jsonInputString.getBytes("utf-8");
                            os.write(input, 0, input.length);
                        }

                        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
                        StringBuilder response = new StringBuilder();
                        String responseLine;
                        while ((responseLine = br.readLine()) != null) {
                            response.append(responseLine.trim());
                        }

                        boolean isHighRisk = response.toString().contains("\"risk_level\": 1");
                        String resultText = isHighRisk ? "HIGH RISK" : "SAFE";

                        // NEW: Save to Local Database (CSV)
                        savePatientData(uniqueID, name, age, sex, bp, chol, resultText);

                        // Output Popup
                        if(isHighRisk) {
                            showCyberPopup(frame, "ID: " + uniqueID + "\nPATIENT: " + name + "\n\nHIGH RISK DETECTED!\nPlease consult a Cardiologist immediately.", true);
                        } else {
                            showCyberPopup(frame, "ID: " + uniqueID + "\nPATIENT: " + name + "\n\nSAFE: Low risk of heart disease.\nPatient vitals are stable and saved.", false);
                        }

                    } catch (Exception ex) {
                        showCyberPopup(frame, "CONNECTION ERROR:\nIs the Python API Server running or are fields empty?", true);
                    }
                });

                frame.setContentPane(mainPanel);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                System.out.println(">>> [3/3] UI Successfully Launched!");

            } catch (Exception ex) {
                System.out.println("\n[ERROR] UI CRASH DETECTED:");
                ex.printStackTrace();
            }
        });
    }

    // ==========================================================
    // NEW: MEMORY SYSTEM (SAVE TO CSV)
    // ==========================================================
    private static void savePatientData(String id, String name, int age, int sex, int bp, int chol, String result) {
        try {
            File file = new File("Patient_Records.csv");
            boolean isNewFile = !file.exists();
            
            FileWriter fw = new FileWriter(file, true); // true means 'append' to existing data
            PrintWriter pw = new PrintWriter(fw);
            
            if (isNewFile) {
                pw.println("Date_Time,Patient_ID,Name,Age,Gender,Resting_BP,Cholesterol,AI_Diagnosis");
            }
            
            String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String genderStr = (sex == 1) ? "Male" : "Female";
            
            pw.printf("%s,%s,%s,%d,%s,%d,%d,%s\n", timeStamp, id, name, age, genderStr, bp, chol, result);
            pw.close();
            System.out.println(">>> Data Saved Successfully for: " + id);
        } catch (Exception e) {
            System.out.println(">>> Error saving data!");
        }
    }

    // ==========================================================
    // POPUP SYSTEM WITH ALARM SOUND
    // ==========================================================
    private static void showCyberPopup(JFrame parent, String message, boolean isHighRisk) {
        // Play Alarm Sound for High Risk
        if (isHighRisk) {
            new Thread(() -> {
                try {
                    for (int i = 0; i < 3; i++) {
                        Toolkit.getDefaultToolkit().beep(); // System Danger Beep
                        Thread.sleep(400); // Wait between beeps
                    }
                } catch (Exception ignored) {}
            }).start();
        }

        JDialog dialog = new JDialog(parent, true);
        dialog.setUndecorated(true); 
        dialog.setSize(480, 280); // Thoda bada kiya text ke liye
        dialog.setLocationRelativeTo(parent);

        JPanel content = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = Math.max(1, getWidth()), h = Math.max(1, getHeight());
                
                g2d.setColor(new Color(10, 20, 15, 245));
                g2d.fillRoundRect(0, 0, w, h, 20, 20);
                
                g2d.setColor(isHighRisk ? new Color(255, 60, 60) : new Color(23, 177, 105));
                g2d.setStroke(new BasicStroke(3.0f));
                g2d.drawRoundRect(1, 1, w-3, h-3, 20, 20);
                g2d.dispose();
            }
        };
        content.setLayout(new BorderLayout());
        content.setOpaque(false);

        JLabel iconLabel = new JLabel(isHighRisk ? "⚠ CRITICAL ALERT" : "✅ SYSTEM SAFE", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Consolas", Font.BOLD, 26));
        iconLabel.setForeground(isHighRisk ? new Color(255, 80, 80) : new Color(23, 177, 105));
        iconLabel.setBorder(new EmptyBorder(20, 0, 10, 0));

        JTextArea msgArea = new JTextArea(message);
        msgArea.setFont(new Font("Segoe UI", Font.BOLD, 17));
        msgArea.setForeground(Color.WHITE);
        msgArea.setBackground(new Color(0,0,0,0));
        msgArea.setOpaque(false);
        msgArea.setEditable(false);
        msgArea.setWrapStyleWord(true);
        msgArea.setLineWrap(true);
        msgArea.setMargin(new Insets(10, 40, 10, 40));

        CyberButton closeBtn = new CyberButton("ACKNOWLEDGE");
        closeBtn.setPreferredSize(new Dimension(250, 45));
        closeBtn.addActionListener(e -> dialog.dispose()); 
        
        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.setBorder(new EmptyBorder(10, 0, 20, 0));
        btnPanel.add(closeBtn);

        content.add(iconLabel, BorderLayout.NORTH);
        content.add(msgArea, BorderLayout.CENTER);
        content.add(btnPanel, BorderLayout.SOUTH);

        dialog.setContentPane(content);
        dialog.setVisible(true);
    }

    private static void addFormRow(JPanel panel, String labelText, JComponent inputNode) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(new Color(224, 242, 233));
        panel.add(label);
        panel.add(inputNode);
    }

    // --- SAFEGUARDED GRAPHICS COMPONENTS ---
    static class GradientBackgroundPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            int w = Math.max(1, getWidth()), h = Math.max(1, getHeight());
            g2d.setPaint(new GradientPaint(0, 0, new Color(5, 18, 11), w, h, new Color(10, 36, 24)));
            g2d.fillRect(0, 0, w, h);
        }
    }

    static class GlassCardPanel extends JPanel {
        public GlassCardPanel() { setOpaque(false); }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = Math.max(1, getWidth()), h = Math.max(1, getHeight());
            g2d.setColor(new Color(0, 0, 0, 180));
            g2d.fillRoundRect(15, 20, w - 30, h - 25, 25, 25);
            g2d.setColor(new Color(15, 41, 30, 220));
            g2d.fillRoundRect(5, 5, w - 30, h - 30, 25, 25);
            g2d.setColor(new Color(23, 177, 105));
            g2d.setStroke(new BasicStroke(2.0f));
            g2d.drawRoundRect(5, 5, w - 30, h - 30, 25, 25);
            g2d.dispose();
        }
    }

    static class CyberTextField extends JTextField {
        public CyberTextField() {
            setOpaque(true); setBackground(new Color(8, 26, 18));
            setForeground(new Color(23, 177, 105)); setCaretColor(Color.WHITE);
            setFont(new Font("Consolas", Font.BOLD, 18));
            setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(18, 138, 82), 1), new EmptyBorder(5, 10, 5, 10)));
        }
    }

    static class CyberComboBox extends JComboBox<String> {
        public CyberComboBox(String[] items) {
            super(items); setBackground(new Color(8, 26, 18));
            setForeground(new Color(23, 177, 105)); setFont(new Font("Consolas", Font.BOLD, 16));
            setBorder(BorderFactory.createLineBorder(new Color(18, 138, 82), 1));
        }
    }

    static class CyberButton extends JButton {
        public CyberButton(String text) {
            super(text); setContentAreaFilled(false); setFocusPainted(false); setBorderPainted(false);
            setForeground(new Color(5, 18, 11)); setFont(new Font("Consolas", Font.BOLD, 20));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = Math.max(1, getWidth()), h = Math.max(1, getHeight());
            
            g2d.setColor(new Color(0, 0, 0, 150));
            g2d.fillRoundRect(5, 8, w - 10, h - 10, 15, 15);
            Color c1 = getModel().isRollover() ? new Color(36, 240, 146) : new Color(28, 214, 129);
            Color c2 = getModel().isRollover() ? new Color(23, 177, 105) : new Color(18, 138, 82);
            g2d.setPaint(new GradientPaint(0, 0, c1, 0, h, c2));
            g2d.fillRoundRect(5, 3, w - 10, h - 10, 15, 15);
            super.paintComponent(g); g2d.dispose();
        }
    }
}