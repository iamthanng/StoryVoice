import React, { useEffect, useState, useContext } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { getChapterContent, generateOrGetTts, getAudioStreamUrl } from '../services/chapterService';
import { getChaptersByStory } from '../services/chapterService';
import { AuthContext } from '../context/AuthContext';
import AudioPlayer from '../components/AudioPlayer';

const AccessDeniedScreen = ({ accessLevel }) => {
  const config = {
    MEMBER: {
      title: '🔒 Chương này yêu cầu đăng nhập',
      desc: 'Bạn cần đăng nhập để đọc chương này.',
      link: '/login',
      linkLabel: 'Đăng nhập ngay',
    },
    VIP: {
      title: '👑 Chương VIP',
      desc: 'Chương này dành riêng cho Thành viên VIP. Liên hệ Admin để được cấp quyền.',
      link: '/',
      linkLabel: 'Về trang chủ',
    },
  };
  const { title, desc, link, linkLabel } = config[accessLevel] || config.MEMBER;
  return (
    <div className="flex flex-col items-center justify-center py-24 text-center">
      <div className="text-6xl mb-4">🔐</div>
      <h2 className="text-2xl font-bold text-white mb-3">{title}</h2>
      <p className="text-textSecondary mb-6 max-w-sm">{desc}</p>
      <Link
        to={link}
        className="bg-primary hover:bg-orange-600 text-white font-bold py-2 px-6 rounded-lg transition-colors"
      >
        {linkLabel}
      </Link>
    </div>
  );
};

const ChapterPage = () => {
  const { storyId, chapterId } = useParams();
  const navigate = useNavigate();
  const { user } = useContext(AuthContext);

  const [chapter, setChapter] = useState(null);
  const [chapters, setChapters] = useState([]);
  const [loading, setLoading] = useState(true);
  const [accessDenied, setAccessDenied] = useState(null);
  const [audioSrc, setAudioSrc] = useState(null);
  const [ttsLoading, setTtsLoading] = useState(false);
  const [ttsError, setTtsError] = useState('');

  const fetchChapter = async () => {
    setLoading(true);
    setAccessDenied(null);
    setTtsError('');
    try {
      const [chapterRes, chaptersListRes] = await Promise.all([
        getChapterContent(chapterId),
        getChaptersByStory(storyId),
      ]);
      setChapter(chapterRes.data.data);
      setChapters(chaptersListRes.data.data || []);

      // If chapter already has audio, set it
      if (chapterRes.data.data?.audioFileId) {
        setAudioSrc(getAudioStreamUrl(chapterRes.data.data.audioFileId));
      }
    } catch (err) {
      if (err.response?.status === 403) {
        const requiredLevel = err.response?.data?.data?.accessLevel || 'MEMBER';
        setAccessDenied(requiredLevel);
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchChapter();
  }, [chapterId, storyId]);

  const handleGenerateTts = async () => {
    setTtsLoading(true);
    setTtsError('');
    try {
      const res = await generateOrGetTts(chapterId);
      const audioFileId = res.data.data?.audioFileId;
      if (audioFileId) {
        setAudioSrc(getAudioStreamUrl(audioFileId));
      }
    } catch (err) {
      setTtsError(err.response?.data?.message || 'Không thể tạo audio AI. Vui lòng thử lại sau.');
    } finally {
      setTtsLoading(false);
    }
  };

  // Navigation helpers
  const currentIndex = chapters.findIndex((c) => String(c.id) === String(chapterId));
  const prevChapter = currentIndex > 0 ? chapters[currentIndex - 1] : null;
  const nextChapter = currentIndex < chapters.length - 1 ? chapters[currentIndex + 1] : null;

  if (loading) {
    return (
      <div className="max-w-3xl mx-auto animate-pulse space-y-4">
        <div className="bg-surface h-8 rounded w-3/4 mb-6" />
        <div className="space-y-3">
          {[1, 2, 3, 4, 5, 6, 7, 8].map((i) => (
            <div key={i} className="bg-surface h-4 rounded" />
          ))}
        </div>
      </div>
    );
  }

  if (accessDenied) {
    return <AccessDeniedScreen accessLevel={accessDenied} />;
  }

  return (
    <div className="max-w-3xl mx-auto pb-24">
      {/* Header */}
      <div className="mb-8">
        <Link to={`/story/${storyId}`} className="text-textSecondary hover:text-primary text-sm transition-colors">
          ← Quay lại danh sách chương
        </Link>
        <h1 className="text-3xl font-extrabold text-white mt-3 mb-2">
          {chapter?.title}
        </h1>
        <p className="text-textSecondary text-sm">
          Chương {chapter?.chapterNumber}
        </p>
      </div>

      {/* Audio controls */}
      <div className="flex items-center gap-3 mb-6 p-4 bg-surface rounded-xl border border-gray-800">
        {audioSrc ? (
          <button
            onClick={() => setAudioSrc(audioSrc)}
            className="flex items-center gap-2 bg-green-600 hover:bg-green-700 text-white font-bold py-2 px-4 rounded-lg transition-colors text-sm"
          >
            🎵 Nghe audio chương này
          </button>
        ) : (
          <>
            <button
              onClick={handleGenerateTts}
              disabled={ttsLoading || !user}
              className="flex items-center gap-2 bg-primary hover:bg-orange-600 text-white font-bold py-2 px-4 rounded-lg transition-colors text-sm disabled:opacity-50"
            >
              {ttsLoading ? (
                <><span className="animate-spin">⏳</span> Đang tạo AI audio...</>
              ) : (
                <><span>🤖</span> Nghe bằng AI</>
              )}
            </button>
            {!user && (
              <span className="text-textSecondary text-xs">
                Bạn cần{' '}
                <Link to="/login" className="text-primary hover:underline">
                  đăng nhập
                </Link>{' '}
                để dùng tính năng này.
              </span>
            )}
          </>
        )}
        {ttsError && (
          <span className="text-red-400 text-xs">{ttsError}</span>
        )}
      </div>

      {/* Chapter content */}
      <article className="prose prose-invert max-w-none leading-8 text-[17px] text-textPrimary whitespace-pre-wrap">
        {chapter?.content || 'Nội dung trống.'}
      </article>

      {/* Chapter navigation */}
      <div className="flex justify-between mt-12 pt-6 border-t border-gray-800">
        {prevChapter ? (
          <Link
            to={`/story/${storyId}/chapter/${prevChapter.id}`}
            className="flex items-center gap-2 text-textSecondary hover:text-primary transition-colors"
          >
            ← {prevChapter.title}
          </Link>
        ) : <span />}
        {nextChapter ? (
          <Link
            to={`/story/${storyId}/chapter/${nextChapter.id}`}
            className="flex items-center gap-2 text-textSecondary hover:text-primary transition-colors"
          >
            {nextChapter.title} →
          </Link>
        ) : <span />}
      </div>

      {/* Sticky Audio Player */}
      {audioSrc && (
        <AudioPlayer
          audioSrc={audioSrc}
          chapterTitle={chapter?.title}
          onClose={() => setAudioSrc(null)}
        />
      )}
    </div>
  );
};

export default ChapterPage;
