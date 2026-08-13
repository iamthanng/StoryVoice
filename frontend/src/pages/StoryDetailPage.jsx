import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { getStoryById } from '../services/storyService';
import { getChaptersByStory } from '../services/chapterService';

const AccessBadge = ({ level }) => {
  const config = {
    PUBLIC: { label: 'Công khai', className: 'bg-green-500/20 text-green-400 border-green-500/40' },
    MEMBER: { label: '🔒 Đăng nhập', className: 'bg-blue-500/20 text-blue-400 border-blue-500/40' },
    VIP: { label: '🔒 VIP', className: 'bg-yellow-500/20 text-yellow-400 border-yellow-500/40' },
  };
  const { label, className } = config[level] || config.PUBLIC;
  return (
    <span className={`text-xs font-semibold px-2 py-0.5 rounded border ${className}`}>
      {label}
    </span>
  );
};

const StoryDetailPage = () => {
  const { id } = useParams();
  const [story, setStory] = useState(null);
  const [chapters, setChapters] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [storyRes, chaptersRes] = await Promise.all([
          getStoryById(id),
          getChaptersByStory(id),
        ]);
        setStory(storyRes.data.data);
        setChapters(chaptersRes.data.data || []);
      } catch (err) {
        setError('Không thể tải thông tin truyện.');
        console.error(err);
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, [id]);

  if (loading) return (
    <div className="animate-pulse space-y-6">
      <div className="flex gap-8">
        <div className="bg-surface rounded-xl w-48 h-72 flex-shrink-0" />
        <div className="flex-1 space-y-4 pt-4">
          <div className="bg-surface h-8 rounded w-3/4" />
          <div className="bg-surface h-4 rounded w-1/3" />
          <div className="bg-surface h-4 rounded w-full" />
          <div className="bg-surface h-4 rounded w-5/6" />
        </div>
      </div>
    </div>
  );

  if (error) return (
    <div className="text-center py-20 text-red-400">{error}</div>
  );

  if (!story) return null;

  return (
    <div>
      {/* Story Info Section */}
      <div className="flex flex-col md:flex-row gap-8 mb-10 p-6 bg-surface rounded-2xl border border-gray-800">
        <div className="w-full md:w-48 flex-shrink-0">
          {story.coverImage ? (
            <img
              src={story.coverImage}
              alt={story.title}
              className="rounded-xl w-full object-cover aspect-[2/3] shadow-lg shadow-black/50"
            />
          ) : (
            <div className="rounded-xl w-full aspect-[2/3] bg-gray-800 flex items-center justify-center text-textSecondary">
              No Cover
            </div>
          )}
        </div>
        <div className="flex-1">
          <h1 className="text-3xl font-extrabold text-white mb-2">{story.title}</h1>
          <p className="text-textSecondary mb-1">
            <span className="text-textPrimary">Tác giả:</span> {story.authorName || 'Đang cập nhật'}
          </p>
          <p className="text-textSecondary mb-1">
            <span className="text-textPrimary">Thể loại:</span> {story.genreName || 'Đang cập nhật'}
          </p>
          <p className="text-textSecondary mb-4">
            <span className="text-textPrimary">Trạng thái:</span>{' '}
            <span className={story.status === 'COMPLETED' ? 'text-green-400' : 'text-blue-400'}>
              {story.status === 'COMPLETED' ? 'Hoàn thành' : 'Đang ra'}
            </span>
          </p>
          <p className="text-textSecondary text-sm leading-relaxed line-clamp-5">
            {story.description || 'Chưa có mô tả.'}
          </p>
          {chapters.length > 0 && (
            <Link
              to={`/story/${id}/chapter/${chapters[0]?.id}`}
              className="mt-5 inline-block bg-primary hover:bg-orange-600 text-white font-bold py-2 px-6 rounded-lg transition-colors"
            >
              ▶ Đọc từ chương 1
            </Link>
          )}
        </div>
      </div>

      {/* Chapter List */}
      <h2 className="text-xl font-bold text-white mb-4 border-b border-gray-800 pb-3">
        Danh sách chương ({chapters.length} chương)
      </h2>
      {chapters.length === 0 ? (
        <div className="text-center py-10 bg-surface rounded-lg border border-gray-800 text-textSecondary">
          Truyện này chưa có chương nào.
        </div>
      ) : (
        <div className="space-y-2">
          {chapters.map((chapter) => (
            <Link
              key={chapter.id}
              to={`/story/${id}/chapter/${chapter.id}`}
              className="flex items-center justify-between p-4 bg-surface hover:bg-gray-800 rounded-lg border border-gray-800 transition-colors group"
            >
              <div className="flex items-center gap-3">
                <span className="text-textSecondary text-sm w-8">#{chapter.chapterNumber}</span>
                <span className="text-textPrimary group-hover:text-primary transition-colors">
                  {chapter.title}
                </span>
              </div>
              <div className="flex items-center gap-3">
                {chapter.hasAudio && (
                  <span className="text-xs text-green-400 flex items-center gap-1">
                    🎵 Audio
                  </span>
                )}
                <AccessBadge level={chapter.accessLevel} />
              </div>
            </Link>
          ))}
        </div>
      )}
    </div>
  );
};

export default StoryDetailPage;
