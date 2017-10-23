package com.company;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import javax.swing.*;
import java.io.*;
import java.util.*;


public class Main {

    public static void main(String[] args) throws IOException {
        JFrame ventana = new JFrame("Analisis de Logs");
        ventana.setContentPane(new Ventana().view);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setResizable(false);
        ventana.pack();
        ventana.setVisible(true);

    }

    public static boolean conexionSFTP(String login, String maquina, String password, String ficheroOrigen, String directorioDestino) {
        try {
            JSch jsch = new JSch();
            Session session = null;

            session = jsch.getSession(login, maquina, 22);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(password);
            session.connect();
            Channel channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp sftpChannel = (ChannelSftp) channel;
            File directorio = new File(directorioDestino);
            directorio.mkdir();
            sftpChannel.get(ficheroOrigen, directorio + "\\backend.log." + maquina.substring(8));
            sftpChannel.exit();
            session.disconnect();
            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "No se puede conectar");
            return false;
        }
    }

    public static void analizarLog(String nombreDirectorio, String maquina) throws IOException {
        System.out.println("Iniciando analisis del fichero .......");
        String linea;
        String directorio = nombreDirectorio;
        String archivo_destino = "tiempos " + "backend.log." + maquina.substring(8);
        File fichero = new File(directorio + "\\" + archivo_destino);
        BufferedWriter bw;
        bw = new BufferedWriter(new FileWriter(fichero));
        int contador = 1;
        ArrayList<Tiempo> lineas = new ArrayList<>();
        try {
            FileReader fileReader = new FileReader(directorio + "\\" + "backend.log." + maquina.substring(8));
            BufferedReader b = new BufferedReader(fileReader);
            while ((linea = b.readLine()) != null) {
                int inicio = 0;
                if (linea.contains("GET ")) {
                    try {
                        inicio = linea.indexOf("GET");
                        String cadenaPeticion = setCadenaPeticion(contador, linea, inicio);
                        String cadenaDuracion = setCadenaDuracion(linea, cadenaPeticion);
                        double duracion = Double.parseDouble(cadenaDuracion.substring(0, cadenaDuracion.indexOf(" ")));
                        lineas.add(new Tiempo(cadenaPeticion, duracion));
                    } catch (Exception e) {
                        System.out.print(linea + "\n");
                    }
                }
                if (linea.contains("POST ")) {
                    try {
                        inicio = linea.indexOf("POST");
                        String cadenaPeticion = setCadenaPeticion(contador, linea, inicio);
                        String cadenaDuracion = setCadenaDuracion(linea, cadenaPeticion);
                        double duracion = Double.parseDouble(cadenaDuracion.substring(0, cadenaDuracion.indexOf(" ")));
                        lineas.add(new Tiempo(cadenaPeticion, duracion));
                    } catch (Exception e) {
                        System.out.print(linea + "\n");
                    }
                }
                if (linea.contains("PUT ")) {
                    try {
                        inicio = linea.indexOf("PUT");
                        String cadenaPeticion = setCadenaPeticion(contador, linea, inicio);
                        String cadenaDuracion = setCadenaDuracion(linea, cadenaPeticion);
                        double duracion = Double.parseDouble(cadenaDuracion.substring(0, cadenaDuracion.indexOf(" ")));
                        lineas.add(new Tiempo(cadenaPeticion, duracion));

                    } catch (Exception e) {
                        System.out.print(linea + "\n");
                    }
                }
                contador++;
            }
            Collections.sort(lineas, new Comparator<Tiempo>() {
                @Override
                public int compare(Tiempo o1, Tiempo o2) {
                    return new Double(o2.getDuracion()).compareTo((Double) o1.getDuracion());
                }
            });

            Iterator<Tiempo> itrArrayList = lineas.iterator();
            int posicion = 1;
            try {
                while (itrArrayList.hasNext()) {
                    Tiempo siguiente = itrArrayList.next();
                    bw.write(siguiente.getPeticion().toString() + " Duracion: " + String.valueOf(siguiente.getDuracion()) + " ms\n");
                    posicion++;
                }
                bw.close();
                b.close();
                System.out.println("Finalizado analisis del fichero.");

            } catch (NoSuchElementException e) {

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    private static String setCadenaDuracion(String linea, String cadenaPeticion) {
        return linea.substring(linea.indexOf("m", linea.indexOf("m", cadenaPeticion.length())) + 1);
    }

    private static String setCadenaPeticion(int contador, String linea, int inicio) {
        return "Linea :" + contador + " " + linea.substring(inicio, linea.indexOf(" ", inicio + 5)) + " ";
    }

    private static class Tiempo {
        private String peticion;
        private double duracion;

        public Tiempo() {

        }

        public Tiempo(String peticion, double duracion) {
            this.peticion = peticion;
            this.duracion = duracion;
        }

        public String getPeticion() {
            return peticion;
        }

        public void setPeticion(String peticion) {
            this.peticion = peticion;
        }

        public double getDuracion() {
            return duracion;
        }

        public void setDuracion(double duracion) {
            this.duracion = duracion;
        }

        @Override
        public String toString() {
            return this.getPeticion() + "-" + this.getDuracion();
        }

    }
}

