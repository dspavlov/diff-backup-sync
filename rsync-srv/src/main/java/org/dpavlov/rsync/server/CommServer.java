package org.dpavlov.rsync.server;

import net.jcip.annotations.GuardedBy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class CommServer implements ICommServer {
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    @GuardedBy("this")
    private int localPort = -1;

    @Override
    public int getLocalPort() throws InterruptedException {
        synchronized (this) {
            while (localPort == -1) {
                wait();
            }
        }
        return localPort;
    }

    @PostConstruct
    public void start() {
        executor.submit(() -> {
            ServerSocket serverSocket = NetUtil.bindFreePort(NetConfig.BASE_PORT);

            int localPort = serverSocket.getLocalPort();
            synchronized (this) {
                this.localPort = localPort;
                notifyAll();
            }

            try (Socket clientSocket = serverSocket.accept()) {
                InputStream inputStream = clientSocket.getInputStream();

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}
