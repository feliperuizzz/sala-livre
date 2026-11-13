import { useEffect, useMemo, useState } from "react";

import { DoorOpen, Plus } from "lucide-react";

import Sidebar from "../../components/Sidebar";
import ModalConfirmacao from "../../components/ModalConfirmacao";

import BlocoFormModal from "./BlocoFormModal";
import SalaFormModal from "./SalaFormModal";

import adminSalaService from "../../services/adminSalaService";

import "./style.css";

export default function GerenciarSalas() {
  const [blocos, setBlocos] = useState([]);
  const [salas, setSalas] = useState([]);

  const [carregando, setCarregando] = useState(true);

  const [erro, setErro] = useState("");
  const [erroOperacao, setErroOperacao] = useState("");

  const [modalBlocoAberto, setModalBlocoAberto] = useState(false);

  const [blocoEmEdicao, setBlocoEmEdicao] = useState(null);

  const [processandoBloco, setProcessandoBloco] = useState(false);

  const [erroBloco, setErroBloco] = useState("");

  const [blocoParaExcluir, setBlocoParaExcluir] = useState(null);

  const [modalSalaAberto, setModalSalaAberto] = useState(false);

  const [blocoSalaSelecionado, setBlocoSalaSelecionado] = useState(null);

  const [salaEmEdicao, setSalaEmEdicao] = useState(null);

  const [processandoSala, setProcessandoSala] = useState(false);

  const [erroSala, setErroSala] = useState("");

  const [salaParaExcluir, setSalaParaExcluir] = useState(null);

  useEffect(() => {
    carregarDados();
  }, []);

  async function carregarDados() {
    setCarregando(true);
    setErro("");

    try {
      const [blocosResultado, salasResultado] = await Promise.all([
        adminSalaService.listarBlocos(),
        adminSalaService.listarSalas(),
      ]);

      setBlocos(blocosResultado);
      setSalas(salasResultado);
    } catch (error) {
      console.error(error);

      setErro("Não foi possível carregar os blocos e salas.");
    } finally {
      setCarregando(false);
    }
  }

  const salasPorBloco = useMemo(() => {
    return salas.reduce((grupos, sala) => {
      if (!grupos[sala.blocoId]) {
        grupos[sala.blocoId] = [];
      }

      grupos[sala.blocoId].push(sala);

      return grupos;
    }, {});
  }, [salas]);

  function abrirNovoBloco() {
    setBlocoEmEdicao(null);
    setErroBloco("");
    setErroOperacao("");
    setModalBlocoAberto(true);
  }

  function abrirEditarBloco(bloco) {
    setBlocoEmEdicao(bloco);
    setErroBloco("");
    setErroOperacao("");
    setModalBlocoAberto(true);
  }

  function fecharModalBloco() {
    if (processandoBloco) {
      return;
    }

    setModalBlocoAberto(false);
    setBlocoEmEdicao(null);
    setErroBloco("");
  }

  async function salvarBloco(dados) {
    if (!dados.codigo) {
      setErroBloco("Informe o código do bloco.");
      return;
    }

    setProcessandoBloco(true);
    setErroBloco("");

    try {
      if (blocoEmEdicao) {
        await adminSalaService.editarBloco(blocoEmEdicao.id, dados.codigo);
      } else {
        await adminSalaService.cadastrarBloco(dados.codigo);
      }

      setModalBlocoAberto(false);
      setBlocoEmEdicao(null);

      await carregarDados();
    } catch (error) {
      setErroBloco(
        error.response?.data?.erro || "Não foi possível salvar o bloco.",
      );
    } finally {
      setProcessandoBloco(false);
    }
  }

  function solicitarExclusaoBloco(bloco) {
    setErroOperacao("");
    setBlocoParaExcluir(bloco);
  }

  async function excluirBloco() {
    if (!blocoParaExcluir) {
      return;
    }

    try {
      await adminSalaService.excluirBloco(blocoParaExcluir.id);

      setBlocoParaExcluir(null);

      await carregarDados();
    } catch (error) {
      setBlocoParaExcluir(null);

      setErroOperacao(
        error.response?.data?.erro || "Não foi possível excluir o bloco.",
      );
    }
  }

  function abrirCadastroSala(bloco) {
    setBlocoSalaSelecionado(bloco);
    setSalaEmEdicao(null);

    setErroSala("");
    setErroOperacao("");

    setModalSalaAberto(true);
  }

  function abrirEditarSala(sala, bloco) {
    setSalaEmEdicao(sala);
    setBlocoSalaSelecionado(bloco);

    setErroSala("");
    setErroOperacao("");

    setModalSalaAberto(true);
  }

  function fecharModalSala() {
    if (processandoSala) {
      return;
    }

    setModalSalaAberto(false);
    setSalaEmEdicao(null);
    setBlocoSalaSelecionado(null);
    setErroSala("");
  }

  async function salvarSala(dados) {
    if (!dados.codigo || !blocoSalaSelecionado) {
      setErroSala("Informe o código da sala.");
      return;
    }

    setProcessandoSala(true);
    setErroSala("");

    try {
      if (salaEmEdicao) {
        await adminSalaService.editarSala(salaEmEdicao.id, dados.codigo);
      } else {
        await adminSalaService.cadastrarSala(
          dados.codigo,
          blocoSalaSelecionado.id,
        );
      }

      setModalSalaAberto(false);
      setSalaEmEdicao(null);
      setBlocoSalaSelecionado(null);

      await carregarDados();
    } catch (error) {
      setErroSala(
        error.response?.data?.erro || "Não foi possível salvar a sala.",
      );
    } finally {
      setProcessandoSala(false);
    }
  }

  function solicitarExclusaoSala(sala, bloco) {
    setErroOperacao("");

    setSalaParaExcluir({
      ...sala,
      blocoCodigo: bloco.codigo,
    });
  }

  async function excluirSala() {
    if (!salaParaExcluir) {
      return;
    }

    try {
      await adminSalaService.excluirSala(salaParaExcluir.id);

      setSalaParaExcluir(null);

      await carregarDados();
    } catch (error) {
      setSalaParaExcluir(null);

      setErroOperacao(
        error.response?.data?.erro || "Não foi possível excluir a sala.",
      );
    }
  }

  return (
    <div className="app-layout">
      <Sidebar paginaAtiva="gerenciar" />

      <main className="app-content">
        <header className="admin-cabecalho">
          <div>
            <h1>Gerenciar salas</h1>

            <p>Cadastre blocos e organize as salas da instituição.</p>
          </div>

          <button
            type="button"
            className="admin-botao-principal"
            onClick={abrirNovoBloco}
          >
            <Plus size={18} />
            Novo bloco
          </button>
        </header>

        {erroOperacao && (
          <div className="admin-estado erro">{erroOperacao}</div>
        )}

        {carregando && <div className="admin-estado">Carregando...</div>}

        {!carregando && erro && <div className="admin-estado erro">{erro}</div>}

        {!carregando &&
          !erro &&
          blocos.map((bloco) => {
            const salasDoBloco = salasPorBloco[bloco.id] || [];

            return (
              <section className="admin-bloco" key={bloco.id}>
                <div className="admin-bloco-topo">
                  <div>
                    <h2>Bloco {bloco.codigo}</h2>

                    <span>
                      {salasDoBloco.length}{" "}
                      {salasDoBloco.length === 1
                        ? "sala cadastrada"
                        : "salas cadastradas"}
                    </span>
                  </div>

                  <div className="admin-bloco-acoes">
                    <button
                      type="button"
                      className="admin-acao-editar"
                      onClick={() => abrirEditarBloco(bloco)}
                    >
                      Editar
                    </button>

                    <button
                      type="button"
                      className="admin-acao-excluir"
                      onClick={() => solicitarExclusaoBloco(bloco)}
                    >
                      Excluir
                    </button>

                    <button
                      type="button"
                      className="admin-adicionar-sala"
                      onClick={() => abrirCadastroSala(bloco)}
                    >
                      <Plus size={16} />
                      Adicionar sala
                    </button>
                  </div>
                </div>

                {salasDoBloco.length > 0 ? (
                  <div className="admin-salas-grid">
                    {salasDoBloco.map((sala) => (
                      <div className="admin-sala-card" key={sala.id}>
                        <DoorOpen size={20} />

                        <strong>
                          {bloco.codigo}
                          {sala.codigo}
                        </strong>

                        <div className="admin-sala-acoes">
                          <button
                            type="button"
                            className="admin-acao-editar"
                            onClick={() => abrirEditarSala(sala, bloco)}
                          >
                            Editar
                          </button>

                          <button
                            type="button"
                            className="admin-acao-excluir"
                            onClick={() => solicitarExclusaoSala(sala, bloco)}
                          >
                            Excluir
                          </button>
                        </div>
                      </div>
                    ))}
                  </div>
                ) : (
                  <div className="admin-sem-salas">
                    Nenhuma sala cadastrada neste bloco.
                  </div>
                )}
              </section>
            );
          })}
      </main>

      {modalBlocoAberto && (
        <BlocoFormModal
          key={blocoEmEdicao?.id ?? "novo-bloco"}
          bloco={blocoEmEdicao}
          processando={processandoBloco}
          erro={erroBloco}
          onClose={fecharModalBloco}
          onSalvar={salvarBloco}
        />
      )}

      {modalSalaAberto && blocoSalaSelecionado && (
        <SalaFormModal
          key={salaEmEdicao?.id ?? "nova-sala"}
          sala={salaEmEdicao}
          bloco={blocoSalaSelecionado}
          processando={processandoSala}
          erro={erroSala}
          onClose={fecharModalSala}
          onSalvar={salvarSala}
        />
      )}

      <ModalConfirmacao
        aberto={Boolean(blocoParaExcluir)}
        titulo="Excluir bloco"
        mensagem={
          blocoParaExcluir
            ? `Deseja realmente excluir o Bloco ${blocoParaExcluir.codigo}?`
            : ""
        }
        textoConfirmar="Excluir"
        textoCancelar="Cancelar"
        onConfirmar={excluirBloco}
        onCancelar={() => setBlocoParaExcluir(null)}
      />

      <ModalConfirmacao
        aberto={Boolean(salaParaExcluir)}
        titulo="Excluir sala"
        mensagem={
          salaParaExcluir
            ? `Deseja realmente excluir a sala ${salaParaExcluir.blocoCodigo}${salaParaExcluir.codigo}?`
            : ""
        }
        textoConfirmar="Excluir"
        textoCancelar="Cancelar"
        onConfirmar={excluirSala}
        onCancelar={() => setSalaParaExcluir(null)}
      />
    </div>
  );
}
