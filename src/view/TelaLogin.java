package view;

import javax.swing.*;
import java.awt.*;

public class TelaLogin extends Telabase{
    JPanel panel1;
    public TelaLogin(){
        super();
        panel1 = new JPanel();
        Texto txt = new Texto("AQUI VAI SER PRINCIPAL");
        txt.setFont(new Font("ARIAL",Font.BOLD,30));
        panel1.add(txt);
        configuraTela(panel1);
    }
}
