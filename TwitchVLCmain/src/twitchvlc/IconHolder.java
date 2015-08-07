/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package twitchvlc;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 *
 * @author Greg
 */
public class IconHolder {
    
    StreamItem streamI;
    
    public IconHolder(StreamItem jl) {
        streamI=jl;
    }
    
    public void setStreamItem(StreamItem jl) {
        streamI=jl;
    }
    
    public void setIcon(Icon ic) {
        getStream().imageL.setIcon(ic);
    }
    
    public StreamItem getStream() {
        return streamI;
    }
    
    public void setIconToDownload(URL url) {
        try {
            
            BufferedImage img = ImageIO.read(url);
            getStream().imageL.setIcon(new ImageIcon(img));
            
        } catch (IOException ex) {
            Logger.getLogger(IconHolder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
