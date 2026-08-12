import React from 'react';
import { Redirect, Route, type RouteProps } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import type { UserRole } from '../types';

interface ProtectedRouteProps extends RouteProps {
  allowedRoles?: UserRole[];
  children: React.ReactNode;
}

export const ProtectedRoute: React.FC<ProtectedRouteProps> = ({
  children,
  allowedRoles,
  ...rest
}) => {
  const { isAuthenticated, role } = useAuth();

  return (
    <Route
      {...rest}
      render={({ location }) => {
        // 1. Redirección si no está autenticado
        if (!isAuthenticated) {
          return (
            <Redirect
              to={{
                pathname: '/login',
                state: { from: location },
              }}
            />
          );
        }

        // 2. Redirección si el rol no coincide con los autorizados
        if (allowedRoles && role && !allowedRoles.includes(role)) {
          return <Redirect to="/unauthorized" />;
        }

        // 3. Renderizado del contenido protegido
        return children;
      }}
    />
  );
};