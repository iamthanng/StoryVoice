import React, { useEffect, useState } from 'react';
import { adminGetGenres, adminCreateGenre, adminUpdateGenre, adminDeleteGenre } from '../../services/adminService';

const emptyForm = { name: '', description: '' };

const AdminGenres = () => {
  const [genres, setGenres] = useState([]);
  const [loading, setLoading] = useState(true);
  
  // Search state
  const [searchKeyword, setSearchKeyword] = useState('');

  // Modal State
  const [showForm, setShowForm] = useState(false);
  const [editing, setEditing] = useState(null);
  const [form, setForm] = useState(emptyForm);
  const [saving, setSaving] = useState(false);
  const [msg, setMsg] = useState('');

  const fetchGenres = () => {
    setLoading(true);
    adminGetGenres()
      .then((r) => setGenres(r.data.data || []))
      .catch(() => setGenres([]))
      .finally(() => setLoading(false));
  };

  useEffect(() => { fetchGenres(); }, []);

  const openCreate = () => {
    setEditing(null);
    setForm(emptyForm);
    setMsg('');
    setShowForm(true);
  };

  const openEdit = (g) => {
    setEditing(g);
    setForm({ name: g.name, description: g.description || '' });
    setMsg('');
    setShowForm(true);
  };

  const handleSave = async (e) => {
    e.preventDefault();
    setSaving(true);
    setMsg('');
    try {
      if (editing) {
        await adminUpdateGenre(editing.id, form);
      } else {
        await adminCreateGenre(form);
      }
      setMsg('✅ Lưu thành công!');
      fetchGenres();
      setTimeout(() => { setShowForm(false); setMsg(''); }, 800);
    } catch (err) {
      setMsg('❌ ' + (err.response?.data?.message || 'Có lỗi xảy ra.'));
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (id, name) => {
    if (!window.confirm(`Xóa thể loại "${name}"?`)) return;
    await adminDeleteGenre(id).catch(() => alert('Không thể xóa. Thể loại này có thể đang có truyện.'));
    fetchGenres();
  };

  const filteredGenres = genres.filter(g => 
    !searchKeyword || g.name.toLowerCase().includes(searchKeyword.toLowerCase())
  );

  return (
    <div>
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 mb-6">
        <h1 className="text-3xl font-extrabold text-white">Quản lý Thể loại</h1>
        <button onClick={openCreate} className="bg-primary hover:bg-orange-600 text-white font-bold py-2.5 px-5 rounded-xl text-sm transition-colors shadow-lg shadow-orange-950/40">
          + Thêm thể loại mới
        </button>
      </div>

      <div className="bg-surface rounded-xl p-4 border border-gray-800 mb-6">
        <div className="relative">
          <span className="absolute inset-y-0 left-0 pl-3 flex items-center text-textSecondary">🔍</span>
          <input
            type="text"
            placeholder="Tìm theo tên thể loại..."
            value={searchKeyword}
            onChange={(e) => setSearchKeyword(e.target.value)}
            className="w-full bg-background border border-gray-700 rounded-lg pl-9 pr-3 py-2 text-textPrimary focus:outline-none focus:border-primary text-sm"
          />
        </div>
      </div>

      {loading ? (
        <div className="text-textSecondary text-center py-10">Đang tải danh sách thể loại...</div>
      ) : (
        <div className="bg-surface rounded-xl border border-gray-800 overflow-hidden shadow-xl">
          <table className="w-full text-sm">
            <thead className="border-b border-gray-800 text-textSecondary text-xs uppercase bg-gray-900/50">
              <tr>
                <th className="text-left p-4 w-16">ID</th>
                <th className="text-left p-4">Tên thể loại</th>
                <th className="text-left p-4 hidden md:table-cell">Mô tả</th>
                <th className="text-right p-4">Hành động</th>
              </tr>
            </thead>
            <tbody>
              {filteredGenres.map((g) => (
                <tr key={g.id} className="border-b border-gray-800/50 hover:bg-gray-800/30 transition-colors">
                  <td className="p-4 text-textSecondary font-mono">{g.id}</td>
                  <td className="p-4 text-white font-medium">{g.name}</td>
                  <td className="p-4 text-textSecondary hidden md:table-cell">{g.description || '—'}</td>
                  <td className="p-4 text-right">
                    <div className="flex gap-2 justify-end">
                      <button onClick={() => openEdit(g)} className="text-xs px-3 py-1.5 bg-blue-600/30 hover:bg-blue-600/50 text-blue-400 rounded transition-colors font-medium">Sửa</button>
                      <button onClick={() => handleDelete(g.id, g.name)} className="text-xs px-3 py-1.5 bg-red-600/20 hover:bg-red-600/40 text-red-400 rounded transition-colors font-medium">Xóa</button>
                    </div>
                  </td>
                </tr>
              ))}
              {filteredGenres.length === 0 && (
                <tr><td colSpan="4" className="p-12 text-center text-textSecondary">Không tìm thấy thể loại nào.</td></tr>
              )}
            </tbody>
          </table>
        </div>
      )}

      {showForm && (
        <div className="fixed inset-0 bg-black/70 z-50 flex items-center justify-center p-4" onClick={(e) => { if (e.target === e.currentTarget) setShowForm(false); }}>
          <div className="bg-surface rounded-2xl border border-gray-700 w-full max-w-md shadow-2xl shadow-black/60 max-h-[90vh] overflow-y-auto">
            <div className="p-6 border-b border-gray-800 flex justify-between items-center">
              <h2 className="text-xl font-bold text-white">{editing ? 'Chỉnh sửa thể loại' : 'Thêm thể loại mới'}</h2>
              <button onClick={() => setShowForm(false)} className="text-textSecondary hover:text-white text-xl">✕</button>
            </div>
            <form onSubmit={handleSave} className="p-6 space-y-4">
              <div>
                <label className="block text-textSecondary text-xs mb-1 uppercase">Tên thể loại *</label>
                <input required value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })}
                  className="w-full bg-background border border-gray-700 rounded-lg px-3 py-2 text-textPrimary focus:outline-none focus:border-primary text-sm" />
              </div>
              <div>
                <label className="block text-textSecondary text-xs mb-1 uppercase">Mô tả</label>
                <textarea rows={4} value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })}
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

export default AdminGenres;
