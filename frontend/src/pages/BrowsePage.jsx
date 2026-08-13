import React, { useEffect, useState } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { getAllStories, searchStories } from '../services/storyService';
import { getMediaUrl } from '../utils/urlHelper';
import api from '../services/api';

const BrowsePage = () => {
  const [searchParams, setSearchParams] = useSearchParams();
  const [stories, setStories] = useState([]);
  const [genres, setGenres] = useState([]);
  const [loading, setLoading] = useState(true);
  const [totalPages, setTotalPages] = useState(0);

  const keyword = searchParams.get('keyword') || '';
  const genreId = searchParams.get('genre') || '';
  const page = parseInt(searchParams.get('page') || '0');

  useEffect(() => {
    api.get('/genres').then((r) => setGenres(r.data.data || [])).catch(() => {});
  }, []);

  useEffect(() => {
    setLoading(true);
    const fetch = keyword
      ? searchStories(keyword, page)
      : getAllStories(page, 12, genreId ? { genreId } : {});

    fetch
      .then((r) => {
        const pageData = r.data.data;
        setStories(pageData?.content || []);
        setTotalPages(pageData?.totalPages || 0);
      })
      .catch(() => setStories([]))
      .finally(() => setLoading(false));
  }, [keyword, genreId, page]);

  const setParam = (key, value) => {
    const next = new URLSearchParams(searchParams);
    if (value) next.set(key, value); else next.delete(key);
    next.delete('page');
    setSearchParams(next);
  };

  const setPage = (p) => {
    const next = new URLSearchParams(searchParams);
    next.set('page', p);
    setSearchParams(next);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  return (
    <div>
      {/* Search & Filter bar */}
      <div className="flex flex-col md:flex-row gap-3 mb-8 p-4 bg-surface rounded-xl border border-gray-800">
        <input
          type="text"
          placeholder="🔍  Tìm kiếm theo tên truyện, tác giả..."
          defaultValue={keyword}
          onKeyDown={(e) => {
            if (e.key === 'Enter') setParam('keyword', e.target.value.trim());
          }}
          className="flex-1 bg-background border border-gray-700 rounded-lg px-4 py-2 text-textPrimary focus:outline-none focus:border-primary transition-colors text-sm"
        />
        <select
          value={genreId}
          onChange={(e) => setParam('genre', e.target.value)}
          className="bg-background border border-gray-700 rounded-lg px-4 py-2 text-textPrimary focus:outline-none focus:border-primary transition-colors text-sm"
        >
          <option value="">-- Tất cả thể loại --</option>
          {genres.map((g) => (
            <option key={g.id} value={g.id}>{g.name}</option>
          ))}
        </select>
        {(keyword || genreId) && (
          <button
            onClick={() => setSearchParams({})}
            className="text-textSecondary hover:text-white text-sm px-3 py-2 border border-gray-700 rounded-lg transition-colors"
          >
            ✕ Xóa bộ lọc
          </button>
        )}
      </div>

      {/* Results info */}
      {(keyword || genreId) && !loading && (
        <p className="text-textSecondary text-sm mb-4">
          {keyword && <>Kết quả tìm kiếm cho: <span className="text-white font-semibold">"{keyword}"</span> — </>}
          {stories.length === 0 ? 'Không tìm thấy truyện phù hợp.' : `${stories.length} truyện`}
        </p>
      )}

      {/* Story Grid */}
      {loading ? (
        <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-6 gap-5">
          {Array.from({ length: 12 }).map((_, i) => (
            <div key={i} className="animate-pulse">
              <div className="bg-surface h-52 rounded-lg mb-3" />
              <div className="bg-surface h-3 rounded w-3/4 mb-2" />
              <div className="bg-surface h-3 rounded w-1/2" />
            </div>
          ))}
        </div>
      ) : stories.length === 0 ? (
        <div className="text-center py-20 text-textSecondary bg-surface rounded-xl border border-gray-800">
          Không có truyện nào phù hợp.
        </div>
      ) : (
        <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-6 gap-5">
          {stories.map((story) => (
            <Link to={`/story/${story.id}`} key={story.id} className="group">
              <div className="relative overflow-hidden rounded-xl aspect-[2/3] mb-3 bg-surface shadow-md shadow-black/40">
                {story.coverImage ? (
                  <img src={getMediaUrl(story.coverImage)} alt={story.title} className="object-cover w-full h-full group-hover:scale-105 transition-transform duration-300" />
                ) : (
                  <div className="w-full h-full flex items-center justify-center text-textSecondary text-xs bg-gray-800">No Cover</div>
                )}
                {story.status === 'COMPLETED' && (
                  <span className="absolute top-1.5 right-1.5 bg-green-600 text-white text-[10px] font-bold px-1.5 py-0.5 rounded">Full</span>
                )}
              </div>
              <h3 className="text-sm font-semibold text-white line-clamp-2 group-hover:text-primary transition-colors leading-snug">{story.title}</h3>
              <p className="text-xs text-textSecondary mt-1 truncate">{story.authorName || 'Đang cập nhật'}</p>
            </Link>
          ))}
        </div>
      )}

      {/* Pagination */}
      {totalPages > 1 && (
        <div className="flex justify-center items-center gap-2 mt-10">
          <button onClick={() => setPage(page - 1)} disabled={page === 0} className="px-4 py-2 rounded-lg bg-surface border border-gray-700 text-textSecondary hover:text-white disabled:opacity-30 transition-colors text-sm">← Trước</button>
          {Array.from({ length: Math.min(totalPages, 7) }, (_, i) => {
            const p = totalPages <= 7 ? i : Math.max(0, page - 3) + i;
            if (p >= totalPages) return null;
            return (
              <button key={p} onClick={() => setPage(p)} className={`w-9 h-9 rounded-lg text-sm font-medium transition-colors border ${p === page ? 'bg-primary border-primary text-white' : 'bg-surface border-gray-700 text-textSecondary hover:text-white'}`}>{p + 1}</button>
            );
          })}
          <button onClick={() => setPage(page + 1)} disabled={page >= totalPages - 1} className="px-4 py-2 rounded-lg bg-surface border border-gray-700 text-textSecondary hover:text-white disabled:opacity-30 transition-colors text-sm">Sau →</button>
        </div>
      )}
    </div>
  );
};

export default BrowsePage;
