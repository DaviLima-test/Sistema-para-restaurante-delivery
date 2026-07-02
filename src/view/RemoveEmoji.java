package util;

import javax.swing.AbstractButton;
import javax.swing.JLabel;
import java.awt.Component;
import java.awt.Container;
import java.util.regex.Pattern;

/**
 * Utilitario responsavel por detectar se o sistema operacional e o Windows
 * e, caso seja, remover qualquer representacao de emoji presente na
 * interface: texto de botoes (JButton, BotaoArredondado...) e de labels
 * (JLabel, Texto...), inclusive quando o texto esta em HTML
 * (ex: "<html>👆<br>Selecione um item</html>").
 *
 * Como a remocao trabalha em cima do texto puro via regex, tags HTML nao
 * atrapalham: elas simplesmente sao ignoradas e o emoji e removido de
 * dentro delas normalmente.
 *
 * Isso e util porque, dependendo da fonte padrao instalada, o Swing no
 * Windows costuma renderizar emojis como retangulos vazios ("tofu boxes")
 * em vez do glifo colorido, o que prejudica a experiencia visual.
 *
 * Duas formas de uso:
 *
 * 1) Telas inteiras / paineis montados de uma vez (cobre botoes e labels
 *    recursivamente, incluindo os que estao dentro de sub-paineis):
 *
 *      RemoveEmoji.aplicar(painel);
 *
 * 2) Strings avulsas usadas fora de um Container ja montado, como
 *    mensagens de JOptionPane ou texto setado dinamicamente em um
 *    listener (ex: labels de feedback/erro):
 *
 *      JOptionPane.showMessageDialog(this, RemoveEmoji.texto("🎉 Sucesso!"));
 *      lblFeedback.setText(RemoveEmoji.texto("✅ Produto atualizado!"));
 */
public final class RemoveEmoji {

    // Intervalos fora do BMP (planos suplementares, onde vive a maioria
    // dos emojis) precisam usar a sintaxe \x{...} baseada em code point.
    // Escrever pares de surrogates soltos dentro de uma classe de
    // caracteres "[...]" NAO funciona: o motor de regex do Java nao os
    // trata como um unico code point suplementar e a remocao falha
    // silenciosamente.
    private static final Pattern EMOJI_PATTERN = Pattern.compile(
            "[" +
            "\u203C\u2049" +                  // dois/tres pontos de exclamacao/interrogacao
            "\u2122\u2139" +                  // (TM) e simbolo de informacao
            "\u2194-\u21AA" +                 // setas usadas como emoji
            "\u231A-\u231B" +                 // relogios
            "\u2328" +                        // teclado
            "\u23CF" +                        // ejetar
            "\u23E9-\u23F3" +                 // avancar/voltar/pausa/relogio de areia
            "\u23F8-\u23FA" +                 // pausa/parar/gravar
            "\u24C2" +                        // letra M em circulo
            "\u25AA-\u25FE" +                 // quadrados e triangulos pequenos
            "\u2600-\u27BF" +                 // clima, cartas, simbolos diversos, dingbats
            "\u2934-\u2935" +                 // setas curvas
            "\u2B00-\u2BFF" +                 // estrelas, setas, formas
            "\u3030\u303D" +                  // ondulada / parte alternativa
            "\u3297\u3299" +                  // ideogramas circulados (parabens/secreto)
            "\\x{1F000}-\\x{1F0FF}" +         // mahjong / cartas de baralho
            "\\x{1F100}-\\x{1F1FF}" +         // letras/numeros em circulo, bandeiras regionais
            "\\x{1F200}-\\x{1F2FF}" +         // simbolos com quadrado (japones)
            "\\x{1F300}-\\x{1F5FF}" +         // simbolos e pictogramas diversos
            "\\x{1F600}-\\x{1F64F}" +         // emoticons (rostos)
            "\\x{1F680}-\\x{1F6FF}" +         // transporte e mapas
            "\\x{1F700}-\\x{1F77F}" +         // simbolos alquimicos
            "\\x{1F780}-\\x{1F7FF}" +         // formas geometricas estendidas
            "\\x{1F800}-\\x{1F8FF}" +         // setas suplementares
            "\\x{1F900}-\\x{1F9FF}" +         // emojis suplementares (pessoas, comidas, etc.)
            "\\x{1FA00}-\\x{1FA6F}" +         // simbolos de xadrez estendidos
            "\\x{1FA70}-\\x{1FAFF}" +         // emojis mais recentes (objetos, animais, etc.)
            "\uFE0F" +                        // Variation Selector-16 (forca exibicao como emoji)
            "\u200D" +                        // Zero Width Joiner
            "]"
    );

    private RemoveEmoji() {
        // Classe utilitaria: nao deve ser instanciada.
    }

    /**
     * Verifica se o sistema operacional em execucao e o Windows.
     */
    public static boolean isWindows() {
        String os = System.getProperty("os.name");
        return os != null && os.toLowerCase().contains("win");
    }

    /**
     * Remove qualquer emoji de um texto (funciona tambem com texto HTML,
     * pois so mexe nos caracteres de emoji e ignora o resto), alem de
     * espacos duplicados que sobrarem no lugar do emoji removido.
     * Sempre remove, independente do SO — use {@link #texto(String)} se
     * quiser a remocao condicionada ao Windows.
     */
    public static String remover(String texto) {
        if (texto == null || texto.isEmpty()) {
            return texto;
        }
        String semEmoji = EMOJI_PATTERN.matcher(texto).replaceAll("");
        // Colapsa espacos duplos que sobram apos a remocao. Evita colapsar
        // tags HTML (que podem ter espacos legitimos) fazendo o replace
        // apenas em sequencias de espaco "puro".
        return semEmoji.replaceAll("[ \\t]{2,}", " ").trim();
    }

    /**
     * Helper para strings avulsas (mensagens de JOptionPane, labels de
     * feedback setados dinamicamente em listeners, etc.) que nao fazem
     * parte de um Container limpo via {@link #aplicar(Container)}.
     * So remove o emoji se o SO for Windows; nos demais SOs devolve o
     * texto original sem alteracao.
     */
    public static String texto(String textoOriginal) {
        return isWindows() ? remover(textoOriginal) : textoOriginal;
    }

    /**
     * Percorre recursivamente todos os componentes de um Container e
     * remove qualquer emoji do texto de botoes (AbstractButton) e de
     * labels (JLabel, incluindo os que usam HTML como a classe Texto).
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
     * @deprecated use {@link #limparComponentes(Container)}, que tambem
     * cobre labels/HTML. Mantido para compatibilidade.
     */
    @Deprecated
    public static void limparBotoes(Container container) {
        limparComponentes(container);
    }

    /**
     * Ponto de entrada principal: so remove os emojis dos botoes e labels
     * se o sistema operacional detectado for o Windows.
     */
    public static void aplicar(Container container) {
        if (isWindows()) {
            limparComponentes(container);
        }
    }
}
