import React, { useState } from 'react';
import { useSearchParams, Link, useNavigate } from 'react-router-dom';
import api from '../services/api';
import { useTranslation } from 'react-i18next';

const ResetPasswordPage = () => {
  const [searchParams] = useSearchParams();
  const token = searchParams.get('token') || '';
  const navigate = useNavigate();

  const [password, setPassword] = useState('');
  const [confirm, setConfirm] = useState('');
  const [loading, setLoading] = useState(false);
  const [msg, setMsg] = useState('');
  const [isError, setIsError] = useState(false);
  const [done, setDone] = useState(false);
  const [fieldErrors, setFieldErrors] = useState({});

  const { t } = useTranslation();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setFieldErrors({});
    if (password !== confirm) {
      setIsError(true);
      setMsg('Mật khẩu xác nhận không khớp.');
      return;
    }
    if (password.length < 6) {
      setIsError(true);
      setMsg('Mật khẩu phải có ít nhất 6 ký tự.');
      return;
    }
    setLoading(true);
    setMsg('');
    try {
      await api.post('/auth/reset-password', { token, newPassword: password });
      setIsError(false);
      setDone(true);
      setMsg('✅ Mật khẩu đã được đặt lại thành công!');
      setTimeout(() => navigate('/login'), 2500);
    } catch (err) {
      setIsError(true);
      if (err.errorCode === 'VALIDATION_FAILED' && err.fieldErrors) {
        setFieldErrors(err.fieldErrors);
        setMsg('Dữ liệu không hợp lệ. Vui lòng kiểm tra lại.');
      } else {
        setMsg(err.errorCode ? t(`errors.${err.errorCode}`) : (err.message || 'Link đặt lại mật khẩu không hợp lệ hoặc đã hết hạn.'));
      }
    } finally {
      setLoading(false);
    }
  };

  if (!token) {
    return (
      <div className="flex justify-center items-center min-h-[70vh] px-4">
        <div className="bg-surface p-8 rounded-2xl border border-gray-800 text-center max-w-md w-full">
          <div className="text-5xl mb-3">⚠️</div>
          <h2 className="text-xl font-bold text-white mb-2">Link không hợp lệ</h2>
          <p className="text-textSecondary text-sm mb-4">Vui lòng yêu cầu link đặt lại mật khẩu mới.</p>
          <Link to="/forgot-password" className="text-primary hover:underline">Quên mật khẩu</Link>
        </div>
      </div>
    );
  }

  return (
    <div className="flex justify-center items-center min-h-[70vh] px-4">
      <div className="bg-surface p-8 rounded-2xl shadow-2xl shadow-black/50 w-full max-w-md border border-gray-800">
        <div className="text-center mb-6">
          <div className="text-5xl mb-3">🔒</div>
          <h2 className="text-2xl font-extrabold text-white">Đặt lại mật khẩu</h2>
          <p className="text-textSecondary text-sm mt-1">Nhập mật khẩu mới cho tài khoản của bạn.</p>
        </div>

        {msg && (
          <div className={`p-3 rounded-lg mb-4 text-sm ${isError ? 'bg-red-500/10 border border-red-500/40 text-red-400' : 'bg-green-500/10 border border-green-500/40 text-green-400'}`}>
            {msg}
          </div>
        )}

        {!done && (
          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-textSecondary text-sm mb-1">Mật khẩu mới</label>
              <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="Tối thiểu 6 ký tự"
                className="w-full bg-background border border-gray-700 rounded-lg px-4 py-2.5 text-textPrimary focus:outline-none focus:border-primary transition-colors"
              />
              {fieldErrors.newPassword && (
                <p className="text-red-500 text-xs mt-1">{t(`errors.${fieldErrors.newPassword}`)}</p>
              )}
            </div>
            <div>
              <label className="block text-textSecondary text-sm mb-1">Xác nhận mật khẩu</label>
              <input
                type="password"
                required
                value={confirm}
                onChange={(e) => setConfirm(e.target.value)}
                placeholder="Nhập lại mật khẩu mới"
                className="w-full bg-background border border-gray-700 rounded-lg px-4 py-2.5 text-textPrimary focus:outline-none focus:border-primary transition-colors"
              />
            </div>
            <button
              type="submit"
              disabled={loading}
              className="w-full bg-primary hover:bg-orange-600 text-white font-bold py-2.5 px-4 rounded-lg transition-colors disabled:opacity-50"
            >
              {loading ? 'Đang lưu...' : 'Đặt lại mật khẩu'}
            </button>
          </form>
        )}

        {done && (
          <p className="text-center text-textSecondary text-sm mt-2">Đang chuyển về trang đăng nhập...</p>
        )}

        <p className="text-center text-textSecondary text-sm mt-6">
          <Link to="/login" className="text-primary hover:underline">← Quay lại đăng nhập</Link>
        </p>
      </div>
    </div>
  );
};

export default ResetPasswordPage;
