package app;

import util.Theme;
import util.UIHelper;
import util.CardPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.net.URL;

public class LoginFrame extends JFrame {

    private final JTextField user = new JTextField();
    private final JPasswordField pass = new JPasswordField();

    public LoginFrame() {
        setTitle("Gestion de Stock - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 650);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.APP_BG);

        // ================= LEFT (dark) =================
        JPanel left = new JPanel();
        left.setBackground(Theme.SIDEBAR_BG);
        left.setPreferredSize(new Dimension(420, 0));
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBorder(new EmptyBorder(40, 35, 40, 35));

        // ---- Title
        JLabel logo = new JLabel("Gestion de Stock");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        logo.setForeground(Color.WHITE);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ---- Small description
        JLabel slogan = new JLabel("<html><div style='color:#CBD5E1; line-height:1.5; text-align:center;'>"
                + "Application pour gérer les produits, suivre les entrées/sorties<br/>"
                + "et contrôler le niveau de stock."
                + "</div></html>");
        slogan.setFont(Theme.BODY);
        slogan.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ===== IMAGE =====
        JLabel imageLabel = new JLabel();
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        String filePath = "src/main/java/app/images/stock.png"; // ton chemin actuel
        setImageSmart(imageLabel, 280, 180, filePath, "/app/images/stock.png");

        // ---- Center block vertically
        left.add(Box.createVerticalGlue()); // push down

        left.add(logo);
        left.add(Box.createVerticalStrut(10));
        left.add(slogan);
        left.add(Box.createVerticalStrut(18));
        left.add(imageLabel);

        left.add(Box.createVerticalGlue()); // push up => centered

        JLabel hint = new JLabel("<html><div style='color:#94A3B8;font-size:12px; text-align:center;'>"
                + "Compte demo : admin / admin"
                + "</div></html>");
        hint.setAlignmentX(Component.CENTER_ALIGNMENT);
        left.add(hint);

        // ================= RIGHT (login card) =================
        JPanel rightWrap = new JPanel(new GridBagLayout());
        rightWrap.setBackground(Theme.APP_BG);

        CardPanel card = new CardPanel();
        card.setPreferredSize(new Dimension(520, 380));
        card.setLayout(new BorderLayout(14, 14));

        JPanel header = new JPanel(new GridLayout(0, 1, 4, 4));
        header.setOpaque(false);

        JLabel title = new JLabel("Connexion");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Theme.TEXT);

        JLabel sub = new JLabel("Connectez-vous pour accéder au tableau de bord");
        sub.setFont(Theme.BODY);
        sub.setForeground(Theme.MUTED);

        header.add(title);
        header.add(sub);

        JPanel form = new JPanel(new GridLayout(0, 1, 10, 10));
        form.setOpaque(false);

        form.add(label("Nom d'utilisateur"));
        styleField(user);
        form.add(user);

        form.add(label("Mot de passe"));
        styleField(pass);
        form.add(pass);

        JButton btn = primaryButton("Se connecter");
        btn.addActionListener(e -> doLogin());

        // Enter = login
        pass.addActionListener(e -> doLogin());
        user.addActionListener(e -> pass.requestFocusInWindow());

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.add(btn, BorderLayout.CENTER);

        card.add(header, BorderLayout.NORTH);
        card.add(form, BorderLayout.CENTER);
        card.add(footer, BorderLayout.SOUTH);

        rightWrap.add(card);

        root.add(left, BorderLayout.WEST);
        root.add(rightWrap, BorderLayout.CENTER);

        setContentPane(root);

        user.setText("admin");
        pass.setText("admin");
    }

    // ===== Image loader: classpath OR file =====
    private void setImageSmart(JLabel target, int w, int h, String... candidates) {
        for (String path : candidates) {
            // classpath
            if (path.startsWith("/")) {
                URL url = getClass().getResource(path);
                if (url != null) {
                    setScaledIcon(target, new ImageIcon(url), w, h);
                    return;
                }
            } else {
                // file
                File f = new File(path);
                if (f.exists() && f.isFile()) {
                    ImageIcon icon = new ImageIcon(path);
                    if (icon.getIconWidth() > 0) {
                        setScaledIcon(target, icon, w, h);
                        return;
                    }
                }
            }
        }

        target.setText("Image introuvable");
        target.setForeground(new Color(203, 213, 225));
        target.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }

    private void setScaledIcon(JLabel target, ImageIcon icon, int w, int h) {
        Image scaled = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
        target.setIcon(new ImageIcon(scaled));
        target.setText(null);
    }

    private JLabel label(String t) {
        JLabel l = new JLabel(t);
        l.setFont(Theme.BODY);
        l.setForeground(Theme.MUTED);
        return l;
    }

    private void styleField(JTextField f) {
        f.setFont(Theme.BODY);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        f.setBackground(Color.WHITE);
    }

    private JButton primaryButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setForeground(Color.WHITE);
        b.setBackground(Theme.SIDEBAR_ACTIVE);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(0, 44));

        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                b.setBackground(new Color(220, 38, 38));
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                b.setBackground(Theme.SIDEBAR_ACTIVE);
            }
        });
        return b;
    }

    private void doLogin() {
    String u = user.getText().trim();
    String p = new String(pass.getPassword());

    try {
        boolean ok = new dao.UserDAO().checkLogin(u, p);
        if (ok) {
            dispose();
            new MainFrame().setVisible(true);
        } else {
            UIHelper.msg(this, "Identifiants invalides.");
        }
    } catch (Exception ex) {
        UIHelper.msg(this, "Erreur DB: " + ex.getMessage());
    }
}
}