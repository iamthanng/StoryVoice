import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import api from '../services/api';
import { useTranslation } from 'react-i18next';

const RegisterPage = () => {
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [fieldErrors, setFieldErrors] = useState({});
  const [loading, setLoading] = useState(false);
  
  const { t } = useTranslation();
  const navigate = useNavigate();

  const handleRegister = async (e) => {
    e.preventDefault();
    setError('');
    setFieldErrors({});
    setLoading(true);
    try {
      await api.post('/auth/register', { username, email, password });
      alert('Đăng ký thành công! Vui lòng đăng nhập.');
      navigate('/login');
    } catch (err) {
      if (err.errorCode === 'VALIDATION_FAILED' && err.fieldErrors) {
        setFieldErrors(err.fieldErrors);
      } else {
        setError(err.errorCode ? t(`errors.${err.errorCode}`) : err.message);
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex justify-center items-center h-[75vh]">
      <div className="bg-surface p-8 rounded-xl shadow-lg shadow-black/50 w-full max-w-md border border-gray-800">
        <h2 className="text-3xl font-bold text-center text-primary mb-6">Đăng ký</h2>
        {error && (
          <div className="bg-red-500/10 border border-red-500/50 text-red-500 p-3 rounded mb-4 text-sm">
            {error}
          </div>
        )}
        <form onSubmit={handleRegister} className="space-y-4">
          <div>
            <label className="block text-textSecondary text-sm mb-1">Tên đăng nhập</label>
            <input
              type="text"
              className="w-full bg-background border border-gray-700 rounded-md px-4 py-2 text-textPrimary focus:outline-none focus:border-primary transition-colors"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              placeholder="Ví dụ: nguyenvan_a"
            />
            {fieldErrors.username && (
              <p className="text-red-500 text-xs mt-1">{t(`errors.${fieldErrors.username}`)}</p>
            )}
          </div>
          <div>
            <label className="block text-textSecondary text-sm mb-1">Email</label>
            <input
              type="email"
              className="w-full bg-background border border-gray-700 rounded-md px-4 py-2 text-textPrimary focus:outline-none focus:border-primary transition-colors"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="email@example.com"
            />
            {fieldErrors.email && (
              <p className="text-red-500 text-xs mt-1">{t(`errors.${fieldErrors.email}`)}</p>
            )}
          </div>
          <div>
            <label className="block text-textSecondary text-sm mb-1">Mật khẩu</label>
            <input
              type="password"
              className="w-full bg-background border border-gray-700 rounded-md px-4 py-2 text-textPrimary focus:outline-none focus:border-primary transition-colors"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Ít nhất 6 ký tự"
            />
            {fieldErrors.password && (
              <p className="text-red-500 text-xs mt-1">{t(`errors.${fieldErrors.password}`)}</p>
            )}
          </div>
          <button
            type="submit"
            disabled={loading}
            className="w-full bg-primary hover:bg-orange-600 text-white font-bold py-2 px-4 rounded-md transition-colors disabled:opacity-50 mt-2"
          >
            {loading ? 'Đang xử lý...' : 'Đăng ký tài khoản'}
          </button>
        </form>
        <p className="text-center text-textSecondary text-sm mt-6">
          Đã có tài khoản?{' '}
          <Link to="/login" className="text-primary hover:underline">
            Đăng nhập ngay
          </Link>
        </p>
      </div>
    </div>
  );
};

export default RegisterPage;
