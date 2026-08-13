import React, { useContext } from 'react';
import { Navigate } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';

/**
 * Protects a route: redirects to login if not authenticated,
 * or to home if the user doesn't have the required role.
 */
const ProtectedRoute = ({ children, requiredRole }) => {
  const { user, loading } = useContext(AuthContext);

  if (loading) return null;

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  if (requiredRole && user.role !== requiredRole && user.role !== `ROLE_${requiredRole}`) {
    return <Navigate to="/" replace />;
  }

  return children;
};

export default ProtectedRoute;
