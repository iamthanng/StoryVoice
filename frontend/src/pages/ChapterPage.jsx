import React, { useEffect, useState, useContext } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { getChapterContent, generateOrGetTts, getChaptersByStory, getFullAudioUrl } from '../services/chapterService';
import { AuthContext } from '../context/AuthContext';
import { AudioContext } from '../context/AudioContext';

const AccessDeniedScreen = ({ accessLevel }) => {
  const config = {
    MEMBER: {
      title: '🔒 Chương này yêu cầu đăng nhập',
      desc: 'Bạn cần đăng nhập tài khoản để đọc chương này.',
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
  const { currentAudio, playAudio } = useContext(AudioContext);

  const [chapter, setChapter] = useState(null);
  const [chapters, setChapters] = useState([]);
  const [loading, setLoading] = useState(true);
  const [accessDenied, setAccessDenied] = useState(null);
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
      const chapterData = chapterRes.data.data;
      setChapter(chapterData);
      setChapters(chaptersListRes.data.data || []);
    } catch (err) {
      if (err.response?.status === 403 || err.response?.status === 401) {
        const errorCode = err.response?.data?.errorCode;
        const level = errorCode === 'VIP_REQUIRED' ? 'VIP' : 'MEMBER';
        setAccessDenied(level);
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchChapter();
  }, [chapterId, storyId]);

  const handlePlayAudio = () => {
    if (!user) {
      navigate('/login');
      return;
    }
    if (chapter?.audioUrl) {
      const fullUrl = getFullAudioUrl(chapter.audioUrl);
      playAudio({
        src: fullUrl,
        title: chapter.title,
        storyTitle: chapter.storyTitle,
        chapterId: chapterId,
        storyId: storyId,
      });
    }
  };

  const handleGenerateTts = async () => {
    if (!user) {
      navigate('/login');
      return;
    }
    setTtsLoading(true);
    setTtsError('');
    try {
      const res = await generateOrGetTts(chapterId);
      const audioUrl = res.data.data?.audioUrl;
      if (audioUrl) {
        setChapter((prev) => ({
          ...prev,
          hasAudio: true,
          audioUrl: audioUrl,
        }));
        const fullUrl = getFullAudioUrl(audioUrl);
        playAudio({
          src: fullUrl,
          title: chapter?.title,
          storyTitle: chapter?.storyTitle,
          chapterId: chapterId,
          storyId: storyId,
        });
      } else {
        setTtsError('Không nhận được file audio từ hệ thống.');
      }
    } catch (err) {
      setTtsError(err.response?.data?.message || 'Không thể tạo audio AI. Vui lòng thử lại sau.');
    } finally {
      setTtsLoading(false);
    }
  };

  const isCurrentPlaying = currentAudio?.src === getFullAudioUrl(chapter?.audioUrl);

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

      {/* Audio Bar */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-3 mb-8 p-4 bg-surface rounded-xl border border-gray-800 shadow-md">
        <div className="flex items-center gap-3">
          {!user ? (
            <div className="flex items-center gap-3">
              <span className="text-2xl">🎧</span>
              <div>
                <p className="text-sm font-semibold text-white">Phát Audio Chương</p>
                <p className="text-xs text-textSecondary">
                  Bạn cần{' '}
                  <Link to="/login" className="text-primary font-bold hover:underline">
                    Đăng nhập
                  </Link>{' '}
                  để nghe Audio hoặc tạo giọng nói AI.
                </p>
              </div>
            </div>
          ) : chapter?.hasAudio && chapter?.audioUrl ? (
            <div className="flex items-center gap-3">
              <button
                onClick={handlePlayAudio}
                className={`flex items-center gap-2 font-bold py-2.5 px-5 rounded-lg transition-colors text-sm shadow-lg ${
                  isCurrentPlaying
                    ? 'bg-orange-500 hover:bg-orange-600 text-white shadow-orange-950/40 animate-pulse'
                    : 'bg-green-600 hover:bg-green-700 text-white shadow-green-900/30'
                }`}
              >
                <span>{isCurrentPlaying ? '🎵' : '▶'}</span>
                {isCurrentPlaying ? 'Đang phát ở Player' : 'Nghe Audio'}
              </button>
              <span className="text-xs text-green-400 font-medium">✓ Đã có sẵn file Audio</span>
            </div>
          ) : (
            <div className="flex items-center gap-3">
              <button
                onClick={handleGenerateTts}
                disabled={ttsLoading}
                className="flex items-center gap-2 bg-primary hover:bg-orange-600 text-white font-bold py-2.5 px-5 rounded-lg transition-colors text-sm disabled:opacity-50 shadow-lg shadow-orange-900/30"
              >
                {ttsLoading ? (
                  <>
                    <span className="animate-spin">⏳</span> Đang tạo giọng nói AI...
                  </>
                ) : (
                  <>
                    <span>🤖</span> Nghe bằng AI (TTS)
                  </>
                )}
              </button>
              <span className="text-xs text-textSecondary">Chưa có audio. Bấm để tạo giọng nói AI.</span>
            </div>
          )}
        </div>

        {ttsError && (
          <div className="text-red-400 text-xs bg-red-500/10 border border-red-500/30 px-3 py-1.5 rounded">
            {ttsError}
          </div>
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
            className="flex items-center gap-2 text-textSecondary hover:text-primary transition-colors text-sm"
          >
            ← {prevChapter.title}
          </Link>
        ) : <span />}
        {nextChapter ? (
          <Link
            to={`/story/${storyId}/chapter/${nextChapter.id}`}
            className="flex items-center gap-2 text-textSecondary hover:text-primary transition-colors text-sm"
          >
            {nextChapter.title} →
          </Link>
        ) : <span />}
      </div>
    </div>
  );
};

export default ChapterPage;
