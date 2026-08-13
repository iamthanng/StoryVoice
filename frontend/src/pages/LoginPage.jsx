import React, { useState, useContext } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import api from '../services/api';
import { AuthContext } from '../context/AuthContext';

const LoginPage = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  
  const { login } = useContext(AuthContext);
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const response = await api.post('/auth/login', { username, password });
      const { accessToken, user: userData } = response.data.data;
      
      login(accessToken, userData);
      
      navigate('/');
    } catch (err) {
      setError(err.response?.data?.message || 'Đăng nhập thất bại. Vui lòng kiểm tra lại.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex justify-center items-center h-[70vh]">
      <div className="bg-surface p-8 rounded-xl shadow-lg shadow-black/50 w-full max-w-md border border-gray-800">
        <h2 className="text-3xl font-bold text-center text-primary mb-6">Đăng nhập</h2>
        {error && (
          <div className="bg-red-500/10 border border-red-500/50 text-red-500 p-3 rounded mb-4 text-sm">
            {error}
          </div>
        )}
        <form onSubmit={handleLogin} className="space-y-4">
          <div>
            <label className="block text-textSecondary text-sm mb-1">Tên đăng nhập</label>
            <input
              type="text"
              required
              className="w-full bg-background border border-gray-700 rounded-md px-4 py-2 text-textPrimary focus:outline-none focus:border-primary transition-colors"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              placeholder="Nhập tên tài khoản"
            />
          </div>
          <div>
            <label className="block text-textSecondary text-sm mb-1">Mật khẩu</label>
            <input
              type="password"
              required
              className="w-full bg-background border border-gray-700 rounded-md px-4 py-2 text-textPrimary focus:outline-none focus:border-primary transition-colors"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="••••••••"
            />
          </div>
          <button
            type="submit"
            disabled={loading}
            className="w-full bg-primary hover:bg-orange-600 text-white font-bold py-2 px-4 rounded-md transition-colors disabled:opacity-50"
          >
            {loading ? 'Đang xử lý...' : 'Đăng nhập'}
          </button>
        </form>
        <p className="text-center text-textSecondary text-sm mt-6">
          Chưa có tài khoản?{' '}
          <Link to="/register" className="text-primary hover:underline">
            Đăng ký ngay
          </Link>
        </p>
      </div>
    </div>
  );
};

export default LoginPage;
