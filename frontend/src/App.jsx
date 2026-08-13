import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { AudioProvider } from './context/AudioContext';

// Layouts
import MainLayout from './layouts/MainLayout';
import AdminLayout from './layouts/AdminLayout';

// Guards
import ProtectedRoute from './components/ProtectedRoute';

// Public Pages
import HomePage from './pages/HomePage';
import BrowsePage from './pages/BrowsePage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import StoryDetailPage from './pages/StoryDetailPage';
import ChapterPage from './pages/ChapterPage';

// Admin Pages
import AdminDashboard from './pages/admin/AdminDashboard';
import AdminStories from './pages/admin/AdminStories';
import AdminChapters from './pages/admin/AdminChapters';
import AdminUsers from './pages/admin/AdminUsers';

function App() {
  return (
    <AuthProvider>
      <AudioProvider>
        <Router>
          <Routes>
            {/* Public routes */}
            <Route path="/" element={<MainLayout />}>
              <Route index element={<HomePage />} />
              <Route path="browse" element={<BrowsePage />} />
              <Route path="login" element={<LoginPage />} />
              <Route path="register" element={<RegisterPage />} />
              <Route path="story/:id" element={<StoryDetailPage />} />
              <Route path="story/:storyId/chapter/:chapterId" element={<ChapterPage />} />
            </Route>

            {/* Admin routes — protected, require ADMIN role */}
            <Route
              path="/admin"
              element={
                <ProtectedRoute requiredRole="ADMIN">
                  <AdminLayout />
                </ProtectedRoute>
              }
            >
              <Route index element={<AdminDashboard />} />
              <Route path="stories" element={<AdminStories />} />
              <Route path="stories/:storyId/chapters" element={<AdminChapters />} />
              <Route path="users" element={<AdminUsers />} />
            </Route>
          </Routes>
        </Router>
      </AudioProvider>
    </AuthProvider>
  );
}

export default App;
