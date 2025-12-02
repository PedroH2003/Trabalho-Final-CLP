package screens;

import java.awt.Desktop;
import java.net.URI;
import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class SobrePopup {

    public static void mostrarSobre() {
        String htmlContent = """
            <html>
            <head>
                <style>
                    body { font-family: 'Segoe UI', Arial, sans-serif; background-color: #ffffff; color: #333; padding: 15px; font-size: 12px; }
                    
                    /* Títulos */
                    h2 { color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 8px; margin-top: 0; }
                    h3 { color: #e74c3c; margin-top: 20px; margin-bottom: 8px; font-size: 13px; font-weight: bold; }
                    
                    /* Caixas de Texto (Conceito) */
                    .concept { background-color: #f0f8ff; padding: 10px; border-radius: 5px; border-left: 5px solid #3498db; margin-bottom: 15px; color: #2c3e50;}
                    
                    /* Listas de Nomes */
                    ul { margin: 5px 0; padding-left: 25px; color: #555; }
                    li { margin-bottom: 3px; }
                    
                    /* Estilo dos Links (Parecem botões/cards agora) */
                    .repo-container { margin-top: 5px; }
                    .repo-item { 
                        background-color: #f8f9fa; 
                        border: 1px solid #ddd; 
                        margin-bottom: 6px; 
                        padding: 8px; 
                        border-radius: 4px;
                    }
                    /* O link em si */
                    a { 
                        color: #0066cc; 
                        text-decoration: none; 
                        font-weight: bold; 
                        font-size: 12px;
                    }
                    /* Pequena descrição ao lado do link */
                    .repo-desc { color: #777; font-size: 10px; margin-left: 5px; }

                    /* Rodapé */
                    .note { color: #888; font-style: italic; font-size: 10px; margin-top: 25px; border-top: 1px solid #eee; padding-top: 10px; text-align: center; }
                </style>
            </head>
            <body>
                <h2>ℹ️ Sobre o Projeto</h2>
                
                <div class='concept'>
                    Compilador e simulador para a linguagem <b>Instruction List (IL)</b>, desenvolvido colaborativamente.
                </div>

                <h3>👥 Desenvolvedores Atuais (2025)</h3>
                <ul>
                    <li>Jamilly Moura</li>
                    <li>Pedro Franco de Camargo</li>
                    <li>Pedro Henrique Cândido Silva</li>
                </ul>

                <h3>📅 Histórico de Membros</h3>
                <ul>
                    <li><b>2024/02:</b> Diogo Nunes, José Arantes, Vinicius Barbosa, Yuri Duarte</li>
                    <li><b>Anteriores:</b> Bruno Rodrigues, Iasmin Pieraço, Igor Vendramini, Peterson, Vinicius Patrick</li>
                </ul>

                <h3>💻 Código Fonte (GitHub)</h3>
                <div class='repo-container'>
                    <!-- Repositório Atual -->
                    <div class='repo-item' style='background-color: #e8f4fd; border-color: #b6e0fe;'>
                        🚀 <a href='https://github.com/PedroH2003/Trabalho-Final-CLP'>Acessar Repositório Atual</a>
                        <span class='repo-desc'>(Versão em uso)</span>
                    </div>

                    <!-- Repositórios Antigos -->
                    <div class='repo-item'>
                        📂 <a href='https://github.com/Diogo-NB/SimuladorClp'>Repositório 2024/02</a>
                        <span class='repo-desc'>(Base anterior)</span>
                    </div>
                    
                    <div class='repo-item'>
                        📂 <a href='https://github.com/IasminPieraco/Trabalho-Final-CLP'>Repositório 2024/01</a>
                    </div>
                    
                    <div class='repo-item'>
                        📂 <a href='https://github.com/Emanuelle-Oliveira/compilador-il-clp'>Repositório Inicial</a>
                    </div>
                </div>

                <div class='note'>
                    "O sucesso é a soma de pequenos esforços repetidos dia após dia."
                    <br>O projeto evolui a cada semestre com novas turmas.
                </div>
            </body>
            </html>
            """;

        JEditorPane editorPane = new JEditorPane("text/html", htmlContent);
        editorPane.setEditable(false);
        editorPane.setOpaque(false);

        // Listener para cliques
        editorPane.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    try {
                        String url = e.getURL().toString();
                        if (!url.contains("insira o link aqui")) {
                            Desktop.getDesktop().browse(new URI(url));
                        } else {
                            JOptionPane.showMessageDialog(null, 
                                "O link do repositório atual ainda não foi definido no código.", 
                                "Link Indisponível", 
                                JOptionPane.WARNING_MESSAGE);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(editorPane);
        scrollPane.setPreferredSize(new java.awt.Dimension(520, 550));
        // Remove a borda do scrollpane para ficar mais limpo
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        JOptionPane.showMessageDialog(null, scrollPane, "Sobre o Projeto", JOptionPane.PLAIN_MESSAGE);
    }
}