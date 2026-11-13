import { Check } from "lucide-react";

import Modal from "../Modal";
import styles from "./style.module.css";

import { formatarData, formatarPeriodo } from "../../utils/dataHora";

export default function SalaLivreModal({ sala, data, periodo, onClose }) {
  if (!sala) return null;

  return (
    <Modal aberto={true} onClose={onClose}>
      <div className={styles.conteudo}>
        <div className={styles.iconeSucesso}>
          <Check size={27} strokeWidth={2.2} />
        </div>

        <h2 className={styles.titulo}>Sala livre</h2>

        <p className={styles.descricao}>
          Sala <strong>{sala.codigo}</strong> disponível no período da{" "}
          <strong>{formatarPeriodo(periodo)}</strong>.
        </p>

        <div className={styles.informacoes}>
          <div>
            <span>Sala:</span>
            <strong>{sala.codigo}</strong>
          </div>

          <div>
            <span>Bloco:</span>
            <strong>{sala.bloco}</strong>
          </div>

          <div>
            <span>Data:</span>
            <strong>{formatarData(data)}</strong>
          </div>

          <div>
            <span>Período:</span>
            <strong>{formatarPeriodo(periodo)}</strong>
          </div>
        </div>

        <button type="button" className={styles.botaoFechar} onClick={onClose}>
          Fechar
        </button>
      </div>
    </Modal>
  );
}
