package com.management.library.mapper;

import com.management.library.dto.ReservationDTO;
import com.management.library.model.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ReservationMapper {
    ReservationMapper INSTANCE = Mappers.getMapper(ReservationMapper.class);

    ReservationDTO reservationToReservationDTO(Reservation reservation);
    Reservation reservationDTOToReservation(ReservationDTO reservationDTO);
}
