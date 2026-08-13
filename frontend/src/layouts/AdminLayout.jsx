import React from 'react';
import { Outlet, NavLink, useNavigate } from 'react-router-dom';

const AdminLayout = () => {
  const navigate = useNavigate();
  const navItems = [
    { to: '/admin', label: '📊 Dashboard', end: true },
    { to: '/admin/stories', label: '📚 Quản lý Truyện' },
    { to: '/admin/users', label: '👥 Quản lý Người dùng' },
  ];

  return (
    <div className="min-h-screen flex bg-background text-textPrimary font-sans">
      {/* Sidebar */}
      <aside className="w-56 flex-shrink-0 bg-surface border-r border-gray-800 flex flex-col">
        <div className="p-5 border-b border-gray-800">
          <button onClick={() => navigate('/')} className="text-xl font-extrabold text-primary hover:opacity-80 transition-opacity">
            StoryVoice
          </button>
          <p className="text-xs text-textSecondary mt-1">Admin Panel</p>
        </div>
        <nav className="flex-1 p-3 space-y-1">
          {navItems.map(({ to, label, end }) => (
            <NavLink
              key={to}
              to={to}
              end={end}
              className={({ isActive }) =>
                `flex items-center gap-2 px-3 py-2 rounded-lg text-sm font-medium transition-colors ${
                  isActive
                    ? 'bg-primary/20 text-primary'
                    : 'text-textSecondary hover:bg-gray-800 hover:text-white'
                }`
              }
            >
              {label}
            </NavLink>
          ))}
        </nav>
        <div className="p-3 border-t border-gray-800">
          <button
            onClick={() => { localStorage.clear(); navigate('/login'); }}
            className="w-full text-left text-sm text-red-400 hover:text-red-300 px-3 py-2 rounded-lg hover:bg-red-500/10 transition-colors"
          >
            🚪 Đăng xuất
          </button>
        </div>
      </aside>

      {/* Main content */}
      <main className="flex-1 overflow-auto">
        <div className="p-8">
          <Outlet />
        </div>
      </main>
    </div>
  );
};

export default AdminLayout;
