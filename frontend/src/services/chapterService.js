import api from './api';

export const getChaptersByStory = (storyId) =>
  api.get(`/public/stories/${storyId}/chapters`);

export const getChapterContent = (chapterId) =>
  api.get(`/public/chapters/${chapterId}`);

export const generateOrGetTts = (chapterId, voiceId = null) => {
  const params = voiceId ? { voiceId } : {};
  return api.post(`/chapters/${chapterId}/tts`, null, { params });
};

export const getAudioStreamUrl = (audioId) =>
  `http://localhost:8080/api/audio/stream/${audioId}`;
