package com.management.library.mapper;

import com.management.library.dto.BookDTO;
import com.management.library.model.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BookMapper {

    BookDTO bookToBookDTO(Book book);
    Book bookDTOToBook(BookDTO bookDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateBookFromDTO(BookDTO bookDTO, @MappingTarget Book book);
}