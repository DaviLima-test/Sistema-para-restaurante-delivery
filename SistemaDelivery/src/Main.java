import repositorio.SessaoBanco;
//import view.TelaPrincipal;
import view.TelaInicial;

import java.awt.*;

public class Main {
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
        SessaoBanco.inicializarBanco();

        // 2. Verifica o "cookie" no banco de dados SQLite
        boolean usuarioJaLogou = SessaoBanco.verificarSeEstaLogado();

        if (usuarioJaLogou) {
            // Se o banco disser que está logado, pula a TelaInicio e vai direto pro App
            System.out.println("Cookie ativo! Abrindo o feed principal...");
            //TelaPrincipal principal = new TelaPrincipal();
            //principal.setVisible(true);
        } else {
            // Se não tiver cookie, abre a tela com os botões de Login e Cadastro
            System.out.println("Sem cookie. Abrindo tela inicial...");
            TelaInicial inicio = new TelaInicial();
            inicio.setVisible(true);
        }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
