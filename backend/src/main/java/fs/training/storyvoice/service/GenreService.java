package fs.training.storyvoice.service;

import fs.training.storyvoice.dto.request.GenreRequest;
import fs.training.storyvoice.dto.response.GenreResponse;
import fs.training.storyvoice.entity.Genre;
import fs.training.storyvoice.mapper.GenreMapper;
import fs.training.storyvoice.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;

    @Transactional(readOnly = true)
    public List<GenreResponse> getAllGenres() {
        return genreRepository.findAll().stream()
                .map(genreMapper::toGenreResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public GenreResponse getGenreById(Long id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thể loại với ID: " + id));
        return genreMapper.toGenreResponse(genre);
    }

    @Transactional
    public GenreResponse createGenre(GenreRequest request) {
        if (genreRepository.existsByName(request.getName())) {
            throw new RuntimeException("Thể loại '" + request.getName() + "' đã tồn tại");
        }
        Genre genre = genreMapper.toGenre(request);
        Genre savedGenre = genreRepository.save(genre);
        return genreMapper.toGenreResponse(savedGenre);
    }

    @Transactional
    public GenreResponse updateGenre(Long id, GenreRequest request) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thể loại với ID: " + id));

        if (!genre.getName().equalsIgnoreCase(request.getName()) && genreRepository.existsByName(request.getName())) {
            throw new RuntimeException("Thể loại '" + request.getName() + "' đã tồn tại");
        }

        genreMapper.updateGenreFromRequest(request, genre);
        Genre updatedGenre = genreRepository.save(genre);
        return genreMapper.toGenreResponse(updatedGenre);
    }

    @Transactional
    public void deleteGenre(Long id) {
        if (!genreRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy thể loại với ID: " + id);
        }
        genreRepository.deleteById(id);
    }
}
