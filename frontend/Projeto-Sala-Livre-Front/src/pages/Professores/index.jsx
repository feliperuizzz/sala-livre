import { useEffect, useState } from "react";
import { Mail, Plus, UserRound } from "lucide-react";

import Sidebar from "../../components/Sidebar";
import ModalConfirmacao from "../../components/ModalConfirmacao";
import ProfessorFormModal from "./ProfessorFormModal";

import adminProfessorService from "../../services/adminProfessorService";

import "./style.css";

export default function Professores() {
  const [professores, setProfessores] = useState([]);

  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState("");

  const [modalFormularioAberto, setModalFormularioAberto] = useState(false);

  const [professorEmEdicao, setProfessorEmEdicao] = useState(null);

  const [processandoFormulario, setProcessandoFormulario] = useState(false);

  const [erroFormulario, setErroFormulario] = useState("");

  const [professorSituacao, setProfessorSituacao] = useState(null);

  useEffect(() => {
    carregarProfessores();
  }, []);

  async function carregarProfessores() {
    setCarregando(true);
    setErro("");

    try {
      const resultado = await adminProfessorService.listar();

      setProfessores(resultado);
    } catch (error) {
      console.error(error);

      setErro("Não foi possível carregar os professores.");
    } finally {
      setCarregando(false);
    }
  }

  function abrirNovoProfessor() {
    setProfessorEmEdicao(null);
    setErroFormulario("");
    setModalFormularioAberto(true);
  }

  function abrirEditarProfessor(professor) {
    setProfessorEmEdicao(professor);
    setErroFormulario("");
    setModalFormularioAberto(true);
  }

  function fecharFormulario() {
    if (processandoFormulario) {
      return;
    }

    setModalFormularioAberto(false);
    setProfessorEmEdicao(null);
    setErroFormulario("");
  }

  async function salvarProfessor(dados) {
    if (!dados.nome || !dados.email) {
      setErroFormulario("Preencha todos os campos obrigatórios.");
      return;
    }

    if (!professorEmEdicao && !dados.senha?.trim()) {
      setErroFormulario("Informe a senha inicial.");
      return;
    }

    setProcessandoFormulario(true);
    setErroFormulario("");

    try {
      if (professorEmEdicao) {
        await adminProfessorService.editar(professorEmEdicao.id, {
          nome: dados.nome,
          email: dados.email,
        });
      } else {
        await adminProfessorService.cadastrar(
          dados.nome,
          dados.email,
          dados.senha,
        );
      }

      setModalFormularioAberto(false);
      setProfessorEmEdicao(null);

      await carregarProfessores();
    } catch (error) {
      setErroFormulario(
        error.response?.data?.erro || "Não foi possível salvar o professor.",
      );
    } finally {
      setProcessandoFormulario(false);
    }
  }

  function solicitarAlteracaoSituacao(professor) {
    setProfessorSituacao(professor);
  }

  async function confirmarAlteracaoSituacao() {
    if (!professorSituacao) {
      return;
    }

    try {
      await adminProfessorService.alterarSituacao(professorSituacao.id);

      setProfessorSituacao(null);

      await carregarProfessores();
    } catch (error) {
      console.error(error);

      setProfessorSituacao(null);

      setErro(
        error.response?.data?.erro ||
          "Não foi possível alterar a situação do professor.",
      );
    }
  }

  return (
    <div className="app-layout">
      <Sidebar paginaAtiva="professores" />

      <main className="app-content">
        <header className="prof-admin-cabecalho">
          <div>
            <h1>Professores</h1>

            <p>Gerencie os professores que possuem acesso ao sistema.</p>
          </div>

          <button
            type="button"
            className="prof-admin-novo"
            onClick={abrirNovoProfessor}
          >
            <Plus size={18} />
            Novo professor
          </button>
        </header>

        <section className="prof-admin-painel">
          <div className="prof-admin-painel-topo">
            <div>
              <h2>Professores cadastrados</h2>

              <span>
                {professores.length}{" "}
                {professores.length === 1 ? "professor" : "professores"}
              </span>
            </div>
          </div>

          {carregando && (
            <div className="prof-admin-estado">Carregando professores...</div>
          )}

          {!carregando && erro && (
            <div className="prof-admin-estado erro">{erro}</div>
          )}

          {!carregando && !erro && professores.length === 0 && (
            <div className="prof-admin-vazio">Nenhum professor cadastrado.</div>
          )}

          {!carregando && !erro && professores.length > 0 && (
            <div className="prof-admin-lista">
              {professores.map((professor) => (
                <div className="prof-admin-card" key={professor.id}>
                  <div className="prof-admin-avatar">
                    <UserRound size={21} />
                  </div>

                  <div className="prof-admin-dados">
                    <strong>{professor.nome}</strong>

                    <span>
                      <Mail size={13} />
                      {professor.email}
                    </span>
                  </div>

                  <div className="prof-admin-acoes">
                    <span
                      className={
                        professor.ativo ? "status ativo" : "status inativo"
                      }
                    >
                      {professor.ativo ? "Ativo" : "Inativo"}
                    </span>

                    <button
                      type="button"
                      className="prof-admin-editar"
                      onClick={() => abrirEditarProfessor(professor)}
                    >
                      Editar
                    </button>

                    <button
                      type="button"
                      className={
                        professor.ativo
                          ? "prof-admin-situacao desativar"
                          : "prof-admin-situacao ativar"
                      }
                      onClick={() => solicitarAlteracaoSituacao(professor)}
                    >
                      {professor.ativo ? "Desativar" : "Ativar"}
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </section>
      </main>

      {modalFormularioAberto && (
        <ProfessorFormModal
          key={professorEmEdicao?.id ?? "novo-professor"}
          professor={professorEmEdicao}
          processando={processandoFormulario}
          erro={erroFormulario}
          onClose={fecharFormulario}
          onSalvar={salvarProfessor}
        />
      )}

      <ModalConfirmacao
        aberto={Boolean(professorSituacao)}
        titulo={
          professorSituacao?.ativo ? "Desativar professor" : "Ativar professor"
        }
        mensagem={
          professorSituacao
            ? professorSituacao.ativo
              ? `Deseja realmente desativar o acesso de ${professorSituacao.nome}?`
              : `Deseja realmente ativar o acesso de ${professorSituacao.nome}?`
            : ""
        }
        textoConfirmar={professorSituacao?.ativo ? "Desativar" : "Ativar"}
        textoCancelar="Cancelar"
        variante={professorSituacao?.ativo ? "perigo" : "sucesso"}
        onConfirmar={confirmarAlteracaoSituacao}
        onCancelar={() => setProfessorSituacao(null)}
      />
    </div>
  );
}
