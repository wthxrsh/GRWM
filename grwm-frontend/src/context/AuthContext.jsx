import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';
import { api, clearSession, getStoredUser, saveSession, saveUser } from '../api/client.js';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => getStoredUser());
  const [loading, setLoading] = useState(false);

  const login = useCallback(async (credentials) => {
    const data = await api.login(credentials);
    saveSession(data);
    setUser(data.user);
    return data.user;
  }, []);

  const register = useCallback(async (payload) => {
    const data = await api.register(payload);
    saveSession(data);
    setUser(data.user);
    return data.user;
  }, []);

  const logout = useCallback(() => {
    clearSession();
    setUser(null);
  }, []);

  const updateProfile = useCallback(async (payload) => {
    const updated = await api.updateProfile(payload);
    saveUser(updated);
    setUser(updated);
    return updated;
  }, []);

  const refreshProfile = useCallback(async () => {
    const profile = await api.getProfile();
    saveUser(profile);
    setUser(profile);
    return profile;
  }, []);

  useEffect(() => {
    if (user && !loading) {
      refreshProfile().catch(() => clearSession());
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const value = useMemo(
    () => ({ user, loading, login, register, logout, updateProfile, refreshProfile }),
    [user, loading, login, register, logout, updateProfile, refreshProfile],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}