package Core;

import java.io.*;
import java.util.*;


//get the peer information from configuration file
//and store the peer info to the peerInfoList
public class PeerInfoHandler {
    private List<PeerInfo> peerInfoList;
    public PeerInfoHandler() {
        try {
            FileReader fr = new FileReader("./Config/PeerInfo.cfg");
            BufferedReader bufferedReader = new BufferedReader(fr);
            this.peerInfoList = new ArrayList<>();
            String str = null;
            while((str = bufferedReader.readLine()) != null) {
                String[] peerArgs = str.split(" ");
                PeerInfo curPeerInfo = new PeerInfo(Integer.parseInt(peerArgs[0]),  //peer id
                        peerArgs[1],                                                //peer host
                        Integer.parseInt(peerArgs[2]),                              //peer port
                        peerArgs[3].equals("1"));                                   //if peer has file
                peerInfoList.add(curPeerInfo);
            }
            bufferedReader.close();
            fr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<PeerInfo> getPeerInfoList() {
        return peerInfoList;
    }

    public PeerInfo getPeerInfoById(int id) throws Exception{
        PeerInfo curPeer = null;
        for (PeerInfo peer : peerInfoList) {
            if (peer.getId() == id) {
                curPeer = peer;
            }
        }
        if (curPeer == null) {
            throw new Exception("Could not find this peer");
        }
        return curPeer;
    }

    public List<PeerInfo> getPeersBefore(PeerInfo info) {
        List<PeerInfo> ans = new ArrayList<>();
        for (PeerInfo peer : peerInfoList) {
            if (peer.getId() == info.getId()) {
                break;
            } else {
                ans.add(peer);
            }
        }
        return ans;
    }
    public List<PeerInfo> getPeersAfter(PeerInfo info) {
        List<PeerInfo> ans = new ArrayList<>();
        int flag = 0;
        for (PeerInfo peer : peerInfoList) {
            if (peer.getId() == info.getId()) {
                flag = 1;
                continue;
            }
            if (flag == 1) {
                ans.add(peer);
            }
        }
        return ans;
    }

    public int getSize() {
        return peerInfoList.size();
    }

    public static void main(String[] args){
        PeerInfoHandler peerInfoHandler = new PeerInfoHandler();
        System.out.println(peerInfoHandler.getSize());
    }
}
