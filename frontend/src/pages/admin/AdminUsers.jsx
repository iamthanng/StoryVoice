import React, { useEffect, useState } from 'react';
import { adminGetUsers, adminSetVip } from '../../services/adminService';

const AdminUsers = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchUsers = () => {
    setLoading(true);
    adminGetUsers()
      .then((r) => setUsers(r.data.data?.content || r.data.data || []))
      .finally(() => setLoading(false));
  };

  useEffect(() => { fetchUsers(); }, []);

  const toggleVip = async (user) => {
    const newVip = !user.vip;
    const label = newVip ? 'cấp VIP' : 'thu hồi VIP';
    if (!window.confirm(`Xác nhận ${label} cho "${user.username}"?`)) return;
    try {
      await adminSetVip(user.id, newVip);
      fetchUsers();
    } catch (err) {
      alert(err.response?.data?.message || 'Có lỗi xảy ra.');
    }
  };

  return (
    <div>
      <h1 className="text-3xl font-extrabold text-white mb-6">Quản lý Người dùng</h1>
      {loading ? (
        <div className="text-textSecondary">Đang tải...</div>
      ) : (
        <div className="bg-surface rounded-xl border border-gray-800 overflow-hidden">
          <table className="w-full text-sm">
            <thead className="border-b border-gray-800 text-textSecondary text-xs uppercase tracking-wide">
              <tr>
                <th className="text-left p-4 w-12">#</th>
                <th className="text-left p-4">Tên đăng nhập</th>
                <th className="text-left p-4 hidden md:table-cell">Email</th>
                <th className="text-left p-4">Vai trò</th>
                <th className="text-left p-4">VIP</th>
                <th className="text-right p-4">Hành động</th>
              </tr>
            </thead>
            <tbody>
              {users.map((u, i) => (
                <tr key={u.id} className="border-b border-gray-800/50 hover:bg-gray-800/30 transition-colors">
                  <td className="p-4 text-textSecondary">{i + 1}</td>
                  <td className="p-4 font-medium text-white">{u.username}</td>
                  <td className="p-4 text-textSecondary hidden md:table-cell">{u.email}</td>
                  <td className="p-4">
                    <span className={`text-xs font-bold px-2 py-1 rounded ${u.role === 'ADMIN' ? 'bg-red-500/20 text-red-400' : 'bg-gray-700 text-textSecondary'}`}>
                      {u.role}
                    </span>
                  </td>
                  <td className="p-4">
                    {u.vip ? (
                      <span className="text-xs font-bold px-2 py-1 rounded bg-yellow-500/20 text-yellow-400 border border-yellow-500/40">VIP ✓</span>
                    ) : (
                      <span className="text-xs text-textSecondary">—</span>
                    )}
                  </td>
                  <td className="p-4 text-right">
                    {u.role !== 'ADMIN' && (
                      <button
                        onClick={() => toggleVip(u)}
                        className={`text-xs px-3 py-1.5 rounded transition-colors ${
                          u.vip
                            ? 'bg-red-600/20 hover:bg-red-600/40 text-red-400'
                            : 'bg-yellow-500/20 hover:bg-yellow-500/40 text-yellow-400'
                        }`}
                      >
                        {u.vip ? 'Thu hồi VIP' : 'Cấp VIP'}
                      </button>
                    )}
                  </td>
                </tr>
              ))}
              {users.length === 0 && (
                <tr><td colSpan="6" className="p-8 text-center text-textSecondary">Không có người dùng nào.</td></tr>
              )}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
};

export default AdminUsers;
