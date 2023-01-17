package request;

import java.io.Serializable;

public class Response implements Serializable {
    static long serialVersionUID = 2L;
    int code;
    int ID;

    byte[] fileContent;

    public Response(int code) {
        this.code = code;
    }

    public Response(int code, int ID) {
        this.code = code;
        this.ID = ID;
    }

    public Response() {
    }

    public int getCode() {
        return code;
    }

    public long getID() {
        return ID;
    }

    public Response setCode(int code) {
        this.code = code;
        return this;
    }

    public Response setID(int ID) {
        this.ID = ID;
        return this;
    }

    @Override
    public String toString() {
        return "Response{" +
                "code=" + code +
                ", ID=" + ID +
                '}';
    }

    public Response setFileContent(byte[] fileContent) {
        this.fileContent = fileContent;
        return this;
    }

    public byte[] getFileContent() {
        return fileContent;
    }
}
