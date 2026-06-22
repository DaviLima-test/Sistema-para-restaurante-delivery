//import view.TelaPrincipal;
import bd.BancoDados;
import com.formdev.flatlaf.FlatLightLaf;
import model.Login;
import view.TelaLogin;
import view.TelaPrincipal;
import view.Telabase;

import java.awt.*;

public class Main {
    public static void main(String[] args) {

        EventQueue.invokeLater(() -> {
            try {
        Telabase sistema = new Telabase();
        BancoDados.inicializarBanco();
                FlatLightLaf.setup();
        // 2. Verifica o "cookie" no banco de dados SQLite


        boolean usuarioJaLogou = BancoDados.verificarSeEstaLogado();
        if (usuarioJaLogou) {

            //Login.apagarCookie();


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
