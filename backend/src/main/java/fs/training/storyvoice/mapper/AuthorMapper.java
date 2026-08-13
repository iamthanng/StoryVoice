package fs.training.storyvoice.mapper;

import fs.training.storyvoice.dto.request.AuthorRequest;
import fs.training.storyvoice.dto.response.AuthorResponse;
import fs.training.storyvoice.entity.Author;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AuthorMapper {

    AuthorResponse toAuthorResponse(Author author);

    Author toAuthor(AuthorRequest request);

    void updateAuthorFromRequest(AuthorRequest request, @MappingTarget Author author);
}
