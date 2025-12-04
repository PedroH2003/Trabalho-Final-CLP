# 🤖 Simulador de CLP com Interface Interativa (Instruction List - IL)

📚 **Disciplina:** Controladores Lógicos Programáveis (CLP)
🎓 **Curso:** Engenharia de Computação
🏫 **Instituição:** IFTM - Instituto Federal do Triângulo Mineiro
📍 **Campus:** Uberaba - Parque Tecnológico
👨‍🏫 **Professor:** Robson Rodrigues

---

## 🚀 Novidades da Versão 2025 (Current Release)

Esta versão traz melhorias significativas de estabilidade e novas funcionalidades em relação ao projeto original:

### ✨ Novas Funcionalidades
1.  **🚦 Simulação de Semáforo (Traffic Light):**
    *   Novo cenário interativo simulando um cruzamento real.
    *   Controle de semáforos para carros (Norte-Sul e Leste-Oeste) e pedestres.
    *   Sensores de presença indutiva no asfalto.
    *   Sistema de detecção de colisão e falha crítica.
2.  **📖 Interface de Ajuda Renovada:**
    *   Pop-up de ajuda formatado em HTML/CSS para facilitar a leitura dos comandos.
    *   Exemplos práticos de código embutidos na interface.

### 🐛 Correções de Bugs e Melhorias (Fixes)
*   **Monitor de Variáveis (Data Table):** Otimização completa da tabela. Agora ela atualiza em tempo real (`upsert`) sem recriar as linhas, eliminando o "piscar" da tela e melhorando a performance.
*   **Correção de Memória (M0, T, C):** Corrigido bug onde memórias lidas antes de serem escritas causavam erro. Agora elas são auto-inicializadas.
*   **Display de Numéricos:** Correção na limpeza visual dos displays de Temporizadores e Contadores ao reiniciar a simulação (botão Stop/Start).
*   **Interpretador:** Melhoria no *parser* para identificar corretamente endereços de memória contendo dígitos 0 e 9.

---

## 👥 Desenvolvedores

### 🔹 Grupo Atual (Desenvolvimento 2025)
*   **Jamilly Moura**
*   **Pedro Franco de Camargo**
*   **Pedro Henrique Cândido Silva**

### 📅 Membros do Grupo Anterior (2024/02)
*   Diogo Nunes
*   José Arantes
*   Vinicius Barbosa
*   Yuri Duarte

*(O projeto é uma evolução contínua desenvolvida por diversas turmas do curso).*

---

## 🛠️ Funcionalidades Principais

### 📝 Lista de Instruções Suportadas (IL)
O compilador suporta as instruções básicas da norma IEC 61131-3:
*   **Lógica:** `LD`, `LDN`, `ST`, `STN`, `AND`, `ANDN`, `OR`, `ORN`
*   **Temporizadores:** `TON`, `TOF` (T1 a T10)
*   **Contadores:** `CTU`, `CTD` (C1 a C10)
*   **Endereçamento:**
    *   Entradas: `I0.0` a `I1.7`
    *   Saídas: `Q0.0` a `Q1.7`
    *   Memórias Auxiliares: `M0`, `M1`...

### ✅ Modos de Operação
*   🛠️ **PROGRAM:** Edição livre do código.
*   ⏸️ **STOP:** Sistema parado, saídas resetadas.
*   ▶️ **RUN:** Execução cíclica do programa (Scan Cycle).
*   🔄 **RESET:** Reinicia a simulação, limpa a memória e reseta os contadores/temporizadores.

### ✅ Cenários de Simulação
1.  **Painel Padrão:** Botões e LEDs genéricos para testes lógicos.
2.  **Simulação Batch (Tanque):** Controle de nível, mistura e escoamento com animação de fluidos.
3.  **Semáforo (Novo):** Controle de tráfego com carros animados e física básica de frenagem/colisão.

---

## 🎨 Interface do Usuário

### 📄 Relatório Técnico e Manual
[**Interface Principal**](https://github.com/PedroH2003/Trabalho-Final-CLP/blob/main/Relat%C3%B3rio%20-%20SIMULADOR%20DE%20CLP%20-%20Sem%C3%A1foro.pdf)
*Clique na imagem acima para acessar o PDF completo com o relatório e manual.*

### 🎥 Demonstração: Simulação de Semáforo
[**Nova Simulação: Semáforo**](https://www.youtube.com/watch?v=Y22ag0oGnH0)
*Clique na imagem para assistir ao vídeo da simulação de tráfego funcionando.*

### 💾 Download do Software
[**📥 Baixar Instalador_Simulador_CLP.exe**](https://github.com/PedroH2003/Trabalho-Final-CLP/blob/main/Instalador_Simulador_CLP.exe)
*Clique no link e procure o botão de download (ícone [...]) para baixar o instalador para Windows.*

---

## ▶️ Como Executar

1.  Baixe o arquivo `Instalador_Simulador_CLP.exe`

2.  Atualize seu java (jdk) para versão igual ou acima a 23
---


## 📚 Referências e Créditos

Baseado no trabalho desenvolvido pelos alunos do semestre 2024/02:
🔗 [Repositório Base (Diogo-NB)](https://github.com/Diogo-NB/SimuladorClp)

Inspirado no software **LogixPro Simulator**.
_______________________________________________________






