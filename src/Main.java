//import view.TelaPrincipal;
import view.TelaInicial;
import model.Login;
import view.TelaLogin;
import view.TelaPrincipal;
import view.Telabase;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        try {
            // Ativa o Look and Feel Nimbus, que aceita customizações modernas de botões e scrolls
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Não foi possível carregar o tema moderno.");
        }
        EventQueue.invokeLater(() -> {
            try {
        Telabase sistema = new Telabase();
        Login.inicializarBanco();

        // 2. Verifica o "cookie" no banco de dados SQLite


        boolean usuarioJaLogou = Login.verificarSeEstaLogado();
        if (usuarioJaLogou) {
            Login.apagarCookie();
            // Se o banco disser que está logado, pula a TelaInicio e vai direto pro App
            System.out.println("Cookie ativo! Abrindo o feed principal...");
            TelaPrincipal principal = new TelaPrincipal();
            sistema.configuraTela(principal);
            sistema.setVisible(true);
        } else {
            // Se não tiver cookie, abre a tela com os botões de Login e Cadastro
            System.out.println("Sem cookie. Abrindo tela inicial...");
            TelaInicial inicio = new TelaInicial();
            sistema.configuraTela(inicio);
            sistema.setVisible(true);
        }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
