import { useEffect, useMemo, useState } from "react";
import { useAuth } from "../../contexts/AuthContext";
import salaService from "../../services/salaService";
import reservaService from "../../services/reservaService";
import { obterDataAtual, obterPeriodoAtual } from "../../utils/dataHora";
import SalaLivreModal from "../../components/SalaLivreModal";
import SalaOcupadaModal from "../../components/SalaOcupadaModal";
import ReservaModal from "../../components/ReservaModal";
import OperacaoSucessoModal from "../../components/OperacaoSucessoModal";
import ModalConfirmacao from "../../components/ModalConfirmacao";
import Sidebar from "../../components/Sidebar";
import { useConfirmacao } from "../../hooks/useConfirmacao";

import CampoData from "../../components/CampoData";

import "./style.css";

export default function SalaLivre() {
  const { professor, autenticado, carregando: carregandoAuth } = useAuth();

  const [data, setData] = useState(obterDataAtual());

  const [periodo, setPeriodo] = useState(obterPeriodoAtual());

  const [salas, setSalas] = useState([]);

  const [carregandoSalas, setCarregandoSalas] = useState(true);

  const [erro, setErro] = useState("");

  const [salaSelecionada, setSalaSelecionada] = useState(null);

  const [processandoAcao, setProcessandoAcao] = useState(false);

  const [erroAcao, setErroAcao] = useState("");

  const [sucesso, setSucesso] = useState(null);

  const { confirmacao, abrirConfirmacao, fecharConfirmacao } = useConfirmacao();

  useEffect(() => {
    carregarSalas();
  }, [data, periodo]);

  async function carregarSalas() {
    setCarregandoSalas(true);
    setErro("");

    try {
      const resultado = await salaService.consultarDisponibilidade(
        data,
        periodo,
      );

      console.log("SALAS RECEBIDAS:", resultado);

      setSalas(resultado);
    } catch (error) {
      console.error(error);

      setErro("Não foi possível carregar as salas.");
    } finally {
      setCarregandoSalas(false);
    }
  }

  function selecionarSala(sala) {
    setErroAcao("");

    setSalaSelecionada(sala);
  }

  function fecharModal() {
    if (processandoAcao) {
      return;
    }

    setSalaSelecionada(null);

    setErroAcao("");
  }

  async function confirmarReserva() {
    if (!salaSelecionada) {
      return;
    }

    setProcessandoAcao(true);
    setErroAcao("");

    try {
      const salaReservada = salaSelecionada;

      await reservaService.reservar(salaReservada.salaId, data, periodo);

      await carregarSalas();

      setSalaSelecionada(null);

      setSucesso({
        tipo: "RESERVA",
        sala: salaReservada,
        data,
        periodo,
      });
    } catch (error) {
      console.error(error);

      const mensagem =
        error.response?.data?.erro || "Não foi possível realizar a reserva.";

      setErroAcao(mensagem);

      await carregarSalas();
    } finally {
      setProcessandoAcao(false);
    }
  }

  function solicitarCancelamento() {
    if (!salaSelecionada || !salaSelecionada.reservaId) {
      return;
    }

    abrirConfirmacao({
      titulo: "Cancelar reserva?",

      mensagem: `A reserva da sala ${salaSelecionada.codigo} será cancelada. Deseja continuar?`,

      textoConfirmar: "Cancelar reserva",

      textoCancelar: "Voltar",

      onConfirmar: async () => {
        fecharConfirmacao();

        await cancelarReserva();
      },
    });
  }

  async function cancelarReserva() {
    if (!salaSelecionada || !salaSelecionada.reservaId) {
      return;
    }

    setProcessandoAcao(true);
    setErroAcao("");

    try {
      const salaCancelada = salaSelecionada;

      await reservaService.cancelar(salaCancelada.reservaId);

      await carregarSalas();

      setSalaSelecionada(null);

      setSucesso({
        tipo: "CANCELAMENTO",
        sala: salaCancelada,
        data,
        periodo,
      });
    } catch (error) {
      console.error(error);

      setErroAcao(
        error.response?.data?.erro || "Não foi possível cancelar a reserva.",
      );
    } finally {
      setProcessandoAcao(false);
    }
  }

  const salasPorBloco = useMemo(() => {
    return salas.reduce((grupos, sala) => {
      const bloco = sala.bloco;

      if (!grupos[bloco]) {
        grupos[bloco] = [];
      }

      grupos[bloco].push(sala);

      return grupos;
    }, {});
  }, [salas]);

  const reservaPropria = Boolean(
    autenticado &&
    salaSelecionada &&
    !salaSelecionada.disponivel &&
    professor?.id === salaSelecionada.professorId,
  );

  const ehAdministrador = professor?.perfil === "ADMIN";

  const podeCancelarReserva = Boolean(
    autenticado &&
    salaSelecionada &&
    !salaSelecionada.disponivel &&
    (ehAdministrador || reservaPropria),
  );

  if (carregandoAuth) {
    return <div className="pagina-carregando">Carregando...</div>;
  }

  return (
    <div className="app-layout">
      <Sidebar paginaAtiva="salas" />

      <main className="app-content">
        <header className="cabecalho">
          <div>
            <h1>Salas Livres</h1>

            <p>Consulte a disponibilidade das salas por data e período.</p>
          </div>

          <div className="filtros">
            <CampoData
              value={data}
              minDate={obterDataAtual()}
              onChange={setData}
            />

            <div className="periodos">
              <button
                type="button"
                className={periodo === "MANHA" ? "periodo ativo" : "periodo"}
                onClick={() => setPeriodo("MANHA")}
              >
                Manhã
              </button>

              <button
                type="button"
                className={periodo === "TARDE" ? "periodo ativo" : "periodo"}
                onClick={() => setPeriodo("TARDE")}
              >
                Tarde
              </button>

              <button
                type="button"
                className={periodo === "NOITE" ? "periodo ativo" : "periodo"}
                onClick={() => setPeriodo("NOITE")}
              >
                Noite
              </button>
            </div>
          </div>
        </header>

        <section className="painel-salas">
          <div className="painel-topo">
            <div>
              <h2>Disponibilidade</h2>

              <span>{salas.length} salas cadastradas</span>
            </div>

            <div className="legenda">
              <span>
                <i className="legenda-cor livre" />
                Livre
              </span>

              <span>
                <i className="legenda-cor ocupada" />
                Ocupada
              </span>
            </div>
          </div>

          {carregandoSalas && (
            <div className="estado-mensagem">Carregando salas...</div>
          )}

          {!carregandoSalas && erro && (
            <div className="estado-mensagem erro">{erro}</div>
          )}

          {!carregandoSalas &&
            !erro &&
            Object.keys(salasPorBloco)
              .sort()
              .map((bloco) => (
                <section className="bloco" key={bloco}>
                  <div className="bloco-titulo">
                    <h3>Bloco {bloco}</h3>

                    <span>
                      {
                        salasPorBloco[bloco].filter((sala) => sala.disponivel)
                          .length
                      }{" "}
                      disponíveis
                    </span>
                  </div>

                  <div className="salas-grid">
                    {salasPorBloco[bloco].map((sala) => (
                      <button
                        type="button"
                        key={sala.salaId}
                        className={
                          sala.disponivel
                            ? "sala-card livre"
                            : "sala-card ocupada"
                        }
                        onClick={() => selecionarSala(sala)}
                      >
                        <strong>{sala.codigo}</strong>

                        <span>{sala.disponivel ? "Livre" : "Ocupada"}</span>
                      </button>
                    ))}
                  </div>
                </section>
              ))}
        </section>
      </main>

      {salaSelecionada && salaSelecionada.disponivel && !autenticado && (
        <SalaLivreModal
          sala={salaSelecionada}
          data={data}
          periodo={periodo}
          onClose={fecharModal}
        />
      )}

      {salaSelecionada && salaSelecionada.disponivel && autenticado && (
        <ReservaModal
          sala={salaSelecionada}
          data={data}
          periodo={periodo}
          onClose={fecharModal}
          onConfirmar={confirmarReserva}
          processando={processandoAcao}
          erro={erroAcao}
        />
      )}

      {salaSelecionada && !salaSelecionada.disponivel && (
        <SalaOcupadaModal
          sala={salaSelecionada}
          data={data}
          periodo={periodo}
          onClose={fecharModal}
          reservaPropria={reservaPropria}
          podeCancelar={podeCancelarReserva}
          onCancelar={solicitarCancelamento}
          processando={processandoAcao}
          erro={erroAcao}
        />
      )}

      {sucesso && (
        <OperacaoSucessoModal
          aberto={true}
          tipo={sucesso.tipo}
          sala={sucesso.sala}
          data={sucesso.data}
          periodo={sucesso.periodo}
          onClose={() => setSucesso(null)}
        />
      )}

      <ModalConfirmacao
        aberto={confirmacao.aberto}
        titulo={confirmacao.titulo}
        mensagem={confirmacao.mensagem}
        textoConfirmar={confirmacao.textoConfirmar}
        textoCancelar={confirmacao.textoCancelar}
        onCancelar={fecharConfirmacao}
        onConfirmar={confirmacao.onConfirmar}
      />
    </div>
  );
}
