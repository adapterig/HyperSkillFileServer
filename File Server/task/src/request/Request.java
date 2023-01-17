package request;

import java.io.Serializable;
import java.util.Arrays;

public class Request implements Serializable {
    static final long serialVersionUID = 1L;
    int action; /* 1 - get a file, 2 - save a file, 3 - delete a file, 4 - exit  */
    int howToFind; /* 1 - name, 2 - id */
    Integer fileID;
    String fileName;
    byte[] fileContent;

    public Request(int action) {
        this.action = action;
        fileName = null;
        fileContent = null;
    }

    public Request(int action, String fileName) {
        this.action = action;
        this.fileName = fileName;
        this.fileContent = null;
    }

    public Request(int action, String fileName, byte[] bytes) {
        this.action = action;
        this.fileName = fileName;
        this.fileContent = bytes;
    }

    public Request() {
    }

    public int getAction() {
        return action;
    }

    public int getHowToFind() {
        return howToFind;
    }

    public Integer getFileID() {
        return fileID;
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getFileContent() {
        return fileContent;
    }

    @Override
    public String toString() {
        return "Request{" +
                "action=" + action +
                ", howToFind=" + howToFind +
                ", fileID='" + fileID + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileContent=" + Arrays.toString(fileContent) +
                '}';
    }

    public Request setAction(int action) {
        this.action = action;
        return this;
    }

    public Request setHowToFind(int howToFind) {
        this.howToFind = howToFind;
        return this;
    }

    public Request setFileID(Integer fileID) {
        this.fileID = fileID;
        return this;
    }

    public Request setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }
}
