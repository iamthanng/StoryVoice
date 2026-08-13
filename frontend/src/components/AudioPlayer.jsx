import React, { useRef, useState, useEffect } from 'react';

const AudioPlayer = ({ audioSrc, chapterTitle, onClose }) => {
  const audioRef = useRef(null);
  const [isPlaying, setIsPlaying] = useState(false);
  const [currentTime, setCurrentTime] = useState(0);
  const [duration, setDuration] = useState(0);
  const [volume, setVolume] = useState(1);

  useEffect(() => {
    if (audioSrc && audioRef.current) {
      audioRef.current.src = audioSrc;
      audioRef.current.play();
      setIsPlaying(true);
    }
  }, [audioSrc]);

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
    <div className="fixed bottom-0 left-0 right-0 z-50 backdrop-blur-lg bg-surface/90 border-t border-gray-700 shadow-2xl shadow-black/60">
      <audio
        ref={audioRef}
        onTimeUpdate={() => setCurrentTime(audioRef.current?.currentTime || 0)}
        onLoadedMetadata={() => setDuration(audioRef.current?.duration || 0)}
        onEnded={() => setIsPlaying(false)}
      />
      {/* Progress Bar */}
      <div className="relative h-1 bg-gray-700 cursor-pointer">
        <div
          className="absolute top-0 left-0 h-1 bg-primary transition-all"
          style={{ width: `${progressPercent}%` }}
        />
        <input
          type="range"
          min="0"
          max={duration || 0}
          step="0.1"
          value={currentTime}
          onChange={handleSeek}
          className="absolute inset-0 w-full opacity-0 cursor-pointer h-1"
        />
      </div>

      <div className="max-w-7xl mx-auto px-4 py-3 flex items-center gap-4">
        {/* Song Info */}
        <div className="flex-1 min-w-0">
          <p className="text-white text-sm font-semibold truncate">{chapterTitle || 'Audio'}</p>
          <p className="text-textSecondary text-xs">StoryVoice AI Audio</p>
        </div>

        {/* Controls */}
        <div className="flex items-center gap-4">
          <span className="text-textSecondary text-xs">{formatTime(currentTime)}</span>
          <button
            onClick={togglePlay}
            className="w-10 h-10 rounded-full bg-primary hover:bg-orange-600 flex items-center justify-center transition-colors text-white text-lg"
          >
            {isPlaying ? '⏸' : '▶'}
          </button>
          <span className="text-textSecondary text-xs">{formatTime(duration)}</span>
        </div>

        {/* Volume */}
        <div className="hidden md:flex items-center gap-2 w-32">
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

        {/* Close */}
        <button
          onClick={onClose}
          className="text-textSecondary hover:text-white transition-colors text-xl leading-none"
        >
          ✕
        </button>
      </div>
    </div>
  );
};

export default AudioPlayer;
