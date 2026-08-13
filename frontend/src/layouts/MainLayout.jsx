import React from 'react';
import { Outlet } from 'react-router-dom';
import Navbar from '../components/Navbar';
import Footer from '../components/Footer';
import AudioPlayer from '../components/AudioPlayer';

const MainLayout = () => {
  return (
    <div className="min-h-screen flex flex-col bg-background text-textPrimary font-sans">
      <Navbar />
      <main className="flex-grow max-w-7xl mx-auto w-full px-4 sm:px-6 lg:px-8 py-8">
        <Outlet />
      </main>
      <Footer />
      {/* Global persistent audio player across all pages */}
      <AudioPlayer />
    </div>
  );
};

export default MainLayout;
