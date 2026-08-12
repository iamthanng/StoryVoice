package fs.training.storyvoice.mapper;

import fs.training.storyvoice.dto.request.StoryRequest;
import fs.training.storyvoice.dto.response.StoryResponse;
import fs.training.storyvoice.entity.Story;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface StoryMapper {

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "authorName", source = "author.name")
    @Mapping(target = "genreId", source = "genre.id")
    @Mapping(target = "genreName", source = "genre.name")
    @Mapping(target = "totalChapters", ignore = true)
    StoryResponse toStoryResponse(Story story);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "genre", ignore = true)
    @Mapping(target = "coverImage", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Story toStory(StoryRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "genre", ignore = true)
    @Mapping(target = "coverImage", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateStoryFromRequest(StoryRequest request, @MappingTarget Story story);
}
