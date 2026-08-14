import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import {
  adminGetChapters, adminGetChapterById, adminCreateChapter, adminUpdateChapter,
  adminDeleteChapter, adminUploadAudio,
} from '../../services/adminService';
import { getMediaUrl } from '../../utils/urlHelper';

const ACCESS_OPTIONS = [
  { value: 'PUBLIC', label: 'Công khai', color: 'text-green-400' },
  { value: 'MEMBER', label: 'Yêu cầu đăng nhập', color: 'text-blue-400' },
  { value: 'VIP', label: 'VIP', color: 'text-yellow-400' },
];

const emptyForm = { title: '', content: '', chapterNumber: '', accessLevel: 'PUBLIC', storyId: '' };

const AdminChapters = () => {
  const { storyId } = useParams();
  const [chapters, setChapters] = useState([]);
  const [loading, setLoading] = useState(true);

  // Search & Filter state
  const [searchKeyword, setSearchKeyword] = useState('');
  const [accessFilter, setAccessFilter] = useState('ALL');
  const [audioFilter, setAudioFilter] = useState('ALL');

  // Modal State
  const [showForm, setShowForm] = useState(false);
  const [editing, setEditing] = useState(null);
  const [form, setForm] = useState({ ...emptyForm, storyId });
  const [audioFile, setAudioFile] = useState(null);
  const [saving, setSaving] = useState(false);
  const [msg, setMsg] = useState('');

  const fetchChapters = () => {
    setLoading(true);
    adminGetChapters(storyId)
      .then((r) => setChapters(r.data.data || []))
      .catch(() => setChapters([]))
      .finally(() => setLoading(false));
  };

  useEffect(() => { fetchChapters(); }, [storyId]);

  const openCreate = () => {
    const nextNum = chapters.length > 0 ? Math.max(...chapters.map((c) => c.chapterNumber || 0)) + 1 : 1;
    setEditing(null);
    setForm({ ...emptyForm, storyId, chapterNumber: String(nextNum) });
    setAudioFile(null); setMsg(''); setShowForm(true);
  };

  const openEdit = async (c) => {
    setMsg('');
    setAudioFile(null);
    // Lấy nội dung đầy đủ của chương từ API (danh sách không chứa content)
    try {
      const res = await adminGetChapterById(c.id);
      const full = res.data.data;
      setEditing(full);
      setForm({
        title: full.title,
        content: full.content || '',
        chapterNumber: String(full.chapterNumber),
        accessLevel: full.accessLevel,
        storyId,
      });
    } catch {
      // Fallback: dùng dữ liệu có sẵn nếu API lỗi
      setEditing(c);
      setForm({ title: c.title, content: c.content || '', chapterNumber: String(c.chapterNumber), accessLevel: c.accessLevel, storyId });
    }
    setShowForm(true);
  };

  const handleSave = async (e) => {
    e.preventDefault();
    setSaving(true); setMsg('');
    try {
      let id = editing?.id;
      const payload = { ...form, chapterNumber: parseInt(form.chapterNumber), storyId: parseInt(storyId) };
      if (editing) {
        await adminUpdateChapter(id, payload);
      } else {
        const res = await adminCreateChapter(payload);
        id = res.data.data?.id;
      }
      if (audioFile && id) {
        await adminUploadAudio(id, audioFile);
      }
      setMsg('✅ Lưu thành công!');
      fetchChapters();
      setTimeout(() => { setShowForm(false); setMsg(''); }, 800);
    } catch (err) {
      setMsg('❌ ' + (err.response?.data?.message || 'Có lỗi xảy ra.'));
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (id, title) => {
    if (!window.confirm(`Xóa chương "${title}"?`)) return;
    await adminDeleteChapter(id).catch(() => {});
    fetchChapters();
  };

  // Filter chapters locally for instant search response
  const filteredChapters = chapters.filter((c) => {
    const matchesKeyword =
      !searchKeyword ||
      c.title.toLowerCase().includes(searchKeyword.toLowerCase()) ||
      String(c.chapterNumber).includes(searchKeyword);

    const matchesAccess = accessFilter === 'ALL' || c.accessLevel === accessFilter;

    const matchesAudio =
      audioFilter === 'ALL' ||
      (audioFilter === 'HAS_AUDIO' && c.hasAudio) ||
      (audioFilter === 'NO_AUDIO' && !c.hasAudio);

    return matchesKeyword && matchesAccess && matchesAudio;
  });

  return (
    <div>
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 mb-6">
        <div className="flex items-center gap-3">
          <Link to="/admin/stories" className="text-textSecondary hover:text-primary text-sm transition-colors">
            ← Quay lại
          </Link>
          <h1 className="text-3xl font-extrabold text-white">Quản lý Chương</h1>
        </div>
        <button onClick={openCreate} className="bg-primary hover:bg-orange-600 text-white font-bold py-2.5 px-5 rounded-xl text-sm transition-colors shadow-lg shadow-orange-950/40">
          + Thêm chương mới
        </button>
      </div>

      {/* Filter & Search Controls */}
      <div className="bg-surface rounded-xl p-4 border border-gray-800 mb-6 flex flex-col sm:flex-row gap-3 items-center justify-between">
        <div className="relative flex-1 w-full">
          <span className="absolute inset-y-0 left-0 pl-3 flex items-center text-textSecondary">🔍</span>
          <input
            type="text"
            placeholder="Tìm theo tiêu đề hoặc số chương..."
            value={searchKeyword}
            onChange={(e) => setSearchKeyword(e.target.value)}
            className="w-full bg-background border border-gray-700 rounded-lg pl-9 pr-3 py-2 text-textPrimary focus:outline-none focus:border-primary text-sm"
          />
        </div>
        <div className="flex flex-wrap sm:flex-nowrap gap-3 w-full sm:w-auto">
          <select
            value={accessFilter}
            onChange={(e) => setAccessFilter(e.target.value)}
            className="bg-background border border-gray-700 rounded-lg px-3 py-2 text-textPrimary focus:outline-none focus:border-primary text-sm w-full sm:w-auto"
          >
            <option value="ALL">Tất cả quyền truy cập</option>
            <option value="PUBLIC">Công khai</option>
            <option value="MEMBER">Yêu cầu đăng nhập</option>
            <option value="VIP">VIP</option>
          </select>
          <select
            value={audioFilter}
            onChange={(e) => setAudioFilter(e.target.value)}
            className="bg-background border border-gray-700 rounded-lg px-3 py-2 text-textPrimary focus:outline-none focus:border-primary text-sm w-full sm:w-auto"
          >
            <option value="ALL">Tất cả Audio</option>
            <option value="HAS_AUDIO">🎵 Có audio</option>
            <option value="NO_AUDIO">Chưa có audio</option>
          </select>
        </div>
      </div>

      {/* Table */}
      {loading ? (
        <div className="text-textSecondary text-center py-10">Đang tải danh sách chương...</div>
      ) : (
        <div className="bg-surface rounded-xl border border-gray-800 overflow-hidden shadow-xl">
          <table className="w-full text-sm">
            <thead className="border-b border-gray-800 text-textSecondary text-xs uppercase bg-gray-900/50">
              <tr>
                <th className="text-left p-4 w-16">Số</th>
                <th className="text-left p-4">Tiêu đề</th>
                <th className="text-left p-4 hidden md:table-cell">Quyền truy cập</th>
                <th className="text-left p-4 hidden md:table-cell">Audio</th>
                <th className="text-right p-4">Hành động</th>
              </tr>
            </thead>
            <tbody>
              {filteredChapters.map((c) => {
                const acc = ACCESS_OPTIONS.find((o) => o.value === c.accessLevel);
                return (
                  <tr key={c.id} className="border-b border-gray-800/50 hover:bg-gray-800/30 transition-colors">
                    <td className="p-4 text-textSecondary font-mono font-semibold">#{c.chapterNumber}</td>
                    <td className="p-4 text-white font-medium">{c.title}</td>
                    <td className={`p-4 hidden md:table-cell text-xs font-bold ${acc?.color || ''}`}>{acc?.label}</td>
                    <td className="p-4 hidden md:table-cell">
                      {c.hasAudio ? (
                        <span className="text-xs font-bold px-2 py-1 rounded bg-green-500/20 text-green-400 border border-green-500/30">
                          🎵 Có audio
                        </span>
                      ) : (
                        <span className="text-textSecondary text-xs">—</span>
                      )}
                    </td>
                    <td className="p-4 text-right">
                      <div className="flex gap-2 justify-end">
                        <button onClick={() => openEdit(c)} className="text-xs px-3 py-1.5 bg-blue-600/30 hover:bg-blue-600/50 text-blue-400 rounded transition-colors font-medium">Sửa</button>
                        <button onClick={() => handleDelete(c.id, c.title)} className="text-xs px-3 py-1.5 bg-red-600/20 hover:bg-red-600/40 text-red-400 rounded transition-colors font-medium">Xóa</button>
                      </div>
                    </td>
                  </tr>
                );
              })}
              {filteredChapters.length === 0 && (
                <tr><td colSpan="5" className="p-12 text-center text-textSecondary">Không tìm thấy chương nào.</td></tr>
              )}
            </tbody>
          </table>
        </div>
      )}

      {/* Modal Form */}
      {showForm && (
        <div className="fixed inset-0 bg-black/70 z-50 flex items-center justify-center p-4" onClick={(e) => { if (e.target === e.currentTarget) setShowForm(false); }}>
          <div className="bg-surface rounded-2xl border border-gray-700 w-full max-w-2xl shadow-2xl shadow-black/60 max-h-[90vh] overflow-y-auto">
            <div className="p-6 border-b border-gray-800 flex justify-between items-center">
              <h2 className="text-xl font-bold text-white">{editing ? 'Chỉnh sửa chương' : 'Thêm chương mới'}</h2>
              <button onClick={() => setShowForm(false)} className="text-textSecondary hover:text-white text-xl">✕</button>
            </div>
            <form onSubmit={handleSave} className="p-6 space-y-4">
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-textSecondary text-xs mb-1 uppercase">Số chương</label>
                  <input type="number" required value={form.chapterNumber} onChange={(e) => setForm({ ...form, chapterNumber: e.target.value })}
                    className="w-full bg-background border border-gray-700 rounded-lg px-3 py-2 text-textPrimary focus:outline-none focus:border-primary text-sm" />
                </div>
                <div>
                  <label className="block text-textSecondary text-xs mb-1 uppercase">Quyền truy cập</label>
                  <select value={form.accessLevel} onChange={(e) => setForm({ ...form, accessLevel: e.target.value })}
                    className="w-full bg-background border border-gray-700 rounded-lg px-3 py-2 text-textPrimary focus:outline-none focus:border-primary text-sm">
                    {ACCESS_OPTIONS.map((o) => <option key={o.value} value={o.value}>{o.label}</option>)}
                  </select>
                </div>
              </div>
              <div>
                <label className="block text-textSecondary text-xs mb-1 uppercase">Tiêu đề chương *</label>
                <input required value={form.title} onChange={(e) => setForm({ ...form, title: e.target.value })}
                  className="w-full bg-background border border-gray-700 rounded-lg px-3 py-2 text-textPrimary focus:outline-none focus:border-primary text-sm" />
              </div>
              <div>
                <label className="block text-textSecondary text-xs mb-1 uppercase">Nội dung chương</label>
                <textarea rows={10} value={form.content} onChange={(e) => setForm({ ...form, content: e.target.value })}
                  placeholder="Nhập nội dung chương truyện..."
                  className="w-full bg-background border border-gray-700 rounded-lg px-3 py-2 text-textPrimary focus:outline-none focus:border-primary text-sm resize-none font-mono leading-relaxed" />
              </div>
              <div>
                <label className="block text-textSecondary text-xs mb-1 uppercase">Audio</label>

                {/* Hiện audio player nếu chương đã có audio */}
                {editing?.hasAudio && editing?.audioUrl && !audioFile && (
                  <div className="mb-3 p-3 rounded-lg bg-gray-900/60 border border-green-500/30">
                    <p className="text-xs text-green-400 font-semibold mb-2">🎵 Audio hiện tại:</p>
                    <audio
                      controls
                      src={getMediaUrl(editing.audioUrl)}
                      className="w-full h-9"
                      style={{ accentColor: '#f97316' }}
                    />
                  </div>
                )}

                {/* File mới được chọn → preview luôn */}
                {audioFile && (
                  <div className="mb-3 p-3 rounded-lg bg-gray-900/60 border border-blue-500/30">
                    <p className="text-xs text-blue-400 font-semibold mb-2">🎵 File mới đã chọn: {audioFile.name}</p>
                    <audio
                      controls
                      src={URL.createObjectURL(audioFile)}
                      className="w-full h-9"
                      style={{ accentColor: '#f97316' }}
                    />
                  </div>
                )}

                <input type="file" accept="audio/*" onChange={(e) => setAudioFile(e.target.files[0])}
                  className="text-sm text-textSecondary file:mr-3 file:py-1 file:px-3 file:rounded file:border-0 file:text-xs file:bg-primary/20 file:text-primary cursor-pointer" />
                <p className="text-xs text-textSecondary mt-1">Chọn file mới sẽ thay thế audio cũ.</p>
              </div>
              {msg && <p className={`text-sm ${msg.startsWith('✅') ? 'text-green-400' : 'text-red-400'}`}>{msg}</p>}
              <div className="flex justify-end gap-3 pt-2">
                <button type="button" onClick={() => setShowForm(false)} className="px-5 py-2 rounded-lg border border-gray-700 text-textSecondary hover:text-white text-sm transition-colors">Hủy</button>
                <button type="submit" disabled={saving} className="px-5 py-2 rounded-lg bg-primary hover:bg-orange-600 text-white font-bold text-sm transition-colors disabled:opacity-50">
                  {saving ? 'Đang lưu...' : 'Lưu'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default AdminChapters;
