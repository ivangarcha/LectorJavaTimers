package com.company;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

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
            if (servidor.getText().isEmpty() || login.getText().isEmpty() || destino.getText().isEmpty() || password.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Todos los campos son requeridos");
            } else {
                try {
                    if (Main.conexionSFTP(login.getText(), servidor.getText(), password.getText(), origen.getText(), destino.getText())) {
                        Main.analizarLog(destino.getText());
                    }
                    ;
                } catch (JSchException e1) {
                    e1.printStackTrace();
                } catch (SftpException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }
        }
    }
}

