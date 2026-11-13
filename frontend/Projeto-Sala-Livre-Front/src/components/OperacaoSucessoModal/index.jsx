import { Check } from "lucide-react";

import Modal from "../Modal";
import styles from "./style.module.css";

import { formatarData, formatarPeriodo } from "../../utils/dataHora";

export default function OperacaoSucessoModal({
  aberto,
  tipo,
  sala,
  data,
  periodo,
  onClose,
}) {
  if (!aberto || !sala) {
    return null;
  }

  const reservaRealizada = tipo === "RESERVA";

  return (
    <Modal aberto={aberto} onClose={onClose}>
      <div className={styles.conteudo}>
        <div className={styles.icone}>
          <Check size={28} strokeWidth={2.3} />
        </div>

        <h2>{reservaRealizada ? "Reserva realizada" : "Reserva cancelada"}</h2>

        <p className={styles.descricao}>
          {reservaRealizada ? (
            <>
              A sala <strong>{sala.codigo}</strong> foi reservada com sucesso.
            </>
          ) : (
            <>
              A reserva da sala <strong>{sala.codigo}</strong> foi cancelada com
              sucesso.
            </>
          )}
        </p>

        <div className={styles.resumo}>
          <strong>{sala.codigo}</strong>

          <span>
            {formatarData(data)}
            {" • "}
            {formatarPeriodo(periodo)}
          </span>
        </div>

        <button type="button" className={styles.botao} onClick={onClose}>
          Fechar
        </button>
      </div>
    </Modal>
  );
}
