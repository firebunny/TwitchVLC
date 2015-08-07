/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package twitchvlc;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

/**
 *
 * @author Greg
 */
public class StreamItem extends javax.swing.JPanel {
    
    public static int extraw = 30;
    
    public static int default_width = 406;
    
    private IconHolder IH;
    
    private boolean notify=false;
    public boolean press=false;
    private long lastimagedownload = 0;
    private String username;
    private boolean fav;
    
    private static long lastdel = 0;
    
    //private static int offlinew = 406;
    //private static int offlineh = 30;
    private static int offlineh = 75;
    
    //private static int onlinew = 406;
    private static int onlineh = 75;
    
    private static java.awt.Color normal = new java.awt.Color(40, 40, 40);
    private static java.awt.Color highlighted = new java.awt.Color(45, 45, 45);
    private static java.awt.Color selected = new java.awt.Color(30, 30, 30);
    
    private static java.awt.Color redtext = new java.awt.Color(240, 100, 100);
    private static java.awt.Color viewstext = new java.awt.Color(230, 80, 80);
    
    private static java.awt.Color normaltext = new java.awt.Color(150, 150, 150);
    private static java.awt.Color highlighttext = new java.awt.Color(220, 220, 220);
    private static java.awt.Color downtext = new java.awt.Color(220, 220, 220);
    
    
    private static java.awt.Color buttonhighlighted = new java.awt.Color(55, 55, 55);
    
    private static java.awt.Color buttonnormal = new java.awt.Color(50, 50, 50);
    
    private static Border border = javax.swing.BorderFactory.createBevelBorder(0,new java.awt.Color(49,49,49),null,Color.BLACK,null);
    private static Border highlightborder = javax.swing.BorderFactory.createBevelBorder(0,new java.awt.Color(54,54,54),null,Color.BLACK,null);
    private static Border downborder = javax.swing.BorderFactory.createBevelBorder(1,new java.awt.Color(18,18,18),new java.awt.Color(18,18,18),null,Color.BLACK);
    
    public StreamItem(final String accName, boolean isFav) {
        super();
        initComponents();
        //forceSize(offlinew,offlineh);
        //setWidth(offlinew);
        setFocusable(true);
        this.setBackground(normal);
        username=accName;
        fav=isFav;
        
        //this.setBorder(javax.swing.BorderFactory.createMatteBorder(3, 0, 0, 0, new java.awt.Color(51, 51, 51)));
        
        this.addComponentListener(new ComponentListener() {
            @Override
            public void componentShown(ComponentEvent e) {
                if(getThis().getParent()==null) {
                    return;
                }
                //setBorder(javax.swing.BorderFactory.createMatteBorder(3, 0, 0, 0, getParent().getBackground()));
                setNormalstate(getThis());
            }

            @Override
            public void componentResized(ComponentEvent e) {
                if(getThis().getParent()==null) {
                    return;
                }
                //setBorder(javax.swing.BorderFactory.createMatteBorder(3, 0, 0, 0, getParent().getBackground()));
                setNormalstate(getThis());
            }

            @Override
            public void componentMoved(ComponentEvent e) {
            }

            @Override
            public void componentHidden(ComponentEvent e) {
            }
        });
        
        java.awt.event.MouseAdapter ma = new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                press=true;
                
                //setDownstate((StreamItem)evt.getComponent().getParent());
                setDownstate(getThis());
            }
            
            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                if(!press) {
                    return;
                }
                press=false;
                TwitchVLC.currentName.setToolTipText(getUserName());
                TwitchVLC.currentName.setText(getUserName());
                TwitchVLC.currentTitle.setToolTipText(titleL.getText());
                TwitchVLC.currentTitle.setText(titleL.getText());
                TwitchVLC.currentGame.setToolTipText(gameL.getText());
                TwitchVLC.currentGame.setText(gameL.getText());
                
                setStates(TwitchVLC.mainPanel);
                setStates(TwitchVLC.offPanel);
                setStates(TwitchVLC.searchResultsPanel);
            }
            
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                press=false;
                if(!nameL.getText().equalsIgnoreCase(TwitchVLC.currentName.getText())) {
                    setHighlightstate(getThis());
                }
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                press=false;
                if(nameL.getText().equalsIgnoreCase(TwitchVLC.currentName.getText())) {
                    setDownstate(getThis());
                }
                else {
                    setNormalstate(getThis());
                }
            }
        };
        
        
        notifyL.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                notify=!notify;
                setNotify(notify);
                TwitchVLC.updateFavouriteFile();
            }
            
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if(!nameL.getText().equalsIgnoreCase(TwitchVLC.currentName.getText())) {
                    setHighlightstate(getThis());
                }
                
                evt.getComponent().setBackground(buttonhighlighted);
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if(nameL.getText().equalsIgnoreCase(TwitchVLC.currentName.getText())) {
                    setDownstate(getThis());
                }
                else {
                    setNormalstate(getThis());
                }
                
                evt.getComponent().setBackground(buttonnormal);
            }
        });
        
        notifyL.setBackground(buttonnormal);
        notifyL.setForeground(normaltext);
        notifyL.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        notifyL.setText("notify");
        notifyL.setToolTipText("Notify when stream goes live");
        notifyL.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        notifyL.setOpaque(true);
        
        if(isFav()) {
            deleteL.setBackground(buttonnormal);
            deleteL.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            deleteL.setText("delete");
            deleteL.setToolTipText("Delete favourite");
            deleteL.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            deleteL.setOpaque(true);
            
            deleteL.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    if(lastdel+800>System.currentTimeMillis()) {
                        return;
                    }
                    lastdel=System.currentTimeMillis();
                    getThis().removeAll();
                    imageL=null;
                    titleL=null;
                    gameL=null;
                    nameL=null;
                    viewL=null;
                    deleteL=null;
                    notifyL=null;
                    TwitchVLC.mainPanel.remove(getThis());
                    TwitchVLC.offPanel.remove(getThis());

                    TwitchVLC.mainframe.pack();

                    TwitchVLC.mainPanel.repaint();
                    TwitchVLC.offPanel.repaint();
                    TwitchVLC.updateFavouriteFile();
                }
                
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    if(!nameL.getText().equalsIgnoreCase(TwitchVLC.currentName.getText())) {
                        setHighlightstate(getThis());
                    }
                    
                    evt.getComponent().setBackground(buttonhighlighted);
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    if(nameL.getText().equalsIgnoreCase(TwitchVLC.currentName.getText())) {
                        setDownstate(getThis());
                    }
                    else {
                        setNormalstate(getThis());
                    }

                    evt.getComponent().setBackground(buttonnormal);
                }
            });
        }
        else {
            deleteL.setBackground(buttonnormal);
            deleteL.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            deleteL.setText(" ");
            deleteL.setToolTipText(" ");
            deleteL.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            deleteL.setOpaque(true);
        }
        
        jpnl.addMouseListener(ma);
        jpnl.setOpaque(false);
        
        
        imageL.setBackground(buttonnormal);
        imageL.setForeground(normaltext);
        imageL.setOpaque(false);
        imageL.setText("No Image");
        imageL.setHorizontalAlignment(0);
        imageL.setVerticalAlignment(SwingConstants.TOP);
        
        IH = new IconHolder(this);
        
        titleL.setBackground(buttonnormal);
        titleL.setToolTipText("");
        titleL.setOpaque(true);

        gameL.setBackground(buttonnormal);
        gameL.setOpaque(true);

        nameL.setBackground(buttonnormal);
        nameL.setToolTipText(getUserName());
        nameL.setText(getUserName());
        nameL.setOpaque(true);
        
        viewL.setBackground(buttonnormal);
        viewL.setForeground(viewstext);
        viewL.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        viewL.setToolTipText("Live viewers");
        viewL.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        viewL.setOpaque(true);
        
        
        imageL.addMouseListener(ma);
        titleL.addMouseListener(ma);
        gameL.addMouseListener(ma);
        nameL.addMouseListener(ma);
        viewL.addMouseListener(ma);
    }
    
    public void setWidth(int w) {
        this.setSize(w, this.getHeight());

        //this.setPreferredSize(new Dimension(100,this.getHeight()));
        //this.setMinimumSize(new Dimension(100,this.getHeight()));
    }
    
    public static void setForeground(JComponent com, Color col) {
        if(com==null) {
            return;
        }
        com.setForeground(col);
    }
    
    public static void setDownstate(JComponent com) {
        if(com==null) {
            return;
        }
        com.setBorder(downborder);
        com.setBackground(selected);
        if(com instanceof StreamItem) {
            setForeground(((StreamItem)com).deleteL,downtext);
            setForeground(((StreamItem)com).nameL,downtext);
            setForeground(((StreamItem)com).titleL,downtext);
            setForeground(((StreamItem)com).gameL,downtext);
        }
    }
    
    public static void setNormalstate(JComponent com) {
        if(com==null) {
            return;
        }
        com.setBorder(border);
        com.setBackground(normal);
        if(com instanceof StreamItem) {
            setForeground(((StreamItem)com).deleteL,normaltext);
            setForeground(((StreamItem)com).nameL,normaltext);
            setForeground(((StreamItem)com).titleL,normaltext);
            setForeground(((StreamItem)com).gameL,normaltext);
        }
    }
    
    public static void setHighlightstate(JComponent com) {
        if(com==null) {
            return;
        }
        com.setBorder(highlightborder);
        com.setBackground(highlighted);
        if(com instanceof StreamItem) {
            setForeground(((StreamItem)com).deleteL,highlighttext);
            setForeground(((StreamItem)com).nameL,highlighttext);
            setForeground(((StreamItem)com).titleL,highlighttext);
            setForeground(((StreamItem)com).gameL,highlighttext);
        }
    }
    
    public void setStates(JPanel jp) {
        for(Component com: jp.getComponents()) {
            if(com instanceof StreamItem) {
                if(((StreamItem)com).nameL.getText().equalsIgnoreCase(nameL.getText())) {
                    setDownstate(((StreamItem)com));
                }
                else {
                    setNormalstate(((StreamItem)com));
                }
            }
        }
    }
    
    public StreamItem getThis() {
        return this;
    }
    
    public boolean isNotify() {
        return notify;
    }
    
    public void setNotify(boolean noti) {
        notify=noti;
        if(notify) {
            notifyL.setForeground(redtext);
        }
        else {
            notifyL.setForeground(normaltext);
        }
    }
    
    public void moveToOnlineFrame() {
        if(this.getParent() == null) {
            TwitchVLC.mainPanel.add(this);
        }
        else if(this.getParent().equals(TwitchVLC.offPanel)) {
            TwitchVLC.offPanel.remove(this);
            TwitchVLC.mainPanel.add(this);
        }
    }
    
    public void moveToOfflineFrame() {
        if(this.getParent() == null) {
            TwitchVLC.offPanel.add(this);
        }
        else if(this.getParent().equals(TwitchVLC.mainPanel)) {
            TwitchVLC.mainPanel.remove(this);
            TwitchVLC.offPanel.add(this);
        }
    }
    
    public void clearLabels() {
        titleL.setText(" ");
        gameL.setText(" ");
        viewL.setText(" ");
        titleL.setPreferredSize(titleL.getPreferredSize());
        gameL.setPreferredSize(gameL.getPreferredSize());
        viewL.setPreferredSize(viewL.getPreferredSize());
        //imageL.setIcon(null);
    }
    
    @Override
    public String getName() {
        if(nameL==null) {
            return "Offline";
        }
        return nameL.getText();
    }
    
    public final String getUserName() {
        return username+"";
    }
    
    public final boolean isFav() {
        return fav;
    }
    
    public void updateStream() {
        updateStream(false);
    }
    
    public void updateStream(final boolean firsttime) {
        updateStream(firsttime, null);
    }
    
    public void updateStream(final boolean firsttime, final JsonElement streamx) {
        new Thread() {
            @Override
            public void run() {
                JsonElement stream=streamx;
                if(stream==null) {
                    JsonParser jp = new JsonParser();
                    try{
                        JsonObject jo = (JsonObject)jp.parse(getJSON("https://api.twitch.tv/kraken/streams/"+nameL.getText()));
                        stream = jo.get("stream");
                    }
                    catch(Exception e) {
                        return;
                    }
                }
                
                if(stream==null || (stream instanceof JsonNull)) {
                    clearLabels();
                    //forceSize(offlinew,offlineh);
                    //setWidth(offlinew);
                    //forceSize(getThis().getWidth(),offlineh);
                    getThis().setMaximumSize(new Dimension(getThis().getMaximumSize().width,offlineh));
                    
                    titleL.setText("Offline");
                    titleL.setPreferredSize(titleL.getPreferredSize());
                    titleL.setToolTipText("Offline");
                    moveToOfflineFrame();

                    viewL.setText("Offline");
                    viewL.setPreferredSize(viewL.getPreferredSize());
                    viewL.setToolTipText("Offline");
                    viewL.setForeground(normaltext);
                    //imageL.setIcon(null);
                    IH.setIcon(null);
                    TwitchVLC.sortFavourites();
                    return;
                }
                
                JsonObject channel = (JsonObject)((JsonObject)stream).get("channel");
                String status = channel.get("status").getAsString();
                String game = channel.get("game").getAsString();
                String viewers = ((JsonObject)stream).get("viewers").getAsString();
                String name = channel.get("name").getAsString();
                
                JsonObject preview = (JsonObject)((JsonObject)stream).get("preview");
                String imgurl = preview.get("small").getAsString();

                if((titleL.getText().equals("") || titleL.getText().equals("Offline")) && isNotify() && !firsttime) {
                    // if coming online, announce
                    new Thread() {@Override public void run() {
                        TwitchVLC.voice.speak("User "+nameL.getText().replace("_", " ").trim()+" is online");
                    }}.start();
                    /*
                    final Runnable runnable = (Runnable) Toolkit.getDefaultToolkit().getDesktopProperty("win.sound.exclamation");
                    if (runnable != null) {
                        runnable.run();
                    }*/
                }

                //forceSize(onlinew,onlineh);
                //setWidth(onlinew); 
                //forceSize(getThis().getParent().getWidth(),onlineh);
                getThis().setMaximumSize(new Dimension(getThis().getMaximumSize().width,onlineh));
                
                nameL.setText(name);
                nameL.setPreferredSize(nameL.getPreferredSize());
                nameL.setToolTipText(name);
                
                gameL.setText(game);
                gameL.setPreferredSize(gameL.getPreferredSize());
                gameL.setToolTipText(game);

                viewL.setText(viewers);
                viewL.setPreferredSize(viewL.getPreferredSize());
                viewL.setToolTipText(viewers);
                viewL.setForeground(viewstext);

        titleL.setPreferredSize(new Dimension(100,titleL.getHeight()));
        titleL.setMinimumSize(new Dimension(100,titleL.getHeight()));
        
                titleL.setText(status);
                titleL.setPreferredSize(titleL.getPreferredSize());
                titleL.setToolTipText(status);
                moveToOnlineFrame();

                try {
                    if(System.currentTimeMillis()-lastimagedownload>60000 || ((titleL.getText().equals("") || titleL.getText().equals("Offline")) && isNotify())) {
                        lastimagedownload=System.currentTimeMillis();

                        IH.setIconToDownload(new URL(imgurl));


                        //BufferedImage img = ImageIO.read(new URL(nList.item(0).getTextContent()));
                        //IH.setIcon(new ImageIcon(img));


                        //imageL.setIcon(new ImageIcon(img));
                    }
                } catch (IOException ex) {
                    Logger.getLogger(TwitchVLC.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }.start();
    }
    
    public final void forceSize(int w, int h) {
        this.setPreferredSize(new Dimension(w,h));
        this.setMaximumSize(new Dimension(w,h));
        this.setMinimumSize(new Dimension(w,h));
    }
    
    public static String getJSON(String url) {
        try {
            URL u = new URL(url);
            HttpURLConnection c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setRequestProperty("Content-length", "0");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            //c.setConnectTimeout(timeout);
            //c.setReadTimeout(timeout);
            c.connect();
            int status = c.getResponseCode();

            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line+"\n");
                    }
                    br.close();
                    return sb.toString();
            }

        } catch (MalformedURLException ex) {
            //Logger.getLogger(DebugServer.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex.toString());
        } catch (IOException ex) {
            //Logger.getLogger(DebugServer.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex.toString());
        }
        return null;
    }
    
    public boolean isOnline() {
        if(viewL==null) {
            return false;
        }
        if(viewL.getText().equals("Offline") || viewL.getText().equals("")) {
            return false;
        }
        return true;
    }
    
    public int getViews() {
        if(viewL.getText().equals("") || viewL.getText().equals(" ") || viewL.getText().equals("Offline")) {
            return -1;
        }
        return Integer.parseInt(viewL.getText());
    }
    
    public StreamItem() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jpnl = new javax.swing.JPanel();
        imageL = new javax.swing.JLabel();
        viewL = new javax.swing.JLabel();
        gameL = new javax.swing.JLabel();
        titleL = new javax.swing.JLabel();
        deleteL = new javax.swing.JLabel();
        notifyL = new javax.swing.JLabel();
        nameL = new javax.swing.JLabel();

        setMaximumSize(new java.awt.Dimension(32767, 75));
        setPreferredSize(new java.awt.Dimension(406, 75));

        imageL.setBackground(new java.awt.Color(102, 102, 102));
        imageL.setOpaque(true);

        viewL.setBackground(new java.awt.Color(102, 102, 102));
        viewL.setForeground(new java.awt.Color(255, 255, 255));
        viewL.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        viewL.setText("0");
        viewL.setOpaque(true);

        gameL.setBackground(new java.awt.Color(102, 102, 102));
        gameL.setForeground(new java.awt.Color(255, 255, 255));
        gameL.setText("Game");
        gameL.setOpaque(true);

        titleL.setBackground(new java.awt.Color(102, 102, 102));
        titleL.setForeground(new java.awt.Color(255, 255, 255));
        titleL.setText("Status");
        titleL.setOpaque(true);

        deleteL.setBackground(new java.awt.Color(102, 102, 102));
        deleteL.setForeground(new java.awt.Color(255, 255, 255));
        deleteL.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        deleteL.setText("delete");
        deleteL.setOpaque(true);

        notifyL.setBackground(new java.awt.Color(102, 102, 102));
        notifyL.setForeground(new java.awt.Color(255, 255, 255));
        notifyL.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        notifyL.setText("notify");
        notifyL.setOpaque(true);

        nameL.setBackground(new java.awt.Color(102, 102, 102));
        nameL.setForeground(new java.awt.Color(255, 255, 255));
        nameL.setText("Name");
        nameL.setOpaque(true);

        javax.swing.GroupLayout jpnlLayout = new javax.swing.GroupLayout(jpnl);
        jpnl.setLayout(jpnlLayout);
        jpnlLayout.setHorizontalGroup(
            jpnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnlLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpnlLayout.createSequentialGroup()
                        .addComponent(nameL, javax.swing.GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(notifyL, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deleteL, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(viewL, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(gameL, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(titleL, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(imageL, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jpnlLayout.setVerticalGroup(
            jpnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpnlLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jpnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(imageL, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jpnlLayout.createSequentialGroup()
                        .addGroup(jpnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(nameL, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(notifyL, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(viewL, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(deleteL, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(gameL, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(titleL, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jpnl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jpnl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel deleteL;
    private javax.swing.JLabel gameL;
    public javax.swing.JLabel imageL;
    private javax.swing.JPanel jpnl;
    public javax.swing.JLabel nameL;
    private javax.swing.JLabel notifyL;
    private javax.swing.JLabel titleL;
    private javax.swing.JLabel viewL;
    // End of variables declaration//GEN-END:variables
}
