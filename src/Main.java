//import view.TelaPrincipal;
import com.formdev.flatlaf.FlatLightLaf;
import view.TelaInicial;
import model.Login;
import view.TelaLogin;
import view.TelaPrincipal;
import view.Telabase;

import javax.swing.*;
import java.awt.*;
import com.formdev.flatlaf.FlatDarkLaf;
public class Main {
    public static void main(String[] args) {

        EventQueue.invokeLater(() -> {
            try {
        Telabase sistema = new Telabase();
        Login.inicializarBanco();
                FlatLightLaf.setup();
        // 2. Verifica o "cookie" no banco de dados SQLite


        boolean usuarioJaLogou = Login.verificarSeEstaLogado();
        if (usuarioJaLogou) {

            //Login.apagarCookie();
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
