import React, { createContext, useState } from 'react';

export const AudioContext = createContext();

export const AudioProvider = ({ children }) => {
  const [currentAudio, setCurrentAudio] = useState(null);
  // currentAudio = { src: string, title: string, storyTitle?: string, chapterId?: number|string, storyId?: number|string } | null

  const playAudio = (audioInfo) => {
    setCurrentAudio(audioInfo);
  };

  const stopAudio = () => {
    setCurrentAudio(null);
  };

  return (
    <AudioContext.Provider value={{ currentAudio, playAudio, stopAudio }}>
      {children}
    </AudioContext.Provider>
  );
};
