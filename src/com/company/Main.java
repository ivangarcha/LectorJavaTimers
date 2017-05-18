package com.company;

import com.jcraft.jsch.*;

import java.io.*;
import java.util.*;


public class Main {

    public static void main(String[] args) throws IOException, SftpException, JSchException {
        JSch jsch = new JSch();
        Session session = null;

        try {
            session = jsch.getSession("root", "192.168.242.153", 22);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword("ctphOnE2015");
            session.connect();
        } catch (Exception e) {

        }
        Channel channel = session.openChannel("sftp");
        channel.connect();
        ChannelSftp sftpChannel = (ChannelSftp) channel;
        sftpChannel.get("/root/logs/backend.log.18_05_2017", "backend.log.153");
        sftpChannel.exit();
        session.disconnect();

        analizarLog("backend.log.153");

    }

    private static void analizarLog(String nombreFichero) throws IOException {
        String linea;
        String archivo = nombreFichero;
        String archivo_destino = "tiempos " + archivo;
        File fichero = new File(archivo_destino);
        BufferedWriter bw;
        bw = new BufferedWriter(new FileWriter(fichero));
        int contador = 1;
        ArrayList<Tiempo> lineas = new ArrayList<>();
        try {
            FileReader fileReader = new FileReader(archivo);
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
                    bw.write(itrArrayList.next().getPeticion().toString() + " Duracion: " + String.valueOf(itrArrayList.next().getDuracion()) + " ms\n");
                    posicion++;
                }
                bw.close();
                b.close();
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

        public Tiempo(){

        }
        public Tiempo(String peticion, double duracion){
            this.peticion=peticion;
            this.duracion=duracion;
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
        public String toString(){
            return this.getPeticion()+"-"+this.getDuracion();
        }

    }
}
