package vn.hoidanit.jobhunter.util.error;

public class EmailExisted extends Exception {
    public EmailExisted(String message) {
        super(message);
    }
}
