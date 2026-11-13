import { CalendarCheck } from "lucide-react";

import Modal from "../Modal";
import styles from "./style.module.css";

import { formatarData, formatarPeriodo } from "../../utils/dataHora";

export default function ReservaModal({
  sala,
  data,
  periodo,
  onClose,
  onConfirmar,
  processando,
  erro,
}) {
  if (!sala) return null;

  return (
    <Modal aberto={true} onClose={processando ? () => {} : onClose}>
      <div className={styles.conteudo}>
        <div className={styles.cabecalho}>
          <div className={styles.icone}>
            <CalendarCheck size={22} />
          </div>

          <div>
            <h2>Reservar sala</h2>

            <p>Confira os dados antes de confirmar.</p>
          </div>
        </div>

        <div className={styles.resumo}>
          <span className={styles.resumoTitulo}>Confirmar reserva</span>

          <div>
            <span>Sala:</span>
            <strong>{sala.codigo}</strong>
          </div>

          <div>
            <span>Bloco:</span>
            <strong>{sala.bloco}</strong>
          </div>

          <div>
            <span>Período:</span>
            <strong>{formatarPeriodo(periodo)}</strong>
          </div>

          <div>
            <span>Data:</span>
            <strong>{formatarData(data)}</strong>
          </div>
        </div>

        {erro && <div className={styles.erro}>{erro}</div>}

        <div className={styles.acoes}>
          <button
            type="button"
            className={styles.cancelar}
            disabled={processando}
            onClick={onClose}
          >
            Cancelar
          </button>

          <button
            type="button"
            className={styles.confirmar}
            disabled={processando}
            onClick={onConfirmar}
          >
            {processando ? "Reservando..." : "Confirmar reserva"}
          </button>
        </div>
      </div>
    </Modal>
  );
}
