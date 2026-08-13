import React, { useRef, useState, useEffect, useContext } from 'react';
import { Link } from 'react-router-dom';
import { AudioContext } from '../context/AudioContext';

const AudioPlayer = () => {
  const { currentAudio, stopAudio } = useContext(AudioContext);
  const audioRef = useRef(null);
  const [isPlaying, setIsPlaying] = useState(false);
  const [currentTime, setCurrentTime] = useState(0);
  const [duration, setDuration] = useState(0);
  const [volume, setVolume] = useState(1);

  useEffect(() => {
    if (currentAudio?.src && audioRef.current) {
      audioRef.current.src = currentAudio.src;
      audioRef.current.play().then(() => setIsPlaying(true)).catch(() => setIsPlaying(false));
    }
  }, [currentAudio?.src]);

  if (!currentAudio) return null;

  const formatTime = (time) => {
    if (!time || isNaN(time)) return '0:00';
    const m = Math.floor(time / 60);
    const s = Math.floor(time % 60).toString().padStart(2, '0');
    return `${m}:${s}`;
  };

  const togglePlay = () => {
    if (!audioRef.current) return;
    if (isPlaying) {
      audioRef.current.pause();
    } else {
      audioRef.current.play();
    }
    setIsPlaying(!isPlaying);
  };

  const handleSeek = (e) => {
    if (!audioRef.current) return;
    const newTime = Number(e.target.value);
    audioRef.current.currentTime = newTime;
    setCurrentTime(newTime);
  };

  const handleVolumeChange = (e) => {
    const newVol = Number(e.target.value);
    setVolume(newVol);
    if (audioRef.current) audioRef.current.volume = newVol;
  };

  const progressPercent = duration ? (currentTime / duration) * 100 : 0;

  return (
    <div className="fixed bottom-0 left-0 right-0 z-50 backdrop-blur-lg bg-surface/95 border-t border-gray-700 shadow-2xl shadow-black/80">
      <audio
        ref={audioRef}
        onTimeUpdate={() => setCurrentTime(audioRef.current?.currentTime || 0)}
        onLoadedMetadata={() => setDuration(audioRef.current?.duration || 0)}
        onEnded={() => setIsPlaying(false)}
      />
      {/* Progress Bar (Click & Seek) */}
      <div className="relative h-1.5 bg-gray-700 cursor-pointer group">
        <div
          className="absolute top-0 left-0 h-full bg-primary transition-all group-hover:bg-orange-400"
          style={{ width: `${progressPercent}%` }}
        />
        <input
          type="range"
          min="0"
          max={duration || 0}
          step="0.1"
          value={currentTime}
          onChange={handleSeek}
          className="absolute inset-0 w-full opacity-0 cursor-pointer h-full"
        />
      </div>

      <div className="max-w-7xl mx-auto px-4 py-3 flex items-center justify-between gap-4">
        {/* Info */}
        <div className="flex-1 min-w-0">
          {currentAudio.storyId && currentAudio.chapterId ? (
            <Link
              to={`/story/${currentAudio.storyId}/chapter/${currentAudio.chapterId}`}
              className="text-white text-sm font-semibold hover:text-primary transition-colors truncate block"
            >
              {currentAudio.title || 'Audio'}
            </Link>
          ) : (
            <p className="text-white text-sm font-semibold truncate">{currentAudio.title || 'Audio'}</p>
          )}
          <p className="text-textSecondary text-xs truncate">
            {currentAudio.storyTitle ? `Truyện: ${currentAudio.storyTitle}` : 'StoryVoice AI Audio'}
          </p>
        </div>

        {/* Controls */}
        <div className="flex items-center gap-4">
          <span className="text-textSecondary text-xs font-mono">{formatTime(currentTime)}</span>
          <button
            onClick={togglePlay}
            className="w-10 h-10 rounded-full bg-primary hover:bg-orange-600 flex items-center justify-center transition-colors text-white text-lg shadow-md shadow-orange-950/50"
          >
            {isPlaying ? '⏸' : '▶'}
          </button>
          <span className="text-textSecondary text-xs font-mono">{formatTime(duration)}</span>
        </div>

        {/* Volume & Close */}
        <div className="flex items-center gap-4">
          <div className="hidden md:flex items-center gap-2 w-28">
            <span className="text-textSecondary text-sm">🔊</span>
            <input
              type="range"
              min="0"
              max="1"
              step="0.05"
              value={volume}
              onChange={handleVolumeChange}
              className="w-full h-1 accent-primary cursor-pointer"
            />
          </div>

          <button
            onClick={stopAudio}
            className="text-textSecondary hover:text-white transition-colors text-xl leading-none px-1"
            title="Đóng trình phát"
          >
            ✕
          </button>
        </div>
      </div>
    </div>
  );
};

export default AudioPlayer;
