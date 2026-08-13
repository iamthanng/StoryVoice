package fs.training.storyvoice.service;

import fs.training.storyvoice.dto.request.AuthorRequest;
import fs.training.storyvoice.dto.response.AuthorResponse;
import fs.training.storyvoice.entity.Author;
import fs.training.storyvoice.mapper.AuthorMapper;
import fs.training.storyvoice.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;

    @Transactional(readOnly = true)
    public List<AuthorResponse> getAllAuthors() {
        return authorRepository.findAll().stream()
                .map(authorMapper::toAuthorResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AuthorResponse getAuthorById(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tác giả với ID: " + id));
        return authorMapper.toAuthorResponse(author);
    }

    @Transactional
    public AuthorResponse createAuthor(AuthorRequest request) {
        Author author = authorMapper.toAuthor(request);
        Author savedAuthor = authorRepository.save(author);
        return authorMapper.toAuthorResponse(savedAuthor);
    }

    @Transactional
    public AuthorResponse updateAuthor(Long id, AuthorRequest request) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tác giả với ID: " + id));

        authorMapper.updateAuthorFromRequest(request, author);
        Author updatedAuthor = authorRepository.save(author);
        return authorMapper.toAuthorResponse(updatedAuthor);
    }

    @Transactional
    public void deleteAuthor(Long id) {
        if (!authorRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy tác giả với ID: " + id);
        }
        authorRepository.deleteById(id);
    }
}
