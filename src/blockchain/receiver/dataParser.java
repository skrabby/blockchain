package blockchain.receiver;

import blockchain.core.Main;

import javax.swing.*;
import java.awt.*;

public class dataParser implements Runnable {

    public void run() {
        String data;
        while (true) {
            UIManager.put("OptionPane.minimumSize", new Dimension(250, 100));
            JTextField xField = new JTextField(5);
            JTextField yField = new JTextField(5);
            JTextField zField = new JTextField(5);
            JPanel myPanel = new JPanel(new GridLayout(0, 1, 2, 2));
            myPanel.add(new JLabel("Source:"));
            myPanel.add(xField);
            myPanel.add(new JLabel("Destination:"));
            myPanel.add(yField);
            myPanel.add(new JLabel("Amount(VC):"));
            myPanel.add(zField);

            int result = JOptionPane.showConfirmDialog(null, myPanel,
                    "Please Enter Transaction to Record", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                if (!xField.getText().equals("") && !yField.getText().equals("") && !zField.getText().equals(""))
                    Main.GeneratedData += xField.getText() + " sent " + zField.getText() + " VC to " + yField.getText() + "\n";
            } else break;
        }
        Main.EXIT_FLAG = true;
    }
}
