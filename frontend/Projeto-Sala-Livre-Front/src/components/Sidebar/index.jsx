import {
  Building2,
  DoorOpen,
  LogIn,
  LogOut,
  UserRound,
  Users,
} from "lucide-react";

import { useNavigate } from "react-router-dom";

import { useAuth } from "../../contexts/AuthContext";

import { useConfirmacao } from "../../hooks/useConfirmacao";

import ModalConfirmacao from "../../components/ModalConfirmacao";
import styles from "./style.module.css";

export default function Sidebar({ paginaAtiva = "salas" }) {
  const navigate = useNavigate();

  const { professor, autenticado, logout } = useAuth();

  const { confirmacao, abrirConfirmacao, fecharConfirmacao } = useConfirmacao();

  const ehAdministrador = professor?.perfil === "ADMIN";

  function solicitarLogout() {
    abrirConfirmacao({
      titulo: "Sair da conta?",

      mensagem:
        "Você voltará ao modo de consulta pública. Deseja realmente sair?",

      textoConfirmar: "Sair",

      textoCancelar: "Cancelar",

      onConfirmar: () => {
        fecharConfirmacao();

        logout();

        window.location.href = "/";
      },
    });
  }

  return (
    <>
      <aside className={styles.sidebar}>
        <div>
          <div className={styles.marca}>
            <div className={styles.marcaIcone}>
              <DoorOpen size={25} />
            </div>

            <div>
              <strong>Sala Livre</strong>

              <span>Ambiente acadêmico</span>
            </div>
          </div>

          <nav className={styles.menu}>
            <button
              type="button"
              className={
                paginaAtiva === "salas"
                  ? `${styles.menuItem} ${styles.ativo}`
                  : styles.menuItem
              }
              onClick={() => navigate("/")}
            >
              <DoorOpen size={19} />

              <span>Salas</span>
            </button>

            {ehAdministrador && (
              <>
                <button
                  type="button"
                  className={
                    paginaAtiva === "gerenciar"
                      ? `${styles.menuItem} ${styles.ativo}`
                      : styles.menuItem
                  }
                  onClick={() => navigate("/admin/salas")}
                >
                  <Building2 size={19} />

                  <span>Gerenciar salas</span>
                </button>

                <button
                  type="button"
                  className={
                    paginaAtiva === "professores"
                      ? `${styles.menuItem} ${styles.ativo}`
                      : styles.menuItem
                  }
                  onClick={() => navigate("/admin/professores")}
                >
                  <Users size={19} />

                  <span>Professores</span>
                </button>
              </>
            )}
          </nav>
        </div>

        <div className={styles.usuarioArea}>
          {autenticado ? (
            <>
              <div className={styles.usuarioInfo}>
                <div className={styles.usuarioAvatar}>
                  <UserRound size={20} />
                </div>

                <div>
                  <strong>{professor.nome}</strong>

                  <span>{ehAdministrador ? "Administrador" : "Professor"}</span>
                </div>
              </div>

              <button
                type="button"
                className={styles.botaoSidebar}
                onClick={solicitarLogout}
              >
                <LogOut size={18} />
                Sair
              </button>
            </>
          ) : (
            <>
              <div className={styles.usuarioInfo}>
                <div className={styles.usuarioAvatar}>
                  <UserRound size={20} />
                </div>

                <div>
                  <strong>Visitante</strong>

                  <span>Consulta pública</span>
                </div>
              </div>

              <button
                type="button"
                className={`${styles.botaoSidebar} ${styles.entrar}`}
                onClick={() => navigate("/login")}
              >
                <LogIn size={18} />
                Entrar no sistema
              </button>
            </>
          )}
        </div>
      </aside>

      <ModalConfirmacao
        aberto={confirmacao.aberto}
        titulo={confirmacao.titulo}
        mensagem={confirmacao.mensagem}
        textoConfirmar={confirmacao.textoConfirmar}
        textoCancelar={confirmacao.textoCancelar}
        onCancelar={fecharConfirmacao}
        onConfirmar={confirmacao.onConfirmar}
      />
    </>
  );
}
