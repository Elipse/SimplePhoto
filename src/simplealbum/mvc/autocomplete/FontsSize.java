/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplealbum.mvc.autocomplete;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import javax.swing.UIManager;

/**
 *
 * @author elialva
 */
public class FontsSize {

    public static void main(String[] args) {
        String text = "wwwwwwwwww";  //27 & 14    89&14
        AffineTransform affinetransform = new AffineTransform();
        FontRenderContext frc = new FontRenderContext(affinetransform, true, true);

        Font font = new Font("Tahoma", Font.PLAIN, 12);

        int textwidth = (int) (font.getStringBounds(text, frc).getWidth());
        int textheight = (int) (font.getStringBounds(text, frc).getHeight());

        System.out.println("text " + text + " w: " + textwidth + " h: " + textheight);

        System.out.println("---------------" + UIManager.getDefaults().getFont("TextField.font"));

        Font font2 = new Font("Tahoma", Font.PLAIN, 12);
        FontMetrics metrics = new FontMetrics(font2) {
        };
        Rectangle2D bounds = metrics.getStringBounds("wwwwwwwwww", null);
        int widthInPixels = (int) bounds.getWidth();
        System.out.println("widthInPixels " + widthInPixels);
        System.out.println("heightInPixeles " + bounds.getHeight());

    }

}
