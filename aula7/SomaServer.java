import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

public class SomaServer {

    private static class estadoPartilhado {
        private int sumAll;
        private int contAll;
        private ReentrantLock l = new ReentrantLock();

        public estadoPartilhado ()
        {
            sumAll = 0;
            contAll = 0;
        }

        public void addSumAll (int valueToAdd) {
            l.lock();
            try {
                sumAll += valueToAdd;
                contAll++;
            } finally {
                l.unlock();
            }
        }

        public int getAverage () {
            l.lock();
            try {
                return sumAll / contAll;
            } finally {
                l.unlock();
            }
        }

    }

    private static class ClientHandler implements Runnable {

        Socket cliente;
        estadoPartilhado estado;

        public ClientHandler (Socket cliente, estadoPartilhado estado) { this.cliente = cliente; this.estado = estado;}

        public void run ()
        {
            try {
                int sum = 0;
                int cont = 0;
                BufferedReader in = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
                PrintWriter out = new PrintWriter(cliente.getOutputStream());
    
                String line;
                while ((line = in.readLine()) != null) {
                    int number = Integer.parseInt(line);
                    sum += number;
                    cont++;
                    estado.addSumAll(number);
                    out.println(sum);
                    out.flush();
                }
                int media = estado.getAverage();
                out.println(media);
                out.flush();
                cliente.shutdownOutput();
                cliente.shutdownInput();
                cliente.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
    private static ServerSocket ss;

    public static void main(String[] args) {
        try {
            ss = new ServerSocket(12345);
            estadoPartilhado estado = new estadoPartilhado();
            while (true) {
                Socket socket = ss.accept();
                new Thread(new ClientHandler(socket, estado)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try{
                ss.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
