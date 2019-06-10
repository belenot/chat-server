package com.belenot.chat.exception;

public class ServerException extends Throwable {

    private ServerExceptionCode code = ServerExceptionCode.UNDEF;
    private String message;
    

    public ServerExceptionCode getCode() { return code; }

    public String getMessage() { return message; }

    public ServerException() { super(); }
    
    public ServerException(String message) { super(message); this.message = message; }

    public ServerException(String message, Throwable throwable) { super(message, throwable); this.message = message; }

    public ServerException(ServerExceptionCode code) { super(); this.code = code; }

    public ServerException(String message, ServerExceptionCode code) { super(message); this.code = code; this.message = message; }

    public ServerException(String message, Throwable throwable, ServerExceptionCode code) { super(message, throwable); this.code = code; this.message = message; }

    public void setMessage(String message) { this.message = message; }
    
}
