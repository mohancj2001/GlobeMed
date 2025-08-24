package view;

import com.formdev.flatlaf.FlatClientProperties;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.SwingUtilities;
import model.LogedUserBean;

public class Home extends javax.swing.JFrame {

    LogedUserBean userBean;

    Dashboard dashboard;
    Patient patient;
    private int HomeMenu = 0;

    private static boolean menu_status = true;

    public Home(LogedUserBean userBean) {
        initComponents();
        this.userBean = userBean;
        userInfo();
        main_panel.removeAll();
        dashboard = new Dashboard(userBean);
        main_panel.add(dashboard, BorderLayout.CENTER);
        SwingUtilities.updateComponentTreeUI(main_panel);
        HomeMenu = 1;
        applyTheme();
        this.setTitle("GlobeMed Health Care");
    }

    private void applyTheme() {
        if (HomeMenu == 1) {
            dashboard_btn.putClientProperty(FlatClientProperties.STYLE, ""
                    + "borderWidth:0; focusWidth:0; background:#00FF97;");
            p_records_btn.putClientProperty(FlatClientProperties.STYLE, ""
                    + "borderWidth:0; focusWidth:0; background:#FFFFFF;");
            appointments_btn.putClientProperty(FlatClientProperties.STYLE, ""
                    + "borderWidth:0; focusWidth:0; background:#FFFFFF;");
            billing_btn.putClientProperty(FlatClientProperties.STYLE, ""
                    + "borderWidth:0; focusWidth:0; background:#FFFFFF;");
            pharmacy_btn.putClientProperty(FlatClientProperties.STYLE, ""
                    + "borderWidth:0; focusWidth:0; background:#FFFFFF;");
            medical_reports_btn.putClientProperty(FlatClientProperties.STYLE, ""
                    + "borderWidth:0; focusWidth:0; background:#FFFFFF;");
            staff_btn.putClientProperty(FlatClientProperties.STYLE, ""
                    + "borderWidth:0; focusWidth:0; background:#FFFFFF;");

        } else if (HomeMenu == 2) {
            dashboard_btn.putClientProperty(FlatClientProperties.STYLE, ""
                    + "borderWidth:0; focusWidth:0; background:#FFFFFF;");
            p_records_btn.putClientProperty(FlatClientProperties.STYLE, ""
                    + "borderWidth:0; focusWidth:0; background:#00FF97;");
            appointments_btn.putClientProperty(FlatClientProperties.STYLE, ""
                    + "borderWidth:0; focusWidth:0; background:#FFFFFF;");
            billing_btn.putClientProperty(FlatClientProperties.STYLE, ""
                    + "borderWidth:0; focusWidth:0; background:#FFFFFF;");
            pharmacy_btn.putClientProperty(FlatClientProperties.STYLE, ""
                    + "borderWidth:0; focusWidth:0; background:#FFFFFF;");
            medical_reports_btn.putClientProperty(FlatClientProperties.STYLE, ""
                    + "borderWidth:0; focusWidth:0; background:#FFFFFF;");
            staff_btn.putClientProperty(FlatClientProperties.STYLE, ""
                    + "borderWidth:0; focusWidth:0; background:#FFFFFF;");
        } else if (HomeMenu == 3) {
            dashboard_btn.putClientProperty(FlatClientProperties.STYLE, ""
                    + "borderWidth:0; focusWidth:0; background:#FFFFFF;");
            p_records_btn.putClientProperty(FlatClientProperties.STYLE, ""
                    + "borderWidth:0; focusWidth:0; background:#FFFFFF;");
            appointments_btn.putClientProperty(FlatClientProperties.STYLE, ""
                    + "borderWidth:0; focusWidth:0; background:#00FF97;");
            billing_btn.putClientProperty(FlatClientProperties.STYLE, ""
                    + "borderWidth:0; focusWidth:0; background:#FFFFFF;");
            pharmacy_btn.putClientProperty(FlatClientProperties.STYLE, ""
                    + "borderWidth:0; focusWidth:0; background:#FFFFFF;");
            medical_reports_btn.putClientProperty(FlatClientProperties.STYLE, ""
                    + "borderWidth:0; focusWidth:0; background:#FFFFFF;");
            staff_btn.putClientProperty(FlatClientProperties.STYLE, ""
                    + "borderWidth:0; focusWidth:0; background:#FFFFFF;");
        } else if (HomeMenu == 4) {
            dashboard_btn.putClientProperty(FlatClientProperties.STYLE, ""
                    + "borderWidth:0; focusWidth:0; background:#FFFFFF;");
            p_records_btn.putClientProperty(FlatClientProperties.STYLE, ""
                    + "borderWidth:0; focusWidth:0; background:#FFFFFF;");
            appointments_btn.putClientProperty(FlatClientProperties.STYLE, ""
                    + "borderWidth:0; focusWidth:0; background:#FFFFFF;");
            billing_btn.putClientProperty(FlatClientProperties.STYLE, ""
                    + "borderWidth:0; focusWidth:0; background:#00FF97;");
            pharmacy_btn.putClientProperty(FlatClientProperties.STYLE, ""
                    + "borderWidth:0; focusWidth:0; background:#FFFFFF;");
            medical_reports_btn.putClientProperty(FlatClientProperties.STYLE, ""
                    + "borderWidth:0; focusWidth:0; background:#FFFFFF;");
            staff_btn.putClientProperty(FlatClientProperties.STYLE, ""
                    + "borderWidth:0; focusWidth:0; background:#FFFFFF;");
        } else if (HomeMenu == 5) {
            dashboard_btn.putClientProperty(FlatClientProperties.STYLE, ""
                    + "borderWidth:0; focusWidth:0; background:#FFFFFF;");
            p_records_btn.putClientProperty(FlatClientProperties.STYLE, ""
                    + "borderWidth:0; focusWidth:0; background:#FFFFFF;");
            appointments_btn.putClientProperty(FlatClientProperties.STYLE, ""
                    + "borderWidth:0; focusWidth:0; background:#FFFFFF;");
            billing_btn.putClientProperty(FlatClientProperties.STYLE, ""
                    + "borderWidth:0; focusWidth:0; background:#FFFFFF;");
            pharmacy_btn.putClientProperty(FlatClientProperties.STYLE, ""
                    + "borderWidth:0; focusWidth:0; background:#00FF97;");
            medical_reports_btn.putClientProperty(FlatClientProperties.STYLE, ""
                    + "borderWidth:0; focusWidth:0; background:#FFFFFF;");
            staff_btn.putClientProperty(FlatClientProperties.STYLE, ""
                    + "borderWidth:0; focusWidth:0; background:#FFFFFF;");
        } else if (HomeMenu == 6) {
            dashboard_btn.putClientProperty(FlatClientProperties.STYLE, ""
                    + "borderWidth:0; focusWidth:0; background:#FFFFFF;");
            p_records_btn.putClientProperty(FlatClientProperties.STYLE, ""
                    + "borderWidth:0; focusWidth:0; background:#FFFFFF;");
            appointments_btn.putClientProperty(FlatClientProperties.STYLE, ""
                    + "borderWidth:0; focusWidth:0; background:#FFFFFF;");
            billing_btn.putClientProperty(FlatClientProperties.STYLE, ""
                    + "borderWidth:0; focusWidth:0; background:#FFFFFF;");
            pharmacy_btn.putClientProperty(FlatClientProperties.STYLE, ""
                    + "borderWidth:0; focusWidth:0; background:#FFFFFF;");
            medical_reports_btn.putClientProperty(FlatClientProperties.STYLE, ""
                    + "borderWidth:0; focusWidth:0; background:#00FF97;");
            staff_btn.putClientProperty(FlatClientProperties.STYLE, ""
                    + "borderWidth:0; focusWidth:0; background:#FFFFFF;");
        } else if (HomeMenu == 7) {
            dashboard_btn.putClientProperty(FlatClientProperties.STYLE, ""
                    + "borderWidth:0; focusWidth:0; background:#FFFFFF;");
            p_records_btn.putClientProperty(FlatClientProperties.STYLE, ""
                    + "borderWidth:0; focusWidth:0; background:#FFFFFF;");
            appointments_btn.putClientProperty(FlatClientProperties.STYLE, ""
                    + "borderWidth:0; focusWidth:0; background:#FFFFFF;");
            billing_btn.putClientProperty(FlatClientProperties.STYLE, ""
                    + "borderWidth:0; focusWidth:0; background:#FFFFFF;");
            pharmacy_btn.putClientProperty(FlatClientProperties.STYLE, ""
                    + "borderWidth:0; focusWidth:0; background:#FFFFFF;");
            medical_reports_btn.putClientProperty(FlatClientProperties.STYLE, ""
                    + "borderWidth:0; focusWidth:0; background:#FFFFFF;");
            staff_btn.putClientProperty(FlatClientProperties.STYLE, ""
                    + "borderWidth:0; focusWidth:0; background:#00FF97;");
        }

        SwingUtilities.updateComponentTreeUI(this);
        this.revalidate();
        this.repaint();
    }

    private void userInfo() {
        name_label.setText(userBean.getFname() + " " + userBean.getLname());
        position_label.setText(userBean.getStaffRole());
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel6 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        name_label = new javax.swing.JLabel();
        position_label = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        dashboard_btn = new javax.swing.JButton();
        appointments_btn = new javax.swing.JButton();
        medical_reports_btn = new javax.swing.JButton();
        billing_btn = new javax.swing.JButton();
        logout_btn = new javax.swing.JButton();
        pharmacy_btn = new javax.swing.JButton();
        staff_btn = new javax.swing.JButton();
        p_records_btn = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel1 = new javax.swing.JLabel();
        main_panel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel6.setBackground(new java.awt.Color(204, 0, 0));
        jPanel6.setLayout(new java.awt.BorderLayout());

        jPanel2.setBackground(new java.awt.Color(0, 150, 136));
        jPanel2.setPreferredSize(new java.awt.Dimension(190, 720));

        name_label.setFont(new java.awt.Font("Poppins", 1, 16)); // NOI18N
        name_label.setForeground(new java.awt.Color(255, 255, 255));
        name_label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        name_label.setText("Mohan Chanaka");

        position_label.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        position_label.setForeground(new java.awt.Color(255, 255, 255));
        position_label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        position_label.setText("Shop Owner");

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/menu-bar (2).png"))); // NOI18N
        jLabel7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel7MouseClicked(evt);
            }
        });

        jSeparator1.setForeground(new java.awt.Color(102, 102, 102));

        dashboard_btn.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        dashboard_btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons8-dashboard-24.png"))); // NOI18N
        dashboard_btn.setText("Dashboard");
        dashboard_btn.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        dashboard_btn.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        dashboard_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dashboard_btnActionPerformed(evt);
            }
        });

        appointments_btn.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        appointments_btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons8-appointments-24.png"))); // NOI18N
        appointments_btn.setText("Appointments");
        appointments_btn.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        appointments_btn.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        appointments_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                appointments_btnActionPerformed(evt);
            }
        });

        medical_reports_btn.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        medical_reports_btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons8-medical-reports-24.png"))); // NOI18N
        medical_reports_btn.setText("Medical Reports");
        medical_reports_btn.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        medical_reports_btn.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        medical_reports_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                medical_reports_btnActionPerformed(evt);
            }
        });

        billing_btn.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        billing_btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons8-billing-24.png"))); // NOI18N
        billing_btn.setText("Billing & Invoicing");
        billing_btn.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        billing_btn.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        billing_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                billing_btnActionPerformed(evt);
            }
        });

        logout_btn.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        logout_btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons8-logout-24.png"))); // NOI18N
        logout_btn.setText("Log Out");
        logout_btn.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        logout_btn.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

        pharmacy_btn.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        pharmacy_btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons8-pharmacy-24.png"))); // NOI18N
        pharmacy_btn.setText("Pharmacy ");
        pharmacy_btn.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        pharmacy_btn.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        pharmacy_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pharmacy_btnActionPerformed(evt);
            }
        });

        staff_btn.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        staff_btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons8-doctor-24.png"))); // NOI18N
        staff_btn.setText("Staff Management");
        staff_btn.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        staff_btn.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        staff_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                staff_btnActionPerformed(evt);
            }
        });

        p_records_btn.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        p_records_btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons8-sick-24.png"))); // NOI18N
        p_records_btn.setText("Patient");
        p_records_btn.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        p_records_btn.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        p_records_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                p_records_btnActionPerformed(evt);
            }
        });

        jSeparator2.setForeground(new java.awt.Color(102, 102, 102));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/Black and Blue Simple Medical Health Logo (7) (3).png"))); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(p_records_btn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(dashboard_btn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(appointments_btn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(billing_btn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pharmacy_btn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(medical_reports_btn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(staff_btn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(logout_btn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSeparator1)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(name_label, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(position_label, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.LEADING))
                .addGap(10, 10, 10))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(57, 57, 57))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addComponent(name_label)
                .addGap(0, 0, 0)
                .addComponent(position_label)
                .addGap(18, 18, 18)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(dashboard_btn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(p_records_btn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(appointments_btn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(billing_btn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pharmacy_btn)
                .addGap(12, 12, 12)
                .addComponent(medical_reports_btn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(staff_btn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 66, Short.MAX_VALUE)
                .addComponent(logout_btn)
                .addGap(144, 144, 144))
        );

        jPanel6.add(jPanel2, java.awt.BorderLayout.LINE_START);

        main_panel.setBackground(new java.awt.Color(204, 204, 204));
        main_panel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 0, true));
        main_panel.setPreferredSize(new java.awt.Dimension(1090, 720));
        main_panel.setLayout(new java.awt.BorderLayout());
        jPanel6.add(main_panel, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel2MouseClicked

    }//GEN-LAST:event_jLabel2MouseClicked

    private void jLabel3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel3MouseClicked

    }//GEN-LAST:event_jLabel3MouseClicked

    private void jLabel4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel4MouseClicked

    }//GEN-LAST:event_jLabel4MouseClicked

    private void formWindowDeiconified(java.awt.event.WindowEvent evt) {

    }


    private void jLabel7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel7MouseClicked

        if (menu_status) {
            new Thread(() -> {
                for (int i = jPanel2.getWidth(); i >= 50; i -= 15) {
                    final int width = i;
                    SwingUtilities.invokeLater(() -> {
                        jPanel2.setPreferredSize(new Dimension(width, jPanel2.getHeight()));
                        jPanel2.revalidate();
                        jPanel2.repaint();
                    });

                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                SwingUtilities.invokeLater(() -> {
                    dashboard_btn.setText("");
                    appointments_btn.setText("");
                    p_records_btn.setText("");
                    medical_reports_btn.setText("");
                    billing_btn.setText("");
                    pharmacy_btn.setText("");
                    staff_btn.setText("");

                    logout_btn.setText("");
                    name_label.setText(" ");
                    position_label.setText(" ");
//                    light_theme_btn.setVisible(false);
//                    dark_theme_btn.setVisible(false);
                    menu_status = false;
                });
            }).start();
            menu_status = false;
        } else {
            new Thread(() -> {
                for (int i = 50; i <= 190; i += 15) {
                    final int width = i;
                    SwingUtilities.invokeLater(() -> {

                        jPanel2.setPreferredSize(new Dimension(width, jPanel2.getHeight()));

                        jPanel2.revalidate();
                        jPanel2.repaint();
                    });

                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            dashboard_btn.setText("Dashboard");
            appointments_btn.setText("Appointments");
            medical_reports_btn.setText("Medical Reports");
            billing_btn.setText("Billing & Invoicing");
            p_records_btn.setText("Patient");
            pharmacy_btn.setText("Pharmacy");
            staff_btn.setText("Staff Management");
            name_label.setText(userBean.getFname() + " " + userBean.getLname());
            position_label.setText(userBean.getStaffRole());
            logout_btn.setText("Log Out");

            menu_status = true;
        }
    }//GEN-LAST:event_jLabel7MouseClicked

    private void appointments_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_appointments_btnActionPerformed
        HomeMenu = 3;
        applyTheme();
    }//GEN-LAST:event_appointments_btnActionPerformed

    private void dashboard_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dashboard_btnActionPerformed
        HomeMenu = 1;
        applyTheme();
        main_panel.removeAll();
        dashboard = new Dashboard(userBean);
        main_panel.add(dashboard, BorderLayout.CENTER);
        SwingUtilities.updateComponentTreeUI(main_panel);

    }//GEN-LAST:event_dashboard_btnActionPerformed

    private void p_records_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_p_records_btnActionPerformed
        //Patient
        main_panel.removeAll();
        patient = new Patient(userBean);
        main_panel.add(patient, BorderLayout.CENTER);
        SwingUtilities.updateComponentTreeUI(main_panel);
        HomeMenu = 2;
        applyTheme();


    }//GEN-LAST:event_p_records_btnActionPerformed

    private void pharmacy_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pharmacy_btnActionPerformed
        HomeMenu = 5;
        applyTheme();

    }//GEN-LAST:event_pharmacy_btnActionPerformed

    private void billing_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_billing_btnActionPerformed
        HomeMenu = 4;
        applyTheme();
    }//GEN-LAST:event_billing_btnActionPerformed

    private void medical_reports_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_medical_reports_btnActionPerformed
        HomeMenu = 6;
        applyTheme();
    }//GEN-LAST:event_medical_reports_btnActionPerformed

    private void staff_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_staff_btnActionPerformed
        HomeMenu = 7;
        applyTheme();
    }//GEN-LAST:event_staff_btnActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton appointments_btn;
    private javax.swing.JButton billing_btn;
    private javax.swing.JButton dashboard_btn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JButton logout_btn;
    private javax.swing.JPanel main_panel;
    private javax.swing.JButton medical_reports_btn;
    private javax.swing.JLabel name_label;
    private javax.swing.JButton p_records_btn;
    private javax.swing.JButton pharmacy_btn;
    private javax.swing.JLabel position_label;
    private javax.swing.JButton staff_btn;
    // End of variables declaration//GEN-END:variables
}
