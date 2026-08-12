package fs.training.storyvoice.mapper;

import fs.training.storyvoice.dto.request.ChapterRequest;
import fs.training.storyvoice.dto.response.ChapterResponse;
import fs.training.storyvoice.entity.Chapter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ChapterMapper {

    @Mapping(target = "storyId", source = "story.id")
    @Mapping(target = "storyTitle", source = "story.title")
    @Mapping(target = "isLocked", ignore = true)
    @Mapping(target = "hasAudio", ignore = true)
    @Mapping(target = "audioUrl", ignore = true)
    @Mapping(target = "audioSource", ignore = true)
    ChapterResponse toChapterResponse(Chapter chapter);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "story", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Chapter toChapter(ChapterRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "story", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateChapterFromRequest(ChapterRequest request, @MappingTarget Chapter chapter);
}
