package Core;

import java.io.*;

//store basic information for each peer
public class PeerInfo {
    private int peerID;
    private String host;
    private int port;
    private Boolean hasFile;
    private File file;
    private String filePath;
    BufferedWriter writer;

    public PeerInfo(int peerID, String host, int port, Boolean hasFile) {
        this.peerID = peerID;
        this.host = host;
        this.port = port;
        this.hasFile = hasFile;
    }
    public int getId() {
        return peerID;
    }
    public String getHost() {
        return host;
    }
    public int getPort() {
        return port;
    }
    public Boolean getHasFile() {
        return hasFile;
    }
    public void setHasFile(boolean hasFile){
        this.hasFile = hasFile;
    }

}