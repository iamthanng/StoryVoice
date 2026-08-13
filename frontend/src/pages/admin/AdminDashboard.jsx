import React, { useEffect, useState } from 'react';
import { getDashboardStats } from '../../services/adminService';

const StatCard = ({ icon, label, value, color }) => (
  <div className={`bg-surface rounded-xl p-6 border ${color} flex items-center gap-5`}>
    <div className="text-4xl">{icon}</div>
    <div>
      <p className="text-textSecondary text-sm mb-1">{label}</p>
      <p className="text-3xl font-extrabold text-white">{value ?? '—'}</p>
    </div>
  </div>
);

const AdminDashboard = () => {
  const [stats, setStats] = useState(null);

  useEffect(() => {
    getDashboardStats()
      .then((r) => setStats(r.data.data))
      .catch(() => {});
  }, []);

  return (
    <div>
      <h1 className="text-3xl font-extrabold text-white mb-8">Dashboard</h1>
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5 mb-10">
        <StatCard icon="📚" label="Tổng truyện" value={stats?.totalStories} color="border-blue-500/30" />
        <StatCard icon="📄" label="Tổng chương" value={stats?.totalChapters} color="border-orange-500/30" />
        <StatCard icon="👥" label="Người dùng" value={stats?.totalUsers} color="border-green-500/30" />
        <StatCard icon="🎵" label="File audio" value={stats?.totalAudioFiles} color="border-purple-500/30" />
      </div>
      <div className="bg-surface rounded-xl p-6 border border-gray-800">
        <p className="text-textSecondary text-sm">
          Chào mừng đến với Trang Quản trị StoryVoice. Chọn mục trong menu bên trái để bắt đầu quản lý nội dung.
        </p>
      </div>
    </div>
  );
};

export default AdminDashboard;
