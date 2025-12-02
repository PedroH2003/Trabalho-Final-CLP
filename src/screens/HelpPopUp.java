package screens;

import java.awt.Desktop;
import java.net.URI;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class HelpPopUp {

    public static void showHelp() {
        String htmlContent = """
    <html>
    <head>
        <style>
            body { font-family: Arial, sans-serif; background-color: #ffffff; color: #333333; padding: 10px; font-size: 11px; }
            h2 { color: #0056b3; border-bottom: 2px solid #0056b3; padding-bottom: 5px; margin-top: 0; }
            h3 { color: #d9534f; margin-top: 15px; margin-bottom: 5px; font-size: 12px; }
            .concept { background-color: #e9ecef; padding: 5px; border-radius: 4px; margin-bottom: 5px; }
            .code-box { background-color: #2d2d2d; color: #50fa7b; padding: 8px; border-radius: 4px; font-family: Consolas, monospace; margin: 5px 0; }
            .note { color: #666; font-style: italic; font-size: 10px; }
            strong { color: #000; }
            li { margin-bottom: 4px; }
        </style>
    </head>
    <body>
        <h2>📖 Guia Rápido de Programação IL</h2>
        
        <div class='concept'>
            <strong>Como funciona?</strong> O CLP lê o código linha por linha. Imagine que você está montando uma frase lógica: <br>
            <i>"SE (botão apertado) E (sensor ativo) ENTÃO (ligue a lâmpada)"</i>.
        </div>

        <h3>1. Endereços (Quem é quem?)</h3>
        <ul>
            <li><strong>I0.0 a I1.7:</strong> Entradas (Botões, Sensores, Chaves).</li>
            <li><strong>Q0.0 a Q1.7:</strong> Saídas (Lâmpadas, Motores, Semáforos).</li>
            <li><strong>M0, M1...:</strong> Memórias (Guardam valor temporariamente, não existem no mundo físico).</li>
            <li><strong>T1, T2...:</strong> Temporizadores.</li>
            <li><strong>C1, C2...:</strong> Contadores.</li>
        </ul>

        <h3>2. Comandos Básicos (Lógica)</h3>
        <ul>
            <li><strong>LD (Load):</strong> Começa uma nova lógica ("Se...").</li>
            <li><strong>AND:</strong> Adiciona uma condição ("E...").</li>
            <li><strong>OR:</strong> Cria uma alternativa ("OU...").</li>
            <li><strong>ST (Store):</strong> Finaliza enviando para uma saída ("Então ligue...").</li>
            <li><strong>N (Sufixo):</strong> Negação (Inverso). Ex: <code>LDN</code> (Se NÃO apertar), <code>ANDN</code> (E NÃO estiver ativo).</li>
        </ul>
        <div class='code-box'>
            LD I0.0 &nbsp;&nbsp;&nbsp;&nbsp;(Se apertar I0.0)<br>
            ANDN I0.1 &nbsp;&nbsp;(E NÃO apertar I0.1)<br>
            ST Q0.0 &nbsp;&nbsp;&nbsp;&nbsp;(Então ligue Q0.0)
        </div>

        <h3>3. Temporizadores (TON / TOFF)</h3>
        <p>Usados para esperar um tempo antes de ligar ou desligar.</p>
        <ul>
            <li><strong>Configurar:</strong> <code>TON T1,20</code> (Cria T1 com 20 décimos de segundo = 2s).</li>
            <li><strong>Ativar:</strong> Use <code>ST T1</code> para iniciar a contagem.</li>
            <li><strong>Ler:</strong> Use <code>LD T1</code> para saber se o tempo acabou.</li>
        </ul>
        <div class='code-box'>
            TON T1,20 &nbsp;&nbsp;(Configura T1 para 2 seg)<br>
            LD I0.0 &nbsp;&nbsp;&nbsp;&nbsp;(Se botão I0.0...)<br>
            ST T1 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(Inicia contagem do T1)<br>
            LD T1 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(Se T1 acabou a contagem...)<br>
            ST Q0.0 &nbsp;&nbsp;&nbsp;&nbsp;(Ligue Q0.0)
        </div>

        <h3>4. Contadores (CTU / CTD)</h3>
        <p>Contam quantas vezes um evento ocorreu.</p>
        <ul>
            <li><strong>Configurar:</strong> <code>CTU C1,3</code> (Conta até 3 para ativar).</li>
            <li><strong>Contar:</strong> Use <code>ST C1</code> para enviar o pulso de contagem.</li>
            <li><strong>Ler:</strong> Use <code>LD C1</code> para saber se atingiu a meta.</li>
        </ul>
        <div class='code-box'>
            CTU C1,3 &nbsp;&nbsp;&nbsp;(Meta: 3 pulsos)<br>
            LD I0.0 &nbsp;&nbsp;&nbsp;&nbsp;(Ler botão)<br>
            ST C1 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(Envia pulso para C1)<br>
            LD C1 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(Se C1 chegou em 3...)<br>
            ST Q0.0 &nbsp;&nbsp;&nbsp;&nbsp;(Ligue a saída)
        </div>

        <h3>5. Memória e Selo (Manter ligado)</h3>
        <p>Como fazer um botão de campainha virar um interruptor? Usamos memória!</p>
        <div class='code-box'>
            LD I0.0 &nbsp;&nbsp;&nbsp;&nbsp;(Botão Liga)<br>
            OR M0 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(OU a memória já ligada)<br>
            ANDN I0.1 &nbsp;&nbsp;(E o botão Desliga solto)<br>
            ST M0 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(Salva na memória M0)<br>
            LD M0 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(Lê a memória)<br>
            ST Q0.0 &nbsp;&nbsp;&nbsp;&nbsp;(Liga a luz real)
        </div>

        <br>
        <div class="footer">
            🎥 <a href='https://www.youtube.com/watch?v=e-C53fbtbfo'>Vídeo Tutorial no YouTube</a> &nbsp;|&nbsp; 
            💻 <a href='https://github.com/PedroH2003/Trabalho-Final-CLP/tree/main/examples/Batch'>Baixar Exemplos Batch</a>
            💻 <a href='https://github.com/PedroH2003/Trabalho-Final-CLP/tree/main/examples/Traffic-light'>Baixar Exemplos Traffic light</a>
        </div>
    </body>
    </html>
""";
        JEditorPane editorPane = new JEditorPane("text/html", htmlContent);
        editorPane.setEditable(false);
        editorPane.setOpaque(false);

        editorPane.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    try {
                        Desktop.getDesktop().browse(new URI(e.getURL().toString()));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(editorPane);
        scrollPane.setPreferredSize(new java.awt.Dimension(500, 500)); // Aumentei um pouco para caber o guia

        JOptionPane.showMessageDialog(null, scrollPane, "Manual do Usuário - CLP", JOptionPane.PLAIN_MESSAGE);
    }
}