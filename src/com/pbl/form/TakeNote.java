
package com.pbl.form;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.pbl.model.Takenote;
import com.pbl.service.TakeNoteService;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class TakeNote extends JPanel {
    
    private static final Font CONTENT_FONT = 
    new Font("Segoe UI",Font.ITALIC, 14);
    private static final Font CONTENT_FONTt = 
    new Font("Segoe UI", Font.PLAIN,14);
    
    private final TakeNoteService takeNoteService = new TakeNoteService();
    private final DefaultListModel<Takenote> listModel = new DefaultListModel<>();
    private final JList<Takenote> listNotes = new JList<>(listModel);
    private final JTextField txtTitle   = new JTextField();
    private final JTextArea  txtContent = new JTextArea();
    JTextField txtSearch = new JTextField();
  
    private final JLabel     lblDate    = new JLabel();
    private final JButton    btnAdd     = new JButton("+ Add");
    private final JButton    btnUpdate  = new JButton("Update");
    private final JButton    btnDelete  = new JButton("Delete");

    private Takenote currentNote;
    private final int currentUserId;

    public TakeNote(int userId) {
        this.currentUserId = userId;
        initUI();
        loadNotes();
        initEvents();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 250));
        setOpaque(true);

        // Wrapper with purple rounded background
        JPanel wrapper = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(193, 202, 255));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(wrapper, BorderLayout.CENTER);

        // Main white container rounded
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(Color.WHITE);
        main.setBorder(new RoundedBorder(30));
        wrapper.add(main, BorderLayout.CENTER);

        // SplitPane static
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setBorder(null);
        splitPane.setEnabled(false);
        splitPane.setDividerSize(0);
        int leftWidth = 300;
        splitPane.setDividerLocation(leftWidth);
        splitPane.setResizeWeight(0.0);
        main.add(splitPane, BorderLayout.CENTER);

        // LEFT PANEL: Notes list
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(new Color(230, 230, 255));
        leftPanel.setBorder(new RoundedBorder(20));
        leftPanel.setPreferredSize(new Dimension(leftWidth, 0));

        // Header "Notes"
        JLabel lblHeader = new JLabel("Notes");
        lblHeader.setForeground(Color.GREEN);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 45));
        lblHeader.setBorder(BorderFactory.createEmptyBorder(5, 20, 10, 0));
        leftPanel.add(lblHeader, BorderLayout.NORTH);

        // Configure list spacing and scroll
        listNotes.setCellRenderer(new TakenoteCellRenderer());
        listNotes.setBackground(new Color(230, 230, 255));
        listNotes.setFixedCellHeight(100);
        JScrollPane scrollList = new JScrollPane(listNotes);
//        scrollList.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        scrollList.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollList.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollList.setBackground(new Color(255, 240, 245));
        scrollList.getViewport().addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int w = scrollList.getViewport().getWidth();
                // 20 = tổng 2 bên padding trái + phải bạn dùng ở margins
                listNotes.setFixedCellWidth(w - 20);
            }
        });
        leftPanel.add(scrollList, BorderLayout.CENTER);

        splitPane.setLeftComponent(leftPanel);

        // RIGHT PANEL: Detail
        JPanel detailPanel = new JPanel(new BorderLayout(15, 15));
        detailPanel.setBackground(Color.WHITE);
        Border pad = BorderFactory.createEmptyBorder(20, 20, 20, 20);
        Border round = new RoundedBorder(20);
        detailPanel.setBorder(BorderFactory.createCompoundBorder(round, pad));

        splitPane.setRightComponent(detailPanel);

        // Title field rounded
        txtTitle.setFont(CONTENT_FONT);
        txtTitle.setFont(txtTitle.getFont().deriveFont(18f));
        txtTitle.setBorder(new RoundedBorder(10));
        txtTitle.setBackground(new Color(250, 250, 255));
        detailPanel.add(txtTitle, BorderLayout.NORTH);

        // Content area rounded
        txtContent.setLineWrap(true);
        txtContent.setWrapStyleWord(true);
        txtContent.setBackground(new Color(250, 250, 255));
        
        txtContent.setFont(CONTENT_FONTt);
        JScrollPane contentScroll = new JScrollPane(txtContent);
        Border pad2   = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        Border round2 = new RoundedBorder(10);
        contentScroll.setBorder(BorderFactory.createCompoundBorder(round2, pad2));
        detailPanel.add(contentScroll, BorderLayout.CENTER);

        // Bottom: date + buttons
        
        
        JPanel bottom = new JPanel(new BorderLayout(10, 0));
        bottom.setBackground(Color.WHITE);
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));

        lblDate.setFont(lblDate.getFont().deriveFont(14f));
        bottom.add(lblDate, BorderLayout.WEST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        btnPanel.setBackground(Color.WHITE);
        
        JPanel buts = new JPanel(new GridLayout(1,2));
        buts.setBackground(Color.WHITE);
        
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Search...");
        txtSearch.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, new FlatSVGIcon("com/pbl/icon/search.svg"));
        txtSearch.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc:15;"
                + "borderWidth:0;"
                + "focusWidth:0;"
                + "innerFocusWidth:0;"
                + "margin:5,20,5,20;"
                + "background:$Panel.background");
     
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) { filter(); }
        @Override
        public void removeUpdate(DocumentEvent e) { filter(); }
        @Override
        public void changedUpdate(DocumentEvent e) { filter(); }
        private void filter() {
            String key = txtSearch.getText();
            listModel.clear();
            takeNoteService.search(currentUserId, key)
                           .forEach(listModel::addElement);
        }
    });
        
        buts.add(txtSearch);
        
           
        styleButton(btnAdd,    new Color( 66,150,242));
        styleButton(btnUpdate, new Color(123,139,245));
        styleButton(btnDelete, new Color(240,  95, 87));
        
        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        buts.add(btnPanel);
        bottom.add(buts, BorderLayout.EAST);

        detailPanel.add(bottom, BorderLayout.SOUTH);
    }

    private void loadNotes() {
        listModel.clear();
        List<Takenote> notes = takeNoteService.loadAll(currentUserId);
        notes.forEach(listModel::addElement);
    }

    private void Refresh() {
        currentNote = null;
        txtTitle.setText("");
        txtContent.setText("");
        lblDate.setText("");
        listNotes.clearSelection();
    }

    private void initEvents() {
        listNotes.addListSelectionListener(e -> {
    if (!e.getValueIsAdjusting()) {
        currentNote = listNotes.getSelectedValue();
        if (currentNote != null) {
            txtTitle.setText(currentNote.getTitle());
            txtContent.setText(currentNote.getContent());
            lblDate.setText(currentNote.getCreatedAt()
                .format(DateTimeFormatter.ofPattern("MMM d, yyyy")));
            btnAdd.setEnabled(false);
        } else {
            btnAdd.setEnabled(true);
        }
    }
});

        btnAdd.addActionListener(e -> {
            String title   = txtTitle.getText().trim();
            String content = txtContent.getText().trim();
            if (title.isEmpty() || content.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Tiêu đề và nội dung không được để trống",
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Takenote n = new Takenote(0, currentUserId, title, content, null);
            takeNoteService.add(n);
            JOptionPane.showMessageDialog(this,
                "Bạn đã thêm ghi chú thành công",
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            loadNotes(); Refresh();
        });

        btnUpdate.addActionListener(e -> {
            if (currentNote != null) {
                currentNote.setTitle(txtTitle.getText());
                currentNote.setContent(txtContent.getText());
                takeNoteService.update(currentNote);
                JOptionPane.showMessageDialog(this,
                    "Bạn đã cập nhật ghi chú thành công",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadNotes();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn ghi chú để cập nhật",
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnDelete.addActionListener(e -> {
            if (currentNote != null) {
                int ans = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc muốn xóa ghi chú này?",
                    "Xác nhận", JOptionPane.YES_NO_OPTION);
                if (ans == JOptionPane.YES_OPTION) {
                    takeNoteService.delete(currentNote.getId());
                    JOptionPane.showMessageDialog(this,
                        "Bạn đã xóa ghi chú thành công",
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    loadNotes(); Refresh();
                }
            } else {
                JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn ghi chú để xóa",
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            }
        });
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorder(new RoundedBorder(12));
        btn.setFocusPainted(false);
    }

    private static class RoundedBorder implements Border {
        private final int radius;
        public RoundedBorder(int radius) { this.radius = radius; }
        @Override public Insets getBorderInsets(Component c) {
            return new Insets(radius+1, radius+1, radius+2, radius+1);
        }
        @Override public boolean isBorderOpaque() { return false; }
        @Override public void paintBorder(Component c, Graphics g,
                                          int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setStroke(new BasicStroke(2f));   // tăng độ dày đường viền
            g2.setColor(new Color(100,100,200));
            g2.drawRoundRect(x, y, width-1, height-1, radius, radius);

        }
    }

 private static class TakenoteCellRenderer implements ListCellRenderer<Takenote> {
    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("MMM d, yyyy");
    private final Color LIST_BG = new Color(230, 230, 255);
    private final Color CARD_BG = Color.WHITE;
    private final Color CARD_SEL = new Color(255, 250, 205);

    @Override
    public Component getListCellRendererComponent(
        JList<? extends Takenote> list,
        Takenote note,
        int index,
        boolean isSelected,
        boolean cellHasFocus) {

        JPanel outer = new JPanel(new BorderLayout());
        outer.setOpaque(true);
        outer.setBackground(LIST_BG);
    
        outer.setBorder(BorderFactory.createEmptyBorder(5, 10, 8, 10));

        JPanel card = new JPanel(new BorderLayout());
        card.setOpaque(true);
        card.setBackground(isSelected ? CARD_SEL : CARD_BG);
        Border round = new RoundedBorder(12);
        Border inner = BorderFactory.createEmptyBorder(10, 12, 10, 12);
        card.setBorder(BorderFactory.createCompoundBorder(round, inner));

        JLabel lblTitle = new JLabel(note.getTitle());
        lblTitle.setFont(lblTitle.getFont().deriveFont(14f));
        JLabel lblDate  = new JLabel(note.getCreatedAt().format(FMT));
        lblDate.setFont(lblDate.getFont().deriveFont(12f));
        lblDate.setForeground(new Color(100, 100, 120));
        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblDate, BorderLayout.SOUTH);

        outer.add(card, BorderLayout.CENTER);
        return outer;
    }
}

}
