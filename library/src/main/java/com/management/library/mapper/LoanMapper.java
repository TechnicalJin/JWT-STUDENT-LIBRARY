package com.management.library.mapper;

import com.management.library.dto.LoanDTO;
import com.management.library.model.Loan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring") // Add componentModel parameter
public interface LoanMapper {
    LoanDTO loanToLoanDTO(Loan loan);

    @Mapping(target = "id", ignore = true)
    Loan loanDTOToLoan(LoanDTO loanDTO);
}