/**
 * @project flight-reservation-management-system
 * @author DEV on 18/11/2024
 */

package com.windev.payment_service.mapper;

import com.windev.payment_service.dto.PaymentEventDTO;
import com.windev.payment_service.entity.Payment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    PaymentEventDTO toPaymentEventDTO(Payment payment);
}
