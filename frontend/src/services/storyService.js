import api from './api';

export const getAllStories = (page = 0, size = 10, params = {}) =>
  api.get('/public/stories', { params: { page, size, ...params } });

export const getStoryById = (id) =>
  api.get(`/public/stories/${id}`);

export const searchStories = (keyword, page = 0, size = 10) =>
  api.get('/public/stories/search', { params: { keyword, page, size } });

export const getStoriesByGenre = (genreId, page = 0, size = 10) =>
  api.get(`/public/stories/genre/${genreId}`, { params: { page, size } });
