import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import SalaLivre from "../pages/SalaLivre";
import Login from "../pages/Login";
import AdminRoute from "./AdminRoute";
import GerenciarSalas from "../pages/GerenciarSalas";
import Professores from "../pages/Professores";

export default function AppRoutes() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<SalaLivre />} />

        <Route path="/login" element={<Login />} />

        <Route
          path="/admin/salas"
          element={
            <AdminRoute>
              <GerenciarSalas />
            </AdminRoute>
          }
        />

        <Route
          path="/admin/professores"
          element={
            <AdminRoute>
              <Professores />
            </AdminRoute>
          }
        />

        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  );
}
