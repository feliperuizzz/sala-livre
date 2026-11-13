import { X } from "lucide-react";

import Modal from "../Modal";
import styles from "./style.module.css";

import { formatarData, formatarPeriodo } from "../../utils/dataHora";

export default function SalaOcupadaModal({
  sala,
  data,
  periodo,
  onClose,
  reservaPropria,
  podeCancelar,
  onCancelar,
  processando,
  erro,
}) {
  if (!sala) return null;

  return (
    <Modal aberto={true} onClose={processando ? () => {} : onClose}>
      <div className={styles.conteudo}>
        <div className={styles.iconeOcupada}>
          <X size={27} strokeWidth={2.2} />
        </div>

        <h2 className={styles.titulo}>Sala ocupada</h2>

        <p className={styles.descricao}>
          {reservaPropria
            ? "Esta sala está reservada por você."
            : podeCancelar
              ? "Esta sala está reservada por outro professor. Como administrador, você pode cancelar esta reserva."
              : "Esta sala já está reservada neste período."}
        </p>

        <div className={styles.informacoes}>
          <div>
            <span>Sala:</span>
            <strong>{sala.codigo}</strong>
          </div>

          <div>
            <span>Data:</span>
            <strong>{formatarData(data)}</strong>
          </div>

          <div>
            <span>Período:</span>
            <strong>{formatarPeriodo(periodo)}</strong>
          </div>

          <div>
            <span>Professor:</span>
            <strong>{sala.professor || "-"}</strong>
          </div>
        </div>

        {erro && <div className={styles.erro}>{erro}</div>}

        <div className={styles.acoes}>
          <button
            type="button"
            className={styles.fechar}
            disabled={processando}
            onClick={onClose}
          >
            Fechar
          </button>

          {podeCancelar && (
            <button
              type="button"
              className={styles.cancelarReserva}
              disabled={processando}
              onClick={onCancelar}
            >
              {processando ? "Cancelando..." : "Cancelar reserva"}
            </button>
          )}
        </div>
      </div>
    </Modal>
  );
}
