//import view.TelaPrincipal;
import com.formdev.flatlaf.FlatLightLaf;
import view.TelaCadastro;
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
        Login.inicializarBanco();
                FlatLightLaf.setup();
        // 2. Verifica o "cookie" no banco de dados SQLite


        boolean usuarioJaLogou = Login.verificarSeEstaLogado();
        if (usuarioJaLogou) {

            //Login.apagarCookie();


            System.out.println("Cookie ativo! Abrindo o feed principal...");
            TelaPrincipal principal = new TelaPrincipal();
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
