package com.pbl.form;

import com.pbl.chart.ModelChart;
import com.pbl.main.Main;
import com.pbl.model.ModelCard;
import com.pbl.model.StatusType;
import com.pbl.model.Task;
import com.pbl.service.TaskService;
import com.pbl.swing.ScrollBar;

import com.pbl.swing.noticeboard.ModelNoticeBoard;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

public class DashBoard extends javax.swing.JPanel {

    public DashBoard(int user_id) {
        initComponents();
        setOpaque(false);
        initData(user_id);
        spTable.setVerticalScrollBar(new ScrollBar());
        spTable.getVerticalScrollBar().setBackground(Color.DARK_GRAY);
        spTable.getViewport().setBackground(Color.BLACK);
        JPanel p = new JPanel();
        p.setBackground(Color.BLUE);
        Color BG_PANEL = new Color(0xF3F4F6);
        Color BG_HEADER = new Color(0xE2E8F0);
        Color BG_ROW_ALT = new Color(0xEDF2F7);
        Color BORDER = new Color(0xCBD5E1);
        Color TEXT = new Color(0x334155);
        Color ACCENT = new Color(0x14B8A6);
        table.setBackground(BG_PANEL);
        table.setGridColor(BORDER);
        table.setShowGrid(true);
        table.setRowHeight(28);
        table.setSelectionBackground(ACCENT);
        table.setSelectionForeground(Color.WHITE);
        spTable.setCorner(JScrollPane.UPPER_RIGHT_CORNER, p);
        table.getTableHeader().setBackground(new Color(0x3498DB));
        table.getTableHeader().setForeground(TEXT);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object val,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(tbl, val, isSelected, hasFocus, row, col);
                if (isSelected) {
                    c.setBackground(ACCENT);
                    c.setForeground(Color.WHITE);
                } else {
                    Color evenRow = new Color(0xD1F2EB);
                    Color oddRow = new Color(0xF9FAFB);
                    c.setBackground(row % 2 == 0 ? evenRow : evenRow);
                    c.setForeground(TEXT);
                }
                return c;
            }
        };
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel()
                    .getColumn(i)
                    .setCellRenderer(renderer);
        }
        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable tbl, Object value,
                    boolean isSelected, boolean hasFocus,
                    int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(
                        tbl, value, isSelected, hasFocus, row, col);
                lbl.setOpaque(true);                            // bắt buộc phải opaque
                lbl.setBackground(new Color(0x3498DB));         // Peter River
                lbl.setForeground(TEXT);                        // TEXT là Color bạn khai báo
                lbl.setHorizontalAlignment(JLabel.CENTER);      // hoặc LEFT/RIGHT tuỳ ý
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
                return lbl;
            }
        });

        initTaskTable(user_id);
    }

    private void initData(int user_id) {

        initNoticeBoard(user_id);

    }

    private void initTaskTable(int userId) {
        // Giả sử table đã có model với 5 cột: No, Time, Task, Category, Status

        TaskService service = new TaskService();
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(29);  // 30 ngày gần nhất
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM, yyyy HH:mm");

        // Lấy tất cả task 30 ngày gần nhất
        List<Task> tasks = service.getAllTasksByUser(userId).stream()
                .filter(t -> {
                    LocalDate d = t.getDateTime().toLocalDate();
                    return !d.isBefore(startDate) && !d.isAfter(today);
                })
                .sorted(Comparator.comparing((Task t)
                        -> t.getDateTime().toLocalDate().equals(today) ? 0 : 1
                ).thenComparing(Task::getDateTime).reversed())
                .collect(Collectors.toList());

        int no = 1;
        for (Task t : tasks) {
            String time = t.getDateTime().format(fmt);
            String title = t.getTitle();
            String category = t.getCategory();
            StatusType status = t.isDone() ? StatusType.DONE : StatusType.NODONE;
            table.addRow(new Object[]{no++, time, title, category, status});
        }
    }

    private void initNoticeBoard(int userId) {

        TaskService taskService = new TaskService();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        LocalDate today = LocalDate.now();
        List<Task> tasks = taskService.getAllTasksByUser(userId);
        tasks.sort(Comparator.comparing(Task::getDateTime));

        boolean printedToday = false;
        for (Task task : tasks) {
            if (task.getDateTime().toLocalDate().equals(today)) {
                if (!printedToday) {
                    noticeBoard.addDate(today.format(dateFormatter));
                    printedToday = true;
                }
                noticeBoard.addNoticeBoard(new ModelNoticeBoard(
                        new Color(27, 188, 204),
                        task.getTitle(),
                        task.getDateTime().format(timeFormatter),
                        task.getDescription()
                ));
            }
        }

        Map<LocalDate, List<Task>> overdueMap = new TreeMap<>(Comparator.reverseOrder());
        for (Task task : tasks) {
            LocalDate d = task.getDateTime().toLocalDate();
            if (d.isBefore(today) && !task.isDone()) {
                overdueMap
                        .computeIfAbsent(d, k -> new ArrayList<>())
                        .add(task);
            }
        }

        for (Map.Entry<LocalDate, List<Task>> e : overdueMap.entrySet()) {
            noticeBoard.addDate(e.getKey().format(dateFormatter));
            for (Task task : e.getValue()) {
                noticeBoard.addNoticeBoard(new ModelNoticeBoard(
                        new Color(255, 0, 0),
                        task.getTitle(),
                        task.getDateTime().format(timeFormatter),
                        task.getDescription()
                ));
            }
        }

        noticeBoard.scrollToTop();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        roundPanel1 = new com.pbl.swing.RoundPanel();
        spTable = new javax.swing.JScrollPane();
        table = new com.pbl.swing.Table();
        roundPanel2 = new com.pbl.swing.RoundPanel();
        jPanel1 = new javax.swing.JPanel();
        noticeBoard = new com.pbl.swing.noticeboard.NoticeBoard();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();

        jLabel1.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(26, 188, 156));
        jLabel1.setText("Dashboard / Home");

        jLabel5.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(26, 188, 156));
        jLabel5.setText("Recent Tasks (Last 30 Days)");

        roundPanel1.setBackground(new java.awt.Color(255, 255, 255));

        spTable.setBorder(null);
        spTable.setForeground(new java.awt.Color(255, 204, 153));

        table.setBackground(new java.awt.Color(204, 255, 204));
        table.setForeground(new java.awt.Color(255, 255, 102));
        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "Time", "Task", "Category", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table.setGridColor(new java.awt.Color(51, 255, 0));
        table.setSelectionBackground(new java.awt.Color(204, 204, 0));
        table.setSelectionForeground(new java.awt.Color(255, 204, 204));
        spTable.setViewportView(table);

        javax.swing.GroupLayout roundPanel1Layout = new javax.swing.GroupLayout(roundPanel1);
        roundPanel1.setLayout(roundPanel1Layout);
        roundPanel1Layout.setHorizontalGroup(
            roundPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(spTable, javax.swing.GroupLayout.DEFAULT_SIZE, 656, Short.MAX_VALUE)
        );
        roundPanel1Layout.setVerticalGroup(
            roundPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(spTable)
        );

        roundPanel2.setBackground(new java.awt.Color(209, 242, 235));

        jPanel1.setBackground(new java.awt.Color(209, 242, 235));

        noticeBoard.setBackground(new java.awt.Color(204, 255, 0));

        jLabel2.setFont(new java.awt.Font("sansserif", 1, 15)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(26, 188, 156));
        jLabel2.setText("Notice Board");
        jLabel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 1));

        jLabel3.setBackground(new java.awt.Color(26, 188, 156));
        jLabel3.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(26, 188, 156));
        jLabel3.setText("Simple Miglayout API Doc");
        jLabel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 1));

        jLabel4.setOpaque(true);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addGap(0, 338, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(noticeBoard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addGap(15, 15, 15)
                .addComponent(jLabel3)
                .addGap(9, 9, 9)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 1, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(noticeBoard, javax.swing.GroupLayout.PREFERRED_SIZE, 584, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout roundPanel2Layout = new javax.swing.GroupLayout(roundPanel2);
        roundPanel2.setLayout(roundPanel2Layout);
        roundPanel2Layout.setHorizontalGroup(
            roundPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        roundPanel2Layout.setVerticalGroup(
            roundPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundPanel2Layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(roundPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(roundPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel5))
                .addContainerGap(62, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(27, 27, 27)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(roundPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(roundPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private com.pbl.swing.noticeboard.NoticeBoard noticeBoard;
    private com.pbl.swing.RoundPanel roundPanel1;
    private com.pbl.swing.RoundPanel roundPanel2;
    private javax.swing.JScrollPane spTable;
    private com.pbl.swing.Table table;
    // End of variables declaration//GEN-END:variables
}
