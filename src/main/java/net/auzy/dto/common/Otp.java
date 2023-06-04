package net.auzy.dto.common;

import lombok.Data;
import net.auzy.enums.VerificationType;

import java.io.Serializable;

@Data
public class Otp implements Serializable {

    private VerificationType verificationType;
    private String phone;
    private String email;
    private String verificationCode;

    private Otp(Builder builder) {
        this.verificationType = builder.verificationType;
        this.phone = builder.phone;
        this.email = builder.email;
        this.verificationCode = builder.verificationCode;
    }

    public static class Builder {
        private VerificationType verificationType;
        private String phone;
        private String email;
        private String verificationCode;

        public Builder verificationType(VerificationType verificationType) {
            this.verificationType = verificationType;
            return this;
        }

        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder verificationCode(String verificationCode) {
            this.verificationCode = verificationCode;
            return this;
        }

        public Otp build() {
            return new Otp(this);
        }
    }
}
