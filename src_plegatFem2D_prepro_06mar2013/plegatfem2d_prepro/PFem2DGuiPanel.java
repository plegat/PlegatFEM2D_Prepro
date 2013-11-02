package plegatfem2d_prepro;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
 *
 * @author jmb2
 */
public class PFem2DGuiPanel extends JPanel implements MouseListener, MouseMotionListener {

    public PFem2DGuiPanel() {
        super();

        this.mode = VIEW;

        this.offsetX = 0;
        this.offsetY = 0;

        this.echelle = ECHELLE_BASE;

        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }
    private PFEM2DObjectManager pom;
    private int xold, yold;
    private int xstart, ystart;
    private int mode;
    private double offsetX, offsetY;
    private double echelle;
    private static int VIEW = 0;
    private static int DRAG = 1;
    private static int SCALE = 2;
    public static double ECHELLE_BASE = 100.;

    public PFEM2DObjectManager getPom() {
        return pom;
    }

    public void setPom(PFEM2DObjectManager pom) {
        this.pom = pom;
    }

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
        int roundEchelle = (int) Math.round(this.echelle);

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

        int nbxp = (w - (w / 2 + roundOffsetX)) / roundEchelle + 1;
        int nbxm = (w / 2 + roundOffsetX) / roundEchelle + 1;

        for (int i = 1; i < nbxp; i++) {
            int xline = w / 2 + roundOffsetX + (int) Math.round(i * this.echelle);
            g.drawLine(xline, 0, xline, h);

        }
        for (int i = 1; i < nbxm; i++) {
            int xline = w / 2 + roundOffsetX - (int) Math.round(i * this.echelle);
            g.drawLine(xline, 0, xline, h);
        }

        g2.setStroke(bsX);

        int nbym = (h - (h / 2 + roundOffsetY)) / roundEchelle + 1;
        int nbyp = (h / 2 + roundOffsetY) / roundEchelle + 1;

        for (int i = 1; i < nbyp; i++) {
            int yline = h / 2 + roundOffsetY - (int) Math.round(i * this.echelle);
            g.drawLine(0, yline, w, yline);
        }
        for (int i = 1; i < nbym; i++) {
            int yline = h / 2 + roundOffsetY + (int) Math.round(i * this.echelle);
            g.drawLine(0, yline, w, yline);
        }

        g2.setStroke(new BasicStroke());

        if (this.pom != null) {
            this.pom.draw(g, this);
        }
    }

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

            int delta = 0;
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

    public int getLocalCoordX(double x) {

        return (int) Math.round(this.getWidth() / 2 + this.offsetX + x * this.echelle / ECHELLE_BASE);

    }

    public int getLocalCoordY(double y) {

        return (int) Math.round(this.getHeight() / 2 + this.offsetY - y * this.echelle / ECHELLE_BASE);

    }

    public double getRealCoordX(double x) {

        return (x - (this.getWidth() / 2 + this.offsetX)) * ECHELLE_BASE / this.echelle;

    }

    public double getRealCoordY(double y) {

        return (y - (this.getHeight() / 2 + this.offsetY)) * -ECHELLE_BASE / this.echelle;

    }

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
