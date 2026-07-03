package util;

import javax.swing.AbstractButton;
import javax.swing.JLabel;
import java.awt.Component;
import java.awt.Container;
import java.util.regex.Pattern;

/**
 * Utilitário de infraestrutura para tratamento de compatibilidade de fontes visuais na interface gráfica.
 * <p>
 * Fornece mecanismos automatizados baseados em expressões regulares (RegEx) para detecção do
 * sistema operacional Windows e remoção em cascata de caracteres Unicode pertencentes a blocos de emojis.
 * O propósito principal desta classe é prevenir falhas de renderização (como glifos quadrados em branco ou
 * corrompidos) em plataformas que não possuem suporte nativo de fallback para emojis no Java Swing.
 * </p>
 *
 * @author Arthur, Felipe, Davi
 * @version 1.2
 */
public class RemoveEmoji {

    /**
     * Expressão regular contendo os intervalos de blocos de códigos Unicode correspondentes a emojis
     * comuns, símbolos miscelâneos, transporte, mapas, pictogramas e dingbats.
     */
    private static final Pattern EMOJI_PATTERN = Pattern.compile(
            "[\\x{1F300}-\\x{1F6FF}\\x{1F900}-\\x{1F9FF}\\x{2600}-\\x{26FF}\\x{2700}-\\x{27BF}]"
    );

    /**
     * Verifica se o sistema operacional hospedeiro da aplicação pertence à família Microsoft Windows.
     *
     * @return {@code true} se o nome do sistema operacional contiver o prefixo/subcadeia "win";
     * {@code false} caso contrário (sistemas baseados em Unix, Linux ou macOS).
     */
    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    /**
     * Analisa uma cadeia de caracteres alfanumérica e substitui todas as ocorrências de símbolos
     * e emojis mapeados por uma sequência vazia.
     *
     * @param textoOriginal A sequência de texto contendo possíveis caracteres ou blocos de emojis.
     * @return Uma nova {@link String} limpa de qualquer emoji, ou {@code null} se a cadeia de entrada for nula.
     */
    public static String remover(String textoOriginal) {
        if (textoOriginal == null) {
            return null;
        }
        return EMOJI_PATTERN.matcher(textoOriginal).find() ?
                EMOJI_PATTERN.matcher(textoOriginal).replaceAll("") : textoOriginal;
    }

    /**
     * Fornece um apelido simplificado (atalho estático) para invocação direta da rotina de limpeza de texto.
     *
     * @param texto A sequência de texto a ser sanitizada.
     * @return A sequência textual filtrada sem a presença de emojis.
     */
    public static String texto(String texto) {
        return remover(texto);
    }

    /**
     * Varre recursivamente toda a árvore topológica de componentes visuais aninhados em um contêiner
     * e aplica a filtragem em rótulos de botões e caixas de texto descritivo.
     *
     * @param container O contêiner de interface gráfica base (como {@code JPanel} ou {@code JFrame})
     * que passará pela varredura recursiva de limpeza.
     */
    public static void limparComponentes(Container container) {
        if (container == null) {
            return;
        }
        for (Component componente : container.getComponents()) {
            if (componente instanceof AbstractButton) {
                AbstractButton botao = (AbstractButton) componente;
                botao.setText(remover(botao.getText()));
            } else if (componente instanceof JLabel) {
                JLabel label = (JLabel) componente;
                label.setText(remover(label.getText()));
            }
            if (componente instanceof Container) {
                limparComponentes((Container) componente);
            }
        }
    }

    /**
     * Aciona o processo de higienização do contêiner de componentes visuais condicionado estritamente
     * ao ambiente operacional detectado. A limpeza ocorre apenas se o sistema atual for Windows.
     *
     * @param container O contêiner de componentes gráficos ativo na tela que receberá o tratamento.
     */
    public static void aplicar(Container container) {
        if (isWindows()) {
            limparComponentes(container);
        }
    }
}