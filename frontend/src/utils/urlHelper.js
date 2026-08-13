/**
 * Utility helper to construct full URL for backend static media files (/uploads/...)
 */
export const getMediaUrl = (path) => {
  if (!path) return null;
  if (path.startsWith('http://') || path.startsWith('https://')) {
    return path;
  }
  let cleanPath = path.startsWith('/') ? path : `/${path}`;
  if (!cleanPath.startsWith('/uploads/')) {
    cleanPath = `/uploads${cleanPath}`;
  }
  return `http://localhost:8080${cleanPath}`;
};
