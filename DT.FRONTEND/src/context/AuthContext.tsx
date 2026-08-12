import React, { createContext, useContext, useState, useEffect, useRef, type ReactNode } from 'react';
import { jwtDecode } from 'jwt-decode';
import type { UserRole } from '../types';
import { userService } from '../services/userService';

interface JwtPayload {
  sub: string;
  email?: string;
  name?: string;
  'cognito:groups'?: string[];
  exp: number;
}

interface AuthContextType {
  token: string | null;
  cognitoId: string | null;
  role: UserRole | null;
  isAuthenticated: boolean;
  login: (token: string) => void;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [token, setToken] = useState<string | null>(localStorage.getItem('token'));
  const [cognitoId, setCognitoId] = useState<string | null>(null);
  const [role, setRole] = useState<UserRole | null>(null);

  const isSyncingRef = useRef(false);

  const processToken = async (jwtToken: string) => {
    if (isSyncingRef.current) return;
    isSyncingRef.current = true;

    try {
      const decoded = jwtDecode<JwtPayload>(jwtToken);
      
      if (decoded.exp * 1000 < Date.now()) {
        logout();
        return;
      }

      setCognitoId(decoded.sub);

      const groups = decoded['cognito:groups'] || [];
      let currentRole: UserRole = 'STUDENT';
      if (groups.includes('ADMIN')) currentRole = 'ADMIN';
      else if (groups.includes('TUTOR')) currentRole = 'TUTOR';
      
      setRole(currentRole);

      const email = decoded.email || '';
      const fullName = decoded.name || email.split('@')[0] || 'Usuario';

      // Sincronización con users-service
      await userService.createUserProfile({
        cognitoId: decoded.sub,
        email: email,
        fullName: fullName,
        role: currentRole
      });

    } catch (error) {
      console.error('Error al procesar token o sincronizar usuario:', error);
      // No ejecutamos logout() para evitar re-renders infinitos en caso de fallos de red
    } finally {
      isSyncingRef.current = false;
    }
  };

  useEffect(() => {
    if (token) {
      processToken(token);
    }
  }, [token]);

  const login = (newToken: string) => {
    localStorage.setItem('token', newToken);
    setToken(newToken);
  };

  const logout = () => {
    localStorage.removeItem('token');
    setToken(null);
    setCognitoId(null);
    setRole(null);
    isSyncingRef.current = false;

    // Redirección global que limpia la aplicación y recarga en la vista de Login
    window.location.href = '/login';
  };

  return (
    <AuthContext.Provider
      value={{
        token,
        cognitoId,
        role,
        isAuthenticated: !!token,
        login,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = (): AuthContextType => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth debe ser usado dentro de un AuthProvider');
  }
  return context;
};