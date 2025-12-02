# 🧪 Guia de Testes e Validação - Simulador CLP

Este documento descreve o objetivo de cada exemplo incluído e o roteiro de testes para validar o funcionamento do simulador.

---

## 📂 1. Ex01_Basico_Logica.txt
**Objetivo:** Testar portas lógicas básicas (AND, OR) e acionamento direto.

*   **Ação 1:** Não aperte nada.
    *   **Resultado:** Tudo apagado.
*   **Ação 2:** Aperte e segure **I0.0** (Botão Pedestre Esq).
    *   **Resultado:** Acende Vermelho Esq (**Q0.0**) e Pedestre Esq (**Q0.3**).
*   **Ação 3:** Aperte e segure **I0.1** (Botão Pedestre Dir).
    *   **Resultado:** Acende Amarelo Esq (**Q0.1**) e Pedestre Esq (**Q0.3**).
*   **Ação 4:** Aperte **I0.0 E I0.1** ao mesmo tempo.
    *   **Resultado:** Acende Verde Esq (**Q0.2**) (Lógica AND).

---

## 📂 2. Ex02_Logica_Inversa.txt
**Objetivo:** Testar se o compilador entende instruções negadas (`LDN`, `STN`, `ANDN`, `ORN`).

*   **Ação 1:** Não aperte nada (Estado inicial).
    *   **Resultado:** Vermelho Esq (**Q0.0**) ACESO (`LDN` inverte 0 para 1).
    *   **Resultado:** Pedestre Esq (**Q0.3**) ACESO (Lógica NAND/ORN).
*   **Ação 2:** Aperte **I0.0**.
    *   **Resultado:** Vermelho Esq (**Q0.0**) APAGA.
    *   **Resultado:** Verde Esq (**Q0.2**) ACENDE (Se I0.1 estiver solto).
*   **Ação 3:** Aperte **I0.1**.
    *   **Resultado:** Amarelo Esq (**Q0.1**) ACENDE. (Lógica de dupla negação: `LDN` inverte, `STN` inverte de volta).

---

## 📂 3. Ex03_Temporizadores.txt
**Objetivo:** Diferenciar TON (Delay para ligar) de TOF (Delay para desligar).

*   **Ação 1:** Aperte e **SEGURE I0.0**.
    *   **Imediato:** Amarelo Esq (**Q0.1**) acende na hora (`TOF` carrega a energia).
    *   **Após ~2 segundos:** Vermelho Esq (**Q0.0**) acende (`TON` completou o tempo).
*   **Ação 2:** **SOLTE I0.0**.
    *   **Imediato:** Vermelho Esq (**Q0.0**) apaga na hora.
    *   **Após ~3 segundos:** Amarelo Esq (**Q0.1**) apaga (`TOF` conta o tempo após o corte de energia).

---

## 📂 4. Ex04_Contadores.txt
**Objetivo:** Testar contagem crescente e decrescente. Preset = 3.

*   **Ação 1:** Dê cliques rápidos em **I0.0** (Botão Esq).
    *   1º Clique: Nada.
    *   2º Clique: Nada.
    *   3º Clique: Vermelho Esq (**Q0.0**) ACENDE e fica aceso.
*   **Ação 2:** Dê mais cliques em **I0.0**.
    *   **Resultado:** Continua aceso (Contador > 3).
*   **Ação 3:** Dê um clique em **I0.1** (Botão Dir).
    *   **Resultado:** Vermelho Esq (**Q0.0**) APAGA (O contador diminuiu o valor).

---

## 📂 5. Ex05_Semaforo_Automatico.txt
**Objetivo:** Teste de estresse e lógica sequencial complexa.

*   **Pré-requisito:** Ative **PARK** (**I0.4** e **I0.5**) para os carros andarem.
*   **O que observar:** O ciclo deve rodar sozinho infinitamente.
    1.  **Carro Azul:** Verde -> Amarelo -> Vermelho.
    2.  **Segurança:** Observe que quando o Azul fica Vermelho, o Vermelho do outro lado **NÃO** fica Verde imediatamente. Há um breve momento onde **AMBOS OS SINAIS** (Q0.0 e Q0.4) ficam vermelhos. Isso evita colisões na troca de sinal.
    3.  **Carro Vermelho:** Verde -> Amarelo -> Vermelho.
    4.  **Segurança:** Pausa com ambos vermelhos novamente.
    5.  Repete.

---

## 📂 6. Ex06_Pedestre_Botao.txt
**Objetivo:** Testar sistema reativo (Sinal inteligente).

*   **Estado Inicial:** O Verde Esq (**Q0.2**) está sempre ligado. O Carro Azul passa direto sem parar.
*   **Ação:** Dê um clique rápido no botão **I0.0** (Pedestre).
*   **Resultado (Sequência):**
    1.  O sinal Verde apaga.
    2.  Amarelo (**Q0.1**) acende por um tempo.
    3.  Vermelho (**Q0.0**) acende e o carro para.
    4.  Junto com o vermelho, a luz de pedestre (**Q0.3**) muda de estado (simulando "Pode Passar").
    5.  Após o tempo acabar, tudo reseta e o Verde (**Q0.2**) volta a ligar.

---

## 📂 7. Ex07_Teste_Sensores_Hardware.txt
**Objetivo:** Validar se a simulação dos sensores de faixa (indutivos) está funcionando.

*   **Pré-requisito:** PARK (**I0.4** e **I0.5**) LIGADOS.
*   **Estado Inicial:** Ambos os carros têm sinal Verde e estão andando.
*   **Teste Botões:**
    *   Aperte **I0.0**: Acende Amarelo Esq.
    *   Aperte **I0.1**: Acende Amarelo Dir.
*   **Teste Sensores de Faixa (O mais importante):**
    *   Olhe para a **Caixa de Pedestre Esquerda (Q0.3)**.
    *   Quando o **Carro Azul** passar sobre a faixa de pedestre (zona preta/branca), a luz da caixa deve piscar/acender. Isso confirma que o input **I0.2** está sendo ativado pela posição do carro.
    *   Olhe para a **Caixa de Pedestre Direita (Q0.7)**.
    *   Quando o **Carro Vermelho** passar sobre a faixa, a luz deve piscar/acender. Isso confirma o input **I0.3**.

---

## 📂 8. Ex08_Contadores_Duplos.txt
**Objetivo:** Testar múltiplos contadores independentes e lógica direta simultânea sem travas.

*   **Teste A (Contador 1 - Preset 3):**
    *   **Ação:** Dê toques rápidos em **I0.0**.
    *   **Resultado:** No 3º toque (ou se segurar um pouco), o Vermelho Esq (**Q0.0**) acende.
*   **Teste B (Contador 2 - Preset 5):**
    *   **Ação:** Dê toques rápidos em **I0.1**.
    *   **Resultado:** Este precisa de mais pulsos. No 5º toque, o Amarelo Esq (**Q0.1**) acende.
*   **Teste C (Lógica Direta):**
    *   **Ação:** Aperte e segure **I0.2** (Só dá pra testar se passar o carro em cima da faixa ou simular o input manualmente).
    *   **Resultado:** O Verde Esq (**Q0.2**) acende instantaneamente. Soltou, apagou.