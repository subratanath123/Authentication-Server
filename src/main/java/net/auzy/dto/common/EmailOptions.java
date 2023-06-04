package net.auzy.dto.common;

import java.io.Serializable;

public class EmailOptions implements MessageOption, Serializable {

    private String email;
    private String msgBody;
    private String subject;
    private String verificationCode;

    private EmailOptions() {
        // Private constructor to enforce the use of the builder
    }

    @Override
    public String getTargetDestination() {
        return email;
    }

    @Override
    public void setTargetDestination(String targetDestination) {
        this.email = targetDestination;
    }

    public String getMsgBody() {
        return msgBody;
    }

    public void setMsgBody(String msgBody) {
        this.msgBody = msgBody;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final EmailOptions emailOptions;

        public Builder() {
            emailOptions = new EmailOptions();
        }

        public Builder recipient(String recipient) {
            emailOptions.email = recipient;
            return this;
        }

        public Builder msgBody(String msgBody) {
            emailOptions.msgBody = msgBody;
            return this;
        }

        public Builder subject(String subject) {
            emailOptions.subject = subject;
            return this;
        }

        public Builder verificationCode(String verificationCode) {
            emailOptions.verificationCode = verificationCode;
            return this;
        }

        public EmailOptions build() {
            return emailOptions;
        }
    }
}
