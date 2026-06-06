package view;

import javax.swing.*;
import java.awt.*;
import view.Telabase;
public class TelaLogin extends JPanel{

    public TelaLogin(){
        setLayout(new GridBagLayout());
        setOpaque(false);
        JTabbedPane menuAbas = new JTabbedPane();

        // Criando as telas de conteúdo (Abas)
        JPanel telaRestaurantes = new JPanel();
        telaRestaurantes.setBackground(Color.WHITE);
        telaRestaurantes.add(new JLabel("Gerenciamento de Restaurantes"));

        JPanel telaCardapios = new JPanel();
        telaCardapios.setBackground(Color.WHITE);
        telaCardapios.add(new JLabel("Gerenciamento de Cardápios"));

        // Adicionando as telas nas abas correspondentes
        menuAbas.addTab("Ver Restaurantes", telaRestaurantes);
        menuAbas.addTab("Ver Cardápios", telaCardapios);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0 ;
        gbc.gridy=1;
        this.add(menuAbas,gbc);


    }
}
