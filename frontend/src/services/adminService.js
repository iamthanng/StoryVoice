import api from './api';

// Stories
export const adminGetStories = (page = 0, size = 100, params = {}) =>
  api.get('/admin/stories', { params: { page, size, ...params } });

export const adminCreateStory = (data) =>
  api.post('/admin/stories', data);

export const adminUpdateStory = (id, data) =>
  api.put(`/admin/stories/${id}`, data);

export const adminDeleteStory = (id) =>
  api.delete(`/admin/stories/${id}`);

export const adminUploadCover = (id, file) => {
  const form = new FormData();
  form.append('coverImage', file);
  return api.post(`/admin/stories/${id}/cover`, form, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
};

// Chapters
export const adminGetChapters = (storyId) =>
  api.get(`/admin/chapters/story/${storyId}`);

export const adminGetChapterById = (id) =>
  api.get(`/admin/chapters/${id}`);

export const adminCreateChapter = (data) =>
  api.post('/admin/chapters', data);

export const adminUpdateChapter = (id, data) =>
  api.put(`/admin/chapters/${id}`, data);

export const adminDeleteChapter = (id) =>
  api.delete(`/admin/chapters/${id}`);

export const adminUploadAudio = (chapterId, file) => {
  const form = new FormData();
  form.append('audioFile', file);
  return api.post(`/admin/chapters/${chapterId}/audio`, form, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
};

// Users
export const adminGetUsers = (page = 0, size = 20) =>
  api.get('/admin/users', { params: { page, size } });

export const adminSetVip = (id, isVip) =>
  api.put(`/admin/users/${id}/vip`, null, { params: { grant: isVip } });

// Genres & Authors (for dropdowns) - match backend: /api/genres, /api/authors
export const getGenres = () => api.get('/genres');
export const getAuthors = () => api.get('/authors');

// Admin Genres
export const adminGetGenres = () => api.get('/admin/genres');
export const adminCreateGenre = (data) => api.post('/admin/genres', data);
export const adminUpdateGenre = (id, data) => api.put(`/admin/genres/${id}`, data);
export const adminDeleteGenre = (id) => api.delete(`/admin/genres/${id}`);

// Admin Authors
export const adminGetAuthors = () => api.get('/admin/authors');
export const adminCreateAuthor = (data) => api.post('/admin/authors', data);
export const adminUpdateAuthor = (id, data) => api.put(`/admin/authors/${id}`, data);
export const adminDeleteAuthor = (id) => api.delete(`/admin/authors/${id}`);

// Dashboard stats
export const getDashboardStats = () => api.get('/admin/dashboard/stats');
