package brutes.server;

import brutes.server.db.DatasManager;
import brutes.server.net.NetworkServer;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Karl
 */
public class ServerThread extends Thread {

    public static final int TIMEOUT_ACCEPT = 10000;
    public static final int TIMEOUT_CLIENT = 1000;

    @Override
    public void run() {
        try (ServerSocket sockserv = new ServerSocket(42666)) {
            sockserv.setSoTimeout(ServerThread.TIMEOUT_ACCEPT);

            File dbFile = new File("~$bdd.db");
            boolean needPopulate = !dbFile.exists();

            DatasManager.getInstance("sqlite", "~$bdd.db");

            if(needPopulate){
                DatasManager.populate();
            }

            while (!this.isInterrupted()) {
                try {
                    final Socket sockcli = sockserv.accept();
                    sockcli.setSoTimeout(ServerThread.TIMEOUT_CLIENT);
                    new Thread() {
                        @Override
                        public void run() {
                            try (NetworkServer n = new NetworkServer(sockcli)) {
                                n.read();
                            } catch (Exception ex) {
                                Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }.start();
                } catch (SocketTimeoutException ex) {
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
