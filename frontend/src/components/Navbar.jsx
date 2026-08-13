import React, { useContext } from 'react';
import { Link } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';

const Navbar = () => {
  const { user, logout } = useContext(AuthContext);

  return (
    <nav className="sticky top-0 z-50 w-full backdrop-blur-md bg-surface/80 border-b border-gray-800">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          <div className="flex-shrink-0">
            <Link to="/" className="text-2xl font-bold text-primary">
              StoryVoice
            </Link>
          </div>
          <div className="hidden md:block">
            <div className="ml-10 flex items-baseline space-x-4">
              <Link to="/" className="text-textPrimary hover:text-primary px-3 py-2 rounded-md font-medium transition-colors">
                Trang chủ
              </Link>
              <Link to="/browse" className="text-textPrimary hover:text-primary px-3 py-2 rounded-md font-medium transition-colors">
                Thư viện
              </Link>
              {(user?.role === 'ROLE_ADMIN' || user?.role === 'ADMIN') && (
                <Link to="/admin" className="text-textPrimary hover:text-primary px-3 py-2 rounded-md font-medium transition-colors">
                  Quản trị
                </Link>
              )}
            </div>
          </div>
          <div className="hidden md:block">
            <div className="ml-4 flex items-center md:ml-6">
              {user ? (
                <div className="flex items-center space-x-4">
                  <span className="text-sm text-textSecondary">
                    Chào, <span className="text-white font-semibold">{user.username}</span>
                  </span>
                  {(user?.isVip || user?.vip || user?.is_vip) && (
                    <span className="px-2 py-1 text-xs font-bold bg-yellow-500/20 text-yellow-500 rounded border border-yellow-500/50">
                      VIP
                    </span>
                  )}
                  <button
                    onClick={logout}
                    className="bg-red-500/10 hover:bg-red-500/20 text-red-500 px-4 py-2 rounded-md text-sm font-medium transition-colors"
                  >
                    Đăng xuất
                  </button>
                </div>
              ) : (
                <div className="space-x-2">
                  <Link
                    to="/login"
                    className="text-textPrimary hover:text-white px-4 py-2 rounded-md text-sm font-medium transition-colors"
                  >
                    Đăng nhập
                  </Link>
                  <Link
                    to="/register"
                    className="bg-primary hover:bg-orange-600 text-white px-4 py-2 rounded-md text-sm font-medium transition-colors"
                  >
                    Đăng ký
                  </Link>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
