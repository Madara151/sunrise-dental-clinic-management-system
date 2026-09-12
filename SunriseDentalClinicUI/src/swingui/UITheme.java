package swingui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * UITheme - centralized color palette, fonts, and helper methods for a
 * consistent, modern look across all Swing windows.
 *
 * Using a shared theme class (rather than repeating colors/fonts in every
 * frame) is a simple application of the DRY principle and keeps the whole
 * application visually consistent - worth mentioning as a design decision
 * in the report.
 */
public class UITheme {

    // ---- Color palette (light, clinical, modern) ----
    public static final Color PRIMARY = new Color(0x2E75B6);       // clinic blue
    public static final Color PRIMARY_DARK = new Color(0x1B4F72);
    public static final Color PRIMARY_LIGHT = new Color(0xD6EAF8);
    public static final Color ACCENT = new Color(0x2E9E4F);        // success green
    public static final Color DANGER = new Color(0xC0392B);        // error red
    public static final Color BACKGROUND = new Color(0xF4F7FA);
    public static final Color CARD_BACKGROUND = Color.WHITE;
    public static final Color TEXT_DARK = new Color(0x1B2631);
    public static final Color TEXT_MUTED = new Color(0x7F8C8D);
    public static final Color BORDER = new Color(0xD5DBDB);

    // ---- Fonts ----
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_LABEL = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_FIELD = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_MONO = new Font("Consolas", Font.PLAIN, 13);

    /**
     * Applies a flat, modern look to a JButton (primary/filled style).
     */
    public static void stylePrimaryButton(JButton button) {
        button.setFont(FONT_BUTTON);
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 18, 10, 18));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setBorderPainted(false);
    }

    /**
     * Applies a flat, modern look to a secondary/outline-style JButton.
     */
    public static void styleSecondaryButton(JButton button) {
        button.setFont(FONT_BUTTON);
        button.setForeground(PRIMARY_DARK);
        button.setBackground(PRIMARY_LIGHT);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 18, 10, 18));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setBorderPainted(false);
    }

    /**
     * Styles a JTextField/JPasswordField/JSpinner-editor with padding
     * and a soft border.
     */
    public static void styleField(JComponent field) {
        field.setFont(FONT_FIELD);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                new EmptyBorder(6, 8, 6, 8)
        ));
    }

    public static void styleLabel(JLabel label) {
        label.setFont(FONT_LABEL);
        label.setForeground(TEXT_DARK);
    }

    public static void styleTitle(JLabel label) {
        label.setFont(FONT_TITLE);
        label.setForeground(PRIMARY_DARK);
    }

    public static void styleSubtitle(JLabel label) {
        label.setFont(FONT_SUBTITLE);
        label.setForeground(TEXT_MUTED);
    }

    /**
     * Creates a white "card" panel with padding and a subtle border,
     * used as the main content container in each window.
     */
    public static JPanel createCardPanel(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(CARD_BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                new EmptyBorder(25, 25, 25, 25)
        ));
        return panel;
    }
}