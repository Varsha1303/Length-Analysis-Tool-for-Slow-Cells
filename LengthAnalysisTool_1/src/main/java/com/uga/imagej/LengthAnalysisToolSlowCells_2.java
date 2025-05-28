//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.uga.imagej;

import ij.Prefs;
import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.ImageWindow;
import ij.measure.ResultsTable;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import fiji.threshold.Auto_Threshold;
import inra.ijpb.morphology.Reconstruction;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import sc.fiji.skeletonize3D.Skeletonize3D_;

/**
 * This class runs the plugin for automated length analysis of paths traced by cells.
 * This plugin is executed using the ImageJ software which is available
 * on {@see https://imagej.net/software/fiji/}
 *
 * @author Varshini Bonagiri
 * @version 2.0
 * @since 01-15-2022
 */
public class LengthAnalysisToolSlowCells_2 implements PlugInFilter {
    protected ImagePlus image;
    public double value;
    public String name;
    
    // Default values for pixels and exposure time
    private static double magnificationFactor = 1.365;  
    private static int expTime = 1; 

    ArrayList<ArrayList<Integer>> twoDArray;

    public ResultsTable table = ResultsTable.getResultsTable();

    public LengthAnalysisToolSlowCells_2() {
    	System.out.println("Instance Created: " + this);
    }
    /**
     * This method is used to set up the plugin for ImageJ
     *
     * @param arg arguments passed by user
     * @param imp Input image
     * @return Input image converted to gray scale
     */
    public int setup(String arg, ImagePlus imp) {
        if (arg.equals("about")) {
            return 4096;
        } else {
            this.image = imp;
            return 97;
        }
    }
    //User Input for Exposure Time and Pixels
    private void getUserInputs() {
        boolean isValid = false;

        // Load saved values (or set defaults if not found)
        double defaultMag = Prefs.get("LengthAnalysisTool.magnificationFactor", 1.365);
        int defaultExp = (int) Prefs.get("LengthAnalysisTool.expTime", 1);

        while (!isValid) {
            // Text fields for input
            JTextField magInputField = new JTextField(String.valueOf(defaultMag));
            JTextField expInputField = new JTextField(String.valueOf(defaultExp));
            
            // Checkbox to use default values
            JCheckBox useDefaultsCheck = new JCheckBox("Use Default Values");
            useDefaultsCheck.setSelected(false);

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.add(new JLabel("Pixels(μm):"));
            panel.add(magInputField);
            panel.add(new JLabel("Exposure Time(s):"));
            panel.add(expInputField);
            panel.add(useDefaultsCheck); // Add the checkbox

            int result = JOptionPane.showConfirmDialog(
                    null, panel, "User Input", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                try {
                    // If checkbox is checked, set default values
                    if (useDefaultsCheck.isSelected()) {
                        magnificationFactor = 1.365;
                        expTime = 1;
                    } else {
                        // Otherwise, get user input
                        double inputMag = Double.parseDouble(magInputField.getText().trim());
                        int inputExp = Integer.parseInt(expInputField.getText().trim());

                        if (inputMag > 0 && inputExp > 0) {
                            magnificationFactor = inputMag;
                            expTime = inputExp;
                        } else {
                            JOptionPane.showMessageDialog(null, 
                                "Please enter positive values for both fields.", 
                                "Invalid Input", JOptionPane.WARNING_MESSAGE);
                            continue; // Restart loop
                        }
                    }

                    // Save values persistently
                    Prefs.set("LengthAnalysisTool.magnificationFactor", magnificationFactor);
                    Prefs.set("LengthAnalysisTool.expTime", expTime);
                    Prefs.savePreferences();  // Save permanently

                    isValid = true; // Exit loop
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, 
                        "Invalid input format. Please enter numeric values.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, 
                    "Action canceled. Using default values.", 
                    "Info", JOptionPane.INFORMATION_MESSAGE);
                magnificationFactor = 1.365;
                expTime = 1;
                return;
            }
        }
    }
    /**
     * This method is used to generate the results table
     *
     * @param id   Unique identifier for each cell
     * @param len1 Distance between end points of path traced by cells
     * @param len2 Actual distance of path traced by the cell
     */
    public void createResultsTable(int id, double len1, double len2,double curvature) {
    	table.incrementCounter();
    	table.addValue("Id", id);
    	table.addValue("End-End Length", len1);
    	table.addValue("Path Length", len2);
    	double endVel = (len1 / expTime) * magnificationFactor;
    	double pathVel = (len2 / expTime) * magnificationFactor;
    	table.addValue("End-End Velocity", endVel);
    	table.addValue("Path Velocity", pathVel);
    	table.addValue("Ratio", endVel / pathVel);
       
    }
    public ArrayList<ArrayList<Integer>> imgToArray(ImageProcessor ip) {
        twoDArray = new ArrayList<>();
        for (int p1 = 0; p1 < ip.getWidth() - 1; p1++) {
            twoDArray.add(new ArrayList<>());
            for (int p2 = 0; p2 < ip.getHeight() - 1; p2++) {
                twoDArray.get(p1).add(p2, ip.getPixel(p1, p2));
            }
        }
        return twoDArray;
    }
    /**
     * This method is used to run the ImageJ plugin
     *
     * @param ip Image processor of input image
     */
    public void run(ImageProcessor ip) {
    	getUserInputs();
        ImageProcessor ip1 = ip.duplicate();
        ip1.gamma(1.15);
        ip1.blurGaussian(1.0);
        ImagePlus image = new ImagePlus("input image", ip1);
        Auto_Threshold auto = new Auto_Threshold();
        auto.exec(image, "Otsu", true, false, true, false, false, false);
        ImageProcessor img = image.getProcessor();
        ImageProcessor result = Reconstruction.killBorders(img);
        Skeletonize3D_ sk = new Skeletonize3D_();
        ImagePlus resultPlus = new ImagePlus("killBorders", result);
        sk.setup("", resultPlus);
        ImageProcessor rp = resultPlus.getProcessor();
        sk.run(rp);
        resultPlus.show();

        this.twoDArray = imgToArray(rp);
        Contours c = new Contours();
        Junctions j = new Junctions();
        j.findJunctions(this.twoDArray);
        ArrayList<ArrayList<Integer>> lines = c.findLines(this.twoDArray);
        j.fixJunctions(lines);
        c.calculateLength(lines);
        c.displayContours(resultPlus,this.twoDArray);
        this.table.show("Results");
        
    }
    // Custom ImageWindow to prevent save prompt on close
    static class CustomImageWindow extends ImageWindow {
        public CustomImageWindow(ImagePlus imp) {
            super(imp);
            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    imp.changes = false; // Prevent "Save changes?" prompt
                    imp.hide();          // Hide first
                    imp.flush();         // Release memory
                    WindowManager.removeWindow(CustomImageWindow.this); // Remove from ImageJ
                    dispose();           // Dispose of this window
                }
            });
        }
    }
    /**
     * This is the main method which starts ImageJ and
     * calls the run method to execute the length analysis tool plugin.
     *
     * @param args Unused
     * @throws Exception Runtime exception
     */
    public static void main(String[] args) throws Exception {
        Class<?> clazz = LengthAnalysisToolSlowCells_2.class;
        URL url = clazz.getProtectionDomain().getCodeSource().getLocation();
        File file = new File(url.toURI());
        System.setProperty("plugins.dir", file.getAbsolutePath());
        new ImageJ();
        ImagePlus image = IJ.openImage("src/Images/test.tif");
        image.show();
        if (image != null) {
            new CustomImageWindow(image);
        }
        IJ.runPlugIn(clazz.getName(), "");
    }
}

