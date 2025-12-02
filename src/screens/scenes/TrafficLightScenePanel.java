package screens.scenes;

import ilcompiler.input.Input.InputType;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.*;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class TrafficLightScenePanel extends JPanel implements IScenePanel {

    // --- DEBUG: DESLIGADO ---
    private final boolean SHOW_DEBUG_GRID = false; 
    private int mouseX = 0;
    private int mouseY = 0;

    private InputEventListener inputListener;
    private Runnable onCriticalFailureCallback;

    // Botões
    private final PushButton buttonPedestre1;
    private final PushButton buttonPedestre2;
    
    // Botões de Park Individuais
    private final PushButton buttonParkBlue; // I0.4
    private final PushButton buttonParkRed;  // I0.5

    // Sliders de Velocidade
    private final JSlider sliderSpeedBlue;
    private final JSlider sliderSpeedRed;

    // Estados dos Semáforos (Saídas do CLP)
    private boolean nsRed, nsYellow, nsGreen;
    private boolean ewRed, ewYellow, ewGreen;
    private boolean pedLeftLight, pedRightLight; 

    // Estados de Entrada (Botões)
    private boolean p1Pressed = false;
    private boolean p2Pressed = false;
    private boolean parkBlueActive = false;
    private boolean parkRedActive = false;

    private boolean isSimulating = false;

    private Timer animationTimer;
    
    // Progresso dos carros (0.0 a 100.0)
    private float carEW_Progress = 0f; 
    private float carNS_Progress = 0f;
    
    private boolean crashOccurred = false;
    
    // Sensores
    private boolean carOnSensorEW = false;
    private boolean carOnSensorNS = false;

    // --- DIMENSÕES DO PAINEL ---
    private final int W = 624;
    private final int H = 394;
    
    // Centro aproximado do cruzamento (para medir distância)
    private final Point CENTER_POINT = new Point(312, 197);

    // --- COORDENADAS FIXAS DOS SENSORES ---
    private final Point sensorLeftPos = new Point(270, 230);
    private final Point sensorRightPos = new Point(358, 227);
    
    private final int SENSOR_W = 100;
    private final int SENSOR_H = 45;

    private final double ROAD_ANGLE = Math.toRadians(34); 

    // Posição da Torre
    private final int TOWER_X = 312;
    private final int TOWER_Y = 110;

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

        // --- BOTÕES DE PEDESTRE ---
        buttonPedestre1 = new PushButton("I0.0", InputType.NO);
        buttonPedestre1.setBounds(230, 310, 40, 40);

        buttonPedestre2 = new PushButton("I0.1", InputType.NO);
        buttonPedestre2.setBounds(350, 310, 40, 40);

        // --- CONTROLES LADO ESQUERDO (AZUL) ---
        JLabel lblBlue = new JLabel("Carro Azul");
        lblBlue.setForeground(Color.CYAN);
        lblBlue.setFont(new Font("Arial", Font.BOLD, 12));
        lblBlue.setBounds(20, 10, 100, 20);
        this.add(lblBlue);

        // Slider Azul
        sliderSpeedBlue = new JSlider(0, 50, 15);
        sliderSpeedBlue.setBounds(15, 30, 120, 20);
        sliderSpeedBlue.setOpaque(false);
        this.add(sliderSpeedBlue);

        // Botão Park Azul
        buttonParkBlue = new PushButton("I0.4", InputType.SWITCH); 
        buttonParkBlue.setBounds(45, 55, 60, 40);
        JLabel lblParkBlue = new JLabel("PARK (I0.4)");
        lblParkBlue.setForeground(Color.WHITE);
        lblParkBlue.setFont(new Font("Arial", Font.PLAIN, 10));
        lblParkBlue.setBounds(45, 95, 80, 15);
        this.add(lblParkBlue);


        // --- CONTROLES LADO DIREITO (VERMELHO) ---
        int rightMarginX = W - 140;

        JLabel lblRed = new JLabel("Carro Vermelho");
        lblRed.setForeground(new Color(255, 100, 100)); // Vermelho claro
        lblRed.setFont(new Font("Arial", Font.BOLD, 12));
        lblRed.setBounds(rightMarginX, 10, 120, 20);
        this.add(lblRed);

        // Slider Vermelho
        sliderSpeedRed = new JSlider(0, 50, 15);
        sliderSpeedRed.setBounds(rightMarginX - 5, 30, 120, 20);
        sliderSpeedRed.setOpaque(false);
        this.add(sliderSpeedRed);

        // Botão Park Vermelho
        buttonParkRed = new PushButton("I0.5", InputType.SWITCH, PushButton.ButtonPalette.RED);
        buttonParkRed.setBounds(rightMarginX + 25, 55, 60, 40);
        JLabel lblParkRed = new JLabel("PARK (I0.5)");
        lblParkRed.setForeground(Color.WHITE);
        lblParkRed.setFont(new Font("Arial", Font.PLAIN, 10));
        lblParkRed.setBounds(rightMarginX + 25, 95, 80, 15);
        this.add(lblParkRed);


        // Adiciona botões ao painel
        this.add(buttonPedestre1);
        this.add(buttonPedestre2);
        this.add(buttonParkBlue);
        this.add(buttonParkRed);

        // Habilita listener do mouse (apenas se precisar reativar debug futuramente)
        if (SHOW_DEBUG_GRID) {
            this.addMouseMotionListener(new MouseMotionListener() {
                @Override public void mouseDragged(MouseEvent e) { mouseX = e.getX(); mouseY = e.getY(); repaint(); }
                @Override public void mouseMoved(MouseEvent e) { mouseX = e.getX(); mouseY = e.getY(); repaint(); }
            });
        }

        animationTimer = new Timer(33, e -> {
            if (isSimulating && !crashOccurred) updateCars();
            repaint();
        });
        animationTimer.start();
    }

    private void updateCars() {
        
        // ==================================================================================
        // CARRO AZUL (Leste-Oeste / EW) -> OBEDECE 'ewRed' (Q0.0)
        // ==================================================================================
        if (parkBlueActive) {
            float speedBlue = sliderSpeedBlue.getValue() / 10.0f;
            float stopEW = 25f; 
            
            boolean shouldStop = ewRed && !ewGreen; 
            
            float nextPos = carEW_Progress + speedBlue;

            // --- Lógica de Movimento ---
            if (carEW_Progress < stopEW && nextPos >= stopEW) {
                if (shouldStop) {
                    carEW_Progress = stopEW; // Para na linha
                } else {
                    carEW_Progress = nextPos; // Continua
                }
            } else {
                if (Math.abs(carEW_Progress - stopEW) < 0.1f && shouldStop) {
                    // Mantém parado
                } else {
                    carEW_Progress = nextPos; // Move normal
                }
            }

            // Loop Infinito
            if (carEW_Progress > 100f) {
                carEW_Progress = 0f;
            }
            
            // --- Lógica do Sensor I0.2 (I:1/02) ---
            // Sensor de presença: ativo entre 25% e 39%
            if (carEW_Progress >= 25.0f && carEW_Progress <= 39.0f) {
                carOnSensorEW = true;
            } else {
                carOnSensorEW = false;
            }
        }

        // ==================================================================================
        // CARRO VERMELHO (Norte-Sul / NS) -> OBEDECE 'nsRed' (Q0.4)
        // ==================================================================================
        if (parkRedActive) {
            float speedRed = sliderSpeedRed.getValue() / 10.0f;
            float stopNS = 27f;
            
            boolean shouldStop = nsRed && !nsGreen;
            
            float nextPos = carNS_Progress + speedRed;

            // --- Lógica de Movimento ---
            if (carNS_Progress < stopNS && nextPos >= stopNS) {
                if (shouldStop) {
                    carNS_Progress = stopNS; // Para na linha
                } else {
                    carNS_Progress = nextPos; // Continua
                }
            } else {
                if (Math.abs(carNS_Progress - stopNS) < 0.1f && shouldStop) {
                    // Mantém parado
                } else {
                    carNS_Progress = nextPos; // Move normal
                }
            }

            // Loop Infinito
            if (carNS_Progress > 100f) {
                carNS_Progress = 0f;
            }
            
            // --- Lógica do Sensor I0.3 (I:1/03) ---
            // Sensor de presença: ativo entre 27% e 41%
            if (carNS_Progress >= 27.0f && carNS_Progress <= 41.0f) {
                carOnSensorNS = true;
            } else {
                carOnSensorNS = false;
            }
        }

        // --- COLISÃO CALIBRADA ---
        if (carEW_Progress > 38.0f && carEW_Progress < 50.0f && 
            carNS_Progress > 33.5f && carNS_Progress < 44.0f) {
            handleCrash();
        }
    }

    private void handleCrash() {
        crashOccurred = true;
        repaint();
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, "COLISÃO! Ocorreu um acidente no cruzamento.", "Falha Crítica", JOptionPane.ERROR_MESSAGE);
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
        inputsType.put("I0.4", InputType.SWITCH); inputs.put("I0.4", false); 
        inputsType.put("I0.5", InputType.SWITCH); inputs.put("I0.5", false); 
    }

    @Override
    public void updateUIState(Map<String, InputType> inputsType, Map<String, Boolean> inputs, Map<String, Boolean> outputs) {
        isSimulating = true;
        
        ewRed    = outputs.getOrDefault("Q0.0", false);
        ewYellow = outputs.getOrDefault("Q0.1", false);
        ewGreen  = outputs.getOrDefault("Q0.2", false);
        
        pedLeftLight = outputs.getOrDefault("Q0.3", false); 
        
        nsRed    = outputs.getOrDefault("Q0.4", false);
        nsYellow = outputs.getOrDefault("Q0.5", false);
        nsGreen  = outputs.getOrDefault("Q0.6", false);
        
        pedRightLight = outputs.getOrDefault("Q0.7", false);
        
        p1Pressed = inputs.getOrDefault("I0.0", false);
        p2Pressed = inputs.getOrDefault("I0.1", false);
        parkBlueActive = inputs.getOrDefault("I0.4", false);
        parkRedActive = inputs.getOrDefault("I0.5", false);

        inputs.put("I0.2", carOnSensorEW);
        inputs.put("I0.3", carOnSensorNS);
    }

    @Override
    public void resetUIState() {
        nsRed = nsYellow = nsGreen = false;
        ewRed = ewYellow = ewGreen = false;
        pedLeftLight = pedRightLight = false;
        p1Pressed = p2Pressed = false;
        
        carEW_Progress = 0f; 
        carNS_Progress = 0f;
        carOnSensorEW = false; 
        carOnSensorNS = false;
        
        crashOccurred = false; 
        isSimulating = false;
        repaint();
    }

    @Override public void setInputListener(InputEventListener l) { 
        this.inputListener = l; 
        buttonPedestre1.setInputEventListener(l); 
        buttonPedestre2.setInputEventListener(l); 
        buttonParkBlue.setInputEventListener(l);
        buttonParkRed.setInputEventListener(l);
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
        
        // DEBUG: Zonas de Colisão (Onde os carros batem)
        if (SHOW_DEBUG_GRID) {
             drawCrashZones(g2);
        }

        // Sensores
        drawLaneFeature(g2, sensorLeftPos.x, sensorLeftPos.y, true);
        drawLaneFeature(g2, sensorRightPos.x, sensorRightPos.y, false);

        // Carros
        drawCars(g2);

        // Torre Central
        drawCentralTower(g2, TOWER_X, TOWER_Y);

        // Sinais de Pedestre
        drawPedestrianSignals(g2);

        // Decoração dos Botões (Setas VERMELHAS Rotacionadas e Menores)
        drawButtonDecorations(g2);

        // Labels
        drawLabels(g2);
        
        // Explosão (Topo)
        if (crashOccurred) drawExplosion(g2);
        
        if (SHOW_DEBUG_GRID) drawDebugOverlay(g2);
    }
    
    // --- MÉTODOS DE VISUALIZAÇÃO DEBUG (Desativados por flag) ---
    private void drawDebugOverlay(Graphics2D g2) {
        // Mira do Mouse
        g2.setColor(new Color(255, 255, 255, 100));
        g2.setStroke(new BasicStroke(1));
        g2.drawLine(0, mouseY, W, mouseY);
        g2.drawLine(mouseX, 0, mouseX, H);

        // Coordenadas Mouse
        String mouseCoords = "MOUSE X: " + mouseX + " Y: " + mouseY;
        g2.setColor(Color.BLACK);
        g2.fillRect(mouseX + 10, mouseY + 10, 140, 20);
        g2.setColor(Color.YELLOW);
        g2.setFont(new Font("Monospaced", Font.BOLD, 12));
        g2.drawString(mouseCoords, mouseX + 15, mouseY + 24);
        
        // Centro do Cruzamento
        g2.setColor(Color.MAGENTA);
        g2.fillOval(CENTER_POINT.x - 3, CENTER_POINT.y - 3, 6, 6);
    }
    
    private void drawCrashZones(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 255, 50)); // Zona Azul
        g2.fillOval(250, 180, 100, 60); 
        g2.setColor(new Color(255, 0, 0, 50)); // Zona Vermelha
        g2.fillOval(280, 160, 60, 100);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.PLAIN, 10));
        g2.drawString("CRASH ZONE", 280, 200);
    }

    private void drawGrass(Graphics2D g2) {
        g2.setColor(GRASS_COLOR);
        g2.fillRect(0, 0, getWidth(), getHeight());
    }

    private void drawRoads(Graphics2D g2) {
        int roadW = 150;
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

    private void drawLaneFeature(Graphics2D g2, int cx, int cy, boolean isBlueRoad) {
        AffineTransform old = g2.getTransform();
        g2.translate(cx, cy);
        
        double angle;
        if (isBlueRoad) angle = ROAD_ANGLE + Math.toRadians(12); 
        else            angle = -ROAD_ANGLE + Math.toRadians(-12); 
        
        g2.rotate(angle);

        int w = SENSOR_W; 
        int h = SENSOR_H; 
        
        int lx = -w / 2;
        int ly = 8; 
        
        // Indicador Visual do Sensor (Aceso/Apagado)
        boolean sensorActive = isBlueRoad ? carOnSensorEW : carOnSensorNS;
        
        if (sensorActive) {
            g2.setColor(new Color(255, 255, 0, 150)); // Amarelo se ativo
        } else {
            g2.setColor(new Color(100, 100, 100, 80)); // Cinza se inativo
        }
        g2.fillRect(lx, ly, w, h);
        
        g2.setColor(Color.DARK_GRAY);
        g2.setStroke(new BasicStroke(2));
        g2.drawRect(lx, ly, w, h);

        g2.setColor(Color.WHITE);
        int stripes = 6; 
        int stripW = (w / stripes) - 4; 
        for (int i = 0; i < stripes; i++) {
            g2.fillRect(lx + 4 + (i * (w / stripes)), ly + 2, stripW, h - 4);
        }
        
        g2.setStroke(new BasicStroke(5));
        g2.drawLine(lx - 5, ly, lx - 5, ly + h);

        g2.rotate(-angle); 
        g2.setColor(Color.BLACK); 
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        if (isBlueRoad) g2.drawString("I:1/02", -20, 50);
        else            g2.drawString("I:1/03", -30, 50);

        g2.setTransform(old);
    }

    private void drawCars(Graphics2D g2) {
        // --- Carro Azul ---
        double startX_B = -100; double startY_B = H + 63; 
        double endX_B = W + 0; double endY_B = -140;
        
        double curX_B = startX_B + (endX_B - startX_B) * (carEW_Progress / 100.0);
        double curY_B = startY_B + (endY_B - startY_B) * (carEW_Progress / 100.0);
        
        double blueOffsetX = 110;
        double blueOffsetY = -30; 
        
        int drawX_B = (int)(curX_B + blueOffsetX);
        int drawY_B = (int)(curY_B + blueOffsetY);

        if (carEW_Progress > 0 && carEW_Progress < 100) {
            drawRotatedCar(g2, drawX_B, drawY_B, Color.BLUE, -ROAD_ANGLE - Math.toRadians(5));
            
            // DEBUG INFO AZUL (Oculto)
            if (SHOW_DEBUG_GRID) {
                g2.setColor(Color.CYAN);
                g2.drawLine(drawX_B, drawY_B, CENTER_POINT.x, CENTER_POINT.y);
                double dist = Point2D.distance(drawX_B, drawY_B, CENTER_POINT.x, CENTER_POINT.y);
                String info = String.format("Pos[%d,%d] Prog:%.1f%% Dist:%dpx", drawX_B, drawY_B, carEW_Progress, (int)dist);
                g2.setFont(new Font("Arial", Font.BOLD, 11));
                g2.drawString(info, drawX_B - 60, drawY_B - 20);
            }
        }

        // --- Carro Vermelho ---
        double startX_R = W + 100; double startY_R = H + 63;
        double endX_R = 0; double endY_R = -140;
        
        double curX_R = startX_R + (endX_R - startX_R) * (carNS_Progress / 100.0);
        double curY_R = startY_R + (endY_R - startY_R) * (carNS_Progress / 100.0);

        double redOffsetX = -110;
        double redOffsetY = 0;
        
        int drawX_R = (int)(curX_R + redOffsetX);
        int drawY_R = (int)(curY_R + redOffsetY);

        if (carNS_Progress > 0 && carNS_Progress < 100) {
            drawRotatedCar(g2, drawX_R, drawY_R, Color.RED, Math.PI + ROAD_ANGLE + Math.toRadians(5));
            
            // DEBUG INFO VERMELHO (Oculto)
            if (SHOW_DEBUG_GRID) {
                g2.setColor(Color.PINK);
                g2.drawLine(drawX_R, drawY_R, CENTER_POINT.x, CENTER_POINT.y);
                double dist = Point2D.distance(drawX_R, drawY_R, CENTER_POINT.x, CENTER_POINT.y);
                String info = String.format("Pos[%d,%d] Prog:%.1f%% Dist:%dpx", drawX_R, drawY_R, carNS_Progress, (int)dist);
                g2.setFont(new Font("Arial", Font.BOLD, 11));
                g2.drawString(info, drawX_R + 20, drawY_R - 20);
            }
        }
    }

    private void drawRotatedCar(Graphics2D g2, int x, int y, Color c, double angle) {
        AffineTransform old = g2.getTransform();
        g2.translate(x, y);
        g2.rotate(angle);
        
        int w = 55; int h = 28;
        int carY = 35 - (h/2); 
        
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
        
        // Ponto central do carro para debug (Oculto)
        if (SHOW_DEBUG_GRID) {
            g2.setColor(Color.WHITE);
            g2.fillRect(-2, carY + h/2 - 2, 4, 4);
        }
        
        g2.setTransform(old);
    }

    private void drawExplosion(Graphics2D g2) {
        int cx = 312; 
        int cy = 200; 
        g2.setColor(new Color(255, 69, 0, 220)); 
        g2.fillOval(cx-50, cy-40, 100, 80); 
        g2.setColor(Color.ORANGE);
        g2.fillOval(cx-30, cy-25, 60, 50);
        g2.setColor(Color.YELLOW);
        g2.setFont(new Font("Arial Black", Font.BOLD, 26));
        g2.drawString("CRASH!", cx-55, cy+10);
        g2.setStroke(new BasicStroke(3));
        g2.setColor(Color.BLACK);
        g2.drawOval(cx-50, cy-40, 100, 80);
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

        // Lado Esquerdo (Blue/EW) -> ew...
        drawLight(g2, x - 45, y - 95, ewRed, Color.RED);
        drawLight(g2, x - 45, y - 60, ewYellow, Color.YELLOW);
        drawLight(g2, x - 45, y - 25, ewGreen, Color.GREEN);
        
        // Lado Direito (Red/NS) -> ns...
        drawLight(g2, x + 15, y - 95, nsRed, Color.RED);
        drawLight(g2, x + 15, y - 60, nsYellow, Color.YELLOW);
        drawLight(g2, x + 15, y - 25, nsGreen, Color.GREEN);
    }

    private void drawLight(Graphics2D g2, int x, int y, boolean on, Color c) {
        g2.setColor(on ? c : c.darker().darker().darker());
        g2.fillOval(x, y, 30, 30);
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(1)); g2.drawOval(x, y, 30, 30);
    }

    private void drawPedestrianSignals(Graphics2D g2) {
        drawSignalPost(g2, 160, 240, pedLeftLight, "O:2/03");
        drawSignalPost(g2, 465, 240, pedRightLight, "O:2/07");
    }

    private void drawSignalPost(Graphics2D g2, int x, int y, boolean active, String label) {
        // 1. O Poste
        g2.setColor(Color.BLACK);
        g2.fillRect(x - 6, y - 60, 12, 60);

        // 2. A Caixa do sinal (80x50)
        g2.setColor(Color.GRAY);
        g2.fill3DRect(x - 40, y - 110, 80, 50, true);

        // 3. Configuração do Texto
        g2.setFont(new Font("Monospaced", Font.BOLD, 20));

        if (active) g2.setColor(new Color(255, 140, 0)); // Texto Laranja Aceso
        else        g2.setColor(new Color(60, 20, 0));   // Texto Apagado

        // Texto "DONT" (Subi um pouco para -85 para caber a bolinha em baixo)
        g2.drawString("DONT", x - 25, y - 85);

        // --- NOVA PARTE: A BOLINHA VERMELHA ---
        if (active) {
            g2.setColor(Color.RED); // Vermelho vivo quando ativo
        } else {
            g2.setColor(new Color(40, 0, 0)); // Vermelho muito escuro (apagado)
        }
        
        // Desenha a bolinha centralizada abaixo do texto
        // x - 6 (para centralizar uma bolinha de 12px)
        // y - 78 (posição vertical logo abaixo do texto)
        g2.fillOval(x - 6, y - 78, 12, 12);
        
        // Borda preta fina na bolinha para acabamento
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(1));
        g2.drawOval(x - 6, y - 78, 12, 12);
        // --------------------------------------

        // 4. Label Lateral (Endereço da Saída)
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
        g2.drawString(label, x + 45, y - 90);
    }
    
    private void drawButtonDecorations(Graphics2D g2) {
        drawRotatedArrow(g2, 250, 290, true, p1Pressed);
        drawRotatedArrow(g2, 370, 290, false, p2Pressed);
    }

    private void drawRotatedArrow(Graphics2D g2, int x, int y, boolean isLeftButton, boolean isPressed) {
        AffineTransform old = g2.getTransform();
        g2.translate(x, y);
        
        double angle = isLeftButton ? Math.toRadians(-45) : Math.toRadians(45);
        g2.rotate(angle);
        
        Polygon arrow = new Polygon();
        int w = 8; 
        int h = 10; 
        int stemW = 4; 
        int stemH = 10; 
        
        // Ponta
        arrow.addPoint(0, -h - stemH); 
        arrow.addPoint(-w, -stemH);
        arrow.addPoint(-stemW, -stemH);
        arrow.addPoint(-stemW, stemH);
        arrow.addPoint(stemW, stemH);
        arrow.addPoint(stemW, -stemH);
        arrow.addPoint(w, -stemH);
        
        if (isPressed) {
            g2.setColor(Color.CYAN); // Brilhando
        } else {
            g2.setColor(new Color(200, 0, 0)); // Vermelho normal
        }
        
        g2.fillPolygon(arrow);
        
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(1));
        g2.drawPolygon(arrow);
        
        g2.setTransform(old);
    }

    private void drawLabels(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
        int ly = TOWER_Y - 70;
        int lx = TOWER_X - 120; 
        
        // OUTPUTS (O:)
        g2.drawString("O:2/00", lx, ly); g2.drawString("O:2/01", lx, ly + 35); g2.drawString("O:2/02", lx, ly + 70);
        int rx = TOWER_X + 70;
        g2.drawString("O:2/04", rx, ly); g2.drawString("O:2/05", rx, ly + 35); g2.drawString("O:2/06", rx, ly + 70);
        
        // INPUTS (I:1/xx) - Labels dos botões de pedestre
        g2.drawString("I:1/00", 200, 360); g2.drawString("I:1/01", 390, 360);
    }
}