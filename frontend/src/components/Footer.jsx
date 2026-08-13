import React from 'react';

const Footer = () => {
  return (
    <footer className="bg-surface border-t border-gray-800 py-6 mt-auto">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 flex flex-col md:flex-row justify-between items-center">
        <p className="text-textSecondary text-sm">
          &copy; {new Date().getFullYear()} StoryVoice. Nền tảng nghe & đọc truyện AI.
        </p>
        <div className="flex space-x-4 mt-4 md:mt-0 text-sm text-textSecondary">
          <a href="#" className="hover:text-primary transition-colors">Điều khoản</a>
          <a href="#" className="hover:text-primary transition-colors">Bảo mật</a>
          <a href="#" className="hover:text-primary transition-colors">Liên hệ</a>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
