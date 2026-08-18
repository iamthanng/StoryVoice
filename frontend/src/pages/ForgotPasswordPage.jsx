import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import api from '../services/api';
import { useTranslation } from 'react-i18next';

const ForgotPasswordPage = () => {
  const [email, setEmail] = useState('');
  const [loading, setLoading] = useState(false);
  const [msg, setMsg] = useState('');
  const [isError, setIsError] = useState(false);
  const [fieldErrors, setFieldErrors] = useState({});

  const { t } = useTranslation();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setMsg('');
    setFieldErrors({});
    try {
      await api.post('/auth/forgot-password', { email });
      setIsError(false);
      setMsg('✅ Email đặt lại mật khẩu đã được gửi! Vui lòng kiểm tra hộp thư (kể cả Spam).');
    } catch (err) {
      setIsError(true);
      if (err.errorCode === 'VALIDATION_FAILED' && err.fieldErrors) {
        setFieldErrors(err.fieldErrors);
        setMsg('Dữ liệu không hợp lệ. Vui lòng kiểm tra lại.');
      } else {
        setMsg(err.errorCode ? t(`errors.${err.errorCode}`) : err.message);
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex justify-center items-center min-h-[70vh] px-4">
      <div className="bg-surface p-8 rounded-2xl shadow-2xl shadow-black/50 w-full max-w-md border border-gray-800">
        <div className="text-center mb-6">
          <div className="text-5xl mb-3">🔑</div>
          <h2 className="text-2xl font-extrabold text-white">Quên mật khẩu?</h2>
          <p className="text-textSecondary text-sm mt-1">
            Nhập email của bạn, chúng tôi sẽ gửi link đặt lại mật khẩu.
          </p>
        </div>

        {msg && (
          <div className={`p-3 rounded-lg mb-4 text-sm ${isError ? 'bg-red-500/10 border border-red-500/40 text-red-400' : 'bg-green-500/10 border border-green-500/40 text-green-400'}`}>
            {msg}
          </div>
        )}

        {!msg || isError ? (
          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-textSecondary text-sm mb-1">Địa chỉ Email</label>
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="example@gmail.com"
                className="w-full bg-background border border-gray-700 rounded-lg px-4 py-2.5 text-textPrimary focus:outline-none focus:border-primary transition-colors"
              />
              {fieldErrors.email && (
                <p className="text-red-500 text-xs mt-1">{t(`errors.${fieldErrors.email}`)}</p>
              )}
            </div>
            <button
              type="submit"
              disabled={loading}
              className="w-full bg-primary hover:bg-orange-600 text-white font-bold py-2.5 px-4 rounded-lg transition-colors disabled:opacity-50"
            >
              {loading ? 'Đang gửi...' : 'Gửi link đặt lại mật khẩu'}
            </button>
          </form>
        ) : (
          <button
            onClick={() => { setMsg(''); setEmail(''); }}
            className="w-full bg-gray-700 hover:bg-gray-600 text-white py-2.5 px-4 rounded-lg transition-colors text-sm"
          >
            Gửi lại email khác
          </button>
        )}

        <p className="text-center text-textSecondary text-sm mt-6">
          <Link to="/login" className="text-primary hover:underline">← Quay lại đăng nhập</Link>
        </p>
      </div>
    </div>
  );
};

export default ForgotPasswordPage;
