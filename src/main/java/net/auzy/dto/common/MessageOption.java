package net.auzy.dto.common;

public interface MessageOption {

    String getTargetDestination();

    void setTargetDestination(String targetDestination);

    String getMsgBody();

    void setMsgBody(String msgBody);

    String getSubject();

    void setSubject(String subject);

    String getVerificationCode();

    void setVerificationCode(String verificationCode);

}
