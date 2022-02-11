package common;

import java.util.ArrayList;

public class RefreshServerMessage extends AbstractMessage {   // обновление
    private ArrayList<String> serverFileList;

    public RefreshServerMessage() {
    }

    public RefreshServerMessage(ArrayList<String> serverFileList) {
        this.serverFileList = serverFileList;
    }

    public ArrayList<String> getServerFileList() {
        return serverFileList;
    }
}
