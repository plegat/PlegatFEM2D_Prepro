package plegatfem2d_prepro;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Classe définissant un JPanel personnalisé. Ce JPanel permet l'affichage d'une
 * grille 2D (axes X et Y, quadrillage horizontal et vertical) ainsi que
 * d'objets graphiques 2D définis par le programmeur. L'affichage est interactif
 * via la souris, l'utilisateur peut ainsi déplacer la vue, ainsi que zoomer sur
 * une zone particulière.
 *
 * @author Jean-Michel BORLOT
 */
public class PFEM2DGuiPanel extends JPanel implements MouseListener, MouseMotionListener {

    /**
     * Constructeur.
     */
    public PFEM2DGuiPanel() {
        super();

        this.mode = VIEW;

        this.offsetX = 0;
        this.offsetY = 0;

        this.echelle = ECHELLE_BASE;

        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }
    private PFEM2DObjectManager pom;            // gestionnaire d'objets graphiques
    private int xold, yold;                     // coordonnées du point local précédent
    private int xstart, ystart;                 // coordonnées du point local initial
    private int mode;                           // mode d'interaction: VIEW, DRAG ou SCALE
    private double offsetX, offsetY;            // décalage de la vue
    private double echelle;                     // échelle de la vue
    private static int VIEW = 0;
    private static int DRAG = 1;
    private static int SCALE = 2;
    private static double ECHELLE_BASE = 100.;  // échelle de base
    private static double stepX = 50, stepY = 50; // pas des quadrillages sens X et sens Y

    /**
     * Renvoie le gestionnaire d'objets graphiques
     *
     * @return le gestionnaire d'objets graphiques
     */
    public PFEM2DObjectManager getPom() {
        return pom;
    }

    /**
     * Définit le gestionnaire d'objets graphiques
     *
     * @param pom le gestionnaire d'objets graphiques
     */
    public void setPom(PFEM2DObjectManager pom) {
        this.pom = pom;
    }

    // surcharge de la méthode paintComponent afin de dessiner notre vue personnalisée
    @Override
    public void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D) g;

        int h = this.getHeight();
        int w = this.getWidth();

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, w, h);

        //tracé des axes et du quadrillage

        int roundOffsetX = (int) Math.round(this.offsetX);
        int roundOffsetY = (int) Math.round(this.offsetY);
        int localStepX = (int) Math.round(this.echelle / ECHELLE_BASE * stepX);
        int localStepY = (int) Math.round(this.echelle / ECHELLE_BASE * stepY);

        g.setColor(Color.GREEN);
        g.drawLine(w / 2 + roundOffsetX, 0, w / 2 + roundOffsetX, h);
        g.drawLine(0, h / 2 + roundOffsetY, w, h / 2 + roundOffsetY);

        int offsetStrokeX = 7 - roundOffsetX % 7;
        int offsetStrokeY = 7 - roundOffsetY % 7;

        float[] dash = {2, 5};
        BasicStroke bsX = new BasicStroke(1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND, 10, dash, offsetStrokeX);
        BasicStroke bsY = new BasicStroke(1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND, 10, dash, offsetStrokeY);

        g.setColor(new Color(60, 85, 60));

        g2.setStroke(bsY);

        int nbxp = (w - (w / 2 + roundOffsetX)) / localStepX + 1;
        int nbxm = (w / 2 + roundOffsetX) / localStepX + 1;

        for (int i = 1; i < nbxp; i++) {
            int xline = w / 2 + roundOffsetX + (int) Math.round(i * stepX * this.echelle / ECHELLE_BASE);
            g.drawLine(xline, 0, xline, h);

        }
        for (int i = 1; i < nbxm; i++) {
            int xline = w / 2 + roundOffsetX - (int) Math.round(i * stepX * this.echelle / ECHELLE_BASE);
            g.drawLine(xline, 0, xline, h);
        }

        g2.setStroke(bsX);

        int nbym = (h - (h / 2 + roundOffsetY)) / localStepY + 1;
        int nbyp = (h / 2 + roundOffsetY) / localStepY + 1;

        for (int i = 1; i < nbyp; i++) {
            int yline = h / 2 + roundOffsetY - (int) Math.round(i * stepY * this.echelle / ECHELLE_BASE);
            g.drawLine(0, yline, w, yline);
        }
        for (int i = 1; i < nbym; i++) {
            int yline = h / 2 + roundOffsetY + (int) Math.round(i * stepY * this.echelle / ECHELLE_BASE);
            g.drawLine(0, yline, w, yline);
        }

        g2.setStroke(new BasicStroke());

        Font font=g.getFont();
        
        int screenRes = Toolkit.getDefaultToolkit().getScreenResolution();
        int fontSize = (int)Math.round(8.0 * screenRes / 72.0);

        g.setFont(new Font(font.getName(), font.getStyle(), fontSize));
        
        
        if (this.pom != null) {
            this.pom.draw(g, this);
        }
    }

    // surcharge de la méthode mouseDragged. Gestion du déplacement souris avec bouton appuyé
    @Override
    public void mouseDragged(MouseEvent e) {
        if (this.mode == DRAG) {
            int x = e.getX();
            int y = e.getY();

            this.offsetX = this.offsetX + (x - this.xold);
            this.offsetY = this.offsetY + (y - this.yold);

            this.xold = x;
            this.yold = y;

            this.repaint();
        } else if (this.mode == SCALE) {
            int x = e.getX();
            int y = e.getY();

            int dx = x - this.xold;
            int dy = y - this.yold;

            int delta;
            if (Math.abs(dx) > Math.abs(dy)) {
                delta = dx;
            } else {
                delta = dy;
            }

            double newEchelle = Math.max(1, this.echelle * Math.pow(1.01, delta));
            double newOffsetX = this.offsetX + this.getRealCoordX(this.xstart) / ECHELLE_BASE * (this.echelle - newEchelle);
            double newOffsetY = this.offsetY - this.getRealCoordY(this.ystart) / ECHELLE_BASE * (this.echelle - newEchelle);

            this.offsetX = newOffsetX;
            this.offsetY = newOffsetY;
            this.echelle = newEchelle;

            this.xold = x;
            this.yold = y;

            this.repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    // surcharge de la méthode mousePressed. Gestion du clic souris.
    @Override
    public void mousePressed(MouseEvent e) {
        this.xold = e.getX();
        this.yold = e.getY();

        if (e.getButton() == MouseEvent.BUTTON1) {
            this.mode = DRAG;
        } else if (e.getButton() == MouseEvent.BUTTON2) {
            this.mode = SCALE;
            this.xstart = e.getX();
            this.ystart = e.getY();
        } else {
            this.mode = VIEW;
        }
    }

    // surcharge de la méthode mouseReleased. Gestion du laché de clic souris.
    @Override
    public void mouseReleased(MouseEvent e) {
        this.mode = VIEW;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    /**
     * Convertit une abscisse réelle en abscisse locale (repère JPanel)
     *
     * @param x l'abscisse réelle
     * @return l'abscisse locale
     */
    public int getLocalCoordX(double x) {

        return (int) Math.round(this.getWidth() / 2 + this.offsetX + x * this.echelle / ECHELLE_BASE);

    }

    /**
     * Convertit une ordonnée réelle en ordonnée locale (repère JPanel)
     *
     * @param y l'ordonnée réelle
     * @return l'ordonnée locale
     */
    public int getLocalCoordY(double y) {

        return (int) Math.round(this.getHeight() / 2 + this.offsetY - y * this.echelle / ECHELLE_BASE);

    }

    /**
     * Convertit une abscisse locale (repère JPanel) en abscisse réelle
     *
     * @param x l'abscisse locale
     * @return l'abscisse réelle
     */
    public double getRealCoordX(double x) {

        return (x - (this.getWidth() / 2 + this.offsetX)) * ECHELLE_BASE / this.echelle;

    }

    /**
     * Convertit une ordonnée locale (repère JPanel) en ordonnée réelle
     *
     * @param y l'ordonnée locale
     * @return l'ordonnée réelle
     */
    public double getRealCoordY(double y) {

        return (y - (this.getHeight() / 2 + this.offsetY)) * -ECHELLE_BASE / this.echelle;

    }

    /**
     * Créé une capture d'écran de l'affichage courant. Le fichier de sortie est
     * défini par sélection du fichier via un JFileChooser. Deux formats
     * possibles en sortie: png ou jpg
     */
    public void takeScreenshot() {

        JFileChooser fc = new JFileChooser();

        int returnVal = fc.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {

            File fichier = fc.getSelectedFile();
            String nomFichier = fichier.getName().toLowerCase();

            if ((nomFichier.endsWith("png")) || (nomFichier.endsWith("jpg"))) {
                try {
                    BufferedImage bufImage = new BufferedImage(this.getSize().width, this.getSize().height, BufferedImage.TYPE_INT_RGB);
                    this.paint(bufImage.createGraphics());

                    String extension = "png";
                    if (nomFichier.endsWith("jpg")) {
                        extension = "jpg";
                    }

                    ImageIO.write(bufImage, extension, fichier);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Veuillez préciser l'extension du fichier image (png ou jpg)",
                        "Extension fichier manquante", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Centre la vue sur les éléments à afficher.
     *
     * @param boundingBox tableau de 4 double: Xmin, Xmax, Ymin,Ymax, en
     * coordonnées réelles
     */
    public void centreView(double[] boundingBox) {

        if ((boundingBox[1] - boundingBox[0] != Double.NEGATIVE_INFINITY) && (boundingBox[3] - boundingBox[2] != Double.NEGATIVE_INFINITY)) {

            double echelleX = (this.getWidth() - 50) * ECHELLE_BASE / (boundingBox[1] - boundingBox[0]);
            double echelleY = (this.getHeight() - 50) * ECHELLE_BASE / (boundingBox[3] - boundingBox[2]);

            this.echelle = Math.min(echelleX, echelleY);
            
            this.offsetX = -(this.getLocalCoordX((boundingBox[1] + boundingBox[0]) / 2) - this.getLocalCoordX(0));
            this.offsetY = -(this.getLocalCoordY((boundingBox[3] + boundingBox[2]) / 2) - this.getLocalCoordY(0));

            this.repaint();
        }
    }
}
