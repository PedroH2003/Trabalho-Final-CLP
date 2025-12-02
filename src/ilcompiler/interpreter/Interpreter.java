package ilcompiler.interpreter;

import ilcompiler.memoryvariable.MemoryVariable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import screens.HomePg;

// Classe que interpreta as intruções
public class Interpreter {

    // Cria variáveis
    static Boolean accumulator;
    static List<String> validOperators = new ArrayList<>();

    // Define operadores válidos
    public static void initializeValidOperators() {
        validOperators.add("LD");
        validOperators.add("LDN");
        validOperators.add("ST");
        validOperators.add("STN");
        validOperators.add("AND");
        validOperators.add("ANDN");
        validOperators.add("OR");
        validOperators.add("ORN");
        validOperators.add("TON");
        validOperators.add("TOFF");
        validOperators.add("CTD");
        validOperators.add("CTU");
    }

    // Recebe linhas vindas da tela e separa operador e variável
    public static Map receiveLines(List<String> lineList, Map<String, Boolean> inputs, Map<String, Boolean> outputs,
            Map<String, MemoryVariable> memoryVariables) {

        // Variáveis auxiliares
        char character = '-';
        Boolean spaceDetected = false;
        String operator = "";
        String variable = "";
        ArrayList<String> variables = new ArrayList();
        Boolean justEmptyLines = true;

        initializeValidOperators();

        // Limpa acumulador
        accumulator = null;

        // Itera lista de linhas
        for (int i = 0; i < lineList.size(); i++) {
            // Ignora linhas vazias
            if (!lineList.get(i).isBlank()) {
                justEmptyLines = false;
                // Itera caracteres da linha
                for (int j = 0; j < lineList.get(i).length(); j++) {
                    character = lineList.get(i).charAt(j);

                    if (character != ' ' && character != '\n' && character != '\t' && character != ','
                            && !spaceDetected) {
                        operator = operator + character;
                    }

                    if ((character == ' ' || character == '\t') && !operator.equals("")) {
                        spaceDetected = true;
                    }

                    if (character == ',' && !operator.equals("")) {
                        variables.add(variable);
                        variable = "";
                    }

                    if (character != ' ' && character != '\n' && character != '\t' && character != ','
                            && spaceDetected) {
                        variable = variable + character;
                    }
                }

                variables.add(variable);

                outputs = executeInstruction(operator, variables, inputs, outputs, memoryVariables);
            }

            spaceDetected = false;
            operator = "";
            variable = "";
            variables.clear();
        }

        if (justEmptyLines) {
            HomePg.showErrorMessage("Insira as intruções para o CLP!");
        }

        return outputs;
    }

    // Verifica se operador é válido
    public static boolean operatorIsValid(String operator) {
        Boolean isValid = false;
        for (int i = 0; i < validOperators.size(); i++) {
            if (!isValid && validOperators.get(i).equals(operator)) {
                isValid = true;
            }
        }
        return isValid;
    }

    public static String getMemoryType(String variable) {
        String type = "";
        String code = "";
        int cod = -1;
        
        // --- CORREÇÃO DE BUG: Leitura de dígitos 0 e 9 ---
        for (int i = 0; i < variable.length(); i++) {
            // Antes estava > '0' e < '9', ignorando o 0 e o 9.
            if (variable.charAt(i) >= '0' && variable.charAt(i) <= '9') {
                code = code + variable.charAt(i);
            } else {
                type = type + variable.charAt(i);
            }
        }

        try {
            cod = Integer.parseInt(code);
        } catch (Exception E) {
            // Falha silenciosa no parse int
        }

        if (!type.equals("M") && !type.equals("T") && !type.equals("C")) {
            // Se falhar a detecção, exibe erro.
            HomePg.showErrorMessage("Sintaxe incorreta! Espaço de memória " + variable + " não existe!");
            return "";
        } else if (cod != -1) {
            return type;
        } else {
            HomePg.showErrorMessage("Sintaxe incorreta! Espaço de memória " + variable + " não existe!");
            return "";
        }
    }

    // Verifica se entrada é válido
    public static boolean inputIsValid(ArrayList<String> variables, Map<String, Boolean> inputs) {
        Boolean isValid = true;

        if (inputs.get(variables.get(0)) == null) {
            isValid = false;
        }
        return isValid;
    }

    // Verifica se saída é válida
    public static boolean outputIsValid(ArrayList<String> variables, Map<String, Boolean> outputs) {
        Boolean isValid = true;

        if (outputs.get(variables.get(0)) == null) {
            isValid = false;
        }
        return isValid;
    }

    // Verifica se variável de memória é válida
    public static boolean memoryVariableIsValid(ArrayList<String> variables,
            Map<String, MemoryVariable> memoryVariables) {
        Boolean isValid = true;

        if (memoryVariables.get(variables.get(0)) == null) {
            isValid = false;
        }
        return isValid;
    }

    // Executa instruções
    public static Map executeInstruction(String operator, ArrayList<String> variables, Map<String, Boolean> inputs,
            Map<String, Boolean> outputs, Map<String, MemoryVariable> memoryVariables) {
        
        // Caso operador seja válido e tenhamos como variável uma entrada ou uma saida
        if (operatorIsValid(operator) && (inputIsValid(variables, inputs) || outputIsValid(variables, outputs))) {

            // Carrega entrada ou saida para o acumulador
            if (operator.equals("LD")) {
                if (variables.get(0).charAt(0) == 'I') {
                    accumulator = inputs.get(variables.get(0));
                }

                if (variables.get(0).charAt(0) == 'Q') {
                    accumulator = outputs.get(variables.get(0));
                }
            }

            // Carrega entrada ou saida negada para o acumulador
            if (operator.equals("LDN")) {
                if (variables.get(0).charAt(0) == 'I') {
                    accumulator = !(inputs.get(variables.get(0)));
                }

                if (variables.get(0).charAt(0) == 'Q') {
                    accumulator = !(outputs.get(variables.get(0)));
                }
            }

            // Verifica se o valor do acumulador não é nulo
            if (accumulator != null) {
                if (operator.equals("ST") || operator.equals("STN")) {
                    if (outputIsValid(variables, outputs)) {
                        // Carrega o valor do acumulador para a variável (saida)
                        if (operator.equals("ST")) {
                            if (variables.get(0).charAt(0) == 'Q') {
                                outputs.put(variables.get(0), accumulator);
                            }
                        }

                        // Carrega o valor do acumulador negado para a variável (saida)
                        if (operator.equals("STN")) {
                            if (variables.get(0).charAt(0) == 'Q') {
                                outputs.put(variables.get(0), !accumulator);
                            }
                        }
                    } else {
                        HomePg.showErrorMessage(
                                "Entradas não podem ser modificadas, portanto, operadores ST e STN não são válidos para entradas!");
                    }
                }

                // Operações lógicas
                if (operator.equals("AND")) {
                    if (variables.get(0).charAt(0) == 'I') {
                        accumulator = (accumulator && inputs.get(variables.get(0)));
                    }

                    if (variables.get(0).charAt(0) == 'Q') {
                        accumulator = (accumulator && outputs.get(variables.get(0)));
                    }
                }

                if (operator.equals("ANDN")) {
                    if (variables.get(0).charAt(0) == 'I') {
                        accumulator = (accumulator && !(inputs.get(variables.get(0))));
                    }

                    if (variables.get(0).charAt(0) == 'Q') {
                        accumulator = (accumulator && !(outputs.get(variables.get(0))));
                    }
                }

                if (operator.equals("OR")) {
                    if (variables.get(0).charAt(0) == 'I') {
                        accumulator = (accumulator || inputs.get(variables.get(0)));
                    }

                    if (variables.get(0).charAt(0) == 'Q') {
                        accumulator = (accumulator || outputs.get(variables.get(0)));
                    }
                }

                if (operator.equals("ORN")) {
                    if (variables.get(0).charAt(0) == 'I') {
                        accumulator = (accumulator || !(inputs.get(variables.get(0))));
                    }

                    if (variables.get(0).charAt(0) == 'Q') {
                        accumulator = (accumulator || !(outputs.get(variables.get(0))));
                    }
                }
            } else {
                HomePg.showErrorMessage(
                        "Acumulador vazio! Carregue inicialmente a variável desejada para o acumulador com as funções LD ou LDN!");
            }

            // Caso operador seja válido e tenhamos como variável uma memória
        } else if (operatorIsValid(operator) && !inputIsValid(variables, inputs)
                && !outputIsValid(variables, outputs)) {
            // Para operações de carregamento (onde variável de memória são criadas ou escritas)
            if (operator.equals("ST") || operator.equals("STN") || operator.equals("TON") || operator.equals("TOFF")
                    || operator.equals("CTD") || operator.equals("CTU")) {
                // Se memória já existe, só atualiza no hash
                String type = getMemoryType(variables.get(0));
                if (!type.equals("")) {
                    if (memoryVariableIsValid(variables, memoryVariables)) {
                        MemoryVariable memVar = memoryVariables.get(variables.get(0));
                        
                        if (operator.equals("ST")) {
                            if (type.equals("C")) {
                                memVar.testEndTimer();
                                // Borda de subida no acumulador
                                if (!memVar.currentValue && accumulator) {
                                    if (memVar.counterType.equals("UP")) memVar.incrementCounter();
                                    else if (memVar.counterType.equals("DOWN")) memVar.decrementCounter();
                                }
                            }
                            memVar.currentValue = accumulator;
                        }

                        if (operator.equals("STN")) {
                            if (type.equals("C")) {
                                memVar.testEndTimer();
                                if (memVar.currentValue && !accumulator) {
                                    if (memVar.counterType.equals("UP")) memVar.incrementCounter();
                                    else if (memVar.counterType.equals("DOWN")) memVar.decrementCounter();
                                }
                            }
                            memVar.currentValue = !accumulator;
                        }

                        if (operator.equals("TON") && type.equals("T")) {
                            memVar.maxTimer = Integer.parseInt(variables.get(1));
                            memVar.timerType = "ON";
                        } else if (operator.equals("TON")) {
                             HomePg.showErrorMessage("Sintaxe incorreta! Espaço de memória " + variables.get(0) + " invalido!");
                        }

                        if (operator.equals("TOFF") && type.equals("T")) {
                            memVar.maxTimer = Integer.parseInt(variables.get(1));
                            memVar.timerType = "OFF";
                        } else if (operator.equals("TOFF")) {
                             HomePg.showErrorMessage("Sintaxe incorreta! Espaço de memória " + variables.get(0) + " invalido!");
                        }

                        if (operator.equals("CTD") && type.equals("C")) {
                            memVar.maxTimer = Integer.parseInt(variables.get(1));
                            memVar.counterType = "DOWN";
                        } else if (operator.equals("CTD")) {
                             HomePg.showErrorMessage("Sintaxe incorreta! Espaço de memória " + variables.get(0) + " invalido!");
                        }

                        if (operator.equals("CTU") && type.equals("C")) {
                            memVar.maxTimer = Integer.parseInt(variables.get(1));
                            memVar.counterType = "UP";
                        } else if (operator.equals("CTU")) {
                             HomePg.showErrorMessage("Sintaxe incorreta! Espaço de memória " + variables.get(0) + " invalido!");
                        }
                    // Se memória não existe, ela é criada
                    } else {
                        if (operator.equals("ST") || operator.equals("STN")) {
                            MemoryVariable newMem = new MemoryVariable(variables.get(0));
                            if (operator.equals("ST")) newMem.currentValue = accumulator;
                            else newMem.currentValue = !accumulator;
                            memoryVariables.put(variables.get(0), newMem);
                        }

                        if (operator.equals("TON") && type.equals("T")) {
                            MemoryVariable newMem = new MemoryVariable(variables.get(0));
                            newMem.maxTimer = Integer.parseInt(variables.get(1));
                            newMem.timerType = "ON";
                            memoryVariables.put(variables.get(0), newMem);
                        } 

                        if (operator.equals("TOFF") && type.equals("T")) {
                            MemoryVariable newMem = new MemoryVariable(variables.get(0));
                            newMem.maxTimer = Integer.parseInt(variables.get(1));
                            newMem.timerType = "OFF";
                            memoryVariables.put(variables.get(0), newMem);
                        }

                        if (operator.equals("CTD") && type.equals("C")) {
                            MemoryVariable newMem = new MemoryVariable(variables.get(0));
                            newMem.maxTimer = Integer.parseInt(variables.get(1));
                            newMem.counterType = "DOWN";
                            memoryVariables.put(variables.get(0), newMem);
                        }

                        if (operator.equals("CTU") && type.equals("C")) {
                            MemoryVariable newMem = new MemoryVariable(variables.get(0));
                            newMem.maxTimer = Integer.parseInt(variables.get(1));
                            newMem.counterType = "UP";
                            memoryVariables.put(variables.get(0), newMem);
                        }
                    }
                }
            } else {
                // --- CORREÇÃO INÍCIO: Auto-inicialização de memória na leitura ---
                // Se a memória for lida antes de ser escrita, cria-a com valor padrão (false/0).
                // Isso impede o erro ao usar LD M0, OR M0, ANDN T1, etc.
                if (memoryVariables.get(variables.get(0)) == null) {
                    String typeToCheck = getMemoryType(variables.get(0));
                    // Se typeToCheck vier vazio, é porque o getMemoryType já deu erro de sintaxe.
                    if (!typeToCheck.equals("")) {
                        memoryVariables.put(variables.get(0), new MemoryVariable(variables.get(0)));
                    }
                }
                // --- CORREÇÃO FIM ---

                // Memória precisa existir (agora vai existir se o nome for válido)
                if (memoryVariableIsValid(variables, memoryVariables)) {
                    MemoryVariable memVar = memoryVariables.get(variables.get(0));
                    boolean isTimerOrCounter = variables.get(0).charAt(0) == 'T' || variables.get(0).charAt(0) == 'C';
                    boolean val = isTimerOrCounter ? memVar.endTimer : memVar.currentValue;

                    if (operator.equals("LD"))   accumulator = val;
                    if (operator.equals("LDN"))  accumulator = !val;
                    if (operator.equals("AND"))  accumulator = accumulator && val;
                    if (operator.equals("ANDN")) accumulator = accumulator && !val;
                    if (operator.equals("OR"))   accumulator = accumulator || val;
                    if (operator.equals("ORN"))  accumulator = accumulator || !val;
                } else {
                    HomePg.showErrorMessage("Sintaxe incorreta! Variável " + variables.get(0) + " não existe!");
                }
            }
        } else {
            HomePg.showErrorMessage("Sintaxe incorreta! Operador " + operator + " não existe!");
        }

        return outputs;
    }
}