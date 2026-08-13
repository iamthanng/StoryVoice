import api from './api';

// PublicChapterController maps to /api (base) so:
// GET /api/stories/{storyId}/chapters
// GET /api/chapters/{id}
export const getChaptersByStory = (storyId) =>
  api.get(`/stories/${storyId}/chapters`);

export const getChapterContent = (chapterId) =>
  api.get(`/chapters/${chapterId}`);

// AudioController: POST /api/chapters/{chapterId}/tts
export const generateOrGetTts = (chapterId, voiceId = null) => {
  const params = voiceId ? { voiceId } : {};
  return api.post(`/chapters/${chapterId}/tts`, null, { params });
};

import { getMediaUrl } from '../utils/urlHelper';

// Helper function to build full Audio URL for browser HTML5 <audio>
export const getFullAudioUrl = (audioUrl) => getMediaUrl(audioUrl);
