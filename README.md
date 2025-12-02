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

### ✅ Cenários de Simulação
1.  **Painel Padrão:** Botões e LEDs genéricos para testes lógicos.
2.  **Simulação Batch (Tanque):** Controle de nível, mistura e escoamento com animação de fluidos.
3.  **Semáforo (Novo):** Controle de tráfego com carros animados e física básica de frenagem/colisão.

---

## 🎨 Interface do Usuário

### Tela Principal
![Interface Principal](./docs/home_preview.png)
*Interface principal com editor de código e painel de simulação.*

### Nova Simulação: Semáforo
![Semáforo](./docs/traffic_light_preview.png)
*Novo cenário implementado para controle de tráfego.*

---

## ▶️ Como Executar

1.  Baixe o arquivo `.jar` na aba [Releases] ou compile o código fonte.
2.  Certifique-se de ter o **Java (JDK 22 ou superior)** instalado.
3.  Execute o simulador.
4.  Selecione o cenário desejado no menu "Simulação".
5.  Escreva ou carregue um código IL.
6.  Pressione **PLAY** ▶️.

---

## 📚 Referências e Créditos

Baseado no trabalho desenvolvido pelos alunos do semestre 2024/02:
🔗 [Repositório Base (Diogo-NB)](https://github.com/Diogo-NB/SimuladorClp)

Inspirado no software **LogixPro Simulator**.
___________________________________________________________________________________________________

Principais alterações desta versão (Release 2025):

✨ Novas Funcionalidades:
- Implementação completa da cena 'Traffic Light' (carros, semáforos, sensores indutivos e lógica de colisão).
- Novo design para os popups de 'Ajuda' e 'Sobre' utilizando HTML/CSS modernos.

🐛 Correções e Melhorias (Fixes):
- Correção crítica na inicialização de memórias (M0, T, C): leitura antes de escrita não gera mais erro.
- Otimização do Monitor de Variáveis (Data Table): atualização via 'upsert' elimina o piscar da tela.
- Correção no reset visual dos displays numéricos ao parar a simulação.
- Ajuste no parser para aceitar endereços com dígitos 0 e 9 corretamente.

📝 Documentação:
- Adicionados exemplos de código IL (Ex01 a Ex08) cobrindo lógica básica, timers, contadores e o novo semáforo.
__________________________________________________________________________

📦 Guia: Como Criar o Instalador Windows (.exe)
Como o projeto é em Java, o build padrão gera um arquivo .jar. Para atender ao requisito do professor ("dispor de um instalador em ambiente Windows"), precisamos de dois passos:
Transformar o .jar em um executável .exe (wrapper).
Empacotar esse .exe em um instalador (aquele com botão "Próximo > Próximo > Instalar").
🛠️ Ferramentas Necessárias (Gratuitas)
Launch4j: Para criar o executável. Baixar aqui
Inno Setup: Para criar o instalador. Baixar aqui
🔹 Passo 1: Criar o Executável (Launch4j)
O objetivo aqui é fazer o programa abrir clicando duas vezes, com ícone próprio, sem parecer um arquivo Java solto.
Abra o Launch4j.
Na aba Basic:
Output file: Escolha onde salvar e o nome (ex: SimuladorCLP.exe).
Jar: Selecione o arquivo dist/SimuladorClp.jar do seu projeto.
Icon: (Opcional) Selecione um arquivo .ico para ficar bonito (tem conversores online de png para ico).
Na aba JRE:
Min JRE version: Coloque a versão mínima (ex: 1.8.0 ou 22 dependendo de como compilou).
Clique no ícone de engrenagem (Build wrapper).
Resultado: Você terá um arquivo SimuladorCLP.exe. Teste se ele abre o programa.
🔹 Passo 2: Criar o Instalador (Inno Setup)
O objetivo é criar o arquivo Instalador_Simulador.exe que o professor vai usar para instalar o programa no computador dele.
Abra o Inno Setup Compiler.
Selecione "Create a new script file using the Script Wizard" (É o jeito mais fácil).
Preencha os dados:
Application Name: Simulador CLP
Version: 2025.1
Publisher: Seu Grupo
Application Files:
Application main executable file: Selecione o SimuladorCLP.exe que você criou no Passo 1.
Add files: Clique aqui e adicione a pasta lib (onde está o AbsoluteLayout.jar) e a pasta examples (para o professor ter os exemplos). Isso é crucial para o programa funcionar.
Continue clicando em "Next" (pode deixar as opções padrão de criar atalho na área de trabalho, etc).
No final, clique em Finish e ele vai pedir para compilar o script. Diga Sim.
Resultado: Ele vai gerar um arquivo (geralmente na pasta Output) chamado mysetup.exe (ou o nome que você definiu).
✅ O que entregar para o professor?
Quando ele pedir o instalador, você entrega apenas o arquivo final gerado pelo Inno Setup (ex: Instalador_Simulador_CLP.exe).
Quando ele rodar esse arquivo:
Vai abrir o assistente de instalação.
Vai instalar o programa em Arquivos de Programas.
Vai criar o atalho no Desktop.
O programa vai rodar perfeitamente com todas as dependências inclusas.
_______________________________________________________________________________



em ajuda:


colocar outro video de explicação 

colcoar o link dos exemplos prontos





