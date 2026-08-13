import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { getAllStories } from '../services/storyService';
import { getMediaUrl } from '../utils/urlHelper';

const HomePage = () => {
  const [stories, setStories] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchStories = async () => {
      try {
        const response = await getAllStories(0, 10);
        setStories(response.data.data?.content || []);
      } catch (error) {
        console.error("Lỗi khi lấy danh sách truyện", error);
      } finally {
        setLoading(false);
      }
    };

    fetchStories();
  }, []);

  return (
    <div>
      <div className="bg-gradient-to-r from-surface to-background rounded-2xl p-8 mb-10 border border-gray-800">
        <h1 className="text-4xl font-extrabold text-white mb-4">
          Khám phá thế giới <span className="text-primary">Truyện & Audio</span>
        </h1>
        <p className="text-textSecondary text-lg max-w-2xl mb-6">
          Đọc truyện chữ, nghe truyện audio có sẵn, hoặc tự động chuyển đổi bất kỳ chương truyện nào thành giọng nói AI sống động chỉ với một cú click.
        </p>
        <button className="bg-primary hover:bg-orange-600 text-white font-bold py-3 px-6 rounded-lg transition-colors">
          Khám phá ngay
        </button>
      </div>

      <div className="flex justify-between items-end mb-6">
        <h2 className="text-2xl font-bold text-white border-b-2 border-primary pb-2 inline-block">Mới cập nhật</h2>
      </div>

      {loading ? (
        <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-6">
          {[1, 2, 3, 4, 5].map((i) => (
            <div key={i} className="animate-pulse">
              <div className="bg-surface h-64 rounded-lg w-full mb-3"></div>
              <div className="bg-surface h-4 rounded w-3/4 mb-2"></div>
              <div className="bg-surface h-3 rounded w-1/2"></div>
            </div>
          ))}
        </div>
      ) : stories.length > 0 ? (
        <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-6">
          {stories.map((story) => (
            <Link to={`/story/${story.id}`} key={story.id} className="group cursor-pointer">
              <div className="relative overflow-hidden rounded-lg aspect-[2/3] mb-3 bg-surface">
                {story.coverImage ? (
                  <img
                    src={getMediaUrl(story.coverImage)}
                    alt={story.title}
                    className="object-cover w-full h-full group-hover:scale-105 transition-transform duration-300"
                  />
                ) : (
                  <div className="w-full h-full flex justify-center items-center text-textSecondary bg-gray-800">
                    No Image
                  </div>
                )}
                {story.status === 'COMPLETED' && (
                  <div className="absolute top-2 right-2 bg-green-500 text-white text-xs font-bold px-2 py-1 rounded">
                    Full
                  </div>
                )}
              </div>
              <h3 className="text-white font-semibold text-sm line-clamp-2 group-hover:text-primary transition-colors">
                {story.title}
              </h3>
              <p className="text-textSecondary text-xs mt-1 truncate">
                {story.authorName || 'Đang cập nhật'}
              </p>
            </Link>
          ))}
        </div>
      ) : (
        <div className="text-center py-10 bg-surface rounded-lg border border-gray-800 text-textSecondary">
          Chưa có truyện nào trong hệ thống.
        </div>
      )}
    </div>
  );
};

export default HomePage;
