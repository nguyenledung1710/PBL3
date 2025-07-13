package com.pbl.main;

import com.formdev.flatlaf.FlatLightLaf;
import com.pbl.component.ThoiKhoaBieu;
import com.pbl.event.EventColorChange;
import com.pbl.form.CalendarForm;
import com.pbl.form.Clock;
import com.pbl.form.DashBoard;

import com.pbl.form.Home_Form;
import com.pbl.form.MainForm;
import com.pbl.form.Setting_Form;
import com.pbl.form.TakeNote;
import com.pbl.form.ThongKeForm;
import com.pbl.menu.EventMenu;
import com.pbl.model.Users;
import com.pbl.properties.SystemProperties;
import com.pbl.service.EmailNotificationService;
import com.pbl.theme.SystemTheme;
import com.pbl.theme.ThemeColor;
import com.pbl.theme.ThemeColorChange;
import java.awt.Color;
import java.awt.Dimension;
import java.sql.SQLException;
import java.time.LocalDate;
import javax.swing.UIManager;

public class Main extends javax.swing.JFrame {

    private Setting_Form settingForm;
    private MainForm main;
    private int userID;
    private DashBoard formHome;
    private EmailNotificationService emailService;

    public Main(Users user) throws SQLException {
        initComponents();
        setBackground(new Color(0, 0, 0, 0));
        this.userID = user.getUser_id();
        formHome = new DashBoard(userID);
        this.emailService = new EmailNotificationService(user);
        this.emailService.start();
        init();
    }

    private void init() throws SQLException {
        main = new MainForm();
        header.initMoving(this);
        header.initEvent(this, panelBackground1);
        menu.addEventMenu(new EventMenu() {
            @Override
            public void selectedMenu(int index) {
                if (index == 1) {
                    LocalDate today = LocalDate.now();
                    mainBody.displayForm(new CalendarForm(main, today, userID));

                } else if (index == 2) {

                    mainBody.displayForm(new ThongKeForm(userID));
                } else if (index == 0) {

                    mainBody.displayForm(formHome);
                } else if (index == 6) {
                    mainBody.displayForm(settingForm, "Setting");
                } else if (index == 3) {
                    TakeNote tempTN = new TakeNote(userID);
                    Dimension sizeTN = tempTN.getPreferredSize();

                    Clock clockPanel = new Clock(userID);
                    clockPanel.setPreferredSize(sizeTN);

                    mainBody.displayForm(clockPanel);
                } else if (index == 4) {
                    try {
                        UIManager.setLookAndFeel(new FlatLightLaf());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    mainBody.displayForm(new TakeNote(userID));
                } else if (index == 5) {
                    mainBody.displayForm(new ThoiKhoaBieu());
                }
            }
        });
        ThemeColorChange.getInstance().addThemes(new ThemeColor(new Color(34, 34, 34), Color.WHITE) {
            @Override
            public void onColorChange(Color color) {
                panelBackground1.setBackground(color);
            }
        });
        ThemeColorChange.getInstance().addThemes(new ThemeColor(Color.WHITE, new Color(80, 80, 80)) {
            @Override
            public void onColorChange(Color color) {
                mainBody.changeColor(color);
            }
        });
        ThemeColorChange.getInstance().initBackground(panelBackground1);
        SystemProperties pro = new SystemProperties();
        pro.loadFromFile();
        if (!pro.isDarkMode()) {
            ThemeColorChange.getInstance().setMode(ThemeColorChange.Mode.LIGHT);
            panelBackground1.setBackground(Color.WHITE);
            mainBody.changeColor(new Color(80, 80, 80));
        }
        if (pro.getBackgroundImage() != null) {
            ThemeColorChange.getInstance().changeBackgroundImage(pro.getBackgroundImage());
        }
        SystemTheme.mainColor = pro.getColor();
        settingForm = new Setting_Form(userID);
        settingForm.setEventColorChange(new EventColorChange() {
            @Override
            public void colorChange(Color color) {
                SystemTheme.mainColor = color;
                ThemeColorChange.getInstance().ruenEventColorChange(color);
                repaint();
                pro.save("theme_color", color.getRGB() + "");
            }
        });
        settingForm.setSelectedThemeColor(pro.getColor());
        settingForm.setDarkMode(pro.isDarkMode());
        settingForm.initBackgroundImage(pro.getBackgroundImage());
        mainBody.displayForm(formHome);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelBackground1 = new com.pbl.swing.PanelBackground();
        header = new com.pbl.component.Header();
        menu = new com.pbl.menu.Menu();
        mainBody = new com.pbl.component.MainBody();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        panelBackground1.setBackground(new java.awt.Color(34, 34, 34));

        javax.swing.GroupLayout panelBackground1Layout = new javax.swing.GroupLayout(panelBackground1);
        panelBackground1.setLayout(panelBackground1Layout);
        panelBackground1Layout.setHorizontalGroup(
            panelBackground1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(header, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(panelBackground1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(menu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mainBody, javax.swing.GroupLayout.DEFAULT_SIZE, 1108, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelBackground1Layout.setVerticalGroup(
            panelBackground1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBackground1Layout.createSequentialGroup()
                .addComponent(header, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelBackground1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(menu, javax.swing.GroupLayout.DEFAULT_SIZE, 597, Short.MAX_VALUE)
                    .addComponent(mainBody, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelBackground1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelBackground1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.pbl.component.Header header;
    public com.pbl.component.MainBody mainBody;
    private com.pbl.menu.Menu menu;
    private com.pbl.swing.PanelBackground panelBackground1;
    // End of variables declaration//GEN-END:variables
}
