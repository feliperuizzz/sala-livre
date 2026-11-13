import { Navigate } from "react-router-dom";

import { useAuth } from "../contexts/AuthContext";

export default function AdminRoute({ children }) {
  const { professor, autenticado, carregando } = useAuth();

  if (carregando) {
    return <div className="pagina-carregando">Carregando...</div>;
  }

  if (!autenticado) {
    return <Navigate to="/login" replace />;
  }

  if (professor?.perfil !== "ADMIN") {
    return <Navigate to="/" replace />;
  }

  return children;
}
