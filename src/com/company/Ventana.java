package com.company;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Created by igarcia on 18/05/2017.
 */
public class Ventana {
    private JTextField servidor;
    private JTextField login;
    public JPanel view;
    private JTextField destino;
    private JButton buscarButton;
    private JPasswordField password;
    private JTextField origen;

    public Ventana() {
        buscarButton.addActionListener(new Buscar());
    }



    private class Buscar implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (servidor.getText().isEmpty() || login.getText().isEmpty() || destino.getText().isEmpty() || password.getPassword().length < 1) {
                JOptionPane.showMessageDialog(null, "Todos los campos son requeridos");
            } else {
                try {
                    buscarButton.setEnabled(false);
                    String pass = new String(password.getPassword());
                    if (Main.conexionSFTP(login.getText(), servidor.getText(), pass, origen.getText(), destino.getText())) {
                        Main.analizarLog(destino.getText());
                    }

                } catch (IOException e1) {
                    e1.printStackTrace();buscarButton.setEnabled(true);
                }
                buscarButton.setEnabled(true);

            }
        }
    }
}

