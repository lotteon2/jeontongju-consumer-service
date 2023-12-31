package com.jeontongju.consumer.mapper;

import com.jeontongju.consumer.domain.Address;
import com.jeontongju.consumer.domain.Consumer;
import com.jeontongju.consumer.dto.request.AddressInfoForModifyRequestDto;
import com.jeontongju.consumer.dto.request.AddressInfoForRegisterRequestDto;
import com.jeontongju.consumer.dto.response.DefaultAddressInfoForInquiryResponseDto;
import io.github.bitbox.bitbox.dto.AddressDto;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {

  public DefaultAddressInfoForInquiryResponseDto toDefaultAddressInquiryResponseDto(Address address) {

    return DefaultAddressInfoForInquiryResponseDto.builder()
        .addressId(address.getAddressId())
        .basicAddress(address.getBasicAddress())
        .addressDetail(address.getAddressDetail())
        .zonecode(address.getZoneCode())
        .recipientName(address.getRecipientName())
        .recipientPhoneNumber(address.getRecipientPhoneNumber())
        .isDefault(address.getIsDefault())
        .build();
  }

  public List<DefaultAddressInfoForInquiryResponseDto> toListLookupResponseDto(
      List<Address> addresses) {

    List<DefaultAddressInfoForInquiryResponseDto> addressesResponseDto = new ArrayList<>();

    for (Address address : addresses) {
      addressesResponseDto.add(toDefaultAddressInquiryResponseDto(address));
    }
    return addressesResponseDto;
  }

  public AddressDto toDefaultAddressDto(Address address) {

    return AddressDto.builder()
        .basicAddress(address.getBasicAddress())
        .addressDetail(address.getAddressDetail())
        .recipientName(address.getRecipientName())
        .recipientPhoneNumber(address.getRecipientPhoneNumber())
        .zonecode(address.getZoneCode())
        .build();
  }
  
  public Address toEntity(AddressInfoForRegisterRequestDto registerRequestDto, Consumer consumer) {

    Boolean isDefault = consumer.getAddressList().isEmpty() || registerRequestDto.getIsDefault();

    return Address.builder()
        .basicAddress(registerRequestDto.getBasicAddress())
        .addressDetail(registerRequestDto.getAddressDetail())
        .zoneCode(registerRequestDto.getZonecode())
        .recipientName(registerRequestDto.getRecipientName())
        .recipientPhoneNumber(registerRequestDto.getRecipientPhoneNumber())
        .isDefault(isDefault)
        .consumer(consumer)
        .build();
  }

  public void toRenewed(Consumer consumer, Address address, AddressInfoForModifyRequestDto modifyRequestDto) {

    address.assignBasicAddress(modifyRequestDto.getBasicAddress());
    address.assignAddressDetail(modifyRequestDto.getAddressDetail());
    address.assignZonecone(modifyRequestDto.getZonecode());
    address.assignRecipientName(modifyRequestDto.getRecipientName());
    address.assignRecipientPhoneNumber(modifyRequestDto.getRecipientPhoneNumber());
    address.assignIsDefault(modifyRequestDto.getIsDefault());
  }
}
