package com.example.diego_000.chatsocketservidor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServidorReceiver extends BroadcastReceiver {

    public static List<Socket> listaClientes;
    @Override
    public void onReceive(Context context, Intent intent)
    {
        HiloServidor h = new HiloServidor();
        h.execute();
    }

    class HiloServidor extends AsyncTask<Void, Void,Void>
    {
        @Override
        protected Void doInBackground(Void... voids) {
            ServerSocket servidor = null;
            listaClientes = new ArrayList<Socket>();

            try{
                servidor = new ServerSocket(1234);
                while(true){
                    Socket cliente = servidor.accept();
                    listaClientes.add(cliente);
                    BufferedReader in = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
                    String nickname = in.readLine();
                    System.out.println(nickname+" entró a la sala de Chat");
                    HiloXCliente hiloCliente = new HiloXCliente(cliente, nickname);
                    hiloCliente.start();
                }
            }
            catch(Exception e){
                System.out.println("Error: "+e.getMessage());
            }
            finally{
                try{ servidor.close(); } catch(Exception e){ }
            }

            return null;
        }
    }

    class HiloXCliente extends Thread{
        Socket cliente;
        BufferedReader in;
        PrintWriter out;
        String nickname;

        public HiloXCliente(Socket cliente, String nickname){
            this.cliente = cliente;
            this.nickname = nickname;
        }

        public void run(){
            try{
                in = new BufferedReader(new InputStreamReader(this.cliente.getInputStream()));
                out = new PrintWriter(this.cliente.getOutputStream(), true);
                out.println("¡Bienvenido a la Sala de Chat!");
                String eco = "";
                while(!eco.equals("bye")){
                    eco = in.readLine();
                    System.out.println("["+nickname+"]: "+eco);

                    for(int i=0;i<listaClientes.size();i++){
                        out = new PrintWriter((listaClientes.get(i)).getOutputStream(), true);
                        out.println("["+nickname+"]: "+eco);
                    }

                }
            }
            catch(Exception e){
                System.out.println("Error: "+e.getMessage());
            }
            finally{
                try{ this.cliente.close(); } catch(Exception e){ }
            }

        }

    }


}
