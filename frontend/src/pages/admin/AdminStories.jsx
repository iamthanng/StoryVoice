import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import {
  adminGetStories, adminCreateStory, adminUpdateStory,
  adminDeleteStory, adminUploadCover, getGenres, getAuthors,
} from '../../services/adminService';
import { getMediaUrl } from '../../utils/urlHelper';
import { useTranslation } from 'react-i18next';

const emptyForm = { title: '', description: '', authorId: '', genreId: '', status: 'ONGOING' };

const AdminStories = () => {
  const [stories, setStories] = useState([]);
  const [genres, setGenres] = useState([]);
  const [authors, setAuthors] = useState([]);
  const [loading, setLoading] = useState(true);

  // Search & Filter State
  const [keyword, setKeyword] = useState('');
  const [selectedGenre, setSelectedGenre] = useState('');
  const [selectedAuthor, setSelectedAuthor] = useState('');

  // Modal State
  const [showForm, setShowForm] = useState(false);
  const [editing, setEditing] = useState(null);
  const [form, setForm] = useState(emptyForm);
  const [coverFile, setCoverFile] = useState(null);
  const [saving, setSaving] = useState(false);
  const [msg, setMsg] = useState('');
  const [fieldErrors, setFieldErrors] = useState({});

  const { t } = useTranslation();

  const fetchStories = (searchParams = {}) => {
    setLoading(true);
    const params = {
      keyword: searchParams.keyword ?? keyword,
      genreId: searchParams.genreId ?? selectedGenre,
      authorId: searchParams.authorId ?? selectedAuthor,
    };

    // Remove empty params
    Object.keys(params).forEach((key) => {
      if (!params[key]) delete params[key];
    });

    adminGetStories(0, 100, params)
      .then((r) => setStories(r.data.data?.content || r.data.data || []))
      .catch(() => setStories([]))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    fetchStories();
    getGenres().then((r) => setGenres(r.data.data || [])).catch(() => {});
    getAuthors().then((r) => setAuthors(r.data.data || [])).catch(() => {});
  }, []);

  const handleSearchSubmit = (e) => {
    e.preventDefault();
    fetchStories();
  };

  const handleClearFilters = () => {
    setKeyword('');
    setSelectedGenre('');
    setSelectedAuthor('');
    fetchStories({ keyword: '', genreId: '', authorId: '' });
  };

  const openCreate = () => { setEditing(null); setForm(emptyForm); setCoverFile(null); setMsg(''); setFieldErrors({}); setShowForm(true); };
  const openEdit = (s) => {
    setEditing(s);
    setForm({ title: s.title, description: s.description || '', authorId: s.authorId || '', genreId: s.genreId || '', status: s.status });
    setCoverFile(null);
    setMsg('');
    setFieldErrors({});
    setShowForm(true);
  };
  const closeForm = () => setShowForm(false);

  const handleSave = async (e) => {
    e.preventDefault();
    setSaving(true);
    setMsg('');
    setFieldErrors({});
    try {
      let id = editing?.id;
      if (editing) {
        await adminUpdateStory(id, form);
      } else {
        const res = await adminCreateStory(form);
        id = res.data.data?.id;
      }
      if (coverFile && id) {
        await adminUploadCover(id, coverFile);
      }
      setMsg('✅ Lưu thành công!');
      fetchStories();
      setTimeout(() => { setShowForm(false); setMsg(''); setFieldErrors({}); }, 800);
    } catch (err) {
      if (err.errorCode === 'VALIDATION_FAILED' && err.fieldErrors) {
        setFieldErrors(err.fieldErrors);
        setMsg('❌ Dữ liệu không hợp lệ. Vui lòng kiểm tra lại.');
      } else {
        setMsg('❌ ' + (err.errorCode ? t(`errors.${err.errorCode}`) : err.message));
      }
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (id, title) => {
    if (!window.confirm(`Xóa truyện "${title}"? Hành động này không thể hoàn tác.`)) return;
    await adminDeleteStory(id).catch(() => {});
    fetchStories();
  };

  return (
    <div>
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 mb-6">
        <h1 className="text-3xl font-extrabold text-white">Quản lý Truyện</h1>
        <button onClick={openCreate} className="bg-primary hover:bg-orange-600 text-white font-bold py-2.5 px-5 rounded-xl transition-colors text-sm shadow-lg shadow-orange-950/40">
          + Thêm truyện mới
        </button>
      </div>

      {/* Filter & Search Bar */}
      <form onSubmit={handleSearchSubmit} className="bg-surface rounded-xl p-4 border border-gray-800 mb-6 flex flex-col lg:flex-row gap-3 items-stretch lg:items-center justify-between">
        <div className="flex-1 flex flex-col sm:flex-row gap-3">
          <div className="relative flex-1">
            <span className="absolute inset-y-0 left-0 pl-3 flex items-center text-textSecondary">🔍</span>
            <input
              type="text"
              placeholder="Tìm kiếm theo tên truyện..."
              value={keyword}
              onChange={(e) => setKeyword(e.target.value)}
              className="w-full bg-background border border-gray-700 rounded-lg pl-9 pr-3 py-2 text-textPrimary focus:outline-none focus:border-primary text-sm"
            />
          </div>
          <select
            value={selectedGenre}
            onChange={(e) => setSelectedGenre(e.target.value)}
            className="bg-background border border-gray-700 rounded-lg px-3 py-2 text-textPrimary focus:outline-none focus:border-primary text-sm"
          >
            <option value="">-- Tất cả Thể loại --</option>
            {genres.map((g) => <option key={g.id} value={g.id}>{g.name}</option>)}
          </select>
          <select
            value={selectedAuthor}
            onChange={(e) => setSelectedAuthor(e.target.value)}
            className="bg-background border border-gray-700 rounded-lg px-3 py-2 text-textPrimary focus:outline-none focus:border-primary text-sm"
          >
            <option value="">-- Tất cả Tác giả --</option>
            {authors.map((a) => <option key={a.id} value={a.id}>{a.name}</option>)}
          </select>
        </div>
        <div className="flex gap-2">
          <button type="submit" className="bg-primary hover:bg-orange-600 text-white font-semibold px-4 py-2 rounded-lg text-sm transition-colors">
            Tìm kiếm
          </button>
          {(keyword || selectedGenre || selectedAuthor) && (
            <button type="button" onClick={handleClearFilters} className="bg-gray-800 hover:bg-gray-700 text-textSecondary hover:text-white px-3 py-2 rounded-lg text-sm transition-colors">
              Xóa bộ lọc
            </button>
          )}
        </div>
      </form>

      {/* Story Table */}
      {loading ? (
        <div className="text-textSecondary text-center py-10">Đang tải danh sách truyện...</div>
      ) : (
        <div className="bg-surface rounded-xl border border-gray-800 overflow-hidden shadow-xl">
          <table className="w-full text-sm">
            <thead className="border-b border-gray-800 text-textSecondary text-xs uppercase tracking-wide bg-gray-900/50">
              <tr>
                <th className="text-left p-4 w-12">#</th>
                <th className="text-left p-4">Tên truyện</th>
                <th className="text-left p-4 hidden md:table-cell">Tác giả</th>
                <th className="text-left p-4 hidden lg:table-cell">Thể loại</th>
                <th className="text-left p-4">Trạng thái</th>
                <th className="text-right p-4">Hành động</th>
              </tr>
            </thead>
            <tbody>
              {stories.map((s, i) => (
                <tr key={s.id} className="border-b border-gray-800/50 hover:bg-gray-800/30 transition-colors">
                  <td className="p-4 text-textSecondary font-mono">{i + 1}</td>
                  <td className="p-4">
                    <div className="flex items-center gap-3">
                      {s.coverImage ? (
                        <img src={getMediaUrl(s.coverImage)} alt="" className="w-10 h-12 rounded object-cover flex-shrink-0 shadow" />
                      ) : (
                        <div className="w-10 h-12 rounded bg-gray-800 flex items-center justify-center text-[10px] text-textSecondary">No Cover</div>
                      )}
                      <div>
                        <p className="text-white font-medium line-clamp-1">{s.title}</p>
                        <p className="text-xs text-textSecondary md:hidden">{s.authorName || 'Chưa rõ'}</p>
                      </div>
                    </div>
                  </td>
                  <td className="p-4 text-textSecondary hidden md:table-cell">{s.authorName || '—'}</td>
                  <td className="p-4 text-textSecondary hidden lg:table-cell">{s.genreName || '—'}</td>
                  <td className="p-4">
                    <span className={`text-xs font-bold px-2.5 py-1 rounded-full ${s.status === 'COMPLETED' ? 'bg-green-500/20 text-green-400 border border-green-500/30' : 'bg-blue-500/20 text-blue-400 border border-blue-500/30'}`}>
                      {s.status === 'COMPLETED' ? 'Hoàn thành' : 'Đang ra'}
                    </span>
                  </td>
                  <td className="p-4 text-right">
                    <div className="flex gap-2 justify-end">
                      <Link to={`/admin/stories/${s.id}/chapters`} className="text-xs px-3 py-1.5 bg-gray-700 hover:bg-gray-600 rounded transition-colors text-white font-medium">
                        Chương
                      </Link>
                      <button onClick={() => openEdit(s)} className="text-xs px-3 py-1.5 bg-blue-600/30 hover:bg-blue-600/50 text-blue-400 rounded transition-colors font-medium">
                        Sửa
                      </button>
                      <button onClick={() => handleDelete(s.id, s.title)} className="text-xs px-3 py-1.5 bg-red-600/20 hover:bg-red-600/40 text-red-400 rounded transition-colors font-medium">
                        Xóa
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
              {stories.length === 0 && (
                <tr><td colSpan="6" className="p-12 text-center text-textSecondary">Không tìm thấy truyện nào.</td></tr>
              )}
            </tbody>
          </table>
        </div>
      )}

      {/* Modal Form */}
      {showForm && (
        <div className="fixed inset-0 bg-black/70 z-50 flex items-center justify-center p-4" onClick={(e) => { if (e.target === e.currentTarget) closeForm(); }}>
          <div className="bg-surface rounded-2xl border border-gray-700 w-full max-w-lg shadow-2xl shadow-black/60 max-h-[90vh] overflow-y-auto">
            <div className="p-6 border-b border-gray-800 flex justify-between items-center">
              <h2 className="text-xl font-bold text-white">{editing ? 'Chỉnh sửa truyện' : 'Thêm truyện mới'}</h2>
              <button onClick={closeForm} className="text-textSecondary hover:text-white text-xl">✕</button>
            </div>
            <form onSubmit={handleSave} className="p-6 space-y-4">
              <div>
                <label className="block text-textSecondary text-xs mb-1 uppercase tracking-wide">Tên truyện *</label>
                <input value={form.title} onChange={(e) => setForm({ ...form, title: e.target.value })}
                  className="w-full bg-background border border-gray-700 rounded-lg px-3 py-2 text-textPrimary focus:outline-none focus:border-primary text-sm" />
                {fieldErrors.title && <p className="text-red-500 text-xs mt-1">{t(`errors.${fieldErrors.title}`)}</p>}
              </div>
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-textSecondary text-xs mb-1 uppercase tracking-wide">Tác giả</label>
                  <select value={form.authorId} onChange={(e) => setForm({ ...form, authorId: e.target.value })}
                    className="w-full bg-background border border-gray-700 rounded-lg px-3 py-2 text-textPrimary focus:outline-none focus:border-primary text-sm">
                    <option value="">-- Chọn tác giả --</option>
                    {authors.map((a) => <option key={a.id} value={a.id}>{a.name}</option>)}
                  </select>
                  {fieldErrors.authorId && <p className="text-red-500 text-xs mt-1">{t(`errors.${fieldErrors.authorId}`)}</p>}
                </div>
                <div>
                  <label className="block text-textSecondary text-xs mb-1 uppercase tracking-wide">Thể loại</label>
                  <select value={form.genreId} onChange={(e) => setForm({ ...form, genreId: e.target.value })}
                    className="w-full bg-background border border-gray-700 rounded-lg px-3 py-2 text-textPrimary focus:outline-none focus:border-primary text-sm">
                    <option value="">-- Chọn thể loại --</option>
                    {genres.map((g) => <option key={g.id} value={g.id}>{g.name}</option>)}
                  </select>
                  {fieldErrors.genreId && <p className="text-red-500 text-xs mt-1">{t(`errors.${fieldErrors.genreId}`)}</p>}
                </div>
              </div>
              <div>
                <label className="block text-textSecondary text-xs mb-1 uppercase tracking-wide">Trạng thái</label>
                <select value={form.status} onChange={(e) => setForm({ ...form, status: e.target.value })}
                  className="w-full bg-background border border-gray-700 rounded-lg px-3 py-2 text-textPrimary focus:outline-none focus:border-primary text-sm">
                  <option value="ONGOING">Đang ra</option>
                  <option value="COMPLETED">Hoàn thành</option>
                </select>
              </div>
              <div>
                <label className="block text-textSecondary text-xs mb-1 uppercase tracking-wide">Mô tả</label>
                <textarea rows={3} value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })}
                  className="w-full bg-background border border-gray-700 rounded-lg px-3 py-2 text-textPrimary focus:outline-none focus:border-primary text-sm resize-none" />
                {fieldErrors.description && <p className="text-red-500 text-xs mt-1">{t(`errors.${fieldErrors.description}`)}</p>}
              </div>
              <div>
                <label className="block text-textSecondary text-xs mb-1 uppercase tracking-wide">Ảnh bìa</label>

                <div className="flex gap-4 items-start mb-3">
                  {/* Ảnh bìa hiện tại */}
                  {editing?.coverImage && !coverFile && (
                    <div className="flex flex-col items-center gap-1">
                      <p className="text-xs text-textSecondary">Hiện tại:</p>
                      <img
                        src={getMediaUrl(editing.coverImage)}
                        alt="Ảnh bìa hiện tại"
                        className="w-20 h-28 object-cover rounded-lg border border-gray-700 shadow"
                      />
                    </div>
                  )}
                  {/* Preview file mới được chọn */}
                  {coverFile && (
                    <div className="flex flex-col items-center gap-1">
                      <p className="text-xs text-blue-400">Ảnh mới:</p>
                      <img
                        src={URL.createObjectURL(coverFile)}
                        alt="Ảnh bìa mới"
                        className="w-20 h-28 object-cover rounded-lg border border-blue-500/50 shadow"
                      />
                    </div>
                  )}
                </div>

                <input type="file" accept="image/*" onChange={(e) => setCoverFile(e.target.files[0])}
                  className="text-sm text-textSecondary file:mr-3 file:py-1 file:px-3 file:rounded file:border-0 file:text-xs file:bg-primary/20 file:text-primary cursor-pointer" />
                {editing?.coverImage && <p className="text-xs text-textSecondary mt-1">Chọn file mới sẽ thay thế ảnh bìa cũ.</p>}
              </div>
              {msg && <p className={`text-sm ${msg.startsWith('✅') ? 'text-green-400' : 'text-red-400'}`}>{msg}</p>}
              <div className="flex justify-end gap-3 pt-2">
                <button type="button" onClick={closeForm} className="px-5 py-2 rounded-lg border border-gray-700 text-textSecondary hover:text-white text-sm transition-colors">Hủy</button>
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

export default AdminStories;
