
package banquero;

import java.awt.event.ActionEvent;
import static java.lang.Thread.sleep;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class Simulacion extends javax.swing.JFrame {
    
    private int progress, n, m;
    private boolean allowBar;
    private int[] printSafeSeq;
 

    //Global variables for banker's algorithm
    private int[] total;         //Total resources
    private int[] available;        //Available resources
    private int[][] need;           //Requested resources
    private int[][] allocation;     //Allocated resources
    private int[][] max;            //Maximum resources to assign
    private int totalmax;
    
    Random rand = new Random();
    
    public Simulacion(int m, int n, int[] trucks, int[][] trucksPerOrder) {
        initComponents();
        
        progress = 0;
        allowBar = false;
        printSafeSeq = new int[n];
        
        this.n = n;
        this.m = m;
        
        //Initialize global vars for banker's algo
        total = new int[m];
        available = new int[m];
        need = new int[n][m];
        allocation = new int[n][m];
        max = new int[n][m];
        totalmax = 0;
        
        for(int i=0; i<m; i++){
            total[i] = trucks[i];
            available[i] = total[i];
        }
        for(int i=0; i<n; i++){
            for(int j=0; j<m; j++){
                max[i][j] = trucksPerOrder[i][j];
                need[i][j] = max[i][j];
            }
        }
        
        for (int i = 0; i<m; i++) {         //encuentra el numero maximo que pudiese tener available (menor o igual)
            if(totalmax <= total[i])
                totalmax = total[i];
        }
        
        for(int i=0; i<n; i++){             //RANDOMIZER
            for(int j=0; j<m; j++){
                if(need[i][j]==0){
                    allocation[i][j] = rand.nextInt((totalmax/2)+1);
                }else{
                    allocation[i][j] = rand.nextInt((totalmax/2)+1);  //Esto funciona relativamente bien
                                                                      
                }
                
                available[j] = available[j] - allocation[i][j];       //se crea available
                for (int k = 0; k < m; k++) {                         //se verifica que available no tenga numeros negativos
                    if(available[k] < 0)
                        available[k]= available[k] * (-1);
                }
                
                need[i][j] = max[i][j] - allocation[i][j];            //se forma la matriz need
                for (int a = 0; a < n; a++) {                         //se verifica que la matriz need no tenga numeros negativos
                    for (int b = 0; b < m; b++) {
                        if(need[a][b] < 0)
                            need[a][b] = need[a][b] * (-1);  
                    }
                }
            }
        }
        
        
        
        //GUI adjustments
        tableAdjustments();
        
        Timer timer = new Timer(1, (ActionEvent e) -> { //Update GUI
            if(allowBar == true && progress < 100){
                progress++;
                jProgressBar1.setValue(progress);
                /*try {
                    sleep(200);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Simulacion.class.getName()).log(Level.SEVERE, null, ex);
                }*/
            }
            
            for(int i=0; i<m; i++){
                jTable2.setValueAt(available[i], 0, i);
                
                for(int j=0; j<n; j++){
                    jTable3.setValueAt(allocation[j][i], j, i+1);
                    jTable4.setValueAt(need[j][i], j, i+1);
                    jTable5.setValueAt(max[j][i], j, i+1);
                }
            }
        });
        timer.start();
    }
    
    public void tableAdjustments(){
        //JTable1 - total
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( JLabel.CENTER );
        
        jTable1.setModel(new DefaultTableModel(1, m));  //Dimensiones
        jTable1.getTableHeader().setReorderingAllowed(false);
        
        for(int i=0; i<m; i++){
            jTable1.getTableHeader().getColumnModel().getColumn(i).setHeaderValue("Route"+(i+1));
            jTable1.setValueAt(total[i], 0, i);
            jTable1.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            jTable1.getTableHeader().getColumnModel().getColumn(i).setHeaderRenderer(centerRenderer);
            jTable1.getColumnModel().getColumn(i).setPreferredWidth(60);
        }
        
        //JTable2 - available
        jTable2.setModel(new DefaultTableModel(1, m));  //Dimensiones
        jTable2.getTableHeader().setReorderingAllowed(false);
        
        for(int i=0; i<m; i++){
            jTable2.getTableHeader().getColumnModel().getColumn(i).setHeaderValue("Route"+(i+1));
            jTable2.setValueAt(available[i], 0, i);
            jTable2.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            jTable2.getTableHeader().getColumnModel().getColumn(i).setHeaderRenderer(centerRenderer);
            jTable2.getColumnModel().getColumn(i).setPreferredWidth(60);
        }
        
        //JTable3 - allocated
        jTable3.setModel(new DefaultTableModel(n, m+1));  //Dimensiones
        jTable3.getTableHeader().setReorderingAllowed(false);
        jTable3.getTableHeader().getColumnModel().getColumn(0).setHeaderValue(null);
        
        for(int i=0; i<m; i++){
            jTable3.getTableHeader().getColumnModel().getColumn(i+1).setHeaderValue("Route"+(i+1));
            
            for(int j=0; j<n; j++){
                jTable3.setValueAt("Order"+(j+1), j, 0);
                jTable3.setValueAt(allocation[j][i], j, i+1);
                jTable3.getColumnModel().getColumn(i+1).setCellRenderer(centerRenderer);
                jTable3.getTableHeader().getColumnModel().getColumn(i+1).setHeaderRenderer(centerRenderer);
                jTable3.getColumnModel().getColumn(i+1).setPreferredWidth(60);
                
                //Column 0 - Order #
                jTable3.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
                jTable3.getTableHeader().getColumnModel().getColumn(0).setHeaderRenderer(centerRenderer);
                jTable3.getColumnModel().getColumn(0).setPreferredWidth(60);
            }
        }
        
        //JTable4 - need
        jTable4.setModel(new DefaultTableModel(n, m+1));  //Dimensiones
        jTable4.getTableHeader().setReorderingAllowed(false);
        jTable4.getTableHeader().getColumnModel().getColumn(0).setHeaderValue(null);
        
        for(int i=0; i<m; i++){
            jTable4.getTableHeader().getColumnModel().getColumn(i+1).setHeaderValue("Route"+(i+1));
            
            for(int j=0; j<n; j++){
                jTable4.setValueAt("Order"+(j+1), j, 0);
                jTable4.setValueAt(need[j][i], j, i+1);
                jTable4.getColumnModel().getColumn(i+1).setCellRenderer(centerRenderer);
                jTable4.getTableHeader().getColumnModel().getColumn(i+1).setHeaderRenderer(centerRenderer);
                jTable4.getColumnModel().getColumn(i+1).setPreferredWidth(60);
                
                //Column 0 - Order #
                jTable4.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
                jTable4.getTableHeader().getColumnModel().getColumn(0).setHeaderRenderer(centerRenderer);
                jTable4.getColumnModel().getColumn(0).setPreferredWidth(60);
            }
        }
        
        //JTable5 - need
        jTable5.setModel(new DefaultTableModel(n, m+1));  //Dimensiones
        jTable5.getTableHeader().setReorderingAllowed(false);
        jTable5.getTableHeader().getColumnModel().getColumn(0).setHeaderValue(null);
        
        for(int i=0; i<m; i++){
            jTable5.getTableHeader().getColumnModel().getColumn(i+1).setHeaderValue("Route"+(i+1));
            
            for(int j=0; j<n; j++){
                jTable5.setValueAt("Order"+(j+1), j, 0);
                jTable5.setValueAt(max[j][i], j, i+1);
                jTable5.getColumnModel().getColumn(i+1).setCellRenderer(centerRenderer);
                jTable5.getTableHeader().getColumnModel().getColumn(i+1).setHeaderRenderer(centerRenderer);
                jTable5.getColumnModel().getColumn(i+1).setPreferredWidth(60);
                
                //Column 0 - Order #
                jTable5.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
                jTable5.getTableHeader().getColumnModel().getColumn(0).setHeaderRenderer(centerRenderer);
                jTable5.getColumnModel().getColumn(0).setPreferredWidth(60);
            }
        }
        
        //JTable6 - safe sequence
        jTable6.setModel(new DefaultTableModel(n, 2));  //Dimensiones
        jTable6.getTableHeader().setReorderingAllowed(false);
        jTable6.getTableHeader().getColumnModel().getColumn(0).setHeaderValue(null);
        jTable6.getTableHeader().getColumnModel().getColumn(1).setHeaderValue("SAFE SEQUENCE");
        jTable6.getColumnModel().getColumn(0).setPreferredWidth(120);
        jTable6.getColumnModel().getColumn(1).setPreferredWidth(280);
        jTable6.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        jTable6.getTableHeader().getColumnModel().getColumn(0).setHeaderRenderer(centerRenderer);
        jTable6.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        jTable6.getTableHeader().getColumnModel().getColumn(1).setHeaderRenderer(centerRenderer);
        
        for(int j=0; j<n; j++){
            jTable6.setValueAt("Order #", j, 0);
        }
        
    }
    
    public boolean safetyAlgo(){
        boolean isSafe = true;
        boolean confirm = true;
        int k=0;
        
        //STEP 1
        int[] work = new int[m];
        boolean[] finish = new boolean[n];
        
        for(int j=0; j<m; j++){
            work[j] = available[j];
        }
        for(int i=0; i<n; i++){
            finish[i] = false;
        }
        
        for(int i=0; i<n; i++){
            //STEP 2
            if(finish[i] == false){   //find an i where false
                System.out.println("primero");
                for(int j=0; j<m; j++){
                    if(need[i][j] > work[j]){  //opposite, need is greater than work - not safe
                        System.out.println("segundo");
                        isSafe = false;
                    }
                }
            }
            else{
                isSafe = false;
            }
            
            if(isSafe == true){ //If an i exists (STEP 3)
                for(int j=0; j<m; j++){
                    work[j] += allocation[i][j];
                }
                    
                System.out.println("proceso "+(i+1));
                finish[i] = true;
                
                printSafeSeq[k] = i;
                k++;
                
                i=-1; //go to step 2
            }
            
            isSafe = true;
        }
        
        //STEP 4
        for(int i=0; i<n; i++){
            if(finish[i] == false){
                confirm = false;
            }
        }
        
        return confirm;
    }
    
    /*
    public void requestAlgo(int i){
        boolean aux = true;
        boolean aux2 = true;
        
        int[] request;
        request = new int[m];
        
        
        for(int j=0; j<m; j++){
            request[j] = need[i][j] - allocation[i][j];
        }
        
        
        //STEP 1
        for(int j=0; j<m; j++){
            if(request[j] > need[i][j]){
                aux = false;    //condition violated
            }
        }
           
        //STEP 2
        if(aux == true){
            for(int j=0; j<m; j++){
                if(request[j] > available[j]){
                    aux2 = false;   //condition2 violated
                }
            }
        }
        else{
            //error condition - maximum claim exceeded (frenar)
        }
        
        //STEP 3
        if(aux2 == true){
            for(int j=0; j<m; j++){
                available[j] = available[j] - request[j];
                allocation[i][j] = allocation[i][j] + request[j];
                need[i][j] = need[i][j] - request[j];
            }
        }
        else{
            //Pi must wait, not enough resources available (saltar)
        }
    }*/
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jProgressBar1 = new javax.swing.JProgressBar();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTable4 = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTable5 = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        jTable6 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Simulation");
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel3.setBackground(new java.awt.Color(51, 51, 51));

        jProgressBar1.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jProgressBar1.setForeground(new java.awt.Color(0, 0, 0));
        jProgressBar1.setStringPainted(true);

        jButton1.setFont(new java.awt.Font("Eras Demi ITC", 0, 16)); // NOI18N
        jButton1.setText("Exit");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setFont(new java.awt.Font("Eras Demi ITC", 0, 16)); // NOI18N
        jButton2.setText("Start");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(225, 225, 225)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(61, 61, 61)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(61, 61, 61)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(225, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(30, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 690, 1250, 90));

        jTabbedPane2.setBackground(new java.awt.Color(51, 51, 51));

        jPanel2.setBackground(new java.awt.Color(51, 51, 51));

        jLabel1.setFont(new java.awt.Font("Eras Demi ITC", 0, 28)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 153, 0));
        jLabel1.setText("Total");

        jLabel2.setFont(new java.awt.Font("Eras Demi ITC", 0, 28)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 153, 0));
        jLabel2.setText("Available");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTable1.setEnabled(false);
        jTable1.setFocusable(false);
        jTable1.setRowHeight(22);
        jScrollPane2.setViewportView(jTable1);

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable2.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTable2.setEnabled(false);
        jTable2.setRowHeight(22);
        jScrollPane3.setViewportView(jTable2);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(65, 65, 65)
                        .addComponent(jLabel1))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(jLabel2)))
                .addGap(40, 40, 40)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 943, Short.MAX_VALUE)
                    .addComponent(jScrollPane3))
                .addContainerGap(565, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(160, 160, 160)
                        .addComponent(jLabel1))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(141, 141, 141)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(202, 202, 202)
                        .addComponent(jLabel2))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(185, 185, 185)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(400, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Trucks per route (Total & Available)", jPanel2);

        jPanel1.setBackground(new java.awt.Color(51, 51, 51));

        jTable4.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable4.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTable4.setEnabled(false);
        jTable4.setFocusable(false);
        jTable4.setRowHeight(22);
        jScrollPane5.setViewportView(jTable4);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 1142, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(532, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 484, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(357, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Trucks per order (Need)", jPanel1);

        jPanel4.setBackground(new java.awt.Color(51, 51, 51));

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable3.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTable3.setEnabled(false);
        jTable3.setRequestFocusEnabled(false);
        jTable3.setRowHeight(22);
        jScrollPane4.setViewportView(jTable3);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 1142, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(532, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 484, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(357, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Trucks per order (Allocated)", jPanel4);

        jPanel5.setBackground(new java.awt.Color(51, 51, 51));

        jTable5.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable5.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTable5.setEnabled(false);
        jTable5.setFocusable(false);
        jTable5.setRequestFocusEnabled(false);
        jTable5.setRowHeight(22);
        jScrollPane6.setViewportView(jTable5);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 1142, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(532, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 484, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(357, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Trucks per Order (Max)", jPanel5);

        jPanel6.setBackground(new java.awt.Color(51, 51, 51));

        jTable6.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null},
                {null},
                {null}
            },
            new String [] {
                "Title 1"
            }
        ));
        jTable6.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTable6.setEnabled(false);
        jTable6.setFocusable(false);
        jTable6.setRequestFocusEnabled(false);
        jScrollPane7.setViewportView(jTable6);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(427, 427, 427)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(883, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 568, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(266, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Safe Sequence", jPanel6);

        jScrollPane1.setViewportView(jTabbedPane2);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(3, 3, 1250, 690));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        //Algorithms
        jButton2.setEnabled(false);
        allowBar = true;
        
        if(safetyAlgo()){
            //PRINT SAFE SEQUENCE
            for(int j=0; j<n; j++){
                jTable6.setValueAt((printSafeSeq[j]+1), j, 1);
            }
        } else {
            //No safe sequence
            JOptionPane.showMessageDialog (null, "No safe sequence exists for the given conditions", "Error", JOptionPane.ERROR_MESSAGE);
        }
        
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        //Exit
        int reply = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit the program?", "", JOptionPane.YES_NO_OPTION);

        if(reply == JOptionPane.YES_OPTION){
            System.exit(0);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

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
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Simulacion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Simulacion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Simulacion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Simulacion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    private javax.swing.JTable jTable4;
    private javax.swing.JTable jTable5;
    private javax.swing.JTable jTable6;
    // End of variables declaration//GEN-END:variables
}
