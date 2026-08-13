package fs.training.storyvoice.mapper;

import fs.training.storyvoice.dto.request.GenreRequest;
import fs.training.storyvoice.dto.response.GenreResponse;
import fs.training.storyvoice.entity.Genre;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface GenreMapper {

    GenreResponse toGenreResponse(Genre genre);

    Genre toGenre(GenreRequest request);

    void updateGenreFromRequest(GenreRequest request, @MappingTarget Genre genre);
}
