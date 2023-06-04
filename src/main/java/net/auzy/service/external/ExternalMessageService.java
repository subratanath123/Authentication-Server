package net.auzy.service.external;

import net.auzy.dto.common.MessageOption;

public interface ExternalMessageService<T extends MessageOption> {

    String sendSimpleMessage(T messageOption);

    String sendComplexMessage(T messageOption);

}
