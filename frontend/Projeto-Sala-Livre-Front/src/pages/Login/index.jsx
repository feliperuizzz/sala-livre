import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import {
  ArrowLeft,
  DoorOpen,
  Eye,
  EyeOff,
  LockKeyhole,
  Mail,
} from "lucide-react";

import api from "../../services/api";
import { useAuth } from "../../contexts/AuthContext";

import "./style.css";

export default function Login() {
  const navigate = useNavigate();

  const { login, autenticado, carregando } = useAuth();

  const [email, setEmail] = useState("");
  const [senha, setSenha] = useState("");

  const [mostrarSenha, setMostrarSenha] = useState(false);

  const [erro, setErro] = useState("");

  const [enviando, setEnviando] = useState(false);

  useEffect(() => {
    if (!carregando && autenticado) {
      navigate("/", { replace: true });
    }
  }, [autenticado, carregando, navigate]);

  async function entrar(event) {
    event.preventDefault();

    setErro("");
    setEnviando(true);

    try {
      const response = await api.post("/auth/login", {
        email,
        senha,
      });

      await login(response.data.token);

      navigate("/", {
        replace: true,
      });
    } catch (error) {
      console.error(error);

      setErro(error.response?.data?.erro || "E-mail ou senha inválidos.");
    } finally {
      setEnviando(false);
    }
  }

  if (carregando) {
    return <div className="login-carregando">Carregando...</div>;
  }

  return (
    <main className="login-pagina">
      <div className="login-forma-superior" />
      <div className="login-forma-inferior" />

      <button
        type="button"
        className="login-voltar"
        onClick={() => navigate("/")}
      >
        <ArrowLeft size={18} />
        Consultar salas
      </button>

      <section className="login-card">
        <div className="login-marca">
          <div className="login-logo">
            <DoorOpen size={32} strokeWidth={2} />
          </div>

          <h1>Sala Livre</h1>

          <p>
            Entre para acessar as funcionalidades <br />
            disponíveis ao seu perfil.
          </p>
        </div>

        <form className="login-formulario" onSubmit={entrar}>
          <div className="login-grupo">
            <label htmlFor="email">E-mail</label>

            <div className="login-campo">
              <Mail size={18} />

              <input
                id="email"
                type="email"
                placeholder="seu@email.com"
                value={email}
                autoComplete="email"
                required
                onChange={(event) => setEmail(event.target.value)}
              />
            </div>
          </div>

          <div className="login-grupo">
            <label htmlFor="senha">Senha</label>

            <div className="login-campo">
              <LockKeyhole size={18} />

              <input
                id="senha"
                type={mostrarSenha ? "text" : "password"}
                placeholder="••••••••"
                value={senha}
                autoComplete="current-password"
                required
                onChange={(event) => setSenha(event.target.value)}
              />

              <button
                type="button"
                className="login-mostrar-senha"
                onClick={() => setMostrarSenha((valor) => !valor)}
                aria-label={mostrarSenha ? "Ocultar senha" : "Mostrar senha"}
              >
                {mostrarSenha ? <EyeOff size={18} /> : <Eye size={18} />}
              </button>
            </div>
          </div>

          {erro && <div className="login-erro">{erro}</div>}

          <button type="submit" className="login-botao" disabled={enviando}>
            {enviando ? "Entrando..." : "Entrar"}
          </button>
        </form>
      </section>
    </main>
  );
}
