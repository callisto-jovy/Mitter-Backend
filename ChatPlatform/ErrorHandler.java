
enum ErrorType {

    ACCOUNT_LOGIN_FAILED(122),
    ACCOUNT_DOES_NOT_EXIST(120),
    ACCOUNT_TAG_ALREADY_TAKEN(110),
    ACCOUNT_NOT_LOGGED_IN(112),
    ACCOUNT_NOT_LOGGED_OUT(113),
    RECEIVER_NOT_ONLINE(151),
    CHAT_DOES_NOT_EXIST(200);

    private int code;


    ErrorType(final int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
