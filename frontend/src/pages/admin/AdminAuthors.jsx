import React, { useEffect, useState } from 'react';
import { adminGetAuthors, adminCreateAuthor, adminUpdateAuthor, adminDeleteAuthor } from '../../services/adminService';
import { useTranslation } from 'react-i18next';

const emptyForm = { name: '', bio: '' };

const AdminAuthors = () => {
  const [authors, setAuthors] = useState([]);
  const [loading, setLoading] = useState(true);
  
  // Search state
  const [searchKeyword, setSearchKeyword] = useState('');

  // Modal State
  const [showForm, setShowForm] = useState(false);
  const [editing, setEditing] = useState(null);
  const [form, setForm] = useState(emptyForm);
  const [saving, setSaving] = useState(false);
  const [msg, setMsg] = useState('');
  const [fieldErrors, setFieldErrors] = useState({});

  const { t } = useTranslation();

  const fetchAuthors = () => {
    setLoading(true);
    adminGetAuthors()
      .then((r) => setAuthors(r.data.data || []))
      .catch(() => setAuthors([]))
      .finally(() => setLoading(false));
  };

  useEffect(() => { fetchAuthors(); }, []);

  const openCreate = () => {
    setEditing(null);
    setForm(emptyForm);
    setMsg('');
    setFieldErrors({});
    setShowForm(true);
  };

  const openEdit = (a) => {
    setEditing(a);
    setForm({ name: a.name, bio: a.bio || '' });
    setMsg('');
    setFieldErrors({});
    setShowForm(true);
  };

  const handleSave = async (e) => {
    e.preventDefault();
    setSaving(true);
    setMsg('');
    setFieldErrors({});
    try {
      if (editing) {
        await adminUpdateAuthor(editing.id, form);
      } else {
        await adminCreateAuthor(form);
      }
      setMsg('✅ Lưu thành công!');
      fetchAuthors();
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

  const handleDelete = async (id, name) => {
    if (!window.confirm(`Xóa tác giả "${name}"?`)) return;
    await adminDeleteAuthor(id).catch(() => alert('Không thể xóa. Tác giả này có thể đang có truyện.'));
    fetchAuthors();
  };

  const filteredAuthors = authors.filter(a => 
    !searchKeyword || a.name.toLowerCase().includes(searchKeyword.toLowerCase())
  );

  return (
    <div>
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 mb-6">
        <h1 className="text-3xl font-extrabold text-white">Quản lý Tác giả</h1>
        <button onClick={openCreate} className="bg-primary hover:bg-orange-600 text-white font-bold py-2.5 px-5 rounded-xl text-sm transition-colors shadow-lg shadow-orange-950/40">
          + Thêm tác giả mới
        </button>
      </div>

      <div className="bg-surface rounded-xl p-4 border border-gray-800 mb-6">
        <div className="relative">
          <span className="absolute inset-y-0 left-0 pl-3 flex items-center text-textSecondary">🔍</span>
          <input
            type="text"
            placeholder="Tìm theo tên tác giả..."
            value={searchKeyword}
            onChange={(e) => setSearchKeyword(e.target.value)}
            className="w-full bg-background border border-gray-700 rounded-lg pl-9 pr-3 py-2 text-textPrimary focus:outline-none focus:border-primary text-sm"
          />
        </div>
      </div>

      {loading ? (
        <div className="text-textSecondary text-center py-10">Đang tải danh sách tác giả...</div>
      ) : (
        <div className="bg-surface rounded-xl border border-gray-800 overflow-hidden shadow-xl">
          <table className="w-full text-sm">
            <thead className="border-b border-gray-800 text-textSecondary text-xs uppercase bg-gray-900/50">
              <tr>
                <th className="text-left p-4 w-16">ID</th>
                <th className="text-left p-4">Tên tác giả</th>
                <th className="text-left p-4 hidden md:table-cell">Tiểu sử</th>
                <th className="text-right p-4">Hành động</th>
              </tr>
            </thead>
            <tbody>
              {filteredAuthors.map((a) => (
                <tr key={a.id} className="border-b border-gray-800/50 hover:bg-gray-800/30 transition-colors">
                  <td className="p-4 text-textSecondary font-mono">{a.id}</td>
                  <td className="p-4 text-white font-medium">{a.name}</td>
                  <td className="p-4 text-textSecondary hidden md:table-cell">{a.bio || '—'}</td>
                  <td className="p-4 text-right">
                    <div className="flex gap-2 justify-end">
                      <button onClick={() => openEdit(a)} className="text-xs px-3 py-1.5 bg-blue-600/30 hover:bg-blue-600/50 text-blue-400 rounded transition-colors font-medium">Sửa</button>
                      <button onClick={() => handleDelete(a.id, a.name)} className="text-xs px-3 py-1.5 bg-red-600/20 hover:bg-red-600/40 text-red-400 rounded transition-colors font-medium">Xóa</button>
                    </div>
                  </td>
                </tr>
              ))}
              {filteredAuthors.length === 0 && (
                <tr><td colSpan="4" className="p-12 text-center text-textSecondary">Không tìm thấy tác giả nào.</td></tr>
              )}
            </tbody>
          </table>
        </div>
      )}

      {showForm && (
        <div className="fixed inset-0 bg-black/70 z-50 flex items-center justify-center p-4" onClick={(e) => { if (e.target === e.currentTarget) setShowForm(false); }}>
          <div className="bg-surface rounded-2xl border border-gray-700 w-full max-w-md shadow-2xl shadow-black/60 max-h-[90vh] overflow-y-auto">
            <div className="p-6 border-b border-gray-800 flex justify-between items-center">
              <h2 className="text-xl font-bold text-white">{editing ? 'Chỉnh sửa tác giả' : 'Thêm tác giả mới'}</h2>
              <button onClick={() => setShowForm(false)} className="text-textSecondary hover:text-white text-xl">✕</button>
            </div>
            <form onSubmit={handleSave} className="p-6 space-y-4">
              <div>
                <label className="block text-textSecondary text-xs mb-1 uppercase">Tên tác giả *</label>
                <input value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })}
                  className="w-full bg-background border border-gray-700 rounded-lg px-3 py-2 text-textPrimary focus:outline-none focus:border-primary text-sm" />
                {fieldErrors.name && <p className="text-red-500 text-xs mt-1">{t(`errors.${fieldErrors.name}`)}</p>}
              </div>
              <div>
                <label className="block text-textSecondary text-xs mb-1 uppercase">Tiểu sử</label>
                <textarea rows={4} value={form.bio} onChange={(e) => setForm({ ...form, bio: e.target.value })}
                  className="w-full bg-background border border-gray-700 rounded-lg px-3 py-2 text-textPrimary focus:outline-none focus:border-primary text-sm resize-none" />
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

export default AdminAuthors;
