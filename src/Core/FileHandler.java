package Core;

import java.io.*;

public class FileHandler {
    private String filePath;
    private File file;
    public FileHandler() {
    }

    public void setFile(PeerInfo peerInfo) {
        this.filePath = peerInfo.getId() + "/" + ServerThreadPool.file_name;		//file address
        this.file = new File(filePath);
        //if the peer has file
        if(peerInfo.getHasFile()) {
            if(!file.exists()) {
                System.out.println("Error: File doesn't exist.");
                System.exit(0);
            }
        }
        //if the peer doesn't have file
        else {
            File dir = new File("" + peerInfo.getId());
            dir.mkdir();
            file.delete();			//Delete and start writing to .temp file to keep intention clean
            file = new File(filePath + ".temp");
            //file.delete();

        }

    }


    //read piece from the file and send as bytes
    public byte[] read(int index, int peerID) {
        try {
            String path = "./" + peerID + "/" + ServerThreadPool.file_name;
            File cur = new File(path);
            RandomAccessFile input = new RandomAccessFile(cur, "r");
            input.seek(index * ServerThreadPool.piece_size);
            int piecesNum = ServerThreadPool.file_size / ServerThreadPool.piece_size;
            if(ServerThreadPool.file_size % ServerThreadPool.piece_size != 0) {
                piecesNum ++;
            }
            //create pieceContent of the regular size and the last piece
            byte[] pieceContent;
            if(index == (piecesNum - 1)) {
                pieceContent = new byte[ServerThreadPool.file_size - (piecesNum - 1) * ServerThreadPool.piece_size];
            }
            else {
                pieceContent = new byte[ServerThreadPool.piece_size];
            }
            input.read(pieceContent);
            input.close();
            System.out.println("Succeed to read index " + index);
            return pieceContent;
        } catch (Exception e) {
            System.out.println("Error: could not read index " + index);
            e.printStackTrace();
            return null;
        }
    }

    synchronized public void write(int index, byte[] data, int peerID) {
        try {
            String path = "./" + peerID + "/" + ServerThreadPool.file_name;
            File cur = new File(path);
            RandomAccessFile output = new RandomAccessFile(cur, "rw");
            output.seek(index * ServerThreadPool.piece_size);
            output.write(data);
            output.close();
            System.out.println("Succeed to write index " + index);
        } catch (Exception e) {
            System.out.println("Error: could not write index " + index);
        }
    }


    public static void main(String[] args){
       //test set, read, write methods

    }
}