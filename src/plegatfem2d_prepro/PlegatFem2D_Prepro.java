package plegatfem2d_prepro;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JSeparator;
import javax.swing.JToolBar;

/**
 *
 * @author jmb2
 */
public class PlegatFem2D_Prepro extends JFrame {

    public PlegatFem2D_Prepro(String frameTitle) {
        super(frameTitle);
        this.initComponents();
    }

    private void initComponents() {

        final PFEM2DObjectManager pom = new PFEM2DObjectManager();

        this.pfguipanel = new PFEM2DGuiPanel();
        this.pfguipanel.setPreferredSize(new Dimension(800, 600));
        this.add(this.pfguipanel, BorderLayout.CENTER);

        JToolBar toolbar = new JToolBar();
        toolbar.setPreferredSize(new Dimension(100, 40));
        toolbar.setFloatable(false);

        // bouton open

        JButton jbOpen = new JButton("open", new javax.swing.ImageIcon(getClass().getResource("/ressources/icons/add.png")));
        jbOpen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JFileChooser fc = new JFileChooser();
                int returnVal = fc.showOpenDialog(pfguipanel.getParent());

                if (returnVal == JFileChooser.APPROVE_OPTION) {

                    File fichier = fc.getSelectedFile();
                    boolean flag=pom.openFile(fichier);
                    
                    if (flag) {
                        pfguipanel.repaint();
                    }
                }
            }
        });
        toolbar.add(jbOpen);

        // bouton mesh

        JButton jbMesh = new JButton("mesh", new javax.swing.ImageIcon(getClass().getResource("/ressources/icons/cog.png")));
        jbMesh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pom.mesh();
                pfguipanel.repaint();
            }
        });
        toolbar.add(jbMesh);

        // bouton screenshot

        JButton jbScreen = new JButton("screenshot", new javax.swing.ImageIcon(getClass().getResource("/ressources/icons/snapshot.png")));
        jbScreen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pfguipanel.takeScreenshot();
            }
        });
        toolbar.add(jbScreen);

        // bouton resize

        JButton jbResize = new JButton("resize", new javax.swing.ImageIcon(getClass().getResource("/ressources/icons/resize_picture.png")));
        jbResize.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pfguipanel.centreView(pom.getBoundingBox());
            }
        });
        toolbar.add(jbResize);


        toolbar.add(new JSeparator(JSeparator.VERTICAL));

        // bouton close

        JButton jbClose = new JButton("close", new javax.swing.ImageIcon(getClass().getResource("/ressources/icons/cross.png")));
        jbClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        toolbar.add(jbClose);

        this.add(toolbar, BorderLayout.NORTH);
        this.pack();

        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        this.pfguipanel.setPom(pom);

    }
    private PFEM2DGuiPanel pfguipanel;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new PlegatFem2D_Prepro("Preprocesseur PFEM").setVisible(true);

                System.out.println("lancement ok");
            }
        });
    }
}
