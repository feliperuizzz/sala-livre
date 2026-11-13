import { createContext, useContext, useEffect, useState } from "react";

import api from "../services/api";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => localStorage.getItem("token"));

  const [professor, setProfessor] = useState(null);

  const [carregando, setCarregando] = useState(true);

  useEffect(() => {
    async function verificarSessao() {
      if (!token) {
        setCarregando(false);
        return;
      }

      try {
        const response = await api.get("/auth/me");

        setProfessor(response.data);
      } catch (error) {
        localStorage.removeItem("token");

        setToken(null);
        setProfessor(null);
      } finally {
        setCarregando(false);
      }
    }

    verificarSessao();
  }, []);

  async function login(novoToken) {
    localStorage.setItem("token", novoToken);

    setToken(novoToken);

    try {
      const response = await api.get("/auth/me");

      setProfessor(response.data);

      return response.data;
    } catch (error) {
      logout();

      throw error;
    }
  }

  function logout() {
    localStorage.removeItem("token");

    setToken(null);
    setProfessor(null);
  }

  const autenticado = !!token && !!professor;

  return (
    <AuthContext.Provider
      value={{
        token,
        professor,
        autenticado,
        carregando,
        login,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);

  if (!context) {
    throw new Error("useAuth deve ser usado dentro de AuthProvider");
  }

  return context;
}
