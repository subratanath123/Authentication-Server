package net.auzy.dto.common;

import java.io.Serializable;

public class PhoneOption implements MessageOption, Serializable {

    private String phone;
    private String msgBody;
    private String subject;
    private String verificationCode;

    private PhoneOption() {
        // Private constructor to enforce the use of the builder
    }

    @Override
    public String getTargetDestination() {
        return phone;
    }

    @Override
    public void setTargetDestination(String targetDestination) {
        this.phone = targetDestination;
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

    public static EmailOptions.Builder builder() {
        return new EmailOptions.Builder();
    }

    public static class Builder {
        private final PhoneOption phoneOption;

        public Builder() {
            phoneOption = new PhoneOption();
        }

        public PhoneOption.Builder recipient(String recipient) {
            phoneOption.phone = recipient;
            return this;
        }

        public PhoneOption.Builder msgBody(String msgBody) {
            phoneOption.msgBody = msgBody;
            return this;
        }

        public PhoneOption.Builder subject(String subject) {
            phoneOption.subject = subject;
            return this;
        }

        public PhoneOption.Builder verificationCode(String verificationCode) {
            phoneOption.verificationCode = verificationCode;
            return this;
        }

        public PhoneOption build() {
            return phoneOption;
        }
    }
}
