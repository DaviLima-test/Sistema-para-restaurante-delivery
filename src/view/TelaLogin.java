package view;

import javax.swing.*;
import java.awt.*;
import view.Telabase;
public class TelaLogin extends JPanel{

    public TelaLogin(){
        setLayout(new GridBagLayout());
        setOpaque(false);
        Texto txt = new Texto("AQUI VAI SER PRINCIPAL");
        txt.setFont(new Font("ARIAL",Font.BOLD,30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0 ;
        gbc.gridy=1;
        this.add(txt,gbc);


    }
}
