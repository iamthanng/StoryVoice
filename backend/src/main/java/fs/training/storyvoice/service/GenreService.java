package fs.training.storyvoice.service;

import fs.training.storyvoice.dto.request.GenreRequest;
import fs.training.storyvoice.dto.response.GenreResponse;

import java.util.List;

public interface GenreService {
    List<GenreResponse> getAllGenres();
    GenreResponse getGenreById(Long id);
    GenreResponse createGenre(GenreRequest request);
    GenreResponse updateGenre(Long id, GenreRequest request);
    void deleteGenre(Long id);
}
