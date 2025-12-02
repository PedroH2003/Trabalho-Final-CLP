package screens.scenes;

import ilcompiler.input.Input.InputType;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.*;
import java.util.Map;
import java.util.Random;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class TrafficLightScenePanel extends JPanel implements IScenePanel {

    // --- DEBUG (Mantive ligado para você conferir) ---
    private final boolean SHOW_DEBUG_GRID = true; 
    private int mouseX = 0;
    private int mouseY = 0;

    private InputEventListener inputListener;
    private Runnable onCriticalFailureCallback;

    private final PushButton buttonPedestre1;
    private final PushButton buttonPedestre2;

    private boolean nsRed, nsYellow, nsGreen;
    private boolean ewRed, ewYellow, ewGreen;

    private boolean p1Pressed = false;
    private boolean p2Pressed = false;
    private boolean isSimulating = false;

    private Timer animationTimer;
    private float carEW_Progress = 0f; 
    private float carNS_Progress = 0f;
    private boolean crashOccurred = false;
    private boolean carOnSensorEW = false;
    private boolean carOnSensorNS = false;

    // --- DIMENSÕES DO PAINEL ---
    private final int W = 624;
    private final int H = 394;
    
    // --- COORDENADAS FIXAS DOS SENSORES (Baseado nos seus círculos amarelos) ---
    private final Point sensorLeftPos = new Point(270, 230);
    private final Point sensorRightPos = new Point(358, 227);
    
    // Tamanho do Sensor (Dobrado/Grande para cobrir a faixa)
    private final int SENSOR_W = 100;
    private final int SENSOR_H = 45;

    // Ângulo das ruas (Calculado para passar pelos sensores)
    // A rua é uma diagonal. O ângulo é aprox 32 a 35 graus.
    private final double ROAD_ANGLE = Math.toRadians(34); 

    // Cores
    private static final Color GRASS_COLOR = new Color(34, 100, 34);
    private static final Color ROAD_COLOR = new Color(80, 80, 80);
    private static final Color SIDEWALK_COLOR = new Color(180, 180, 180);
    private static final Color BOX_COLOR = new Color(255, 200, 0);
    private static final Color BOX_SHADE = new Color(200, 150, 0);

    public TrafficLightScenePanel() {
        this.setLayout(null);
        this.setBackground(Color.BLACK);
        this.setPreferredSize(new Dimension(W, H));
        this.setSize(W, H);

        buttonPedestre1 = new PushButton("I0.0", InputType.NO);
        buttonPedestre1.setBounds(230, 310, 40, 40);

        buttonPedestre2 = new PushButton("I0.1", InputType.NO);
        buttonPedestre2.setBounds(350, 310, 40, 40);

        this.add(buttonPedestre1);
        this.add(buttonPedestre2);

        if (SHOW_DEBUG_GRID) {
            this.addMouseMotionListener(new MouseMotionListener() {
                @Override public void mouseDragged(MouseEvent e) {}
                @Override public void mouseMoved(MouseEvent e) {
                    mouseX = e.getX(); mouseY = e.getY(); repaint();
                }
            });
        }

        animationTimer = new Timer(33, e -> {
            if (isSimulating && !crashOccurred) updateCars();
            repaint();
        });
        animationTimer.start();
    }

    private void updateCars() {
        // Ponto de parada calculado: O carro deve parar quando chegar perto do sensor (aprox 30% do trajeto)
        // Azul (EW)
        float stopEW = 26f;
        boolean atStopEW = (carEW_Progress > stopEW - 1 && carEW_Progress < stopEW + 1);
        boolean canGoEW = ewGreen || (ewYellow && carEW_Progress > stopEW) || (carEW_Progress > stopEW + 10);

        if (atStopEW && !canGoEW) carOnSensorEW = true;
        else {
            carEW_Progress += 0.6f;
            if (!atStopEW) carOnSensorEW = false;
        }
        if (carEW_Progress > 100f) carEW_Progress = 0f;

        // Vermelho (NS)
        float stopNS = 28f;
        boolean atStopNS = (carNS_Progress > stopNS - 1 && carNS_Progress < stopNS + 1);
        boolean canGoNS = nsGreen || (nsYellow && carNS_Progress > stopNS) || (carNS_Progress > stopNS + 10);

        if (atStopNS && !canGoNS) carOnSensorNS = true;
        else {
            carNS_Progress += 0.6f;
            if (!atStopNS) carOnSensorNS = false;
        }
        if (carNS_Progress > 100f) carNS_Progress = 0f;

        // Colisão (Centro)
        if (carEW_Progress > 45 && carEW_Progress < 55 && carNS_Progress > 45 && carNS_Progress < 55) handleCrash();
    }

    private void handleCrash() {
        crashOccurred = true;
        repaint();
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, "COLISÃO!", "Erro", JOptionPane.ERROR_MESSAGE);
            if (onCriticalFailureCallback != null) onCriticalFailureCallback.run();
            stop();
        });
    }

    @Override
    public void initInputs(Map<String, InputType> inputsType, Map<String, Boolean> inputs) {
        inputsType.put("I0.0", InputType.NO); inputs.put("I0.0", false);
        inputsType.put("I0.1", InputType.NO); inputs.put("I0.1", false);
        inputsType.put("I0.2", InputType.NO); inputs.put("I0.2", false);
        inputsType.put("I0.3", InputType.NO); inputs.put("I0.3", false);
    }

    @Override
    public void updateUIState(Map<String, InputType> inputsType, Map<String, Boolean> inputs, Map<String, Boolean> outputs) {
        isSimulating = true;
        nsRed = outputs.getOrDefault("Q0.0", false);
        nsYellow = outputs.getOrDefault("Q0.1", false);
        nsGreen = outputs.getOrDefault("Q0.2", false);
        ewRed = outputs.getOrDefault("Q0.3", false);
        ewYellow = outputs.getOrDefault("Q0.4", false);
        ewGreen = outputs.getOrDefault("Q0.5", false);
        p1Pressed = inputs.getOrDefault("I0.0", false);
        p2Pressed = inputs.getOrDefault("I0.1", false);
        inputs.put("I0.2", carOnSensorEW);
        inputs.put("I0.3", carOnSensorNS);
    }

    @Override
    public void resetUIState() {
        nsRed = nsYellow = nsGreen = false;
        ewRed = ewYellow = ewGreen = false;
        p1Pressed = p2Pressed = false;
        carEW_Progress = 0f; carNS_Progress = 0f;
        carOnSensorEW = false; carOnSensorNS = false;
        crashOccurred = false; isSimulating = false;
        repaint();
    }

    @Override public void setInputListener(InputEventListener l) { 
        this.inputListener = l; 
        buttonPedestre1.setInputEventListener(l); 
        buttonPedestre2.setInputEventListener(l); 
    }
    @Override public void setOnCriticalFailureCallback(Runnable r) { this.onCriticalFailureCallback = r; }
    @Override public void stop() { isSimulating = false; }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawGrass(g2);
        drawRoads(g2);

        // --- DESENHAR SENSORES E FAIXAS NAS POSIÇÕES FIXAS ---
        
        drawLaneFeature(g2, sensorLeftPos.x, sensorLeftPos.y, true, carOnSensorEW);

       
        drawLaneFeature(g2, sensorRightPos.x, sensorRightPos.y, false, carOnSensorNS);

        // --- CARROS ---
        drawCars(g2);

        if (crashOccurred) drawExplosion(g2);

        // Torre (deslocada para cima para não tapar o cruzamento)
        drawCentralTower(g2, 312, 140);
        drawPedestrianPosts(g2);
        drawLabels(g2);
        
        if (SHOW_DEBUG_GRID) drawDebugGrid(g2);
    }
    
    private void drawDebugGrid(Graphics2D g2) {
        g2.setColor(new Color(0, 255, 0, 50));
        for (int i = 0; i < W; i+=50) g2.drawLine(i, 0, i, H);
        for (int i = 0; i < H; i+=50) g2.drawLine(0, i, W, i);
        g2.setColor(Color.BLACK); g2.fillRect(5, 5, 130, 25);
        g2.setColor(Color.YELLOW); g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.drawString("X: " + mouseX + " Y: " + mouseY, 10, 22);
        g2.setColor(Color.RED); g2.drawLine(mouseX - 5, mouseY, mouseX + 5, mouseY); g2.drawLine(mouseX, mouseY - 5, mouseX, mouseY + 5);
    }

    private void drawGrass(Graphics2D g2) {
        g2.setColor(GRASS_COLOR);
        g2.fillRect(0, 0, getWidth(), getHeight());
    }

    private void drawRoads(Graphics2D g2) {
        int roadW = 150;
        // As ruas são desenhadas baseadas na diagonal do painel
        Polygon roadEW = new Polygon();
        roadEW.addPoint(0, 0); roadEW.addPoint(roadW, 0);
        roadEW.addPoint(W, H); roadEW.addPoint(W - roadW, H);

        Polygon roadNS = new Polygon();
        roadNS.addPoint(W - roadW, 0); roadNS.addPoint(W, 0);
        roadNS.addPoint(roadW, H); roadNS.addPoint(0, H);

        g2.setColor(SIDEWALK_COLOR); g2.setStroke(new BasicStroke(18));
        g2.drawPolygon(roadEW); g2.drawPolygon(roadNS);
        g2.setColor(ROAD_COLOR);
        g2.fillPolygon(roadEW); g2.fillPolygon(roadNS);

        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, new float[]{15, 15}, 0));
        g2.drawLine(roadW / 2, 0, W - roadW / 2, H);
        g2.drawLine(W - roadW / 2, 0, roadW / 2, H);
    }

    /**
     * Desenha o sensor (fundo) e as listras (faixa) juntos, usando rotação.
     */
    private void drawLaneFeature(Graphics2D g2, int cx, int cy, boolean isBlueRoad, boolean active) {
        AffineTransform old = g2.getTransform();
        g2.translate(cx, cy);
        
        // Ângulo: 
        // Esquerda (Blue): ROAD_ANGLE
        // Direita (Red): -ROAD_ANGLE
        double angle;

        if (isBlueRoad) {
            // --- LADO ESQUERDO (SENSOR AZUL) ---
            // Mude o '0' para girar.
            // Positivo (+) gira Horário. Negativo (-) gira Anti-Horário.
            angle = ROAD_ANGLE + Math.toRadians(12); 
        } else {
            // --- LADO DIREITO (SENSOR VERMELHO) ---
            // Mude o '0' para girar.
            // Tente valores como 10, -10, 20...
            angle = -ROAD_ANGLE + Math.toRadians(-12); 
        }
        g2.rotate(angle);

        int w = SENSOR_W; // 100
        int h = SENSOR_H; // 45
        
        // Centraliza o retângulo na coordenada do mouse/ponto fixo
        int lx = -w / 2;
        int ly = 8; // Offset para alinhar na pista da direita (abaixo da linha branca no eixo local)
        
        // 1. Fundo do Sensor (Amarelo ou Cinza)
        if (active) g2.setColor(new Color(255, 255, 0, 180)); 
        else g2.setColor(new Color(100, 100, 100, 80)); 
        g2.fillRect(lx, ly, w, h);
        
        // Borda
        g2.setColor(active ? Color.YELLOW : Color.DARK_GRAY);
        g2.setStroke(new BasicStroke(2));
        g2.drawRect(lx, ly, w, h);

        // 2. Listras (Zebras)
        g2.setColor(Color.WHITE);
        int stripes = 6; 
        int stripW = (w / stripes) - 4; 
        for (int i = 0; i < stripes; i++) {
            g2.fillRect(lx + 4 + (i * (w / stripes)), ly + 2, stripW, h - 4);
        }
        
        // 3. Linha de Pare
        g2.setStroke(new BasicStroke(5));
        g2.drawLine(lx - 5, ly, lx - 5, ly + h);

        // 4. Label ID (Des-rotacionada)
        g2.rotate(-angle); 
        g2.setColor(Color.BLACK); 
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        // Ajuste manual para o texto ficar legível
        if (isBlueRoad) g2.drawString("I:1/02", -20, 50);
        else            g2.drawString("I:1/03", -30, 50);

        g2.setTransform(old);
    }

    private void drawCars(Graphics2D g2) {
        // Definir os vetores de movimento baseados no tamanho da tela e offsets
        // Pista Azul: (Esquerda-Baixo -> Direita-Cima).
        // Ajuste de X e Y para alinhar com o centro da pista da direita.
        // O ROAD_ANGLE é ~34 graus. 
        // Offset perpendicular: ~35px.
        
        // Calculo vetorial simplificado:
        double startX_B = -100; double startY_B = H + 63; 
        double endX_B = W + 0; double endY_B = -140;
        
        // Azul
        double curX_B = startX_B + (endX_B - startX_B) * (carEW_Progress / 100.0);
        double curY_B = startY_B + (endY_B - startY_B) * (carEW_Progress / 100.0);
        
        // Offset lateral manual para encaixar na pista
        double blueOffsetX = 110;
        double blueOffsetY = -30; 

        if (carEW_Progress > 0 && carEW_Progress < 100) {
            // Ângulo negativo pois Y diminui
            drawRotatedCar(g2, (int)(curX_B + blueOffsetX), (int)(curY_B + blueOffsetY), Color.BLUE, -ROAD_ANGLE- Math.toRadians(5));
        }

        // Vermelho (Direita-Baixo -> Esquerda-Cima)
        double startX_R = W + 100; double startY_R = H + 63;
        double endX_R = 0; double endY_R = -140;
        
        double curX_R = startX_R + (endX_R - startX_R) * (carNS_Progress / 100.0);
        double curY_R = startY_R + (endY_R - startY_R) * (carNS_Progress / 100.0);

        // Offset lateral manual
        double redOffsetX = -110;
        double redOffsetY = 0;

        if (carNS_Progress > 0 && carNS_Progress < 100) {
            // Ângulo: 180 graus + ROAD_ANGLE
            drawRotatedCar(g2, (int)(curX_R + redOffsetX), (int)(curY_R + redOffsetY), Color.RED, Math.PI + ROAD_ANGLE+Math.toRadians(5));
        }
    }

    private void drawRotatedCar(Graphics2D g2, int x, int y, Color c, double angle) {
        AffineTransform old = g2.getTransform();
        g2.translate(x, y);
        g2.rotate(angle);
        
        int w = 55; int h = 28;
        // Desenha centralizado no eixo Y local mas deslocado no eixo Y (faixa)
        int carY = 35 - (h/2); // Offset 35 da linha central
        
        g2.setColor(c);
        g2.fillRoundRect(-w/2, carY, w, h, 8, 8);
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(1));
        g2.drawRoundRect(-w/2, carY, w, h, 8, 8);
        
        g2.setColor(c.brighter());
        g2.fillRoundRect(-w/2 + 5, carY + 3, 30, h - 6, 5, 5); 
        g2.setColor(Color.BLACK);
        g2.drawRoundRect(-w/2 + 5, carY + 3, 30, h - 6, 5, 5);
        
        g2.setColor(Color.YELLOW);
        g2.fillOval(w/2 - 5, carY + 3, 4, 4);
        g2.fillOval(w/2 - 5, carY + h - 7, 4, 4);
        
        g2.setTransform(old);
    }

    private void drawExplosion(Graphics2D g2) {
        g2.setColor(Color.ORANGE);
        g2.fillOval(312-40, 140-10, 80, 60); 
        g2.setColor(Color.RED);
        g2.fillOval(312-20, 140, 40, 30);
        g2.setColor(Color.YELLOW);
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        g2.drawString("CRASH!", 312-35, 140+25);
    }

    private void drawCentralTower(Graphics2D g2, int x, int y) {
        int towerW = 60; int towerH = 140; 
        int[] tx = {x, x + towerW, x, x - towerW}; 
        int[] ty = {y - towerH - 30, y - towerH, y - towerH + 30, y - towerH};
        int[] rx = {x, x + towerW, x + towerW, x}; 
        int[] ry = {y - towerH + 30, y - towerH, y, y + 30};
        int[] lx = {x, x, x - towerW, x - towerW}; 
        int[] ly = {y + 30, y - towerH + 30, y - towerH, y};

        g2.setColor(new Color(255, 230, 100)); g2.fillPolygon(tx, ty, 4);
        g2.setColor(Color.BLACK); g2.setStroke(new BasicStroke(2)); g2.drawPolygon(tx, ty, 4);
        g2.setColor(BOX_SHADE); g2.fillPolygon(rx, ry, 4); g2.setColor(Color.BLACK); g2.drawPolygon(rx, ry, 4);
        g2.setColor(BOX_COLOR); g2.fillPolygon(lx, ly, 4); g2.setColor(Color.BLACK); g2.drawPolygon(lx, ly, 4);

        drawLight(g2, x - 45, y - 95, nsRed, Color.RED);
        drawLight(g2, x - 45, y - 60, nsYellow, Color.YELLOW);
        drawLight(g2, x - 45, y - 25, nsGreen, Color.GREEN);
        drawLight(g2, x + 15, y - 95, ewRed, Color.RED);
        drawLight(g2, x + 15, y - 60, ewYellow, Color.YELLOW);
        drawLight(g2, x + 15, y - 25, ewGreen, Color.GREEN);
    }

    private void drawLight(Graphics2D g2, int x, int y, boolean on, Color c) {
        g2.setColor(on ? c : c.darker().darker().darker());
        g2.fillOval(x, y, 30, 30);
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(1)); g2.drawOval(x, y, 30, 30);
    }

    private void drawPedestrianPosts(Graphics2D g2) {
        drawPostShape(g2, 230, 310, p1Pressed);
        drawPostShape(g2, 350, 310, p2Pressed);
    }

    private void drawPostShape(Graphics2D g2, int x, int y, boolean pressed) {
        g2.setColor(Color.GRAY);
        g2.fillRect(x - 5, y - 50, 10, 50);
        g2.setColor(Color.DARK_GRAY);
        g2.fill3DRect(x - 20, y - 80, 40, 30, true);
        g2.setFont(new Font("Arial", Font.BOLD, 9));
        g2.setColor(Color.RED);
        g2.drawString("DONT", x - 12, y - 68);
        g2.setColor(Color.WHITE);
        g2.drawString("WALK", x - 12, y - 58);
        g2.setColor(pressed ? Color.CYAN : Color.RED);
        int[] ax = {x, x + 10, x + 10, x + 20, x + 10, x + 10, x};
        int[] ay = {y - 10, y - 10, y - 15, y - 5, y + 5, y, y};
        g2.fillPolygon(ax, ay, 7);
    }

    private void drawLabels(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
        int towerY = 140; // Mesmo Y da torre
        int lx = 312 - 120; int ly = towerY - 70;
        g2.drawString("O:2/00", lx, ly); g2.drawString("O:2/01", lx, ly + 35); g2.drawString("O:2/02", lx, ly + 70);
        int rx = 312 + 70;
        g2.drawString("O:2/04", rx, ly); g2.drawString("O:2/05", rx, ly + 35); g2.drawString("O:2/06", rx, ly + 70);
        g2.drawString("I:1/00", 200, 360); g2.drawString("I:1/01", 390, 360);
    }
}