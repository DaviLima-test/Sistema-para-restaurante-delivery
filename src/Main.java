
import bd.BancoDados;
import com.formdev.flatlaf.FlatLightLaf;
import view.TelaLogin;
import view.TelaPrincipal;
import view.Telabase;
import java.awt.*;

/**
 * Classe principal responsável pela inicialização do sistema de delivery,
 * configuração do tema visual e gerenciamento do fluxo de login por sessão.
 * * @author Seu Nome
 * @version 1.0
 */
public class Main {

    /**
     * Método de entrada do aplicativo. Inicializa o banco de dados,
     * aplica o Look and Feel e define qual tela inicial deve ser exibida.
     * * @param args Argumentos de linha de comando (não utilizados).
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                FlatLightLaf.setup();

                Telabase sistema = new Telabase();
                BancoDados.inicializarBanco();

                boolean usuarioJaLogou = BancoDados.verificarSeEstaLogado();

                if (usuarioJaLogou) {
                    System.out.println("Cookie ativo! Abrindo o feed principal...");
                    TelaPrincipal principal = new TelaPrincipal(sistema);
                    sistema.configuraTela(principal);
                    sistema.setVisible(true);
                } else {
                    System.out.println("Sem cookie. Abrindo tela inicial...");
                    TelaLogin inicio = new TelaLogin();
                    sistema.configuraTela(inicio);
                    sistema.setVisible(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}