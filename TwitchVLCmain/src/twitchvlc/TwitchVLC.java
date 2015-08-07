package twitchvlc;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import java.awt.AWTException;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

public final class TwitchVLC extends javax.swing.JFrame {

    int posX=0,posY=0;
    
    static ConcurrentSkipListSet<SerialStream> favs = new ConcurrentSkipListSet<>();
    
    static TwitchVLC mainframe;
    
    
    public static final String VOICENAME = "kevin16";
    public static final VoiceManager voiceManager = VoiceManager.getInstance();
    public static Voice voice;
    
    //private NodeList nList=null;
    
    private boolean traySupport=true;
    
    final public TwitchVLC getThis() {
        return this;
    } 
    public TwitchVLC() {
        voice = voiceManager.getVoice(VOICENAME);
        voice.allocate();
        
        
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(TwitchVLC.class.getName()).log(Level.SEVERE, null, ex);
        }
        //this.setUndecorated(true);
        
        this.setIconImage(new javax.swing.ImageIcon(getClass().getResource("/twitchvlc/twitch32.png")).getImage());
        
        //final TwitchVLC vl = this;
        
        // using component resize allows for precise control
        /*this.addComponentListener(new ComponentAdapter() {
            // polygon points non-inclusive
            // {0,0} {350,0} {350,960} {1600,960} {1600,1200} {0,1200}
            
            //int[] xpoints = {0,200,630,630,0};
            //int[] ypoints = {0,50,0,450,450};
            
            int[] xpoints = {20,440, 440 , 465, 474, 526, 526, 568 , 568 , 579 , 579, 588 , 599 , 608 , 632 , 632 , 645 , 657 , 657 ,650,650,20};
            int[] ypoints = {20,20 , 7   , 7  , 17 , 17 , 7  , 7   , 17  , 17  , 25 , 17  , 17  , 7   , 7   , 17  , 17  , 28  , 65  , 70,470,470};
            
            @Override
            public void componentResized(ComponentEvent evt)
            {  
                // create the polygon (L-Shape)
                Shape shape = new Polygon(xpoints, ypoints, xpoints.length);

                // set the window shape
                AWTUtilities.setWindowShape(vl, shape);
            }
        });*/
        
        
        
        this.setLocationRelativeTo(null);
        initComponents();
        
        searchScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        favouritesScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        this.setLocation(((int)this.getLocation().getX())-this.getWidth()/2, ((int)this.getLocation().getY())-this.getHeight()/2);
        
        jTabbedPane1.setUI(new BasicTabbedPaneUI() {  
            @Override  
            protected int calculateTabAreaHeight(int tab_placement, int run_count, int max_tab_height) {  
                //if (jTabbedPane1.getTabCount() > 1)
                //    return super.calculateTabAreaHeight(tab_placement, run_count, max_tab_height);  
                //else  
                    return -2;  
            }  
        });
        
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                //setExtendedState(Frame.ICONIFIED);
                
                if(traySupport) {
                    mainframe.setVisible(false);
                }
                else{
                    System.exit(0);
                }
            }
        });
        
        this.setMinimumSize(new Dimension(740,536));
        this.addComponentListener(new ComponentAdapter(){
            @Override
            public void componentResized(ComponentEvent e){
                Dimension d=getSize();
                Dimension minD=getMinimumSize();
                if(d.width<minD.width) {
                    d.width=minD.width;
                }
                if(d.height<minD.height) {
                    d.height=minD.height;
                }
                setSize(d);
                
            }
        });
        
        setUpSystemTray();
        
        loadFavsFromFile();
    }
    
    TrayIcon trayIcon = null;
    public void setUpSystemTray() {
        if (SystemTray.isSupported()) {
            // get the SystemTray instance
            SystemTray tray = SystemTray.getSystemTray();
            // load an image
            Image image = new javax.swing.ImageIcon(getClass().getResource("/twitchvlc/twitch16.png")).getImage();
            // create a action listener to listen for default action executed on the tray icon
            
            
            // create a popup menu
            PopupMenu popup = new PopupMenu();
            
            // create menu item for the default action
            MenuItem openApp = new MenuItem("Open");
            openApp.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    setVisible(true);
                }
            });
            
            MenuItem closeApp = new MenuItem("Exit");
            closeApp.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });
            
            
            popup.add(openApp);
            popup.add(closeApp);
            
            // construct a TrayIcon
            trayIcon = new TrayIcon(image, "Twitch VLC", popup);
            // set the TrayIcon properties
            trayIcon.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    mainframe.setVisible(!mainframe.isVisible());
                }
            });
            
            // add the tray image
            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                System.err.println(e);
            }
        } else {
            // disable tray option in your application or
            // perform other actions
            traySupport=false;
        }
        // ...
        // some time later
        // the application state has changed - update the image
        if (trayIcon != null) {
            trayIcon.setImage(new javax.swing.ImageIcon(getClass().getResource("/twitchvlc/twitch16.png")).getImage());
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainContainerPanel = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        favouritesPanel = new javax.swing.JPanel();
        favouritesScrollPane = new javax.swing.JScrollPane();
        pnl = new javax.swing.JPanel();
        offPanel = new javax.swing.JPanel();
        mainPanel = new javax.swing.JPanel();
        favouritesHeader = new javax.swing.JPanel();
        channelTextField = new javax.swing.JTextField();
        mainSearchPanel = new javax.swing.JPanel();
        searchHeaderPanel = new javax.swing.JPanel();
        searchButton = new javax.swing.JButton();
        gameSearch = new javax.swing.JComboBox();
        searchScrollPane = new javax.swing.JScrollPane();
        searchResultsPanel = new javax.swing.JPanel();
        searchNextButton = new javax.swing.JButton();
        searchPrevButton = new javax.swing.JButton();
        searchFirstButton = new javax.swing.JButton();
        searchStatusLabel = new javax.swing.JLabel();
        rightPanel = new javax.swing.JPanel();
        currentChatButton = new javax.swing.JButton();
        currentName = new javax.swing.JLabel();
        currentWatchButton1 = new javax.swing.JButton();
        currentGame = new javax.swing.JLabel();
        currentTitle = new javax.swing.JTextArea();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        favouriteButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("TwitchVLC");
        setBackground(new java.awt.Color(51, 51, 51));

        mainContainerPanel.setBackground(new java.awt.Color(13, 13, 13));

        jTabbedPane1.setBackground(new java.awt.Color(13, 13, 13));
        jTabbedPane1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTabbedPane1.setFocusable(false);
        jTabbedPane1.setPreferredSize(new java.awt.Dimension(395, 525));

        favouritesPanel.setPreferredSize(new java.awt.Dimension(394, 495));

        favouritesScrollPane.setBorder(null);
        favouritesScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        favouritesScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        favouritesScrollPane.setOpaque(false);

        pnl.setLayout(new java.awt.BorderLayout());

        offPanel.setBackground(new java.awt.Color(13, 13, 13));
        offPanel.setLayout(new javax.swing.BoxLayout(offPanel, javax.swing.BoxLayout.PAGE_AXIS));
        pnl.add(offPanel, java.awt.BorderLayout.PAGE_END);

        mainPanel.setBackground(new java.awt.Color(13, 13, 13));
        mainPanel.setLayout(new javax.swing.BoxLayout(mainPanel, javax.swing.BoxLayout.PAGE_AXIS));
        pnl.add(mainPanel, java.awt.BorderLayout.CENTER);

        favouritesScrollPane.setViewportView(pnl);

        favouritesHeader.setBackground(new java.awt.Color(13, 13, 13));
        favouritesHeader.setPreferredSize(new java.awt.Dimension(394, 35));

        channelTextField.setBackground(new java.awt.Color(102, 102, 102));
        channelTextField.setForeground(new java.awt.Color(255, 255, 255));
        channelTextField.setBorder(null);
        channelTextField.setCaretColor(new java.awt.Color(255, 255, 255));
        channelTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                channelTextFieldKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout favouritesHeaderLayout = new javax.swing.GroupLayout(favouritesHeader);
        favouritesHeader.setLayout(favouritesHeaderLayout);
        favouritesHeaderLayout.setHorizontalGroup(
            favouritesHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(favouritesHeaderLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(channelTextField)
                .addGap(5, 5, 5))
        );
        favouritesHeaderLayout.setVerticalGroup(
            favouritesHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(favouritesHeaderLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(channelTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE)
                .addGap(5, 5, 5))
        );

        javax.swing.GroupLayout favouritesPanelLayout = new javax.swing.GroupLayout(favouritesPanel);
        favouritesPanel.setLayout(favouritesPanelLayout);
        favouritesPanelLayout.setHorizontalGroup(
            favouritesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(favouritesHeader, javax.swing.GroupLayout.DEFAULT_SIZE, 495, Short.MAX_VALUE)
            .addComponent(favouritesScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 495, Short.MAX_VALUE)
        );
        favouritesPanelLayout.setVerticalGroup(
            favouritesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(favouritesPanelLayout.createSequentialGroup()
                .addComponent(favouritesHeader, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(favouritesScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 438, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Favourites", new javax.swing.ImageIcon(getClass().getResource("/twitchvlc/heart-icon.png")), favouritesPanel); // NOI18N

        mainSearchPanel.setBackground(new java.awt.Color(255, 255, 255));

        searchHeaderPanel.setBackground(new java.awt.Color(13, 13, 13));
        searchHeaderPanel.setMinimumSize(new java.awt.Dimension(30, 40));
        searchHeaderPanel.setPreferredSize(new java.awt.Dimension(371, 50));
        searchHeaderPanel.setRequestFocusEnabled(false);

        searchButton.setText("Search");
        searchButton.setFocusable(false);
        searchButton.setOpaque(false);
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });

        gameSearch.setBackground(new java.awt.Color(102, 102, 102));
        gameSearch.setEditable(true);
        gameSearch.setForeground(new java.awt.Color(255, 255, 255));
        gameSearch.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "All", "Dota 2", "Hearthstone: Heroes of Warcraft", "DayZ", "World of Warcraft: Mists of Pandaria", "Counter-Strike: Global Offensive", "StarCraft II: Heart of the Swarm", "Minecraft", "Diablo III", "Injustice: Gods Among Us", "Call of Duty: Black Ops II", "League of Legends" }));
        gameSearch.setBorder(null);
        gameSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                gameSearchKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout searchHeaderPanelLayout = new javax.swing.GroupLayout(searchHeaderPanel);
        searchHeaderPanel.setLayout(searchHeaderPanelLayout);
        searchHeaderPanelLayout.setHorizontalGroup(
            searchHeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(searchHeaderPanelLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(gameSearch, 0, 414, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(searchButton)
                .addGap(5, 5, 5))
        );
        searchHeaderPanelLayout.setVerticalGroup(
            searchHeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(searchHeaderPanelLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(searchHeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(gameSearch)
                    .addComponent(searchButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        searchScrollPane.setBorder(null);
        searchScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        searchScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        searchResultsPanel.setBackground(new java.awt.Color(30, 30, 30));
        searchResultsPanel.setLayout(new javax.swing.BoxLayout(searchResultsPanel, javax.swing.BoxLayout.PAGE_AXIS));
        searchScrollPane.setViewportView(searchResultsPanel);

        searchNextButton.setText(">");
        searchNextButton.setEnabled(false);
        searchNextButton.setFocusable(false);
        searchNextButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        searchNextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchNextButtonActionPerformed(evt);
            }
        });

        searchPrevButton.setText("<");
        searchPrevButton.setEnabled(false);
        searchPrevButton.setFocusable(false);
        searchPrevButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        searchPrevButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchPrevButtonActionPerformed(evt);
            }
        });

        searchFirstButton.setText("<<");
        searchFirstButton.setEnabled(false);
        searchFirstButton.setFocusable(false);
        searchFirstButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        searchFirstButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchFirstButtonActionPerformed(evt);
            }
        });

        searchStatusLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        searchStatusLabel.setText("Ready");

        javax.swing.GroupLayout mainSearchPanelLayout = new javax.swing.GroupLayout(mainSearchPanel);
        mainSearchPanel.setLayout(mainSearchPanelLayout);
        mainSearchPanelLayout.setHorizontalGroup(
            mainSearchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(searchHeaderPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 495, Short.MAX_VALUE)
            .addComponent(searchScrollPane)
            .addGroup(mainSearchPanelLayout.createSequentialGroup()
                .addComponent(searchFirstButton, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(searchPrevButton, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(45, 45, 45)
                .addComponent(searchStatusLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(searchNextButton, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        mainSearchPanelLayout.setVerticalGroup(
            mainSearchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainSearchPanelLayout.createSequentialGroup()
                .addComponent(searchHeaderPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(searchScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 414, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addGroup(mainSearchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(searchFirstButton, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchPrevButton, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchStatusLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchNextButton, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jTabbedPane1.addTab("Browse", new javax.swing.ImageIcon(getClass().getResource("/twitchvlc/magnifier.png")), mainSearchPanel); // NOI18N

        rightPanel.setBackground(new java.awt.Color(51, 51, 51));
        rightPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        currentChatButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/twitchvlc/chat.png"))); // NOI18N
        currentChatButton.setText("Chat");
        currentChatButton.setFocusPainted(false);
        currentChatButton.setFocusable(false);
        currentChatButton.setIconTextGap(2);
        currentChatButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        currentChatButton.setOpaque(false);
        currentChatButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                currentChatButtonActionPerformed(evt);
            }
        });
        rightPanel.add(currentChatButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 140, 110, 30));

        currentName.setBackground(new java.awt.Color(255, 255, 255));
        currentName.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        currentName.setForeground(new java.awt.Color(255, 255, 255));
        currentName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        rightPanel.add(currentName, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 30, 220, 30));

        currentWatchButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/twitchvlc/i1.png"))); // NOI18N
        currentWatchButton1.setText("Web");
        currentWatchButton1.setToolTipText("");
        currentWatchButton1.setFocusPainted(false);
        currentWatchButton1.setFocusable(false);
        currentWatchButton1.setIconTextGap(2);
        currentWatchButton1.setMargin(new java.awt.Insets(0, 0, 0, 0));
        currentWatchButton1.setOpaque(false);
        currentWatchButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                currentWatchButton1ActionPerformed(evt);
            }
        });
        rightPanel.add(currentWatchButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 140, 110, 30));

        currentGame.setBackground(new java.awt.Color(255, 255, 255));
        currentGame.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        currentGame.setForeground(new java.awt.Color(255, 255, 255));
        currentGame.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        rightPanel.add(currentGame, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 50, 220, 20));

        currentTitle.setEditable(false);
        currentTitle.setColumns(20);
        currentTitle.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        currentTitle.setForeground(new java.awt.Color(255, 255, 255));
        currentTitle.setLineWrap(true);
        currentTitle.setRows(3);
        currentTitle.setWrapStyleWord(true);
        currentTitle.setCaretColor(new java.awt.Color(255, 255, 255));
        currentTitle.setDisabledTextColor(new java.awt.Color(255, 255, 255));
        currentTitle.setEnabled(false);
        currentTitle.setFocusable(false);
        currentTitle.setHighlighter(null);
        currentTitle.setOpaque(false);
        rightPanel.add(currentTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, 200, 70));

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/twitchvlc/twitch32.png"))); // NOI18N
        jButton1.setText("Watch!");
        jButton1.setFocusPainted(false);
        jButton1.setFocusable(false);
        jButton1.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton1.setOpaque(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        rightPanel.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 170, 220, 60));

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/twitchvlc/heart-icon.png"))); // NOI18N
        jButton2.setText("Favourites");
        jButton2.setFocusPainted(false);
        jButton2.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton2.setOpaque(false);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        rightPanel.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(28, -3, 100, 34));

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/twitchvlc/magnifier.png"))); // NOI18N
        jButton3.setText("Search");
        jButton3.setFocusPainted(false);
        jButton3.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton3.setOpaque(false);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        rightPanel.add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(124, -3, 96, 34));

        favouriteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/twitchvlc/heart-icon.png"))); // NOI18N
        favouriteButton.setFocusable(false);
        favouriteButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        favouriteButton.setOpaque(false);
        favouriteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                favouriteButtonActionPerformed(evt);
            }
        });
        rightPanel.add(favouriteButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(-3, -3, 34, 34));

        javax.swing.GroupLayout mainContainerPanelLayout = new javax.swing.GroupLayout(mainContainerPanel);
        mainContainerPanel.setLayout(mainContainerPanelLayout);
        mainContainerPanelLayout.setHorizontalGroup(
            mainContainerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainContainerPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(rightPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
        mainContainerPanelLayout.setVerticalGroup(
            mainContainerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainContainerPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(mainContainerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rightPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 498, Short.MAX_VALUE)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(0, 0, 0))
        );

        jTabbedPane1.getAccessibleContext().setAccessibleDescription("");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainContainerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainContainerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void favouriteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_favouriteButtonActionPerformed
        if(channelTextField.getText().equals("")) {
            return;
        }
        
        StreamItem si = new StreamItem(channelTextField.getText(),true);
        si.setSize(StreamItem.default_width, 75);
        mainPanel.add(si);
        si.updateStream();
        channelTextField.setText("");
        
        updateFavouriteFile();
        
    }//GEN-LAST:event_favouriteButtonActionPerformed

    private void currentChatButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_currentChatButtonActionPerformed
        if(currentName.getText().equals("")) {
            return;
        }
        if(currentGame.getText().equals("Offline")) {
            return;
        }
        
        try {
            openWebpage(new URL("http://www.twitch.tv/chat/embed?channel="+currentName.getText()+"&popout_chat=true"));
        } catch (MalformedURLException ex) {
            Logger.getLogger(TwitchVLC.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_currentChatButtonActionPerformed

    private void channelTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_channelTextFieldKeyReleased
        if(evt.getKeyCode()==KeyStroke.getKeyStroke( "ENTER" ).getKeyCode()) {
            favouriteButtonActionPerformed(null);
        }
    }//GEN-LAST:event_channelTextFieldKeyReleased

    private void currentWatchButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_currentWatchButton1ActionPerformed
        if(currentName.getText().equals("")) {
            return;
        }
        if(currentGame.getText().equals("Offline")) {
            return;
        }
        
        try {
            openWebpage(new URL("http://www.twitch.tv/"+currentName.getText()));
        } catch (MalformedURLException ex) {
            Logger.getLogger(TwitchVLC.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_currentWatchButton1ActionPerformed
    
    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        searchStatusLabel.setText("Loading...");
        searchButton.setEnabled(false);
        searchNextButton.setEnabled(false);
        searchPrevButton.setEnabled(false);
        searchFirstButton.setEnabled(false);
        currentStart=0;
        new Thread() {
            @Override
            public void run() {
                String urltext;
                if(gameSearch.getSelectedItem().toString().trim().replace(" ", "%20").equalsIgnoreCase("All") || gameSearch.getSelectedItem().toString().trim().replace(" ", "%20").equals("") || gameSearch.getSelectedItem().toString().trim().replace(" ", "%20").equals("*")) {
                    //url = new URL("http://api.justin.tv/api/stream/list.xml");
                    urltext = "https://api.twitch.tv/kraken/streams";
                }
                else {
                    //url = new URL("http://api.justin.tv/api/stream/list.xml?meta_game="+gameSearch.getSelectedItem().toString().trim().replace(" ", "%20"));
                    urltext = "https://api.twitch.tv/kraken/streams?game="+gameSearch.getSelectedItem().toString().trim().replace(" ", "%20");
                }

                String jsontext = StreamItem.getJSON(urltext);
                
                
                JsonParser jp = new JsonParser();
                JsonObject jo = (JsonObject)jp.parse(jsontext);
                JsonArray streams = jo.getAsJsonArray("streams");
                
                
                populateSearchBox(25, streams);

                searchNextButton.setEnabled(true);
            }
        }.start();
    }//GEN-LAST:event_searchButtonActionPerformed
    
    private int currentStart=0;
    
    private void populateSearchBox(int length, JsonArray streams) {
        searchResultsPanel.removeAll();
        System.out.println(0+", "+streams.size());
        for(int i=0;i<Math.min(length,streams.size());i++) {
            //StreamItem si = new StreamItem(((JsonObject)((JsonObject)streams.get(i)).get("channel")).get("display_name").getAsString(),false);
            StreamItem si = new StreamItem(((JsonObject)((JsonObject)streams.get(i)).get("channel")).get("name").getAsString(),false);
            si.setSize(StreamItem.default_width, 75);
            //mainPanel.add(si);
            
            mainPanel.add(si);
            si.updateStream(true,streams.get(i));  // <-- DATA HERE
            searchResultsPanel.add(si);
            
            
            
            if(i>=streams.size()) {
                searchNextButton.setEnabled(false);
            }
        }
        
        searchStatusLabel.setText("Ready");
        searchButton.setEnabled(true);
    }
    
    private void gameSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_gameSearchKeyReleased
        if(evt.getKeyCode()==KeyStroke.getKeyStroke("ENTER").getKeyCode()) {
            searchButtonActionPerformed(null);
        }
    }//GEN-LAST:event_gameSearchKeyReleased

    private void searchFirstButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchFirstButtonActionPerformed
        currentStart=0;
        searchPrevButton.setEnabled(false);
        searchFirstButton.setEnabled(false);
        searchNextButton.setEnabled(true);
            String urltext;
            if(gameSearch.getSelectedItem().toString().trim().replace(" ", "%20").equalsIgnoreCase("All") || gameSearch.getSelectedItem().toString().trim().replace(" ", "%20").equals("") || gameSearch.getSelectedItem().toString().trim().replace(" ", "%20").equals("*")) {
                //url = new URL("http://api.justin.tv/api/stream/list.xml");
                urltext = "https://api.twitch.tv/kraken/streams";
            }
            else {
                //url = new URL("http://api.justin.tv/api/stream/list.xml?meta_game="+gameSearch.getSelectedItem().toString().trim().replace(" ", "%20"));
                urltext = "https://api.twitch.tv/kraken/streams?game="+gameSearch.getSelectedItem().toString().trim().replace(" ", "%20");
            }

            String jsontext = StreamItem.getJSON(urltext);


            JsonParser jp = new JsonParser();
            JsonObject jo = (JsonObject)jp.parse(jsontext);
            JsonArray streams = jo.getAsJsonArray("streams");
        populateSearchBox(25,streams);
    }//GEN-LAST:event_searchFirstButtonActionPerformed

    private void searchNextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchNextButtonActionPerformed
        currentStart+=25;
        searchPrevButton.setEnabled(true);
        searchFirstButton.setEnabled(true);
            String urltext;
            if(gameSearch.getSelectedItem().toString().trim().replace(" ", "%20").equalsIgnoreCase("All") || gameSearch.getSelectedItem().toString().trim().replace(" ", "%20").equals("") || gameSearch.getSelectedItem().toString().trim().replace(" ", "%20").equals("*")) {
                //url = new URL("http://api.justin.tv/api/stream/list.xml");
                urltext = "https://api.twitch.tv/kraken/streams?offset="+currentStart;
            }
            else {
                //url = new URL("http://api.justin.tv/api/stream/list.xml?meta_game="+gameSearch.getSelectedItem().toString().trim().replace(" ", "%20"));
                urltext = "https://api.twitch.tv/kraken/streams?offset="+currentStart+"&game="+gameSearch.getSelectedItem().toString().trim().replace(" ", "%20");
            }

            String jsontext = StreamItem.getJSON(urltext);


            JsonParser jp = new JsonParser();
            JsonObject jo = (JsonObject)jp.parse(jsontext);
            JsonArray streams = jo.getAsJsonArray("streams");
        populateSearchBox(25,streams);
    }//GEN-LAST:event_searchNextButtonActionPerformed

    private void searchPrevButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchPrevButtonActionPerformed
        currentStart-=25;
        if(currentStart<=0) {
            currentStart=0;
            searchPrevButton.setEnabled(false);
            searchFirstButton.setEnabled(false);
        }
        searchNextButton.setEnabled(true);
            String urltext;
            if(gameSearch.getSelectedItem().toString().trim().replace(" ", "%20").equalsIgnoreCase("All") || gameSearch.getSelectedItem().toString().trim().replace(" ", "%20").equals("") || gameSearch.getSelectedItem().toString().trim().replace(" ", "%20").equals("*")) {
                //url = new URL("http://api.justin.tv/api/stream/list.xml");
                urltext = "https://api.twitch.tv/kraken/streams?offset="+currentStart;
            }
            else {
                //url = new URL("http://api.justin.tv/api/stream/list.xml?meta_game="+gameSearch.getSelectedItem().toString().trim().replace(" ", "%20"));
                urltext = "https://api.twitch.tv/kraken/streams?offset="+currentStart+"&game="+gameSearch.getSelectedItem().toString().trim().replace(" ", "%20");
            }

            String jsontext = StreamItem.getJSON(urltext);


            JsonParser jp = new JsonParser();
            JsonObject jo = (JsonObject)jp.parse(jsontext);
            JsonArray streams = jo.getAsJsonArray("streams");
        populateSearchBox(25,streams);
    }//GEN-LAST:event_searchPrevButtonActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        try {
            Runtime.getRuntime().exec("cmd /c start /B data\\tbv\\TwitchBrowser.exe "+currentName.getText().trim());
        } catch (IOException ex) {
            Logger.getLogger(TwitchVLC.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        jTabbedPane1.setSelectedIndex(0);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        jTabbedPane1.setSelectedIndex(1);
    }//GEN-LAST:event_jButton3ActionPerformed
    
    
    java.util.concurrent.ConcurrentHashMap<String,Integer> games;    
    public void setComboItems(final JComboBox jlc) {
        new Thread() {
            @Override
            public void run() {
                Object selobj = jlc.getSelectedItem();
                games = new java.util.concurrent.ConcurrentHashMap<>();
                
                
                String jsontext = StreamItem.getJSON("https://api.twitch.tv/kraken/games/top?limit=25&offset=0");
                JsonParser jp = new JsonParser();
                JsonObject jo = (JsonObject)jp.parse(jsontext);
                
                JsonArray topgames = jo.getAsJsonArray("top");
                
                for(int i=0;i<topgames.size();i++) {
                    String gamename = ((JsonObject)((JsonObject)topgames.get(i)).get("game")).get("name").getAsString();
                    games.put(gamename.replace("%20", " "), ((JsonObject)topgames.get(i)).get("viewers").getAsInt());
                }
                
                
                List<String> gamesx = new ArrayList<>();
                gamesx.add("All");
                while(games.size()>0) {
                    int highest=-1;
                    String str="";
                    for(String gna: games.keySet()) {
                        if(games.get(gna)>highest) {
                            highest=games.get(gna);
                            str=gna;
                        }
                    }
                    gamesx.add(str);
                    games.remove(str);
                }
                
                jlc.setModel(new javax.swing.DefaultComboBoxModel(gamesx.toArray()));
                jlc.setSelectedItem(selobj);
            }
        }.start();
    }
    
    public void buttonExited(MouseEvent evt) {
        evt.getComponent().setForeground(new java.awt.Color(0,0,0));
    }
    
    public void buttonEntered(MouseEvent evt) {
        evt.getComponent().setForeground(new java.awt.Color(122,65,165));
    }
    
    public void buttonPressed(MouseEvent evt) {
        evt.getComponent().setForeground(new java.awt.Color(162,105,205));
    }
    
    public void buttonReleased(MouseEvent evt) {
        if(evt.getComponent().hasFocus()) {
            buttonEntered(evt);
        }
        else {
            buttonExited(evt);
        }
    }
    
    public static void sortFavourites() {
        sortStreamItemPanel(mainPanel);
    }
    
    /*
     * List sort
     * */
    public static void sortStreamItemPanel(Container jp) {
        if(jp==null) {
            return;
        }
        List<StreamItem> sortList = new ArrayList<>();
        for(Component com:jp.getComponents()) {
            if(com instanceof StreamItem) {
                sortList.add((StreamItem)com);
            }
            else {
                if(com!=null) {
                    jp.remove(com);
                }
            }
        }
        addHighestFav(sortList,jp);
        sortList=null;
    }
    
    public static void addHighestFav(List<StreamItem> sortList, Container jp) {
        if(sortList.size()<=0) {
            return;
        }
        
        int pos=0;
        int maxviews=0;
        
        for(int i=0;i<sortList.size();i++) {
            int views = sortList.get(i).getViews();
            if(views>maxviews) {
                pos=i;
                maxviews=views;
            }
        }
        
        jp.remove(sortList.get(pos));
        jp.add(sortList.get(pos));
        sortList.remove(pos);
        
        if(sortList.size()<=0) {
            return;
        }
        addHighestFav(sortList,jp);
    }
    
    // swap sort
    /*
    public static void sortStreamItemPanel(Container jp) {
        if(jp==null) {
            return;
        }
        
        for(Component com:jp.getComponents()) {
            if(!(com instanceof StreamItem)) {
                jp.remove(com);
            }
        }
        
        for(int i=0;i<jp.getComponentCount()-1;i++) {
            for(int j=i+1;j<jp.getComponentCount();j++) {
                String nameI=((StreamItem)jp.getComponents()[i]).getUserName();
                int viewsI=((StreamItem)jp.getComponents()[i]).getViews();
                
                String nameJ=((StreamItem)jp.getComponents()[j]).getUserName();
                int viewsJ=((StreamItem)jp.getComponents()[j]).getViews();
                
                
                if(viewsJ>viewsI) {
                    ((StreamItem)jp.getComponents()[j]).swapWith((StreamItem)jp.getComponents()[i]);
                }
                else if(viewsJ==viewsI) {
                    if(nameJ.compareToIgnoreCase(nameI)<0) {
                        ((StreamItem)jp.getComponents()[j]).swapWith((StreamItem)jp.getComponents()[i]);
                    }
                }
            }
        }
    }*/
    
    
    public static void updateFavouriteFile() {
        favs.clear();
        new Thread() {
            @Override
            public void run() {
                try {
                    for(Component cm:mainPanel.getComponents()) {
                        if(cm instanceof StreamItem) {
                            favs.add(new SerialStream(((StreamItem)cm).nameL.getText(),((StreamItem)cm).isNotify()));
                        }
                    }
                    for(Component cm:offPanel.getComponents()) {
                        if(cm instanceof StreamItem) {
                            favs.add(new SerialStream(((StreamItem)cm).nameL.getText(),((StreamItem)cm).isNotify()));
                        }
                    }
                    try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("data\\fav.dat"))) {
                        out.writeObject(favs);
                    }
                }
                catch(Exception e) {
                }
                
            }
        }.start();
    }
    
    public void loadFavsFromFile() {
        try {
            FileInputStream door = new FileInputStream("data\\fav.dat");
            ObjectInputStream reader = new ObjectInputStream(door);
            ConcurrentSkipListSet<SerialStream> newList =(ConcurrentSkipListSet<SerialStream>)reader.readObject();
            for(SerialStream ss:newList) {
                favs.add(ss);
            }
        } catch (IOException|ClassNotFoundException ex) {
            //Logger.getLogger(TwitchVLC.class.getName()).log(Level.SEVERE, null, ex);
            if(new File("\\data").mkdir()) {
                try {
                    new File("\\data\\fav.dat").createNewFile();
                } catch (IOException ex1) {
                    JOptionPane.showMessageDialog(mainframe, "Favourite data file inaccessible (data\\fav.dat)", "Access error", 0);
                }
            }
        }
        catch (ClassCastException ex) {
            JOptionPane.showMessageDialog(mainframe, "Favourite data file corrupt (data\\fav.dat)", "Corrupt file", 0);
        }
        for(SerialStream ss:favs) {
            StreamItem si = new StreamItem(ss.name,true);
            si.setNotify(ss.notify);
            si.setSize(StreamItem.default_width, 75);
            
            offPanel.add(si);
            
            
            si.updateStream(true);
        }
    }
    
    public static void refreshFavourites() {
            new Thread() {
                @Override
                public void run() {
                    try {
                        for(Component cm:mainPanel.getComponents()) {
                            if(cm instanceof StreamItem) {
                                ((StreamItem)cm).updateStream();
                            }
                        }
                        for(Component cm:offPanel.getComponents()) {
                            if(cm instanceof StreamItem) {
                                ((StreamItem)cm).updateStream();
                            }
                        }
                    }
                    catch(Exception e) {
                    }
                }
            }.start();
    }
    
    public static void openWebpage(URI uri) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(uri);
            } catch (Exception e) {
            }
        }
    }

    public static void openWebpage(URL url) {
        try {
            openWebpage(url.toURI());
        } catch (URISyntaxException e) {
        }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TwitchVLC.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                mainframe = new TwitchVLC();
                mainframe.setVisible(true);
            }
        });
        
        new Thread() {
            @Override
            public void run() {
                while(true) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(TwitchVLC.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    if(mainframe!=null) {
                        System.out.println("OKLOL");
                        mainframe.setComboItems(mainframe.gameSearch);
                        try {
                            Thread.sleep(118000);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(TwitchVLC.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    
                }
            }
        }.start();
        
        while(true) {
            
            if(mainframe!=null) {
                mainframe.favouritesPanel.setIgnoreRepaint(true);
                for(Component cmx: mainframe.favouritesPanel.getComponents()) {
                    cmx.setIgnoreRepaint(true);
                }
            }
            
            sortFavourites();
            if(mainframe!=null) {
                sortStreamItemPanel(mainframe.searchResultsPanel);
            }
            try {
                Thread.sleep(1500);
            } catch (InterruptedException ex) {
                Logger.getLogger(TwitchVLC.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
            refreshFavourites();
            sortFavourites();
            if(mainframe!=null) {
                sortStreamItemPanel(mainframe.searchResultsPanel);
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
                Logger.getLogger(TwitchVLC.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
            sortFavourites();
            if(mainframe!=null) {
                sortStreamItemPanel(mainframe.searchResultsPanel);
            }
            try {
                Thread.sleep(10500);
            } catch (InterruptedException ex) {
                Logger.getLogger(TwitchVLC.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
            if(mainframe!=null) {
                mainframe.favouritesPanel.setIgnoreRepaint(false);
                for(Component cmx: mainframe.favouritesPanel.getComponents()) {
                    cmx.setIgnoreRepaint(false);
                }
            }
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField channelTextField;
    private javax.swing.JButton currentChatButton;
    public static javax.swing.JLabel currentGame;
    public static javax.swing.JLabel currentName;
    public static javax.swing.JTextArea currentTitle;
    private javax.swing.JButton currentWatchButton1;
    private javax.swing.JButton favouriteButton;
    private javax.swing.JPanel favouritesHeader;
    private javax.swing.JPanel favouritesPanel;
    private javax.swing.JScrollPane favouritesScrollPane;
    private javax.swing.JComboBox gameSearch;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JPanel mainContainerPanel;
    public static javax.swing.JPanel mainPanel;
    private javax.swing.JPanel mainSearchPanel;
    public static javax.swing.JPanel offPanel;
    public static javax.swing.JPanel pnl;
    private javax.swing.JPanel rightPanel;
    private javax.swing.JButton searchButton;
    private javax.swing.JButton searchFirstButton;
    private javax.swing.JPanel searchHeaderPanel;
    private javax.swing.JButton searchNextButton;
    private javax.swing.JButton searchPrevButton;
    public static javax.swing.JPanel searchResultsPanel;
    private javax.swing.JScrollPane searchScrollPane;
    private javax.swing.JLabel searchStatusLabel;
    // End of variables declaration//GEN-END:variables
}
