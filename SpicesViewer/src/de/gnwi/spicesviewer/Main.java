/**
 * SPICES (Simplified Particle Input ConnEction Specification) Viewer
 * Copyright (C) 2018  Achim Zielesny (achim.zielesny@googlemail.com)
 * 
 * Source code is available at <https://github.com/zielesny/SPICES>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.gnwi.spicesviewer;

import de.gnwi.spicestographstream.SpicesToGraphStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Locale;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

/**
 * The Spices Viewer application's main class. Its main() method starts the
 * application by testing some system requirements and instantiating the
 * {@link de.gnwi.spicesviewer.Controller}.
 * 
 * @author Jonas Schaub
 */
public class Main {

    //<editor-fold defaultstate="collapsed" desc="Public static 'main()' method">
    /**
     * Instantiates the {@link de.gnwi.spicesviewer.Controller} to start the 
     * application.
     * <p>
     * Prior to this it tests the system's java version (it must be release 1.8 or higher), sets the LookAndFeel 
     * of the application (to the 'Nimbus' LookAndFeel), sets the default locale of the system to English (so that 
     * used templates like the JColorChooser are in English, too), enquires the default temp file path from the system
     * and it creates a folder for the user preferences file in the AppData directory (in Windows) if it does not exist
     * already (if no user preferences file from a previous run is available this method creates a new one).
     * If one of these steps fails the application is terminated.
     * 
     * @param args the command line arguments (none required)
     */
    public static void main(String[] args) {
        try {
            //<editor-fold defaultstate="collapsed" desc="Pre-start steps">
            //<editor-fold defaultstate="collapsed" desc="Testing the system's java version">
            double tmpVersionDouble = 0.0;
            try {
                String tmpJvmVersion = System.getProperty("java.vm.specification.version");
                if (tmpJvmVersion == null || tmpJvmVersion.trim().isEmpty()) {
                    throw new NumberFormatException();
                }
                tmpVersionDouble = Double.parseDouble(tmpJvmVersion);
            } catch (Exception anException) {
                JOptionPane.showMessageDialog(null,
                        Language.getString("DETERMINING_JAVA_VERSION_ERROR_MESSAGE"),
                        Language.getString("ERROR"),
                        JOptionPane.WARNING_MESSAGE);
                System.exit(1);
            }
            if (tmpVersionDouble < Constants.REQUIRED_JAVA_VERSION) {
                JOptionPane.showMessageDialog(null,
                        Language.getString("JAVA_VERSION_ERROR_MESSAGE"),
                        Language.getString("ERROR"),
                        JOptionPane.WARNING_MESSAGE);
                System.exit(1);
            }
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Setting the Look&Feel">
            try {
                for (LookAndFeelInfo tmpInfo : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(tmpInfo.getName())) {
                        UIManager.setLookAndFeel(tmpInfo.getClassName());
                        break;
                    }
                }
                if (!UIManager.getLookAndFeel().getName().equals("Nimbus")) {
                    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                }
            } catch (Exception anException) {
                JOptionPane.showMessageDialog(null,
                        Language.getString("LOOK_AND_FEEL_ERROR_MESSAGE"),
                        Language.getString("ERROR"),
                        JOptionPane.WARNING_MESSAGE);
                System.exit(1);
            }
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Setting the default locale">
            Locale.setDefault(new Locale("en"));
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Determining the application's directory and the default temp file path">
            String tmpAppDir = "";
            String tmpTempDir = "";
            try {
                String tmpOS = System.getProperty("os.name").toUpperCase();
                if (tmpOS.contains("WIN")) {
                    tmpAppDir = System.getenv("AppData");
                } else if (tmpOS.contains("MAC")) {
                    tmpAppDir = System.getProperty("user.home");
                } else if (tmpOS.contains("NUX") || tmpOS.contains("NIX") || tmpOS.contains("AIX")) {
                    tmpAppDir = System.getProperty("user.home");
                } else {
                    throw new SecurityException();
                }
                if (tmpAppDir == null) {
                    throw new SecurityException();
                }
                File tmpAppDirFile = new File(tmpAppDir);
                if (!tmpAppDirFile.exists() || !tmpAppDirFile.isDirectory()) {
                    throw new SecurityException();
                }
                if (tmpOS.contains("MAC")) {
                    tmpAppDir += File.separator + "Library" + File.separator + "Application Support";
                }
                tmpAppDir += File.separator + Constants.VENDOR_NAME + File.separator + Constants.APPLICATION_NAME;
                tmpAppDirFile = new File(tmpAppDir);
                if (!tmpAppDirFile.exists()) {
                    tmpAppDirFile.mkdirs();
                }
                File tmpPreferencesFile = new File(tmpAppDir + File.separator + Constants.USER_PREFERENCES_FILENAME);
                if (!tmpPreferencesFile.exists()) {
                    UserPreferences tmpPreferences = new UserPreferences((new SpicesToGraphStream()).getStylesheet());
                    FileOutputStream tmpFileOutputStream = null;
                    ObjectOutputStream tmpObjectOutputStream = null;
                    try {
                        tmpFileOutputStream = new FileOutputStream(tmpPreferencesFile);
                        tmpObjectOutputStream = new ObjectOutputStream(tmpFileOutputStream);
                        tmpObjectOutputStream.writeObject(tmpPreferences);
                    } catch (FileNotFoundException aFileNotFoundException) {
                        String tmpErrorMessage = Language.getString("PROBLEM_LOADING_PREFERENCES");
                        JOptionPane.showMessageDialog(null, tmpErrorMessage, 
                                Language.getString("ERROR"), JOptionPane.ERROR_MESSAGE);
                    } catch (IOException anIOException) {
                        String tmpErrorMessage = Language.getString("PROBLEM_LOADING_PREFERENCES");
                        JOptionPane.showMessageDialog(null, tmpErrorMessage, 
                                Language.getString("ERROR"), JOptionPane.ERROR_MESSAGE);
                    } finally {
                        try {
                            if (tmpFileOutputStream != null) 
                                tmpFileOutputStream.close();
                            if (tmpObjectOutputStream != null) {
                                tmpObjectOutputStream.flush();
                                tmpObjectOutputStream.close();
                            }
                        } catch (IOException anIOException) {
                        }
                    }
                }
                
                tmpTempDir = System.getProperty("java.io.tmpdir");
                if (tmpTempDir == null) {
                    throw new SecurityException();
                }
                File tmpTempDirFile = new File(tmpTempDir);
                if (!tmpTempDirFile.isDirectory() || !tmpTempDirFile.exists()) {
                    throw new SecurityException();
                }
            } catch (SecurityException aSecurityException) {
                JOptionPane.showMessageDialog(null,
                        Language.getString("DETERMINING_DIRECTORIES_ERROR_MESSAGE"),
                        Language.getString("ERROR"),
                        JOptionPane.WARNING_MESSAGE);
                System.exit(1);
            }
            //</editor-fold>
            //</editor-fold>
            //<editor-fold defaultstate="collapsed" desc="Starting the application">
            try {
                Controller tmpController = new Controller(tmpAppDir, tmpTempDir);
            } catch (IllegalArgumentException anIllegalArgumentException) {
                JOptionPane.showMessageDialog(null,
                        Language.getString("DETERMINING_DIRECTORIES_ERROR_MESSAGE"),
                        Language.getString("ERROR"),
                        JOptionPane.WARNING_MESSAGE);
                System.exit(1);
            }
            //</editor-fold>
        } catch (Exception anException) {
            JOptionPane.showMessageDialog(null,
                    Language.getString("UNEXPECTED_ERROR_STARTING"),
                    Language.getString("ERROR"),
                    JOptionPane.WARNING_MESSAGE);
            System.exit(1);
        }
    }
    //</editor-fold>

}
