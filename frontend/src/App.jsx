import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import MainLayout from './layouts/MainLayout';
import HomePage from './pages/HomePage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import StoryDetailPage from './pages/StoryDetailPage';
import ChapterPage from './pages/ChapterPage';

function App() {
  return (
    <AuthProvider>
      <Router>
        <Routes>
          <Route path="/" element={<MainLayout />}>
            <Route index element={<HomePage />} />
            <Route path="login" element={<LoginPage />} />
            <Route path="register" element={<RegisterPage />} />
            <Route path="story/:id" element={<StoryDetailPage />} />
            <Route path="story/:storyId/chapter/:chapterId" element={<ChapterPage />} />
            {/* Admin routes will be added in Sprint 3 */}
          </Route>
        </Routes>
      </Router>
    </AuthProvider>
  );
}

export default App;
